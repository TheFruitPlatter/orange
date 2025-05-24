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
 * Example interface demonstrating cross-set operations between Redis sorted sets.
 * 
 * <p>Provides operations to combine multiple sorted sets in different ways:
 * <ul>
 *   <li>{@code DIFFERENCE} - Elements present in first set but not others</li>
 *   <li>{@code UNION} - All unique elements from all sets</li>
 *   <li>{@code INTERSECT} - Only elements present in all sets</li>
 * </ul>
 * 
 * <p>Each operation supports multiple variants:
 * <ul>
 *   <li>Basic value retrieval</li>
 *   <li>Values with scores (using {@code @WithScores})</li>
 *   <li>Storing results to another set (using {@code @StoreTo})</li>
 *   <li>Weighted operations (using {@code @Weights})</li>
 *   <li>Score aggregation (using {@code @Aggregate})</li>
 * </ul>
 * 
 * <p>Key configuration:
 * <ul>
 *   <li>Operates on sets defined by {@code @CrossOperationKeys}</li>
 *   <li>Supports static weights (annotation) and dynamic weights (parameter)</li>
 *   <li>Aggregation operators: SUM, MIN, MAX</li>
 * </ul>
 * 
 * <p>Example usage:
 * <pre>{@code
 * // Get difference between two sets (values only)
 * Set<ZSetValue> diff = api.difference();
 * 
 * // Get union with scores and SUM aggregation
 * Set<OrangeZSetExampleEntity> union = api.unionWithScoresAndAggregate();
 * 
 * // Calculate intersection with dynamic weights and store result
 * List<Double> weights = Arrays.asList(1.5, 0.5);
 * Long storedCount = api.intersectAndStoreByDynamicWeights(weights);
 * }</pre>
 * 
 * <p>Performance notes:
 * <ul>
 *   <li>Operations have O(N) complexity where N is total elements</li>
 *   <li>Storing results avoids client-server transfer for large sets</li>
 *   <li>Weights are applied during server-side processing</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see CrossOperationKeys Specifies which sets to operate on
 * @see WithScores Includes scores in results
 * @see StoreTo Stores operation results to another set
 * @see Weights Applies weights to set scores
 * @see Aggregate Configures score aggregation
 * @see OrangeRedisZSetExample5Api Example sorted set 1
 * @see OrangeRedisZSetExample6Api Example sorted set 2
 * @see OrangeRedisZSetExample7Api Union storage target
 * @see OrangeRedisZSetExample8Api Intersect storage target
 */
@OrangeRedisZSetCrossKeyClient
public interface OrangeRedisCrossExample1Api {
	
	/**
	 * Calculates the difference between two sorted sets.
	 * 
	 * <p>Returns elements that exist in the first set but not in the second set.
	 * Scores are not included in the result.
	 * 
	 * <p>Operation: ZDIFF key1 key2
	 * 
	 * @return Set of values that exist only in the first set
	 * @see OrangeRedisZSetExample5Api First set
	 * @see OrangeRedisZSetExample6Api Second set
	 */
	@Difference
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	Set<ZSetValue> difference();
	
	/**
	 * Calculates the difference between two sorted sets, including scores.
	 * 
	 * <p>Returns elements that exist in the first set but not in the second set,
	 * along with their scores from the first set.
	 * 
	 * <p>Operation: ZDIFF key1 key2 WITHSCORES
	 * 
	 * @return Set of value-score pairs that exist only in the first set
	 * @see OrangeRedisZSetExample5Api First set
	 * @see OrangeRedisZSetExample6Api Second set
	 */
	@Difference
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	Set<OrangeZSetExampleEntity> differenceWithScores();
	
	/**
	 * Calculates the difference between two sorted sets and stores the result.
	 * 
	 * <p>Computes elements that exist in the first set but not in the second set,
	 * then stores the result in a destination sorted set. Original scores are preserved.
	 * 
	 * <p>Operation: ZDIFFSTORE destination key1 key2
	 * 
	 * @return Number of elements in the resulting set
	 * @see OrangeRedisZSetExample5Api First set
	 * @see OrangeRedisZSetExample6Api Second set (and destination)
	 */
	@Difference
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@StoreTo(OrangeRedisZSetExample6Api.class)
	Long differenceAndStore();
	
	/**
	 * Calculates the union of two sorted sets.
	 * 
	 * <p>Returns all unique elements that exist in either of the sets.
	 * Scores are not included in the result.
	 * 
	 * <p>Operation: ZUNION key1 key2
	 * 
	 * @return Set of all unique values from both sets
	 * @see OrangeRedisZSetExample5Api First set
	 * @see OrangeRedisZSetExample6Api Second set
	 */
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	Set<ZSetValue> union();
	
	/**
	 * Calculates the union of two sorted sets, including scores.
	 * 
	 * <p>Returns all unique elements that exist in either of the sets,
	 * along with their aggregated scores (default SUM).
	 * 
	 * <p>Operation: ZUNION key1 key2 WITHSCORES
	 * 
	 * @return Set of value-score pairs from the union of both sets
	 * @see OrangeRedisZSetExample5Api First set
	 * @see OrangeRedisZSetExample6Api Second set
	 */
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	Set<OrangeZSetExampleEntity> unionWithScores();
	
	/**
	 * Calculates the union of two sorted sets with weighted scores and SUM aggregation.
	 * 
	 * <p>Returns all unique elements with scores calculated as:
	 * <pre>score = (score1 * 1.0) + (score2 * 2.0)</pre>
	 * 
	 * <p>Operation: ZUNION 2 key1 key2 WEIGHTS 1 2 AGGREGATE SUM WITHSCORES
	 * 
	 * @return Set of value-score pairs with weighted and aggregated scores
	 * @see OrangeRedisZSetExample5Api First set (weight 1.0)
	 * @see OrangeRedisZSetExample6Api Second set (weight 2.0)
	 */
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	@Aggregate(operator = Operator.SUM, weights = {1.0,2.0})
	Set<OrangeZSetExampleEntity> unionWithScoresAndAggregate();
	
	/**
	 * Calculates the union of two sorted sets with dynamic weights and SUM aggregation.
	 * 
	 * <p>Returns all unique elements with scores calculated using provided weights:
	 * <pre>score = (score1 * weights[0]) + (score2 * weights[1])</pre>
	 * 
	 * <p>Operation: ZUNION 2 key1 key2 WEIGHTS [dynamic] AGGREGATE SUM WITHSCORES
	 * 
	 * @param weights List of weights to apply to each set's scores (must contain 2 values)
	 * @return Set of value-score pairs with weighted and aggregated scores
	 * @see OrangeRedisZSetExample5Api First set (weight from weights[0])
	 * @see OrangeRedisZSetExample6Api Second set (weight from weights[1])
	 */
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	@Aggregate(operator = Operator.SUM, weights = {1.0,1.0})
	Set<OrangeZSetExampleEntity> unionWithScoresByDynamicWeights(@Weights List<Double> weights);
	
	/**
	 * Calculates the union of two sorted sets and stores the result.
	 * 
	 * <p>Computes all unique elements from both sets and stores them in a destination set.
	 * Scores are aggregated using the default SUM operator.
	 * 
	 * <p>Operation: ZUNIONSTORE destination 2 key1 key2
	 * 
	 * @return Number of elements in the resulting set
	 * @see OrangeRedisZSetExample5Api First set
	 * @see OrangeRedisZSetExample6Api Second set
	 * @see OrangeRedisZSetExample7Api Destination set
	 */
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@StoreTo(OrangeRedisZSetExample7Api.class)
	Long unionAndStore();
	
	/**
	 * Calculates the union with weighted scores, aggregates them, and stores the result.
	 * 
	 * <p>Computes all unique elements with scores calculated as:
	 * <pre>score = (score1 * 1.0) + (score2 * 2.0)</pre>
	 * 
	 * <p>Operation: ZUNIONSTORE destination 2 key1 key2 WEIGHTS 1 2 AGGREGATE SUM
	 * 
	 * @return Number of elements in the resulting set
	 * @see OrangeRedisZSetExample5Api First set (weight 1.0)
	 * @see OrangeRedisZSetExample6Api Second set (weight 2.0)
	 * @see OrangeRedisZSetExample7Api Destination set
	 */
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@Aggregate(operator = Operator.SUM, weights = {1.0,2.0})
	@StoreTo(OrangeRedisZSetExample7Api.class)
	Long unionAndAggregateAndStore();
	
	/**
	 * Calculates the union with dynamic weights, aggregates them, and stores the result.
	 * 
	 * <p>Computes all unique elements with scores calculated using provided weights:
	 * <pre>score = (score1 * weights[0]) + (score2 * weights[1])</pre>
	 * 
	 * <p>Operation: ZUNIONSTORE destination 2 key1 key2 WEIGHTS [dynamic] AGGREGATE SUM
	 * 
	 * @param weights List of weights to apply to each set's scores (must contain 2 values)
	 * @return Number of elements in the resulting set
	 * @see OrangeRedisZSetExample5Api First set (weight from weights[0])
	 * @see OrangeRedisZSetExample6Api Second set (weight from weights[1])
	 * @see OrangeRedisZSetExample7Api Destination set
	 */
	@Union
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@Aggregate(operator = Operator.SUM, weights = {1.0,1.0})
	@StoreTo(OrangeRedisZSetExample7Api.class)
	Long unionAndStoreByDynamicWeights(@Weights List<Double> weights);
	
	/**
	 * Calculates the intersection of two sorted sets.
	 * 
	 * <p>Returns elements that exist in both sets.
	 * Scores are not included in the result.
	 * 
	 * <p>Operation: ZINTER key1 key2
	 * 
	 * @return Set of values that exist in both sets
	 * @see OrangeRedisZSetExample5Api First set
	 * @see OrangeRedisZSetExample6Api Second set
	 */
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	Set<ZSetValue> intersect();
	
	/**
	 * Calculates the intersection of two sorted sets, including scores.
	 * 
	 * <p>Returns elements that exist in both sets,
	 * along with their aggregated scores (default SUM).
	 * 
	 * <p>Operation: ZINTER key1 key2 WITHSCORES
	 * 
	 * @return Set of value-score pairs from the intersection of both sets
	 * @see OrangeRedisZSetExample5Api First set
	 * @see OrangeRedisZSetExample6Api Second set
	 */
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	Set<OrangeZSetExampleEntity> intersectWithScores();
	
	/**
	 * Calculates the intersection of two sorted sets with weighted scores and SUM aggregation.
	 * 
	 * <p>Returns elements that exist in both sets with scores calculated as:
	 * <pre>score = (score1 * 1.0) + (score2 * 2.0)</pre>
	 * 
	 * <p>Operation: ZINTER 2 key1 key2 WEIGHTS 1 2 AGGREGATE SUM WITHSCORES
	 * 
	 * @return Set of value-score pairs with weighted and aggregated scores
	 * @see OrangeRedisZSetExample5Api First set (weight 1.0)
	 * @see OrangeRedisZSetExample6Api Second set (weight 2.0)
	 */
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	@Aggregate(operator = Operator.SUM, weights = {1.0,2.0})
	Set<OrangeZSetExampleEntity> intersectWithScoresAndAggregate();
	
	/**
	 * Calculates the intersection of two sorted sets with dynamic weights and SUM aggregation.
	 * 
	 * <p>Returns elements that exist in both sets with scores calculated using provided weights:
	 * <pre>score = (score1 * weights[0]) + (score2 * weights[1])</pre>
	 * 
	 * <p>Operation: ZINTER 2 key1 key2 WEIGHTS [dynamic] AGGREGATE SUM WITHSCORES
	 * 
	 * @param weights List of weights to apply to each set's scores (must contain 2 values)
	 * @return Set of value-score pairs with weighted and aggregated scores
	 * @see OrangeRedisZSetExample5Api First set (weight from weights[0])
	 * @see OrangeRedisZSetExample6Api Second set (weight from weights[1])
	 */
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@WithScores
	@Aggregate(operator = Operator.SUM, weights = {1.0,1.0})
	Set<OrangeZSetExampleEntity> intersectWithScoresByDynamicWeights(@Weights List<Double> weights);
	
	/**
	 * Calculates the intersection of two sorted sets and stores the result.
	 * 
	 * <p>Computes elements that exist in both sets and stores them in a destination set.
	 * Scores are aggregated using the default SUM operator.
	 * 
	 * <p>Operation: ZINTERSTORE destination 2 key1 key2
	 * 
	 * @return Number of elements in the resulting set
	 * @see OrangeRedisZSetExample5Api First set
	 * @see OrangeRedisZSetExample6Api Second set
	 * @see OrangeRedisZSetExample8Api Destination set
	 */
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@StoreTo(OrangeRedisZSetExample8Api.class)
	Long intersectAndStore();
	
	/**
	 * Calculates the intersection with weighted scores, SUM aggregation, and stores the result.
	 * 
	 * <p>Computes elements that exist in both sets with scores calculated as:
	 * <pre>score = (score1 * 1.0) + (score2 * 2.0)</pre>
	 * 
	 * <p>Operation: ZINTERSTORE destination 2 key1 key2 WEIGHTS 1 2 AGGREGATE SUM
	 * 
	 * @return Number of elements in the resulting set
	 * @see OrangeRedisZSetExample5Api First set (weight 1.0)
	 * @see OrangeRedisZSetExample6Api Second set (weight 2.0)
	 * @see OrangeRedisZSetExample8Api Destination set
	 */
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@Aggregate(operator = Operator.SUM, weights = {1.0,2.0})
	@StoreTo(OrangeRedisZSetExample8Api.class)
	Long intersectAndAggregateAndStore();
	
	/**
	 * Calculates the intersection with dynamic weights, SUM aggregation, and stores the result.
	 * 
	 * <p>Computes elements that exist in both sets with scores calculated using provided weights:
	 * <pre>score = (score1 * weights[0]) + (score2 * weights[1])</pre>
	 * 
	 * <p>Operation: ZINTERSTORE destination 2 key1 key2 WEIGHTS [dynamic] AGGREGATE SUM
	 * 
	 * @param weights List of weights to apply to each set's scores (must contain 2 values)
	 * @return Number of elements in the resulting set
	 * @see OrangeRedisZSetExample5Api First set (weight from weights[0])
	 * @see OrangeRedisZSetExample6Api Second set (weight from weights[1])
	 * @see OrangeRedisZSetExample8Api Destination set
	 */
	@Intersect
	@CrossOperationKeys({OrangeRedisZSetExample5Api.class,OrangeRedisZSetExample6Api.class})
	@Aggregate(operator = Operator.SUM, weights = {1.0,1.0})
	@StoreTo(OrangeRedisZSetExample8Api.class)
	Long intersectAndStoreByDynamicWeights(@Weights List<Double> weights);
}