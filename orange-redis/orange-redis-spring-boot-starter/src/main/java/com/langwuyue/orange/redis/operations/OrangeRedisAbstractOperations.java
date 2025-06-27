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

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * Abstract base class implementing common Redis operations.
 *
 * <p>This class provides basic Redis operations including key deletion, expiration setting,
 * and TTL (Time To Live) checking. It serves as the foundation for more specific Redis
 * operation implementations.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisOperations
 * @see org.springframework.data.redis.core.RedisTemplate
 */
public abstract class OrangeRedisAbstractOperations implements OrangeRedisOperations {
	
	/**
	 * The RedisTemplate instance used for executing Redis operations.
	 * <p>Configured to work with String keys and byte[] values.
	 */
	private RedisTemplate<String,byte[]> template;
	
	/**
	 * Logger instance for recording operation execution details.
	 * <p>Used for debug logging of Redis operations and their results.
	 */
	private OrangeRedisLogger logger;
	
	protected OrangeRedisAbstractOperations(RedisTemplate<String,byte[]> template,OrangeRedisLogger logger) {
		this.template = template;
		this.logger = logger;
	}

	/**
	 * Deletes the specified key from Redis.
	 *
	 * <p>This operation removes the key and its associated value from Redis.
	 * If the key does not exist, the operation will return false.
	 *
	 * @param key the key to delete
	 * @return true if the key was deleted, false if the key did not exist
	 * @throws org.springframework.dao.DataAccessException if there is any Redis error
	 * @see org.springframework.data.redis.core.RedisTemplate#delete(Object)
	 */
	public Boolean delete(String key) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis 'delete' operation executing: delete(key:{})", key);
		}
		Boolean results = template.delete(key);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis 'delete' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Sets the expiration time for the specified key.
	 *
	 * <p>This operation sets a timeout on the key. After the timeout has expired,
	 * the key will automatically be deleted. The timeout is specified in the given
	 * time unit.
	 *
	 * @param key the key to set expiration for
	 * @param expirationTime the time to live for the key
	 * @param expirationTimeUnit the unit of time for expirationTime
	 * @return true if the timeout was set, false if the key does not exist
	 * @throws org.springframework.dao.DataAccessException if there is any Redis error
	 * @throws IllegalArgumentException if expirationTime is negative
	 * @see org.springframework.data.redis.core.RedisTemplate#expire(Object, long, TimeUnit)
	 */
	public Boolean expire(String key, long expirationTime, TimeUnit expirationTimeUnit) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis 'expire' operation executing: expire(key:{},expire:{}ms)", key, TimeUnit.MILLISECONDS.convert(expirationTime, expirationTimeUnit));
		}
		Boolean results = template.expire(key, expirationTime, expirationTimeUnit);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis 'expire' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Gets the remaining time to live of the specified key.
	 *
	 * <p>This operation returns the remaining time to live of a key that has a timeout.
	 * If the key does not have a timeout or does not exist, -1 or -2 will be returned respectively.
	 *
	 * @param key the key to check
	 * @param unit the unit of time for the return value
	 * @return the remaining time to live in the specified time unit,
	 *         -1 if the key exists but has no associated expire,
	 *         -2 if the key does not exist
	 * @throws org.springframework.dao.DataAccessException if there is any Redis error
	 * @see org.springframework.data.redis.core.RedisTemplate#getExpire(Object, TimeUnit)
	 */
	@Override
	public Long getExpiration(String key, TimeUnit unit) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis 'getExpiration' operation executing: getExpiration(key:{},unit:{})", key, unit);
		}
		Long results = template.getExpire(key, unit);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis 'getExpiration' operation returned {}", results);
		}
		return results;
	}
}