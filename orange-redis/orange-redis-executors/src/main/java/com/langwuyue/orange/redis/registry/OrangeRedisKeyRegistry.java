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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.langwuyue.orange.redis.OrangeRedisException;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisKeyRegistry {
	
	public static final String VARIABLE_MARK_CHAR = "?";
	
	private static final Pattern PATTERN = Pattern.compile("\\w+(\\w+|:)*\\"+OrangeRedisKeyRegistry.VARIABLE_MARK_CHAR+"+");
	
	private static final Map<String,Set<OrangeRedisKeyMetaData>> ORANGE_REDIS_OPERATIONS_REGISTRY = new HashMap<>();
	
	private static final Map<String,Set<String>> ORANGE_REDIS_SIMILAR_KEYS_REGISTRY = new HashMap<>();
	
	public static Map<String,Set<OrangeRedisKeyMetaData>> getRegistry() {
		return ORANGE_REDIS_OPERATIONS_REGISTRY;
	}
	
	public static void register(
		String key, 
		String variableKey, 
		Class<?> operationsClass
	) {
		Set<OrangeRedisKeyMetaData> keyMetaDatas = ORANGE_REDIS_OPERATIONS_REGISTRY.get(key);
		if(keyMetaDatas == null) {
			keyMetaDatas = new LinkedHashSet<>();
			ORANGE_REDIS_OPERATIONS_REGISTRY.put(key, keyMetaDatas);
		}
		OrangeRedisKeyMetaData metaData = new OrangeRedisKeyMetaData(key,variableKey,operationsClass);
		keyMetaDatas.add(metaData);
		String variableKeyPrefix = metaData.getVariableKeyPrefix();
		if(variableKeyPrefix == null) {
			return;
		}
		Set<String> similarKeys = ORANGE_REDIS_SIMILAR_KEYS_REGISTRY.get(variableKeyPrefix);
		if(similarKeys == null) {
			similarKeys = new LinkedHashSet<>();
			ORANGE_REDIS_SIMILAR_KEYS_REGISTRY.put(variableKeyPrefix, similarKeys);
		}
		similarKeys.add(key);
	}
	
	public static void checkKey(int level) {
		ORANGE_REDIS_OPERATIONS_REGISTRY.forEach((k,v) -> {
			if(v.size() > 1) {
				throw new OrangeRedisException(String.format(
					"Too many operation classes found for the key %s %n operations class: %s", 
					k, 
					v.stream().map(OrangeRedisKeyMetaData::getOperationClass).collect(Collectors.toList()))
				);
			}
			if(level <= OrangeKeyNamingFormatCheckLevel.ONE_KEY_ONE_OPERATIONS) {
				return;
			}
			OrangeRedisKeyMetaData metaData = v.iterator().next();
			if(metaData.getVariableKey() == null) {
				return;
			}
			if(!PATTERN.matcher(metaData.getVariableKey()).matches()) {
				throw new OrangeRedisException(String.format("The variables of the key %s must be placed at the end.", metaData.getKey()));
			}
			
			if(level <= OrangeKeyNamingFormatCheckLevel.VARIABLE_MUST_BE_LAST || metaData.getVariableKeyPrefix() == null) {
				return;
			}
			if(ORANGE_REDIS_OPERATIONS_REGISTRY.containsKey(metaData.getVariableKeyPrefix())) {
				throw new OrangeRedisException(String.format(
						"The key %s has similar keys, which is dangerous. For example, two different operations could have the same key when values for all arguments are the same.%n similar keys: %s", 
						metaData.getKey(), 
						metaData.getVariableKeyPrefix()
				));
			}
			
		});
		
		if(level <= OrangeKeyNamingFormatCheckLevel.VARIABLE_MUST_BE_LAST ) {
			return;
		}
		
		ORANGE_REDIS_SIMILAR_KEYS_REGISTRY.forEach((k,v) -> {
			if(v.size() > 1) {
				throw new OrangeRedisException(
					String.format(
						"Found similar keys, which is dangerous. For example, two different operations could have the same key when values for all arguments are the same.%n similar keys: %s", 
						String.join(", ", v.toArray(new String[v.size()]))
					)
				);
			}
			
			if(level <= OrangeKeyNamingFormatCheckLevel.SIMILAR_AFTER_IGNORE_VAR) {
				return;
			}
			
			ORANGE_REDIS_SIMILAR_KEYS_REGISTRY.keySet().forEach(t -> {
				if(t.equals(k)) {
					return;
				}
				if(t.startsWith(k) || k.startsWith(t)) {
					Set<String> similarKeys = ORANGE_REDIS_SIMILAR_KEYS_REGISTRY.get(k);
					similarKeys.addAll(ORANGE_REDIS_SIMILAR_KEYS_REGISTRY.get(t));
					throw new OrangeRedisException(
						String.format(
							"Found similar keys, which is dangerous. For example, two different operations could have the same key when values for all arguments are the same.%n similar keys: %s", 
							String.join(", ", similarKeys.toArray(new String[similarKeys.size()]))
						)
					);
				}
			});
		});
		
	}
}
