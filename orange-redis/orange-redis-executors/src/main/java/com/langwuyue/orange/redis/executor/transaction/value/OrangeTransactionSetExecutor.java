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
import java.util.List;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.value.SetValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager.OrangeRedisTransactionKey;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager.OrangeTransactionCommitProcessor;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionKeyConstants;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeTransactionSetExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisHashOperations hashOperations;
	
	private OrangeRedisDefaultTransactionManager transactionManager;
	
	private OrangeTransactionCommitProcessor processor;

	public OrangeTransactionSetExecutor(
		OrangeRedisHashOperations hashOperations,
		OrangeRedisExecutorIdGenerator idGenerator,
		OrangeRedisDefaultTransactionManager transactionManager,
		OrangeTransactionCommitProcessor processor
	) {
		super(idGenerator);
		this.hashOperations = hashOperations;
		this.transactionManager = transactionManager;
		this.processor = processor;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueContext ctx = (OrangeRedisValueContext) context;
		String key = context.getRedisKey().getValue();
		Long version = this.hashOperations.increment(key, OrangeRedisTransactionKeyConstants.NEXT_VERSION, 1L, RedisValueTypeEnum.STRING);
		this.hashOperations.putMember(key, OrangeRedisTransactionKeyConstants.VERSION_PREFIX + version, ctx.getValue(), RedisValueTypeEnum.STRING, context.getValueType());
		OrangeRedisTransactionKey transactionKey = new OrangeRedisTransactionKey();
		transactionKey.setKey(key);
		transactionKey.setOriginKey(ctx.getRedisKey().getOriginalKey());
		transactionKey.setVersion(version);
		this.transactionManager.saveTransactionInfo(this.processor,transactionKey);
		return version;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class,SetValue.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueContext.class;
	}
}
