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
 * Commit Redis transaction after database transaction completes.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Aspect
public class OrangeDBTransactionalAspect {
	
	private OrangeRedisTransactionManager orangeTransactionManager;
	
	private OrangeRedisLogger logger;
	
	public OrangeDBTransactionalAspect(OrangeRedisTransactionManager orangeTransactionManager,OrangeRedisLogger logger) {
		this.orangeTransactionManager = orangeTransactionManager;
		this.logger = logger;
	}
    
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
