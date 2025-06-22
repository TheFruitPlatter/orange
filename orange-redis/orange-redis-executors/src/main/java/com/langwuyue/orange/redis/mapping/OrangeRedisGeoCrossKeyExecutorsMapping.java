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
 * A specialized executor mapping class for Redis Geo cross-key operations.
 * 
 * <p>This class extends {@link OrangeRedisAbstractExecutorsMapping} to provide support
 * for geographical operations that span across multiple Redis keys. It registers various
 * executors that handle operations such as searching within a geographical radius or box
 * and storing the results in a different key.
 * 
 * <p>The mapping includes executors for:
 * <ul>
 *   <li>Limited search operations within a radius with result storage</li>
 *   <li>Limited search operations within a box with result storage</li>
 *   <li>Search operations by existing members with result storage</li>
 *   <li>Various combinations of the above operations</li>
 * </ul>
 * 
 * <p>This class is part of the Orange Redis framework's geo-spatial capabilities,
 * specifically designed for operations that involve storing results in different keys
 * than the source data.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisGeoCrossKeyExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	/**
	 * Constructs a new OrangeRedisGeoCrossKeyExecutorsMapping with the specified components.
	 * 
	 * <p>This constructor initializes the mapping with all necessary dependencies for
	 * handling Redis Geo cross-key operations.
	 *
	 * @param operations the Redis Geo operations implementation to be used by executors
	 * @param generator the executor ID generator specific to Geo cross-key operations
	 * @param listeners collection of listeners for set-if-absent operations
	 * @param scriptOperations operations for executing Redis scripts
	 * @param multipleListeners collection of listeners for multiple set-if-absent operations
	 * @param logger the logger for recording operation details and errors
	 */
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

	/**
	 * Registers all supported Redis Geo cross-key operation executors.
	 * 
	 * <p>This method extends the base implementation by registering executors that handle
	 * geographical operations with result storage in different keys. It initializes and adds
	 * various specialized executors to handle different types of geo-spatial operations.
	 *
	 * @param executors the list to which executors will be added
	 * @param operations the Redis operations implementation
	 * @param generator the executor ID generator
	 * @param scriptOperations operations for executing Redis scripts
	 * @param listeners collection of listeners for set-if-absent operations
	 * @param multipleListeners collection of listeners for multiple set-if-absent operations
	 * @param logger the logger for recording operation details and errors
	 */
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

	/**
	 * Returns the template class used by this executor mapping.
	 * 
	 * <p> This template just helps clarify error messages.
	 *
	 * @return the {@link NoTemplate} class
	 */
	@Override
	protected Class<?> getTemplateClass() {
		return NoTemplate.class;
	}
}