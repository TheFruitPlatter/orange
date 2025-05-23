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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisZSetOperations extends OrangeRedisOperations {

	Long add(String key, Set<ZSetEntry> members, RedisValueTypeEnum valueType) throws Exception;

	Boolean addIfAbsent(String key, ZSetEntry entry, RedisValueTypeEnum valueType) throws Exception;

	Double incrementScore(String key, Object value, double score, RedisValueTypeEnum valueType) throws Exception;

	List<Double> score(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;
	
	Long size(String key)throws Exception;

	Set<ZSetEntry> distinctRandomMembersWithScores(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Set<Object> distinctRandomMembers(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	List<ZSetEntry> randomMembersWithScores(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	List<Object> randomMembers(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Long getMemberRank(String key, Object value, RedisValueTypeEnum valueType) throws Exception;

	Long getMemberReverseRank(String key, Object value, RedisValueTypeEnum valueType) throws Exception;

	Set<Object> range(String key, RankRange rankRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Set<ZSetEntry> rangeWithScores(String key, RankRange rankRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Set<ZSetEntry> reverseRangeWithScores(String key, RankRange rankRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Set<Object> reverseRange(String key, RankRange rankRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Long removeRange(String key, RankRange rankRange)throws Exception;

	Set<ZSetEntry> rangeByScoreWithScores(String key, ScoreRange scoreRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Set<Object> rangeByScore(String key, ScoreRange scoreRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Set<ZSetEntry> reverseRangeByScoreWithScores(String key, ScoreRange scoreRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Set<Object> reverseRangeByScore(String key, ScoreRange scoreRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Set<ZSetEntry> reverseRangeByScoreWithScores(String key, ScoreRange scoreRange, Pager pager,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Set<Object> reverseRangeByScore(String key, ScoreRange scoreRange, Pager pager,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Set<ZSetEntry> rangeByScoreWithScores(String key, ScoreRange scoreRange, Pager pager,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Set<Object> rangeByScore(String key, ScoreRange scoreRange, Pager pager,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Long countByScore(String key, ScoreRange scoreRange)throws Exception;
	
	Long removeRangeByScore(String key, ScoreRange scoreRange)throws Exception;
	
	Set<Object> rangeByLex(String key, LexRange lexRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Set<Object> rangeByLex(String key, LexRange lexRange, Pager pager,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Set<Object> reverseRangeByLex(String key, LexRange lexRange,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Set<Object> reverseRangeByLex(String key, LexRange lexRange, Pager pager,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Long countByLex(String key, LexRange lexRange)throws Exception;
	
	Long removeRangeByLex(String key, LexRange lexRange)throws Exception;
	
	Set<Object> difference(String referenceKey, Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Set<ZSetEntry> differenceWithScores(String referenceKey, Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Long differenceAndStore(String referenceKey, Collection<String> comparisonKeys, String storeTo);
	
	Long remove(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;

	ZSetEntry popMaxScore(String key, long timeout, TimeUnit timeUnit,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	ZSetEntry popMinScore(String key, long timeout, TimeUnit timeUnit,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Set<ZSetEntry> popMinScore(String key, int count,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Set<ZSetEntry> popMaxScore(String key, int count, RedisValueTypeEnum valueType, Type returnType) throws Exception;
	
	Set<ZSetEntry> unionWithScores(String referenceKey, Collection<String> comparisonKeys, Operator operator,double[] weights, RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Set<ZSetEntry> unionWithScores(String referenceKey, Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Set<Object> union(String referenceKey, Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Long unionAndStore(String referenceKey, Collection<String> comparisonKeys, String storeTo) throws Exception;

	Long unionAndStore(String referenceKey, Collection<String> comparisonKeys, String storeTo, Operator operator,double[] weights) throws Exception;
	
	Set<ZSetEntry> intersectWithScores(String referenceKey, Collection<String> comparisonKeys, Operator operator,double[] weights, RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Set<ZSetEntry> intersectWithScores(String referenceKey, Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Set<Object> intersect(String referenceKey, Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Long intersectAndStore(String referenceKey, Collection<String> comparisonKeys, String storeTo, Operator operator,double[] weights) throws Exception;

	Long intersectAndStore(String referenceKey, Collection<String> comparisonKeys, String storeTo) throws Exception;
	
	public static class CASZSetEntry extends ZSetEntry {

		@OldScore
		private Double oldScore;
		
		public CASZSetEntry(Object value, Double score) {
			super(value, score);
		}

		public Double getOldScore() {
			return oldScore;
		}

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
	
	public static class ZSetEntry {
	
		@RedisValue
		private Object value;
		
		@Score
		private Double score;
		
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

		public Object getValue() {
			return value;
		}

		public Double getScore() {
			return score;
		}

	}
	
	public static class ScoreRange {
		@MaxScore
		private double maxScore;
		@MinScore
		private double minScore;
		
		public ScoreRange() {
			super();
		}
		public ScoreRange(double maxScore, double minScore) {
			super();
			this.maxScore = maxScore;
			this.minScore = minScore;
		}
		public double getMaxScore() {
			return maxScore;
		}
		public double getMinScore() {
			return minScore;
		}
	}
	
	public static class RankRange {
		@StartIndex
		private long startIndex;
		@EndIndex
		private long endIndex;
		
		public RankRange() {
			super();
		}

		public RankRange(long startIndex, long endIndex) {
			super();
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		public long getStartIndex() {
			return startIndex;
		}

		public long getEndIndex() {
			return endIndex;
		}
	}
	
	public static class LexRange {
		@MaxLex
		private String maxLex;
		@MinLex
		private String minLex;
		public LexRange(String maxLex, String minLex) {
			super();
			this.maxLex = maxLex;
			this.minLex = minLex;
		}
		public LexRange() {
			super();
		}
		public String getMaxLex() {
			return maxLex;
		}
		public String getMinLex() {
			return minLex;
		}
	}
	
	public static class Pager {
		@PageNo
		private long pageNo;
		@Count
		private long count;
		
		public Pager() {
			super();
		}

		public Pager(long pageNo, long count) {
			super();
			this.pageNo = pageNo;
			this.count = count;
		}
		
		public long getOffset() {
			return (this.pageNo - 1) * this.count;
		}


		public long getPageNo() {
			return pageNo;
		}

		public long getCount() {
			return count;
		}
	}
	
}
