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

import java.util.List;
import java.util.Set;

import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample5Api;
import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample6Api;
import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample7Api;
import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample8Api;
import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity.ZSetValue;
import com.langwuyue.orange.redis.annotation.cross.Aggregate;
import com.langwuyue.orange.redis.annotation.cross.Aggregate.Operator;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.Difference;
import com.langwuyue.orange.redis.annotation.cross.Intersect;
import com.langwuyue.orange.redis.annotation.cross.OrangeRedisZSetCrossKeyClient;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.annotation.cross.Union;
import com.langwuyue.orange.redis.annotation.cross.Weights;
import com.langwuyue.orange.redis.annotation.zset.WithScores;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisZSetCrossKeyClient
public interface OrangeRedisCrossExample1Api {
	
	@Difference
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	Set<ZSetValue> difference();
	
	@Difference
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	Set<OrangeZSetExampleEntity> differenceWithScores();
	
	@Difference
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@StoreTo(OrangeRedisZSetExample6Api.class)
	Long differenceAndStore();
	
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	Set<ZSetValue> union();
	
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	Set<OrangeZSetExampleEntity> unionWithScores();
	
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	@Aggregate(operator = Operator.SUM, weights = {1.0,2.0})
	Set<OrangeZSetExampleEntity> unionWithScoresAndAggregate();
	
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	@Aggregate(operator = Operator.SUM, weights = {1.0,1.0})
	Set<OrangeZSetExampleEntity> unionWithScoresByDynamicWeights(@Weights List<Double> weights);
	
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@StoreTo(OrangeRedisZSetExample7Api.class)
	Long unionAndStore();
	
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@Aggregate(operator = Operator.SUM, weights = {1.0,2.0})
	@StoreTo(OrangeRedisZSetExample7Api.class)
	Long unionAndAggregateAndStore();
	
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@Aggregate(operator = Operator.SUM, weights = {1.0,1.0})
	@StoreTo(OrangeRedisZSetExample7Api.class)
	Long unionAndStoreByDynamicWeights(@Weights List<Double> weights);
	
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	Set<ZSetValue> intersect();
	
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	Set<OrangeZSetExampleEntity> intersectWithScores();
	
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	@Aggregate(operator = Operator.SUM, weights = {1.0,2.0})
	Set<OrangeZSetExampleEntity> intersectWithScoresAndAggregate();
	
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	@Aggregate(operator = Operator.SUM, weights = {1.0,1.0})
	Set<OrangeZSetExampleEntity> intersectWithScoresByDynamicWeights(@Weights List<Double> weights);
	
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@StoreTo(OrangeRedisZSetExample8Api.class)
	Long intersectAndStore();
	
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@Aggregate(operator = Operator.SUM, weights = {1.0,2.0})
	@StoreTo(OrangeRedisZSetExample8Api.class)
	Long intersectAndAggregateAndStore();
	
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@Aggregate(operator = Operator.SUM, weights = {1.0,1.0})
	@StoreTo(OrangeRedisZSetExample8Api.class)
	Long intersectAndStoreByDynamicWeights(@Weights List<Double> weights);
}
