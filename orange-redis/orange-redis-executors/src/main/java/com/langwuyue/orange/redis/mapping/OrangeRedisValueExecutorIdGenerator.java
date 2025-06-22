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

import com.langwuyue.orange.redis.annotation.AutoRenew;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.Decrement;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Increment;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.value.GetValue;
import com.langwuyue.orange.redis.annotation.value.Lock;
import com.langwuyue.orange.redis.annotation.value.SetValue;

/**
 * Executor ID generator for Redis value operations.
 * 
 * <p>This class extends the abstract executor ID generator to provide support
 * for Redis value-specific annotations. It registers all the annotation classes
 * that are relevant for Redis value operations, such as getting and setting values,
 * conditional operations (CAS, IfAbsent), and atomic operations (Increment, Decrement).
 * 
 * <p>The registered annotations are used to generate unique operation IDs that
 * identify specific Redis value operations based on their annotation combinations.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisValueExecutorIdGenerator extends OrangeRedisExecutorIdAbstractGenerator {

	/**
	 * Registers all annotation classes supported by Redis value operations.
	 * 
	 * <p>This method extends the base registration by adding value-specific annotations:
	 * <ul>
	 *   <li>{@link RedisValue} - Marks parameters that represent Redis values</li>
	 *   <li>{@link RedisOldValue} - Marks parameters that capture previous values</li>
	 *   <li>{@link IfAbsent} - Indicates operations that should only proceed if a key is absent</li>
	 *   <li>{@link CAS} - Indicates Compare-And-Set operations</li>
	 *   <li>{@link Increment} - Marks atomic increment operations</li>
	 *   <li>{@link Decrement} - Marks atomic decrement operations</li>
	 *   <li>{@link SetValue} - Indicates value setting operations</li>
	 *   <li>{@link GetValue} - Indicates value retrieval operations</li>
	 *   <li>{@link AutoRenew} - Indicates keys that should be automatically renewed</li>
	 *   <li>{@link Lock} - Indicates Redis-based locking operations</li>
	 * </ul>
	 *
	 * @param supportedAnnotationClasses the list to populate with supported annotation classes
	 */
	@Override
	protected void registerSupportedAnnotationClasses(List<Class<? extends Annotation>> supportedAnnotationClasses) {
		super.registerSupportedAnnotationClasses(supportedAnnotationClasses);
		supportedAnnotationClasses.add(RedisValue.class);
		supportedAnnotationClasses.add(RedisOldValue.class);
		supportedAnnotationClasses.add(IfAbsent.class);
		supportedAnnotationClasses.add(CAS.class);
		supportedAnnotationClasses.add(Increment.class);
		supportedAnnotationClasses.add(Decrement.class);
		supportedAnnotationClasses.add(SetValue.class);
		supportedAnnotationClasses.add(GetValue.class);
		supportedAnnotationClasses.add(AutoRenew.class);
		supportedAnnotationClasses.add(Lock.class);
	}
}