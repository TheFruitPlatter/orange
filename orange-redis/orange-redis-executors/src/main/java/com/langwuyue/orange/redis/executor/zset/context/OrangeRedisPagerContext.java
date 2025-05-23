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
package com.langwuyue.orange.redis.executor.zset.context;

import java.lang.reflect.Field;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisPagerContext {
	
	default Pager getPager(Object pager) {
		if(pager == null) {
			throw new OrangeRedisException(String.format("The argument annotated @%s cannot be null", Pager.class));
		}
		if(pager instanceof com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager) {
			return (com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager)pager;
		}
		Field pageNoField = null;
		Field countField = null;
		Field[] fields = pager.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(PageNo.class)) {
				pageNoField = field;
			}
			else if(field.isAnnotationPresent(Count.class)) {
				countField = field;
			}
			if(pageNoField != null && countField != null) {
				break;
			}
		}
		if(pageNoField == null || countField == null) {
			throw new OrangeRedisException(String.format("The argument annotated @%s must have two fields annotated with @%s and @%s", Pager.class,PageNo.class,Count.class));
		}
		Object pageNo = OrangeReflectionUtils.getFieldValue(pageNoField, pager);
		if(pageNo == null) {
			throw new OrangeRedisException(String.format("The field annotated @%s cannot be null", PageNo.class));
		}
		if(!(OrangeReflectionUtils.isInteger(pageNo.getClass())) && !(pageNo instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated @%s must be a integer or a string", PageNo.class));
		}
		Object count = OrangeReflectionUtils.getFieldValue(countField, pager);
		if(count == null) {
			throw new OrangeRedisException(String.format("The field annotated @%s cannot be null", Count.class));
		}
		if(!(OrangeReflectionUtils.isInteger(count.getClass())) && !(count instanceof String)) {
			throw new OrangeRedisException(String.format("The field annotated @%s must be a integer or a string", Count.class));
		}
		return new Pager(Long.valueOf(pageNo.toString()),Long.valueOf(count.toString()));
	}

}
