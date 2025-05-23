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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeRedisAbstractOperations implements OrangeRedisOperations {
	
	private RedisTemplate<String,byte[]> template;
	
	private OrangeRedisLogger logger;
	
	protected OrangeRedisAbstractOperations(RedisTemplate<String,byte[]> template,OrangeRedisLogger logger) {
		this.template = template;
		this.logger = logger;
	}

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
