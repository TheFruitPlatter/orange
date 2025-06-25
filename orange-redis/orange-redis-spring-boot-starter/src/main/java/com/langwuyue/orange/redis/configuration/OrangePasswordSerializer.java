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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom JSON serializer for password fields that masks the actual password value
 * for security purposes.
 * 
 * <p>This serializer is designed to enhance security by preventing sensitive password
 * information from being exposed in logs, debug output, or any serialized JSON
 * representation. When a password field is serialized, it will always be replaced
 * with asterisks ("******") regardless of the actual password value.
 * 
 *
 * @author Liang.Zhong
 * @see JsonSerializer
 */
public class OrangePasswordSerializer extends JsonSerializer<String>{

	/**
	 * Serializes a password string by replacing it with asterisks for security.
	 * 
	 * <p>This method always writes "******" to the JSON output regardless of
	 * the actual password value, ensuring that sensitive password data is never
	 * exposed in the serialized output.
	 *
	 * @param value the password string to serialize
	 * @param gen the JSON generator
	 * @param serializers the serializer provider
	 * @throws IOException if an I/O error occurs during serialization
	 */
	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString("******");
	}
}