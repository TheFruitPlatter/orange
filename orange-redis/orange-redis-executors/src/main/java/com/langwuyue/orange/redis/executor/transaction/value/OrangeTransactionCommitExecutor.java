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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.transaction.Commit;
import com.langwuyue.orange.redis.annotation.transaction.Version;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.script.ScriptConstants;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionCommitExecutor;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionKeyConstants;
import com.langwuyue.orange.redis.executor.transaction.context.OrangeTransactionVersionContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeTransactionCommitExecutor extends OrangeRedisAbstractExecutor implements OrangeRedisTransactionCommitExecutor {
	/**
	 * Script for production
	 */
	private static final String COMMIT_LUA_SCRIPT = String.join("\n",
		"local valueKeyPrefix = ARGV[3];",
	    "local newValue = ARGV[2];",
	    "local newNumber = tonumber(newValue);",
	    "local hashKey = ARGV[1];",
	    "local key = KEYS[1];",
	    "local valueKey = tostring(valueKeyPrefix) .. tostring(newValue);",
	    "local exists = redis.call('HEXISTS', key, valueKey);",
	    "if exists == 0 then",
	    "    return '0';",
	    "end",
	    "local currentValue = redis.call('HGET', key, hashKey);",
	    "if currentValue == nil or currentValue == false then",
	    "    redis.call('HSET', key, hashKey, newValue);",
	    "    return '1';",
	    "else",
	    "    if tonumber(currentValue) < newNumber then",
	    "        redis.call('HSET', key, hashKey, newValue);",
	    "        return '1';",
	    "    else",
	    "        return '0';",
	    "    end;",
	    "end;"
	);
	/**
	 * Script for production
	 */
	private static final String COMMIT_LUA_SCRIPT_DEBUG = String.join("\n",
		"local traceId = tostring(ARGV[4]);",
		"redis.log(redis.LOG_NOTICE, string.format('traceId: %s, VALUE Transaction OrangeTransactionCommitExecutor executing', traceId));",
		"local valueKeyPrefix = ARGV[3];",
		"redis.log(redis.LOG_NOTICE, string.format('traceId: %s, valueKeyPrefix: %s', traceId, tostring(valueKeyPrefix)));",
	    "local newValue = ARGV[2];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, valueKeyPrefix: %s', traceId, tostring(newValue)));",
	    "local hashKey = ARGV[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, hashKey: %s', traceId, tostring(hashKey)));",
	    "local key = KEYS[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, key: %s', traceId, tostring(key)));",
	    "local valueKey = tostring(valueKeyPrefix) .. tostring(newValue);",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, valueKey: %s', traceId, valueKey));",
	    "local exists = redis.call('HEXISTS', key, valueKey);",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, whether the new version exists: %s', traceId, tostring(exists)));",
	    "if exists == 0 then",
	    "    return '0';",
	    "end",
	    "local currentValue = redis.call('HGET', key, hashKey);",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, currentValue: %s', traceId, tostring(currentValue)));",
	    "if currentValue == nil or currentValue == false then",
	    "    redis.log(redis.LOG_NOTICE, string.format('traceId: %s, currentValue is nil', traceId));",
	    "    redis.call('HSET', key, hashKey, newValue);",
	    "    return '1';",
	    "else",
	    "    if tonumber(currentValue) < tonumber(newValue) then",
	    "        redis.log(redis.LOG_NOTICE, string.format('traceId: %s, new version committed', traceId));",
	    "        redis.call('HSET', key, hashKey, newValue);",
	    "        return '1';",
	    "    else",
	    "        return '0';",
	    "    end;",
	    "end;"
	);
	private OrangeRedisScriptOperations scriptOperations;
	
	private OrangeRedisDefaultTransactionManager transactionManager;
	
	private OrangeRedisLogger logger;

	public OrangeTransactionCommitExecutor(
		OrangeRedisScriptOperations scriptOperations,
		OrangeRedisExecutorIdGenerator idGenerator,
		OrangeRedisLogger logger
	) {
		super(idGenerator);
		this.scriptOperations = scriptOperations;
		this.logger = logger;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeTransactionVersionContext ctx = (OrangeTransactionVersionContext) context;
		return commit(ctx.getRedisKey().getValue(),ctx.getVersion());
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Commit.class,Version.class);
	}
	
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeTransactionVersionContext.class;
	}

	@Override
	public boolean commit(String key,Long version) throws Exception {
		Map<Object, RedisValueTypeEnum> argsValueTypes = new LinkedHashMap<>();
		argsValueTypes.put(OrangeRedisTransactionKeyConstants.CURRENT_VERSION, RedisValueTypeEnum.STRING);
		argsValueTypes.put(OrangeRedisTransactionKeyConstants.VERSION_PREFIX, RedisValueTypeEnum.STRING);
		argsValueTypes.put(version, RedisValueTypeEnum.LONG);
		// Change script when debug is enabled.
		String script = COMMIT_LUA_SCRIPT;
		if(this.logger.isDebugEnabled()) {
			script = COMMIT_LUA_SCRIPT_DEBUG;
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
			OrangeRedisTransactionKeyConstants.CURRENT_VERSION,
			version,
			OrangeRedisTransactionKeyConstants.VERSION_PREFIX,
			this.logger.getTraceId() == null ?  ScriptConstants.NIL : this.logger.getTraceId()
		);
		transactionManager.alreadyCommittedManually();
		return "1".equals(result);
	}

	public void setTransactionManager(OrangeRedisDefaultTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
