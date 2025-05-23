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
package com.langwuyue.orange.example.redis.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.langwuyue.orange.example.redis.entity.OrangeZSetBooleanExampleEntity3;
import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMapToBooleanExampleEntities3Converter {
	
	public List<OrangeZSetBooleanExampleEntity3> toEntities(Map<OrangeZSetExampleEntity,Boolean> result){
		List<OrangeZSetBooleanExampleEntity3> entities = new ArrayList<>();
		for(Entry<OrangeZSetExampleEntity,Boolean> entry : result.entrySet()) {
			OrangeZSetBooleanExampleEntity3 entity = new OrangeZSetBooleanExampleEntity3();
			entity.setMember(entry.getKey());
			entity.setSuccess(entry.getValue());
			entities.add(entity);
		}
		return entities;
	}

}
