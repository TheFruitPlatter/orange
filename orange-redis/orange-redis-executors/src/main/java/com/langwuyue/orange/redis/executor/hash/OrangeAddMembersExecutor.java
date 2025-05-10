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

import java.util.Map;
import java.util.Map.Entry;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeAddMembersAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeAddMembersContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMembersExecutor extends OrangeAddMembersAbstractExecutor {
	
	private OrangeRedisHashOperations operations;

	public OrangeAddMembersExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeAddMembersContext.class;
	}

	@Override
	protected Long doAdd(OrangeRedisContext context) throws Exception {
		OrangeAddMembersContext ctx = (OrangeAddMembersContext) context;
		Map members = ctx.getMembers();
		this.operations.putMembers(ctx.getRedisKey().getValue(), members, ctx.getKeyType(), ctx.getValueType());
		return (long) members.size();
	}

	@Override
	protected Long doAdd(OrangeRedisContext context, Object value) throws Exception {
		OrangeAddMembersContext ctx = (OrangeAddMembersContext) context;
		Entry entry = (Entry) value;
		this.operations.putMember(ctx.getRedisKey().getValue(), entry.getKey(), entry.getValue(), ctx.getKeyType(), ctx.getValueType());
		return 1L;
	}
}
