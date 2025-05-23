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

import java.lang.annotation.Annotation;
import java.util.List;

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
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.annotation.Timeout;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.annotation.zset.GetScores;
import com.langwuyue.orange.redis.annotation.zset.LexRange;
import com.langwuyue.orange.redis.annotation.zset.MaxLex;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.annotation.zset.MinLex;
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.annotation.zset.OldScore;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.annotation.zset.Pager;
import com.langwuyue.orange.redis.annotation.zset.RankRange;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.annotation.zset.ScoreRange;
import com.langwuyue.orange.redis.annotation.zset.WithScores;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisZSetExecutorIdGenerator extends OrangeRedisExecutorIdAbstractGenerator {

	@Override
	protected void registerSupportedAnnotationClasses(List<Class<? extends Annotation>> supportedAnnotationClasses) {
		super.registerSupportedAnnotationClasses(supportedAnnotationClasses);
		supportedAnnotationClasses.add(AddMembers.class);
		supportedAnnotationClasses.add(GetMembers.class);
		supportedAnnotationClasses.add(RemoveMembers.class);
		supportedAnnotationClasses.add(Increment.class);
		supportedAnnotationClasses.add(Decrement.class);
		supportedAnnotationClasses.add(PopMembers.class);
		supportedAnnotationClasses.add(GetScores.class);
		supportedAnnotationClasses.add(GetSize.class);
		supportedAnnotationClasses.add(GetIndexs.class);
		supportedAnnotationClasses.add(RedisValue.class);
		supportedAnnotationClasses.add(IfAbsent.class);
		supportedAnnotationClasses.add(CAS.class);
		supportedAnnotationClasses.add(WithScores.class);
		supportedAnnotationClasses.add(Distinct.class);
		supportedAnnotationClasses.add(Random.class);
		supportedAnnotationClasses.add(Count.class);
		supportedAnnotationClasses.add(PageNo.class);
		supportedAnnotationClasses.add(MaxScore.class);
		supportedAnnotationClasses.add(MinScore.class);
		supportedAnnotationClasses.add(MaxLex.class);
		supportedAnnotationClasses.add(MinLex.class);
		supportedAnnotationClasses.add(StartIndex.class);
		supportedAnnotationClasses.add(EndIndex.class);
		supportedAnnotationClasses.add(ScoreRange.class);
		supportedAnnotationClasses.add(RankRange.class);
		supportedAnnotationClasses.add(LexRange.class);
		supportedAnnotationClasses.add(Pager.class);
		supportedAnnotationClasses.add(TimeoutValue.class);
		supportedAnnotationClasses.add(Timeout.class);
		supportedAnnotationClasses.add(Reverse.class);
		supportedAnnotationClasses.add(Member.class);
		supportedAnnotationClasses.add(Score.class);
		supportedAnnotationClasses.add(Multiple.class);
		supportedAnnotationClasses.add(ContinueOnFailure.class);
		supportedAnnotationClasses.add(OldScore.class);
	
	}
}
