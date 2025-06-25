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
 * Redis value serializer supporting multiple value types (JSON, Long, Double, String).
 * Provides serialization and deserialization methods for Redis operations.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisSerializer {
	
	private StringRedisSerializer stringRedisSerializer;
	
	private ObjectMapper objectMapper;
	
	private RedisSerializer<Long> longRedisSerializer;
	
	private RedisSerializer<Double> doubleRedisSerializer;
	
	/**
	 * Constructs a new OrangeRedisSerializer with required serializers.
	 * 
	 * @param stringRedisSerializer the string serializer for Redis
	 * @param objectMapper the Jackson ObjectMapper for JSON serialization
	 */
	public OrangeRedisSerializer(StringRedisSerializer stringRedisSerializer, ObjectMapper objectMapper) {
		super();
		this.stringRedisSerializer = stringRedisSerializer;
		this.objectMapper = objectMapper;
		this.longRedisSerializer = new GenericToStringSerializer<>(Long.class);
		this.doubleRedisSerializer = new GenericToStringSerializer<>(Double.class);
	}

	/**
	 * Serializes a single value to byte array based on specified value type.
	 * 
	 * @param value the value to serialize (can be null)
	 * @param valueType the Redis value type (JSON, LONG, DOUBLE, STRING)
	 * @return serialized byte array
	 * @throws Exception if serialization fails
	 */
	public byte[] serialize(Object value, RedisValueTypeEnum valueType) throws Exception {
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
	
	/**
	 * Serializes a List of values to List of byte arrays based on specified value type.
	 * 
	 * @param values the List of values to serialize
	 * @param valueType the Redis value type (JSON, LONG, DOUBLE, STRING)
	 * @return List of serialized byte arrays
	 * @throws Exception if serialization fails
	 */
	public List<byte[]> serialize(List<Object> values, RedisValueTypeEnum valueType) throws Exception {
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
	
	/**
	 * Serializes an array of values to 2D byte array based on specified value type.
	 * 
	 * @param values the array of values to serialize
	 * @param valueType the Redis value type (JSON, LONG, DOUBLE, STRING)
	 * @return 2D byte array of serialized values
	 * @throws Exception if serialization fails
	 */
	public byte[][] serialize(Object[] values, RedisValueTypeEnum valueType) throws Exception {
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

	/**
	 * Deserializes a byte array back to object based on specified value type.
	 * 
	 * @param bytes the byte array to deserialize (can be null or empty)
	 * @param valueType the Redis value type (JSON, LONG, DOUBLE, STRING)
	 * @param returnType the expected return type
	 * @return deserialized object or null if input is null/empty
	 * @throws Exception if deserialization fails
	 */
	public Object deserialize(byte[] bytes, RedisValueTypeEnum valueType, Type returnType) throws Exception {
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
	
	/**
	 * Deserializes a Set of byte arrays back to Set of objects.
	 * 
	 * @param values the Set of byte arrays to deserialize
	 * @param valueType the Redis value type (JSON, LONG, DOUBLE, STRING)
	 * @param returnType the expected return type
	 * @return Set of deserialized objects (empty Set if input is null/empty)
	 * @throws Exception if deserialization fails
	 */
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
	
	/**
	 * Deserializes a List of byte arrays back to List of objects.
	 * 
	 * @param values the List of byte arrays to deserialize
	 * @param valueType the Redis value type (JSON, LONG, DOUBLE, STRING)
	 * @param returnType the expected return type
	 * @return List of deserialized objects (empty List if input is null/empty)
	 * @throws Exception if deserialization fails
	 */
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

	/**
	 * Deserializes a Redis hash Map (byte[] keys and values) to Java Map.
	 * 
	 * @param resultMap the Redis hash Map to deserialize
	 * @param hashKeyType the Redis value type for keys (JSON, LONG, DOUBLE, STRING)
	 * @param hashValueType the Redis value type for values (JSON, LONG, DOUBLE, STRING)
	 * @param keyType the expected key type
	 * @param valueType the expected value type
	 * @return deserialized Java Map (empty Map if input is null/empty)
	 * @throws Exception if deserialization fails
	 */
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

	/**
	 * Converts an object to its JSON string representation.
	 * 
	 * @param value the object to serialize
	 * @return JSON string representation of the object
	 * @throws JsonProcessingException if JSON processing fails
	 */
	public Object serializeToJSONString(Object value) throws JsonProcessingException {
		return objectMapper.writeValueAsString(value);
	}
}