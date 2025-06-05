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
package com.langwuyue.orange.redis.executor.zset.add;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.OldScore;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.script.ScriptConstants;
import com.langwuyue.orange.redis.executor.zset.context.OrangeCompareAndSwapContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Redis ZSet Compare-And-Swap (CAS) operation executor.
 * 
 * <p>Features:
 * <ul>
 *   <li>Atomic score updates using CAS operation</li>
 *   <li>Support for null score values (member removal)</li>
 *   <li>Debug mode with detailed operation logging</li>
 *   <li>Trace ID support for operation tracking</li>
 * </ul>
 *
 * <p>The executor uses Lua scripts to ensure atomicity of the CAS operation.
 * It supports both production and debug modes, with the latter providing
 * detailed logging of the operation steps.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeCompareAndSwapExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Lua script for atomic Compare-And-Swap operation in Redis ZSet.
	 * 
	 * <p>Script logic:
	 * <ol>
	 *   <li>Extract key, member, expected score and new score from arguments</li>
	 *   <li>Get current score for the member</li>
	 *   <li>Compare current score with expected score (handles nil case)</li>
	 *   <li>If match, update score or remove member (if new score is nil)</li>
	 *   <li>Return '1' for success, '0' for failure</li>
	 * </ol>
	 * 
	 * <p>This script ensures atomicity of the CAS operation in production environments.
	 */
	private static final String CAS_LUA_SCRIPT = String.join("\n",
	    "local key = KEYS[1];",
	    "local member = ARGV[1];",
	    "local expectedScore = ARGV[2];",
	    "local newScore = ARGV[3];",
	    "local currentScore = redis.call('ZSCORE', key, member);",
	    "if ((currentScore == nil or currentScore == false) and expectedScore == '[[NIL]]') or ",
	    "   (currentScore ~= nil and expectedScore ~= nil and tonumber(currentScore) == tonumber(expectedScore)) ",
	    "then",
	    "    if newScore == '[[NIL]]' then",
	    "        redis.call('ZREM', key, member);",
	    "    else",
	    "        redis.call('ZADD', key, newScore, member);",
	    "    end;",
	    "    return '1';",
	    "else",
	    "    return '0';",
	    "end;"
	);
	
	/**
	 * Debug version of the CAS Lua script with detailed logging.
	 * 
	 * <p>Extends the production script by adding comprehensive logging:
	 * <ul>
	 *   <li>Trace ID for operation tracking</li>
	 *   <li>Key and member information</li>
	 *   <li>Expected, current, and new score values</li>
	 *   <li>Operation steps and decisions</li>
	 * </ul>
	 * 
	 * <p>This script should only be used in development/testing environments
	 * as it generates additional log entries that may impact performance.
	 * The debug mode is automatically enabled when logger's debug level is set.
	 */
	private static final String CAS_LUA_SCRIPT_DEBUG = String.join("\n",
		"local traceId = tostring(ARGV[4]);",
		"redis.log(redis.LOG_NOTICE, string.format('traceId: %s, ZSET OrangeCompareAndSwapExecutor executing', traceId));",
	    "local key = KEYS[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, key: %s', traceId, tostring(key)));",
	    "local member = ARGV[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, member: %s', traceId, tostring(member)));",
	    "local expectedScore = ARGV[2];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, expectedScore: %s', traceId, tostring(expectedScore)));",
	    "local newScore = ARGV[3];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, newScore: %s', traceId, tostring(newScore)));",
	    "local currentScore = redis.call('ZSCORE', key, member);",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, currentScore: %s', traceId, tostring(currentScore)));",
	    "if ((currentScore == nil or currentScore == false) and expectedScore == '[[NIL]]') or ",
	    "   (currentScore ~= nil and expectedScore ~= nil and tonumber(currentScore) == tonumber(expectedScore)) ",
	    "then",
	    "    if newScore == '[[NIL]]' then",
	    "        redis.log(redis.LOG_NOTICE, string.format('traceId: %s, newScore is nil and member is removing.', traceId));",
	    "        redis.call('ZREM', key, member);",
	    "    else",
	    "        redis.log(redis.LOG_NOTICE, string.format('traceId: %s, add member now.', traceId));",
	    "        redis.call('ZADD', key, tonumber(newScore), member);",
	    "    end;",
	    "    return '1';",
	    "else",
	    "    return '0';",
	    "end;"
	);
	
	/**
	 * Redis script operations for executing Lua scripts against Redis.
	 * Provides functionality to execute scripts with proper argument handling.
	 */
	private OrangeRedisScriptOperations operations;
	
	/**
	 * Logger for recording operation details and debug information.
	 * Controls whether debug mode is enabled for script execution.
	 */
	private OrangeRedisLogger logger;

	/**
	 * Constructs a new CAS executor for Redis ZSet operations.
	 *
	 * @param operations The Redis script operations for executing Lua scripts
	 * @param idGenerator Generator for creating unique executor IDs
	 * @param logger Logger for recording operation details and debug information
	 */
	public OrangeCompareAndSwapExecutor(OrangeRedisScriptOperations operations, OrangeRedisExecutorIdGenerator idGenerator, OrangeRedisLogger logger) {
		super(idGenerator);
		this.operations = operations;
		this.logger = logger;
	}

	/**
	 * Executes the CAS operation using the provided Redis context.
	 *
	 * @param context The Redis operation context containing key, value, and score information
	 * @return Boolean indicating whether the CAS operation succeeded
	 * @throws Exception if the operation fails
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeCompareAndSwapContext ctx = (OrangeCompareAndSwapContext)context;
		return casExecute(ctx.getRedisKey().getValue(),ctx.getValue(),ctx.getValueType(),ctx.getOldScore(),ctx.getNullableScore());
	}
	
	/**
	 * Performs the atomic Compare-And-Swap operation on a Redis ZSet.
	 *
	 * @param key The Redis key for the ZSet
	 * @param value The member value to update
	 * @param valueType The type of the member value
	 * @param oldScore The expected current score (null means member should not exist)
	 * @param score The new score to set (null means remove the member)
	 * @return true if the operation succeeded, false if the current score didn't match the expected score
	 * @throws Exception if the operation fails
	 */
	protected boolean casExecute(String key,Object value,RedisValueTypeEnum valueType,Double oldScore,Double score) throws Exception {
		Map<Object, RedisValueTypeEnum> argsValueTypes = new LinkedHashMap<>();
		if(oldScore == null) {
			argsValueTypes.put(ScriptConstants.NIL, RedisValueTypeEnum.STRING);
		}else {
			argsValueTypes.put(oldScore, RedisValueTypeEnum.DOUBLE);
		}
		if(score == null) {
			argsValueTypes.put(ScriptConstants.NIL, RedisValueTypeEnum.STRING);
		}else {
			argsValueTypes.put(score, RedisValueTypeEnum.DOUBLE);
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
		argsValueTypes.put(value, valueType);
		Object result = this.operations.execute(
			script, 
			argsValueTypes, 
			RedisValueTypeEnum.STRING, 
			String.class, 
			OrangeCollectionUtils.asList(key), 
			value,
			oldScore == null ? ScriptConstants.NIL : oldScore,
			score == null ? ScriptConstants.NIL : score,
			this.logger.getTraceId() == null ?  ScriptConstants.NIL : this.logger.getTraceId()
		);
		return "1".equals(result);
	}

	/**
	 * Returns the list of annotations supported by this executor.
	 * 
	 * @return List of supported annotation classes including:
	 *         <ul>
	 *           <li>{@link RedisValue} - Marks the member value parameter</li>
	 *           <li>{@link Score} - Marks the new score parameter</li>
	 *           <li>{@link OldScore} - Marks the expected score parameter</li>
	 *           <li>{@link CAS} - Indicates the method uses CAS operation</li>
	 *         </ul>
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class,Score.class,OldScore.class,CAS.class);
	}

	/**
	 * Returns the context class used by this executor.
	 * 
	 * @return {@link OrangeCompareAndSwapContext} class which contains all necessary
	 *         information for performing CAS operations on Redis ZSet, including
	 *         key, member value, expected score and new score
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCompareAndSwapContext.class;
	}
}