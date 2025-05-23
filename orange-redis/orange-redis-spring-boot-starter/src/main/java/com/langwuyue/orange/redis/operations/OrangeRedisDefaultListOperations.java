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
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisListCommands.Direction;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.langwuyue.orange.redis.ListMoveDirectionEnum;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.cross.ListMoveDirection;
import com.langwuyue.orange.redis.configuration.OrangeRedisSerializer;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisDefaultListOperations extends OrangeRedisAbstractOperations implements OrangeRedisListOperations {

	private ListOperations<String, byte[]> operations;
	
	private OrangeRedisSerializer redisSerializer;
	
	private OrangeRedisLogger logger;
	
	public OrangeRedisDefaultListOperations(RedisTemplate<String,byte[]> template,OrangeRedisSerializer redisSerializer,OrangeRedisLogger logger) {
		super(template,logger);
		this.operations = template.opsForList();
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}
	
	@Override
	public Object index(String key, long index, RedisValueTypeEnum valueType, Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation executing: index(key:{},index:{})",key,index);
		}
		byte[] bytes = this.operations.index(key, index);
		Object result = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation returned", new String(bytes));
		}
		return result;
	}

	@Override
	public Long indexOf(String key, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'indexOf' operation executing: indexOf(key:{},member:{})",key,new String(bytes));
		}
		Long results = this.operations.indexOf(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'indexOf' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Long lastIndexOf(String key, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'lastIndexOf' operation executing: lastIndexOf(key:{},index:{})",key,new String(bytes));
		}
		Long results = this.operations.lastIndexOf(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'lastIndexOf' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Long size(String key) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'size' operation executing: size(key:{})",key);
		}
		Long results = this.operations.size(key);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'size' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Long remove(String key, long count, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation executing: index(key:{},count:{},member:{})",key,count,new String(bytes));
		}
		Long results = this.operations.remove(key, count, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Long leftPush(String key, RedisValueTypeEnum valueType, Object... values) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis leftPush 'index' operation executing: leftPush(key:{},members:{})",key,redisSerializer.serializeToJSONString(values));
		}
		byte[][] bytes = redisSerializer.serialize(values, valueType);
		Long results = this.operations.leftPushAll(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Long leftPush(String key, Object pivot, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] pivotBytes = redisSerializer.serialize(pivot, valueType);
		byte[] valueBytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis list 'leftPush' operation executing: leftPush(key:{},pivot:{},member:{})",
				key,
				new String(pivotBytes),
				new String(valueBytes)
			);
		}
		Long results = this.operations.leftPush(key, pivotBytes, valueBytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'leftPush' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Object leftPop(
		String key, 
		long timeout, 
		TimeUnit timeUnit, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'leftPop' operation executing: leftPop(key:{},timeout:{}ms)",
				key,
				TimeUnit.MILLISECONDS.convert(timeout,timeUnit)
			);
		}
		byte[] bytes = this.operations.leftPop(key,timeout,timeUnit);
		Object results = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'leftPop' operation returned {}", new String(bytes));
		}
		return results;
	}

	@Override
	public Long rightPush(String key, RedisValueTypeEnum valueType, Object... values) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis list 'rightPush' operation executing: rightPush(key:{},members:{})",
				key,
				redisSerializer.serializeToJSONString(values)
			);
		}
		byte[][] bytes = redisSerializer.serialize(values, valueType);
		Long results = this.operations.rightPushAll(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'rightPush' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Long rightPush(String key, Object pivot, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] pivotBytes = redisSerializer.serialize(pivot, valueType);
		byte[] valueBytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis list 'rightPush' operation executing: rightPush(key:{},pivot:{},member:{})",
				key,
				new String(pivotBytes),
				new String(valueBytes)
			);
		}
		Long results = this.operations.rightPush(key, pivotBytes, valueBytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'rightPush' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Object rightPop(
		String key, 
		long timeout, 
		TimeUnit timeUnit, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis list 'rightPop' operation executing: rightPop(key:{},timeout:{}ms)",
				key,
				TimeUnit.MILLISECONDS.convert(timeout, timeUnit)
			);
		}
		byte[] bytes = this.operations.rightPop(key,timeout,timeUnit);
		Object results = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'rightPop' operation returned {}", new String(bytes));
		}
		return results;
	}

	@Override
	public List<Object> range(
		String key, 
		Long startIndex, 
		Long endIndex, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'range' operation executing: range(key:{},startIndex:{},endIndex:{})",key,startIndex,endIndex);
		}
		List<byte[]> result = this.operations.range(key, startIndex, endIndex);
		List<Object> results = redisSerializer.deserialize(result, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'range' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public List<Object> leftPop(
		String key, 
		long count, 
		RedisValueTypeEnum valueType, 
		Type returnType
	)throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'leftPop' operation executing: leftPop(key:{},count:{})",key,count);
		}
		List<byte[]> bytes = this.operations.leftPop(key,count);
		List<Object> results = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'leftPop' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public List<Object> rightPop(
		String key, 
		long count, 
		RedisValueTypeEnum valueType, 
		Type returnType
	)throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'rightPop' operation executing: rightPop(key:{},count:{})",key,count);
		}
		List<byte[]> bytes = this.operations.rightPop(key,count);
		List<Object> results = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'rightPop' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public void set(String key, long index, Object value, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'set' operation executing: set(key:{},index:{},member:{})",key,index,new String(bytes));
		}
		this.operations.set(key, index, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'set' operation execute successfully");
		}
	}

	@Override
	public void trim(String key, Long startIndex, Long endIndex) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation executing: index(key:{},startIndex:{},endIndex:{})",key,startIndex,endIndex);
		}
		this.operations.trim(key, startIndex, endIndex);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'index' operation execute successfully");
		}
	}

	@Override
	public Object move(String referenceKey, String destKey, ListMoveDirection direction, RedisValueTypeEnum valueType, Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'move' operation executing: move(referenceKey:{},destKey:{},direction:{})",referenceKey,destKey,direction);
		}
		Direction from = direction.from() == ListMoveDirectionEnum.LEFT ? Direction.LEFT : Direction.RIGHT;
		Direction to = direction.to() == ListMoveDirectionEnum.LEFT ? Direction.LEFT : Direction.RIGHT;
		byte[] bytes = this.operations.move(referenceKey, from, destKey, to);
		if(bytes == null || bytes.length == 0) {
			return null;
		}
		Object results = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'move' operation returned {}", new String(bytes));
		}
		return results;
	}

	@Override
	public Object move(
		String referenceKey, 
		String destKey, 
		ListMoveDirection direction, 
		RedisValueTypeEnum valueType,
		Type returnType, 
		long timeoutValue, 
		TimeUnit timeoutUnit
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis list 'move' operation executing: move(referenceKey:{},destKey:{},direction:{},timeout:{}ms)",
				referenceKey,
				destKey,
				direction,
				TimeUnit.MILLISECONDS.convert(timeoutValue, timeoutUnit)
			);
		}
		Direction from = direction.from() == ListMoveDirectionEnum.LEFT ? Direction.LEFT : Direction.RIGHT;
		Direction to = direction.to() == ListMoveDirectionEnum.LEFT ? Direction.LEFT : Direction.RIGHT;
		byte[] bytes = this.operations.move(referenceKey, from, destKey, to, timeoutValue, timeoutUnit);
		Object results = redisSerializer.deserialize(bytes, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis list 'move' operation returned {}", new String(bytes));
		}
		return results;
	}
}
