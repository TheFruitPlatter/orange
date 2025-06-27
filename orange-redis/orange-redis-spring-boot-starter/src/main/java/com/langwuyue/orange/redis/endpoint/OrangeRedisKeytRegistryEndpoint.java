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

import java.util.Map;
import java.util.Set;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import com.langwuyue.orange.redis.registry.OrangeRedisKeyMetaData;
import com.langwuyue.orange.redis.registry.OrangeRedisKeyRegistry;

/**
 * Spring Boot Actuator endpoint that exposes the Redis key registry information.
 * 
 * <p>This endpoint provides a read-only view of all registered Redis keys and their
 * associated metadata. It can be accessed via the "/actuator/of-redis-key-registry"
 * path when Spring Boot Actuator is enabled and the endpoint is exposed.
 * 
 * <p>The registry contains information about Redis keys used in the application,
 * including their metadata such as:
 * <ul>
 *   <li>Key patterns and their usage</li>
 *   <li>Associated operations and configurations</li>
 *   <li>Metadata about key lifecycle and management</li>
 * </ul>
 * 
 * <p>This endpoint is particularly useful for:
 * <ul>
 *   <li>Monitoring Redis key usage patterns</li>
 *   <li>Debugging key-related issues</li>
 *   <li>Understanding the Redis key space organization</li>
 *   <li>Auditing Redis key configurations</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Endpoint(id = "of-redis-key-registry")
public class OrangeRedisKeytRegistryEndpoint {
	
	/**
	 * Retrieves the complete Redis key registry information.
	 * 
	 * <p>This method is invoked when a GET request is made to the endpoint.
	 * It returns a map containing all registered Redis keys and their associated
	 * metadata. The map is organized with key patterns as keys and sets of
	 * metadata objects as values.
	 * 
	 * <p>The returned data structure provides a comprehensive view of:
	 * <ul>
	 *   <li>All registered Redis key patterns</li>
	 *   <li>Metadata associated with each key pattern</li>
	 *   <li>Configuration and usage information for each key</li>
	 * </ul>
	 *
	 * @return A map where keys are Redis key patterns (String) and values are sets
	 *         of metadata ({@link OrangeRedisKeyMetaData}) associated with those patterns
	 */
	@ReadOperation
	public Map<String,Set<OrangeRedisKeyMetaData>> getRegistry(){
		return OrangeRedisKeyRegistry.getRegistry();
	}
}