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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.value.GetValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisGetOneAbstractExecutor;
import com.langwuyue.orange.redis.executor.script.ScriptConstants;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionKeyConstants;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * A Redis executor implementation that handles transactional value retrieval operations.
 * 
 * <p>This executor is responsible for retrieving values from Redis within a transactional
 * context. It supports reading both committed values and uncommitted changes made within
 * the current transaction, providing transaction isolation capabilities.
 * 
 * <p>Key features include:
 * <ul>
 *   <li>Version-aware value retrieval using Lua scripts</li>
 *   <li>Support for reading uncommitted changes within the current transaction</li>
 *   <li>Debug mode with detailed logging of Redis operations</li>
 *   <li>Automatic type conversion of retrieved values</li>
 * </ul>
 * 
 * <p>The executor uses a Lua script to ensure atomic operations when retrieving values,
 * taking into account the current transaction version and any uncommitted changes.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/transaction">Orange Redis Transaction Documentation</a>
 */
public class OrangeTransactionGetExecutor extends OrangeRedisGetOneAbstractExecutor {
	
	/**
	 * Script for production
	 */
	private static final String GET_LUA_SCRIPT = String.join("\n",
	    "local versionKey = ARGV[1];",
	    "local valueKeyPrefix = ARGV[2];",
	    "local version = ARGV[3];",
	    "local key = KEYS[1];",
	    "if version == '[[NIL]]' then",
	    "    version = redis.call('HGET', key, versionKey);",
	    "end;",
	    "if version == nil or version == false or version == '[[NIL]]' then",
	    "    return nil;",
	    "else",
	    "    local valueKey = tostring(valueKeyPrefix) .. tostring(version);",
	    "    return redis.call('HGET', key, valueKey);",
	    "end;"
	);
	/**
	 * Script for debug
	 */
	private static final String GET_LUA_SCRIPT_DEBUG = String.join("\n",
		"local traceId = tostring(ARGV[4]);",
		"redis.log(redis.LOG_NOTICE, string.format('traceId: %s, VALUE Transaction OrangeTransactionGetExecutor executing', traceId));",
	    "local versionKey = ARGV[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, versionKey: %s', traceId, tostring(versionKey)));",
	    "local valueKeyPrefix = ARGV[2];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, valueKeyPrefix: %s', traceId, tostring(valueKeyPrefix)));",
	    "local version = ARGV[3];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, version: %s', traceId, tostring(version)));",
	    "local key = KEYS[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, key: %s', traceId, tostring(key)));",
	    "if version == '[[NIL]]' then",
	    "    version = redis.call('HGET', key, versionKey);",
	    "end;",
	    "if version == nil or version == false or version == '[[NIL]]' then",
	    "    return nil;",
	    "else",
	    "    local valueKey = tostring(valueKeyPrefix) .. tostring(version);",
	    "    redis.log(redis.LOG_NOTICE, string.format('traceId: %s, valueKey: %s', traceId, valueKey));",
	    "    return redis.call('HGET', key, valueKey);",
	    "end;"
	);
	/**
	 * Redis script operations for executing Lua scripts.
	 * 
	 * <p>This component is responsible for executing the Lua scripts that perform
	 * atomic value retrieval operations in Redis. It handles script loading,
	 * execution, and result conversion.
	 */
	private OrangeRedisScriptOperations scriptOperations;
	
	/**
	 * Transaction manager for handling Redis transaction state.
	 * 
	 * <p>This component manages transaction lifecycle and provides access to
	 * transaction-specific information such as the current transaction version.
	 * It is used to determine whether there are any uncommitted changes that
	 * should be visible to the current operation.
	 */
	private OrangeRedisDefaultTransactionManager transactionManager;
	
	/**
	 * Logger for diagnostic and debugging information.
	 * 
	 * <p>When debug mode is enabled, this logger is used to provide detailed
	 * information about Redis operations, including trace IDs and operation
	 * parameters. It helps in troubleshooting and monitoring transaction
	 * operations.
	 */
	private OrangeRedisLogger logger;

	/**
	 * Constructs a new transaction get executor with required dependencies.
	 * 
	 * @param scriptOperations Redis script operations for executing Lua scripts
	 * @param idGenerator Generator for creating unique executor identifiers
	 * @param transactionManager Manager for handling transaction state and lifecycle
	 * @param logger Logger for diagnostic and debugging information
	 */
	public OrangeTransactionGetExecutor(
		OrangeRedisScriptOperations scriptOperations,
		OrangeRedisExecutorIdGenerator idGenerator,
		OrangeRedisDefaultTransactionManager transactionManager,
		OrangeRedisLogger logger
	) {
		super(idGenerator);
		this.scriptOperations = scriptOperations;
		this.transactionManager = transactionManager;
		this.logger = logger;
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This method defines which annotations this executor can handle. Currently,
	 * it only supports the {@link GetValue} annotation, which is used to mark
	 * methods that should retrieve values from Redis in a transactional context.
	 * 
	 * <p>The {@link GetValue} annotation enables transactional value retrieval
	 * operations with version awareness, ensuring proper transaction isolation
	 * and consistency when reading data from Redis.
	 *
	 * @return A list containing only the {@link GetValue} annotation class
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetValue.class);
	}

	/**
	 * Performs the actual value retrieval operation from Redis within a transactional context.
	 * 
	 * <p>This method implements the core functionality of retrieving values from Redis
	 * with transaction awareness. It executes a Lua script that handles the following:
	 * <ul>
	 *   <li>Determining the appropriate transaction version to use</li>
	 *   <li>Reading uncommitted changes made within the current transaction</li>
	 *   <li>Retrieving the value associated with the specified key and version</li>
	 *   <li>Converting the retrieved value to the appropriate Java type</li>
	 * </ul>
	 * 
	 * <p>The method supports debug mode, which provides detailed logging of the operation
	 * through Redis logs. When debug is enabled, an alternative script with additional
	 * logging statements is used.
	 * 
	 * <p>The transaction version is obtained from the transaction manager. If a transaction
	 * is active, the method will read uncommitted changes made within that transaction,
	 * providing "read uncommitted" isolation level within the transaction context.
	 *
	 * @param context The Redis operation context containing key and value information
	 * @param valueField The field to which the retrieved value will be assigned (may be null)
	 * @param returnArgumentType The expected return type for type conversion
	 * @return A collection containing the retrieved value, or an empty collection if no value was found
	 * @throws Exception If an error occurs during the Redis operation or value conversion
	 */
	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		// Get the current transaction version for the specified key
		Long transactionVersion = transactionManager.getTransactionVerion(context.getRedisKey().getValue());
		
		// Prepare arguments and their types for the Lua script
		Map<Object, RedisValueTypeEnum> argsValueTypes = new LinkedHashMap<>();
		argsValueTypes.put(OrangeRedisTransactionKeyConstants.CURRENT_VERSION, RedisValueTypeEnum.STRING);
		argsValueTypes.put(OrangeRedisTransactionKeyConstants.VERSION_PREFIX, RedisValueTypeEnum.STRING);
		argsValueTypes.put(transactionVersion == null ? ScriptConstants.NIL : transactionVersion.toString(), RedisValueTypeEnum.STRING);
		
		// Select the appropriate script based on debug mode
		String script = GET_LUA_SCRIPT;
		if(this.logger.isDebugEnabled()) {
			script = GET_LUA_SCRIPT_DEBUG;
		}
		
		// Add trace ID for debugging if available
		if(this.logger.getTraceId() == null) {
			argsValueTypes.put(ScriptConstants.NIL, RedisValueTypeEnum.STRING);
		}else{
			argsValueTypes.put(this.logger.getTraceId(), RedisValueTypeEnum.STRING);
		}
		
		// Execute the Lua script to retrieve the value
		// This will read uncommitted changes made within the current transaction when 'transactionVersion' is not null
		Object result = this.scriptOperations.execute(
			script, 
			argsValueTypes, 
			context.getValueType(), 
			valueField == null ? returnArgumentType : valueField.getGenericType(),
			OrangeCollectionUtils.asList(context.getRedisKey().getValue()), 
			OrangeRedisTransactionKeyConstants.CURRENT_VERSION,
			OrangeRedisTransactionKeyConstants.VERSION_PREFIX,
			transactionVersion == null ? ScriptConstants.NIL : transactionVersion.toString(),
			this.logger.getTraceId() == null ?  ScriptConstants.NIL : this.logger.getTraceId()
		);
		
		// Return the result as a collection
		return OrangeCollectionUtils.asList(result);
	}
}