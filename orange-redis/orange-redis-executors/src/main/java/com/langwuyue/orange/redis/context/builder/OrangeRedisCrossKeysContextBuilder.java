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
package com.langwuyue.orange.redis.context.builder;

import java.util.List;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.cross.context.OrangeCrossOperationContext;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisCrossKeysContextBuilder extends OrangeRedisContextBuilder {
	
	private List<String> keys;
	
	private String storeTo;
	
	public OrangeRedisCrossKeysContextBuilder() {}
	
	@Override
	protected OrangeRedisContext newContext() throws Exception {
		return OrangeCrossOperationContext.newInstance(
			this.getContextClass(),
			this.getOperationOwner(),
			this.getOperationMethod(),
			this.getArgs(),
			this.keys,
			this.storeTo,
			((OrangeCrossKeysOperationArgHandlerMapping)this.getOperationArgHandlerMapping()).getValueType(this.getOperationMethod())
		);
	}

	public OrangeRedisCrossKeysContextBuilder keys(List<String> keys) {
		this.keys = keys;
		return this;
	}

	public OrangeRedisCrossKeysContextBuilder storeTo(String storeTo) {
		this.storeTo = storeTo;
		return this;
	}
}
