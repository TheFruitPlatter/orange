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
package com.langwuyue.orange.redis.executor.value;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.OrangeCompareAndSwapContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.script.ScriptConstants;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for performing atomic Compare-And-Swap (CAS) operations on Redis values.
 * 
 * <p>This executor provides an atomic way to update Redis values only if they match an expected
 * value, implementing the CAS (Compare-And-Swap) pattern. The operation is performed using
 * a Lua script to ensure atomicity at the Redis server level.</p>
 * 
 * <p>The CAS operation works as follows:</p>
 * <ol>
 *   <li>Read the current value from Redis for a given key</li>
 *   <li>Compare it with an expected value</li>
 *   <li>If they match, update the value to a new value</li>
 *   <li>If they don't match, the operation fails and returns false</li>
 * </ol>
 * 
 * <p>This executor supports handling null values for both the expected and new values:
 * <ul>
 *   <li>If the expected value is null, it will match only if the current value is also null</li>
 *   <li>If the new value is null, the key will be deleted from Redis</li>
 * </ul>
 * 
 * <p>The executor supports the following annotations:</p>
 * <ul>
 *   <li>{@link RedisValue}: Marks the parameter that contains the new value to set</li>
 *   <li>{@link RedisOldValue}: Marks the parameter that contains the expected value</li>
 *   <li>{@link CAS}: Marks a method for CAS operation</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeCompareAndSwapExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Lua script for executing the Compare-And-Swap operation in production environments.
	 */
	private static final String CAS_LUA_SCRIPT = String.join("\n",
	    "local key = KEYS[1];",
	    "local expected = ARGV[1];",
	    "local newValue = ARGV[2];",
	    "local current = redis.call('GET', key);",
	    "if ((current == nil or current == false) and expected == '[[NIL]]') or current == expected then",
	    "    if newValue == '[[NIL]]' then",
	    "        redis.call('DEL', KEYS[1]);",
	    "    else",
	    "        redis.call('SET', key, newValue);",
	    "    end;",
	    "    return '1';",
	    "else",
	    "    return '0';",
	    "end;"
	);
	
	/**
	 * Lua script for executing the Compare-And-Swap operation in debug environments.
	 * 
	 * <p>This script is similar to the production script but includes additional logging
	 * statements to help with debugging. It logs the key, expected value, current value,
	 * and new value during execution, making it easier to trace the operation flow.</p>
	 * 
	 * <p>The debug script should only be used in development or testing environments
	 * as it produces additional Redis logs that may impact performance.</p>
	 */
	private static final String CAS_LUA_SCRIPT_DEBUG = String.join("\n",
		"local traceId = tostring(ARGV[3]);",
		"redis.log(redis.LOG_NOTICE, string.format('traceId: %s, VALUE OrangeCompareAndSwapExecutor executing', traceId));",
	    "local key = KEYS[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, key: %s', traceId, tostring(key)));",
	    "local expected = ARGV[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, expected: %s', traceId, tostring(expected)));",
	    "local newValue = ARGV[2];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, newValue: %s', traceId, tostring(newValue)));",
	    "local current = redis.call('GET', key);",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, current: %s', traceId, tostring(current)));",
	    "if ((current == nil or current == false) and expected == '[[NIL]]') or current == expected then",
	    "    if newValue == '[[NIL]]' then",
	    "        redis.log(redis.LOG_NOTICE, string.format('traceId: %s, newValue is nil and value is deleting', traceId));",
	    "        redis.call('DEL', KEYS[1]);",
	    "    else",
	    "        redis.log(redis.LOG_NOTICE, string.format('traceId: %s, set value now', traceId));",
	    "        redis.call('SET', key, newValue);",
	    "    end;",
	    "    return '1';",
	    "else",
	    "    return '0';",
	    "end;"
	);
	
	/**
	 * Redis script operations instance used to execute Lua scripts.
	 * This is used to ensure atomic execution of the CAS operation.
	 */
	private OrangeRedisScriptOperations operations;
	
	/**
	 * Logger instance for recording operation details and errors.
	 * Used to provide detailed logging in both production and debug modes.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new OrangeCompareAndSwapExecutor with the required dependencies.
	 *
	 * @param operations The Redis script operations instance used to execute Lua scripts,
	 *                  ensuring atomic execution of CAS operations
	 * @param idGenerator The ID generator used to create unique identifiers for each
	 *                   executor instance, helping with tracking and debugging
	 * @param logger The logger instance used to record operation details, successes,
	 *              and failures during CAS operations
	 */
	public OrangeCompareAndSwapExecutor(OrangeRedisScriptOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator);
		this.operations = operations;
		this.logger = logger;
	}

	/**
	 * Executes the Compare-And-Swap operation on a Redis key.
	 * 
	 * <p>This method performs an atomic CAS operation by:</p>
	 * <ol>
	 *   <li>Extracting the old (expected) value and new value from the context</li>
	 *   <li>Preparing the arguments for the Lua script execution</li>
	 *   <li>Executing the appropriate Lua script (standard or debug version)</li>
	 *   <li>Interpreting the result ('1' means success, '0' means failure)</li>
	 * </ol>
	 * 
	 * <p>The method handles null values for both the old and new values using the special
	 * marker '[[NIL]]'. If the new value is null, the key will be deleted from Redis
	 * when the CAS operation succeeds.</p>
	 *
	 * @param context The OrangeRedisContext containing the operation parameters,
	 *                must be an instance of OrangeCompareAndSwapContext
	 * @return Boolean indicating whether the CAS operation succeeded (true) or failed (false)
	 * @throws Exception If an error occurs during the execution of the Redis operation
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeCompareAndSwapContext ctx = (OrangeCompareAndSwapContext)context;
		Map<Object, RedisValueTypeEnum> argsValueTypes = new LinkedHashMap<>();
		Object oldValue = ctx.getNullableOldValue();
		Object newValue = ctx.getNullableValue();
		argsValueTypes.put(ScriptConstants.NIL, RedisValueTypeEnum.STRING);
		if(newValue != null) {
			argsValueTypes.put(newValue, context.getValueType());
		}
		if(oldValue != null) {
			argsValueTypes.put(oldValue, context.getValueType());
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
		Object result = this.operations.execute(
			script, 
			argsValueTypes, 
			RedisValueTypeEnum.STRING, 
			String.class,
			OrangeCollectionUtils.asList(ctx.getRedisKey().getValue()), 
			oldValue == null ? ScriptConstants.NIL : oldValue,
			newValue == null ? ScriptConstants.NIL : newValue,
			this.logger.getTraceId() == null ?  ScriptConstants.NIL : this.logger.getTraceId()
		);
		return "1".equals(result);
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the following annotations:</p>
	 * <ul>
	 *   <li>{@link CAS}: Marks a method for CAS operation</li>
	 * </ul>
	 *
	 * @return A list containing the CAS annotation class
	 */
	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class, RedisOldValue.class, CAS.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses the {@link OrangeCompareAndSwapContext} class to store
	 * and manage the parameters required for the CAS operation, including the key,
	 * expected value, and new value.</p>
	 *
	 * @return The OrangeCompareAndSwapContext class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCompareAndSwapContext.class;
	}
}