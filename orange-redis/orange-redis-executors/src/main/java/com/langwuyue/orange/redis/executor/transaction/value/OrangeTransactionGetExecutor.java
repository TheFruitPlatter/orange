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
 * @author Liang.Zhong
 * @since 1.0.0
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
	private OrangeRedisScriptOperations scriptOperations;
	
	private OrangeRedisDefaultTransactionManager transactionManager;
	
	private OrangeRedisLogger logger;

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

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(GetValue.class);
	}

	@Override
	protected Collection doGet(OrangeRedisContext context, Field valueField, Type returnArgumentType) throws Exception {
		Long transactionVersion = transactionManager.getTransactionVerion(context.getRedisKey().getValue());
		Map<Object, RedisValueTypeEnum> argsValueTypes = new LinkedHashMap<>();
		argsValueTypes.put(OrangeRedisTransactionKeyConstants.CURRENT_VERSION, RedisValueTypeEnum.STRING);
		argsValueTypes.put(OrangeRedisTransactionKeyConstants.VERSION_PREFIX, RedisValueTypeEnum.STRING);
		argsValueTypes.put(transactionVersion == null ? ScriptConstants.NIL : transactionVersion.toString(), RedisValueTypeEnum.STRING);
		// Change script when debug is enabled.
		String script = GET_LUA_SCRIPT;
		if(this.logger.isDebugEnabled()) {
			script = GET_LUA_SCRIPT_DEBUG;
		}
		// Id for trace 
		if(this.logger.getTraceId() == null) {
			argsValueTypes.put(ScriptConstants.NIL, RedisValueTypeEnum.STRING);
		}else{
			argsValueTypes.put(this.logger.getTraceId(), RedisValueTypeEnum.STRING);
		}
		//Read uncommitted changes made within the current transaction when 'transactionVersion' is not null
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
		return OrangeCollectionUtils.asList(result);
	}
}
