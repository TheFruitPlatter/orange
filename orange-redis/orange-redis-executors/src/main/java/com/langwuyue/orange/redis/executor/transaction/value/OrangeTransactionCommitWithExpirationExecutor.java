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
package com.langwuyue.orange.redis.executor.transaction.value;

import java.lang.annotation.Annotation;
import java.util.List;

import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.transaction.Commit;
import com.langwuyue.orange.redis.annotation.transaction.Version;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Transaction commit executor that adds key expiration functionality.
 * 
 * <p>This executor extends the standard {@link OrangeTransactionCommitExecutor} by adding
 * the ability to set an expiration time on the Redis key after a successful commit.
 * This is particularly useful for:
 * <ul>
 *   <li>Implementing time-to-live (TTL) for cached transaction data</li>
 *   <li>Automatic cleanup of transaction resources after a specified period</li>
 *   <li>Supporting temporary or session-based transaction data</li>
 *   <li>Implementing data retention policies within Redis</li>
 * </ul>
 * 
 * <p>The executor works by first performing the standard commit operation using the parent
 * class implementation, and then setting the expiration time on the key if the commit
 * was successful. This ensures that expiration is only applied to successfully committed
 * transactions.
 * 
 * <p>This executor supports the {@link SetExpiration} annotation in addition to the
 * standard transaction annotations, allowing for declarative expiration time configuration
 * in application code.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeTransactionCommitExecutor
 * @see SetExpiration
 */
public class OrangeTransactionCommitWithExpirationExecutor extends OrangeTransactionCommitExecutor {
	
	/**
	 * Redis hash operations.
	 */
	private OrangeRedisHashOperations operations;
	
	/**
	 * Constructs a new transaction commit executor with expiration support.
	 * 
	 *
	 * @param scriptOperations Redis script operations for executing the atomic commit script
	 * @param operations Redis hash operations for setting key expiration
	 * @param idGenerator Generator for creating unique executor identifiers
	 * @param transactionManager Manager for coordinating transaction lifecycle events
	 * @param logger Logger for diagnostic and debugging information
	 */
	public OrangeTransactionCommitWithExpirationExecutor(
		OrangeRedisScriptOperations scriptOperations,
		OrangeRedisHashOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		OrangeRedisDefaultTransactionManager transactionManager,
		OrangeRedisLogger logger
	) {
		super(scriptOperations,idGenerator,logger);
		this.operations = operations;
		this.setTransactionManager(transactionManager);
	}

	/**
	 * Executes the transaction commit operation with expiration time setting.
	 * 
	 * @param context The Redis operation context containing transaction data and configuration
	 * @return Boolean indicating whether the commit and expiration setting were successful
	 * @throws Exception if any error occurs during commit or expiration setting
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Boolean success = (Boolean) super.execute(context);
		if(success != null && success.booleanValue()) {
			Key key = context.getRedisKey();
			this.operations.expire(key.getValue(), key.getExpirationTime(), key.getExpirationTimeUnit());
		}
		return success;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This method defines the complete set of annotations that this executor supports,
	 * which includes:
	 * <ul>
	 *   <li>{@link Commit} - For standard transaction commit operations</li>
	 *   <li>{@link Version} - For versioned transaction operations</li>
	 *   <li>{@link SetExpiration} - For configuring key expiration</li>
	 * </ul>
	 * 
	 * <p>Unlike the parent class implementation that adds to an existing list,
	 * this implementation directly creates a new list with all supported annotations.
	 * This approach ensures that the executor properly handles all relevant annotations
	 * without depending on the parent class's implementation details.
	 * 
	 * <p>The framework uses this method to determine which executor should handle
	 * a particular annotated method call based on the annotations present.
	 *
	 * @return A list of annotation classes supported by this executor
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Commit.class,Version.class,SetExpiration.class);
	}
}