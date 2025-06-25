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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Redis metrics auto-configuration class that registers Redis-related metrics with Micrometer.
 *
 * <p>This configuration is automatically enabled when the property {@code orange.redis.metrics.enabled}
 * is set to {@code true}. By default, metrics collection is disabled.</p>
 *
 * <p>Currently registers the following metrics:
 * <ul>
 *   <li>{@code redis.active.request} - Gauge tracking the number of active Redis requests</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see SmartInitializingSingleton
 * @see MeterRegistry
 * @see Gauge
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "orange.redis.metrics.enabled", havingValue = "true", matchIfMissing = false)
public class OrangeRedisMetricsAutoConfigurer implements SmartInitializingSingleton {
	
	/**
	 * The Redis transaction client factory bean that provides access to Redis metrics data.
	 * <p>Injected through constructor and cannot be null.</p>
	 */
	private final OrangeRedisTransactionClientFactoryBean factoryBean;
	
	/**
	 * Provider for MeterRegistry to support optional metrics collection.
	 * <p>Injected through constructor and lazily provides the MeterRegistry instance.</p>
	 * <p>May be null if metrics collection is not available in the application context.</p>
	 */
	private final ObjectProvider<MeterRegistry> registryProvider;
	
	/**
	 * Constructs a new metrics auto-configurer for Redis.
	 *
	 * @param factoryBean the Redis transaction client factory
	 * @param registryProvider the provider for MeterRegistry (metrics registry)
	 */
	public OrangeRedisMetricsAutoConfigurer(
		OrangeRedisTransactionClientFactoryBean factoryBean,
		ObjectProvider<MeterRegistry> registryProvider
	) {
		this.factoryBean = factoryBean;
		this.registryProvider = registryProvider;
	}
	
	/**
	 * Registers Redis metrics after all singletons have been initialized.
	 * 
	 * <p>This method registers the following metrics when a MeterRegistry is available:
	 * <ul>
	 *   <li>{@code redis.active.request} - Gauge tracking active Redis requests</li>
	 *   <li>{@code redis.connection.count} - Gauge tracking active Redis connections</li>
	 * </ul>
	 * 
	 * @throws IllegalStateException if metrics registration fails
	 */
	@Override
	public void afterSingletonsInstantiated() {
        MeterRegistry registry = registryProvider.getIfAvailable();
        if (registry != null) {
            Gauge.builder("redis.active.request", factoryBean, OrangeRedisTransactionClientFactoryBean::getActiveRequestCount)
                .description("Number of active requests")
                .register(registry);
        }
	}
}