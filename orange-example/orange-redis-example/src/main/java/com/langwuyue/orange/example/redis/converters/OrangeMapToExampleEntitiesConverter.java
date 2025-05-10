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

import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity.ZSetValue;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMapToExampleEntitiesConverter {
	
	public List<OrangeZSetExampleEntity> toEntities(Map<ZSetValue,Double> result){
		List<OrangeZSetExampleEntity> entities = new ArrayList<>();
		for(Entry<ZSetValue,Double> entry : result.entrySet()) {
			OrangeZSetExampleEntity entity = new OrangeZSetExampleEntity();
			entity.setValue(entry.getKey());
			entity.setScore(entry.getValue());
			entities.add(entity);
		}
		return entities;
	}

	public List<OrangeZSetExampleEntity> toEntities(List<Map<ZSetValue, Double>> randomGetMembersWithScores) {
		List<OrangeZSetExampleEntity> entities = new ArrayList<>();
		for(Map<ZSetValue, Double> member : randomGetMembersWithScores) {
			for(Entry<ZSetValue,Double> entry : member.entrySet()) {
				OrangeZSetExampleEntity entity = new OrangeZSetExampleEntity();
				entity.setValue(entry.getKey());
				entity.setScore(entry.getValue());
				entities.add(entity);
			}
		}
		return entities;
	}

}
