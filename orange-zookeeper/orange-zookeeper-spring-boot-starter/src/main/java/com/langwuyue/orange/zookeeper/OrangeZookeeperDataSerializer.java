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
package com.langwuyue.orange.zookeeper;

import java.lang.reflect.Type;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeZookeeperDataSerializer {
	
	private ObjectMapper objectMapper;
	
	private OrangeStringSerializer stringSerializer;
	
	public OrangeZookeeperDataSerializer(ObjectMapper objectMapper,OrangeStringSerializer stringSerializer) {
		super();
		this.objectMapper = objectMapper;
		this.stringSerializer = stringSerializer;
	}

	public byte[] serialize(Object value,DataTypeEnum dataType) throws Exception {
		if(DataTypeEnum.JSON == dataType) {
			return objectMapper.writeValueAsBytes(value);
		}
		else{
			return stringSerializer.serialize((String)value);
		}
	}

	public Object deserialize(byte[] bytes,DataTypeEnum dataType,Type returnType) throws Exception {
		if(bytes == null || bytes.length == 0) {
			return null;
		}
		if(DataTypeEnum.JSON == dataType) {
			return objectMapper.readValue(bytes, new TypeReference<Object>() {

				@Override
				public Type getType() {
					return returnType;
				}
				
			});
		}else{
			return stringSerializer.deserialize(bytes);
		}
	}
}
