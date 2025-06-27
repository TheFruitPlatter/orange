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

import java.util.List;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import com.langwuyue.orange.redis.registry.OrangeSlowOperationRegistry;
import com.langwuyue.orange.redis.registry.OrangeSlowOperationRegistry.SlowInfo;

/**
 * Spring Boot Actuator endpoint that exposes information about slow Redis operations.
 * 
 * <p>This endpoint provides a read-only view of Redis operations that have been
 * identified as "slow" based on their execution time. It can be accessed via the
 * "/actuator/of-redis-slow-ops" path when Spring Boot Actuator is enabled and
 * the endpoint is exposed.
 * 
 * <p>Slow operations are Redis commands that take longer than expected to complete,
 * which might indicate performance issues or potential bottlenecks in the system.
 * This endpoint helps in:
 * <ul>
 *   <li>Identifying performance bottlenecks in Redis operations</li>
 *   <li>Monitoring operation execution times</li>
 *   <li>Debugging slow-running commands</li>
 *   <li>Performance tuning and optimization</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeSlowOperationRegistry
 * @see SlowInfo
 */
@Endpoint(id = "of-redis-slow-ops")
public class OrangeRedisSlowOperationEndpoint {
	
	/**
	 * Retrieves information about slow Redis operations.
	 * 
	 * <p>This method is invoked when a GET request is made to the endpoint.
	 * It returns a list of slow operation information objects that contain
	 * details about Redis operations that have been flagged as slow based
	 * on their execution time.
	 * 
	 * <p>The returned list includes details such as:
	 * <ul>
	 *   <li>Operation execution time</li>
	 *   <li>Command details</li>
	 *   <li>Timestamp of occurrence</li>
	 *   <li>Additional context information</li>
	 * </ul>
	 *
	 * @return A list of {@link SlowInfo} objects containing details about
	 *         slow Redis operations
	 * @see OrangeSlowOperationRegistry#getRegistry()
	 */
	@ReadOperation
	public List<SlowInfo> getDeadTransaction(){
		return OrangeSlowOperationRegistry.getRegistry();
	}
}