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
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetMembersExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	public OrangeGetMembersExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashContext.class;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Class returnClass = context.getOperationMethod().getReturnType();
		if(Map.class.isAssignableFrom(returnClass)) {
			Type[] types = OrangeReflectionUtils.getMapActaulTypeArguments(context.getOperationMethod().getGenericReturnType());
			Type keyType = types[0];
			Type valueType = types[1];
			return doGet(context,keyType,valueType);
		}else{
			Type returnArgumentType = getReturnArgumentType(context);
			Field valueField = getValueField(returnArgumentType);
			Field keyField = getKeyField(returnArgumentType);
			if(valueField == null || keyField == null) {
				throw new OrangeRedisException(String.format("The return value must contain two fields annotated with @%s and @%s", HashKey.class,RedisValue.class));
			}
			Map resultMap = doGet(context,keyField.getGenericType(),valueField.getGenericType());
			return toReturnValue(resultMap,keyField,valueField,returnArgumentType,returnClass);
		}
		
	}
	
	protected Map doGet(OrangeRedisContext context, Type keyType, Type valueType) throws Exception {
		OrangeHashContext ctx = (OrangeHashContext) context;
		return this.operations.entries(context.getRedisKey().getValue(), ctx.getKeyType(), ctx.getValueType(), keyType, valueType);
	}
	
	protected Object toReturnValue(Map resultMap, Field keyField, Field valueField, Type returnArgumentType, Class returnClass) throws Exception {
		Class<?> argumentClass = getRawType(returnArgumentType);
		Object instance = getArrayOrCollectionInstance(returnClass, resultMap.size());
		if(instance instanceof Collection) {
			Collection collection = (Collection) instance;
			Set<Entry> entrySet = resultMap.entrySet();
			for(Entry entry : entrySet) {
				Object obj = argumentClass.getConstructor().newInstance();
				OrangeReflectionUtils.setFieldValue(keyField, obj, entry.getKey());
				OrangeReflectionUtils.setFieldValue(valueField, obj, entry.getValue());
				collection.add(obj);
			}
			return collection;
		}else{
			int i = 0;
			Set<Entry> entrySet = resultMap.entrySet();
			for(Entry entry : entrySet) {
				Object obj = argumentClass.getConstructor().newInstance();
				OrangeReflectionUtils.setFieldValue(keyField, obj, entry.getKey());
				OrangeReflectionUtils.setFieldValue(valueField, obj, entry.getValue());
				Array.set(instance, i, obj);
				i++;
			}
			return instance;
		}
	}
	
	protected Object getArrayOrCollectionInstance(Class<?> returnType,int size) {
		return OrangeReflectionUtils.getArrayOrCollectionInstance(returnType, size);
	}
	
	protected Field getValueField(Type type){
		Class<?> returnType = getRawType(type);
		Field[] fields = returnType.getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(RedisValue.class)) {
				return field;
			}
		}
		return null;
	}
	
	protected Field getKeyField(Type type){
		Class<?> returnType = getRawType(type);
		Field[] fields = returnType.getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(HashKey.class)) {
				return field;
			}
		}
		return null;
	}
	
	protected Class<?> getRawType(Type type){
		return OrangeReflectionUtils.getRawType(type);
	}
	
	protected Type getReturnArgumentType(OrangeRedisContext context){
		return OrangeReflectionUtils.getCollectionOrArrayArgumentType(context.getOperationMethod().getGenericReturnType());
	}
}
