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
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Decrement;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.GetIndexs;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Increment;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.annotation.zset.GetScores;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.annotation.zset.OldScore;
import com.langwuyue.orange.redis.annotation.zset.OrangeRedisZSetClient;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate;

/**
 * Interface template for Redis ZSet operations. 
 * Developers should extend this interface and annotate the child interface with {@code OrangeRedisKey}.
 * 
 * <p>A example is:
 * <blockquote><pre>
 *  {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:zset:example1")} 
 *  public interface OrangeRedisZSetExample1Api extends JSONOperationsTemplate{@code<ZSetValue>} {
 *  
 *  }
 * </pre></blockquote>
 * 
 * 
 * <p>Please review examples for more information.
 * 
 * @param <T> The type of value stored in the Redis ZSet (will be serialized as JSON)
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisZSetClient(valueType = RedisValueTypeEnum.JSON)
public interface JSONOperationsTemplate<T> extends GlobalOperationsTemplate {

	/**
	 * Add {@code value} to a sorted set, or update its {@code score} if it already exists.
	 * 
	 * @param value 	The value.
	 * @param score 	The score.
	 * @return Boolean	True if the {@code value} and {@code score} was updated, false otherwise (e.g., if {@code value} and {@code score} is already exists).
	 */
	@AddMembers
	Boolean add(@RedisValue T value, @Score Double score);
	
	/**
	 * Add {@code members} to a sorted set, or update its {@code score} if it already exists.
	 * 
	 * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines whether the method adding remaining members to the Redis set upon exceptions.
	 * True: continue | False: break
	 * 
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @param members is a map. The keys of this map are the values of a sorted set,and the values of this map are the scores.
	 * @return a LinkedHashMap. The keys are the values; the values are the results of adding these members
	 */
	@AddMembers
	@ContinueOnFailure(true)
	Map<T, Boolean> add(@Multiple Map<T,Double> members);
	
	/**
	 * Add {@code value} to a sorted set if it does not already exists.
	 * 
	 * <p>
	 * Once the addition process is completed, the {@code OrangeRedisZSetAddMemberIfAbsentListener} component ​​will be triggered​​. 
	 * Developers should ​​configure​​ {@code OrangeRedisZSetAddMemberIfAbsentListener} to manage post-addition business logic. 
	 * Note that the {@code OrangeRedisZSetAddMemberIfAbsentListener} implementation class must be annotated with Spring’s {@code @Component}
	 * 
	 * 
	 * <p>
	 * The property {@code deleteInTheEnd} of {@code @IfAbsent} determines if the value is deleted after operation completion.
	 * True​​: Delete | ​​False​​: Keep
	 * 
	 * 
	 * <p>
	 * Please review examples for more information.
	 * 
	 * 
	 * @param value the value.
	 * @param score the score.
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	void addIfAsent(@RedisValue T value, @Score Double score);
	
	/**
	 * Add {@code value} to a sorted set if it does not already exists.
	 * 
	 * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines whether the method adding remaining members to the Redis set upon exceptions.
	 * True: continue | False: break
	 * 
	 * 
	 * <p>
	 * Once the addition process is completed, the {@code OrangeRedisZSetAddMembersIfAbsentListener} component ​​will be triggered​​. 
	 * Developers should ​​configure​​ {@code OrangeRedisZSetAddMembersIfAbsentListener} to manage post-addition business logic. 
	 * Note that the {@code OrangeRedisZSetAddMembersIfAbsentListener} implementation class must be annotated with Spring’s {@code @Component}
	 * 
	 * 
	 * <p>
	 * The property {@code deleteInTheEnd} of {@code @IfAbsent} determines if the value is deleted after operation completion.
	 * True​​: Delete | ​​False​​: Keep
	 * 
	 * 
	 * <p>
	 * Please review examples for more information.
	 * 
	 * 
	 * @param members is map. The keys of this map are the values of a sorted set,and the values of this map are the scores.
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	@ContinueOnFailure(true)
	void addIfAsent(@Multiple Map<T,Double> members);
	
	/**
	 * Add {@code value} to a sorted set if it does not already exists.
	 * 
	 * <p>
	 * Once the addition process is completed, the {@code OrangeRedisZSetAddMemberIfAbsentListener} component ​​will be triggered​​. 
	 * Developers should ​​configure​​ {@code OrangeRedisZSetAddMemberIfAbsentListener} to manage post-addition business logic. 
	 * Note that the {@code OrangeRedisZSetAddMemberIfAbsentListener} implementation class must be annotated with Spring’s {@code @Component}
	 * 
	 * 
	 * <p>
	 * The property {@code deleteInTheEnd} of {@code @IfAbsent} determines if the value is deleted after operation completion.
	 * True​​: Delete | ​​False​​: Keep
	 * 
	 * 
	 * <p>
	 * Please review examples for more information.
	 * 
	 * 
	 * @param value The value.
	 * @param score The score.
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=true)
	void acquire(@RedisValue T value, @Score Double score);
	
	/**
	 * Add {@code value} to a sorted set if it does not already exists.
	 * 
	 * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines whether the method adding remaining members to the Redis set upon exceptions.
	 * True: continue | False: break
	 * 
	 * 
	 * <p>
	 * Once the addition process is completed, the {@code OrangeRedisZSetAddMembersIfAbsentListener} component ​​will be triggered​​. 
	 * Developers should ​​configure​​ {@code OrangeRedisZSetAddMembersIfAbsentListener} to manage post-addition business logic. 
	 * Note that the {@code OrangeRedisZSetAddMembersIfAbsentListener} implementation class must be annotated with Spring’s {@code @Component}
	 * 
	 * 
	 * <p>
	 * The property {@code deleteInTheEnd} of {@code @IfAbsent} determines if the value is deleted after operation completion.
	 * True​​: Delete | ​​False​​: Keep
	 * 
	 * 
	 * <p>
	 * Please review examples for more information.
	 * 
	 * 
	 * @param members is map. The keys of this map are the values of a sorted set,and the values of this map are the scores.
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=true)
	@ContinueOnFailure(true)
	void acquire(@Multiple Map<T,Double> members);
	
	/**
	 * The {@code oldScore} will be overwritten by {@code newScore} only if it matches the member's current score in the sorted set.
	 * 
	 * 
	 * @param value 	The value.
	 * @param oldScore  The expected current score in the sorted set.
	 * @param newScore  The new score to set if verification succeeds.
	 * @return Boolean  True if the score was updated, false otherwise (e.g., if {@code oldScore} did not match).
	 */
	@CAS
	Boolean compareAndSwap(@RedisValue T value, @OldScore Double oldScore, @Score Double newScore);
	
	/**
	 * Get random value from set
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @return the value.
	 */
	@GetMembers
	@Random
	T randomGetValue();
	
	/**
	 * Get random member from set
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @return a LinkedHashMap{"value": score}.
	 */
	@GetMembers
	@WithScores
	@Random
	Map<T,Double> randomGetMember();
	
	/**
	 * Get {@code count} random values from set
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @param count 	Number of values to return.
	 * @return a ArrayList,the returned list maintains the same order as the randomized sequence.
	 */
	@GetMembers
	@Random
	List<T> randomGetValues(@Count Integer count);
	
	/**
	 * Get {@code count} random members from set
	 *
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @param count 		Number of members to return.
	 * @return a ArrayList, single-entry maps: {"value": score} for each item.
	 * The returned list maintains the same order as the randomized sequence.
	 */
	@GetMembers
	@WithScores
	@Random
	List<Map<T, Double>> randomGetMembers(@Count Integer count);
	
	/**
	 * Get {@code count} distinct random values from set.
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 *  
	 * 
	 * @param count 	Number of members to return.
	 * @return a LinkedHashSet,the returned set maintains the same order as the randomized sequence.
	 */
	@GetMembers
	@Distinct
	@Random
	Set<T> distinctRandomGetValues(@Count Integer count);
	
	/**
	 * Get {@code count} random members from set
	 *
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @param count 				Number of members to return.
	 * @return a LinkedHashMap{"value": score}, the returned map maintains the same order as the randomized sequence.
	 */
	@GetMembers
	@WithScores
	@Distinct
	@Random
	Map<T,Double> distinctRandomGetMembers(@Count Integer count);
	
	/**
	 * Get values in the range [{@code minScore} {@code maxScore}].
	 * 
	 * @param maxScore
	 * @param minScore
	 * @return a LinkedHashSet, the values are sorted by score in ascending order.
	 */
	@GetMembers
	Set<T> getByScoreRange(@MaxScore Double maxScore,@MinScore Double minScore);
	
	/**
	 * Paginated results for values in the range [{@code minScore} {@code maxScore}].
	 * 
	 * @param maxScore
	 * @param minScore
	 * @param pageNo
	 * @param count
	 * @return a LinkedHashSet, the values are sorted by score in ascending order.
	 */
	@GetMembers
	Set<T> getByScoreRange(@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	/**
	 * Get values in the range [{@code startIndex}, {@code endIndex}].
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @return a LinkedHashSet, the values are sorted by score in ascending order.
	 */
	@GetMembers
	Set<T> getByRankRange(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	/**
	 * Get members in the range [{@code minScore} {@code maxScore}].
	 * 
	 * @param maxScore
	 * @param minScore
	 * @return a LinkedHashMap{"value": score},  the members are sorted by score in ascending order.
	 */
	@GetMembers
	@WithScores
	Map<T,Double> getByScoreRangeWithScores(@MaxScore Double maxScore,@MinScore Double minScore);
	
	/**
	 * Paginated results for members in the range [{@code minScore} {@code maxScore}].
	 * 
	 * @param maxScore
	 * @param minScore
	 * @param pageNo
	 * @param count
	 * @return a LinkedHashMap{"value": score}, the members are sorted by score in ascending order.
	 */
	@GetMembers
	@WithScores
	Map<T,Double> getByScoreRangeWithScores(@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	/**
	 * Get members in the range [{@code startIndex}, {@code endIndex}].
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @return a LinkedHashMap{"value": score}, the members are sorted by score in ascending order.
	 */
	@GetMembers
	@WithScores
	Map<T,Double> getByRankRangeWithScores(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	/**
	 * Get values in the range [{@code minScore} {@code maxScore}].
	 * 
	 * @param maxScore
	 * @param minScore
	 * @return a LinkedHashSet, the values are sorted by score in descending order.
	 */
	@GetMembers
	@Reverse
	Set<T> reveseScoreRange(@MaxScore Double maxScore,@MinScore Double minScore);
	
	/**
	 * Paginated results for values in the range [{@code minScore} {@code maxScore}].
	 * 
	 * @param maxScore
	 * @param minScore
	 * @param pageNo
	 * @param count
	 * @return a LinkedHashSet, the values are sorted by score in descending order.
	 */
	@GetMembers
	@Reverse
	Set<T> reveseScoreRange(@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	/**
	 * Get values in the range [{@code startIndex}, {@code endIndex}].
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @return a LinkedHashSet, the values are sorted by score in descending order.
	 */
	@GetMembers
	@Reverse
	Set<T> reveseRankRange(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	/**
	 * Get members in the range [{@code startIndex}, {@code endIndex}].
	 * 
	 * 
	 * @param maxScore
	 * @param minScore
	 * @return a LinkedHashMap{"value": score}, the members are sorted by score in descending order.
	 */
	@GetMembers
	@Reverse
	@WithScores
	Map<T,Double> reveseScoreRangeWithScores(@MaxScore Double maxScore,@MinScore Double minScore);
	
	/**
	 * Paginated results for members in the range [{@code minScore} {@code maxScore}].
	 * 
	 * @param maxScore
	 * @param minScore
	 * @param pageNo
	 * @param count
	 * @return a LinkedHashMap{"value": score}, the members are sorted by score in descending order.
	 */
	@GetMembers
	@Reverse
	@WithScores
	Map<T,Double> reveseScoreRangeWithScores(@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	/**
	 * Paginated results for members in the range [{@code startIndex}, {@code endIndex}].
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @return a LinkedHashMap{"value": score}, the members are sorted by score in descending order.
	 */
	@GetMembers
	@Reverse
	@WithScores
	Map<T,Double> reveseRankRangeWithScores(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	/**
	 * Atomically removes and returns the member with the highest score.
	 * 
	 * @return a LinkedHashMap{"value": score}.
	 */
	@PopMembers
	@MaxScore
	Map<T,Double> popMax();
	
	/**
	 * Atomically removes and returns the member with the lowest score.
	 * 
	 * @return a LinkedHashMap{"value": score}.
	 */
	@PopMembers
	@MinScore
	Map<T,Double> popMin();
	
	/**
	 * Atomically removes and returns up to {@code count} members with the highest scores.
	 * 
	 * @param count
	 * @return a LinkedHashMap{"value": score}, the members are sorted by score in descending order.
	 */
	@PopMembers
	@MaxScore
	Map<T,Double> popMax(@Count Integer count);
	
	/**
	 * Atomically removes and returns up to {@code count} members with the lowest scores.
	 * 
	 * @param count
	 * @return a LinkedHashMap{"value": score}, the members are sorted by score in ascending order.
	 */
	@PopMembers
	@MinScore
	Map<T,Double> popMin(@Count Integer count);
	
	/**
	 * Atomically removes and returns the member the highest score within {@code value} {@code unit}.
	 * 
	 * @param value timeout value
	 * @param unit  timeout unit
	 * @return a LinkedHashMap{"value": score}
	 */
	@PopMembers
	@MaxScore
	Map<T,Double> timeLimitedPopMax(@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
	
	/**
	 * Atomically removes and returns the member the lowest score within {@code value} {@code unit}.
	 * 
	 * @param value timeout value
	 * @param unit  timeout unit
	 * @return a LinkedHashMap{"value": score}
	 */
	@PopMembers
	@MinScore
	Map<T,Double> timeLimitedPopMin(@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
	
	/**
	 * Get the ascending rank of the value in the sorted set.
	 * 
	 * @param value
	 * @return rank
	 */
	@GetIndexs
	Long getRank(@RedisValue T value);
	
	/**
	 * Get the ascending ranks of multiple values in the sorted set.
	 * 
	 * @param value
	 * @return a LinkedHashMap{"value": rank}
	 */
	@GetIndexs
	@ContinueOnFailure(true)
	Map<T,Long> getRanks(@Multiple Set<T> value);
	
	/**
	 * Get score of value in the sorted set.
	 * 
	 * @param value
	 * @return score
	 */
	@GetScores
	Double getScore(@RedisValue T value);
	
	/**
	 * Get scores of multiple values in the sorted set.
	 * 
	 * @param value
	 * @return a LinkedHashMap{"value": score}
	 */
	@GetScores
	Map<T, Double> getScores(@Multiple Set<T> value);
	
	/**
	 * Get the descending rank of the value in the sorted set.
	 * 
	 * @param value
	 * @return rank
	 */
	@GetIndexs
	@Reverse
	Long reveseRank(@RedisValue T value);
	
	/**
	 * Get the descending ranks of multiple values in the sorted set.
	 * 
	 * @param value
	 * @return a LinkedHashMap{"value": rank}
	 */
	@GetIndexs
	@Reverse
	@ContinueOnFailure(true)
	Map<T,Long> reveseRanks(@Multiple Set<T> value);
	
	/**
	 * Get the size of a sorted set.
	 * 
	 * @return size
	 */
	@GetSize
	Long getSize();
	
	/**
	 * Get the size in the range [{@code minScore} {@code maxScore}
	 * 
	 * @param maxScore
	 * @param minScore
	 * @return size
	 */
	@GetSize
	Long getSizeByScoreRange(@MaxScore Double maxScore,@MinScore Double minScore);
	
	/**
	 * Remove the member by the value in sorted set.
	 * 
	 * @param value
	 * @return True if the member was removed successfully, false otherwise.
	 * 
	 */
	@RemoveMembers
	Boolean remove(@RedisValue T value);
	
	/**
	 * Remove the members by the values in sorted set.
	 * 
	 * @param values
	 * @return a LinkedHashMap{"value": result}
	 */
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<T,Boolean> remove(@Multiple Set<T> values);
	
	/**
	 * Remove the members in the range [{@code startIndex}, {@code endIndex}]
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @return The number of members removed from the sorted set.
	 */
	@RemoveMembers
	Long removeByRankRange(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	/**
	 * Remove the members in the range [{@code minScore}, {@code maxScore}]
	 * 
	 * @param maxScore
	 * @param minScore
	 * @return The number of members removed from the sorted set.
	 */
	@RemoveMembers
	Long removeByScoreRange(@MaxScore Double maxScore,@MinScore Double minScore);
	
	/**
	 * Increments the member's score by 1 and returns the new score.
	 * 
	 * @param value
	 * @return New score
	 */
	@Increment
	Double increment(@RedisValue T value);
	
	/**
	 * Decrements the member's score by 1 and returns the new score.
	 * 
	 * @param value
	 * @return New score
	 */
	@Decrement
	Double decrement(@RedisValue T value);
	
	/**
	 * Increments the member's score by {@code delta} and returns the new score.
	 * 
	 * @param value
	 * @param delta
	 * @return New score
	 */
	@Increment
	Double increment(@RedisValue T value, @Score Double delta);
	
	/**
	 * Decrements the member's score by {@code delta} and returns the new score.
	 * 
	 * @param value
	 * @param delta
	 * @return New score
	 */
	@Decrement
	Double decrement(@RedisValue T value, @Score Double delta);
}
