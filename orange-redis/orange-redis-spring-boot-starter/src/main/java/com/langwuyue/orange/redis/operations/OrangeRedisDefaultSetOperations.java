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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.configuration.OrangeRedisSerializer;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisDefaultSetOperations extends OrangeRedisAbstractOperations implements OrangeRedisSetOperations{

	private SetOperations<String, byte[]> operations;
	
	private OrangeRedisSerializer redisSerializer;
	
	private OrangeRedisLogger logger;
	
	public OrangeRedisDefaultSetOperations(RedisTemplate<String,byte[]> template,OrangeRedisSerializer redisSerializer,OrangeRedisLogger logger) {
		super(template,logger);
		this.operations = template.opsForSet();
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}

	@Override
	public Long add(String key, RedisValueTypeEnum valueType, Object... values) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'add' operation executing: add(key:{},values:{})", key, redisSerializer.serializeToJSONString(values));
		}
		Long results = this.operations.add(key, redisSerializer.serialize(values,valueType));
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'add' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Set<Object> members(String key,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'members' operation executing: members(key:{})", key);
		}
		Set<byte[]> values = this.operations.members(key);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'members' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}
	

	@Override
	public Long size(String key) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'size' operation executing: size(key:{})", key);
		}
		Long results = this.operations.size(key);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'size' operation returned {}", results);
		}
		return results;
	}


	@Override
	public Map<Object, Boolean> isMember(String key, RedisValueTypeEnum valueType, Object... values) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'isMember' operation executing: isMember(key:{},values:{})", key,redisSerializer.serializeToJSONString(values));
		}
		int len = values.length;
		Object[] newArray = new Object[len];
		for(int i = 0; i < len; i++) {
			newArray[i] = redisSerializer.serialize(values[i], valueType);
		}
		Map<Object,Boolean> resultMap = this.operations.isMember(key, newArray);
		if(resultMap == null) {
			return new LinkedHashMap<>();
		}
		for(int i = 0; i < len; i++) {
			Object byteObj = newArray[i];
			Boolean result = resultMap.get(byteObj);
			resultMap.remove(byteObj);
			resultMap.put(values[i], result);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'isMember' operation returned {}", redisSerializer.serializeToJSONString(resultMap));
		}
		return resultMap;
	}

	@Override
	public Set<Object> distinctRandomMembers(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'distinctRandomMembers' operation executing: distinctRandomMembers(key:{},count:{})", key,count);
		}
		Set<byte[]> values = this.operations.distinctRandomMembers(key, count);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'distinctRandomMembers' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public List<Object> randomMembers(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'randomMembers' operation executing: randomMembers(key:{},count:{})", key,count);
		}
		List<byte[]> values =  this.operations.randomMembers(key, count);
		List<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'randomMembers' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public Long remove(String key, RedisValueTypeEnum valueType, Object... values) throws Exception{
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'remove' operation executing: remove(key:{})", key,redisSerializer.serializeToJSONString(values));
		}
		Object[] newArray = new Object[values.length];
		for(int i = 0; i < values.length; i++) {
			newArray[i] = redisSerializer.serialize(values[i], valueType);
		}
		Long results = this.operations.remove(key, newArray);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'remove' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Set<Object> difference(Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'difference' operation executing: difference(comparisonKeys:{})", comparisonKeys);
		}
		Set<byte[]> values = this.operations.difference(comparisonKeys);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'difference' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public Long differenceAndStore(Collection<String> comparisonKeys, String storeTo) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'differenceAndStore' operation executing: differenceAndStore(comparisonKeys:{},storeTo:{})", comparisonKeys,storeTo);
		}
		Long results = this.operations.differenceAndStore(comparisonKeys, storeTo);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'differenceAndStore' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Long unionAndStore(Collection<String> comparisonKeys, String storeTo) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'unionAndStore' operation executing: unionAndStore(comparisonKeys:{},storeTo:{})", comparisonKeys,storeTo);
		}
		Long results = this.operations.unionAndStore(comparisonKeys, storeTo);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'unionAndStore' operation returned {}", results);
		}
		return results;
	}

	@Override
	public Set<Object> union(Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'union' operation executing: union(comparisonKeys:{})", comparisonKeys);
		}
		Set<byte[]> values = this.operations.union(comparisonKeys);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'union' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public Set<Object> intersect(Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'intersect' operation executing: intersect(comparisonKeys:{})", comparisonKeys);
		}
		Set<byte[]> values = this.operations.intersect(comparisonKeys);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'intersect' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public Long intersectAndStore(Collection<String> comparisonKeys, String storeTo) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'intersectAndStore' operation executing: intersectAndStore(comparisonKeys:{},storeTo:{})", comparisonKeys,storeTo);
		}
		Long results = this.operations.intersectAndStore(comparisonKeys, storeTo);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'intersectAndStore' operation returned {}", results);
		}
		return results;
	}

	@Override
	public List<Object> popMember(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'popMember' operation executing: popMember(key:{},count:{})", key,count);
		}
		List<byte[]> values = this.operations.pop(key, count);
		List<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'popMember' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	@Override
	public Boolean move(String key, Object value, RedisValueTypeEnum valueType, String destKey) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'move' operation executing: move(key:{},member:{},destKey:{})", key,redisSerializer.serializeToJSONString(value),destKey);
		}
		byte[] valueBytes = this.redisSerializer.serialize(value, valueType);
		Boolean results = this.operations.move(key, valueBytes, destKey);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'move' operation returned {}", results);
		}
		return results;
	}

	@Override
	public ScanResults scan(String key, String pattern, Integer count, Long pageNo, RedisValueTypeEnum valueType, Type returnType) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis set 'scan' operation executing: scan(key:{},pattern:{},count:{},pageNo:{})", key,pattern,count,pageNo);
		}
		try(Cursor<byte[]> cursor = this.operations.scan(key, ScanOptions.scanOptions().match(pattern).count(count).build())){
			Set<byte[]> results = new HashSet<>();
			long offset = count * (pageNo - 1);
			for(int i = 0, c = 0; cursor.hasNext() && c < count; i++) {
				if(i >= offset) {
					c++;
					results.add(cursor.next());
					continue;
				}
				cursor.next();
			}
			Set<Object> members = redisSerializer.deserialize(results, valueType, returnType);
			
			if(logger.isDebugEnabled()) {
				logger.debug("Redis set 'scan' operation returned {}", redisSerializer.serializeToJSONString(members));
			}
			return new ScanResults(members,pageNo+1);
		}catch (Exception e) {
			throw e;
		}
		
	}
}
