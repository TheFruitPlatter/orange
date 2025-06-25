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

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;

/**
 * A singleton enum implementation of {@link RedisSerializer} for byte arrays.
 * 
 * This serializer provides a pass-through implementation for byte array serialization
 * and deserialization, meaning it returns the input byte array as-is without any
 * transformation. This is useful when the data is already in byte array format
 * and no additional serialization/deserialization is needed.
 * 
 * Being implemented as an enum singleton ensures that only one instance of this
 * serializer exists in the JVM, following the singleton pattern in an enum-based
 * implementation which is both thread-safe and serialization-safe.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
enum OrangeByteArrayRedisSerializer implements RedisSerializer<byte[]> {

	INSTANCE;

	/**
	 * Serializes the given byte array.
	 * 
	 * This is a pass-through implementation that returns the input byte array as-is
	 * without any transformation.
	 *
	 * @param bytes the byte array to serialize (may be {@literal null})
	 * @return the input byte array without any changes
	 * @throws SerializationException if serialization fails
	 */
	@Nullable
	@Override
	public byte[] serialize(@Nullable byte[] bytes) throws SerializationException {
		return bytes;
	}

	/**
	 * Deserializes the given byte array.
	 * 
	 * This is a pass-through implementation that returns the input byte array as-is
	 * without any transformation.
	 *
	 * @param bytes the byte array to deserialize (may be {@literal null})
	 * @return the input byte array without any changes
	 * @throws SerializationException if deserialization fails
	 */
	@Nullable
	@Override
	public byte[] deserialize(@Nullable byte[] bytes) throws SerializationException {
		return bytes;
	}
}