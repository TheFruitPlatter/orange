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
package com.langwuyue.orange.redis.configuration;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.langwuyue.orange.redis.RedisValueTypeEnum;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisSerializer {
	
	private StringRedisSerializer stringRedisSerializer;
	
	private ObjectMapper objectMapper;
	
	private RedisSerializer<Long> longRedisSerializer;
	
	private RedisSerializer<Double> doubleRedisSerializer;
	
	public OrangeRedisSerializer(StringRedisSerializer stringRedisSerializer, ObjectMapper objectMapper) {
		super();
		this.stringRedisSerializer = stringRedisSerializer;
		this.objectMapper = objectMapper;
		this.longRedisSerializer = new GenericToStringSerializer<>(Long.class);
		this.doubleRedisSerializer = new GenericToStringSerializer<>(Double.class);
	}

	public byte[] serialize(Object value,RedisValueTypeEnum valueType) throws Exception {
		if(RedisValueTypeEnum.JSON == valueType) {
			return objectMapper.writeValueAsBytes(value);
		}else if(RedisValueTypeEnum.LONG == valueType){
			return longRedisSerializer.serialize((Long)value);
		}else if(RedisValueTypeEnum.DOUBLE == valueType){
			return doubleRedisSerializer.serialize((Double)value);
		}else{
			return stringRedisSerializer.serialize((String)value);
		}
	}
	
	public List<byte[]> serialize(List<Object> values,RedisValueTypeEnum valueType) throws Exception {
		int size = values.size();
		List<byte[]> result = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			byte[] bytes = serialize(values.get(i), valueType);
			if(bytes == null) {
				continue;
			}
			result.add(i, bytes);
		}
		return result;
	}
	
	public byte[][] serialize(Object[] values,RedisValueTypeEnum valueType) throws Exception {
		int len = values.length;
		byte[][] args = new byte[len][];
		for(int i = 0; i < len; i++) {
			byte[] bytes = serialize(values[i], valueType);
			if(bytes == null) {
				continue;
			}
			args[i] = bytes;
		}
		return args;
	}

	public Object deserialize(byte[] bytes,RedisValueTypeEnum valueType, Type returnType) throws Exception {
		if(bytes == null || bytes.length == 0) {
			return null;
		}
		if(RedisValueTypeEnum.JSON == valueType) {
			return objectMapper.readValue(bytes, new TypeReference<Object>() {

				@Override
				public Type getType() {
					return returnType;
				}
				
			});
		}else if(RedisValueTypeEnum.LONG == valueType){
			return longRedisSerializer.deserialize(bytes);
		}else if(RedisValueTypeEnum.DOUBLE == valueType){
			return doubleRedisSerializer.deserialize(bytes);
		}else{
			return stringRedisSerializer.deserialize(bytes);
		}
	}
	
	public Set<Object> deserialize(
		Set<byte[]> values,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(values == null || values.isEmpty()) {
			return new LinkedHashSet<>();
		}
		Set<Object> result = new LinkedHashSet<>(values.size());
		for(byte[] bytes : values) {
			Object value = deserialize(bytes, valueType, returnType);
			if(value == null) {
				continue;
			}
			result.add(value);
		}
		return result;
	}
	
	public List<Object> deserialize(
		List<byte[]> values, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(values == null || values.isEmpty()) {
			return new ArrayList<>();
		}
		int size = values.size();
		List<Object> result = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			byte[] bytes = values.get(i);
			if(bytes == null) {
				result.add(null);
				continue;
			}
			Object value = deserialize(bytes, valueType, returnType);
			result.add(value);
		}
		return result;
	}

	public Map<Object, Object> deserialize(
			Map<byte[], byte[]> resultMap,
			RedisValueTypeEnum hashKeyType,
			RedisValueTypeEnum hashValueType, 
			Type keyType, 
			Type valueType
	) throws Exception {
		if(resultMap == null || resultMap.isEmpty()) {
			return new HashMap<>();
		}
		int size = resultMap.size();
		Map<Object, Object> map = new HashMap<>(size);
		for(Entry<byte[], byte[]> entry : resultMap.entrySet()) {
			Object key = deserialize(entry.getKey(), hashKeyType, keyType);
			if(key == null) {
				continue;
			}
			Object value = deserialize(entry.getValue(), hashValueType, valueType);
			if(value == null) {
				continue;
			}
			map.put(key, value);
		}
		return map;
	}

	public Object serializeToJSONString(Object value) throws JsonProcessingException {
		return objectMapper.writeValueAsString(value);
	}
}
