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
package com.langwuyue.orange.redis.executor.zset;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.GetScores;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;
/**
 * Executor for retrieving scores of multiple members in a Redis Sorted Set.
 * 
 * <p>This executor provides functionality to get scores for multiple members in a Redis
 * Sorted Set with support for various return types and automatic type conversion.
 *
 * <p>Key features:
 * <ul>
 *   <li>Batch retrieval of scores for multiple members</li>
 *   <li>Flexible return type support:
 *     <ul>
 *       <li>List of numeric types (Double, Long, Integer, Float, BigDecimal)</li>
 *       <li>Arrays of numeric types</li>
 *       <li>Map with members as keys and scores as values</li>
 *       <li>List/Array of custom objects with @RedisValue and @Score annotations</li>
 *     </ul>
 *   </li>
 *   <li>Automatic type conversion for numeric types</li>
 *   <li>Support for both simple types and complex objects as members</li>
 *   <li>Null handling for non-existent members</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public class OrangeGetScoresExecutor extends OrangeRedisAbstractExecutor {

	/** The Redis Sorted Set operations implementation */
	private OrangeRedisZSetOperations operations;

	/**
	 * Constructs a new OrangeGetScoresExecutor.
	 *
	 * @param operations the Redis Sorted Set operations implementation
	 * @param idGenerator the executor ID generator for tracking and monitoring
	 */
	public OrangeGetScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Executes the batch score retrieval operation for multiple members in a Redis Sorted Set.
	 *
	 * <p>This method handles the retrieval of scores for multiple members and performs
	 * type conversion based on the method's return type. The process includes:
	 * <ol>
	 *   <li>Extracting member values from the context</li>
	 *   <li>Retrieving scores from Redis</li>
	 *   <li>Converting results to the appropriate return type:
	 *     <ul>
	 *       <li>ArrayList - converts to List of specified numeric type</li>
	 *       <li>Array - converts to array of specified numeric type</li>
	 *       <li>Map - creates map with members as keys and scores as values</li>
	 *       <li>Custom objects - creates objects with @RedisValue and @Score fields</li>
	 *     </ul>
	 *   </li>
	 * </ol>
	 *
	 * @param context the execution context containing key and member information
	 * @return the converted result based on the method's return type, or null if no results
	 * @throws Exception if an error occurs during execution or type conversion
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisMultipleValueContext ctx = (OrangeRedisMultipleValueContext)context;
		final List<Object> values = new ArrayList<>();
		final List<Object> originValues = new ArrayList<>();
		ctx.forEach((t,o) -> {
			values.add(t);
			originValues.add(o);
		});
		List<Double> results = this.operations.score(ctx.getRedisKey().getValue(), ctx.getValueType(), values.toArray());
		if(results == null || results.isEmpty()) {
			return null;
		}
		Class returnClass = context.getOperationMethod().getReturnType();
		if(returnClass.isAssignableFrom(ArrayList.class)) {
			Type returnArgumentType = OrangeReflectionUtils.getCollectionOrArrayArgumentType(context.getOperationMethod().getGenericReturnType());
			Class<?> argumentClass = OrangeReflectionUtils.getRawType(returnArgumentType);
			return toList(argumentClass,results, values);
		}
		else if(returnClass.isArray()) {
			Type returnArgumentType = OrangeReflectionUtils.getCollectionOrArrayArgumentType(context.getOperationMethod().getGenericReturnType());
			Class<?> argumentClass = OrangeReflectionUtils.getRawType(returnArgumentType);
			return toArray(argumentClass,results, values);
		}
		else if(Map.class.isAssignableFrom(returnClass)){
			return toMap(ctx,originValues,returnClass,results);
		}
		return results;
	}

	/**
	 * Returns the list of supported annotations for this executor.
	 *
	 * <p>This executor supports:
	 * <ul>
	 *   <li>{@link GetScores} - for marking methods that retrieve multiple member scores</li>
	 *   <li>{@link Multiple} - for marking parameters that represent multiple members</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetScores.class,Multiple.class);
	}
	
	/**
	 * Returns the context class used by this executor.
	 *
	 * <p>This executor uses {@link OrangeRedisMultipleValueContext} to handle:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Multiple member values and their types</li>
	 * </ul>
	 *
	 * @return the class of the context used by this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisMultipleValueContext.class;
	}
	
	/**
	 * Converts the scores to a Map with members as keys and scores as values.
	 *
	 * <p>This method creates a Map where:
	 * <ul>
	 *   <li>Keys are the original member values</li>
	 *   <li>Values are the scores converted to the specified numeric type</li>
	 * </ul>
	 *
	 * <p>Supported value types:
	 * <ul>
	 *   <li>Double (default) - Original score values</li>
	 *   <li>Long - Converted using {@link Double#longValue()}</li>
	 *   <li>Integer - Converted using {@link Double#intValue()}</li>
	 *   <li>Float - Converted using {@link Double#floatValue()}</li>
	 *   <li>BigDecimal - Created from score's string representation</li>
	 * </ul>
	 *
	 * @param ctx the Redis context containing method information
	 * @param originValues the original member values to use as map keys
	 * @param returnClass the specific Map implementation class to instantiate
	 * @param results the list of scores to convert
	 * @return a Map containing member-score pairs with converted score values
	 */
	private Map toMap(OrangeRedisContext ctx, List<Object> originValues, Class returnClass, List<Double> results) {
		Type mapValueType = OrangeReflectionUtils.getMapValueType(ctx.getOperationMethod().getGenericReturnType());
		Class mapValueClass = OrangeReflectionUtils.getRawType(mapValueType);
		int len = originValues.size();
		if(mapValueClass == Long.class) {
			Map resultMap = OrangeReflectionUtils.newMap(returnClass);
			for(int i = 0; i < len; i++) {
				resultMap.put(originValues.get(i), results.get(i).longValue());
			}
			return resultMap;
		}
		else if(mapValueClass == Integer.class) {
			Map resultMap = OrangeReflectionUtils.newMap(returnClass);
			for(int i = 0; i < len; i++) {
				resultMap.put(originValues.get(i), results.get(i).intValue());
			}
			return resultMap;
		}
		else if(mapValueClass == Float.class) {
			Map resultMap = OrangeReflectionUtils.newMap(returnClass);
			for(int i = 0; i < len; i++) {
				resultMap.put(originValues.get(i), results.get(i).floatValue());
			}
			return resultMap;
		}
		else if(mapValueClass == BigDecimal.class) {
			Map resultMap = OrangeReflectionUtils.newMap(returnClass);
			for(int i = 0; i < len; i++) {
				resultMap.put(originValues.get(i), new BigDecimal(results.get(i).toString()));
			}
			return resultMap;
		}
		Map resultMap = OrangeReflectionUtils.newMap(returnClass);
		for(int i = 0; i < len; i++) {
			resultMap.put(originValues.get(i), results.get(i));
		}
		return resultMap;
	}
	
	/**
	 * Converts scores to an array of the specified type.
	 *
	 * <p>This method supports conversion to:
	 * <ul>
	 *   <li>Numeric arrays (Long[], Integer[], Float[], BigDecimal[], Double[])</li>
	 *   <li>Custom object arrays with @RedisValue and @Score annotations</li>
	 * </ul>
	 *
	 * <p>For numeric types, the conversion follows these rules:
	 * <ul>
	 *   <li>Long[] - Using {@link Double#longValue()}</li>
	 *   <li>Integer[] - Using {@link Double#intValue()}</li>
	 *   <li>Float[] - Using {@link Double#floatValue()}</li>
	 *   <li>BigDecimal[] - Created from score's string representation</li>
	 *   <li>Double[] - Original values</li>
	 * </ul>
	 *
	 * @param argumentClass the target array component type
	 * @param results the list of scores to convert
	 * @param values the original member values (used for custom objects)
	 * @return an array of the specified type containing the converted scores
	 */
	private Object toArray(Class<?> argumentClass,List<Double> results, List<Object> values) {
		if(argumentClass == Long.class) {
			int size = results.size();
			Long[] newResult = new Long[size];
			for(int i = 0; i < size; i++) {
				newResult[i] = results.get(i).longValue();
			}
			return newResult;
		}
		else if(argumentClass == Integer.class) {
			int size = results.size();
			Integer[] newResult = new Integer[size];
			for(int i = 0; i < size; i++) {
				newResult[i] = results.get(i).intValue();
			}
			return newResult;
		}
		else if(argumentClass == Float.class) {
			int size = results.size();
			Float[] newResult = new Float[size];
			for(int i = 0; i < size; i++) {
				newResult[i] = results.get(i).floatValue();
			}
			return newResult;
		}
		else if(argumentClass == BigDecimal.class) {
			int size = results.size();
			BigDecimal[] newResult = new BigDecimal[size];
			for(int i = 0; i < size; i++) {
				newResult[i] = new BigDecimal(results.get(i).toString());
			}
			return newResult;
		}
		else if (argumentClass == Double.class) {
			return results.toArray(new Double[results.size()]);	
		}
		List objectWithScoreList = toObjectWithScoreList(argumentClass,results,values);
		return objectWithScoreList.toArray();
	}
	
	/**
	 * Converts scores to a List of the specified type.
	 *
	 * <p>This method supports conversion to:
	 * <ul>
	 *   <li>Lists of numeric types (Long, Integer, Float, BigDecimal, Double)</li>
	 *   <li>Lists of custom objects with {@code @RedisValue} and {@code @Score} annotations</li>
	 * </ul>
	 *
	 * <p>For numeric types, the conversion follows these rules:
	 * <ul>
	 *   <li>Long - Using {@link Double#longValue()}</li>
	 *   <li>Integer - Using {@link Double#intValue()}</li>
	 *   <li>Float - Using {@link Double#floatValue()}</li>
	 *   <li>BigDecimal - Created from score's string representation</li>
	 *   <li>Double - Original values returned as-is</li>
	 * </ul>
	 *
	 * @param argumentClass the type of elements in the resulting list
	 * @param results the list of scores to convert
	 * @param values the original member values (used for custom objects)
	 * @return a List of the specified type containing the converted scores
	 */
	private Object toList(Class<?> argumentClass,List<Double> results, List<Object> values) {
		if(argumentClass == Long.class) {
			List<Long> newResult = new ArrayList<>(results.size());
			results.forEach(t -> newResult.add(t.longValue()));
			return newResult;
		}
		else if(argumentClass == Integer.class) {
			List<Integer> newResult = new ArrayList<>(results.size());
			results.forEach(t -> newResult.add(t.intValue()));
			return newResult;
		}
		else if(argumentClass == Float.class) {
			List<Float> newResult = new ArrayList<>(results.size());
			results.forEach(t -> newResult.add(t.floatValue()));
			return newResult;
		}
		else if(argumentClass == BigDecimal.class) {
			List<BigDecimal> newResult = new ArrayList<>(results.size());
			results.forEach(t -> newResult.add(new BigDecimal(t.toString())));
			return newResult;
		}
		else if (argumentClass == Double.class) {
			return results;
		}
		return toObjectWithScoreList(argumentClass,results,values);
	}
	
	/**
	 * Creates a list of custom objects with member values and scores.
	 *
	 * <p>This method creates objects of the specified type and populates them with:
	 * <ul>
	 *   <li>Member values - injected into fields annotated with {@code @RedisValue}</li>
	 *   <li>Scores - injected into fields annotated with {@code @Score}</li>
	 * </ul>
	 *
	 * <p>The target class must have:
	 * <ul>
	 *   <li>A no-args constructor</li>
	 *   <li>At least one field annotated with {@code @RedisValue} for member values</li>
	 *   <li>At least one field annotated with {@code @Score} for score values</li>
	 * </ul>
	 *
	 * <p>Score field types can be any of:
	 * <ul>
	 *   <li>Double - Original score value</li>
	 *   <li>Long - Converted using {@link Double#longValue()}</li>
	 *   <li>Integer - Converted using {@link Double#intValue()}</li>
	 *   <li>Float - Converted using {@link Double#floatValue()}</li>
	 *   <li>BigDecimal - Created from score's string representation</li>
	 * </ul>
	 *
	 * @param argumentClass the type of objects to create
	 * @param results the list of scores to inject
	 * @param values the list of member values to inject
	 * @return a List of populated objects of the specified type
	 */
	private List toObjectWithScoreList(Class<?> argumentClass,List<Double> results, List<Object> values) {
		Field[] fields = argumentClass.getDeclaredFields();
		Field scoreField = null;
		Field valueField = null;
		for(Field field : fields) {
			if(field.isAnnotationPresent(RedisValue.class)) {
				valueField = field;
			}
			else if(field.isAnnotationPresent(Score.class)) {
				scoreField = field;
			}
		}
		if(scoreField == null || valueField == null) {
			throw new OrangeRedisException(String.format("Return type not match, the argument type of the collection or array must be a number or a object have two fields.%n And the fields must be annotated with @%s or @%s", RedisValue.class,Score.class));
		}
		List<Object> actualResult = new ArrayList<>();
		int size = results.size();
		for(int i = 0; i < size; i++) {
			try {
				Object obj = argumentClass.getConstructor().newInstance();
				OrangeReflectionUtils.setFieldValue(scoreField, obj, results.get(i));
				OrangeReflectionUtils.setFieldValue(valueField, obj, values.get(i));
				actualResult.add(obj);
			}catch (Exception e) {
				throw new OrangeRedisException(String.format("The argument type %s must have a constructor without any argument", argumentClass));
			}
		}
		return actualResult;	
	}

}