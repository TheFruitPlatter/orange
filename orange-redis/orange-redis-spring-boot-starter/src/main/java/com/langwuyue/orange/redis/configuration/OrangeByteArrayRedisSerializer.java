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
 * @author Liang.Zhong
 * @since 1.0.0
 */
enum OrangeByteArrayRedisSerializer implements RedisSerializer<byte[]> {

	INSTANCE;

	@Nullable
	@Override
	public byte[] serialize(@Nullable byte[] bytes) throws SerializationException {
		return bytes;
	}

	@Nullable
	@Override
	public byte[] deserialize(@Nullable byte[] bytes) throws SerializationException {
		return bytes;
	}
}
