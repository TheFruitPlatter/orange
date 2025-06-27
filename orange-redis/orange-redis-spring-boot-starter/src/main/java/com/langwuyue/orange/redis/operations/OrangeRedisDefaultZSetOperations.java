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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.connection.RedisZSetCommands.Weights;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

import com.langwuyue.orange.redis.OrangeAggregateMapSpringAggregateEnum;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.cross.Aggregate.Operator;
import com.langwuyue.orange.redis.configuration.OrangeRedisSerializer;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * Default implementation of Redis Sorted Set (ZSet) operations.
 * 
 * <p>This class provides a comprehensive implementation of Redis ZSet operations,
 * extending the abstract operations class and implementing the ZSet operations interface.
 * It handles all standard Redis sorted set operations including:
 * <ul>
 *   <li>Adding and removing members with scores</li>
 *   <li>Incrementing scores</li>
 *   <li>Range operations (by score, rank, and lexicographical order)</li>
 *   <li>Set operations (union, intersection, difference)</li>
 *   <li>Pop operations</li>
 *   <li>Counting and size operations</li>
 * </ul>
 * 
 * <p>The implementation includes proper serialization/deserialization of values,
 * comprehensive logging of operations (when debug is enabled), and proper error
 * handling. It uses Spring's {@link RedisTemplate} for actual Redis operations
 * while providing a more feature-rich and type-safe API.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisZSetOperations
 * @see OrangeRedisAbstractOperations
 * @see ZSetOperations
 */
public class OrangeRedisDefaultZSetOperations extends OrangeRedisAbstractOperations implements OrangeRedisZSetOperations {

	/**
	 * The underlying Spring Redis ZSet operations implementation.
	 * Handles the actual Redis commands for sorted set operations.
	 */
	private ZSetOperations<String, byte[]> operations;
	
	/**
	 * Serializer for converting between Java objects and byte arrays.
	 * Used for serializing/deserializing values stored in Redis.
	 */
	private OrangeRedisSerializer redisSerializer;
	
	/**
	 * Logger for recording operation details and debug information.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new OrangeRedisDefaultZSetOperations instance.
	 *
	 * @param template The Redis template to use for operations
	 * @param redisSerializer The serializer to use for converting objects to/from byte arrays
	 * @param logger The logger to use for recording operations and debug information
	 */
	public OrangeRedisDefaultZSetOperations(RedisTemplate<String,byte[]> template, OrangeRedisSerializer redisSerializer, OrangeRedisLogger logger) {
		super(template,logger);
		this.operations = template.opsForZSet();
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}
	
	/**
	 * Adds multiple members with their associated scores to a sorted set stored at the specified key.
	 * If a member already exists in the sorted set, its score is updated and the member
	 * is reinserted at the appropriate position.
	 * 
	 * <p>This method serializes each value according to the specified value type before
	 * adding it to the sorted set. If the key does not exist, a new sorted set is created.
	 * 
	 * <p>Each ZSetEntry in the members set contains a member and its associated score.
	 * The score can be any double-precision floating point number.
	 *
	 * @param key The key of the sorted set
	 * @param members A set of ZSetEntry objects, each containing a member and its score
	 * @param valueType The type of the values to be stored, used for serialization
	 * @return The number of elements added to the sorted set, not including elements already existing for which the score was updated
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see ZSetOperations#add(Object, Set)
	 */
	@Override
	public Long add(String key, Set<ZSetEntry> members, RedisValueTypeEnum valueType) throws Exception {
		Set<TypedTuple<byte[]>> typedTuples = new HashSet<>();
		for(ZSetEntry member : members) {
			byte[] bytes = redisSerializer.serialize(member.getValue(), valueType);
			TypedTuple<byte[]> typedTuple = new DefaultTypedTuple<>(bytes,member.getScore());
			typedTuples.add(typedTuple);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'add' operation executing: add(key:{},members:{})", key, redisSerializer.serializeToJSONString(members));
		}
		Long result = this.operations.add(key, typedTuples);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'add' operation returned {}", result);
		}
		return result;
	}

	/**
	 * Adds a member with its associated score to a sorted set stored at the specified key,
	 * but only if the member does not already exist in the set. This is an atomic operation.
	 * 
	 * <p>This method serializes the value according to the specified value type before
	 * attempting to add it to the sorted set. If the key does not exist, a new sorted set is created.
	 * 
	 * <p>Unlike the regular add method, this method will not update the score if the member
	 * already exists in the set. Instead, it will return false to indicate that the operation
	 * was not performed.
	 *
	 * @param key The key of the sorted set
	 * @param entry A ZSetEntry object containing the member and its score
	 * @param valueType The type of the value to be stored, used for serialization
	 * @return true if the member was added with its score, false if the member already existed
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see ZSetOperations#addIfAbsent(Object, Object, double)
	 */
	@Override
	public Boolean addIfAbsent(String key, ZSetEntry entry, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(entry.getValue(), valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'addIfAbsent' operation executing: addIfAbsent(key:{},value:{},score:{})", key, new String(bytes),entry.getScore());
		}
		Boolean result =  this.operations.addIfAbsent(key, bytes, entry.getScore());
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'addIfAbsent' operation returned {}", result);
		}
		return result;
	}

	
	/**
	 * Increments the score of a member in a sorted set by the given amount.
	 * This is an atomic operation that both gets and sets the new score.
	 * 
	 * <p>This method serializes the value according to the specified value type before
	 * performing the increment operation. If the member does not exist in the sorted set,
	 * it is added with the specified score (not the increment). If key does not exist,
	 * a new sorted set with the specified member and score is created.
	 * 
	 * <p>The score can be negative to perform a decrement operation. The resulting score
	 * can also be negative, as Redis ZSet scores can be any double-precision floating point number.
	 *
	 * @param key The key of the sorted set
	 * @param value The member whose score should be incremented
	 * @param score The increment value (can be negative for decrement)
	 * @param valueType The type of the value to be stored, used for serialization
	 * @return The new score of the member after the increment operation
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see ZSetOperations#incrementScore(Object, Object, double)
	 */
	@Override
	public Double incrementScore(String key, Object value, double score, RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'incrementScore' operation executing: incrementScore(Key:{},value:{},score:{})", key, new String(bytes),score);
		}
		Double result =  this.operations.incrementScore(key, bytes, score);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'incrementScore' operation returned {}", result);
		}
		return result;
	}

	/**
	 * Retrieves the scores of one or more members in a sorted set.
	 * 
	 * <p>This method serializes each value according to the specified value type before
	 * querying the sorted set. For each member, the method returns its associated score.
	 * If a member does not exist in the sorted set, null will be returned for that member
	 * in the result list.
	 * 
	 * <p>The order of scores in the returned list corresponds to the order of the members
	 * in the input array.
	 *
	 * @param key The key of the sorted set
	 * @param valueType The type of the values stored, used for serialization
	 * @param values One or more members whose scores are to be retrieved
	 * @return A list of Double values representing the scores of the specified members,
	 *         or null for members that do not exist in the sorted set
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see ZSetOperations#score(Object, Object)
	 */
	@Override
	public List<Double> score(String key, RedisValueTypeEnum valueType, Object... values) throws Exception {
		Object[] newArray = new Object[values.length];
		for(int i = 0; i < values.length; i++) {
			newArray[i] = redisSerializer.serialize(values[i], valueType);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'score' operation executing: score(key:{},value:{})", key, redisSerializer.serializeToJSONString(values));
		}
		List<Double> results = this.operations.score(key, newArray);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'score' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Returns the number of members in a sorted set.
	 * 
	 * <p>This method returns the cardinality (number of elements) of the sorted set
	 * stored at the specified key. If the key does not exist, it is interpreted as
	 * an empty sorted set and 0 is returned.
	 *
	 * @param key The key of the sorted set
	 * @return The number of members in the sorted set, or 0 if the key does not exist
	 * @see ZSetOperations#size(Object)
	 */
	@Override
	public Long size(String key) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'size' operation executing: size(key:{})", key);
		}
		Long results = this.operations.size(key);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'score' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Retrieves a specified number of distinct random members with their scores
	 * from a sorted set stored at the specified key.
	 * 
	 * <p>This method returns unique random members from the sorted set along with their scores.
	 * If count is greater than the cardinality of the sorted set, all members are returned.
	 * If the key does not exist, an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them as ZSetEntry objects containing both
	 * the member value and its score.
	 *
	 * @param key The key of the sorted set
	 * @param count The number of distinct random members to return
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the randomly selected members and their scores
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#distinctRandomMembersWithScore(Object, long)
	 */
	@Override
	public Set<ZSetEntry> distinctRandomMembersWithScores(
		String key, 
		long count, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'distinctRandomMembersWithScore' operation executing: distinctRandomMembersWithScore(key:{},count:{})", key,count);
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.distinctRandomMembersWithScore(key, count);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'distinctRandomMembersWithScore' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves a specified number of distinct random members from a sorted set stored at the specified key.
	 * 
	 * <p>This method ensures that all returned members are unique (no duplicates).
	 * If the count is greater than the number of members in the sorted set,
	 * all members will be returned. If the key does not exist, an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them.
	 *
	 * @param key The key of the sorted set
	 * @param count The number of distinct random members to return
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of deserialized objects representing the distinct random members
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#distinctRandomMembers(Object, long)
	 */
	@Override
	public Set<Object> distinctRandomMembers(
		String key, 
		long count,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'distinctRandomMembers' operation executing: distinctRandomMembers(key:{},count:{})", key,count);
		}
		Set<byte[]> values = this.operations.distinctRandomMembers(key, count);
		Set<Object> results = redisSerializer.deserialize(values,valueType,returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'distinctRandomMembers' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves a specified number of random members and their scores from a sorted set stored at the specified key.
	 * 
	 * <p>This method may return the same member multiple times if count is greater than
	 * the number of members in the sorted set. If the key does not exist, an empty list is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them as ZSetEntry objects containing both
	 * the member value and its score.
	 *
	 * @param key The key of the sorted set
	 * @param count The number of random members to return
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A list of ZSetEntry objects containing the random members and their scores
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#randomMembersWithScore(Object, long)
	 */
	@Override
	public List<ZSetEntry> randomMembersWithScores(
		String key, 
		long count,	
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'randomMembersWithScores' operation executing: randomMembersWithScores(key:{},count:{})", key,count);
		}
		List<TypedTuple<byte[]>> typedTuples = this.operations.randomMembersWithScore(key, count);
		List<ZSetEntry> results = typedTupleListToZSetEntryList(typedTuples,valueType,returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'randomMembersWithScores' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}
	
	private List<ZSetEntry> typedTupleListToZSetEntryList(
		List<TypedTuple<byte[]>> typedTuples,
		RedisValueTypeEnum valueType,
		Type returnType
	) throws Exception {
		if(typedTuples == null || typedTuples.isEmpty()) {
			return new ArrayList<>();
		}
		int size = typedTuples.size();
		List<ZSetEntry> entries = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			TypedTuple<byte[]> typedTuple = typedTuples.get(i);
			Object value = redisSerializer.deserialize(typedTuple.getValue(), valueType, returnType);
			if(value == null) {
				continue;
			}
			entries.add(i, new ZSetEntry(value,typedTuple.getScore()));
		}
		return entries;
	}

	/**
	 * Retrieves a specified number of random members from a sorted set stored at the specified key.
	 * 
	 * <p>This method may return the same member multiple times if count is greater than
	 * the number of members in the sorted set. If the key does not exist, an empty list is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them.
	 *
	 * @param key The key of the sorted set
	 * @param count The number of random members to return
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A list of deserialized objects representing the random members
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#randomMembers(Object, long)
	 */
	@Override
	public List<Object> randomMembers(
		String key, 
		long count,	
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'randomMembers' operation executing: randomMembers(key:{},count:{})", key,count);
		}
		List<byte[]> values = this.operations.randomMembers(key, count);
		List<Object> results = redisSerializer.deserialize(values,valueType,returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'randomMembers' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}
	
	/**
	 * Returns the rank (index) of a member in a sorted set stored at the specified key,
	 * with scores ordered from low to high.
	 * 
	 * <p>This method returns the zero-based position of the member in the sorted set,
	 * where members are ordered by their scores from lowest to highest. If the member
	 * does not exist in the set or if the key does not exist, null is returned.
	 * 
	 * <p>This method serializes the member value according to the specified value type
	 * before querying the Redis sorted set.
	 *
	 * @param key The key of the sorted set
	 * @param value The member whose rank is to be determined
	 * @param valueType The type of the value being queried, used for serialization
	 * @return The rank (zero-based index) of the member in the sorted set, or null if the member or key does not exist
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see ZSetOperations#rank(Object, Object)
	 */
	@Override
	public Long getMemberRank(String key, Object value,RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(bytes == null) {
			return null;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'getMemberRank' operation executing: getMemberRank(key:{},value:{})", key,value);
		}
		Long result = this.operations.rank(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'getMemberRank' operation returned {}", result);
		}
		return result;
	}

	/**
	 * Returns the reverse rank (index) of a member in a sorted set stored at the specified key,
	 * with scores ordered from high to low.
	 * 
	 * <p>This method returns the zero-based position of the member in the sorted set,
	 * where members are ordered by their scores from highest to lowest. If the member
	 * does not exist in the set or if the key does not exist, null is returned.
	 * 
	 * <p>This method serializes the member value according to the specified value type
	 * before querying the Redis sorted set.
	 *
	 * @param key The key of the sorted set
	 * @param value The member whose reverse rank is to be determined
	 * @param valueType The type of the value being queried, used for serialization
	 * @return The reverse rank (zero-based index) of the member in the sorted set, or null if the member or key does not exist
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see ZSetOperations#reverseRank(Object, Object)
	 */
	@Override
	public Long getMemberReverseRank(String key, Object value,RedisValueTypeEnum valueType) throws Exception {
		byte[] bytes = redisSerializer.serialize(value, valueType);
		if(bytes == null) {
			return null;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'getMemberReverseRank' operation executing: getMemberReverseRank(key:{},value:{})", key,value);
		}
		Long result = this.operations.reverseRank(key, bytes);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'getMemberReverseRank' operation returned {}", result);
		}
		return result;
	}

	/**
	 * Retrieves members from a sorted set stored at the specified key
	 * within the specified index range, ordered by their scores from low to high.
	 * 
	 * <p>The indices are zero-based, with 0 being the element with the lowest score.
	 * Negative indices can be used to specify elements starting from the end of the sorted set,
	 * with -1 being the element with the highest score.
	 * If the key does not exist, it is treated as an empty sorted set and an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them.
	 *
	 * @param key The key of the sorted set
	 * @param rankRange The range object specifying the start and end positions
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of deserialized objects representing the members within the specified index range, ordered by scores from low to high
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#range(Object, long, long)
	 */
	@Override
	public Set<Object> range(
		String key, 
		RankRange rankRange,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'range' operation executing: range(key:{},start:{},end:{})", key,rankRange.getStartIndex(),rankRange.getEndIndex());
		}
		Set<byte[]> values = this.operations.range(key, rankRange.getStartIndex(), rankRange.getEndIndex());
		Set<Object> results = redisSerializer.deserialize(values,valueType,returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'range' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves members and their scores from a sorted set stored at the specified key
	 * within the specified index range, ordered by their scores from low to high.
	 * 
	 * <p>The indices are zero-based, with 0 being the element with the lowest score.
	 * Negative indices can be used to specify elements starting from the end of the sorted set,
	 * with -1 being the element with the highest score.
	 * If the key does not exist, it is treated as an empty sorted set and an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them as ZSetEntry objects containing both
	 * the member value and its score.
	 *
	 * @param key The key of the sorted set
	 * @param rankRange The range object specifying the start and end positions
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the members and their scores within the specified index range, ordered by scores from low to high
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#rangeWithScores(Object, long, long)
	 */
	@Override
	public Set<ZSetEntry> rangeWithScores(
		String key, 
		RankRange rankRange,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeWithScores' operation executing: rangeWithScores(key:{},start:{},end:{})", key,rankRange.getStartIndex(),rankRange.getEndIndex());
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.rangeWithScores(
			key, 
			rankRange.getStartIndex(), 
			rankRange.getEndIndex()
		);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples,valueType,returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeWithScores' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves members and their scores from a sorted set stored at the specified key
	 * within the specified range of indices, ordered by their scores from high to low.
	 * 
	 * <p>This method returns members with indices between start and end (inclusive),
	 * where the indices are zero-based and ordered by scores from highest to lowest.
	 * Negative indices can be used to specify positions from the end of the sorted set,
	 * where -1 is the last element, -2 is the penultimate element, and so on.
	 * If the key does not exist, it is treated as an empty sorted set and an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them as ZSetEntry objects containing both
	 * the member value and its score.
	 *
	 * @param key The key of the sorted set
	 * @param rankRange The range object specifying the start and end positions
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the members and their scores within the specified range, ordered by scores from high to low
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#reverseRangeWithScores(Object, long, long)
	 */
	@Override
	public Set<ZSetEntry> reverseRangeWithScores(
		String key, 
		RankRange rankRange, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeWithScores' operation executing: reverseRangeWithScores(key:{},start:{},end:{})", key,rankRange.getStartIndex(),rankRange.getEndIndex());
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.reverseRangeWithScores(
			key, 
			rankRange.getStartIndex(), 
			rankRange.getEndIndex()
		);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples,valueType,returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeWithScores' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}
	
	/**
	 * Converts a set of Redis TypedTuple objects to ZSetEntry objects.
	 * 
	 * <p>This internal utility method transforms Redis sorted set entries (TypedTuple)
	 * into the application's ZSetEntry format, handling deserialization of values
	 * according to the specified type information.
	 *
	 * @param typedTuples The set of TypedTuple objects to convert
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the deserialized values and their scores
	 * @throws Exception If deserialization fails
	 */
	private Set<ZSetEntry> typedTuplesToZSetEntries(
		Set<TypedTuple<byte[]>> typedTuples,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(typedTuples == null || typedTuples.isEmpty()) {
			return Collections.EMPTY_SET;
		}
		Set<ZSetEntry> entries = new LinkedHashSet<>(typedTuples.size());
		for(TypedTuple<byte[]> typedTuple : typedTuples) {
			ZSetEntry entry = typedTupleToZSetEntry(typedTuple,valueType,returnType);
			if(entry == null) {
				continue;
			}
			entries.add(entry);
		}
		return entries;
	}
	
	private ZSetEntry typedTupleToZSetEntry(
		TypedTuple<byte[]> typedTuple,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(typedTuple == null) {
			return null;
		}
		Object value = redisSerializer.deserialize(typedTuple.getValue(), valueType, returnType);
		if(value == null) {
			return null;
		}
		return new ZSetEntry(value, typedTuple.getScore());
	}

	/**
	 * Retrieves members from a sorted set stored at the specified key
	 * within the specified index range, ordered by their scores from high to low.
	 * 
	 * <p>The indices are zero-based, with 0 being the element with the highest score.
	 * Negative indices can be used to specify elements starting from the end of the sorted set,
	 * with -1 being the element with the lowest score.
	 * If the key does not exist, it is treated as an empty sorted set and an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them.
	 *
	 * @param key The key of the sorted set
	 * @param rankRange The range object specifying the start and end positions
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of deserialized objects representing the members within the specified index range, ordered by scores from high to low
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#reverseRange(Object, long, long)
	 */
	@Override
	public Set<Object> reverseRange(
		String key, 
		RankRange rankRange,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRange' operation executing: reverseRange(key:{},start:{},end:{})", key,rankRange.getStartIndex(),rankRange.getEndIndex());
		}
		Set<byte[]> values = this.operations.reverseRange(key, rankRange.getStartIndex(), rankRange.getEndIndex());
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRange' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Removes members from a sorted set stored at the specified key within the specified index range.
	 * 
	 * <p>The indices are zero-based, with 0 being the element with the lowest score.
	 * Negative indices can be used to specify elements starting from the end of the sorted set,
	 * with -1 being the element with the highest score.
	 * If the key does not exist, it is treated as an empty sorted set and 0 is returned.
	 * 
	 * <p>This method removes all members with indices between start and end (inclusive).
	 * After the operation, these members will no longer exist in the sorted set.
	 *
	 * @param key The key of the sorted set
	 * @param rankRange The range object specifying the start and end positions of members to remove
	 * @return The number of members removed from the sorted set
	 * @see ZSetOperations#removeRange(Object, long, long)
	 */
	@Override
	public Long removeRange(String key, RankRange rankRange) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'removeRange' operation executing: removeRange(key:{},start:{},end:{})", key,rankRange.getStartIndex(),rankRange.getEndIndex());
		}
		Long results = this.operations.removeRange(key, rankRange.getStartIndex(), rankRange.getEndIndex());
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'removeRange' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Retrieves members and their scores from a sorted set stored at the specified key whose scores
	 * fall within the specified score range, ordered by their scores from low to high.
	 * 
	 * <p>This method returns members with scores greater than or equal to the minimum score
	 * and less than or equal to the maximum score, in ascending order of scores.
	 * If the key does not exist, it is treated as an empty sorted set and an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them as ZSetEntry objects containing both
	 * the member value and its score.
	 *
	 * @param key The key of the sorted set
	 * @param scoreRange The range object specifying the minimum and maximum scores
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the members and their scores whose scores fall within the specified range, ordered by scores from low to high
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#rangeByScoreWithScores(Object, double, double)
	 */
	@Override
	public Set<ZSetEntry> rangeByScoreWithScores(
		String key, 
		ScoreRange scoreRange,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByScoreWithScores' operation executing: rangeByScoreWithScores(key:{},min:{},max:{})", key,scoreRange.getMinScore(),scoreRange.getMaxScore());
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.rangeByScoreWithScores(
			key, 
			scoreRange.getMinScore(),
			scoreRange.getMaxScore() 
		);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByScoreWithScores' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}
	
	/**
	 * Retrieves members from a sorted set stored at the specified key whose scores
	 * fall within the specified score range, ordered by their scores from low to high.
	 * 
	 * <p>This method returns members with scores greater than or equal to the minimum score
	 * and less than or equal to the maximum score, in ascending order of scores.
	 * If the key does not exist, it is treated as an empty sorted set and an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them.
	 *
	 * @param key The key of the sorted set
	 * @param scoreRange The range object specifying the minimum and maximum scores
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of deserialized objects representing the members whose scores fall within the specified range, ordered by scores from low to high
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#rangeByScore(Object, double, double)
	 */
	@Override
	public Set<Object> rangeByScore(
		String key, 
		ScoreRange scoreRange,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByScore' operation executing: rangeByScore(key:{},min:{},max:{})", key,scoreRange.getMinScore(),scoreRange.getMaxScore());
		}
		Set<byte[]> values = this.operations.rangeByScore(key, scoreRange.getMinScore(), scoreRange.getMaxScore());
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByScore' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves members and their scores from a sorted set stored at the specified key whose scores
	 * fall within the specified score range, ordered by their scores from high to low.
	 * 
	 * <p>This method returns members with scores greater than or equal to the minimum score
	 * and less than or equal to the maximum score, in descending order of scores.
	 * If the key does not exist, it is treated as an empty sorted set and an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them as ZSetEntry objects containing both
	 * the member value and its score.
	 *
	 * @param key The key of the sorted set
	 * @param scoreRange The range object specifying the minimum and maximum scores
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the members and their scores whose scores fall within the specified range, ordered by scores from high to low
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#reverseRangeByScoreWithScores(Object, double, double)
	 */
	@Override
	public Set<ZSetEntry> reverseRangeByScoreWithScores(
		String key, 
		ScoreRange scoreRange,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeByScoreWithScores' operation executing: reverseRangeByScoreWithScores(key:{},min:{},max:{})", key,scoreRange.getMinScore(),scoreRange.getMaxScore());
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.reverseRangeByScoreWithScores(
			key, 
			scoreRange.getMinScore(), 
			scoreRange.getMaxScore()
		);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeByScoreWithScores' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves members from a sorted set stored at the specified key whose scores
	 * fall within the specified score range, ordered by their scores from high to low.
	 * 
	 * <p>This method returns members with scores greater than or equal to the minimum score
	 * and less than or equal to the maximum score, in descending order of scores.
	 * If the key does not exist, it is treated as an empty sorted set and an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them.
	 *
	 * @param key The key of the sorted set
	 * @param scoreRange The range object specifying the minimum and maximum scores
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of deserialized objects representing the members whose scores fall within the specified range, ordered by scores from high to low
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#reverseRangeByScore(Object, double, double)
	 */
	@Override
	public Set<Object> reverseRangeByScore(
		String key, 
		ScoreRange scoreRange,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeByScore' operation executing: reverseRangeByScore(key:{},min:{},max:{})", key,scoreRange.getMinScore(),scoreRange.getMaxScore());
		}
		Set<byte[]> values = this.operations.reverseRangeByScore(key, scoreRange.getMinScore(), scoreRange.getMaxScore());
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeByScore' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves members and their scores from a sorted set stored at the specified key whose scores
	 * fall within the specified score range, ordered by their scores from high to low,
	 * with pagination support.
	 * 
	 * <p>This method returns members with scores greater than or equal to the minimum score
	 * and less than or equal to the maximum score, in descending order of scores.
	 * The results are paginated according to the specified pager parameters.
	 * If the key does not exist, it is treated as an empty sorted set and an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them as ZSetEntry objects containing both
	 * the member value and its score.
	 *
	 * @param key The key of the sorted set
	 * @param scoreRange The range object specifying the minimum and maximum scores
	 * @param pager The pagination parameters specifying offset and count
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the members and their scores whose scores fall within the specified range, ordered by scores from high to low
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#reverseRangeByScoreWithScores(Object, double, double, long, long)
	 */
	@Override
	public Set<ZSetEntry> reverseRangeByScoreWithScores(
		String key, 
		ScoreRange scoreRange, 
		Pager pager, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeByScoreWithScores' operation executing: reverseRangeByScoreWithScores(key:{},min:{},max:{},page:{},count:{})", 
				key,
				scoreRange.getMinScore(),
				scoreRange.getMaxScore(),
				pager.getPageNo(),
				pager.getCount()
			);
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.reverseRangeByScoreWithScores(
			key, 
			scoreRange.getMinScore(),
			scoreRange.getMaxScore(), 
			pager.getOffset(),
			pager.getCount()
		);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeByScoreWithScores' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves members from a sorted set stored at the specified key whose scores
	 * fall within the specified score range, ordered by their scores from high to low,
	 * with pagination support.
	 * 
	 * <p>This method returns members with scores greater than or equal to the minimum score
	 * and less than or equal to the maximum score, in descending order of scores.
	 * The results are paginated according to the specified pager parameters.
	 * If the key does not exist, it is treated as an empty sorted set and an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them.
	 *
	 * @param key The key of the sorted set
	 * @param scoreRange The range object specifying the minimum and maximum scores
	 * @param pager The pagination parameters specifying offset and count
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of deserialized objects representing the members whose scores fall within the specified range, ordered by scores from high to low
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#reverseRangeByScore(Object, double, double, long, long)
	 */
	@Override
	public Set<Object> reverseRangeByScore(
		String key, 
		ScoreRange scoreRange, 
		Pager pager, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeByScore' operation executing: reverseRangeByScore(key:{},min:{},max:{},page:{},count:{})", 
				key,
				scoreRange.getMinScore(),
				scoreRange.getMaxScore(),
				pager.getPageNo(),
				pager.getCount()
			);
		}
		Set<byte[]> values = this.operations.reverseRangeByScore(
			key, 
			scoreRange.getMinScore(),
			scoreRange.getMaxScore(), 
			pager.getOffset(),
			pager.getCount()
		);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeByScore' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves members and their scores from a sorted set stored at the specified key whose scores
	 * fall within the specified score range, ordered by their scores from low to high,
	 * with pagination support.
	 * 
	 * <p>This method returns members with scores greater than or equal to the minimum score
	 * and less than or equal to the maximum score, in ascending order of scores.
	 * The results are paginated according to the specified pager parameters.
	 * If the key does not exist, it is treated as an empty sorted set and an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them as ZSetEntry objects containing both
	 * the member value and its score.
	 *
	 * @param key The key of the sorted set
	 * @param scoreRange The range object specifying the minimum and maximum scores
	 * @param pager The pagination parameters specifying offset and count
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the members and their scores whose scores fall within the specified range, ordered by scores from low to high
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#rangeByScoreWithScores(Object, double, double, long, long)
	 */
	@Override
	public Set<ZSetEntry> rangeByScoreWithScores(
		String key, 
		ScoreRange scoreRange, 
		Pager pager, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByScoreWithScores' operation executing: reverseRangeByScore(key:{},min:{},max:{},page:{},count:{})", 
				key,
				scoreRange.getMinScore(),
				scoreRange.getMaxScore(),
				pager.getPageNo(),
				pager.getCount()
			);
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.rangeByScoreWithScores(
			key, 
			scoreRange.getMinScore(),
			scoreRange.getMaxScore(), 
			pager.getOffset(),
			pager.getCount()
		);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByScoreWithScores' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves members from a sorted set stored at the specified key whose scores
	 * fall within the specified score range, ordered by their scores from low to high,
	 * with pagination support.
	 * 
	 * <p>This method returns members with scores greater than or equal to the minimum score
	 * and less than or equal to the maximum score, in ascending order of scores.
	 * The results are paginated according to the specified pager parameters.
	 * If the key does not exist, it is treated as an empty sorted set and an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them.
	 *
	 * @param key The key of the sorted set
	 * @param scoreRange The range object specifying the minimum and maximum scores
	 * @param pager The pagination parameters specifying offset and count
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of deserialized objects representing the members whose scores fall within the specified range, ordered by scores from low to high
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#rangeByScore(Object, double, double, long, long)
	 */
	@Override
	public Set<Object> rangeByScore(
		String key, 
		ScoreRange scoreRange, 
		Pager pager, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByScore' operation executing: rangeByScore(key:{},min:{},max:{},page:{},count:{})", 
				key,
				scoreRange.getMinScore(),
				scoreRange.getMaxScore(),
				pager.getPageNo(),
				pager.getCount()
			);
		}
		Set<byte[]> values = this.operations.rangeByScore(
			key, 
			scoreRange.getMinScore(),
			scoreRange.getMaxScore(), 
			pager.getOffset(),
			pager.getCount()
		);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByScore' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Counts the number of members in a sorted set stored at the specified key
	 * whose scores fall within the specified score range.
	 * 
	 * <p>This method returns the count of members with scores greater than or equal to
	 * the minimum score and less than or equal to the maximum score.
	 * If the key does not exist, it is treated as an empty sorted set and 0 is returned.
	 *
	 * @param key The key of the sorted set
	 * @param scoreRange The range object specifying the minimum and maximum scores
	 * @return The number of members in the sorted set with scores within the specified range
	 * @see ZSetOperations#count(Object, double, double)
	 */
	@Override
	public Long countByScore(String key, ScoreRange scoreRange) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'countByScore' operation executing: countByScore(key:{},min:{},max:{})", key,scoreRange.getMinScore(),scoreRange.getMaxScore());
		}
		Long results = this.operations.count(key, scoreRange.getMinScore(), scoreRange.getMaxScore());
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'countByScore' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Removes members from a sorted set stored at the specified key
	 * whose scores fall within the specified score range.
	 * 
	 * <p>This method removes all members with scores greater than or equal to
	 * the minimum score and less than or equal to the maximum score.
	 * If the key does not exist, it is treated as an empty sorted set and 0 is returned.
	 * 
	 * <p>After the operation, members with scores in the specified range will no longer
	 * exist in the sorted set.
	 *
	 * @param key The key of the sorted set
	 * @param scoreRange The range object specifying the minimum and maximum scores of members to remove
	 * @return The number of members removed from the sorted set
	 * @see ZSetOperations#removeRangeByScore(Object, double, double)
	 */
	@Override
	public Long removeRangeByScore(String key, ScoreRange scoreRange) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'removeRangeByScore' operation executing: removeRangeByScore(key:{},min:{},max:{})", key,scoreRange.getMinScore(),scoreRange.getMaxScore());
		}
		Long results =  this.operations.removeRangeByScore(key, scoreRange.getMinScore(), scoreRange.getMaxScore());
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'removeRangeByScore' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Retrieves members from a sorted set stored at the specified key whose values
	 * fall within the specified lexicographical range, ordered by their values.
	 * 
	 * <p>This method is particularly useful for sorted sets where all members have the same score,
	 * and the ordering is based on the lexicographical ordering of the member values.
	 * If the key does not exist, an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them.
	 *
	 * @param key The key of the sorted set
	 * @param lexRange The range object specifying the minimum and maximum lexicographical values
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of deserialized objects representing the members whose values fall within the specified lexicographical range
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#rangeByLex(Object, Range)
	 */
	@Override
	public Set<Object> rangeByLex(
		String key, 
		LexRange lexRange,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByLex' operation executing: rangeByLex(key:{},min:{},max:{})", key,lexRange.getMinLex(),lexRange.getMaxLex());
		}
		Range range = Range.range();
		range.lte(lexRange.getMaxLex());
		range.gte(lexRange.getMinLex());
		Set<byte[]> values = this.operations.rangeByLex(key, range);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByLex' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves a paginated set of members from a sorted set stored at the specified key
	 * whose values fall within the specified lexicographical range, ordered by their values.
	 * 
	 * <p>This method is particularly useful for sorted sets where all members have the same score,
	 * and the ordering is based on the lexicographical ordering of the member values.
	 * If the key does not exist, an empty set is returned.
	 * 
	 * <p>The pager parameter allows for pagination of results, specifying the offset and count
	 * of members to return. This is useful when dealing with large sorted sets to retrieve
	 * results in smaller chunks.
	 *
	 * @param key The key of the sorted set
	 * @param lexRange The range object specifying the minimum and maximum lexicographical values
	 * @param pager The pagination parameters specifying offset and count
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A paginated set of deserialized objects representing the members whose values fall within the specified lexicographical range
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#rangeByLex(Object, Range, org.springframework.data.redis.connection.RedisZSetCommands.Limit)
	 */
	@Override
	public Set<Object> rangeByLex(
		String key, 
		LexRange lexRange,
		Pager pager,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByLex' operation executing: rangeByLex(key:{},min:{},max:{},page:{},count:{})", 
					key,
					lexRange.getMinLex(),
					lexRange.getMaxLex(),
					pager.getPageNo(),
					pager.getCount()
			);
		}
		Range range = Range.range();
		range.lte(lexRange.getMaxLex());
		range.gte(lexRange.getMinLex());
		
		Limit limit = Limit.limit();
		limit.count((int)pager.getCount());
		limit.offset((int)pager.getOffset());
		Set<byte[]> values = this.operations.rangeByLex(key, range, limit);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByLex' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves members from a sorted set stored at the specified key whose values
	 * fall within the specified lexicographical range, ordered in reverse lexicographical order.
	 * 
	 * <p>This method is similar to rangeByLex but returns the results in reverse order.
	 * It is particularly useful for sorted sets where all members have the same score,
	 * and the ordering is based on the lexicographical ordering of the member values.
	 * If the key does not exist, an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them.
	 *
	 * @param key The key of the sorted set
	 * @param lexRange The range object specifying the minimum and maximum lexicographical values
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of deserialized objects representing the members whose values fall within the specified lexicographical range, in reverse order
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#reverseRangeByLex(Object, Range)
	 */
	@Override
	public Set<Object> reverseRangeByLex(
		String key, 
		LexRange lexRange,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeByLex' operation executing: reverseRangeByLex(key:{},min:{},max:{})", key,lexRange.getMinLex(),lexRange.getMaxLex());
		}
		Range range = Range.range();
		range.lte(lexRange.getMaxLex());
		range.gte(lexRange.getMinLex());
		Set<byte[]> values = this.operations.reverseRangeByLex(key, range);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeByLex' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Retrieves a paginated set of members from a sorted set stored at the specified key
	 * whose values fall within the specified lexicographical range, ordered in reverse lexicographical order.
	 * 
	 * <p>This method is similar to rangeByLex with pagination but returns the results in reverse order.
	 * It is particularly useful for sorted sets where all members have the same score,
	 * and the ordering is based on the lexicographical ordering of the member values.
	 * If the key does not exist, an empty set is returned.
	 * 
	 * <p>The pager parameter allows for pagination of results, specifying the offset and count
	 * of members to return. This is useful when dealing with large sorted sets to retrieve
	 * results in smaller chunks.
	 *
	 * @param key The key of the sorted set
	 * @param lexRange The range object specifying the minimum and maximum lexicographical values
	 * @param pager The pagination parameters specifying offset and count
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A paginated set of deserialized objects representing the members whose values fall within the specified lexicographical range, in reverse order
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#reverseRangeByLex(Object, Range, org.springframework.data.redis.connection.RedisZSetCommands.Limit)
	 */
	@Override
	public Set<Object> reverseRangeByLex(
		String key, 
		LexRange lexRange,
		Pager pager,
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeByLex' operation executing: reverseRangeByLex(key:{},min:{},max:{},page:{},count:{})", 
					key,
					lexRange.getMinLex(),
					lexRange.getMinLex(),
					pager.getPageNo(),
					pager.getCount()
			);
		}
		Range range = Range.range();
		range.lte(lexRange.getMaxLex());
		range.gte(lexRange.getMinLex());
		
		Limit limit = Limit.limit();
		limit.count((int)pager.getCount());
		limit.offset((int)pager.getOffset());
		Set<byte[]> values = this.operations.reverseRangeByLex(key, range, limit);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'reverseRangeByLex' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Counts the number of members in a sorted set stored at the specified key
	 * whose values fall within the specified lexicographical range.
	 * 
	 * <p>This method is particularly useful for sorted sets where all members have the same score,
	 * and the ordering is based on the lexicographical ordering of the member values.
	 * If the key does not exist, zero is returned.
	 * 
	 * <p>Unlike the range methods, this method only returns the count of matching members
	 * without retrieving the actual members, making it more efficient when only the count is needed.
	 *
	 * @param key The key of the sorted set
	 * @param lexRange The range object specifying the minimum and maximum lexicographical values
	 * @return The number of members in the sorted set whose values fall within the specified lexicographical range
	 * @see ZSetOperations#lexCount(Object, Range)
	 */
	@Override
	public Long countByLex(String key, LexRange lexRange) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'countByLex' operation executing: countByLex(key:{},min:{},max:{})", key,lexRange.getMinLex(),lexRange.getMaxLex());
		}
		Range range = Range.range();
		range.lte(lexRange.getMaxLex());
		range.gte(lexRange.getMinLex());
		Long results = this.operations.lexCount(key, range);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByLex' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Removes members from a sorted set stored at the specified key whose values
	 * fall within the specified lexicographical range.
	 * 
	 * <p>This method is particularly useful for sorted sets where all members have the same score,
	 * and the ordering is based on the lexicographical ordering of the member values.
	 * If the key does not exist, zero is returned.
	 * 
	 * <p>After the operation, members with values in the specified lexicographical range
	 * will no longer exist in the sorted set.
	 *
	 * @param key The key of the sorted set
	 * @param lexRange The range object specifying the minimum and maximum lexicographical values of members to remove
	 * @return The number of members removed from the sorted set
	 * @see ZSetOperations#removeRangeByLex(Object, Range)
	 */
	@Override
	public Long removeRangeByLex(String key, LexRange lexRange) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'removeRangeByLex' operation executing: removeRangeByLex(key:{},min:{},max:{})", key,lexRange.getMinLex(),lexRange.getMaxLex());
		}
		Range range = Range.range();
		range.lte(lexRange.getMaxLex());
		range.gte(lexRange.getMinLex());
		Long results = this.operations.removeRangeByLex(key, range);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'rangeByLex' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Returns the difference between the first sorted set and all the other sorted sets.
	 * 
	 * <p>This operation returns members from the first sorted set that do not exist in any
	 * of the other specified sorted sets. The resulting set contains only members that are
	 * unique to the first set when compared to all other sets.
	 * 
	 * <p>If a key does not exist, it is considered an empty set. If the first key does not exist,
	 * the result will be an empty set.
	 *
	 * @param referenceKey The key of the first sorted set
	 * @param comparisonKeys Collection of keys of other sorted sets to compare against
	 * @param valueType The type of values stored in the sorted sets, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of deserialized objects representing members that exist in the first set but not in any of the other sets
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#difference(Object, Collection)
	 */
	@Override
	public Set<Object> difference(
		String referenceKey, 
		Collection<String> comparisonKeys, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'difference' operation executing: difference(referenceKey:{},comparisonKeys:{})", referenceKey,comparisonKeys);
		}
		Set<byte[]> values = this.operations.difference(referenceKey, comparisonKeys);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'difference' operation returned {} ", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Returns the difference between the first sorted set and all the other sorted sets,
	 * including the scores of the resulting members.
	 * 
	 * <p>This operation returns members from the first sorted set that do not exist in any
	 * of the other specified sorted sets, along with their scores. The resulting set contains
	 * only members that are unique to the first set when compared to all other sets.
	 * 
	 * <p>If a key does not exist, it is considered an empty set. If the first key does not exist,
	 * the result will be an empty set.
	 *
	 * @param referenceKey The key of the first sorted set
	 * @param comparisonKeys Collection of keys of other sorted sets to compare against
	 * @param valueType The type of values stored in the sorted sets, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing members that exist in the first set but not in any of the other sets, along with their scores
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#differenceWithScores(Object, Collection)
	 */
	@Override
	public Set<ZSetEntry> differenceWithScores(
		String referenceKey, 
		Collection<String> comparisonKeys, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'differenceWithScores' operation executing: differenceWithScores(referenceKey:{},comparisonKeys:{})", referenceKey,comparisonKeys);
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.differenceWithScores(referenceKey, comparisonKeys);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'differenceWithScores' operation returned {} ", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Computes the difference between the first sorted set and all the other sorted sets,
	 * and stores the result in a new sorted set at the destination key.
	 * 
	 * <p>This operation calculates members from the first sorted set that do not exist in any
	 * of the other specified sorted sets, and stores these members along with their scores
	 * in a new sorted set at the destination key. If the destination key already exists,
	 * it is overwritten.
	 * 
	 * <p>If a key does not exist, it is considered an empty set. If the first key does not exist,
	 * an empty set will be stored at the destination.
	 *
	 * @param referenceKey The key of the first sorted set
	 * @param comparisonKeys Collection of keys of other sorted sets to compare against
	 * @param storeTo The key where the resulting sorted set will be stored
	 * @return The number of members in the resulting sorted set stored at the destination key
	 * @see ZSetOperations#differenceAndStore(Object, Object, Collection)
	 */
	@Override
	public Long differenceAndStore(String referenceKey, Collection<String> comparisonKeys, String storeTo) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'differenceAndStore' operation executing: differenceAndStore(referenceKey:{},comparisonKeys:{},storeTo:{})", referenceKey,comparisonKeys,storeTo);
		}
		Long results = this.operations.differenceAndStore(referenceKey, comparisonKeys, storeTo);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'differenceAndStore' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Removes one or more members from a sorted set stored at the specified key.
	 * 
	 * <p>This method removes the specified members from the sorted set. If a member does not
	 * exist in the set, it is ignored. If the key does not exist, it is treated as an empty
	 * sorted set and 0 is returned.
	 * 
	 * <p>This method serializes each value according to the specified value type before
	 * attempting to remove it from the sorted set. The operation is atomic, meaning all
	 * specified members are removed in a single operation.
	 *
	 * @param key The key of the sorted set
	 * @param valueType The type of the values to be removed, used for serialization
	 * @param values One or more members to remove from the sorted set
	 * @return The number of members that were removed from the sorted set
	 * @throws Exception If serialization fails or Redis operation encounters an error
	 * @see ZSetOperations#remove(Object, Object...)
	 */
	@Override
	public Long remove(String key, RedisValueTypeEnum valueType, Object... values) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'remove' operation executing: remove(key:{},values:{})", key, redisSerializer.serializeToJSONString(values));
		}
		Object[] newArray = new Object[values.length];
		for(int i = 0; i < values.length; i++) {
			newArray[i] = redisSerializer.serialize(values[i], valueType);
		}
		Long results = this.operations.remove(key, newArray);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'remove' operation returned {} ", results);
		}
		return results;
	}

	/**
	 * Removes and returns the member with the highest score from a sorted set stored at the specified key.
	 * 
	 * <p>This method blocks until an element becomes available or the timeout expires.
	 * If the key does not exist, null is returned after the timeout.
	 * 
	 * <p>This method deserializes the retrieved value according to the specified value
	 * type and return type before returning it as a ZSetEntry object containing both
	 * the member value and its score.
	 *
	 * @param key The key of the sorted set
	 * @param timeout The maximum time to wait for an element to become available
	 * @param timeUnit The unit of the timeout parameter
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A ZSetEntry object containing the member with the highest score and its score, or null if the operation times out
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#popMax(Object, long, TimeUnit)
	 */
	@Override
	public ZSetEntry popMaxScore(
		String key, 
		long timeout, 
		TimeUnit timeUnit, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'popMaxScore' operation executing: popMaxScore(key:{},timeout:{}ms)", key, TimeUnit.MILLISECONDS.convert(timeout, timeUnit));
		}
		TypedTuple<byte[]> typedTuple = this.operations.popMax(key, timeout, timeUnit);
		ZSetEntry results = typedTupleToZSetEntry(typedTuple, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'popMaxScore' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Removes and returns the member with the lowest score from a sorted set stored at the specified key.
	 * 
	 * <p>This method blocks until an element becomes available or the timeout expires.
	 * If the key does not exist, null is returned after the timeout.
	 * 
	 * <p>This method deserializes the retrieved value according to the specified value
	 * type and return type before returning it as a ZSetEntry object containing both
	 * the member value and its score.
	 *
	 * @param key The key of the sorted set
	 * @param timeout The maximum time to wait for an element to become available
	 * @param timeUnit The unit of the timeout parameter
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A ZSetEntry object containing the member with the lowest score and its score, or null if the operation times out
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#popMin(Object, long, TimeUnit)
	 */
	@Override
	public ZSetEntry popMinScore(
		String key, 
		long timeout, 
		TimeUnit timeUnit, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'popMinScore' operation executing: popMinScore(key:{},timeout:{}ms)", key, TimeUnit.MILLISECONDS.convert(timeout, timeUnit));
		}
		TypedTuple<byte[]> typedTuple = this.operations.popMin(key, timeout, timeUnit);
		ZSetEntry results = typedTupleToZSetEntry(typedTuple, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'popMinScore' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Removes and returns multiple members with the lowest scores from a sorted set stored at the specified key.
	 * 
	 * <p>This method removes and returns up to 'count' members with the lowest scores from the sorted set.
	 * If there are fewer than 'count' members in the sorted set, all members are returned.
	 * If the key does not exist, an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them as ZSetEntry objects, each containing
	 * a member value and its score.
	 *
	 * @param key The key of the sorted set
	 * @param count The maximum number of members to pop
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the members with the lowest scores and their scores
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#popMin(Object, long)
	 */
	@Override
	public Set<ZSetEntry> popMinScore(
		String key, 
		int count, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'popMinScore' operation executing: popMinScore(key:{},count:{})", key, count);
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.popMin(key, count);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'popMinScore' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Removes and returns multiple members with the highest scores from a sorted set stored at the specified key.
	 * 
	 * <p>This method removes and returns up to 'count' members with the highest scores from the sorted set.
	 * If there are fewer than 'count' members in the sorted set, all members are returned.
	 * If the key does not exist, an empty set is returned.
	 * 
	 * <p>This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them as ZSetEntry objects, each containing
	 * a member value and its score.
	 *
	 * @param key The key of the sorted set
	 * @param count The maximum number of members to pop
	 * @param valueType The type of values stored in the sorted set, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the members with the highest scores and their scores
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#popMax(Object, long)
	 */
	@Override
	public Set<ZSetEntry> popMaxScore(
		String key, 
		int count, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'popMaxScore' operation executing: popMaxScore(key:{},count:{})", key, count);
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.popMax(key, count);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'popMaxScore' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Computes the union of multiple sorted sets and returns the resulting members with their scores.
	 * 
	 * <p>This operation combines the members from the reference sorted set and all
	 * comparison sorted sets. The resulting set contains all unique elements that appear
	 * in any of the input sorted sets, along with their computed scores.
	 * 
	 * <p>This method allows specifying an aggregate function (SUM, MIN, MAX) to determine how
	 * the scores of members present in multiple sets are combined, as well as weights to
	 * multiply the scores in each input sorted set before aggregation.
	 *
	 * @param referenceKey The key of the reference sorted set
	 * @param comparisonKeys A collection of keys for the comparison sorted sets
	 * @param operator The aggregate function to use for score combination (SUM, MIN, MAX)
	 * @param weights An array of weights to apply to the scores in each input sorted set
	 * @param valueType The type of values stored in the sorted sets, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the members and their computed scores from the union
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#unionWithScores(Object, Collection, org.springframework.data.redis.connection.RedisZSetCommands.Aggregate, Weights)
	 */
	@Override
	public Set<ZSetEntry> unionWithScores(
		String referenceKey, 
		Collection<String> comparisonKeys, 
		Operator operator,
		double[] weights, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis zset 'unionWithScores' operation executing: unionWithScores(referenceKey:{},comparisonKeys:{},weights:{},operator:{})", 
				referenceKey,
				comparisonKeys,
				redisSerializer.serializeToJSONString(weights),
				operator
			);
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.unionWithScores(
			referenceKey, 
			comparisonKeys, 
			OrangeAggregateMapSpringAggregateEnum.getByOrangeAggregateOperator(operator),
			Weights.of(weights)
		);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'unionWithScores' operation returned {} ", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Computes the union of multiple sorted sets and returns the resulting members with their scores.
	 * 
	 * <p>This operation combines the members from the reference sorted set and all
	 * comparison sorted sets. The resulting set contains all unique elements that appear
	 * in any of the input sorted sets, along with their computed scores.
	 * 
	 * <p>For members that exist in multiple sets, their scores are summed by default.
	 * This is equivalent to calling the overloaded method with SUM as the operator
	 * and equal weights for all input sorted sets.
	 *
	 * @param referenceKey The key of the reference sorted set
	 * @param comparisonKeys A collection of keys for the comparison sorted sets
	 * @param valueType The type of values stored in the sorted sets, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the members and their computed scores from the union
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#unionWithScores(Object, Collection)
	 */
	@Override
	public Set<ZSetEntry> unionWithScores(
		String referenceKey, 
		Collection<String> comparisonKeys, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'unionWithScores' operation executing: unionWithScores(referenceKey:{},comparisonKeys:{})", referenceKey,comparisonKeys);
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.unionWithScores(referenceKey, comparisonKeys);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'unionWithScores' operation returned {} ", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Computes the union of multiple sorted sets and returns the resulting members.
	 * 
	 * <p>This operation combines the members from the reference sorted set and all
	 * comparison sorted sets. The resulting set contains all elements that appear
	 * in at least one of the input sorted sets.
	 * 
	 * <p>For members that exist in multiple sets, their scores are summed by default.
	 * This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them.
	 *
	 * @param referenceKey The key of the reference sorted set
	 * @param comparisonKeys A collection of keys for the comparison sorted sets
	 * @param valueType The type of values stored in the sorted sets, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of deserialized objects representing the members in the union of all specified sorted sets
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#union(Object, Collection)
	 */
	@Override
	public Set<Object> union(
			String referenceKey, 
			Collection<String> comparisonKeys, 
			RedisValueTypeEnum valueType, 
			Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'union' operation executing: union(referenceKey:{},comparisonKeys:{})", referenceKey,comparisonKeys);
		}
		Set<byte[]> values = this.operations.union(referenceKey, comparisonKeys);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'union' operation returned {} ", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Computes the union of multiple sorted sets and stores the result in a new key.
	 * 
	 * <p>This operation combines the members from the reference sorted set and all
	 * comparison sorted sets, and stores the result in the destination key. The resulting set
	 * contains all unique elements that appear in any of the input sorted sets.
	 * 
	 * <p>For members that exist in multiple sets, their scores are summed by default.
	 * This is equivalent to calling the overloaded method with SUM as the operator
	 * and equal weights for all input sorted sets.
	 *
	 * @param referenceKey The key of the reference sorted set
	 * @param comparisonKeys A collection of keys for the comparison sorted sets
	 * @param storeTo The key where the resulting sorted set will be stored
	 * @return The number of members in the resulting sorted set
	 * @see ZSetOperations#unionAndStore(Object, Collection, Object)
	 */
	@Override
	public Long unionAndStore(String referenceKey, Collection<String> comparisonKeys, String storeTo) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'unionAndStore' operation executing: union(referenceKey:{},comparisonKeys:{},storeTo:{})", referenceKey,comparisonKeys,storeTo);
		}
		Long results = this.operations.unionAndStore(referenceKey, comparisonKeys, storeTo);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'unionAndStore' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Computes the union of multiple sorted sets and stores the result in a new key.
	 * 
	 * <p>This operation combines the members from the reference sorted set and all
	 * comparison sorted sets, and stores the result in the destination key. The resulting set
	 * contains all unique elements that appear in any of the input sorted sets.
	 * 
	 * <p>This method allows specifying an aggregate function (SUM, MIN, MAX) to determine how
	 * the scores of members present in multiple sets are combined, as well as weights to
	 * multiply the scores in each input sorted set before aggregation.
	 *
	 * @param referenceKey The key of the reference sorted set
	 * @param comparisonKeys A collection of keys for the comparison sorted sets
	 * @param storeTo The key where the resulting sorted set will be stored
	 * @param operator The aggregate function to use for score combination (SUM, MIN, MAX)
	 * @param weights An array of weights to apply to the scores in each input sorted set
	 * @return The number of members in the resulting sorted set
	 * @throws Exception If Redis operation encounters an error
	 * @see ZSetOperations#unionAndStore(Object, Collection, Object, org.springframework.data.redis.connection.RedisZSetCommands.Aggregate, Weights)
	 */
	@Override
	public Long unionAndStore(
		String referenceKey, 
		Collection<String> comparisonKeys, 
		String storeTo, 
		Operator operator,
		double[] weights
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis zset 'unionAndStore' operation executing: unionAndStore(referenceKey:{},comparisonKeys:{},storeTo:{},weights:{},operator:{})", 
				referenceKey,
				comparisonKeys,
				storeTo,
				redisSerializer.serializeToJSONString(weights),
				operator
			);
		}
		Long results = this.operations.unionAndStore(
				referenceKey, 
				comparisonKeys, 
				storeTo,
				OrangeAggregateMapSpringAggregateEnum.getByOrangeAggregateOperator(operator),
				Weights.of(weights)
		);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'unionAndStore' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Computes the intersection of multiple sorted sets and returns the resulting members with their scores.
	 * 
	 * <p>This operation finds the members that exist in the reference sorted set and all
	 * comparison sorted sets. The resulting set contains only elements that appear in all
	 * of the input sorted sets, along with their computed scores.
	 * 
	 * <p>This method allows specifying an aggregate function (SUM, MIN, MAX) to determine how
	 * the scores of members present in multiple sets are combined, as well as weights to
	 * multiply the scores in each input sorted set before aggregation.
	 *
	 * @param referenceKey The key of the reference sorted set
	 * @param comparisonKeys A collection of keys for the comparison sorted sets
	 * @param operator The aggregate function to use for score combination (SUM, MIN, MAX)
	 * @param weights An array of weights to apply to the scores in each input sorted set
	 * @param valueType The type of values stored in the sorted sets, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the members and their computed scores from the intersection
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#intersectWithScores(Object, Collection, org.springframework.data.redis.connection.RedisZSetCommands.Aggregate, Weights)
	 */
	@Override
	public Set<ZSetEntry> intersectWithScores(
		String referenceKey, 
		Collection<String> comparisonKeys, 
		Operator operator,
		double[] weights, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug(
				"Redis zset 'intersectWithScores' operation executing: intersectWithScores(referenceKey:{},comparisonKeys:{},weights:{},operator:{})", 
				referenceKey,
				comparisonKeys,
				redisSerializer.serializeToJSONString(weights),
				operator
			);
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.intersectWithScores(
				referenceKey, 
				comparisonKeys,
				OrangeAggregateMapSpringAggregateEnum.getByOrangeAggregateOperator(operator),
				Weights.of(weights)
		);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'intersectWithScores' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Computes the intersection of multiple sorted sets and returns the resulting members with their scores.
	 * 
	 * <p>This operation finds the members that exist in the reference sorted set and all
	 * comparison sorted sets. The resulting set contains only elements that appear in all
	 * of the input sorted sets, along with their computed scores.
	 * 
	 * <p>For members that exist in multiple sets, their scores are summed by default.
	 * This is equivalent to calling the overloaded method with SUM as the operator
	 * and equal weights for all input sorted sets.
	 *
	 * @param referenceKey The key of the reference sorted set
	 * @param comparisonKeys A collection of keys for the comparison sorted sets
	 * @param valueType The type of values stored in the sorted sets, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of ZSetEntry objects containing the members and their computed scores from the intersection
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#intersectWithScores(Object, Collection)
	 */
	@Override
	public Set<ZSetEntry> intersectWithScores(
		String referenceKey, 
		Collection<String> comparisonKeys, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'intersectWithScores' operation executing: intersectWithScores(referenceKey:{},comparisonKeys:{})", referenceKey,comparisonKeys);
		}
		Set<TypedTuple<byte[]>> typedTuples = this.operations.intersectWithScores(referenceKey, comparisonKeys);
		Set<ZSetEntry> results = typedTuplesToZSetEntries(typedTuples, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'intersectWithScores' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Computes the intersection of multiple sorted sets and returns the resulting members.
	 * 
	 * <p>This operation finds the members that exist in the reference sorted set and all
	 * comparison sorted sets. The resulting set contains only elements that appear
	 * in all of the input sorted sets.
	 * 
	 * <p>For members that exist in multiple sets, their scores are summed by default.
	 * This method deserializes the retrieved values according to the specified value
	 * type and return type before returning them.
	 *
	 * @param referenceKey The key of the reference sorted set
	 * @param comparisonKeys A collection of keys for the comparison sorted sets
	 * @param valueType The type of values stored in the sorted sets, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A set of deserialized objects representing the members in the intersection of all specified sorted sets
	 * @throws Exception If deserialization fails or Redis operation encounters an error
	 * @see ZSetOperations#intersect(Object, Collection)
	 */
	@Override
	public Set<Object> intersect(
		String referenceKey, 
		Collection<String> comparisonKeys, 
		RedisValueTypeEnum valueType, 
		Type returnType
	) throws Exception {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'intersect' operation executing: intersect(referenceKey:{},comparisonKeys:{})", referenceKey,comparisonKeys);
		}
		Set<byte[]> values = this.operations.intersect(referenceKey, comparisonKeys);
		Set<Object> results = redisSerializer.deserialize(values, valueType, returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'intersect' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Computes the intersection of multiple sorted sets and stores the result in a new key.
	 * 
	 * <p>This operation finds the members that exist in the reference sorted set and all
	 * comparison sorted sets, and stores the result in the destination key. The resulting set
	 * contains only elements that appear in all of the input sorted sets.
	 * 
	 * <p>This method allows specifying an aggregate function (SUM, MIN, MAX) to determine how
	 * the scores of members present in multiple sets are combined, as well as weights to
	 * multiply the scores in each input sorted set before aggregation.
	 *
	 * @param referenceKey The key of the reference sorted set
	 * @param comparisonKeys A collection of keys for the comparison sorted sets
	 * @param storeTo The key where the resulting sorted set will be stored
	 * @param operator The aggregate function to use for score combination (SUM, MIN, MAX)
	 * @param weights An array of weights to apply to the scores in each input sorted set
	 * @return The number of members in the resulting sorted set
	 * @see ZSetOperations#intersectAndStore(Object, Collection, Object, org.springframework.data.redis.connection.RedisZSetCommands.Aggregate, Weights)
	 */
	@Override
	public Long intersectAndStore(
		String referenceKey, 
		Collection<String> comparisonKeys, 
		String storeTo,
		Operator operator,
		double[] weights
	) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'intersectAndStore' operation executing: intersectAndStore(referenceKey:{},comparisonKeys:{},storeTo:{})", referenceKey,comparisonKeys,storeTo);
		}
		Long results = this.operations.intersectAndStore(
			referenceKey, 
			comparisonKeys,
			storeTo,
			OrangeAggregateMapSpringAggregateEnum.getByOrangeAggregateOperator(operator),
			Weights.of(weights)
		);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'intersectAndStore' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Computes the intersection of multiple sorted sets and stores the result in a new key.
	 * 
	 * <p>This operation finds the members that exist in the reference sorted set and all
	 * comparison sorted sets, and stores the result in the destination key. The resulting set
	 * contains only elements that appear in all of the input sorted sets.
	 * 
	 * <p>For members that exist in multiple sets, their scores are summed by default.
	 * This is equivalent to calling the overloaded method with SUM as the operator
	 * and equal weights for all input sorted sets.
	 *
	 * @param referenceKey The key of the reference sorted set
	 * @param comparisonKeys A collection of keys for the comparison sorted sets
	 * @param storeTo The key where the resulting sorted set will be stored
	 * @return The number of members in the resulting sorted set
	 * @see ZSetOperations#intersectAndStore(Object, Collection, Object)
	 */
	@Override
	public Long intersectAndStore(String referenceKey, Collection<String> comparisonKeys, String storeTo) {
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'intersectAndStore' operation executing: intersectAndStore(referenceKey:{},comparisonKeys:{},storeTo:{})", referenceKey,comparisonKeys,storeTo);
		}
		Long results = this.operations.intersectAndStore(referenceKey, comparisonKeys, storeTo);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis zset 'intersectAndStore' operation returned {}", results);
		}
		return results;
	}

}