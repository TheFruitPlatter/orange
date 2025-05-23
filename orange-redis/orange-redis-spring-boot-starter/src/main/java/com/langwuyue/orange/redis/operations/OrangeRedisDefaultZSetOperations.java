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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisDefaultZSetOperations extends OrangeRedisAbstractOperations implements OrangeRedisZSetOperations {

	private ZSetOperations<String, byte[]> operations;
	
	private OrangeRedisSerializer redisSerializer;
	
	private OrangeRedisLogger logger;
	
	public OrangeRedisDefaultZSetOperations(RedisTemplate<String,byte[]> template,OrangeRedisSerializer redisSerializer,OrangeRedisLogger logger) {
		super(template,logger);
		this.operations = template.opsForZSet();
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}
	
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
