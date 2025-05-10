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
package com.langwuyue.orange.example.redis.api.cross;

import java.util.Set;

import com.langwuyue.orange.example.redis.api.set.OrangeRedisSetExample4Api;
import com.langwuyue.orange.example.redis.api.set.OrangeRedisSetExample5Api;
import com.langwuyue.orange.example.redis.api.set.OrangeRedisSetExample6Api;
import com.langwuyue.orange.example.redis.api.set.OrangeRedisSetExample7Api;
import com.langwuyue.orange.example.redis.api.set.OrangeRedisSetExample8Api;
import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.Difference;
import com.langwuyue.orange.redis.annotation.cross.Intersect;
import com.langwuyue.orange.redis.annotation.cross.Move;
import com.langwuyue.orange.redis.annotation.cross.OrangeRedisSetCrossKeyClient;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.annotation.cross.Union;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisSetCrossKeyClient
public interface OrangeRedisCrossExample2Api {

	@Difference
	@CrossOperationKeys({OrangeRedisSetExample4Api.class,OrangeRedisSetExample5Api.class})
	Set<OrangeValueExampleEntity> difference();
	
	@Difference
	@CrossOperationKeys({OrangeRedisSetExample4Api.class,OrangeRedisSetExample5Api.class})
	@StoreTo(OrangeRedisSetExample5Api.class)
	Long differenceAndStore();
	
	@Union
	@CrossOperationKeys({OrangeRedisSetExample4Api.class,OrangeRedisSetExample5Api.class})
	Set<OrangeValueExampleEntity> union();
	
	@Union
	@CrossOperationKeys({OrangeRedisSetExample4Api.class,OrangeRedisSetExample5Api.class})
	@StoreTo(OrangeRedisSetExample6Api.class)
	Long unionAndStore();
	
	@Intersect
	@CrossOperationKeys({OrangeRedisSetExample4Api.class,OrangeRedisSetExample5Api.class})
	Set<OrangeValueExampleEntity> intersect();
	
	@Intersect
	@CrossOperationKeys({OrangeRedisSetExample4Api.class,OrangeRedisSetExample5Api.class})
	@StoreTo(OrangeRedisSetExample7Api.class)
	Long intersectAndStore();
	
	@Move
	@CrossOperationKeys({OrangeRedisSetExample4Api.class})
	@StoreTo(OrangeRedisSetExample8Api.class)
	Boolean move(@RedisValue OrangeValueExampleEntity member);
}
