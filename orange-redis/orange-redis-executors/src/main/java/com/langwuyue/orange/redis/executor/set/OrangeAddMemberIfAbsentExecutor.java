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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.OrangeRedisIfAbsentException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.context.OrangeRedisValueIfAbsentContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.script.ScriptConstants;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.set.OrangeAddMemberIfAbsentFailedEvent;
import com.langwuyue.orange.redis.listener.set.OrangeAddMemberIfAbsentSuccessEvent;
import com.langwuyue.orange.redis.listener.set.OrangeRemoveMemberFailedEvent;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisSetOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentExecutor extends OrangeRedisAbstractExecutor {
	/**
	 * Script for production
	 */
	private static final String LUA_SCRIPT = String.join("\n",
	    "local key = KEYS[1];",
	    "local member = ARGV[1];",
	    "local exists = redis.call('SISMEMBER', key, member);",
	    "if exists == 0 then",
	    "    return tostring(redis.call('SADD', key, member));",
	    "else",
	    "    return '0';",
	    "end;"
	);
	
	/**
	 * Script for debug
	 */
	private static final String LUA_SCRIPT_DEBUG = String.join("\n",
		"local traceId = tostring(ARGV[2]);",
		"redis.log(redis.LOG_NOTICE, string.format('traceId: %s, SET OrangeAddMemberIfAbsentExecutor executing', traceId));",
	    "local key = KEYS[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, key: %s', traceId, tostring(key)));",
	    "local member = ARGV[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, member: %s', traceId, tostring(member)));",
	    "local exists = redis.call('SISMEMBER', key, member);",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, whether member exists: %s', traceId, tostring(exists)));",
	    "if exists == 0 then",
	    "    return tostring(redis.call('SADD', key, member));",
	    "else",
	    "    return '0';",
	    "end;"
	);

	private OrangeRedisScriptOperations scriptOperations;
	
	private OrangeRedisSetOperations operations;
	
	private Collection<OrangeRedisSetIfAbsentListener> listeners;
	
	private OrangeRedisLogger logger;
	
	public OrangeAddMemberIfAbsentExecutor(
		OrangeRedisScriptOperations scriptOperations,
		OrangeRedisSetOperations operations,
		OrangeRedisExecutorIdGenerator idGenerator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRedisLogger logger
	) {
		super(idGenerator);
		this.scriptOperations = scriptOperations;
		this.operations = operations;
		this.listeners = listeners;
		this.logger = logger;
	}

	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeRedisValueIfAbsentContext ctx = (OrangeRedisValueIfAbsentContext)context;
		Boolean success = Boolean.FALSE;
		
		try {
			success = executeIfAbsent(ctx,ctx.getValue());
			if(success == null || !success.booleanValue()) {
				throw new OrangeRedisIfAbsentException("False returned");
			}
		}catch (Exception e) {
			// The operation may have been interrupted by a client timeout or network error, but it was actually completed successfully.
			listeners.forEach(t -> 
				t.onFailure(
					context.getRedisKey().getOriginalKey(),
					new OrangeAddMemberIfAbsentFailedEvent(
						context.getArgs(),
						ctx.getValue(),
						e
					)
				)
			);
		}
		
		notifyListeners(success,ctx);
		
		return null;
	}
	
	private void notifyListeners(Boolean success,OrangeRedisValueIfAbsentContext ctx) {
		if(success == null || !success.booleanValue()) {
			return;
		}
		boolean removeFailed = false;
		Exception removeFailedException = null;
		try {
			listeners.forEach(t -> 
				t.onSuccess(
					ctx.getRedisKey().getOriginalKey(),
					new OrangeAddMemberIfAbsentSuccessEvent(ctx.getArgs(), ctx.getValue())
				)
			);
		}finally {
			try {
				if(ctx.isDeleteInTheEnd()) {
					Long removed = operations.remove(ctx.getRedisKey().getValue(), ctx.getValueType(), ctx.getValue());
					removeFailed = removed == null || removed <= 0;
				}
			}catch (Exception e) {
				// Warning, maybe a network error causes the removal to fail.
				// Also, maybe Redis Client timeout, but the operation success.
				removeFailed = true;
				removeFailedException = e;
				
			}
		}
		
		if(!removeFailed) {
			return;
		}
		
		Exception exception = removeFailedException;
		listeners.forEach(t -> 
			t.onRemoveFailed(
				ctx.getRedisKey().getOriginalKey(), 
				new OrangeRemoveMemberFailedEvent(ctx.getArgs(),ctx.getValue(),exception)
			)
		);
	}

	protected Boolean executeIfAbsent(OrangeRedisContext ctx,Object member) throws Exception {
		Map<Object, RedisValueTypeEnum> argsValueTypes = new LinkedHashMap<>();
		argsValueTypes.put(member, ctx.getValueType());
		// Change script when debug is enabled.
		String script = LUA_SCRIPT;
		if(this.logger.isDebugEnabled()) {
			script = LUA_SCRIPT_DEBUG;
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
			member,
			this.logger.getTraceId() == null ?  ScriptConstants.NIL : this.logger.getTraceId()
		);
		return "1".equals(result);
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(AddMembers.class,RedisValue.class,IfAbsent.class);
	}

	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeRedisValueIfAbsentContext.class;
	}

	OrangeRedisScriptOperations getScriptOperations() {
		return scriptOperations;
	}

	OrangeRedisSetOperations getOperations() {
		return operations;
	}
}
