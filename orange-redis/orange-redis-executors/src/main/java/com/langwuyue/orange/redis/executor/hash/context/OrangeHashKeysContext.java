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
package com.langwuyue.orange.redis.executor.hash.context;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisIterableContext;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgMultipleHandler;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Redis hash multi-key operation context, used for handling multi-key operations with @Multiple annotation.
 * 
 * <p>Extends {@link OrangeHashContext}, implements {@link OrangeRedisIterableContext} interface,
 * provides batch processing capability for multiple hash keys.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeHashKeysContext extends OrangeHashContext implements OrangeRedisIterableContext {
	
	/**
	 * Object storing multiple hash keys, can be a collection or an array.
	 * 
	 * <p>This field is bound to method parameters through {@link Multiple} annotation and processed by {@link OrangeOperationArgMultipleHandler},
	 * supports the following types:
	 * <ul>
	 *   <li>Any collection class implementing the {@link Collection} interface</li>
	 *   <li>Any type of array</li>
	 * </ul>
	 */
	@OrangeRedisOperationArg(binding = Multiple.class, valueHandler = OrangeOperationArgMultipleHandler.class)
	private Object hashKeys;
	
	/**
	 * Configuration for whether to continue execution when an operation fails.
	 * 
	 * <p>This field is bound to method parameters through {@link ContinueOnFailure} annotation and processed by {@link OrangeMethodAnnotationHandler},
	 * when set to true, it will continue processing subsequent keys even if an operation on a certain key fails.
	 */
	@OrangeRedisOperationArg(binding = ContinueOnFailure.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private ContinueOnFailure continueOnFailure;
	
	/**
	 * Cached list of hash keys
	 */
	private List cachedKeys;
	
	/**
	 * Constructs a Redis hash context for multi-key operations.
	 *
	 * @param operationOwner  the class that owns the Redis operation
	 * @param operationMethod the method representing the Redis operation
	 * @param args            the arguments passed to the operation method
	 * @param redisKey        the Redis key
	 * @param valueType       the type of Redis value
	 * @param keyType         the type of Redis key
	 */
	public OrangeHashKeysContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args, 
		Key redisKey,
		RedisValueTypeEnum valueType,
		RedisValueTypeEnum keyType
	) {
		super(operationOwner, operationMethod, args, redisKey, valueType, keyType);
	}
	
	/**
	 * Gets a list of all hash keys.
	 *
	 * @return List containing all hash keys
	 */
	public List getHashKeys(){
		return getHashKeys(null);
	}
	
	/**
	 * Gets a list of all hash keys and performs the specified operation on each key-value pair.
	 *
	 * @param consumer the operation to perform on each key-value pair, can be null
	 * @return List containing all hash keys
	 * @throws OrangeRedisException if hashKeys is not a collection or array type
	 */
	private List getHashKeys(BiConsumer consumer){
		if(hashKeys instanceof Collection) {
			List keys = new ArrayList<>();	
			Collection values = (Collection)hashKeys;
			int i = 0;
			for(Object value : values) {
				Object key = getKey(value);
				if(key == null) {
					continue;
				}
				keys.add(i,key);
				if(consumer != null) {
					consumer.accept(key, value);
				}
				i++;
			}
			this.cachedKeys = keys;
			return keys;
		}else if(hashKeys instanceof Array) {
			int len = Array.getLength(hashKeys);
			List keys = new ArrayList<>();
			for(int i = 0; i < len; i++) {
				Object value = Array.get(hashKeys, i);
				Object key = getKey(value);
				if(key == null) {
					continue;
				}
				keys.add(i,key);
				if(consumer != null) {
					consumer.accept(key, value);
				}
			}
			this.cachedKeys = keys;
			return keys;
		}
		throw new OrangeRedisException(String.format("The argument annotated with @%s must be a collection or an array", Multiple.class));
	}
	
	/**
	 * Extracts the hash key from a member object.
	 * 
	 * <p>If the member object has a field annotated with @HashKey, returns the value of that field.
	 * Otherwise, returns the member object itself.
	 *
	 * @param member the object to extract the key from
	 * @return the extracted key value, or null if the key field value is null
	 */
	private Object getKey(Object member) {
		Field keyField = null;
		Field[] fields = member.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(HashKey.class)) {
				keyField = field;
				break;
			}
		}
		if(keyField == null) {
			return member;
		}
		Object key = OrangeReflectionUtils.getFieldValue(keyField, member);
		if(key == null) {
			return null;
		}
		return key;
	}

	/**
	 * Gets the cached list of hash keys.
	 * 
	 * <p>Returns the list of hash keys that was previously generated and cached
	 * by a call to {@link #getHashKeys()} or {@link #getHashKeys(BiConsumer)}.
	 *
	 * @return the cached list of hash keys, may be null if keys haven't been processed yet
	 */
	public List getCachedKeys() {
		return cachedKeys;
	}

	/**
	 * Performs the given action for each key-value pair in the hash.
	 * 
	 * <p>Implementation of {@link OrangeRedisIterableContext#forEach(BiConsumer)}.
	 * Processes all hash keys and their corresponding values, applying the given
	 * action to each pair.
	 *
	 * @param t the action to be performed for each key-value pair
	 */
	@Override
	public void forEach(BiConsumer t) {
		getHashKeys(t);
	}

	/**
	 * Converts all hash keys to an array.
	 * 
	 * <p>Implementation of {@link OrangeRedisIterableContext#toArray()}.
	 * Returns an array containing all the hash keys in this context.
	 *
	 * @return an array containing all the hash keys
	 */
	@Override
	public Object[] toArray() {
		return getHashKeys().toArray();
	}
	
	/**
	 * Determines if the operation should continue when a failure occurs.
	 * 
	 * <p>Implementation of {@link OrangeRedisIterableContext#continueOnFailure()}.
	 * Specifies whether to continue processing remaining items when an error occurs
	 * during iteration.
	 *
	 * @return true if the operation should continue on failure, false otherwise
	 */
	@Override
	public boolean continueOnFailure() {
		return continueOnFailure.value();
	}

}