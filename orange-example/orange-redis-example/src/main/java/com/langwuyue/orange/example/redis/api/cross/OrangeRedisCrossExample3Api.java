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

import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.example.redis.api.list.OrangeRedisListExample1Api;
import com.langwuyue.orange.example.redis.api.list.OrangeRedisListExample4Api;
import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.ListMoveDirectionEnum;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.ListMoveDirection;
import com.langwuyue.orange.redis.annotation.cross.Move;
import com.langwuyue.orange.redis.annotation.cross.OrangeRedisListCrossKeyClient;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisListCrossKeyClient
public interface OrangeRedisCrossExample3Api {

	@Move
	@CrossOperationKeys({OrangeRedisListExample1Api.class})
	@StoreTo(OrangeRedisListExample4Api.class)
	@ListMoveDirection(from = ListMoveDirectionEnum.LEFT,to = ListMoveDirectionEnum.LEFT)
	OrangeValueExampleEntity move();
	
	@Move
	@CrossOperationKeys({OrangeRedisListExample1Api.class})
	@StoreTo(OrangeRedisListExample4Api.class)
	@ListMoveDirection(from = ListMoveDirectionEnum.LEFT,to = ListMoveDirectionEnum.LEFT)
	OrangeValueExampleEntity moveByTimeout(@TimeoutValue Long timeoutValue, @TimeoutUnit TimeUnit unit);
	
	@Move
	@CrossOperationKeys({OrangeRedisListExample1Api.class})
	@StoreTo(OrangeRedisListExample4Api.class)
	@ListMoveDirection(from = ListMoveDirectionEnum.LEFT,to = ListMoveDirectionEnum.LEFT)
	@Timeout(value = 10,unit = TimeUnit.SECONDS)
	OrangeValueExampleEntity moveByTimeout();
}
