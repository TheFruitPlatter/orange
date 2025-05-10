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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetScoresExecutor extends OrangeRedisAbstractExecutor {

	private OrangeRedisZSetOperations operations;

	public OrangeGetScoresExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}


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

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetScores.class,Multiple.class);
	}
	
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisMultipleValueContext.class;
	}
	
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
