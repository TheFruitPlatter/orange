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
import com.langwuyue.orange.redis.annotation.value.SetValue;
import com.langwuyue.orange.redis.context.OrangeCompareAndSwapContext;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.script.ScriptConstants;
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
	 * Script for debug
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

	@Override
	public List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class, RedisOldValue.class, CAS.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCompareAndSwapContext.class;
	}
}
