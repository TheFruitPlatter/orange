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

import com.langwuyue.orange.example.redis.entity.OrangePagerEntity;
import com.langwuyue.orange.example.redis.entity.OrangeRankRangeEntity;
import com.langwuyue.orange.example.redis.entity.OrangeScoreRangeEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity;
import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity.ZSetValue;
import com.langwuyue.orange.example.redis.entity.OrangeZSetScoreNullableExampleEntity;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.GetIndexs;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.zset.GetScores;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.annotation.zset.OrangeRedisZSetClient;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.annotation.zset.Pager;
import com.langwuyue.orange.redis.annotation.zset.RankRange;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.annotation.zset.ScoreRange;
import com.langwuyue.orange.redis.annotation.zset.WithScores;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:zset:example3")
@OrangeRedisZSetClient(valueType = RedisValueTypeEnum.JSON)
public interface OrangeRedisZSetExample3Api {
	
	@AddMembers
	Boolean add(@Member OrangeZSetExampleEntity member);
	
	@AddMembers
	@ContinueOnFailure(true)
	Map<OrangeZSetExampleEntity, Boolean> add(@Multiple Set<OrangeZSetExampleEntity> members);
	
	@AddMembers
	@IfAbsent
	Boolean addIfAsent(@Member OrangeZSetExampleEntity member);
	
	@AddMembers
	@IfAbsent
	@ContinueOnFailure(true)
	Map<OrangeZSetExampleEntity, Boolean> addIfAsent(@Multiple Set<OrangeZSetExampleEntity> members);
	
	@GetMembers
	@Random
	OrangeZSetExampleEntity randomGetValue1();
	
	@GetMembers
	@Random
	ZSetValue randomGetValue2();
	
	@GetMembers
	@WithScores
	@Random
	OrangeZSetExampleEntity randomGetMember();
	
	@GetMembers
	@Random
	List<OrangeZSetExampleEntity> randomGetValues1(@Count Integer count);
	
	@GetMembers
	@Random
	List<ZSetValue> randomGetValues2(@Count Integer count);
	
	@GetMembers
	@WithScores
	@Random
	List<OrangeZSetExampleEntity> randomGetMembers(@Count Integer count);
	
	@GetMembers
	@Distinct
	@Random
	Set<OrangeZSetExampleEntity> distinctRandomGetValues1(@Count Integer count);
	
	@GetMembers
	@Distinct
	@Random
	Set<ZSetValue> distinctRandomGetValues2(@Count Integer count);
	
	@GetMembers
	@WithScores
	@Distinct
	@Random
	Set<OrangeZSetExampleEntity> distinctRandomGetMembers(@Count Integer count);
	
	@GetMembers
	Set<ZSetValue> getByScoreRange1(@MaxScore Double maxScore,@MinScore Double minScore);
	
	@GetMembers
	Set<OrangeZSetExampleEntity> getByScoreRange2(@MaxScore Double maxScore,@MinScore Double minScore);
	
	@GetMembers
	Set<OrangeZSetExampleEntity> getByScoreRange3(@ScoreRange OrangeScoreRangeEntity scoreRange);
	
	@GetMembers
	Set<ZSetValue> getByScoreRange1(@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	@GetMembers
	Set<OrangeZSetExampleEntity> getByScoreRange2(@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	@GetMembers
	Set<OrangeZSetExampleEntity> getByScoreRange3(@ScoreRange OrangeScoreRangeEntity scoreRange, @Pager OrangePagerEntity pager);
	
	@GetMembers
	Set<OrangeZSetExampleEntity> getByScoreRange4(@ScoreRange OrangeScoreRangeEntity scoreRange, @PageNo Long pageNo, @Count Long count);
	
	@GetMembers
	Set<OrangeZSetExampleEntity> getByScoreRange5(@MaxScore Double maxScore,@MinScore Double minScore, @Pager OrangePagerEntity pager);
	
	@GetMembers
	Set<ZSetValue> getByRankRange1(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	@GetMembers
	Set<OrangeZSetExampleEntity> getByRankRange2(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	@GetMembers
	Set<OrangeZSetExampleEntity> getByRankRange3(@RankRange OrangeRankRangeEntity rankRange);
	
	@GetMembers
	@WithScores
	Set<OrangeZSetExampleEntity> getByScoreRangeWithScores1(@MaxScore Double maxScore,@MinScore Double minScore);
	
	@GetMembers
	@WithScores
	Set<OrangeZSetExampleEntity> getByScoreRangeWithScores2(@ScoreRange OrangeScoreRangeEntity scoreRange);
	
	@GetMembers
	@WithScores
	Set<OrangeZSetExampleEntity> getByScoreRangeWithScores1(@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	@GetMembers
	@WithScores
	Set<OrangeZSetExampleEntity> getByScoreRangeWithScores2(@ScoreRange OrangeScoreRangeEntity scoreRange, @Pager OrangePagerEntity pager);
	
	@GetMembers
	@WithScores
	Set<OrangeZSetExampleEntity> getByScoreRangeWithScores3(@ScoreRange OrangeScoreRangeEntity scoreRange, @PageNo Long pageNo, @Count Long count);
	
	@GetMembers
	@WithScores
	Set<OrangeZSetExampleEntity> getByScoreRangeWithScores4(@MaxScore Double maxScore,@MinScore Double minScore, @Pager OrangePagerEntity pager);
	
	@GetMembers
	@WithScores
	Set<OrangeZSetExampleEntity> getByRankRangeWithScores1(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	@GetMembers
	@WithScores
	Set<OrangeZSetExampleEntity> getByRankRangeWithScores2(@RankRange OrangeRankRangeEntity rankRange);
	
	@GetMembers
	@Reverse
	Set<OrangeZSetExampleEntity> reveseScoreRange1(@MaxScore Double maxScore,@MinScore Double minScore);
	
	@GetMembers
	@Reverse
	Set<ZSetValue> reveseScoreRange2(@MaxScore Double maxScore,@MinScore Double minScore);
	
	@GetMembers
	@Reverse
	Set<ZSetValue> reveseScoreRange3(@ScoreRange OrangeScoreRangeEntity scoreRange);
	
	@GetMembers
	@Reverse
	Set<ZSetValue> reveseScoreRange1(@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	@GetMembers
	@Reverse
	Set<OrangeZSetExampleEntity> reveseScoreRange2(@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	@GetMembers
	@Reverse
	Set<OrangeZSetExampleEntity> reveseScoreRange3(@ScoreRange OrangeScoreRangeEntity scoreRange, @PageNo Long pageNo, @Count Long count);
	
	@GetMembers
	@Reverse
	Set<OrangeZSetExampleEntity> reveseScoreRange4(@MaxScore Double maxScore,@MinScore Double minScore, @Pager OrangePagerEntity pager);
	
	@GetMembers
	@Reverse
	Set<OrangeZSetExampleEntity> reveseScoreRange5(@ScoreRange OrangeScoreRangeEntity scoreRange, @Pager OrangePagerEntity pager);
	
	@GetMembers
	@Reverse
	Set<OrangeZSetExampleEntity> reveseRankRange1(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	@GetMembers
	@Reverse
	Set<ZSetValue> reveseRankRange2(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	@GetMembers
	@Reverse
	Set<ZSetValue> reveseRankRange3(@RankRange OrangeRankRangeEntity rankRange);
	
	@GetMembers
	@Reverse
	@WithScores
	Set<OrangeZSetExampleEntity> reveseScoreRangeWithScores1(@MaxScore Double maxScore,@MinScore Double minScore);
	
	@GetMembers
	@Reverse
	@WithScores
	Set<OrangeZSetExampleEntity> reveseScoreRangeWithScores2(@ScoreRange OrangeScoreRangeEntity scoreRange);
	
	@GetMembers
	@Reverse
	@WithScores
	Set<OrangeZSetExampleEntity> reveseScoreRangeWithScores1(@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	@GetMembers
	@Reverse
	@WithScores
	Set<OrangeZSetExampleEntity> reveseScoreRangeWithScores2(@ScoreRange OrangeScoreRangeEntity scoreRange, @Pager OrangePagerEntity pager);
	
	@GetMembers
	@Reverse
	@WithScores
	Set<OrangeZSetExampleEntity> reveseScoreRangeWithScores3(@ScoreRange OrangeScoreRangeEntity scoreRange, @PageNo Long pageNo, @Count Long count);
	
	@GetMembers
	@Reverse
	@WithScores
	Set<OrangeZSetExampleEntity> reveseScoreRangeWithScores4(@MaxScore Double maxScore,@MinScore Double minScore, @Pager OrangePagerEntity pager);
	
	@GetMembers
	@Reverse
	@WithScores
	Set<OrangeZSetExampleEntity> reveseRankRangeWithScores1(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	@GetMembers
	@Reverse
	@WithScores
	Set<OrangeZSetExampleEntity> reveseRankRangeWithScores2(@RankRange OrangeRankRangeEntity rankRange);
	
	@PopMembers
	@MaxScore
	OrangeZSetExampleEntity popMax();
	
	@PopMembers
	@MinScore
	OrangeZSetExampleEntity popMin();
	
	@PopMembers
	@MaxScore
	List<OrangeZSetExampleEntity> popMax(@Count Integer count);
	
	@PopMembers
	@MinScore
	List<OrangeZSetExampleEntity> popMin(@Count Integer count);
	
	@PopMembers
	@MaxScore
	OrangeZSetExampleEntity timeLimitedPopMax(@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
	
	@PopMembers
	@MinScore
	OrangeZSetExampleEntity timeLimitedPopMin(@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
	
	@GetScores
	Map<OrangeZSetScoreNullableExampleEntity, Double> getScores1(@Multiple Set<OrangeZSetScoreNullableExampleEntity> value);
	
	@GetScores
	List<OrangeZSetScoreNullableExampleEntity> getScores2(@Multiple Set<OrangeZSetScoreNullableExampleEntity> value);
	
	@GetScores
	List<Double> getScores3(@Multiple OrangeZSetScoreNullableExampleEntity[] values);
	
	@GetIndexs
	@ContinueOnFailure(true)
	Map<OrangeZSetScoreNullableExampleEntity, Long> getRanks(@Multiple Set<OrangeZSetScoreNullableExampleEntity> value);
	
	@GetIndexs
	@Reverse
	@ContinueOnFailure(true)
	Map<OrangeZSetScoreNullableExampleEntity,Long> reveseRanks(@Multiple Set<OrangeZSetScoreNullableExampleEntity> value);
	
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<OrangeZSetScoreNullableExampleEntity,Boolean> remove(@Multiple Set<OrangeZSetScoreNullableExampleEntity> values);

	@Delete
	boolean delete();
}