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
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
class OrangeRedisTransactionAutoConfigurer {
	
	
	private OrangeRedisTransactionClientFactoryBean factoryBean;
	
	OrangeRedisTransactionAutoConfigurer(OrangeRedisTransactionClientFactoryBean factoryBean){
		this.factoryBean = factoryBean;
	}
	
	@Bean
	OrangeRedisTransactionManager newTransactionManager() {
		return factoryBean.getTransactionManger();
	}
	
	@Bean
	@Order(Ordered.LOWEST_PRECEDENCE - 1024)
	OrangeDBTransactionalAspect newOrangeDBTransactionalAspect(OrangeRedisTransactionManager transactionManager,OrangeRedisLogger logger) {
		return new OrangeDBTransactionalAspect(transactionManager,logger);
	}
	
}
