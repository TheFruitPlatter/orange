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
package com.langwuyue.orange.redis.executor.multiplelocks.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;

/**
 * Multiple locks expiration retrieval context class, used for handling expiration time information for multiple distributed locks.
 * This class extends {@link OrangeMultipleLocksContext} and is specifically designed for retrieving lock expiration times.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeMultipleLocksContext
 */
public class OrangeMultipleLocksGetExpirationsContext extends OrangeRedisMultipleValueContext {
	
	private List<Object> cachedKeys;

	/**
	 * Constructs a new multiple locks expiration retrieval context instance.
	 *
	 * @param operationOwner    The class that owns the operation
	 * @param operationMethod   The method representing the operation
	 * @param args              The arguments array passed to the method
	 * @param redisKey          The Redis key for the operation
	 * @param valueType         The Redis value type enum
	 */
	public OrangeMultipleLocksGetExpirationsContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType);
	}
	
	/**
	 * Converts multiple values to a list format and caches the keys.
	 * 
	 * @return A list containing all values, or an empty list if no values exist
	 */
	public List toList() {
		final List newArray = new ArrayList<>();
		if(getMultipleValue() == null) {
			return newArray;
		}
		forEach((t,o)-> newArray.add(t));
		this.cachedKeys = newArray;
		return newArray;
	}

	/**
	 * Retrieves the cached list of keys.
	 * 
	 * <p>Returns the list of keys that was previously cached by the {@link #toList()} method.
	 * This method provides quick access to the keys without needing to reconstruct the list.
	 *
	 * @return The cached list of keys, may be null if {@link #toList()} hasn't been called yet
	 */
	public List<Object> getCachedKeys() {
		return cachedKeys;
	}
}