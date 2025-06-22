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
package com.langwuyue.orange.redis.operations;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.annotation.cross.Aggregate.Operator;
import com.langwuyue.orange.redis.annotation.zset.MaxLex;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.annotation.zset.MinLex;
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.annotation.zset.OldScore;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.annotation.zset.Score;

/**
 * Interface for Redis sorted set (ZSet) operations providing methods to manipulate
 * and query ordered sets with scores. Supports operations like range queries by
 * score/rank, set operations (union/intersection), and atomic increments.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisZSetOperations extends OrangeRedisOperations {

	/**
	 * Adds one or more members to a sorted set, or updates their scores if they already exist.
	 * Equivalent to Redis ZADD command.
	 *
	 * @param key the sorted set key
	 * @param members set of value/score pairs to add
	 * @param valueType the type of the values
	 * @return number of elements added (not including updated elements)
	 * @throws Exception if serialization fails
	 */
	Long add(String key, Set<ZSetEntry> members, RedisValueTypeEnum valueType) throws Exception;

	/**
	 * Adds a member to a sorted set only if it does not already exist.
	 * Equivalent to Redis ZADD command with NX option.
	 *
	 * @param key the sorted set key
	 * @param entry value/score pair to add
	 * @param valueType the type of the value
	 * @return true if added, false if already existed
	 * @throws Exception if serialization fails
	 */
	Boolean addIfAbsent(String key, ZSetEntry entry, RedisValueTypeEnum valueType) throws Exception;

	/**
	 * Increments the score of a member in a sorted set.
	 * Equivalent to Redis ZINCRBY command.
	 *
	 * @param key the sorted set key
	 * @param value the member value
	 * @param score the increment value
	 * @param valueType the type of the value
	 * @return the new score of the member
	 * @throws Exception if serialization fails
	 */
	Double incrementScore(String key, Object value, double score, RedisValueTypeEnum valueType) throws Exception;

	/**
	 * Gets the scores of one or more members in a sorted set.
	 * Equivalent to Redis ZSCORE command for multiple members.
	 *
	 * @param key the sorted set key
	 * @param valueType the type of the values
	 * @param values the member values to get scores for
	 * @return list of scores in same order as input values (null if member doesn't exist)
	 * @throws Exception if serialization fails
	 */
	List<Double> score(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;
	
	/**
	 * Gets the number of members in a sorted set.
	 * Equivalent to Redis ZCARD command.
	 *
	 * @param key the sorted set key
	 * @return the number of members in the set
	 * @throws Exception if any error occurs
	 */
	Long size(String key)throws Exception;

	/**
	 * Gets distinct random members with scores from a sorted set.
	 * Similar to Redis SRANDMEMBER command but for sorted sets with scores.
	 *
	 * @param key the sorted set key
	 * @param count number of distinct members to return
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of distinct random value/score pairs
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> distinctRandomMembersWithScores(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Gets distinct random members from a sorted set.
	 * Similar to Redis SRANDMEMBER command but for sorted sets.
	 *
	 * @param key the sorted set key
	 * @param count number of distinct members to return
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of distinct random members
	 * @throws Exception if serialization fails
	 */
	Set<Object> distinctRandomMembers(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Gets random members with scores from a sorted set (may contain duplicates).
	 * Similar to Redis SRANDMEMBER command but for sorted sets with scores.
	 *
	 * @param key the sorted set key
	 * @param count number of members to return
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return list of random value/score pairs (may contain duplicates)
	 * @throws Exception if serialization fails
	 */
	List<ZSetEntry> randomMembersWithScores(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Gets random members from a sorted set (may contain duplicates).
	 * Similar to Redis SRANDMEMBER command but for sorted sets.
	 *
	 * @param key the sorted set key
	 * @param count number of members to return
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return list of random members (may contain duplicates)
	 * @throws Exception if serialization fails
	 */
	List<Object> randomMembers(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Gets the rank of a member in a sorted set, with scores ordered from low to high.
	 * Equivalent to Redis ZRANK command.
	 *
	 * @param key the sorted set key
	 * @param value the member value
	 * @param valueType the type of the value
	 * @return the rank of the member (0-based), or null if member doesn't exist
	 * @throws Exception if serialization fails
	 */
	Long getMemberRank(String key, Object value, RedisValueTypeEnum valueType) throws Exception;

	/**
	 * Gets the rank of a member in a sorted set, with scores ordered from high to low.
	 * Equivalent to Redis ZREVRANK command.
	 *
	 * @param key the sorted set key
	 * @param value the member value
	 * @param valueType the type of the value
	 * @return the rank of the member (0-based), or null if member doesn't exist
	 * @throws Exception if serialization fails
	 */
	Long getMemberReverseRank(String key, Object value, RedisValueTypeEnum valueType) throws Exception;

	/**
	 * Gets a range of members in a sorted set by rank (low to high).
	 * Equivalent to Redis ZRANGE command.
	 *
	 * @param key the sorted set key
	 * @param rankRange the rank range to query
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of members in the specified rank range
	 * @throws Exception if serialization fails
	 */
	Set<Object> range(String key, RankRange rankRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Gets a range of members with scores in a sorted set by rank (low to high).
	 * Equivalent to Redis ZRANGE command with WITHSCORES option.
	 *
	 * @param key the sorted set key
	 * @param rankRange the rank range to query
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of value/score pairs in the specified rank range
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> rangeWithScores(String key, RankRange rankRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Gets a range of members with scores in a sorted set by rank (high to low).
	 * Equivalent to Redis ZREVRANGE command with WITHSCORES option.
	 *
	 * @param key the sorted set key
	 * @param rankRange the rank range to query
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of value/score pairs in the specified rank range
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> reverseRangeWithScores(String key, RankRange rankRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Gets a range of members in a sorted set by rank (high to low).
	 * Equivalent to Redis ZREVRANGE command.
	 *
	 * @param key the sorted set key
	 * @param rankRange the rank range to query
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of members in the specified rank range
	 * @throws Exception if serialization fails
	 */
	Set<Object> reverseRange(String key, RankRange rankRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Removes all members in a sorted set within the given rank range.
	 * Equivalent to Redis ZREMRANGEBYRANK command.
	 *
	 * @param key the sorted set key
	 * @param rankRange the rank range to remove
	 * @return number of members removed
	 * @throws Exception if any error occurs
	 */
	Long removeRange(String key, RankRange rankRange)throws Exception;

	/**
	 * Gets a range of members with scores in a sorted set by score (low to high).
	 * Equivalent to Redis ZRANGEBYSCORE command with WITHSCORES option.
	 *
	 * @param key the sorted set key
	 * @param scoreRange the score range to query
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of value/score pairs in the specified score range
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> rangeByScoreWithScores(String key, ScoreRange scoreRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Gets a range of members in a sorted set by score (low to high).
	 * Equivalent to Redis ZRANGEBYSCORE command.
	 *
	 * @param key the sorted set key
	 * @param scoreRange the score range to query
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of members in the specified score range
	 * @throws Exception if serialization fails
	 */
	Set<Object> rangeByScore(String key, ScoreRange scoreRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Gets a range of members with scores in a sorted set by score (high to low).
	 * Equivalent to Redis ZREVRANGEBYSCORE command with WITHSCORES option.
	 *
	 * @param key the sorted set key
	 * @param scoreRange the score range to query
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of value/score pairs in the specified score range
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> reverseRangeByScoreWithScores(String key, ScoreRange scoreRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Gets a range of members in a sorted set by score (high to low).
	 * Equivalent to Redis ZREVRANGEBYSCORE command.
	 *
	 * @param key the sorted set key
	 * @param scoreRange the score range to query
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of members in the specified score range
	 * @throws Exception if serialization fails
	 */
	Set<Object> reverseRangeByScore(String key, ScoreRange scoreRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Gets a range of members with scores in a sorted set by score (high to low) with pagination.
	 * Equivalent to Redis ZREVRANGEBYSCORE command with WITHSCORES, LIMIT offset count options.
	 *
	 * @param key the sorted set key
	 * @param scoreRange the score range to query
	 * @param pager pagination parameters
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of value/score pairs in the specified score range with pagination
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> reverseRangeByScoreWithScores(String key, ScoreRange scoreRange, Pager pager,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Gets a range of members in a sorted set by score (high to low) with pagination.
	 * Equivalent to Redis ZREVRANGEBYSCORE command with LIMIT offset count options.
	 *
	 * @param key the sorted set key
	 * @param scoreRange the score range to query
	 * @param pager pagination parameters
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of members in the specified score range with pagination
	 * @throws Exception if serialization fails
	 */
	Set<Object> reverseRangeByScore(String key, ScoreRange scoreRange, Pager pager,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Gets a range of members with scores in a sorted set by score (low to high) with pagination.
	 * Equivalent to Redis ZRANGEBYSCORE command with WITHSCORES, LIMIT offset count options.
	 *
	 * @param key the sorted set key
	 * @param scoreRange the score range to query
	 * @param pager pagination parameters
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of value/score pairs in the specified score range with pagination
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> rangeByScoreWithScores(String key, ScoreRange scoreRange, Pager pager,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Gets a range of members in a sorted set by score (low to high) with pagination.
	 * Equivalent to Redis ZRANGEBYSCORE command with LIMIT offset count options.
	 *
	 * @param key the sorted set key
	 * @param scoreRange the score range to query
	 * @param pager pagination parameters
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of members in the specified score range with pagination
	 * @throws Exception if serialization fails
	 */
	Set<Object> rangeByScore(String key, ScoreRange scoreRange, Pager pager,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Counts the members in a sorted set with scores within the given range.
	 * Equivalent to Redis ZCOUNT command.
	 *
	 * @param key the sorted set key
	 * @param scoreRange the score range to count
	 * @return number of members in the score range
	 * @throws Exception if any error occurs
	 */
	Long countByScore(String key, ScoreRange scoreRange)throws Exception;
	
	/**
	 * Removes all members in a sorted set within the given score range.
	 * Equivalent to Redis ZREMRANGEBYSCORE command.
	 *
	 * @param key the sorted set key
	 * @param scoreRange the score range to remove
	 * @return number of members removed
	 * @throws Exception if any error occurs
	 */
	Long removeRangeByScore(String key, ScoreRange scoreRange)throws Exception;
	
	/**
	 * Gets a range of members in a sorted set by lexicographical order (low to high).
	 * Equivalent to Redis ZRANGEBYLEX command.
	 *
	 * @param key the sorted set key
	 * @param lexRange the lex range to query
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of members in the specified lex range
	 * @throws Exception if serialization fails
	 */
	Set<Object> rangeByLex(String key, LexRange lexRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Gets a range of members in a sorted set by lexicographical order (low to high) with pagination.
	 * Equivalent to Redis ZRANGEBYLEX command with LIMIT offset count options.
	 *
	 * @param key the sorted set key
	 * @param lexRange the lex range to query
	 * @param pager pagination parameters
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of members in the specified lex range with pagination
	 * @throws Exception if serialization fails
	 */
	Set<Object> rangeByLex(String key, LexRange lexRange, Pager pager,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Gets a range of members in a sorted set by lexicographical order (high to low).
	 * Equivalent to Redis ZREVRANGEBYLEX command.
	 *
	 * @param key the sorted set key
	 * @param lexRange the lex range to query
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of members in the specified lex range
	 * @throws Exception if serialization fails
	 */
	Set<Object> reverseRangeByLex(String key, LexRange lexRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Gets a range of members in a sorted set by lexicographical order (high to low) with pagination.
	 * Equivalent to Redis ZREVRANGEBYLEX command with LIMIT offset count options.
	 *
	 * @param key the sorted set key
	 * @param lexRange the lex range to query
	 * @param pager pagination parameters
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of members in the specified lex range with pagination
	 * @throws Exception if serialization fails
	 */
	Set<Object> reverseRangeByLex(String key, LexRange lexRange, Pager pager,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Counts the members in a sorted set with values between the given lexicographical range.
	 * Equivalent to Redis ZLEXCOUNT command.
	 *
	 * @param key the sorted set key
	 * @param lexRange the lex range to count
	 * @return number of members in the lex range
	 * @throws Exception if any error occurs
	 */
	Long countByLex(String key, LexRange lexRange)throws Exception;
	
	/**
	 * Removes all members in a sorted set within the given lexicographical range.
	 * Equivalent to Redis ZREMRANGEBYLEX command.
	 *
	 * @param key the sorted set key
	 * @param lexRange the lex range to remove
	 * @return number of members removed
	 * @throws Exception if any error occurs
	 */
	Long removeRangeByLex(String key, LexRange lexRange)throws Exception;
	
	/**
	 * Computes the difference between a sorted set and other sorted sets.
	 * Similar to Redis ZDIFF command.
	 *
	 * @param referenceKey the key of the reference sorted set
	 * @param comparisonKeys keys of the comparison sorted sets
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of members in the difference
	 * @throws Exception if serialization fails
	 */
	Set<Object> difference(String referenceKey, Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Computes the difference between a sorted set and other sorted sets, returning members with scores.
	 * Similar to Redis ZDIFF command with WITHSCORES option.
	 *
	 * @param referenceKey the key of the reference sorted set
	 * @param comparisonKeys keys of the comparison sorted sets
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of value/score pairs in the difference
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> differenceWithScores(String referenceKey, Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Computes the difference between a sorted set and other sorted sets and stores the result.
	 * Similar to Redis ZDIFFSTORE command.
	 *
	 * @param referenceKey the key of the reference sorted set
	 * @param comparisonKeys keys of the comparison sorted sets
	 * @param storeTo destination key to store result
	 * @return number of elements in the resulting set
	 * @throws Exception if any error occurs
	 */
	Long differenceAndStore(String referenceKey, Collection<String> comparisonKeys, String storeTo);
	
	/**
	 * Removes one or more members from a sorted set.
	 * Equivalent to Redis ZREM command.
	 *
	 * @param key the sorted set key
	 * @param valueType the type of the values
	 * @param values the members to remove
	 * @return number of members removed
	 * @throws Exception if serialization fails
	 */
	Long remove(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;

	/**
	 * Removes and returns the member with the highest score from a sorted set.
	 * Equivalent to Redis ZPOPMAX command with blocking behavior.
	 *
	 * @param key the sorted set key
	 * @param timeout maximum time to wait for an element
	 * @param timeUnit time unit for the timeout
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return the removed value/score pair, or null if timeout expires
	 * @throws Exception if serialization fails
	 */
	ZSetEntry popMaxScore(String key, long timeout, TimeUnit timeUnit,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Removes and returns the member with the lowest score from a sorted set.
	 * Equivalent to Redis ZPOPMIN command with blocking behavior.
	 *
	 * @param key the sorted set key
	 * @param timeout maximum time to wait for an element
	 * @param timeUnit time unit for the timeout
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return the removed value/score pair, or null if timeout expires
	 * @throws Exception if serialization fails
	 */
	ZSetEntry popMinScore(String key, long timeout, TimeUnit timeUnit,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Removes and returns multiple members with the lowest scores from a sorted set.
	 * Equivalent to Redis ZPOPMIN command with count parameter.
	 *
	 * @param key the sorted set key
	 * @param count number of members to pop
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of removed value/score pairs
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> popMinScore(String key, int count,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Removes and returns multiple members with the highest scores from a sorted set.
	 * Equivalent to Redis ZPOPMAX command with count parameter.
	 *
	 * @param key the sorted set key
	 * @param count number of members to pop
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of removed value/score pairs
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> popMaxScore(String key, int count, RedisValueTypeEnum valueType, Type returnType) throws Exception;
	
	/**
	 * Computes the union of multiple sorted sets with scores, using custom aggregation and weights.
	 * Similar to Redis ZUNION command with WITHSCORES, AGGREGATE and WEIGHTS options.
	 *
	 * @param referenceKey the key of the reference sorted set
	 * @param comparisonKeys keys of sets to union
	 * @param operator aggregation method for scores (SUM, MIN, MAX)
	 * @param weights multiplication factors per input set
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of value/score pairs in the union
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> unionWithScores(String referenceKey, Collection<String> comparisonKeys, Operator operator,double[] weights, RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Computes the union of multiple sorted sets with scores (default SUM aggregation, equal weights).
	 * Similar to Redis ZUNION command with WITHSCORES option.
	 *
	 * @param referenceKey the key of the reference sorted set
	 * @param comparisonKeys keys of sets to union
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of value/score pairs in the union
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> unionWithScores(String referenceKey, Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Computes the union of multiple sorted sets (default SUM aggregation, equal weights).
	 * Similar to Redis ZUNION command.
	 *
	 * @param referenceKey the key of the reference sorted set
	 * @param comparisonKeys keys of sets to union
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of members in the union
	 * @throws Exception if serialization fails
	 */
	Set<Object> union(String referenceKey, Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	/**
	 * Computes the union of multiple sorted sets and stores the result (default SUM aggregation, equal weights).
	 * Similar to Redis ZUNIONSTORE command.
	 *
	 * @param referenceKey the key of the reference sorted set
	 * @param comparisonKeys keys of sets to union
	 * @param storeTo destination key to store result
	 * @return number of elements in the resulting set
	 * @throws Exception if any error occurs
	 */
	Long unionAndStore(String referenceKey, Collection<String> comparisonKeys, String storeTo) throws Exception;

	/**
	 * Computes the union of multiple sorted sets and stores the result with custom aggregation and weights.
	 * Similar to Redis ZUNIONSTORE command with AGGREGATE and WEIGHTS options.
	 *
	 * @param referenceKey the key of the reference sorted set
	 * @param comparisonKeys keys of sets to union
	 * @param storeTo destination key to store result
	 * @param operator aggregation method for scores (SUM, MIN, MAX)
	 * @param weights multiplication factors per input set
	 * @return number of elements in the resulting set
	 * @throws Exception if any error occurs
	 */
	Long unionAndStore(String referenceKey, Collection<String> comparisonKeys, String storeTo, Operator operator,double[] weights) throws Exception;
	
	/**
	 * Computes the intersection of multiple sorted sets with scores, using custom aggregation and weights.
	 * Similar to Redis ZINTER command with WITHSCORES, AGGREGATE and WEIGHTS options.
	 *
	 * @param referenceKey the key of the reference sorted set
	 * @param comparisonKeys keys of sets to intersect
	 * @param operator aggregation method for scores (SUM, MIN, MAX)
	 * @param weights multiplication factors per input set
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of value/score pairs in the intersection
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> intersectWithScores(String referenceKey, Collection<String> comparisonKeys, Operator operator,double[] weights, RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Computes the intersection of multiple sorted sets with scores (default SUM aggregation, equal weights).
	 * Similar to Redis ZINTER command with WITHSCORES option.
	 *
	 * @param referenceKey the key of the reference sorted set
	 * @param comparisonKeys keys of sets to intersect
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of value/score pairs in the intersection
	 * @throws Exception if serialization fails
	 */
	Set<ZSetEntry> intersectWithScores(String referenceKey, Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Computes the intersection of multiple sorted sets (default SUM aggregation, equal weights).
	 * Similar to Redis ZINTER command.
	 *
	 * @param referenceKey the key of the reference sorted set
	 * @param comparisonKeys keys of sets to intersect
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return set of members in the intersection
	 * @throws Exception if serialization fails
	 */
	Set<Object> intersect(String referenceKey, Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	/**
	 * Computes the intersection of multiple sorted sets and stores the result with custom aggregation and weights.
	 * Similar to Redis ZINTERSTORE command with AGGREGATE and WEIGHTS options.
	 *
	 * @param referenceKey the key of the reference sorted set
	 * @param comparisonKeys keys of sets to intersect
	 * @param storeTo destination key to store result
	 * @param operator aggregation method for scores (SUM, MIN, MAX)
	 * @param weights multiplication factors per input set
	 * @return number of elements in the resulting set
	 * @throws Exception if any error occurs
	 */
	Long intersectAndStore(String referenceKey, Collection<String> comparisonKeys, String storeTo, Operator operator,double[] weights) throws Exception;

	/**
	 * Computes the intersection of multiple sorted sets and stores the result (default SUM aggregation, equal weights).
	 * Similar to Redis ZINTERSTORE command.
	 *
	 * @param referenceKey the key of the reference sorted set
	 * @param comparisonKeys keys of sets to intersect
	 * @param storeTo destination key to store result
	 * @return number of elements in the resulting set
	 * @throws Exception if any error occurs
	 */
	Long intersectAndStore(String referenceKey, Collection<String> comparisonKeys, String storeTo) throws Exception;
	
	/**
	 * Represents a sorted set entry with Compare-And-Set (CAS) support.
	 * Extends ZSetEntry to include the original score for atomic compare-and-set operations.
	 */
	public static class CASZSetEntry extends ZSetEntry {

		/** The original score before modification (used for CAS operations) */
		@OldScore
		private Double oldScore;
		
		/**
		 * Creates a new CASZSetEntry with the specified value and score.
		 *
		 * @param value the member value
		 * @param score the member score
		 */
		public CASZSetEntry(Object value, Double score) {
			super(value, score);
		}

		/**
		 * Gets the original score before modification.
		 *
		 * @return the original score
		 */
		public Double getOldScore() {
			return oldScore;
		}

		/**
		 * Sets the original score before modification.
		 *
		 * @param oldScore the original score to set
		 */
		public void setOldScore(Double oldScore) {
			this.oldScore = oldScore;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + Objects.hash(oldScore);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			CASZSetEntry other = (CASZSetEntry) obj;
			return Objects.equals(oldScore, other.oldScore);
		}
		
	}
	
	/**
	 * Represents a member in a sorted set with its score.
	 * Used throughout the interface to represent sorted set entries.
	 */
	public static class ZSetEntry {
	
		/** The member value */
		@RedisValue
		private Object value;
		
		/** The member score */
		@Score
		private Double score;
		
		/**
		 * Creates a new ZSetEntry with the specified value and score.
		 *
		 * @param value the member value
		 * @param score the member score
		 */
		public ZSetEntry(Object value, Double score) {
			super();
			this.value = value;
			this.score = score;
		}

		@Override
		public int hashCode() {
			return Objects.hash(value);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ZSetEntry other = (ZSetEntry) obj;
			return Objects.equals(value, other.value);
		}

		/**
		 * Gets the member value.
		 *
		 * @return the member value
		 */
		public Object getValue() {
			return value;
		}

		/**
		 * Gets the member score.
		 *
		 * @return the member score
		 */
		public Double getScore() {
			return score;
		}

	}
	
	/**
	 * Represents a score range for sorted set queries.
	 * Used to specify minimum and maximum scores for range operations.
	 */
	public static class ScoreRange {
		/** Maximum score in the range (inclusive) */
		@MaxScore
		private double maxScore;
		/** Minimum score in the range (inclusive) */
		@MinScore
		private double minScore;
		
		/**
		 * Creates a new empty ScoreRange.
		 */
		public ScoreRange() {
			super();
		}

		/**
		 * Creates a new ScoreRange with specified bounds.
		 *
		 * @param maxScore the maximum score (inclusive)
		 * @param minScore the minimum score (inclusive)
		 */
		public ScoreRange(double maxScore, double minScore) {
			super();
			this.maxScore = maxScore;
			this.minScore = minScore;
		}

		/**
		 * Gets the maximum score.
		 *
		 * @return the maximum score (inclusive)
		 */
		public double getMaxScore() {
			return maxScore;
		}

		/**
		 * Gets the minimum score.
		 *
		 * @return the minimum score (inclusive)
		 */
		public double getMinScore() {
			return minScore;
		}
	}
	
	/**
	 * Represents a rank range for sorted set queries.
	 * Used to specify start and end indices for range operations.
	 */
	public static class RankRange {
		/** Starting index in the range (0-based, inclusive) */
		@StartIndex
		private long startIndex;
		/** Ending index in the range (inclusive) */
		@EndIndex
		private long endIndex;
		
		/**
		 * Creates a new empty RankRange.
		 */
		public RankRange() {
			super();
		}

		/**
		 * Creates a new RankRange with specified bounds.
		 *
		 * @param startIndex the starting index (0-based, inclusive)
		 * @param endIndex the ending index (inclusive)
		 */
		public RankRange(long startIndex, long endIndex) {
			super();
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		/**
		 * Gets the starting index.
		 *
		 * @return the starting index (0-based, inclusive)
		 */
		public long getStartIndex() {
			return startIndex;
		}

		/**
		 * Gets the ending index.
		 *
		 * @return the ending index (inclusive)
		 */
		public long getEndIndex() {
			return endIndex;
		}
	}
	
	/**
	 * Represents a lexicographical range for sorted set queries.
	 * Used to specify minimum and maximum string values for range operations.
	 */
	public static class LexRange {
		/** Maximum string value in the range (inclusive) */
		@MaxLex
		private String maxLex;
		/** Minimum string value in the range (inclusive) */
		@MinLex
		private String minLex;

		/**
		 * Creates a new LexRange with specified bounds.
		 *
		 * @param maxLex the maximum string value (inclusive)
		 * @param minLex the minimum string value (inclusive)
		 */
		public LexRange(String maxLex, String minLex) {
			super();
			this.maxLex = maxLex;
			this.minLex = minLex;
		}

		/**
		 * Creates a new empty LexRange.
		 */
		public LexRange() {
			super();
		}

		/**
		 * Gets the maximum string value.
		 *
		 * @return the maximum string value (inclusive)
		 */
		public String getMaxLex() {
			return maxLex;
		}

		/**
		 * Gets the minimum string value.
		 *
		 * @return the minimum string value (inclusive)
		 */
		public String getMinLex() {
			return minLex;
		}
	}
	
	/**
	 * Represents pagination parameters for range queries.
	 * Used to implement offset/limit style pagination.
	 */
	public static class Pager {
		/** Page number (1-based) */
		@PageNo
		private long pageNo;
		/** Number of items per page */
		@Count
		private long count;
		
		/**
		 * Creates a new empty Pager.
		 */
		public Pager() {
			super();
		}

		/**
		 * Creates a new Pager with specified parameters.
		 *
		 * @param pageNo the page number (1-based)
		 * @param count number of items per page
		 */
		public Pager(long pageNo, long count) {
			super();
			this.pageNo = pageNo;
			this.count = count;
		}
		
		/**
		 * Calculates the offset for the current page.
		 *
		 * @return the offset (number of items to skip)
		 */
		public long getOffset() {
			return (this.pageNo - 1) * this.count;
		}

		/**
		 * Gets the page number.
		 *
		 * @return the page number (1-based)
		 */
		public long getPageNo() {
			return pageNo;
		}

		/**
		 * Gets the number of items per page.
		 *
		 * @return number of items per page
		 */
		public long getCount() {
			return count;
		}
	}
	
}