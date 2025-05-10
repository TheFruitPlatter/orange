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
package com.langwuyue.orange.redis.operations;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.configuration.OrangeRedisSerializer;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisDefaultValueOperations extends OrangeRedisAbstractOperations implements OrangeRedisValueOperations {

	private ValueOperations<String, byte[]> operations;
	
	private OrangeRedisSerializer redisSerializer;
	
	private OrangeRedisLogger logger;
	
	public OrangeRedisDefaultValueOperations(RedisTemplate<String,byte[]> template,OrangeRedisSerializer redisSerializer,OrangeRedisLogger logger) {
		super(template,logger);
		this.operations = template.opsForValue();
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}

	@Override
	public Object get(String key, RedisValueTypeEnum valueType, Type genericReturnType) throws Exception{
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'get' operation executing: get(key:{})", key);
		}
		byte[] value = this.operations.get(key);
		if(value == null) {
			if(logger.isDebugEnabled()) {
				logger.debug("Redis value 'get' operation returned null");
			}
			return null;
		}
		Object obj = redisSerializer.deserialize(value, valueType, genericReturnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'get' operation returned {}", new String(value));
		}
		return obj;
	}

	@Override
	public Long increment(String key, long value) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'increment' operation executing: increment(key:{},value:{})", key,value);
		}
		Long result = this.operations.increment(key,value);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'increment' operation returned {}", result);
		}
		return result;
	}

	@Override
	public Double increment(String key, double value) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'increment' operation executing: increment(key:{},value:{})", key,value);
		}
		Double result = this.operations.increment(key, value);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'increment' operation returned {}", result);
		}
		return result;
	}

	@Override
	public Long increment(String key) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'increment' operation executing: increment(key:{})", key);
		}
		Long result =  this.operations.increment(key);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'increment' operation returned {}", result);
		}
		return result;
	}

	@Override
	public void set(
		String key, 
		Object value, 
		long expirationTime, 
		TimeUnit expirationTimeUnit, 
		RedisValueTypeEnum valueType
	) throws Exception{
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis value 'set' operation executing: set(key:{},value:{},expire:{}ms)",
				key,
				new String(bytes),
				TimeUnit.MINUTES.convert(expirationTime, expirationTimeUnit)
			);
		}
		this.operations.set(key, bytes, expirationTime, expirationTimeUnit);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value set successfully");
		}
	}

	@Override
	public Boolean setIfAbsent(
		String key, 
		Object value, 
		long expirationTime, 
		TimeUnit expirationTimeUnit, 
		RedisValueTypeEnum valueType
	) throws Exception{
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis value 'setIfAbsent' operation executing: setIfAbsent(key:{},value:{},expire:{}ms)", 
				key,
				new String(bytes),
				TimeUnit.MINUTES.convert(expirationTime, expirationTimeUnit)
			);
		}
		Boolean result = this.operations.setIfAbsent(key, bytes, expirationTime, expirationTimeUnit);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'setIfAbsent' operation returned {}", result);
		}
		return result;
	}

	@Override
	public void set(String key, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'set' operation executing: set(key:{},value:{})", key,new String(bytes));
		}
		this.operations.set(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value set successfully");
		}
	}

	@Override
	public Boolean setIfAbsent(String key, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'setIfAbsent' operation executing: setIfAbsent(key:{},value:{})", key,new String(bytes));
		}
		Boolean result = this.operations.setIfAbsent(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis value 'setIfAbsent' operation returned {}", result);
		}
		return result;
	}
}
