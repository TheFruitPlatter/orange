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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeGetOneWithScoresAbstractExecutor extends OrangeGetWithScoresAbstractExecutor {
	
	protected OrangeGetOneWithScoresAbstractExecutor(OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Object result = super.execute(context);
		if(result == null) {
			return result;
		}
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass.isArray() 
				|| Collection.class.isAssignableFrom(returnClass) 
				|| Map.class.isAssignableFrom(returnClass)) {
			return result;	
		}
		else{
			ArrayList list = ((ArrayList)result);
			return list.isEmpty() ? null : list.get(0);
		}
	}

	@Override
	protected Type getReturnArgumentType(OrangeRedisContext context) {
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass.isArray() 
				|| Collection.class.isAssignableFrom(returnClass) 
				|| Map.class.isAssignableFrom(returnClass)) {
			return super.getReturnArgumentType(context);	
		}
		else{
			return context.getOperationMethod().getGenericReturnType();
		}
	}
	
	@Override
	protected Object toReturnValue(OrangeRedisContext context, Collection result, Type returnArgumentType, Field field) throws Exception {
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(Map.class.isAssignableFrom(returnClass)) {
			Class<?> valueType = getMapValueType(context.getOperationMethod().getGenericReturnType());
			return toMap(valueType,result,field,returnArgumentType,returnClass);
		}else{
			Type genericType = this.getReturnArgumentType(context);
			Class<?> rawType = getRawType(genericType);
			if(Map.class.isAssignableFrom(rawType)) {
				return toWithScoreReturnMultipleMap(result, field, returnArgumentType, returnClass, genericType);
			}
			return toWithScoreReturnValue(result, field, returnArgumentType, returnClass);
		}
	}

	@Override
	protected Object getArrayOrCollectionInstance(Class<?> returnClass, int size) {
		if(returnClass.isArray() 
				|| Collection.class.isAssignableFrom(returnClass) 
				|| Map.class.isAssignableFrom(returnClass)) {
			return super.getArrayOrCollectionInstance(returnClass, size);
		}
		return new ArrayList<>(1);
	}
}
