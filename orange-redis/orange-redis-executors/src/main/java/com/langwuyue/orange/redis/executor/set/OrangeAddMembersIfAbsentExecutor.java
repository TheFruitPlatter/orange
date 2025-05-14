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
package com.langwuyue.orange.redis.executor.set;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.OrangeRedisIfAbsentException;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.set.context.OrangeAddMembersIfAbsentContext;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.set.OrangRemoveMembersFailedEvent;
import com.langwuyue.orange.redis.listener.set.OrangeAddMembersIfAbsentEvent;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMembersIfAbsentExecutor extends OrangeAddMemberIfAbsentExecutor {

	private Collection<OrangeRedisMultipleSetIfAbsentListener> listeners;
	
	public OrangeAddMembersIfAbsentExecutor(
		OrangeRedisScriptOperations scriptOperations,
		OrangeRedisSetOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisMultipleSetIfAbsentListener> listeners,
		OrangeRedisLogger logger
	) {
		super(scriptOperations,operations,idGenerator,null,logger);
		this.listeners = listeners;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeAddMembersIfAbsentContext ctx = (OrangeAddMembersIfAbsentContext)context;
		Set<Object> successEntries = new LinkedHashSet<>();
		Set<Object> successMembers = new LinkedHashSet<>();
		Map<Object,Exception> failedEntries = new LinkedHashMap<>();
		Set<Object> unknownEnties = new LinkedHashSet<>();
		boolean continueOnFailure = ctx.continueOnFailure();
		ctx.forEach((t,o) -> {
			if(!continueOnFailure && !failedEntries.isEmpty()) {
				unknownEnties.add(o);
				return;
			}
			try {
				Boolean success = executeIfAbsent(ctx,t);
				if(success != null && success.booleanValue()) {
					successEntries.add(o);
					successMembers.add(t);
				}else{
					throw new OrangeRedisIfAbsentException("False returned");
				}
			}catch (Exception e) {
				// The operation may have been interrupted by a client timeout or network error, but it was actually completed successfully.
				if(!(e instanceof OrangeRedisIfAbsentException)) {
					unknownEnties.add(o);
				}
				failedEntries.put(o, e);
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
						ctx.getArgs(),
						successEntries,
						failedEntries,
						unknownEnties
					)
				)
			);
		}finally {
			removeMembers(ctx,successEntries,successMembers,failedEntries,unknownEnties);
		}
		return null;
	}
	
	private void removeMembers(
		OrangeAddMembersIfAbsentContext ctx,
		Set<Object> successEntries,
		Set<Object> successMembers,
		Map<Object,Exception> failedEntries,
		Set<Object> unknownEnties
	) {
		if(!ctx.isDeleteInTheEnd() || successMembers.isEmpty()) {
			return;
		}
		Long removed = null;
		try {
			removed = getOperations().remove(
				ctx.getRedisKey().getValue(), 
				ctx.getValueType(), 
				successMembers.toArray()
			);	
			if(removed == null || removed < successMembers.size()) {
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
						successMembers.size(),
						e
					)
				)
			);
		}
	}
	
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,Multiple.class,ContinueOnFailure.class,IfAbsent.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAddMembersIfAbsentContext.class;
	}

}
