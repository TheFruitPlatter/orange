/*
 * Copyright (c) 2025 Liang.Zhong. All rights reserved.
 *
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.langwuyue.orange.redis.executor.hash;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.langwuyue.orange.redis.OrangeRedisIfAbsentException;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeAddMembersIfAbsentContext;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.hash.OrangRemoveMembersFailedEvent;
import com.langwuyue.orange.redis.listener.hash.OrangeAddMembersIfAbsentEvent;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * An executor that adds multiple members to a Redis hash only if they don't already exist.
 * 
 * <p>This executor handles batch operations for conditionally adding multiple field-value pairs
 * to a Redis hash. It only adds entries that don't already exist in the hash, implementing
 * the "if absent" pattern for multiple entries at once.
 *
 * <p>The executor provides the following features:
 * <ul>
 *   <li>Batch processing of multiple hash entries</li>
 *   <li>Conditional addition based on field absence</li>
 *   <li>Optional continuation on failure for partial success</li>
 *   <li>Tracking of successful, failed, and unknown operations</li>
 *   <li>Event notification through registered listeners</li>
 *   <li>Optional cleanup of added entries in case of partial failures</li>
 * </ul>
 *
 * <p>This executor supports the following annotations:
 * <ul>
 *   <li>{@link AddMembers} - Indicates a batch add operation</li>
 *   <li>{@link Multiple} - Marks the operation as handling multiple entries</li>
 *   <li>{@link IfAbsent} - Specifies the conditional nature of the additions</li>
 *   <li>{@link ContinueOnFailure} - Controls behavior when some additions fail</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeAddMembersIfAbsentContext
 * @see OrangeRedisMultipleSetIfAbsentListener
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeAddMembersIfAbsentExecutor extends OrangeRedisAbstractExecutor {
	
	/** The Redis hash operations implementation used by this executor */
	private OrangeRedisHashOperations operations;
	
	/** Collection of listeners to be notified of batch operation results */
	private Collection<OrangeRedisMultipleSetIfAbsentListener> listeners;

	/**
	 * Constructs a new OrangeAddMembersIfAbsentExecutor.
	 * 
	 * <p>This constructor initializes the executor with the necessary dependencies
	 * for performing batch hash operations and handling operation results.
	 *
	 * @param operations the Redis hash operations implementation to use
	 * @param idGenerator the generator for creating unique executor IDs
	 * @param listeners collection of listeners to be notified of operation results
	 */
	public OrangeAddMembersIfAbsentExecutor(
		OrangeRedisHashOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisMultipleSetIfAbsentListener> listeners
	) {
		super(idGenerator);
		this.operations = operations;
		this.listeners = listeners;
	}

	/**
	 * Executes the batch add-if-absent operation for multiple hash entries.
	 * 
	 * <p>This method processes a batch of entries, attempting to add each entry to the Redis hash
	 * only if it doesn't already exist. The operation maintains detailed tracking of the results,
	 * including successful, failed, and unknown (potentially interrupted) operations.
	 *
	 * <p>The execution process:
	 * <ol>
	 *   <li>Initializes result tracking collections for different operation states</li>
	 *   <li>Processes each entry individually:
	 *     <ul>
	 *       <li>Attempts to add the entry if absent using putIfAbsent</li>
	 *       <li>Records success in successEntries and successHashKeys</li>
	 *       <li>Records failures in failedEntries</li>
	 *       <li>Records uncertain operations in unknownEntries</li>
	 *     </ul>
	 *   </li>
	 *   <li>If continueOnFailure is false, stops processing on first failure</li>
	 *   <li>Notifies all registered listeners with the operation results</li>
	 *   <li>Handles cleanup if required (removeMembers)</li>
	 * </ol>
	 *
	 * <p>The method distinguishes between different types of failures:
	 * <ul>
	 *   <li>Known failures (OrangeRedisIfAbsentException) - when Redis confirms the operation failed</li>
	 *   <li>Unknown state (other exceptions) - when the operation might have succeeded despite errors</li>
	 * </ul>
	 *
	 * @param context the operation context containing the entries to process
	 * @return null, as the operation results are communicated through listeners
	 * @throws Exception if an error occurs and continueOnFailure is false
	 * @see OrangeAddMembersIfAbsentContext
	 * @see OrangeRedisIfAbsentException
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeAddMembersIfAbsentContext ctx = (OrangeAddMembersIfAbsentContext) context;
		Set<Object> successEntries = new LinkedHashSet<>();
		Map<Object,Exception> failedEntries = new LinkedHashMap<>();
		Set<Object> unknownEnties = new LinkedHashSet<>();
		Set<Object> successHashKeys = new LinkedHashSet<>();
		boolean continueOnFailure = ctx.continueOnFailure();
		ctx.forEach((t,o) -> {
			Map map = (Map) t; 
			Entry entry = (Entry) map.entrySet().iterator().next();
			if(!continueOnFailure && !failedEntries.isEmpty()) {
				unknownEnties.add(o);
				return;
			}
			
			try {
				Boolean success = this.operations.putIfAbsent(
						ctx.getRedisKey().getValue(), 
						entry.getKey(), 
						entry.getValue(), 
						ctx.getKeyType(), 
						ctx.getValueType()
				);
				if(success != null && success.booleanValue()) {
					successEntries.add(o);
					successHashKeys.add(entry.getKey());
				}else{
					throw new OrangeRedisIfAbsentException("False returned");
				}
			}catch (Exception e) {
				// The operation may have been interrupted by a client timeout or network error, but it was actually completed successfully.
				if(!(e instanceof OrangeRedisIfAbsentException)) {
					unknownEnties.add(o);
				}
				failedEntries.put(o,e);
			}
		});
		
		if(successEntries.isEmpty() && unknownEnties.isEmpty() && failedEntries.isEmpty()) {
			return null;
		}
		try {
			this.listeners.forEach(t -> 
				t.onCompleted(
					context.getRedisKey().getOriginalKey(),
					new OrangeAddMembersIfAbsentEvent(
						context.getArgs(),
						successEntries,
						failedEntries,
						unknownEnties
					)
				)
			);
		}finally {
			removeMembers(ctx,successEntries,failedEntries,unknownEnties,successHashKeys);
		}
		
		return null;
	}
	
	/**
	 * Removes successfully added hash members when cleanup is required.
	 * 
	 * <p>This method handles the cleanup process for successfully added hash entries
	 * when the deleteInTheEnd flag is set in the context. It attempts to remove all
	 * successfully added hash keys and verifies that the expected number of entries
	 * were removed.
	 *
	 * <p>If the removal operation fails or removes fewer entries than expected, it
	 * notifies listeners with a removal failure event containing detailed information
	 * about the state of the operation.
	 *
	 * <p>This cleanup mechanism is important for maintaining data consistency when:
	 * <ul>
	 *   <li>The batch operation is meant to be atomic (all or nothing)</li>
	 *   <li>Some entries failed to be added and the operation should be rolled back</li>
	 *   <li>The context explicitly requests cleanup with deleteInTheEnd=true</li>
	 * </ul>
	 *
	 * @param ctx the operation context
	 * @param successEntries set of entries that were successfully added
	 * @param failedEntries map of entries that failed to be added and their exceptions
	 * @param unknownEnties set of entries with unknown status
	 * @param successHashKeys set of hash keys that were successfully added
	 */
	private void removeMembers(
		OrangeAddMembersIfAbsentContext ctx,
		Set<Object> successEntries,
		Map<Object,Exception> failedEntries,
		Set<Object> unknownEnties,
		Set<Object> successHashKeys
	) {
		if(!ctx.isDeleteInTheEnd() || successHashKeys.isEmpty()) {
			return;
		}
		Long removed = null;
		try {
			removed = operations.removeMembers(ctx.getRedisKey().getValue(), ctx.getKeyType(), successHashKeys.toArray());
			if(removed == null || removed.longValue() < successEntries.size()) {
				throw new OrangeRedisIfAbsentException("The number of members removed was below expectations.");
			}
		}catch (Exception e) {
			// Warning, maybe a network error causes the removal to fail.
			// Also, maybe Redis Client timeout, but the operation success.
			final Long removeCount = removed;
			this.listeners.forEach(t -> 
				t.onCompleted(
					ctx.getRedisKey().getOriginalKey(),
					new OrangRemoveMembersFailedEvent(
						ctx.getArgs(),
						successEntries,
						failedEntries,
						unknownEnties,
						removeCount,
						successHashKeys.size(),
						e
					)
				)
			);
		}
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link AddMembers} - Indicates a batch add operation for hash entries</li>
	 *   <li>{@link Multiple} - Marks the operation as handling multiple entries</li>
	 *   <li>{@link IfAbsent} - Specifies the conditional nature of the additions</li>
	 *   <li>{@link ContinueOnFailure} - Controls behavior when some additions fail</li>
	 * </ul>
	 *
	 * <p>These annotations work together to define the behavior of the batch operation,
	 * including its conditional nature and error handling strategy.
	 *
	 * @return a list of annotation classes that this executor can process
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,Multiple.class,IfAbsent.class,ContinueOnFailure.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This method specifies that this executor works with {@link OrangeAddMembersIfAbsentContext}
	 * objects, which contain the necessary information for batch hash operations including:
	 * <ul>
	 *   <li>The Redis key to operate on</li>
	 *   <li>The entries to be added</li>
	 *   <li>Configuration flags like continueOnFailure and deleteInTheEnd</li>
	 *   <li>Type information for proper serialization/deserialization</li>
	 * </ul>
	 *
	 * @return the class object representing the context type for this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAddMembersIfAbsentContext.class;
	}

	/**
	 * Gets the Redis hash operations implementation used by this executor.
	 * 
	 * <p>This method provides access to the underlying Redis hash operations
	 * implementation, primarily for testing and internal use.
	 *
	 * @return the Redis hash operations implementation
	 */
	OrangeRedisHashOperations getOperations() {
		return operations;
	}
	
}