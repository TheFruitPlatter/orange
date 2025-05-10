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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeCountHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRandomMembersExecutor extends OrangeRandomMemberExecutor {
	
	public OrangeRandomMembersExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List classes = super.getSupportedAnnotationClasses();
		classes.add(Count.class);
		return classes;
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCountHashContext.class;
	}
	
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeCountHashContext ctx = (OrangeCountHashContext) context;
		Integer count = ctx.getCount();
		if(count <= 0) {
			return new ArrayList<>();
		}
		List<Object> results = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			Object object = super.execute(context);
			if(object instanceof Collection) {
				Collection collection = (Collection)object;
				results.addAll(collection);
			}
			else if(object.getClass().isArray()) {
				results.add(Array.get(object, 0));
			}else{
				results.add(object);
			}
		}
		return results;
	}
}
