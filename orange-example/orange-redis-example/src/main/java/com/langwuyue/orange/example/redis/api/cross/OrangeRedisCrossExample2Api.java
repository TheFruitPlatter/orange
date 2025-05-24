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
 * Example interface demonstrating cross-set operations between Redis regular sets.
 * 
 * <p>Provides operations to combine multiple sets in different ways:
 * <ul>
 *   <li>{@code DIFFERENCE} - Elements present in first set but not others</li>
 *   <li>{@code UNION} - All unique elements from all sets</li>
 *   <li>{@code INTERSECT} - Only elements present in all sets</li>
 *   <li>{@code MOVE} - Transfer member between sets</li>
 * </ul>
 * 
 * <p>Key differences from sorted set operations (Example1):
 * <ul>
 *   <li>Works with regular sets (no scores)</li>
 *   <li>Simpler operations without weights/aggregation</li>
 *   <li>Supports direct member movement</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Operates on sets defined by {@code @CrossOperationKeys}</li>
 *   <li>Stores results to sets defined by {@code @StoreTo}</li>
 *   <li>Uses {@code @Move} for member transfer operations</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Get difference between two sets
 * Set<OrangeValueExampleEntity> diff = api.difference();
 * 
 * // Calculate union and store result
 * Long storedCount = api.unionAndStore();
 * 
 * // Move specific member between sets
 * boolean moved = api.move(entity);
 * }</pre>
 * 
 * <p>Performance notes:
 * <ul>
 *   <li>Operations have O(N) complexity where N is total elements</li>
 *   <li>Move operation is atomic with O(1) complexity</li>
 *   <li>Storing results avoids client-server transfer for large sets</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see CrossOperationKeys Specifies which sets to operate on
 * @see StoreTo Stores operation results to another set
 * @see Move Transfers members between sets
 * @see OrangeRedisCrossExample1Api Sorted set operations
 * @see OrangeRedisSetExample4Api Example set 1
 * @see OrangeRedisSetExample5Api Example set 2
 * @see OrangeRedisSetExample6Api Union storage target
 * @see OrangeRedisSetExample7Api Intersect storage target
 * @see OrangeRedisSetExample8Api Move destination
 */
@OrangeRedisSetCrossKeyClient
public interface OrangeRedisCrossExample2Api {

	/**
	 * Calculates the difference between two sets.
	 * 
	 * <p>Returns elements that exist in the first set but not in the second set.
	 * 
	 * <p>Operation: SDIFF key1 key2
	 * 
	 * @return Set of elements that exist only in the first set
	 * @see OrangeRedisSetExample4Api First set
	 * @see OrangeRedisSetExample5Api Second set
	 */
	@Difference
	@CrossOperationKeys({OrangeRedisSetExample4Api.class,OrangeRedisSetExample5Api.class})
	Set<OrangeValueExampleEntity> difference();
	
	/**
	 * Calculates the difference between two sets and stores the result.
	 * 
	 * <p>Computes elements that exist in the first set but not in the second set,
	 * then stores the result in a destination set.
	 * 
	 * <p>Operation: SDIFFSTORE destination key1 key2
	 * 
	 * @return Number of elements in the resulting set
	 * @see OrangeRedisSetExample4Api First set
	 * @see OrangeRedisSetExample5Api Second set (and destination)
	 */
	@Difference
	@CrossOperationKeys({OrangeRedisSetExample4Api.class,OrangeRedisSetExample5Api.class})
	@StoreTo(OrangeRedisSetExample5Api.class)
	Long differenceAndStore();
	
	/**
	 * Calculates the union of two sets.
	 * 
	 * <p>Returns all unique elements that exist in either of the sets.
	 * 
	 * <p>Operation: SUNION key1 key2
	 * 
	 * @return Set of all unique elements from both sets
	 * @see OrangeRedisSetExample4Api First set
	 * @see OrangeRedisSetExample5Api Second set
	 */
	@Union
	@CrossOperationKeys({OrangeRedisSetExample4Api.class,OrangeRedisSetExample5Api.class})
	Set<OrangeValueExampleEntity> union();
	
	/**
	 * Calculates the union of two sets and stores the result.
	 * 
	 * <p>Computes all unique elements from both sets and stores them in a destination set.
	 * 
	 * <p>Operation: SUNIONSTORE destination key1 key2
	 * 
	 * @return Number of elements in the resulting set
	 * @see OrangeRedisSetExample4Api First set
	 * @see OrangeRedisSetExample5Api Second set
	 * @see OrangeRedisSetExample6Api Destination set
	 */
	@Union
	@CrossOperationKeys({OrangeRedisSetExample4Api.class,OrangeRedisSetExample5Api.class})
	@StoreTo(OrangeRedisSetExample6Api.class)
	Long unionAndStore();
	
	/**
	 * Calculates the intersection of two sets.
	 * 
	 * <p>Returns elements that exist in both sets.
	 * 
	 * <p>Operation: SINTER key1 key2
	 * 
	 * @return Set of elements that exist in both sets
	 * @see OrangeRedisSetExample4Api First set
	 * @see OrangeRedisSetExample5Api Second set
	 */
	@Intersect
	@CrossOperationKeys({OrangeRedisSetExample4Api.class,OrangeRedisSetExample5Api.class})
	Set<OrangeValueExampleEntity> intersect();
	
	/**
	 * Calculates the intersection of two sets and stores the result.
	 * 
	 * <p>Computes elements that exist in both sets and stores them in a destination set.
	 * 
	 * <p>Operation: SINTERSTORE destination key1 key2
	 * 
	 * @return Number of elements in the resulting set
	 * @see OrangeRedisSetExample4Api First set
	 * @see OrangeRedisSetExample5Api Second set
	 * @see OrangeRedisSetExample7Api Destination set
	 */
	@Intersect
	@CrossOperationKeys({OrangeRedisSetExample4Api.class,OrangeRedisSetExample5Api.class})
	@StoreTo(OrangeRedisSetExample7Api.class)
	Long intersectAndStore();
	
	/**
	 * Moves a member from one set to another.
	 * 
	 * <p>Atomically removes the member from the source set and adds it to the
	 * destination set. If the member does not exist in the source set or already
	 * exists in the destination set, the operation fails.
	 * 
	 * <p>Operation: SMOVE source destination member
	 * 
	 * @param member The member to move between sets
	 * @return true if the member was moved, false if the member did not exist in the source set
	 * @see OrangeRedisSetExample4Api Source set
	 * @see OrangeRedisSetExample8Api Destination set
	 */
	@Move
	@CrossOperationKeys({OrangeRedisSetExample4Api.class})
	@StoreTo(OrangeRedisSetExample8Api.class)
	Boolean move(@RedisValue OrangeValueExampleEntity member);
}