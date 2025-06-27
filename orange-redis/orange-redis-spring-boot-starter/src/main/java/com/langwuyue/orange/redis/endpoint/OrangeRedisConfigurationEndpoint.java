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

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

/**
 * Spring Boot Actuator endpoint that exposes Orange Redis configuration information.
 * 
 * <p>This endpoint provides a read-only view of the Redis configuration in JSON format,
 * allowing for monitoring and diagnostics of the Redis connection settings and other
 * configuration parameters. It can be accessed via the "/actuator/of-redis-conf" path
 * when Spring Boot Actuator is enabled and the endpoint is exposed.
 * 
 * <p>The endpoint is particularly useful for:
 * <ul>
 *   <li>Verifying the active Redis configuration in a running application</li>
 *   <li>Debugging configuration issues in different environments</li>
 *   <li>Monitoring Redis connection settings</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Endpoint(id = "of-redis-conf")
public class OrangeRedisConfigurationEndpoint {
	
	/**
	 * The Redis configuration information serialized as a JSON string.
	 * This string contains all the relevant Redis configuration parameters
	 * that are exposed through this endpoint.
	 */
	private String configurationJsonString;
	
	/**
	 * Constructs a new OrangeRedisConfigurationEndpoint with the specified configuration.
	 * 
	 * @param configurationJsonString The Redis configuration serialized as a JSON string.
	 *                               This string will be returned as-is when the endpoint is accessed.
	 */
	public OrangeRedisConfigurationEndpoint(String configurationJsonString) {
		this.configurationJsonString = configurationJsonString;
	}
	
	/**
	 * Retrieves the Redis configuration information.
	 * 
	 * <p>This method is invoked when a GET request is made to the endpoint.
	 * It returns the Redis configuration as a JSON string without any processing
	 * or modification.
	 * 
	 * @return The Redis configuration as a JSON string
	 */
	@ReadOperation
	public String getRegistry(){
		return configurationJsonString;
	}
}