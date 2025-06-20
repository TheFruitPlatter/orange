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
package com.langwuyue.orange.redis.executor.hash;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.hash.GetHashKeys;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for retrieving a random key from a Redis hash.
 * This executor supports the {@link GetHashKeys} and {@link Random} annotations
 * to randomly select a single key from a Redis hash structure.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeRandomKeyExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	/**
	 * Redis hash operations instance used for executing hash-related commands.
	 */
	private OrangeRedisHashOperations operations;

	/**
	 * Constructs a new OrangeRandomKeyExecutor with the specified operations and ID generator.
	 *
	 * @param operations the Redis hash operations to use for executing commands
	 * @param idGenerator the generator used for creating executor IDs
	 */
	public OrangeRandomKeyExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * This executor supports {@link GetHashKeys} and {@link Random} annotations
	 * in addition to the annotations supported by the parent class.
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetHashKeys.class,Random.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * This executor uses {@link OrangeHashContext} to handle Redis hash operations.
	 *
	 * @return the class of {@link OrangeHashContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashContext.class;
	}
	
	/**
	 * Returns the field annotated with {@link HashKey} from the given type.
	 * This method is used to identify the field that will store the retrieved random key.
	 *
	 * @param type the type to search for the value field
	 * @return the field annotated with {@link HashKey}, or null if not found
	 */
	@Override
	protected Field getValueField(Type type) {
		Class<?> returnType = getRawType(type);
		Field[] fields = returnType.getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(HashKey.class)) {
				return field;
			}
		}
		return null;
	}

	/**
	 * Executes the random key retrieval operation on the Redis hash.
	 * This method retrieves a single random key from the hash specified in the context.
	 *
	 * @param context the Redis operation context containing the key information
	 * @param valueField the field to store the retrieved key, if annotated with {@link HashKey}
	 * @param returnArgumentType the expected return type
	 * @return a Collection containing the randomly selected key
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeHashContext ctx = (OrangeHashContext) context;
		return this.operations.randomKeys(ctx.getRedisKey().getValue(), 1, ctx.getKeyType(), valueField == null ? returnArgumentType : valueField.getGenericType());
	}
}