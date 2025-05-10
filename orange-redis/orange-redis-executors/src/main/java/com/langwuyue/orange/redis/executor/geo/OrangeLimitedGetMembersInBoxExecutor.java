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
package com.langwuyue.orange.redis.executor.geo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.geo.context.OrangeBoxPointReferenceContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntry;
import com.langwuyue.orange.redis.operations.OrangeRedisGeoOperations.GeoEntryInRadius;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeLimitedGetMembersInBoxExecutor extends OrangeGetMembersInBoxExecutor {

	public OrangeLimitedGetMembersInBoxExecutor(OrangeRedisGeoOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
	}

	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List<Class<? extends Annotation>> supportedAnnotationClasses = super.getSupportedAnnotationClasses();
		supportedAnnotationClasses.add(Count.class);
		return supportedAnnotationClasses;
	}

	@Override
	protected List<GeoEntryInRadius> doGet(OrangeRedisContext ctx, Field valueField) throws Exception {
		OrangeBoxPointReferenceContext context = (OrangeBoxPointReferenceContext) ctx;
		GeoEntry entry = context.getMember();
		return this.getOperations().box(
			context.getRedisKey().getValue(), 
			entry.getLongitude(),
			entry.getLatitude(),
			context.getWidthUnit(), 
			context.getWidth(),
			context.getHeightUnit(),
			context.getHeight(),
			context.getValueType(),
			valueField.getGenericType(),
			context.getSearchArguments()
		);
	}
	
}
