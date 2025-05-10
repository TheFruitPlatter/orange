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
package com.langwuyue.orange.redis.mapping;

import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.executor.cross.geo.OrangeLimitedSearchInBoxAndStoreByExistsMemberExecutor;
import com.langwuyue.orange.redis.executor.cross.geo.OrangeLimitedSearchInBoxAndStoreExecutor;
import com.langwuyue.orange.redis.executor.cross.geo.OrangeLimitedSearchInRadiusAndStoreByExistsMemberExecutor;
import com.langwuyue.orange.redis.executor.cross.geo.OrangeLimitedSearchInRadiusAndStoreExecutor;
import com.langwuyue.orange.redis.executor.cross.geo.OrangeSearchInBoxAndStoreByExistsMemberExecutor;
import com.langwuyue.orange.redis.executor.cross.geo.OrangeSearchInBoxAndStoreExecutor;
import com.langwuyue.orange.redis.executor.cross.geo.OrangeSearchInRadiusAndStoreByExistsMemberExecutor;
import com.langwuyue.orange.redis.executor.cross.geo.OrangeSearchInRadiusAndStoreExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.template.NoTemplate;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisGeoCrossKeyExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	public OrangeRedisGeoCrossKeyExecutorsMapping(
		OrangeRedisGeoOperations operations,
		OrangeRedisGeoCrossKeyExecutorIdGenerator generator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRedisLogger logger
	) {
		super(operations,generator,scriptOperations,listeners,multipleListeners,logger);
	}

	@Override
	protected void registerExecutors(
		List<OrangeRedisExecutor> executors, 
		OrangeRedisOperations operations,
		OrangeRedisExecutorIdGenerator generator,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRedisLogger logger
	) {
		OrangeRedisGeoOperations geoOperations = (OrangeRedisGeoOperations) operations;
		executors.add(new OrangeLimitedSearchInBoxAndStoreByExistsMemberExecutor(geoOperations,generator));
		executors.add(new OrangeLimitedSearchInBoxAndStoreExecutor(geoOperations,generator));
		executors.add(new OrangeLimitedSearchInRadiusAndStoreByExistsMemberExecutor(geoOperations,generator));
		executors.add(new OrangeLimitedSearchInRadiusAndStoreExecutor(geoOperations,generator));
		executors.add(new OrangeSearchInBoxAndStoreByExistsMemberExecutor(geoOperations,generator));
		executors.add(new OrangeSearchInBoxAndStoreExecutor(geoOperations,generator));
		executors.add(new OrangeSearchInRadiusAndStoreByExistsMemberExecutor(geoOperations,generator));
		executors.add(new OrangeSearchInRadiusAndStoreExecutor(geoOperations,generator));
	}

	@Override
	protected Class<?> getTemplateClass() {
		return NoTemplate.class;
	}
}
