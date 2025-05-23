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
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.zset.JSONOperationsTemplate;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:zset:example7")
public interface OrangeRedisZSetExample7Api extends JSONOperationsTemplate<ZSetValue> {

	@Override
	default Map<ZSetValue, Boolean> add(Map<ZSetValue, Double> members) {
		return null;
	}

	@Override
	default void addIfAsent(Map<ZSetValue, Double> members) {
	}

	@Override
	default ZSetValue randomGetValue() {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> randomGetMember() {
		return null;
	}

	@Override
	default List<ZSetValue> randomGetValues(Integer count) {
		return null;
	}

	@Override
	default List<Map<ZSetValue, Double>> randomGetMembers(Integer count) {
		return null;
	}

	@Override
	default Set<ZSetValue> distinctRandomGetValues(Integer count) {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> distinctRandomGetMembers(Integer count) {
		return null;
	}

	@Override
	default Set<ZSetValue> getByScoreRange(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Set<ZSetValue> getByScoreRange(Double maxScore, Double minScore, Long pageNo, Long count) {
		return null;
	}

	@Override
	default Set<ZSetValue> getByRankRange(Long startIndex, Long endIndex) {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> getByScoreRangeWithScores(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> getByScoreRangeWithScores(Double maxScore, Double minScore,
			Long pageNo, Long count) {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> getByRankRangeWithScores(Long startIndex, Long endIndex) {
		return null;
	}

	@Override
	default Set<ZSetValue> reveseScoreRange(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Set<ZSetValue> reveseScoreRange(Double maxScore, Double minScore, Long pageNo, Long count) {
		return null;
	}

	@Override
	default Set<ZSetValue> reveseRankRange(Long startIndex, Long endIndex) {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> reveseScoreRangeWithScores(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> reveseScoreRangeWithScores(Double maxScore, Double minScore,
			Long pageNo, Long count) {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> reveseRankRangeWithScores(Long startIndex, Long endIndex) {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> popMax() {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> popMin() {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> popMax(Integer count) {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> popMin(Integer count) {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> timeLimitedPopMax(Long value, TimeUnit unit) {
		return null;
	}

	@Override
	default Map<ZSetValue, Double> timeLimitedPopMin(Long value, TimeUnit unit) {
		return null;
	}

	
	@Override
	default Map<ZSetValue, Double> getScores(Set<ZSetValue> value) {
		return null;
	}

	@Override
	default Map<ZSetValue, Boolean> remove(Set<ZSetValue> values) {
		return null;
	}

	@Override
	default Map<ZSetValue, Long> getRanks(Set<ZSetValue> value) {
		return null;
	}

	@Override
	default Map<ZSetValue, Long> reveseRanks(Set<ZSetValue> value) {
		return null;
	}
}