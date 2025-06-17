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
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.transaction.Version;
import com.langwuyue.orange.redis.annotation.value.GetValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionSnapshotGetExecutor;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionKeyConstants;
import com.langwuyue.orange.redis.executor.transaction.context.OrangeTransactionVersionContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Redis transaction snapshot retrieval executor, used to fetch values of specific versions from Redis.
 * 
 * <p>This executor implements transaction snapshot reading functionality, allowing clients to retrieve
 * Redis values of specified versions. This is crucial for implementing Multi-Version Concurrency Control (MVCC)
 * and transaction isolation, ensuring data consistency during transaction execution.
 * 
 * <p>The executor is triggered through {@link GetValue} and {@link Version} annotations,
 * using version numbers to locate and retrieve specific versions of data. This mechanism ensures
 * that consistent data snapshots can be read even in concurrent transaction environments.
 * 
 * <p>The core concept of snapshot reading is: each value modification creates a new version
 * rather than directly overwriting existing values. This allows transactions to read data versions
 * that existed when they started, unaffected by subsequent modifications.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/transaction">Orange Redis Transaction Documentation</a>
 */
public class OrangeTransactionSnapshotGetExecutor extends OrangeRedisGetOneAbstractExecutor implements OrangeRedisTransactionSnapshotGetExecutor {
	
	/**
	 * Redis hash operations interface, used for executing underlying Redis hash data structure operations.
	 * 
	 * <p>This component is responsible for actual interaction with Redis, providing hash operation
	 * functionality needed to retrieve specific version values. It handles data serialization and
	 * deserialization, as well as communication with the Redis server.
	 */
	private OrangeRedisHashOperations operations;
	
	/**
	 * Constructs a new transaction snapshot retrieval executor instance.
	 * 
	 * @param operations Redis hash operations interface for executing underlying Redis data access operations
	 * @param idGenerator Executor ID generator for generating unique executor identifiers
	 */
	public OrangeTransactionSnapshotGetExecutor(
		OrangeRedisHashOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator
	) {
		super(idGenerator);
		this.operations = operations;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports two types of annotations:
	 * <ul>
	 *   <li>{@link GetValue} - Identifies fields that need to retrieve values from Redis</li>
	 *   <li>{@link Version} - Specifies the version number of data to retrieve</li>
	 * </ul>
	 * 
	 * <p>These two annotations work together: {@link GetValue} specifies which value to retrieve,
	 * while {@link Version} specifies which version to fetch. This combination enables precise
	 * transaction snapshot reading.
	 *
	 * @return A list containing {@link GetValue} and {@link Version} annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetValue.class,Version.class);
	}

	/**
	 * Executes the Redis value snapshot retrieval operation.
	 * 
	 * <p>This method is the core implementation of the retrieval operation. It extracts necessary
	 * information (key, version number, and value type) from the context, then calls
	 * {@link #get(String, Long, RedisValueTypeEnum, Type)} to perform the actual retrieval.
	 * 
	 * @param context Redis context containing information needed for retrieval operation
	 * @param valueField Target field to populate, may be null
	 * @param returnArgumentType Type to use as return type if valueField is null
	 * @return Single-element collection containing the retrieved value
	 * @throws Exception if an error occurs during retrieval operation
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		OrangeTransactionVersionContext ctx = (OrangeTransactionVersionContext) context;
		Object result = get(
			ctx.getRedisKey().getValue(),
			ctx.getVersion(),
			ctx.getValueType(),
			valueField == null ? returnArgumentType : valueField.getGenericType()
		);
		return OrangeCollectionUtils.asList(result);
	}
	
	/**
	 * Retrieves a value of a specific version from Redis.
	 * 
	 * <p>This method is the low-level implementation for executing Redis snapshot retrieval operations.
	 * It accesses specific version data stored in Redis by combining the key and version number.
	 * The method uses a version prefix to construct complete hash field names, ensuring proper
	 * distinction between different versions of data.
	 * 
	 * @param key Redis key for locating the hash structure
	 * @param version Data version number to retrieve
	 * @param valueType Redis storage type of the value
	 * @param returnType Expected return type for deserialization
	 * @return Retrieved value object, or null if the specified version doesn't exist
	 * @throws Exception if an error occurs during retrieval or deserialization
	 */
	@Override
	public Object get(String key, Long version, RedisValueTypeEnum valueType,Type returnType) throws Exception {
		return this.operations.get(
			key, 
			OrangeRedisTransactionKeyConstants.VERSION_PREFIX + version, 
			RedisValueTypeEnum.STRING, 
			valueType, 
			returnType
		);
	}
}