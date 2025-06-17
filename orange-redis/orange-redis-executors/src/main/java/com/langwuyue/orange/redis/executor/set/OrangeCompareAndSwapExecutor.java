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
package com.langwuyue.orange.redis.executor.set;

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
 * Executor implementation for performing atomic Compare-And-Swap (CAS) operations on Redis Set members.
 * 
 * <p>This executor provides an atomic way to replace one member with another in a Redis Set,
 * ensuring that the operation only succeeds if the old member is present in the set.
 * 
 * <p>The CAS operation follows these rules:
 * <ul>
 *   <li>If the old member is null and new member is not null: adds the new member</li>
 *   <li>If the old member exists in the set and new member is not null: removes the old member and adds the new member</li>
 *   <li>If the old member exists in the set and new member is null: removes the old member</li>
 *   <li>If the old member doesn't exist in the set: the operation fails (returns false)</li>
 * </ul>
 * 
 * <p>This implementation uses Lua scripts to ensure the atomicity of the operation,
 * with separate scripts for production and debug environments.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/set">Orange Redis Set Documentation</a>
 */
public class OrangeCompareAndSwapExecutor extends OrangeRedisAbstractExecutor {
	/**
	 * Script for production
	 */
	private static final String CAS_LUA_SCRIPT = String.join("\n",
	    "local key = KEYS[1];",
	    "local oldMember = ARGV[1];",
	    "local newMember = ARGV[2];",
	    "",
	    "if oldMember == '[[NIL]]' then",
	    "    if newMember ~= '[[NIL]]' then",
	    "        return tostring(redis.call('SADD', key, newMember));",
	    "    else",
	    "        return '0';",
	    "    end;",
	    "else",
	    "    local exists = redis.call('SISMEMBER', key, oldMember);",
	    "    if exists == 1 then",
	    "        if newMember ~= '[[NIL]]' then",
	    "            local added = redis.call('SADD', key, newMember);",
	    "            if added == 1 then",
	    "                redis.call('SREM', key, oldMember);",
	    "                return '1';",
	    "            else",
	    "                return '0';",
	    "            end;",
	    "        else",
	    "            redis.call('SREM', key, oldMember);",
	    "            return '1';",
	    "        end;",
	    "    else",
	    "        return '0';",
	    "    end;",
	    "end;"
	);
	
	/**
	 * Script for debug
	 */
	private static final String CAS_LUA_SCRIPT_DEBUG = String.join("\n",
		"local traceId = tostring(ARGV[3]);",
		"redis.log(redis.LOG_NOTICE, string.format('traceId: %s, SET OrangeCompareAndSwapExecutor executing', traceId));",
	    "local key = KEYS[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, key: %s', traceId, tostring(key)));",
	    "local oldMember = ARGV[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, oldMember: %s', traceId, tostring(oldMember)));",
	    "local newMember = ARGV[2];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, newMember: %s', traceId, tostring(newMember)));",
	    "if oldMember == '[[NIL]]' then",
	    "    if newMember ~= '[[NIL]]' then",
	    "        redis.log(redis.LOG_NOTICE, string.format('traceId: %s, old member is nil and new member is adding.', traceId));",
	    "        return tostring(redis.call('SADD', key, newMember));",
	    "    else",
	    "        return '0';",
	    "    end;",
	    "else",
	    "    local exists = redis.call('SISMEMBER', key, oldMember);",
	    "    redis.log(redis.LOG_NOTICE, string.format('traceId: %s, whether old member exists: %s', traceId, tostring(exists)));",
	    "    if exists == 1 then",
	    "        if newMember ~= '[[NIL]]' then",
	    "            local added = redis.call('SADD', key, newMember);",
	    "            redis.log(redis.LOG_NOTICE, string.format('traceId: %s, new member added: %d', traceId, added));",
	    "            if added == 1 then",
	    "                redis.call('SREM', key, oldMember);",
	    "                redis.log(redis.LOG_NOTICE, string.format('traceId: %s, old member removed', traceId));",
	    "                return '1';",
	    "            else",
	    "                return '0';",
	    "            end;",
	    "        else",
	    "            redis.call('SREM', key, oldMember);",
	    "            redis.log(redis.LOG_NOTICE, string.format('traceId: %s, old member removed', traceId));",
	    "            return '1';",
	    "        end;",
	    "    else",
	    "        return '0';",
	    "    end;",
	    "end;"
	);
	
	private OrangeRedisScriptOperations operations;
	
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new OrangeCompareAndSwapExecutor with the specified dependencies.
	 * 
	 * <p>This constructor initializes the executor with the required components for
	 * executing Redis script operations, generating executor IDs, and logging.
	 *
	 * @param operations the Redis script operations implementation for executing Lua scripts
	 * @param idGenerator the generator for creating unique executor IDs
	 * @param logger the logger for recording execution information and debug messages
	 */
	public OrangeCompareAndSwapExecutor(OrangeRedisScriptOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator);
		this.operations = operations;
		this.logger = logger;
	}

	/**
	 * Executes the Compare-And-Swap operation on a Redis Set.
	 * 
	 * <p>This method performs an atomic operation that compares the old value with
	 * what's in the Redis Set, and if it matches, swaps it with the new value.
	 * The operation is implemented using a Lua script to ensure atomicity.
	 * 
	 * <p>The method handles various scenarios:
	 * <ul>
	 *   <li>If old value is null and new value exists: adds the new value</li>
	 *   <li>If old value exists in the set and new value exists: replaces old with new</li>
	 *   <li>If old value exists in the set and new value is null: removes the old value</li>
	 *   <li>If old value doesn't exist in the set: operation fails (returns false)</li>
	 * </ul>
	 *
	 * @param context the execution context containing the Redis key, old value, and new value
	 * @return boolean indicating success (true) or failure (false) of the operation
	 * @throws Exception if an error occurs during execution
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeCompareAndSwapContext ctx = (OrangeCompareAndSwapContext)context;
		Map<Object, RedisValueTypeEnum> argsValueTypes = new LinkedHashMap<>();
		Object oldValue = ctx.getNullableOldValue();
		Object newValue = ctx.getNullableValue();
		if(newValue != null) {
			argsValueTypes.put(newValue, context.getValueType());
		}else{
			argsValueTypes.put(ScriptConstants.NIL, RedisValueTypeEnum.STRING);
		}
		if(oldValue != null) {
			argsValueTypes.put(oldValue, context.getValueType());
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
	 * <p>This executor supports three annotations:
	 * <ul>
	 *   <li>{@link RedisValue} - Marks the new value to be set</li>
	 *   <li>{@link RedisOldValue} - Marks the old value to be compared against</li>
	 *   <li>{@link CAS} - Indicates that this is a Compare-And-Swap operation</li>
	 * </ul>
	 *
	 * @return a list containing the supported annotation classes
	 */
	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class, RedisOldValue.class, CAS.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * <p>This executor uses the {@link OrangeCompareAndSwapContext} class to
	 * store and manage the operation parameters and results for compare-and-swap operations.
	 * The context holds both the old value for comparison and the new value for setting.
	 *
	 * @return the {@link OrangeCompareAndSwapContext} class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCompareAndSwapContext.class;
	}
}