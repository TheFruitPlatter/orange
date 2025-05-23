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
import com.langwuyue.orange.redis.annotation.AddMembers;
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
 * @author Liang.Zhong
 * @since 1.0.0
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

	public OrangeCompareAndSwapExecutor(OrangeRedisScriptOperations scriptOperations,OrangeRedisExecutorIdGenerator idGenerator,OrangeRedisLogger logger) {
		super(idGenerator);
		this.scriptOperations = scriptOperations;
		this.logger = logger;
	}

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

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(RedisValue.class, CAS.class, Index.class, RedisOldValue.class);
	}
	
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCompareAndSwapContext.class;
	}
}
