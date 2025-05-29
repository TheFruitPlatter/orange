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
 * Circuit breaker interface for Redis operations that provides failure handling and
 * service availability management. This interface defines methods to handle Redis
 * operation failures and service unavailability scenarios.
 *
 * <p>The circuit breaker pattern prevents cascading failures by:
 * <ul>
 *   <li>Detecting Redis operation failures</li>
 *   <li>Handling service unavailability gracefully</li>
 *   <li>Providing clear error context for debugging</li>
 *   <li>Supporting custom error handling strategies</li>
 * </ul>
 *
 * <p>Implementation Example:
 * <pre>{@code
 * public class CustomCircuitBreaker implements OrangeRedisCircuitBreaker {
 *     private final Logger log = LoggerFactory.getLogger(getClass());
 *     private final MetricsCollector metrics;
 *
 *     @Override
 *     public void onException(String key, Class<?> operationOwner,
 *             Method operation, Object[] args, Exception e) {
 *         log.error("Redis operation failed for key: {}", key, e);
 *         metrics.recordFailure(key, operation.getName());
 *         // Custom error handling logic
 *     }
 *
 *     {@code @Override}
 *     public void outOfService(String key, Class<?> operationOwner,
 *             Method operation, Object[] args) {
 *         log.error("Redis service unavailable for key: {}", key);
 *         metrics.recordOutage(key);
 *         // Service unavailability handling logic
 *     }
 * }
 * }</pre>
 *
 * <p>Spring Configuration Example:
 * <pre>{@code
 * @Configuration
 * public class RedisConfig {
 *     @Bean
 *     public OrangeRedisCircuitBreaker redisCircuitBreaker() {
 *         return new CustomCircuitBreaker(metricsCollector);
 *     }
 * }
 * }</pre>
 *
 * <p>Implementation Recommendations:
 * <ul>
 *   <li>Error Handling:
 *     <ul>
 *       <li>Log errors with sufficient context</li>
 *       <li>Consider implementing retry mechanisms</li>
 *       <li>Track failure patterns</li>
 *       <li>Implement fallback strategies</li>
 *     </ul>
 *   </li>
 *   <li>Monitoring:
 *     <ul>
 *       <li>Collect error metrics</li>
 *       <li>Track service availability</li>
 *       <li>Monitor operation latencies</li>
 *       <li>Set up alerting thresholds</li>
 *     </ul>
 *   </li>
 *   <li>Recovery:
 *     <ul>
 *       <li>Implement backoff strategies</li>
 *       <li>Consider circuit state transitions</li>
 *       <li>Handle recovery scenarios</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisCircuitBreaker {
	
    /**
     * Handles exceptions that occur during Redis operations. This method is called
     * when a Redis operation fails due to an exception.
     *
     * <p>Implementation considerations:
     * <ul>
     *   <li>Log the error with appropriate context</li>
     *   <li>Track failure metrics</li>
     *   <li>Implement retry logic if appropriate</li>
     *   <li>Consider circuit breaker state transitions</li>
     * </ul>
     *
     * @param key The Redis key involved in the failed operation
     * @param operationOwner The class that owns the Redis operation method
     * @param operation The method representing the Redis operation that failed
     * @param args The arguments that were passed to the operation
     * @param e The exception that caused the operation to fail
     */
    void onException(String key, Class<?> operationOwner, Method operation, Object[] args, Exception e);
	
    /**
     * Handles situations where the Redis service is unavailable. This method is called
     * when Redis operations cannot be performed due to service unavailability.
     *
     * <p>Implementation considerations:
     * <ul>
     *   <li>Log the service outage</li>
     *   <li>Track availability metrics</li>
     *   <li>Implement fallback mechanisms</li>
     *   <li>Consider service recovery strategies</li>
     * </ul>
     *
     * @param key The Redis key involved in the attempted operation
     * @param operationOwner The class that owns the Redis operation method
     * @param operation The method representing the Redis operation that couldn't be performed
     * @param args The arguments that were to be used in the operation
     */
    void outOfService(String key, Class<?> operationOwner, Method operation, Object[] args);
}