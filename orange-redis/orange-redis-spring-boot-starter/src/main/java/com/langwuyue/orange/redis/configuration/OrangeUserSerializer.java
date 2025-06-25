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
 * A custom JSON serializer for masking sensitive user information.
 * This serializer extends Jackson's JsonSerializer to provide privacy protection
 * for user-related string data by masking the middle portion of strings with asterisks.
 * 
 * <p>When serializing a string value:</p>
 * <ul>
 *   <li>If the string length is greater than 2 characters, it preserves the first and last
 *       characters while replacing the middle portion with "****"</li>
 *   <li>If the string length is 2 or fewer characters, it replaces the entire string with "****"</li>
 * </ul>
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeUserSerializer extends JsonSerializer<String>{

	/**
	 * Serializes a string value with privacy masking applied.
	 * This method implements the masking logic for sensitive user information:
	 * <ul>
	 *   <li>For strings longer than 2 characters: Preserves the first and last characters,
	 *       replacing everything in between with "****"</li>
	 *   <li>For strings with 2 or fewer characters: Replaces the entire string with "****"</li>
	 * </ul>
	 *
	 * @param value the string value to be serialized and masked
	 * @param gen the JSON generator used to write the serialized value
	 * @param serializers the serializer provider that can be used to get serializers for
	 *                    serializing objects contained within the value, if any
	 * @throws IOException if an I/O error occurs during serialization
	 *
	 */
	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value != null && value.length() > 2) {
            String masked = value.substring(0, 1) + "****" + value.substring(value.length() - 1);
            gen.writeString(masked);
        } else {
            gen.writeString("****");
        }
	}
}