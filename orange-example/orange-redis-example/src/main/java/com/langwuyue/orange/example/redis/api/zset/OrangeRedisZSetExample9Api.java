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

import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.template.zset.JSONOperationsTemplate;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:zset:example9")
public interface OrangeRedisZSetExample9Api extends JSONOperationsTemplate<OrangeValueExampleEntity> {

	@Override
	default Map<OrangeValueExampleEntity, Boolean> add(Map<OrangeValueExampleEntity, Double> members) {
		return null;
	}

	@Override
	default void addIfAsent(Map<OrangeValueExampleEntity, Double> members) {
	}

	@Override
	default OrangeValueExampleEntity randomGetValue() {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> randomGetMember() {
		return null;
	}

	@Override
	default List<OrangeValueExampleEntity> randomGetValues(Integer count) {
		return null;
	}

	@Override
	default List<Map<OrangeValueExampleEntity, Double>> randomGetMembers(Integer count) {
		return null;
	}

	@Override
	default Set<OrangeValueExampleEntity> distinctRandomGetValues(Integer count) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> distinctRandomGetMembers(Integer count) {
		return null;
	}

	@Override
	default Set<OrangeValueExampleEntity> getByScoreRange(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Set<OrangeValueExampleEntity> getByScoreRange(Double maxScore, Double minScore, Long pageNo, Long count) {
		return null;
	}

	@Override
	default Set<OrangeValueExampleEntity> getByRankRange(Long startIndex, Long endIndex) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> getByScoreRangeWithScores(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> getByScoreRangeWithScores(Double maxScore, Double minScore,
			Long pageNo, Long count) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> getByRankRangeWithScores(Long startIndex, Long endIndex) {
		return null;
	}

	@Override
	default Set<OrangeValueExampleEntity> reveseScoreRange(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Set<OrangeValueExampleEntity> reveseScoreRange(Double maxScore, Double minScore, Long pageNo, Long count) {
		return null;
	}

	@Override
	default Set<OrangeValueExampleEntity> reveseRankRange(Long startIndex, Long endIndex) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> reveseScoreRangeWithScores(Double maxScore, Double minScore) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> reveseScoreRangeWithScores(Double maxScore, Double minScore,
			Long pageNo, Long count) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> reveseRankRangeWithScores(Long startIndex, Long endIndex) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> popMax() {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> popMin() {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> popMax(Integer count) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> popMin(Integer count) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> timeLimitedPopMax(Long value, TimeUnit unit) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Double> timeLimitedPopMin(Long value, TimeUnit unit) {
		return null;
	}

	
	@Override
	default Map<OrangeValueExampleEntity, Double> getScores(Set<OrangeValueExampleEntity> value) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Boolean> remove(Set<OrangeValueExampleEntity> values) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Long> getRanks(Set<OrangeValueExampleEntity> value) {
		return null;
	}

	@Override
	default Map<OrangeValueExampleEntity, Long> reveseRanks(Set<OrangeValueExampleEntity> value) {
		return null;
	}
}