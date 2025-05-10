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

import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.transaction.Commit;
import com.langwuyue.orange.redis.annotation.transaction.Version;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeTransactionCommitWithExpirationExecutor extends OrangeTransactionCommitExecutor {
	
	private OrangeRedisHashOperations operations;
	
	public OrangeTransactionCommitWithExpirationExecutor(
		OrangeRedisScriptOperations scriptOperations,
		OrangeRedisHashOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		OrangeRedisDefaultTransactionManager transactionManager,
		OrangeRedisLogger logger
	) {
		super(scriptOperations,idGenerator,logger);
		this.operations = operations;
		this.setTransactionManager(transactionManager);
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Boolean success = (Boolean) super.execute(context);
		if(success) {
			Key key = context.getRedisKey();
			this.operations.expire(key.getValue(), key.getExpirationTime(), key.getExpirationTimeUnit());
		}
		return success;
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Commit.class,Version.class,SetExpiration.class);
	}
}
