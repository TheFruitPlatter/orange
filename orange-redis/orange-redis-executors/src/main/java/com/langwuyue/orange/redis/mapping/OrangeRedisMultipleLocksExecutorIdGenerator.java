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
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.multiplelocks.MultipleLocks;
import com.langwuyue.orange.redis.annotation.transaction.Release;

/**
 * A specialized executor ID generator for Redis multiple locks operations.
 * 
 * <p>This class extends {@link OrangeRedisExecutorIdAbstractGenerator} to provide
 * specific support for generating executor IDs in the context of multiple Redis locks
 * operations. It handles various lock-related annotations and generates appropriate
 * executor IDs based on the combination of these annotations.
 * 
 * <p>The generator supports the following annotations:
 * <ul>
 *   <li>{@link MultipleLocks} - For handling multiple locks in a single operation</li>
 *   <li>{@link AutoRenew} - For automatic lock renewal functionality</li>
 *   <li>{@link Multiple} - For operations involving multiple keys</li>
 *   <li>{@link ContinueOnFailure} - For specifying failure handling behavior</li>
 *   <li>{@link Release} - For lock release operations in transactions</li>
 * </ul>
 * 
 * <p>This generator ensures that each combination of these annotations produces
 * a unique executor ID, allowing for proper identification and handling of
 * different types of multiple lock operations.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisMultipleLocksExecutorIdGenerator extends OrangeRedisExecutorIdAbstractGenerator {

	/**
	 * Registers all annotation classes supported by this executor ID generator.
	 * 
	 * <p>This method extends the base implementation by registering additional
	 * annotation classes specific to multiple locks operations. These annotations
	 * are used to identify and differentiate various types of multiple lock operations
	 * when generating executor IDs.
	 * 
	 * @param supportedAnnotationClasses the list to which supported annotation classes will be added
	 */
	@Override
	protected void registerSupportedAnnotationClasses(List<Class<? extends Annotation>> supportedAnnotationClasses) {
		super.registerSupportedAnnotationClasses(supportedAnnotationClasses);
		supportedAnnotationClasses.add(MultipleLocks.class);
		supportedAnnotationClasses.add(AutoRenew.class);
		supportedAnnotationClasses.add(Multiple.class);
		supportedAnnotationClasses.add(ContinueOnFailure.class);
		supportedAnnotationClasses.add(Release.class);
	}
}