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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.hash.HashKey;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.hash.context.OrangeCompareAndSwapContext;
import com.langwuyue.orange.redis.executor.script.ScriptConstants;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for performing Compare-And-Swap (CAS) operations on Redis Hash fields.
 * 
 * <p>This executor provides atomic conditional updates to hash fields by comparing the current value
 * with an expected value before setting a new value. The operation only succeeds if the current value
 * matches the expected value, ensuring atomicity in concurrent environments.
 * 
 * <p>The executor uses Lua scripting to ensure the compare and set operations are performed atomically
 * within Redis. It supports both production and debug modes, with the latter providing detailed logging
 * of the operation steps.
 * 
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link CAS} - Marks a method as a compare-and-swap operation</li>
 *   <li>{@link RedisValue} - Identifies the parameter containing the new value to set</li>
 *   <li>{@link RedisOldValue} - Identifies the parameter containing the expected current value</li>
 *   <li>{@link HashKey} - Identifies the parameter containing the hash field name</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisAbstractExecutor
 * @see CAS
 * @see RedisValue
 * @see RedisOldValue
 * @see HashKey
 */
public class OrangeCompareAndSwapExecutor extends OrangeRedisAbstractExecutor {
	/**
	 * Lua script for performing Compare-And-Swap operations in production mode.
	 * 
	 * <p>This script atomically compares the current value of a hash field with an expected value,
	 * and if they match, sets the field to a new value. The script handles special cases such as:
	 * <ul>
	 *   <li>Comparing with nil values (using the special marker "[[NIL]]")</li>
	 *   <li>Setting to nil values (which results in field deletion)</li>
	 * </ul>
	 * 
	 * <p>The script returns "1" if the operation was successful (the comparison matched and the value was updated),
	 * or "0" if the operation failed (the current value did not match the expected value).
	 */
	private static final String CAS_LUA_SCRIPT = String.join("\n",
	    "local expected = ARGV[2];" ,
	    "local newValue = ARGV[3];" ,
	    "local hashKey = ARGV[1];" ,
	    "local key = KEYS[1];" ,
	    "local currentValue = redis.call('HGET', key, hashKey);" ,
	    "if ((currentValue == nil or currentValue == false) and expected == '[[NIL]]') or (currentValue ~= nil and currentValue == expected) then" ,
	    "    if newValue == '[[NIL]]' then" ,
	    "        redis.call('HDEL', key, hashKey);" ,
	    "    else" ,
	    "        redis.call('HSET', key, hashKey, newValue);" ,
	    "    end;" ,
	    "    return '1';" ,
	    "else" ,
	    "    return '0';" ,
	    "end;"
    );
	/**
	 * Lua script for performing Compare-And-Swap operations in debug mode.
	 * 
	 * <p>This script is similar to {@link #CAS_LUA_SCRIPT} but includes additional debug logging
	 * to help track the operation flow and identify potential issues. The debug script logs:
	 * <ul>
	 *   <li>Input parameters (key, field, expected value, new value)</li>
	 *   <li>Current value in Redis</li>
	 *   <li>Comparison results</li>
	 *   <li>Operation outcome</li>
	 * </ul>
	 * 
	 * <p>The script returns the same values as the production script:
	 * <ul>
	 *   <li>"1" - operation successful (comparison matched and value updated)</li>
	 *   <li>"0" - operation failed (current value did not match expected value)</li>
	 * </ul>
	 * 
	 * <p>Debug logs can be viewed in the Redis server logs when this script is used.
	 */
	private static final String CAS_LUA_SCRIPT_DEBUG = String.join("\n",
		"local traceId = tostring(ARGV[4]);",
		"redis.log(redis.LOG_NOTICE, string.format('traceId: %s, hash OrangeCompareAndSwapExecutor executing', traceId));",
	    "local expected = ARGV[2];" ,
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, expected: %s', traceId, tostring(expected)));",
	    "local newValue = ARGV[3];" ,
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, newValue: %s', traceId, tostring(newValue)));",
	    "local hashKey = ARGV[1];" ,
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, hashKey: %s', traceId, tostring(hashKey)));",
	    "local key = KEYS[1];" ,
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, key: %s', traceId, tostring(key)));",
	    "local currentValue = redis.call('HGET', key, hashKey);" ,
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, currentValue: %s', traceId, tostring(currentValue)));",
	    "if ((currentValue == nil or currentValue == false) and expected == '[[NIL]]') or (currentValue ~= nil and currentValue == expected) then" ,
	    "    if newValue == '[[NIL]]' then" ,
	    "        redis.call('HDEL', key, hashKey);" ,
	    "        redis.log(redis.LOG_NOTICE, string.format('traceId: %s, newValue is nil and old member is removed', traceId));",
	    "    else" ,
	    "        redis.call('HSET', key, hashKey, newValue);" ,
	    "        redis.log(redis.LOG_NOTICE, string.format('traceId: %s, new member is added', traceId));",
	    "    end;" ,
	    "    return '1';" ,
	    "else" ,
	    "    return '0';" ,
	    "end;"
    );
	/**
	 * Redis script operations interface used to execute Lua scripts for atomic CAS operations.
	 * This component is responsible for executing the CAS Lua scripts in both production and debug modes.
	 */
	private OrangeRedisScriptOperations scriptOperations;
	
	/**
	 * Logger component for this executor, providing debug-level logging capabilities and trace ID management.
	 * When debug is enabled, it triggers the use of the debug version of the CAS Lua script.
	 */
	private OrangeRedisLogger logger;

	/**
	 * Constructs a new OrangeCompareAndSwapExecutor with the required dependencies.
	 * 
	 * <p>This constructor initializes the executor with the necessary components for performing
	 * Compare-And-Swap operations on Redis hash fields. It requires:
	 * <ul>
	 *   <li>Script operations for executing Lua scripts</li>
	 *   <li>ID generator for creating unique executor identifiers</li>
	 *   <li>Logger for debug information and tracing</li>
	 * </ul>
	 *
	 * @param scriptOperations the Redis script operations component for executing Lua scripts
	 * @param idGenerator the executor ID generator for creating unique identifiers
	 * @param logger the Redis logger component for debug logging and trace ID management
	 */
	public OrangeCompareAndSwapExecutor(OrangeRedisScriptOperations scriptOperations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator);
		this.scriptOperations = scriptOperations;
		this.logger = logger;
	}

	/**
	 * Executes the Compare-And-Swap operation using the provided Redis context.
	 * 
	 * <p>This method casts the provided context to {@link OrangeCompareAndSwapContext} and delegates
	 * to the {@link #doCAS} method to perform the actual CAS operation. The context contains all
	 * necessary parameters including:
	 * <ul>
	 *   <li>Redis key</li>
	 *   <li>Hash key</li>
	 *   <li>Key type</li>
	 *   <li>Old value (expected value)</li>
	 *   <li>New value</li>
	 *   <li>Value type</li>
	 * </ul>
	 *
	 * @param context the Redis context containing operation parameters and metadata
	 * @return Object the result of the CAS operation (true if successful, false otherwise)
	 * @throws Exception if any error occurs during the operation
	 * @see OrangeCompareAndSwapContext
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeCompareAndSwapContext ctx = (OrangeCompareAndSwapContext) context;
		return doCAS(ctx.getRedisKey().getValue(),ctx.getHashKey(),ctx.getKeyType(),ctx.getOldValue(),ctx.getValue(),ctx.getValueType());
	}
	
	/**
	 * Performs the actual Compare-And-Swap operation on a Redis hash field.
	 * 
	 * <p>This method executes a Lua script that atomically compares the current value of a hash field
	 * with the expected value, and if they match, sets the field to a new value. The method handles:
	 * <ul>
	 *   <li>Null values for both old and new values using special markers</li>
	 *   <li>Type conversion based on the provided value types</li>
	 *   <li>Debug logging when enabled</li>
	 *   <li>Trace ID propagation for distributed tracing</li>
	 * </ul>
	 *
	 * @param key the Redis key of the hash
	 * @param hashKey the field name within the hash
	 * @param hashKeyType the type of the hash key for proper serialization
	 * @param oldValue the expected current value (null means the field should not exist)
	 * @param value the new value to set if comparison succeeds (null means delete the field)
	 * @param valueType the type of the values for proper serialization
	 * @return boolean true if the operation was successful (comparison matched and value was updated),
	 *         false if the operation failed (current value did not match expected value)
	 * @throws Exception if any error occurs during script execution or value conversion
	 */
	public boolean doCAS(String key,Object hashKey,RedisValueTypeEnum hashKeyType,Object oldValue,Object value,RedisValueTypeEnum valueType) throws Exception {
		Map<Object, RedisValueTypeEnum> argsValueTypes = new LinkedHashMap<>();
		argsValueTypes.put(hashKey, hashKeyType);
		if(value != null) {
			argsValueTypes.put(value, valueType);
		}else{
			argsValueTypes.put(ScriptConstants.NIL, RedisValueTypeEnum.STRING);	
		}
		if(oldValue != null) {
			argsValueTypes.put(oldValue, valueType);
		}else{
			argsValueTypes.put(ScriptConstants.NIL, RedisValueTypeEnum.STRING);	
		}
		// Change script when debug is enabled.
		String script = CAS_LUA_SCRIPT;
		if(this.logger.isDebugEnabled()) {
			script = CAS_LUA_SCRIPT_DEBUG;
		}
		// Id for trace 
		if(this.logger.getTraceId() == null) {
			argsValueTypes.put(ScriptConstants.NIL, RedisValueTypeEnum.STRING);
		}else{
			argsValueTypes.put(this.logger.getTraceId(), RedisValueTypeEnum.STRING);
		}
		Object result = this.scriptOperations.execute(
			script, 
			argsValueTypes, 
			RedisValueTypeEnum.STRING, 
			String.class,
			OrangeCollectionUtils.asList(key), 
			hashKey,
			oldValue == null ? ScriptConstants.NIL : oldValue,
			value == null ? ScriptConstants.NIL : value,
			this.logger.getTraceId() == null ?  ScriptConstants.NIL : this.logger.getTraceId()
		);
		return "1".equals(result);
	}

	/**
	 * Returns the list of annotation classes that this executor supports and processes.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link RedisValue} - Marks the parameter containing the new value to set</li>
	 *   <li>{@link CAS} - Marks the method as a Compare-And-Swap operation</li>
	 *   <li>{@link RedisOldValue} - Marks the parameter containing the expected current value</li>
	 *   <li>{@link HashKey} - Marks the parameter containing the hash field name</li>
	 * </ul>
	 *
	 * @return List of annotation classes that this executor can process
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class,CAS.class,RedisOldValue.class,HashKey.class);
	}
	
	/**
	 * Returns the context class used by this executor for handling Redis operations.
	 * 
	 * <p>This executor uses {@link OrangeCompareAndSwapContext} to store and manage:
	 * <ul>
	 *   <li>Redis key information</li>
	 *   <li>Hash field information</li>
	 *   <li>Old and new values for the CAS operation</li>
	 *   <li>Value type information for proper serialization</li>
	 * </ul>
	 *
	 * @return the class object for {@link OrangeCompareAndSwapContext}
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCompareAndSwapContext.class;
	}
}