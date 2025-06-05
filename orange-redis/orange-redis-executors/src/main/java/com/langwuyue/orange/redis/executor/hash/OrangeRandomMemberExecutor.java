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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Executor for randomly retrieving a single member from a Redis Hash.
 *
 * <p>This executor provides functionality to get a random key-value pair from a Redis Hash.
 * The operation is useful for scenarios requiring random sampling or display of hash members.
 *
 * <p>The executor supports the following annotation:
 * <ul>
 *   <li>{@link Random} - Marks this as a random member retrieval operation</li>
 * </ul>
 *
 * <p>Key features:
 * <ul>
 *   <li>Returns exactly one random member from the hash</li>
 *   <li>Uses Redis's HRANDFIELD command internally</li>
 *   <li>Supports both simple and complex return types</li>
 *   <li>Automatically handles type conversion of returned values</li>
 * </ul>
 *
 * <p>Return value handling:
 * <ul>
 *   <li>For non-collection return types: returns a single random member</li>
 *   <li>For collection/array return types: returns a collection with one random member</li>
 * </ul>
 *
 * <p>Performance considerations:
 * The HRANDFIELD operation has a time complexity of O(1), making it very efficient
 * regardless of the hash size.
 *
 * <p>Note: For retrieving multiple random members, use {@link OrangeRandomMembersExecutor}.
 * For retrieving multiple distinct random members, use {@link OrangeRandomAndDistinctMembersExecutor}.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRandomMemberExecutor extends OrangeGetMembersExecutor {
	
	private OrangeRedisHashOperations operations;

	public OrangeRandomMemberExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List classes = super.getSupportedAnnotationClasses();
		classes.add(Random.class);
		return classes;
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeHashContext.class;
	}

	@Override
	protected Map doGet(OrangeRedisContext context, Type keyType, Type valueType) throws Exception {
		OrangeHashContext ctx = (OrangeHashContext) context;
		return this.operations.randomEntries(ctx.getRedisKey().getValue(), 1L, ctx.getKeyType(), ctx.getValueType(), keyType, valueType);
	}

	@Override
	protected Object toReturnValue(
		Map resultMap, 
		Field keyField, 
		Field valueField, 
		Type returnArgumentType,
		Class returnClass
	) throws Exception {
		if(Collection.class.isAssignableFrom(returnClass) || returnClass.isArray()) {
			return super.toReturnValue(resultMap, keyField, valueField, returnArgumentType, returnClass);
		}
		Set<Entry> entrySet = resultMap.entrySet();
		for(Entry entry : entrySet) {
			Object obj = returnClass.getConstructor().newInstance();
			OrangeReflectionUtils.setFieldValue(keyField, obj, entry.getKey());
			OrangeReflectionUtils.setFieldValue(valueField, obj, entry.getValue());
			return obj;
		}
		return null;
	}

	@Override
	protected Type getReturnArgumentType(OrangeRedisContext context) {
		Class returnClass = context.getOperationMethod().getReturnType();
		if(Collection.class.isAssignableFrom(returnClass) || returnClass.isArray()) {
			return super.getReturnArgumentType(context);
		}
		return context.getOperationMethod().getGenericReturnType();
	}

	@Override
	protected Object getArrayOrCollectionInstance(Class<?> returnType, int size) {
		if(Collection.class.isAssignableFrom(returnType) || returnType.isArray()) {
			return super.getArrayOrCollectionInstance(returnType, size);
		}
		return new ArrayList<>(1);
	}
	
	
	
	
}