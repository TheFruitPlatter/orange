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
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.Height;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.annotation.geo.Width;

/**
 * A specialized executor ID generator for Redis Geo operations. This class extends the base
 * {@link OrangeRedisExecutorIdAbstractGenerator} to provide support for geographical-related
 * Redis operations and their associated annotations.
 * 
 * <p>This generator registers various annotations related to Redis Geo operations, including:
 * <ul>
 *   <li>Basic member operations (add, get, remove)</li>
 *   <li>Geographical coordinates (latitude, longitude)</li>
 *   <li>Search parameters (distance, width, height)</li>
 *   <li>Operation modifiers (multiple, continue on failure)</li>
 * </ul>
 * 
 * <p>The registered annotations are used to identify and process Redis Geo commands
 * during method execution.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisGeoExecutorIdGenerator extends OrangeRedisExecutorIdAbstractGenerator {

	/**
	 * Registers all annotation classes supported by this Redis Geo executor ID generator.
	 * 
	 * <p>This method extends the base implementation by adding Geo-specific annotations
	 * to the list of supported annotation classes. These annotations are used to identify
	 * and process Redis Geo commands during method execution.
	 * 
	 * @param supportedAnnotationClasses the list to which supported annotation classes will be added
	 */
	@Override
	protected void registerSupportedAnnotationClasses(List<Class<? extends Annotation>> supportedAnnotationClasses) {
		super.registerSupportedAnnotationClasses(supportedAnnotationClasses);
		supportedAnnotationClasses.add(AddMembers.class);
		supportedAnnotationClasses.add(GetMembers.class);
		supportedAnnotationClasses.add(RemoveMembers.class);
		supportedAnnotationClasses.add(RedisValue.class);
		supportedAnnotationClasses.add(Multiple.class);
		supportedAnnotationClasses.add(ContinueOnFailure.class);
		supportedAnnotationClasses.add(Member.class);
		supportedAnnotationClasses.add(Longitude.class);
		supportedAnnotationClasses.add(Latitude.class);
		supportedAnnotationClasses.add(Distance.class);
		supportedAnnotationClasses.add(SearchArgs.class);
		supportedAnnotationClasses.add(Count.class);
		supportedAnnotationClasses.add(Width.class);
		supportedAnnotationClasses.add(Height.class);
	}
}