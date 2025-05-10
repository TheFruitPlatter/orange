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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisClientRegistry {
	
	private static final Map<Class,OrangeRedisClientFactoryMetaData> ORANGE_REDIS_CLIENT_REGISTRY = new ConcurrentHashMap<>();
	
	private OrangeRedisClientRegistry() {}
	
	public static void register(Class clientClass, OrangeRedisClientFactoryMetaData metaData) {
		ORANGE_REDIS_CLIENT_REGISTRY.put(clientClass, metaData);
	}
	
	public static OrangeRedisClientFactoryMetaData getClientFactoryMetaData(Class clientClass) {
		return ORANGE_REDIS_CLIENT_REGISTRY.get(clientClass);
	}
}
