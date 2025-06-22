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

import com.langwuyue.orange.redis.annotation.cross.Aggregate;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.Difference;
import com.langwuyue.orange.redis.annotation.cross.Intersect;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.annotation.cross.Union;
import com.langwuyue.orange.redis.annotation.cross.Weights;
import com.langwuyue.orange.redis.annotation.zset.WithScores;

/**
 * ID generator for Redis sorted set cross-key operation executors.
 * 
 * <p>This class extends {@link OrangeRedisExecutorIdAbstractGenerator} to provide
 * specific ID generation for Redis sorted set operations that involve multiple keys.
 * It supports the following cross-key operations:
 * <ul>
 *   <li>{@link Difference} - Computes the difference between sorted sets</li>
 *   <li>{@link Union} - Computes the union of sorted sets</li>
 *   <li>{@link Intersect} - Computes the intersection of sorted sets</li>
 *   <li>{@link CrossOperationKeys} - Specifies the keys involved in the operation</li>
 *   <li>{@link WithScores} - Indicates whether to include scores in the result</li>
 *   <li>{@link Aggregate} - Specifies how to aggregate scores during operations</li>
 *   <li>{@link StoreTo} - Specifies the destination key for storing results</li>
 *   <li>{@link Weights} - Specifies weights to apply to input sorted sets</li>
 * </ul>
 * 
 * <p>The generator creates unique IDs for executors based on the combination of
 * these annotations, ensuring proper operation identification and routing.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisZSetCrossKeyExecutorIdGenerator extends OrangeRedisExecutorIdAbstractGenerator {

	/**
	 * Registers the annotation classes supported by this ID generator.
	 * 
	 * @param supportedAnnotationClasses the list to which supported annotation
	 *        classes should be added
	 */
	@Override
	protected void registerSupportedAnnotationClasses(List<Class<? extends Annotation>> supportedAnnotationClasses) {
		supportedAnnotationClasses.add(Difference.class);
		supportedAnnotationClasses.add(CrossOperationKeys.class);
		supportedAnnotationClasses.add(WithScores.class);
		supportedAnnotationClasses.add(Union.class);
		supportedAnnotationClasses.add(Aggregate.class);
		supportedAnnotationClasses.add(StoreTo.class);
		supportedAnnotationClasses.add(Intersect.class);
		supportedAnnotationClasses.add(Weights.class);
	}
}