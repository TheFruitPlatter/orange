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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.configuration.OrangeRedisSerializer;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisDefaultHashOperations extends OrangeRedisAbstractOperations implements OrangeRedisHashOperations{

	private HashOperations<String,byte[],byte[]> operations;
	
	private OrangeRedisSerializer redisSerializer;
	
	private OrangeRedisLogger logger;
	
	public OrangeRedisDefaultHashOperations(RedisTemplate<String,byte[]> template,OrangeRedisSerializer redisSerializer,OrangeRedisLogger logger) {
		super(template,logger);
		this.operations = template.opsForHash();
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}

	@Override
	public void putMember(
		String key, 
		Object hashKey, 
		Object hashValue, 
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType
	) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		byte[] hashValueBytes = redisSerializer.serialize(hashValue, hashValueType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'putMember' operation executing: putMember(key:{},hashKey:{},hashValue:{})", key, new String(hashKeyBytes), new String(hashValueBytes));
		}
		this.operations.put(key, hashKeyBytes, hashValueBytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'putMember' operation execute successfully.");
		}
	}

	@Override
	public void putMembers(
		String key, 
		Map members,
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType
	) throws Exception {
		Map<byte[], byte[]> byteMemebers = new HashMap<>();
		Set<Entry> entries = members.entrySet();
		for(Entry entry : entries) {
			Object hashKey = entry.getKey();
			Object hashValue = entry.getValue();
			byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
			byte[] hashValueBytes = redisSerializer.serialize(hashValue, hashValueType);
			byteMemebers.put(hashKeyBytes, hashValueBytes);
		}
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'putMembers' operation executing: putMembers(key:{},members:{})", key, redisSerializer.serializeToJSONString(members));
		}
		this.operations.putAll(key, byteMemebers);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'putMembers' operation execute successfully.");
		}
	}

	@Override
	public Double increment(String key, Object hashKey, double delta, RedisValueTypeEnum hashKeyType) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'increment' operation executing: increment(key:{},hashKey:{},delta:{})", key, new String(hashKeyBytes), delta);
		}
		Double results = this.operations.increment(key, hashKeyBytes, delta);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'increment' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Long increment(String key, Object hashKey, long delta, RedisValueTypeEnum hashKeyType) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'increment' operation executing: increment(key:{},hashKey:{},delta:{})", key, new String(hashKeyBytes), delta);
		}
		Long results = this.operations.increment(key, hashKeyBytes, delta);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'increment' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Set<Object> keys(String key,RedisValueTypeEnum hashKeyType, Type returnType) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'keys' operation executing: keys(key:{})", key);
		}
		Set<byte[]> keys = this.operations.keys(key);
		Set<Object> results = redisSerializer.deserialize(keys, hashKeyType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'keys' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public List<Object> multiGet(
		String key, 
		List<Object> hashKeys, 
		RedisValueTypeEnum hashKeyType, 
		RedisValueTypeEnum hashValueType, 
		Type returnType
	) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'multiGet' operation executing: multiGet(key:{},hashKeys:{})", key, redisSerializer.serializeToJSONString(hashKeys));
		}
		List<byte[]> bytes = redisSerializer.serialize(hashKeys, hashKeyType);
		List<byte[]> result = this.operations.multiGet(key, bytes);
		List<Object> results = redisSerializer.deserialize(result, hashValueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'multiGet' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public Object get(
		String key, 
		Object hashKey, 
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType,
		Type returnType
	) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'get' operation executing: get(key:{},hashKey:{})", key, new String(hashKeyBytes));
		}
		byte[] hashValueBytes = this.operations.get(key, hashKeyBytes);
		Object results = redisSerializer.deserialize(hashValueBytes, hashValueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'get' operation returned {}", new String(hashKeyBytes));
		}
		return results;
	}

	@Override
	public Map<Object, Object> entries(
		String key,
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType,
		Type keyType,
		Type valueType
	) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'entries' operation executing: entries(key:{})", key);
		}
		Map<byte[],byte[]> resultMap = this.operations.entries(key);
		Map<Object, Object> results = redisSerializer.deserialize(resultMap, hashKeyType, hashValueType, keyType, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'entries' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public Long size(String key) {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'size' operation executing: size(key:{})", key);
		}
		Long results = this.operations.size(key);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'size' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Boolean hasKey(String key, Object hashKey, RedisValueTypeEnum hashKeyType) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'hasKey' operation executing: hasKey(key:{},hashKey:{})", key, new String(hashKeyBytes));
		}
		Boolean results = this.operations.hasKey(key, hashKeyBytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'hasKey' operation returned {}", results);
		}
		return results;
	}

	@Override
	public List<Object> randomKeys(String key, long count, RedisValueTypeEnum hashValueType, Type valueType) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'randomKeys' operation executing: randomKeys(key:{},count:{})", key, count);
		}
		List<byte[]> result = this.operations.randomKeys(key, count);
		List<Object> results = redisSerializer.deserialize(result, hashValueType, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'randomKeys' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public Map<Object, Object> randomEntries(
			String key, 
			long count, 
			RedisValueTypeEnum hashKeyType,
			RedisValueTypeEnum hashValueType,
			Type keyType,
			Type valueType
	) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'randomEntries' operation executing: randomEntries(key:{},count:{})", key, count);
		}
		Map<byte[],byte[]> resultMap = this.operations.randomEntries(key, count);
		Map<Object, Object> results = redisSerializer.deserialize(resultMap, hashKeyType, hashValueType, keyType, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'randomEntries' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public Long removeMembers(String key, RedisValueTypeEnum hashKeyType, Object... hashKeys) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'removeMembers' operation executing: removeMembers(key:{},hashKeys:{})", key, redisSerializer.serializeToJSONString(hashKeys));
		}
		Object[] byteHashKeys = new Object[hashKeys.length];
		for(int i = 0; i < hashKeys.length; i++) {
			byteHashKeys[i] = redisSerializer.serialize(hashKeys[i], hashKeyType);
		}
		Long results = this.operations.delete(key, byteHashKeys);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'removeMembers' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Boolean putIfAbsent(
		String key, 
		Object hashKey, 
		Object hashValue,
		RedisValueTypeEnum hashKeyType,
		RedisValueTypeEnum hashValueType
	) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		byte[] hashValueBytes = redisSerializer.serialize(hashValue, hashValueType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'putIfAbsent' operation executing: putIfAbsent(key:{},hashKey:{},hashValue:{})", key, new String(hashKeyBytes), new String(hashValueBytes));
		}
		Boolean results = this.operations.putIfAbsent(key, hashKeyBytes, hashValueBytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'putIfAbsent' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Long getLengthByHashKey(String key, Object hashKey, RedisValueTypeEnum hashKeyType) throws Exception {
		byte[] hashKeyBytes = redisSerializer.serialize(hashKey, hashKeyType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'putMember' operation executing: putMember(key:{},hashKey:{})", key, new String(hashKeyBytes));
		}
		Long results = this.operations.lengthOfValue(key, hashKeyBytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'putMember' operation returned {}", results);
		}
		return results;
	}

	@Override
	public List<Object> getAllValues(String key, RedisValueTypeEnum valueType, Type returnType) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis hash 'getAllValues' operation executing: getAllValues(key:{})", key);
		}
		List<byte[]> values = this.operations.values(key);
		List<Object> results = this.redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis hash 'getAllValues' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}
	
}
