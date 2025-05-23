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

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.executor.OrangeAddMembersAbstractExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMembersExecutor extends OrangeAddMembersAbstractExecutor {
	
	private OrangeRedisSetOperations operations;

	public OrangeAddMembersExecutor(OrangeRedisSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	@Override
	protected Long doAdd(OrangeRedisContext ctx) throws Exception {
		OrangeRedisMultipleValueContext context = (OrangeRedisMultipleValueContext) ctx;
		return this.operations.add(context.getRedisKey().getValue(), context.getValueType(), context.toArray());
	}

	@Override
	protected Long doAdd(OrangeRedisContext ctx, Object value) throws Exception {
		return this.operations.add(ctx.getRedisKey().getValue(), ctx.getValueType(), value);
	}
}
