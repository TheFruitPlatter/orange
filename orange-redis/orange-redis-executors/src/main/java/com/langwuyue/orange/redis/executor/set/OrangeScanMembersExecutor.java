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
package com.langwuyue.orange.redis.executor.set;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.ScanPattern;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetAbstractExecutor;
import com.langwuyue.orange.redis.executor.set.context.OrangeScanContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations.ScanResults;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeScanMembersExecutor extends OrangeRedisGetAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	public OrangeScanMembersExecutor(OrangeRedisSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetMembers.class,ScanPattern.class,Count.class,PageNo.class);
	}

	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeScanContext ctx = (OrangeScanContext) context;
		ScanResults scanResults = this.operations.scan(
			context.getRedisKey().getValue(), 
			ctx.getPattern(),
			ctx.getCount(),
			ctx.getPageNo(),
			context.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
		if(scanResults == null) {
			return null;
		}
		return OrangeCollectionUtils.asList(scanResults);
	}

	@Override
	protected Object toReturnValue(
		OrangeRedisContext context, 
		Collection result, 
		Type returnArgumentType, 
		Field field
	)throws Exception {
		if(result == null) {
			return null;
		}
		List list = (List) result;
		ScanResults scanResults = (ScanResults)list.get(0);
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Map.class || returnClass.isAssignableFrom(LinkedHashMap.class)) {
			Map<Long,Set<Object>> map = new LinkedHashMap<>();
			map.put(scanResults.getCursor(), scanResults.getMembers());
			return map;
		}
		return super.toReturnValue(context, scanResults.getMembers(), returnArgumentType, field);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeScanContext.class;
	}
	
	@Override
	protected Type getReturnArgumentType(OrangeRedisContext context){
		Class<?> returnClass = context.getOperationMethod().getReturnType();
		if(returnClass == Map.class || returnClass.isAssignableFrom(LinkedHashMap.class)) {
			Type mapValueType = OrangeReflectionUtils.getMapValueType(context.getOperationMethod().getGenericReturnType());
			return OrangeReflectionUtils.getCollectionOrArrayArgumentType(mapValueType);
		}
		return super.getReturnArgumentType(context);
	}
	
}
