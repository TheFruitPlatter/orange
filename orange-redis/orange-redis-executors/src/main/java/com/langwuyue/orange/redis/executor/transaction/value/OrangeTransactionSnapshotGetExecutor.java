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
package com.langwuyue.orange.redis.executor.transaction.value;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.transaction.Version;
import com.langwuyue.orange.redis.annotation.value.GetValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionSnapshotGetExecutor;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionKeyConstants;
import com.langwuyue.orange.redis.executor.transaction.context.OrangeTransactionVersionContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeTransactionSnapshotGetExecutor extends OrangeRedisGetOneAbstractExecutor implements OrangeRedisTransactionSnapshotGetExecutor {
	
	private OrangeRedisHashOperations operations;
	
	public OrangeTransactionSnapshotGetExecutor(
		OrangeRedisHashOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator
	) {
		super(idGenerator);
		this.operations = operations;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetValue.class,Version.class);
	}

	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeTransactionVersionContext ctx = (OrangeTransactionVersionContext) context;
		Object result = get(
			ctx.getRedisKey().getValue(),
			ctx.getVersion(),
			ctx.getValueType(),
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
		return OrangeCollectionUtils.asList(result);
	}
	
	@Override
	public Object get(String key, Long version, RedisValueTypeEnum valueType,Type returnType) throws Exception {
		return this.operations.get(
			key, 
			OrangeRedisTransactionKeyConstants.VERSION_PREFIX + version, 
			RedisValueTypeEnum.STRING, 
			valueType, 
			returnType
		);
	}
}
