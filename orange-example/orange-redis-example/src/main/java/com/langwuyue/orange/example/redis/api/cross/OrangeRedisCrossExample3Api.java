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
 * Example interface demonstrating cross-list operations in Redis.
 * 
 * <p>Specialized for moving elements between Redis lists with:
 * <ul>
 *   <li>Blocking and non-blocking variants</li>
 *   <li>Configurable timeouts</li>
 *   <li>Direction control (LEFT/RIGHT)</li>
 *   <li>Atomic transfer guarantees</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Source list defined by {@code @CrossOperationKeys}</li>
 *   <li>Destination list defined by {@code @StoreTo}</li>
 *   <li>Direction specified by {@code @ListMoveDirection}</li>
 *   <li>Timeout configured via {@code @Timeout} annotations</li>
 * </ul>
 * 
 * <p>Operation variants:
 * <ul>
 *   <li>Non-blocking move - Returns immediately</li>
 *   <li>Blocking move - Waits for element with timeout</li>
 *   <li>Static timeout - Configured via annotation</li>
 *   <li>Dynamic timeout - Provided via parameters</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Non-blocking move from left of source to left of destination
 * OrangeValueExampleEntity moved = api.move();
 * 
 * // Blocking move with 10 second static timeout
 * OrangeValueExampleEntity moved = api.moveByTimeout();
 * 
 * // Blocking move with dynamic timeout (5 seconds)
 * OrangeValueExampleEntity moved = api.moveByTimeout(5L, TimeUnit.SECONDS);
 * }</pre>
 * 
 * <p>Performance notes:
 * <ul>
 *   <li>Non-blocking operations are O(1)</li>
 *   <li>Blocking operations wait up to timeout duration</li>
 *   <li>Direction choices affect performance characteristics</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see CrossOperationKeys Specifies source list
 * @see StoreTo Specifies destination list
 * @see ListMoveDirection Controls pop/push directions
 * @see Timeout Configures operation timeout
 * @see OrangeRedisListExample1Api Source list example
 * @see OrangeRedisListExample4Api Destination list example
 * @see OrangeRedisCrossExample2Api Set operations comparison
 */
@OrangeRedisListCrossKeyClient
public interface OrangeRedisCrossExample3Api {

	/**
	 * Moves an element from the left of the source list to the left of the destination list.
	 * 
	 * <p>This is a non-blocking operation that performs an atomic pop from the source list
	 * and push to the destination list. If the source list is empty, returns null immediately.
	 * 
	 * <p>Operation: LMOVE source destination LEFT LEFT
	 * 
	 * @return The moved element, or null if the source list is empty
	 * @see OrangeRedisListExample1Api Source list
	 * @see OrangeRedisListExample4Api Destination list
	 */
	@Move
	@CrossOperationKeys({OrangeRedisListExample1Api.class})
	@StoreTo(OrangeRedisListExample4Api.class)
	@ListMoveDirection(from = ListMoveDirectionEnum.LEFT,to = ListMoveDirectionEnum.LEFT)
	OrangeValueExampleEntity move();
	
	/**
	 * Moves an element from the left of the source list to the left of the destination list,
	 * with a dynamic timeout.
	 * 
	 * <p>This is a blocking operation that waits for an element to be available in the source list.
	 * If the source list is empty, the method blocks until either an element becomes available
	 * or the specified timeout expires.
	 * 
	 * <p>Operation: BLMOVE source destination LEFT LEFT timeout
	 * 
	 * @param timeoutValue The maximum time to wait for an element
	 * @param unit The time unit of the timeout parameter
	 * @return The moved element, or null if the timeout expires before an element is available
	 * @see OrangeRedisListExample1Api Source list
	 * @see OrangeRedisListExample4Api Destination list
	 */
	@Move
	@CrossOperationKeys({OrangeRedisListExample1Api.class})
	@StoreTo(OrangeRedisListExample4Api.class)
	@ListMoveDirection(from = ListMoveDirectionEnum.LEFT,to = ListMoveDirectionEnum.LEFT)
	OrangeValueExampleEntity moveByTimeout(@TimeoutValue Long timeoutValue, @TimeoutUnit TimeUnit unit);
	
	/**
	 * Moves an element from the left of the source list to the left of the destination list,
	 * with a fixed 10-second timeout.
	 * 
	 * <p>This is a blocking operation that waits for an element to be available in the source list.
	 * If the source list is empty, the method blocks until either an element becomes available
	 * or the 10-second timeout expires.
	 * 
	 * <p>Operation: BLMOVE source destination LEFT LEFT 10
	 * 
	 * @return The moved element, or null if the timeout expires before an element is available
	 * @see OrangeRedisListExample1Api Source list
	 * @see OrangeRedisListExample4Api Destination list
	 */
	@Move
	@CrossOperationKeys({OrangeRedisListExample1Api.class})
	@StoreTo(OrangeRedisListExample4Api.class)
	@ListMoveDirection(from = ListMoveDirectionEnum.LEFT,to = ListMoveDirectionEnum.LEFT)
	@Timeout(value = 10,unit = TimeUnit.SECONDS)
	OrangeValueExampleEntity moveByTimeout();
}