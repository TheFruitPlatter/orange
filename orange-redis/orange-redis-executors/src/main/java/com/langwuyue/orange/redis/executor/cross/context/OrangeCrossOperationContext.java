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
package com.langwuyue.orange.redis.executor.cross.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.context.OrangeRedisContext;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeCrossOperationContext extends OrangeRedisContext {

	private List<String> keys;
	
	private String storeTo;
	
	public OrangeCrossOperationContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		List<String> keys,
		String storeTo,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, null,valueType);
		this.keys = keys;
		this.storeTo = storeTo;
	}
	
	public static OrangeRedisContext newInstance(
		Class<? extends OrangeRedisContext> contextClass,
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		List<String> keys,
		String storeTo,
		RedisValueTypeEnum valueType
	) throws Exception{
		Constructor<? extends OrangeRedisContext> constructor = contextClass.getConstructor(
			Class.class,
			Method.class,
			Object[].class,
			List.class,
			String.class,
			RedisValueTypeEnum.class
		);
		return constructor.newInstance(operationOwner,operationMethod,args,keys,storeTo,valueType);
	}

	public List<String> getKeys() {
		return keys;
	}
	
	public String getReferenceKey() {
		return keys.get(0);
	}
	
	public Set<String> getComparisonKeys() {
		Set<String> comparisonKeys = new LinkedHashSet<>();
		for (int i = 1; i < keys.size(); i++) {
			comparisonKeys.add(keys.get(i));
		}
		return comparisonKeys;
	}

	public String getStoreTo() {
		return storeTo;
	}
}
