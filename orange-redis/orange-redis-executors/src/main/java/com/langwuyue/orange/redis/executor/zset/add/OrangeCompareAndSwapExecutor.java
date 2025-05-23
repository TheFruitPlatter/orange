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
import com.langwuyue.orange.redis.annotation.AddMembers;
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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeCompareAndSwapExecutor extends OrangeRedisAbstractExecutor {
	
	/**
	 * Script for production
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
	 * Script for debug
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
	
	private OrangeRedisScriptOperations operations;
	
	private OrangeRedisLogger logger;

	public OrangeCompareAndSwapExecutor(OrangeRedisScriptOperations operations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator);
		this.operations = operations;
		this.logger = logger;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeCompareAndSwapContext ctx = (OrangeCompareAndSwapContext)context;
		return casExecute(ctx.getRedisKey().getValue(),ctx.getValue(),ctx.getValueType(),ctx.getOldScore(),ctx.getNullableScore());
	}
	
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

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class,Score.class,OldScore.class,CAS.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCompareAndSwapContext.class;
	}
}
