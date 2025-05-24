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
package com.langwuyue.orange.redis.template.zset;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.zset.MaxLex;
import com.langwuyue.orange.redis.annotation.zset.MinLex;
import com.langwuyue.orange.redis.annotation.zset.OrangeRedisZSetClient;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.annotation.zset.Reverse;

/**
 * Interface template for Redis ZSet operations. 
 * Developers should extend this interface and annotate the child interface with {@code OrangeRedisKey}.
 * 
 * <p>A example is:
 * <blockquote><pre>
 *  {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:zset:example1")} 
 *  public interface OrangeRedisZSetExample1Api extends StringOperationsTemplate{
 *  
 *  }
 * </pre></blockquote>
 * 
 * 
 * <p>Please review examples for more information.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisZSetClient(valueType = RedisValueTypeEnum.STRING)
public interface StringOperationsTemplate extends JSONOperationsTemplate<String> {
	
	/**
	 * Get values in the range [{@code minLex},{@code maxLex}].
	 * 
	 * @param maxLex
	 * @param minLex
	 * @return a LinkedHashSet, the values are sorted by score in ascending order.
	 */
	@GetMembers
	Set<String> getByLexRange(@MaxLex String maxLex, @MinLex String minLex);
	
	/**
	 * Paginated results for values in the range [{@code minLex} {@code maxLex}].
	 * 
	 * @param maxLex
	 * @param minLex
	 * @param pageNo
	 * @param count
	 * @return a LinkedHashSet, the values are sorted by score in ascending order.
	 */
	@GetMembers
	Set<String> getByLexRange(@MaxLex String maxLex, @MinLex String minLex, @PageNo Long pageNo, @Count Long count);
	
	/**
	 * Get values in the range [{@code minLex},{@code maxLex}].
	 * 
	 * @param maxLex
	 * @param minLex
	 * @return a LinkedHashSet, the values are sorted by score in descending order.
	 */
	@GetMembers
	@Reverse
	Set<String> reveseByLexRange(@MaxLex String maxLex, @MinLex String minLex);
	
	/**
	 * Paginated results for values in the range [{@code minLex} {@code maxLex}].
	 * 
	 * @param maxLex
	 * @param minLex
	 * @param pageNo
	 * @param count
	 * @return a LinkedHashSet, the values are sorted by score in descending order.
	 */
	@GetMembers
	@Reverse
	Set<String> reveseByLexRange(@MaxLex String maxLex, @MinLex String minLex, @PageNo Long pageNo, @Count Long count);
	
	/**
	 * Get the size in the range [{@code minLex} {@code maxLex}].
	 * 
	 * @param maxLex
	 * @param minLex
	 * @return size
	 */
	@GetSize
	Long countByLexRange(@MaxLex String maxLex, @MinLex String minLex);
	
	/**
	 * Remove members in the range [{@code minLex} {@code maxLex}].
	 * 
	 * @param maxLex
	 * @param minLex
	 * @return The number of members removed from the sorted set.
	 */
	@RemoveMembers
	Long removeByLexRange(@MaxLex String maxLex, @MinLex String minLex);
	
	@Override
	default Map<String, Boolean> add(Map<String, Double> members) {
		return null;
	}

	@Override
	default void addIfAsent(Map<String, Double> members) {
	}

	@Override
	default String randomGetValue() {
		return null;
	}

	@Override
	default Map<String, Double> randomGetMember() {
		return null;
	}

	@Override
	default List<String> randomGetValues(Integer count) {
		return null;
	}

	@Override
	default List<Map<String, Double>> randomGetMembers(Integer count) {
		return null;
	}

	@Override
	default Set<String> distinctRandomGetValues(Integer count) {
		return null;
	}

	@Override
	default Map<String, Double> distinctRandomGetMembers(Integer count) {
		return null;
	}

	@Override
	default Set<String> getByScoreRange(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Set<String> getByScoreRange(Double maxScore, Double minScore, Long pageNo, Long count) {
		return null;
	}

	@Override
	default Set<String> getByRankRange(Long startIndex, Long endIndex) {
		return null;
	}

	@Override
	default Map<String, Double> getByScoreRangeWithScores(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Map<String, Double> getByScoreRangeWithScores(Double maxScore, Double minScore,
			Long pageNo, Long count) {
		return null;
	}

	@Override
	default Map<String, Double> getByRankRangeWithScores(Long startIndex, Long endIndex) {
		return null;
	}

	@Override
	default Set<String> reveseScoreRange(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Set<String> reveseScoreRange(Double maxScore, Double minScore, Long pageNo, Long count) {
		return null;
	}

	@Override
	default Set<String> reveseRankRange(Long startIndex, Long endIndex) {
		return null;
	}

	@Override
	default Map<String, Double> reveseScoreRangeWithScores(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Map<String, Double> reveseScoreRangeWithScores(Double maxScore, Double minScore,
			Long pageNo, Long count) {
		return null;
	}

	@Override
	default Map<String, Double> reveseRankRangeWithScores(Long startIndex, Long endIndex) {
		return null;
	}

	@Override
	default Map<String, Double> popMax() {
		return null;
	}

	@Override
	default Map<String, Double> popMin() {
		return null;
	}

	@Override
	default Map<String, Double> popMax(Integer count) {
		return null;
	}

	@Override
	default Map<String, Double> popMin(Integer count) {
		return null;
	}

	@Override
	default Map<String, Double> timeLimitedPopMax(Long value, TimeUnit unit) {
		return null;
	}

	@Override
	default Map<String, Double> timeLimitedPopMin(Long value, TimeUnit unit) {
		return null;
	}

	
	@Override
	default Map<String, Double> getScores(Set<String> value) {
		return null;
	}

	@Override
	default Map<String, Boolean> remove(Set<String> values) {
		return null;
	}

	@Override
	default Map<String, Long> getRanks(Set<String> value) {
		return null;
	}

	@Override
	default Map<String, Long> reveseRanks(Set<String> value) {
		return null;
	}
	
}
