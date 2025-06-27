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
package com.langwuyue.orange.redis.endpoint;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import com.langwuyue.orange.redis.configuration.OrangeRedisTransactionClientFactoryBean;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * Spring Boot Actuator endpoint that exposes information about dead Redis transactions.
 * 
 * <p>This endpoint provides a read-only view of transactions that have been marked as "dead"
 * (failed, timed out, or otherwise unable to complete). It can be accessed via the 
 * "/actuator/of-redis-dead-tx" path when Spring Boot Actuator is enabled and the endpoint
 * is exposed.
 * 
 * <p>Dead transactions are transactions that have encountered issues during execution
 * and could not be completed successfully. This endpoint helps in monitoring and
 * diagnosing transaction-related issues in Redis operations.
 * 
 * <p>The endpoint returns a set of transaction objects that represent the dead transactions
 * currently tracked by the system. If an error occurs while retrieving the dead transactions,
 * an empty set is returned and the error is logged.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Endpoint(id = "of-redis-dead-tx")
public class OrangeRedisDeadTransactionEndpoint {
	
	/**
	 * The factory bean that creates and manages Redis transaction clients.
	 * This bean provides access to information about dead transactions.
	 */
	private OrangeRedisTransactionClientFactoryBean factoryBean;
	
	/**
	 * Logger for recording operational events and errors related to this endpoint.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new OrangeRedisDeadTransactionEndpoint with the specified components.
	 * 
	 * @param factoryBean The factory bean that provides access to dead transaction information.
	 * @param logger The logger used for recording operational events and errors.
	 */
	public OrangeRedisDeadTransactionEndpoint(OrangeRedisTransactionClientFactoryBean factoryBean, OrangeRedisLogger logger) {
		this.factoryBean = factoryBean;
		this.logger = logger;
	}
	
	/**
	 * Retrieves the set of dead Redis transactions.
	 * 
	 * <p>This method is invoked when a GET request is made to the endpoint.
	 * It returns a set of transaction objects representing transactions that
	 * have been marked as dead (failed, timed out, or otherwise unable to complete).
	 * 
	 * <p>If an error occurs while retrieving the dead transactions, this method
	 * logs the error and returns an empty set to prevent the endpoint from failing.
	 * 
	 * @return A set of objects representing dead transactions, or an empty set if
	 *         no dead transactions exist or an error occurred
	 */
	@ReadOperation
	public Set<Object> getDeadTransaction(){
		try {
			return factoryBean.getDeadTransaction();
		} catch (Exception e) {
			this.logger.error(String.format("Endpoint %s execute error", "ofRedisDeadTx") ,e);
			return new HashSet<>();
		}
	}
}