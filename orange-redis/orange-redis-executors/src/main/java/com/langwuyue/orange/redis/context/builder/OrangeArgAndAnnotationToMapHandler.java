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
package com.langwuyue.orange.redis.context.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeArgAndAnnotationToMapHandler implements OrangeOperationArgHandler {

	@Override
	public void bind(
		Field field, 
		OrangeRedisContext context, 
		Annotation annotation, 
		int parameterCount,
		int parameterIndex, 
		Object[] args
	) {
		Object obj = OrangeReflectionUtils.getFieldValue(field, context);
		if(obj == null) {
			obj = new LinkedHashMap<>();
			OrangeReflectionUtils.setFieldValue(field, context, obj);
		}
		Map map = (Map)obj;
		map.put(args[parameterIndex], annotation);
	}
}
