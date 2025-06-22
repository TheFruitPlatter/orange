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
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Increment;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.hash.GetHashKeys;
import com.langwuyue.orange.redis.annotation.hash.GetHashValueLength;
import com.langwuyue.orange.redis.annotation.hash.GetHashValues;
import com.langwuyue.orange.redis.annotation.hash.HasKeys;
import com.langwuyue.orange.redis.annotation.hash.HashKey;

/**
 * Executor ID generator for Redis hash operations.
 * 
 * <p>This class extends the abstract executor ID generator to provide support
 * for Redis hash-specific annotations. It registers all the annotation classes
 * that are relevant for Redis hash operations, such as adding, retrieving, and
 * removing hash entries, as well as operations for hash keys and values.
 * 
 * <p>The registered annotations are used to generate unique operation IDs that
 * identify specific Redis hash operations based on their annotation combinations.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisHashExecutorIdGenerator extends OrangeRedisExecutorIdAbstractGenerator {

	/**
	 * Registers all annotation classes supported by Redis hash operations.
	 * 
	 * @param supportedAnnotationClasses the list to populate with supported annotation classes
	 */
	@Override
	protected void registerSupportedAnnotationClasses(List<Class<? extends Annotation>> supportedAnnotationClasses) {
		super.registerSupportedAnnotationClasses(supportedAnnotationClasses);
		supportedAnnotationClasses.add(AddMembers.class);
		supportedAnnotationClasses.add(GetMembers.class);
		supportedAnnotationClasses.add(RemoveMembers.class);
		supportedAnnotationClasses.add(GetSize.class);
		supportedAnnotationClasses.add(RedisValue.class);
		supportedAnnotationClasses.add(Random.class);
		supportedAnnotationClasses.add(Count.class);
		supportedAnnotationClasses.add(Multiple.class);
		supportedAnnotationClasses.add(ContinueOnFailure.class);
		supportedAnnotationClasses.add(CAS.class);
		supportedAnnotationClasses.add(RedisOldValue.class);
		supportedAnnotationClasses.add(GetHashValues.class);
		supportedAnnotationClasses.add(GetHashKeys.class);
		supportedAnnotationClasses.add(GetHashValueLength.class);
		supportedAnnotationClasses.add(Decrement.class);
		supportedAnnotationClasses.add(Increment.class);
		supportedAnnotationClasses.add(IfAbsent.class);
		supportedAnnotationClasses.add(HashKey.class);
		supportedAnnotationClasses.add(Member.class);
		supportedAnnotationClasses.add(HasKeys.class);
		supportedAnnotationClasses.add(Distinct.class);
	
	}
}