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
package com.langwuyue.orange.redis.executor.list;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.list.Index;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.list.context.OrangeCompareAndSwapContext;
import com.langwuyue.orange.redis.executor.script.ScriptConstants;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor implementation for Compare-And-Swap (CAS) operations on Redis lists.
 * 
 * <p>This executor provides atomic compare-and-swap operations for list elements,
 * allowing safe concurrent modifications by comparing the current value with an
 * expected value before making any changes.
 * 
 * <p>The executor supports:
 * <ul>
 *   <li>Atomic updates of list elements at specific indices</li>
 *   <li>Null value handling with special markers</li>
 *   <li>Debug mode with detailed operation logging</li>
 *   <li>Tracing support for operation monitoring</li>
 * </ul>
 *
 * <p>The implementation uses Lua scripts to ensure atomicity of the CAS operation
 * on the Redis server side.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/list">Orange Redis List Documentation</a>
 */
public class OrangeCompareAndSwapExecutor extends OrangeRedisAbstractExecutor {
	/**
	 * Script for production
	 */
	private static final String CAS_LUA_SCRIPT = String.join("\n",
	    "local newValue = ARGV[3]",
	    "local index = ARGV[1];",
	    "local expected = ARGV[2];",
	    "local key = KEYS[1];",
	    "local currentValue = redis.call('LINDEX', key, index)",
	    "if (currentValue == nil and expected == '[[NIL]]') or (currentValue ~= nil and currentValue == expected)",
	    "then",
	    "    if newValue == '[[NIL]]' then",
	    "        redis.call('LSET', key, index, '[[DELETED]]')",
	    "        redis.call('LREM', key, 1, '[[DELETED]]')",
	    "    else",
	    "       redis.call('LSET', key, index, newValue)", 
	    "    end",
	    "    return '1'",
	    "else",
	    "    return '0'",
	    "end"
	);
	
	/**
	 * Script for debug
	 */
	private static final String CAS_LUA_SCRIPT_DEBUG = String.join("\n",
		"local traceId = tostring(ARGV[4]);",
		"redis.log(redis.LOG_NOTICE, string.format('traceId: %s, list OrangeCompareAndSwapExecutor executing', traceId));",
	    "local newValue = ARGV[3]",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, newValue: %s', traceId, tostring(newValue)));",
	    "local index = ARGV[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, index: %s', traceId, tostring(index)));",
	    "local expected = ARGV[2];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, expected: %s', traceId, tostring(expected)));",
	    "local key = KEYS[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, key: %s', traceId, tostring(key)));",
	    "local currentValue = redis.call('LINDEX', key, index)",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, currentValue: %s', traceId, tostring(currentValue)));",
	    "if (currentValue == nil and expected == '[[NIL]]') or (currentValue ~= nil and currentValue == expected)",
	    "then",
	    "    if newValue == '[[NIL]]' then",
	    "        redis.log(redis.LOG_NOTICE, string.format('traceId: %s, new member is nil, deleting member now', traceId));",
	    "        redis.call('LSET', key, index, '[[DELETED]]')",
	    "        redis.call('LREM', key, 1, '[[DELETED]]')",
	    "        redis.log(redis.LOG_NOTICE, string.format('traceId: %s, member is deleted safety', traceId));",
	    "    else",
	    "       redis.call('LSET', key, index, newValue)", 
	    "       redis.log(redis.LOG_NOTICE, string.format('traceId: %s, new member swap old member successfully.', traceId));",
	    "    end",
	    "    return '1'",
	    "else",
	    "    return '0'",
	    "end"
	);
	
	private OrangeRedisScriptOperations scriptOperations;
	
	private OrangeRedisLogger logger;

	/**
	 * Constructs a new Compare-And-Swap executor for Redis list operations.
	 * 
	 *
	 * @param scriptOperations the Redis script operations implementation
	 * @param idGenerator the ID generator for creating unique operation identifiers
	 * @param logger the logger for operation tracing and debugging
	 */
	public OrangeCompareAndSwapExecutor(OrangeRedisScriptOperations scriptOperations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator);
		this.scriptOperations = scriptOperations;
		this.logger = logger;
	}

	/**
	 * Executes the Compare-And-Swap operation on a Redis list.
	 * 
	 * <p>This method performs an atomic compare-and-swap operation by:
	 * <ol>
	 *   <li>Extracting the operation context including key, index, expected and new values</li>
	 *   <li>Executing a Lua script that atomically checks and updates the list element</li>
	 *   <li>Processing the result to determine if the operation was successful</li>
	 * </ol>
	 * 
	 * <p>The operation is successful only if the current value at the specified index
	 * matches the expected value. If successful, the value is updated to the new value.
	 *
	 * @param context the Redis operation context containing operation parameters
	 * @return a Boolean indicating whether the CAS operation was successful
	 * @throws Exception if an error occurs during the execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeCompareAndSwapContext ctx = (OrangeCompareAndSwapContext) context;
		Map<Object, RedisValueTypeEnum> argsValueTypes = new LinkedHashMap<>();
		argsValueTypes.put(ctx.getValue(), context.getValueType());
		argsValueTypes.put(ctx.getOldValue(), context.getValueType());
		argsValueTypes.put(ctx.getIndex(), RedisValueTypeEnum.LONG);
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
			OrangeCollectionUtils.asList(ctx.getRedisKey().getValue()), 
			ctx.getIndex(),
			ctx.getOldValue(),
			ctx.getValue(),
			this.logger.getTraceId() == null ?  ScriptConstants.NIL : this.logger.getTraceId()
		);
		return "1".equals(result);
	}

	/**
	 * Returns the list of annotation classes supported by this executor.
	 * 
	 * <p>This executor supports the following annotations:
	 * <ul>
	 *   <li>{@link RedisValue} - Marks the parameter containing the new value to set</li>
	 *   <li>{@link CAS} - Marks the method as a Compare-And-Swap operation</li>
	 *   <li>{@link Index} - Specifies the index in the list to operate on</li>
	 *   <li>{@link RedisOldValue} - Marks the parameter containing the expected value</li>
	 * </ul>
	 *
	 * @return a list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class, CAS.class, Index.class, RedisOldValue.class);
	}
	
	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return the context class for this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCompareAndSwapContext.class;
	}
}