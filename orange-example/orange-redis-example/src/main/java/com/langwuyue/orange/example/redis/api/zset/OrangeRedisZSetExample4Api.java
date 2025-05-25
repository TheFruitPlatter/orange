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
package com.langwuyue.orange.example.redis.api.zset;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity.ZSetValue;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Decrement;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.GetIndexs;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Increment;
import com.langwuyue.orange.redis.annotation.KeyVariable;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.zset.GetScores;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.annotation.zset.OldScore;
import com.langwuyue.orange.redis.annotation.zset.OrangeRedisZSetClient;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.annotation.zset.WithScores;

/**
 * Redis Sorted Set (ZSet) operations interface focusing on union and set operations.
 * 
 * <p>This interface provides comprehensive operations for Redis Sorted Sets including:
 * <ul>
 *   <li>Basic operations (add, remove, get)</li>
 *   <li>Score-based operations (increment, decrement)</li>
 *   <li>Range operations (by score, by rank)</li>
 *   <li>Random member selection</li>
 *   <li>Atomic pop operations</li>
 * </ul>
 * 
 * <p>Key features:
 * <ul>
 *   <li>Automatic JSON serialization/deserialization</li>
 *   <li>Default expiration time: 1 hour</li>
 *   <li>Support for atomic operations</li>
 *   <li>Batch operations with failure handling</li>
 * </ul>
 * 
 * <p>Usage example:
 * <pre>{@code
 * @Autowired
 * private OrangeRedisZSetExample4Api zsetApi;
 * 
 * // Add member with score
 * zsetApi.add("key", new ZSetValue("member"), 1.0);
 * 
 * // Get score range
 * Set<ZSetValue> values = zsetApi.getByScoreRange("key", 0.0, 2.0);
 * }</pre>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.annotation.zset.OrangeRedisZSetClient
 * @see com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity.ZSetValue
 */
@OrangeRedisZSetClient(valueType = RedisValueTypeEnum.JSON)
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:zset:example4:${var}")
public interface OrangeRedisZSetExample4Api  {
	@SetExpiration
	default boolean setExpiration(@KeyVariable(name="var") String keyVar) {
		
		return false;
	}

	@Delete
	default boolean delete(@KeyVariable(name="var") String keyVar) {
		
		return false;
	}

	/**
	 * Gets the expiration time for the given key variable.
	 * 
	 * @param keyVar the key variable to check expiration for
	 * @return the expiration time in milliseconds, or null if no expiration is set
	 */
	@GetExpiration
	default Long getExpiration(@KeyVariable(name="var") String keyVar) {
		
		return null;
	}

	@GetExpiration
	default Long getExpiration(@KeyVariable(name="var") String keyVar,@TimeoutUnit TimeUnit unit) {
		
		return null;
	}

	@AddMembers
	default Boolean add(@KeyVariable(name="var") String keyVar,@RedisValue ZSetValue value, @Score Double score) {
		
		return null;
	}

	@AddMembers
	@ContinueOnFailure(true)
	default Map<ZSetValue, Boolean> add(@KeyVariable(name="var") String keyVar,@Multiple Map<ZSetValue, Double> members) {
		
		return null;
	}


	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	default void addIfAsent(@KeyVariable(name="var") String keyVar,@RedisValue ZSetValue value,@Score Double score) {
		
		
	}

	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	@ContinueOnFailure(true)
	default void addIfAsent(@KeyVariable(name="var") String keyVar,@Multiple Map<ZSetValue, Double> members) {
		
		
	}

	@AddMembers
	@IfAbsent(deleteInTheEnd=true)
	void acquire(@RedisValue ZSetValue value, @Score Double score);
	
	@AddMembers
	@IfAbsent(deleteInTheEnd=true)
	@ContinueOnFailure(true)
	void acquire(@Multiple Map<ZSetValue, Double> members);

	@CAS
	default Boolean compareAndSwap(@KeyVariable(name="var") String keyVar,@RedisValue ZSetValue value,@OldScore Double oldScore,@Score Double newScore) {
		
		return null;
	}

	@GetMembers
	@Random
	default ZSetValue randomGetValue(@KeyVariable(name="var") String keyVar) {
		
		return null;
	}

	@GetMembers
	@WithScores
	@Random
	default Map<ZSetValue, Double> randomGetMember(@KeyVariable(name="var") String keyVar) {
		
		return null;
	}

	@GetMembers
	@Random
	default List<ZSetValue> randomGetValues(@KeyVariable(name="var") String keyVar,@Count Integer count) {
		
		return null;
	}

	@GetMembers
	@WithScores
	@Random
	default List<Map<ZSetValue, Double>> randomGetMembers(@KeyVariable(name="var") String keyVar,@Count Integer count) {
		
		return null;
	}

	@GetMembers
	@Distinct
	@Random	
	default Set<ZSetValue> distinctRandomGetValues(@KeyVariable(name="var") String keyVar,@Count Integer count) {
		
		return null;
	}

	@GetMembers
	@WithScores
	@Distinct
	@Random
	default Map<ZSetValue, Double> distinctRandomGetMembers(@KeyVariable(name="var") String keyVar,@Count Integer count) {
		
		return null;
	}

	@GetMembers
	default Set<ZSetValue> getByScoreRange(@KeyVariable(name="var") String keyVar,@MaxScore Double maxScore,@MinScore Double minScore) {
		
		return null;
	}
	
	@GetMembers
	default Set<ZSetValue> getByScoreRange(@KeyVariable(name="var") String keyVar,@MaxScore Double maxScore,@MinScore Double minScore,@PageNo Long pageNo,@Count Long count) {
		
		return null;
	}

	@GetMembers
	default Set<ZSetValue> getByRankRange(@KeyVariable(name="var") String keyVar,@StartIndex Long startIndex,@EndIndex Long endIndex) {
		
		return null;
	}

	@GetMembers
	@WithScores
	default Map<ZSetValue, Double> getByScoreRangeWithScores(@KeyVariable(name="var") String keyVar,@MaxScore Double maxScore,@MinScore Double minScore) {
		
		return null;
	}

	@GetMembers
	@WithScores
	default Map<ZSetValue, Double> getByScoreRangeWithScores(@KeyVariable(name="var") String keyVar,@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo,@Count Long count) {
		
		return null;
	}

	@GetMembers
	@WithScores
	default Map<ZSetValue, Double> getByRankRangeWithScores(@KeyVariable(name="var") String keyVar,@StartIndex Long startIndex,@EndIndex Long endIndex) {
		
		return null;
	}

	@GetMembers
	@Reverse
	default Set<ZSetValue> reveseScoreRange(@KeyVariable(name="var") String keyVar,@MaxScore Double maxScore,@MinScore Double minScore) {
		
		return null;
	}

	@GetMembers
	@Reverse
	default Set<ZSetValue> reveseScoreRange(@KeyVariable(name="var") String keyVar,@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo,@Count Long count) {
		
		return null;
	}

	@GetMembers
	@Reverse
	default Set<ZSetValue> reveseRankRange(@KeyVariable(name="var") String keyVar,@StartIndex Long startIndex,@EndIndex Long endIndex) {
		
		return null;
	}

	@GetMembers
	@Reverse
	@WithScores
	default Map<ZSetValue, Double> reveseScoreRangeWithScores(@KeyVariable(name="var") String keyVar,@MaxScore Double maxScore,@MinScore Double minScore) {
		
		return null;
	}

	@GetMembers
	@Reverse
	@WithScores
	default Map<ZSetValue, Double> reveseScoreRangeWithScores(@KeyVariable(name="var") String keyVar,@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo,@Count Long count) {
		
		return null;
	}

	@GetMembers
	@Reverse
	@WithScores
	default Map<ZSetValue, Double> reveseRankRangeWithScores(@KeyVariable(name="var") String keyVar,@StartIndex Long startIndex,@EndIndex Long endIndex) {
		
		return null;
	}

	@PopMembers
	@MaxScore
	default Map<ZSetValue, Double> popMax(@KeyVariable(name="var") String keyVar) {
		
		return null;
	}

	@PopMembers
	@MinScore
	default Map<ZSetValue, Double> popMin(@KeyVariable(name="var") String keyVar) {
		
		return null;
	}

	@PopMembers
	@MaxScore
	default Map<ZSetValue, Double> popMax(@KeyVariable(name="var") String keyVar,@Count Integer count) {
		
		return null;
	}

	@PopMembers
	@MinScore
	default Map<ZSetValue, Double> popMin(@KeyVariable(name="var") String keyVar,@Count Integer count) {
		
		return null;
	}

	@PopMembers
	@MaxScore
	default Map<ZSetValue, Double> timeLimitedPopMax(@KeyVariable(name="var") String keyVar,@TimeoutValue Long value,@TimeoutUnit TimeUnit unit) {
		
		return null;
	}

	@PopMembers
	@MinScore
	default Map<ZSetValue, Double> timeLimitedPopMin(@KeyVariable(name="var") String keyVar,@TimeoutValue Long value,@TimeoutUnit TimeUnit unit) {
		
		return null;
	}

	@GetIndexs
	default Long getRank(@KeyVariable(name="var") String keyVar,@RedisValue ZSetValue value) {
		
		return null;
	}

	@GetIndexs
	@ContinueOnFailure(true)
	default Map<ZSetValue, Long> getRanks(@KeyVariable(name="var") String keyVar,@Multiple Set<ZSetValue> value) {
		
		return null;
	}

	@GetScores
	default Double getScore(@KeyVariable(name="var") String keyVar,@RedisValue ZSetValue value) {
		
		return null;
	}

	@GetScores
	default Map<ZSetValue, Double> getScores(@KeyVariable(name="var") String keyVar,@Multiple Set<ZSetValue> value) {
		
		return null;
	}

	@GetIndexs
	@Reverse
	default Long reveseRank(@KeyVariable(name="var") String keyVar,@RedisValue ZSetValue value) {
		
		return null;
	}

	@GetIndexs
	@Reverse
	@ContinueOnFailure(true)
	default Map<ZSetValue, Long> reveseRanks(@KeyVariable(name="var") String keyVar,@Multiple Set<ZSetValue> value) {
		
		return null;
	}

	@GetSize
	default Long getSize(@KeyVariable(name="var") String keyVar) {
		
		return null;
	}

	@GetSize
	default Long getSizeByScoreRange(@KeyVariable(name="var") String keyVar,@MaxScore Double maxScore,@MinScore Double minScore) {
		
		return null;
	}

	@RemoveMembers
	default Boolean remove(@KeyVariable(name="var") String keyVar,@RedisValue ZSetValue value) {
		
		return null;
	}

	@RemoveMembers
	@ContinueOnFailure(true)
	default Map<ZSetValue, Boolean> remove(@KeyVariable(name="var") String keyVar,@Multiple Set<ZSetValue> values) {
		
		return null;
	}

	@RemoveMembers
	default Long removeByRankRange(@KeyVariable(name="var") String keyVar,@StartIndex Long startIndex,@EndIndex Long endIndex) {
		
		return null;
	}

	@RemoveMembers
	default Long removeByScoreRange(@KeyVariable(name="var") String keyVar,@MaxScore Double maxScore,@MinScore Double minScore) {
		
		return null;
	}

	@Increment
	default Double increment(@KeyVariable(name="var") String keyVar, @RedisValue ZSetValue value) {
		
		return null;
	}

	@Decrement
	default Double decrement(@KeyVariable(name="var") String keyVar,@RedisValue ZSetValue value) {
		
		return null;
	}

	@Increment
	default Double increment(@KeyVariable(name="var") String keyVar,@RedisValue ZSetValue value,@Score Double detal) {
		
		return null;
	}

	@Decrement
	default Double decrement(@KeyVariable(name="var") String keyVar,@RedisValue ZSetValue value,@Score Double detal) {
		
		return null;
	}
}