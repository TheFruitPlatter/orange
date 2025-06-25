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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionManager;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * Auto-configuration class for Redis transaction support in Orange framework.
 * This configurer sets up the necessary beans for handling Redis transactions,
 * including transaction manager and transactional aspect.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
class OrangeRedisTransactionAutoConfigurer {
	
	
	private OrangeRedisTransactionClientFactoryBean factoryBean;
	
	/**
	 * Constructs a new OrangeRedisTransactionAutoConfigurer with the specified factory bean.
	 *
	 * @param factoryBean the factory bean for creating Redis transaction clients
	 */
	OrangeRedisTransactionAutoConfigurer(OrangeRedisTransactionClientFactoryBean factoryBean){
		this.factoryBean = factoryBean;
	}
	
	/**
	 * Creates and configures a new OrangeRedisTransactionManager bean.
	 * This bean is responsible for managing Redis transactions within the application.
	 *
	 * @return a new instance of OrangeRedisTransactionManager from the factory bean
	 */
	@Bean
	OrangeRedisTransactionManager newTransactionManager() {
		return factoryBean.getTransactionManger();
	}
	
	/**
	 * Creates and configures a new OrangeDBTransactionalAspect bean.
	 * This aspect handles the transactional behavior for methods annotated with
	 * transactional annotations. It is ordered with a high precedence to ensure
	 * it runs before other aspects that might depend on transaction context.
	 *
	 * @param transactionManager the Redis transaction manager to use
	 * @param logger the logger for transaction-related logging
	 * @return a new instance of OrangeDBTransactionalAspect
	 */
	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE - 1024)
	OrangeDBTransactionalAspect newOrangeDBTransactionalAspect(OrangeRedisTransactionManager transactionManager,OrangeRedisLogger logger) {
		return new OrangeDBTransactionalAspect(transactionManager,logger);
	}
	
}