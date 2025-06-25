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
package com.langwuyue.orange.redis.configuration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionManager;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * An aspect that synchronizes Redis transactions with database transactions.
 * 
 * This aspect monitors Spring's {@code @Transactional} annotated methods and ensures that
 * Redis transactions are properly committed or rolled back in sync with the database
 * transaction's outcome. It works by:
 * 
 * <ul>
 *   <li>Intercepting methods annotated with {@code @Transactional}</li>
 *   <li>Registering a {@link TransactionSynchronization} that will be called after the
 *       database transaction completes</li>
 *   <li>Committing the Redis transaction if the database transaction commits successfully</li>
 *   <li>Rolling back the Redis transaction if the database transaction rolls back</li>
 * </ul>
 * 
 * This ensures data consistency between the database and Redis cache by maintaining
 * transactional integrity across both systems.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see org.springframework.transaction.annotation.Transactional
 * @see OrangeRedisTransactionManager
 */
@Aspect
public class OrangeDBTransactionalAspect {
	
	/**
	 * The transaction manager responsible for handling Redis transactions.
	 */
	private OrangeRedisTransactionManager orangeTransactionManager;
	
	/**
	 * Logger for Redis-related operations and errors.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new OrangeDBTransactionalAspect.
	 * 
	 * @param orangeTransactionManager the Redis transaction manager to handle Redis transactions
	 * @param logger the logger for Redis operations and error reporting
	 */
	public OrangeDBTransactionalAspect(OrangeRedisTransactionManager orangeTransactionManager, OrangeRedisLogger logger) {
		this.orangeTransactionManager = orangeTransactionManager;
		this.logger = logger;
	}
    
    /**
     * Intercepts methods annotated with {@code @Transactional} and synchronizes Redis
     * transactions with the database transaction.
     * 
     * This method:
     * <ul>
     *   <li>Allows the original method to proceed</li>
     *   <li>If transaction synchronization is active, registers a synchronization callback</li>
     *   <li>The callback commits or rolls back the Redis transaction based on the database
     *       transaction's outcome</li>
     * </ul>
     *
     * @param joinPoint the join point representing the intercepted method
     * @return the result of the intercepted method's execution
     * @throws Throwable if the intercepted method throws an exception or if there's an error
     *         in transaction synchronization
     */
    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object monitorTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if(!TransactionSynchronizationManager.isSynchronizationActive()) {
        	return result;
        }
        final OrangeRedisTransactionManager tm = this.orangeTransactionManager;
        final OrangeRedisLogger redisLogger = this.logger;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
            	try {
	                if (status == STATUS_COMMITTED) {
	                	tm.commit();
	                } else if (status == STATUS_ROLLED_BACK) {
	                	tm.rollback();
	                }
            	}catch (Exception e) {
            		redisLogger.error("Failed to commit Redis transaction",e);
				}
            }
        });
        return result;
    }
}