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
package com.langwuyue.orange.redis.context;

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
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.builder.OrangeMethodAnnotationHandler;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgMultipleHandler;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context class for handling Redis operations that involve multiple values.
 * 
 * <p>This class extends {@link OrangeRedisContext} and implements {@link OrangeRedisIterableContext}
 * to provide support for batch operations and operations involving collections of values.
 * It can handle both array and {@link Collection} types, and supports custom objects
 * with {@link RedisValue} annotated fields.
 * 
 * <p>The class supports two main annotations:
 * <ul>
 *   <li>{@link Multiple} - marks a parameter as containing multiple values</li>
 *   <li>{@link ContinueOnFailure} - controls error handling behavior during iteration</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisContext
 * @see OrangeRedisIterableContext
 * @see Multiple
 * @see ContinueOnFailure
 * @see RedisValue
 */
public class OrangeRedisMultipleValueContext extends OrangeRedisContext implements OrangeRedisIterableContext {
	
	/**
	 * The collection or array of multiple values for Redis operations.
	 * 
	 * <p>This field is automatically populated with the value of the method parameter
	 * annotated with {@link Multiple}. It can be:
	 * <ul>
	 *   <li>A Java Collection (List, Set, etc.)</li>
	 *   <li>An array of any type</li>
	 * </ul>
	 * 
	 * <p>The field is marked with {@link OrangeRedisOperationArg} to indicate that
	 * it should be bound to parameters annotated with {@link Multiple}, and uses
	 * {@link OrangeOperationArgMultipleHandler} for value extraction.
	 */
	@OrangeRedisOperationArg(binding = Multiple.class, valueHandler = OrangeOperationArgMultipleHandler.class)
	private Object multipleValue;
	
	/**
	 * Configuration for error handling during batch operations.
	 * 
	 * <p>This field is automatically populated from the {@link ContinueOnFailure} annotation
	 * on the operation method. It determines whether the operation should continue
	 * processing remaining elements when an error occurs with one element.
	 * 
	 * <p>The field is marked with {@link OrangeRedisOperationArg} to indicate that
	 * it should be bound to the method annotation {@link ContinueOnFailure}, and uses
	 * {@link OrangeMethodAnnotationHandler} for annotation extraction.
	 */
	@OrangeRedisOperationArg(binding = ContinueOnFailure.class, valueHandler = OrangeMethodAnnotationHandler.class)
	private ContinueOnFailure continueOnFailure;

	/**
	 * Constructs a new Redis multiple value context with the specified parameters.
	 * 
	 * <p>This constructor initializes the context with the necessary information
	 * for handling multiple value Redis operations.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments to be passed to the operation method
	 * @param redisKey the Redis key information
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeRedisMultipleValueContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Returns the raw multiple value object.
	 * 
	 * <p>This method returns the original collection or array that was passed
	 * to the Redis operation method and annotated with {@link Multiple}.
	 *
	 * @return the multiple value object (collection or array)
	 */
	public Object getMultipleValue() {
		return multipleValue;
	}
	
	/**
	 * Performs the given action for each element in the multiple values.
	 * 
	 * <p>This method iterates through all elements in the multiple value collection
	 * or array, extracts the actual Redis value from each element (if needed),
	 * and applies the consumer function to each value.
	 * 
	 * <p>For each element, the consumer receives:
	 * <ul>
	 *   <li>First parameter: The extracted Redis value (possibly from a {@link RedisValue} field)</li>
	 *   <li>Second parameter: The original object from the collection/array</li>
	 * </ul>
	 * 
	 * <p>If the multiple value is null, this method returns without performing any actions.
	 *
	 * @param consumer the action to be performed for each element
	 * @throws OrangeRedisException if the multiple value is neither a collection nor an array
	 */
	@Override
	public void forEach(BiConsumer consumer) {
		if(multipleValue == null) {
			return;
		}
		if(multipleValue instanceof Collection) {
			((Collection)multipleValue).forEach((t) -> {
				Object value = getValue(t);
				if(value == null) {
					return;
				}
				consumer.accept(value,t);
			});
			return;
		}
		if(multipleValue.getClass().isArray()) {
			int len = Array.getLength(multipleValue);
			for(int i = 0; i < len; i++) {
				Object t = Array.get(multipleValue, i);
				Object value = getValue(t);
				if(value == null) {
					continue;
				}
				consumer.accept(value,t);
			}
			return;
		}
		
		throw new OrangeRedisException(String.format("The arugment annotated with @%s must be an array or a collection", Multiple.class));
	}

	/**
	 * Returns an array containing all extracted values from the multiple value object.
	 * 
	 * <p>This method creates a new array containing all the extracted Redis values
	 * from the multiple value collection or array. The extraction process follows
	 * the same rules as in {@link #forEach(BiConsumer)}.
	 * 
	 * <p>If the multiple value is null, an empty array is returned.
	 *
	 * @return an array containing all extracted Redis values
	 */
	public Object[] toArray() {
		if(multipleValue == null) {
			return new Object[] {};
		}
		final List newArray = new ArrayList<>();
		forEach((t,o)-> newArray.add(t));
		return newArray.toArray();
	}
	
	/**
	 * Extracts the Redis value from an object.
	 * 
	 * <p>This method determines how to extract the actual Redis value from an object:
	 * <ul>
	 *   <li>If the value is null, returns null</li>
	 *   <li>If the value is a Java standard type (from java.* packages), returns the value as is</li>
	 *   <li>If the value is a custom object with a field annotated with {@link RedisValue},
	 *       extracts and returns that field's value</li>
	 *   <li>Otherwise, returns the original value</li>
	 * </ul>
	 *
	 * @param value the object from which to extract the Redis value
	 * @return the extracted Redis value, or the original value if no extraction is needed
	 */
	private Object getValue(Object value) {
		if(value == null) {
			return null;
		}
		Class valueClass = value.getClass();
		if(valueClass.getPackage().getName().startsWith("java")) {
			return value;
		}
		Field[] fields = valueClass.getDeclaredFields();
		if(fields == null || fields.length == 0) {
			return value;
		}
		
		Field valueField = null;
		for(Field field : fields) {
			if(field.isAnnotationPresent(RedisValue.class)) {
				valueField = field;
				break;
			}
		}
		
		if(valueField == null) {
			return value;
		}
		
		return OrangeReflectionUtils.getFieldValue(valueField, value);
	}

	/**
	 * Indicates whether operations should continue when an error occurs.
	 * 
	 * <p>This method returns the value specified by the {@link ContinueOnFailure}
	 * annotation on the Redis operation method. If the annotation is present and
	 * set to true, batch operations will continue processing remaining elements
	 * even if some operations fail.
	 *
	 * @return true if operations should continue after errors, false if they should stop
	 */
	@Override
	public boolean continueOnFailure() {
		return continueOnFailure.value();
	}
}