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

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeGetIndexAbstractExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeGetRanksExecutor extends OrangeGetIndexAbstractExecutor {

	private OrangeRedisZSetOperations operations;

	public OrangeGetRanksExecutor(OrangeRedisZSetOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator,logger);
		this.operations = operations;
	}

	@Override
	protected Long getIndex(OrangeRedisContext context,Object value) throws Exception {
		return this.operations.getMemberRank(context.getRedisKey().getValue(), value, context.getValueType());
	}

}
