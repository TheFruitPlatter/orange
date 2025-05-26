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
 * Interface template for Redis ZSet operations with String values.
 * <p>
 * This interface extends {@link JSONOperationsTemplate} specialized for String values,
 * providing lexicographical range queries in addition to standard ZSet operations.
 *
 * <p>Key features:
 * <ul>
 *   <li>Lexicographical range queries (by string value)</li>
 *   <li>Standard ZSet operations inherited from parent interface</li>
 *   <li>Type safety for String values</li>
 * </ul>
 *
 * <p>Usage example:
 * <pre>{@code
 * @OrangeRedisKey(
 *     key = "user:scores",
 *     expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS)
 * )
 * public interface UserScoreApi extends StringOperationsTemplate {
 *     // Custom methods can be added here
 * }
 * }</pre>
 *
 * <p>Implementation notes:
 * <ul>
 *   <li>All operations are atomic at Redis server</li>
 *   <li>Lexicographical ranges follow Redis lex order rules</li>
 *   <li>Returned sets maintain insertion order by score</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @see JSONOperationsTemplate
 * @since 1.0.0
 */
@OrangeRedisZSetClient(valueType = RedisValueTypeEnum.STRING)
public interface StringOperationsTemplate extends JSONOperationsTemplate<String> {
	
	/**
	 * Retrieves members by lexicographical range from the sorted set.
	 * <p>
	 * Returns all string values between {@code minLex} and {@code maxLex} in Redis's
	 * lexicographical order. Both endpoints are inclusive.
	 *
	 * <p>Parameter notes:
	 * <ul>
	 *   <li>{@code @MaxLex} - defines the upper bound of the range</li>
	 *   <li>{@code @MinLex} - defines the lower bound of the range</li>
	 *   <li>Parameter order is max-first for consistency with Redis conventions</li>
	 * </ul>
	 *
	 * <p>Lexicographical ordering rules:
	 * <ul>
	 *   <li>Follows Redis's byte-by-byte comparison</li>
	 *   <li>Case-sensitive comparison</li>
	 *   <li>Numbers are ordered as strings (e.g., "10" &lt; "2")</li>
	 * </ul>
	 *
	 * <p>Special values:
	 * <ul>
	 *   <li>Use "-" for negative infinity (start from first element)</li>
	 *   <li>Use "+" for positive infinity (go to last element)</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get all members between "apple" and "orange"
	 * Set<String> fruits = getByLexRange("orange", "apple");
	 * 
	 * // Get all members from start to "mango"
	 * Set<String> earlyFruits = getByLexRange("mango", "-");
	 * }</pre>
	 *
	 * @param maxLex the upper bound string value (inclusive), or "+" for unbounded
	 * @param minLex the lower bound string value (inclusive), or "-" for unbounded
	 * @return LinkedHashSet of matching members sorted by score in ascending order
	 */
	@GetMembers
	Set<String> getByLexRange(@MaxLex String maxLex, @MinLex String minLex);
	
	/**
	 * Retrieves members by lexicographical range with pagination support.
	 * <p>
	 * Returns a subset of string values between {@code minLex} and {@code maxLex} 
	 * in Redis's lexicographical order, with pagination control.
	 *
	 * <p>Paging parameters:
	 * <ul>
	 *   <li>{@code @PageNo} - 1-based page number (must be positive)</li>
	 *   <li>{@code @Count} - number of items per page (must be positive)</li>
	 * </ul>
	 *
	 * <p>Compared to non-paged version:
	 * <ul>
	 *   <li>More efficient for large result sets</li>
	 *   <li>Consistent ordering across pages</li>
	 *   <li>Total count can be obtained via {@code countByLexRange}</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get first page of 10 items between "apple" and "orange"
	 * Set<String> page1 = getByLexRange("orange", "apple", 1L, 10L);
	 * 
	 * // Get second page
	 * Set<String> page2 = getByLexRange("orange", "apple", 2L, 10L);
	 * }</pre>
	 *
	 * @param maxLex the upper bound string value (inclusive), or "+" for unbounded
	 * @param minLex the lower bound string value (inclusive), or "-" for unbounded
	 * @param pageNo the 1-based page number (must be >= 1)
	 * @param count the number of items per page (must be >= 1)
	 * @return LinkedHashSet of matching members for the requested page, sorted by score in ascending order
	 */
	@GetMembers
	Set<String> getByLexRange(@MaxLex String maxLex, @MinLex String minLex, @PageNo Long pageNo, @Count Long count);
	
	/**
	 * Retrieves members by lexicographical range in reverse order.
	 * <p>
	 * Returns all string values between {@code minLex} and {@code maxLex} 
	 * in reverse Redis lexicographical order (ZREVRANGEBYLEX).
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Results ordered from max to min lexicographically</li>
	 *   <li>Scores ordered descending (highest first)</li>
	 *   <li>Same parameter order as forward query for consistency</li>
	 *   <li>Annotated with {@code @Reverse} to indicate reverse operation</li>
	 * </ul>
	 *
	 * <p>Typical use cases:
	 * <ul>
	 *   <li>Displaying "newest first" or "highest first" listings</li>
	 *   <li>Reverse alphabetical order presentations</li>
	 *   <li>Getting last N items without knowing max value</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get fruits from "orange" to "apple" in reverse order
	 * Set<String> reverseFruits = reverseByLexRange("orange", "apple");
	 * 
	 * // Get all members from end to "mango"
	 * Set<String> lateFruits = reverseByLexRange("+", "mango");
	 * }</pre>
	 *
	 * @param maxLex the upper bound string value (inclusive), or "+" for unbounded
	 * @param minLex the lower bound string value (inclusive), or "-" for unbounded
	 * @return LinkedHashSet of matching members sorted by score in descending order
	 */
	@GetMembers
	@Reverse
	Set<String> reverseByLexRange(@MaxLex String maxLex, @MinLex String minLex);
	
	/**
	 * Retrieves members by lexicographical range in reverse order with pagination.
	 * <p>
	 * Returns a subset of string values between {@code minLex} and {@code maxLex} 
	 * in reverse Redis lexicographical order (ZREVRANGEBYLEX), with pagination control.
	 *
	 * <p>Paging parameters:
	 * <ul>
	 *   <li>{@code @PageNo} - 1-based page number (must be positive)</li>
	 *   <li>{@code @Count} - number of items per page (must be positive)</li>
	 * </ul>
	 *
	 * <p>Key differences from forward paged query:
	 * <ul>
	 *   <li>Pages are numbered from the end of the result set</li>
	 *   <li>Each page contains items in descending lexicographical order</li>
	 *   <li>Items are sorted by score in descending order within each page</li>
	 * </ul>
	 *
	 * <p>Typical use cases:
	 * <ul>
	 *   <li>Implementing "load more" functionality for reverse-sorted lists</li>
	 *   <li>Displaying paginated "newest first" listings</li>
	 *   <li>Efficiently accessing segments of large reverse-sorted sets</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get first page of 10 items between "orange" and "apple" in reverse order
	 * Set<String> page1 = reverseByLexRange("orange", "apple", 1L, 10L);
	 * 
	 * // Get second page
	 * Set<String> page2 = reverseByLexRange("orange", "apple", 2L, 10L);
	 * }</pre>
	 *
	 * @param maxLex the upper bound string value (inclusive), or "+" for unbounded
	 * @param minLex the lower bound string value (inclusive), or "-" for unbounded
	 * @param pageNo the 1-based page number (must be >= 1)
	 * @param count the number of items per page (must be >= 1)
	 * @return LinkedHashSet of matching members for the requested page, sorted by score in descending order
	 */
	@GetMembers
	@Reverse
	Set<String> reverseByLexRange(@MaxLex String maxLex, @MinLex String minLex, @PageNo Long pageNo, @Count Long count);
	
	/**
	 * Counts members within a lexicographical range.
	 * <p>
	 * Returns the number of string values between {@code minLex} and {@code maxLex}
	 * in Redis's lexicographical order (ZLEXCOUNT).
	 *
	 * <p>Performance characteristics:
	 * <ul>
	 *   <li>O(log(N)) time complexity (N = sorted set size)</li>
	 *   <li>Much more efficient than retrieving all members just for counting</li>
	 *   <li>Does not actually retrieve the member data</li>
	 * </ul>
	 *
	 * <p>Typical use cases:
	 * <ul>
	 *   <li>Displaying total counts for filtered results</li>
	 *   <li>Implementing pagination with total page counts</li>
	 *   <li>Monitoring size of specific ranges</li>
	 * </ul>
	 *
	 * <p>Special values:
	 * <ul>
	 *   <li>Use "-" for negative infinity (start from first element)</li>
	 *   <li>Use "+" for positive infinity (go to last element)</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Count fruits between "apple" and "orange"
	 * long fruitCount = countByLexRange("orange", "apple");
	 * 
	 * // Count all members starting with "user_"
	 * long userCount = countByLexRange("user_\xff", "user_");
	 * }</pre>
	 *
	 * @param maxLex the upper bound string value (inclusive), or "+" for unbounded
	 * @param minLex the lower bound string value (inclusive), or "-" for unbounded
	 * @return the number of elements in the specified lex range
	 */
	@GetSize
	Long countByLexRange(@MaxLex String maxLex, @MinLex String minLex);
	
	/**
	 * Removes members within a lexicographical range atomically.
	 * <p>
	 * Deletes all string values between {@code minLex} and {@code maxLex}
	 * in Redis's lexicographical order (ZREMRANGEBYLEX).
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic operation - either all specified members are removed or none</li>
	 *   <li>O(log(N)+M) time complexity (N = sorted set size, M = removed members)</li>
	 *   <li>Returns count of actually removed members</li>
	 * </ul>
	 *
	 * <p>Warning:
	 * <ul>
	 *   <li>This is a destructive operation - removed data cannot be recovered</li>
	 *   <li>Use with caution in production environments</li>
	 *   <li>Consider taking backup before mass deletion</li>
	 * </ul>
	 *
	 * <p>Special values:
	 * <ul>
	 *   <li>Use "-" for negative infinity (start from first element)</li>
	 *   <li>Use "+" for positive infinity (go to last element)</li>
	 * </ul>
	 *
	 * <p>Typical use cases:
	 * <ul>
	 *   <li>Cleaning up expired or invalid data ranges</li>
	 *   <li>Bulk removal of outdated entries</li>
	 *   <li>Maintaining size limits by removing oldest entries</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Remove fruits between "apple" and "orange"
	 * long removed = removeByLexRange("orange", "apple");
	 * 
	 * // Remove all members starting with "temp_"
	 * long tempRemoved = removeByLexRange("temp_\xff", "temp_");
	 * }</pre>
	 *
	 * @param maxLex the upper bound string value (inclusive), or "+" for unbounded
	 * @param minLex the lower bound string value (inclusive), or "-" for unbounded
	 * @return the number of elements removed
	 */
	@RemoveMembers
	Long removeByLexRange(@MaxLex String maxLex, @MinLex String minLex);
	
	@Override
	default Map<String, Boolean> add(Map<String, Double> members) {
		return null;
	}

	@Override
	default void addIfAbsent(Map<String, Double> members) {
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
	default Set<String> reverseScoreRange(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Set<String> reverseScoreRange(Double maxScore, Double minScore, Long pageNo, Long count) {
		return null;
	}

	@Override
	default Set<String> reverseRankRange(Long startIndex, Long endIndex) {
		return null;
	}

	@Override
	default Map<String, Double> reverseScoreRangeWithScores(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Map<String, Double> reverseScoreRangeWithScores(Double maxScore, Double minScore,
			Long pageNo, Long count) {
		return null;
	}

	@Override
	default Map<String, Double> reverseRankRangeWithScores(Long startIndex, Long endIndex) {
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
	default Map<String, Long> reverseRanks(Set<String> value) {
		return null;
	}
	
}