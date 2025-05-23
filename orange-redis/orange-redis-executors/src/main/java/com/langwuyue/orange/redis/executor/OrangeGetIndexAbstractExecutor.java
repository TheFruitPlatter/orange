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
package com.langwuyue.orange.redis.executor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.GetIndexs;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisMultipleValueContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeGetIndexAbstractExecutor extends OrangeRedisAbstractExecutor {
	
	private OrangeRedisLogger logger;

	protected OrangeGetIndexAbstractExecutor(OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator);
		this.logger = logger;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		Class returnClass = context.getOperationMethod().getReturnType();
		boolean returnMap = Map.class.isAssignableFrom(returnClass);
		if(returnMap) {
			Map<Object,Long> resultMap = new LinkedHashMap<>();
			doGetIndex(context,resultMap::put);
			// It's hard to know whether the null returned by resultMap.get(key) is due to an exception.
			return resultMap;
		}
		else if(returnClass.isAssignableFrom(ArrayList.class)) {
			List<Long> results = new ArrayList<>();
			doGetIndex(context,(o,t) -> results.add(t));
			return results;
		}
		else if(returnClass.isArray()) {
			List<Long> results = new ArrayList<>();
			doGetIndex(context,(o,t) -> results.add(t));
			return results.toArray();
		}
		throw new OrangeRedisException("The return type of an indexing operation must be a collection, an array, or a map");
	}
	
	protected abstract Long getIndex(OrangeRedisContext context,Object value) throws Exception;
	
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisMultipleValueContext.class;
	}
	
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetIndexs.class,Multiple.class,ContinueOnFailure.class);
	}
	
	private void doGetIndex(OrangeRedisContext context,BiConsumer<Object,Long> consumer) {
		OrangeRedisMultipleValueContext ctx = (OrangeRedisMultipleValueContext)context;
		boolean continueOnFailure = ctx.continueOnFailure();
		try {
			ctx.forEach((t,o) -> {
				try {
					Long result = getIndex(context,t);
					if(consumer != null) {
						consumer.accept(o,result);
					}
					if(result == null) {
						throw new OrangeRedisException(
							String.format(
								"Null returned! Operation: %s %n Operatin owner: %s", 
								context.getOperationMethod(),
								context.getOperationOwner()
							)
						);
					}
				}catch (Exception e) {
					// The operation may have been interrupted by a client timeout or network error, 
					// but it was actually completed successfully.
					if(!continueOnFailure) {
						if(e instanceof OrangeRedisException) {
							throw (OrangeRedisException) e;
						}else {
							throw new OrangeRedisException(
								String.format(
									"An exception occurred during get index operation! Operation: %s %n Operatin owner: %s", 
									context.getOperationMethod(),
									context.getOperationOwner()
								),
								e
							);
						}
					}else{
						if(!(e instanceof OrangeRedisException)) {
							this.logger.warn(
								String.format(
									"An exception occurred during get index operation! Operation: %s %n Operatin owner: %s", 
									context.getOperationMethod(),
									context.getOperationOwner()
								),
								e
							);
						}
					}
				}
			});
		}catch (Exception e) {
			this.logger.warn("An exception occurred during get index operation.",e);
		}
	}
}
