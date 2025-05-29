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
package com.langwuyue.orange.redis;

import java.lang.reflect.Method;

/**
 * Default implementation of the {@link OrangeRedisCircuitBreaker} interface that provides
 * basic circuit breaker functionality for Redis operations. This implementation throws
 * runtime exceptions with detailed error information when Redis operations fail or when
 * the service is unavailable.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Detailed error reporting with operation context</li>
 *   <li>Immediate failure notification</li>
 *   <li>Simple exception propagation</li>
 *   <li>Integration with Redis operation tracking</li>
 * </ul>
 *
 * <p>Usage Example:
 * <pre>{@code
 * @Configuration
 * public class RedisConfig {
 *     @Bean
 *     public OrangeRedisCircuitBreaker circuitBreaker() {
 *         return new OrangeRedisDefaultCircuitBreaker();
 *     }
 * }
 * }</pre>
 *
 * <p>Custom Implementation Example:
 * <pre>{@code
 * public class CustomCircuitBreaker extends OrangeRedisDefaultCircuitBreaker {
 *     @Override
 *     public void onException(String key, Class<?> operationOwner,
 *             Method operation, Object[] args, Exception e) {
 *         // Custom error handling logic
 *         log.error("Redis operation failed", e);
 *         metrics.incrementErrorCount();
 *         super.onException(key, operationOwner, operation, args, e);
 *     }
 * }
 * }</pre>
 *
 * <p>Integration with Monitoring:
 * <pre>{@code
 * public class MonitoredCircuitBreaker extends OrangeRedisDefaultCircuitBreaker {
 *     private final MetricsRegistry metrics;
 *     
 *     @Override
 *     public void outOfService(String key, Class<?> operationOwner,
 *             Method operation, Object[] args) {
 *         metrics.incrementOutOfServiceCount();
 *         super.outOfService(key, operationOwner, operation, args);
 *     }
 * }
 * }</pre>
 *
 * @author Liang.Zhong
 * @see OrangeRedisCircuitBreaker
 * @since 1.0.0
 */
public class OrangeRedisDefaultCircuitBreaker implements OrangeRedisCircuitBreaker {

	@Override
	public void onException(String key, Class<?> operationOwner, Method operation, Object[] args, Exception e) {
		throw new RuntimeException(String.format("key:%s;%n operationOwner: %s;%n operation: %s;%n", key,operationOwner,operation),e);
	}

	@Override
	public void outOfService(String key, Class<?> operationOwner, Method operation, Object[] args) {
		throw new RuntimeException("Redis out of service");
	}
}