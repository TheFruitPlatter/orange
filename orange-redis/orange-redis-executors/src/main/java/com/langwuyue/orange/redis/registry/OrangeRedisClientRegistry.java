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
package com.langwuyue.orange.redis.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.langwuyue.orange.redis.client.OrangeRedisClientFactoryMetaData;

/**
 * Registry for managing Redis client factory metadata.
 * 
 * This class provides a centralized registry for Redis client implementations and their
 * associated factory metadata. It allows the framework to track different Redis client
 * implementations and retrieve their factory configuration when needed.
 * 
 * The registry uses a thread-safe concurrent map to store client class to factory metadata
 * mappings, ensuring safe access in multi-threaded environments.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisClientRegistry {
	
	/**
	 * Thread-safe registry mapping Redis client classes to their factory metadata.
	 */
	private static final Map<Class,OrangeRedisClientFactoryMetaData> ORANGE_REDIS_CLIENT_REGISTRY = new ConcurrentHashMap<>();
	
	/**
	 * Private constructor to prevent instantiation of this utility class.
	 */
	private OrangeRedisClientRegistry() {}
	
	/**
	 * Registers a Redis client class with its factory metadata.
	 * 
	 * @param clientClass The Redis client class to register
	 * @param metaData The factory metadata associated with the client class
	 */
	public static void register(Class clientClass, OrangeRedisClientFactoryMetaData metaData) {
		ORANGE_REDIS_CLIENT_REGISTRY.put(clientClass, metaData);
	}
	
	/**
	 * Retrieves the factory metadata for a specific Redis client class.
	 * 
	 * @param clientClass The Redis client class to look up
	 * @return The factory metadata associated with the client class, or null if not found
	 */
	public static OrangeRedisClientFactoryMetaData getClientFactoryMetaData(Class clientClass) {
		return ORANGE_REDIS_CLIENT_REGISTRY.get(clientClass);
	}
}