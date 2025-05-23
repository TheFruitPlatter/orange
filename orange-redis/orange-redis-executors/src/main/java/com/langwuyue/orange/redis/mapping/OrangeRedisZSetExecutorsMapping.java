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
package com.langwuyue.orange.redis.mapping;

import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeCountExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeDistinctRandomGetMembersExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeDistinctRandomGetMembersWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeGetRankExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeGetRanksExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeGetScoreExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeGetScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeRandomGetMemberExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeRandomGetMemberWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeRandomGetMembersExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeRandomGetMembersWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeRemoveMemberExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeRemoveMembersExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeReverseRankExecutor;
import com.langwuyue.orange.redis.executor.zset.OrangeReverseRanksExecutor;
import com.langwuyue.orange.redis.executor.zset.add.OrangeAddByMemberAnnotationExecutor;
import com.langwuyue.orange.redis.executor.zset.add.OrangeAddIfAbsentByMemberAnnotationExecutor;
import com.langwuyue.orange.redis.executor.zset.add.OrangeAddMemberExecutor;
import com.langwuyue.orange.redis.executor.zset.add.OrangeAddMemberIfAbsentExecutor;
import com.langwuyue.orange.redis.executor.zset.add.OrangeAddMembersExecutor;
import com.langwuyue.orange.redis.executor.zset.add.OrangeAddMembersIfAbsentExecutor;
import com.langwuyue.orange.redis.executor.zset.add.OrangeCompareAndSwapExecutor;
import com.langwuyue.orange.redis.executor.zset.add.OrangeDecrementExecutor;
import com.langwuyue.orange.redis.executor.zset.add.OrangeDecrementWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.add.OrangeIncrementExecutor;
import com.langwuyue.orange.redis.executor.zset.add.OrangeIncrementWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.add.OrangeMultipleCompareAndSwapExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeCountByLexRangeExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeCountByMaxLexMinLexExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeGetByLexRangeExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeGetByLexRangePageNoCountExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeGetByLexRangePagerExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeGetByMaxLexMinLexExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeGetByMaxLexMinLexPageNoCountExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeGetByMaxLexMinLexPagerExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeRemoveByLexRangeExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeRemoveByMaxLexMinLexExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeReverseByLexRangeExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeReverseByLexRangePageNoCountExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeReverseByLexRangePagerExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeReverseByMaxLexMinLexExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeReverseByMaxLexMinLexPageNoCountExecutor;
import com.langwuyue.orange.redis.executor.zset.range.lex.OrangeReverseByMaxLexMinLexPagerExecutor;
import com.langwuyue.orange.redis.executor.zset.range.rank.OrangeGetByRankRangeExecutor;
import com.langwuyue.orange.redis.executor.zset.range.rank.OrangeGetByRankRangeWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.rank.OrangeGetByStartIndexEndIndexExecutor;
import com.langwuyue.orange.redis.executor.zset.range.rank.OrangeGetByStartIndexEndIndexWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.rank.OrangeRemoveByRankRangeExecutor;
import com.langwuyue.orange.redis.executor.zset.range.rank.OrangeRemoveByStartIndexEndIndexExecutor;
import com.langwuyue.orange.redis.executor.zset.range.rank.OrangeReverseByRankRangeExecutor;
import com.langwuyue.orange.redis.executor.zset.range.rank.OrangeReverseByRankRangeWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.rank.OrangeReverseByStartIndexEndIndexExecutor;
import com.langwuyue.orange.redis.executor.zset.range.rank.OrangeReverseByStartIndexEndIndexWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeCountByMaxScoreMinScoreExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeCountByScoreRangeExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeGetByMaxScoreMinScoreExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeGetByMaxScoreMinScorePageNoCountExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeGetByMaxScoreMinScorePageNoCountWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeGetByMaxScoreMinScorePagerExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeGetByMaxScoreMinScorePagerWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeGetByMaxScoreMinScoreWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeGetByScoreRangeExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeGetByScoreRangePageNoCountExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeGetByScoreRangePageNoCountWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeGetByScoreRangePagerExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeGetByScoreRangePagerWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeGetByScoreRangeWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangePopMemberByMaxScoreExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangePopMemberByMaxScoreTimeoutExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangePopMemberByMinScoreExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangePopMemberByMinScoreTimeoutExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangePopMembersByMaxScoreExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangePopMembersByMinScoreExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeRemoveByMaxScoreMinScoreExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeRemoveByScoreRangeExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeReverseByMaxScoreMinScoreExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeReverseByMaxScoreMinScorePageNoCountExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeReverseByMaxScoreMinScorePageNoCountWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeReverseByMaxScoreMinScorePagerExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeReverseByMaxScoreMinScorePagerWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeReverseByMaxScoreMinScoreWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeReverseByScoreRangeExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeReverseByScoreRangePageNoCountExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeReverseByScoreRangePageNoCountWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeReverseByScoreRangePagerExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeReverseByScoreRangePagerWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeReverseByScoreRangeWithScoresExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeTimeLimitedPopMemberByMaxScoreExecutor;
import com.langwuyue.orange.redis.executor.zset.range.score.OrangeTimeLimitedPopMemberByMinScoreExecutor;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.operations.OrangeRedisOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations;
import com.langwuyue.orange.redis.template.zset.JSONOperationsTemplate;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisZSetExecutorsMapping extends OrangeRedisAbstractExecutorsMapping {
	
	public OrangeRedisZSetExecutorsMapping(
		OrangeRedisZSetOperations operations,
		OrangeRedisZSetExecutorIdGenerator generator,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRedisLogger logger
	) {
		super(operations,generator,scriptOperations,listeners,multipleListeners,logger);
	}

	@Override
	protected void registerExecutors(
		List<OrangeRedisExecutor> executors, 
		OrangeRedisOperations operations,
		OrangeRedisExecutorIdGenerator generator,
		OrangeRedisScriptOperations scriptOperations,
		Collection<OrangeRedisSetIfAbsentListener> listeners,
		Collection<OrangeRedisMultipleSetIfAbsentListener> multipleListeners,
		OrangeRedisLogger logger
	) {
		super.registerExecutors(executors, operations, generator,scriptOperations,listeners,multipleListeners,logger);
		OrangeRedisZSetOperations zSetOperations = (OrangeRedisZSetOperations) operations;
		// add
		executors.add(new OrangeAddByMemberAnnotationExecutor(zSetOperations,generator));
		executors.add(new OrangeAddIfAbsentByMemberAnnotationExecutor(zSetOperations,generator,listeners));
		//executors.add(new OrangeAddIfAbsentByMemberAnnotationWithExpirationExecutor(zSetOperations,generator,listeners));
		//executors.add(new OrangeAddIfAbsentWithExpirationExecutor(zSetOperations,generator,listeners));
		executors.add(new OrangeAddMemberExecutor(zSetOperations,generator));
		executors.add(new OrangeAddMemberIfAbsentExecutor(zSetOperations,generator,listeners));
		executors.add(new OrangeAddMembersExecutor(zSetOperations,generator,logger));
		executors.add(new OrangeAddMembersIfAbsentExecutor(zSetOperations,generator,multipleListeners));
		//executors.add(new OrangeAddMembersIfAbsentWithExpirationExecutor(zSetOperations,generator,multipleListeners));
		executors.add(new OrangeCompareAndSwapExecutor(scriptOperations,generator,logger));
		executors.add(new OrangeDecrementExecutor(zSetOperations,generator));
		executors.add(new OrangeDecrementWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeIncrementExecutor(zSetOperations,generator));
		executors.add(new OrangeIncrementWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeMultipleCompareAndSwapExecutor(scriptOperations,generator,logger));
		
		// lex
		executors.add(new OrangeCountByLexRangeExecutor(zSetOperations,generator));
		executors.add(new OrangeCountByMaxLexMinLexExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByLexRangeExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByLexRangePageNoCountExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByLexRangePagerExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByMaxLexMinLexExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByMaxLexMinLexPageNoCountExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByMaxLexMinLexPagerExecutor(zSetOperations,generator));
		executors.add(new OrangeRemoveByLexRangeExecutor(zSetOperations,generator));
		executors.add(new OrangeRemoveByMaxLexMinLexExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByLexRangeExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByLexRangePageNoCountExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByLexRangePagerExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByMaxLexMinLexExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByMaxLexMinLexPageNoCountExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByMaxLexMinLexPagerExecutor(zSetOperations,generator));
		// rank
		executors.add(new OrangeGetByRankRangeExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByRankRangeWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByStartIndexEndIndexExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByStartIndexEndIndexWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeRemoveByRankRangeExecutor(zSetOperations,generator));
		executors.add(new OrangeRemoveByStartIndexEndIndexExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByRankRangeExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByRankRangeWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByStartIndexEndIndexExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByStartIndexEndIndexWithScoresExecutor(zSetOperations,generator));
		// score
		executors.add(new OrangeCountByMaxScoreMinScoreExecutor(zSetOperations,generator));
		executors.add(new OrangeCountByScoreRangeExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByMaxScoreMinScoreExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByMaxScoreMinScorePageNoCountExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByMaxScoreMinScorePageNoCountWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByMaxScoreMinScorePagerExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByMaxScoreMinScorePagerWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByMaxScoreMinScoreWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByScoreRangeExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByScoreRangePageNoCountExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByScoreRangePageNoCountWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByScoreRangePagerExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByScoreRangePagerWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeGetByScoreRangeWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangePopMemberByMaxScoreExecutor(zSetOperations,generator));
		executors.add(new OrangePopMemberByMaxScoreTimeoutExecutor(zSetOperations,generator));
		executors.add(new OrangePopMemberByMinScoreExecutor(zSetOperations,generator));
		executors.add(new OrangePopMemberByMinScoreTimeoutExecutor(zSetOperations,generator));
		executors.add(new OrangePopMembersByMaxScoreExecutor(zSetOperations,generator));
		executors.add(new OrangePopMembersByMinScoreExecutor(zSetOperations,generator));
		executors.add(new OrangeRemoveByMaxScoreMinScoreExecutor(zSetOperations,generator));
		executors.add(new OrangeRemoveByScoreRangeExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByMaxScoreMinScoreExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByMaxScoreMinScorePageNoCountExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByMaxScoreMinScorePageNoCountWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByMaxScoreMinScorePagerExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByMaxScoreMinScorePagerWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByMaxScoreMinScoreWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByScoreRangeExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByScoreRangePageNoCountExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByScoreRangePageNoCountWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByScoreRangePagerExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByScoreRangePagerWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseByScoreRangeWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeTimeLimitedPopMemberByMaxScoreExecutor(zSetOperations,generator));
		executors.add(new OrangeTimeLimitedPopMemberByMinScoreExecutor(zSetOperations,generator));
		// other
		executors.add(new OrangeDistinctRandomGetMembersExecutor(zSetOperations,generator));
		executors.add(new OrangeDistinctRandomGetMembersWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeGetRankExecutor(zSetOperations,generator));
		executors.add(new OrangeGetRanksExecutor(zSetOperations,generator,logger));
		executors.add(new OrangeGetScoreExecutor(zSetOperations,generator));
		executors.add(new OrangeGetScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeRandomGetMemberExecutor(zSetOperations,generator));
		executors.add(new OrangeRandomGetMembersExecutor(zSetOperations,generator));
		executors.add(new OrangeRandomGetMembersWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeRandomGetMemberWithScoresExecutor(zSetOperations,generator));
		executors.add(new OrangeRemoveMemberExecutor(zSetOperations,generator));
		executors.add(new OrangeRemoveMembersExecutor(zSetOperations,generator,logger));
		executors.add(new OrangeReverseRankExecutor(zSetOperations,generator));
		executors.add(new OrangeReverseRanksExecutor(zSetOperations,generator,logger));
		executors.add(new OrangeCountExecutor(zSetOperations,generator));
	}

	@Override
	protected Class<?> getTemplateClass() {
		return JSONOperationsTemplate.class;
	}
}
