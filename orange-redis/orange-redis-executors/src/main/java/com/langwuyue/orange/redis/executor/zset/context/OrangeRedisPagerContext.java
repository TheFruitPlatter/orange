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
 * Interface defining the contract for Redis pagination operations in Orange Redis framework.
 * 
 * <p>This interface provides the foundation for implementing pagination in Redis operations,
 * particularly for Sorted Sets (ZSet). It supports two pagination approaches:
 * <ul>
 *   <li>Direct usage of {@link com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager} objects
 *   <li>Custom pagination objects using {@link PageNo} and {@link Count} annotations
 * </ul>
 * 
 * <p>The interface includes a default implementation of {@link #getPager(Object)} that handles
 * both pagination approaches, performing necessary validations and conversions.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.annotation.zset.Pager
 * @see com.langwuyue.orange.redis.annotation.zset.PageNo
 * @see com.langwuyue.orange.redis.annotation.Count
 * @see com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager
 * @see <a href="https://orange.langwuyue.com/redis/advanced/zset">Orange Redis ZSet Documentation</a>
 */
public interface OrangeRedisPagerContext {
	
	/**
	 * Validates and converts a pagination object to a standardized Pager instance.
	 * 
	 * <p>This method supports two types of pagination objects:
	 * <ol>
	 *   <li>Direct instances of {@link com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.Pager}
	 *   <li>Custom objects with fields annotated with {@link PageNo} and {@link Count}
	 * </ol>
	 * 
	 * <p>For custom pagination objects, this method:
	 * <ul>
	 *   <li>Locates fields annotated with @PageNo and @Count
	 *   <li>Validates that both fields exist and are not null
	 *   <li>Ensures the fields are integers or strings convertible to integers
	 *   <li>Creates a new Pager instance with the extracted values
	 * </ul>
	 * 
	 * <p>This flexible approach allows developers to use either the framework's
	 * built-in Pager class or their own domain-specific pagination objects.
	 * 
	 * @param pager The pagination object (either a Pager instance or a custom object)
	 * @return A standardized Pager instance with offset and count values
	 * @throws OrangeRedisException if:
	 *         <ul>
	 *           <li>The pager object is null
	 *           <li>The custom object doesn't have fields with @PageNo and @Count annotations
	 *           <li>The annotated fields are null
	 *           <li>The annotated fields are not integers or strings convertible to integers
	 *         </ul>
	 */
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

	/**
	 * Retrieves the pagination parameter for the current Redis operation context.
	 * 
	 * <p>This method should be implemented by concrete context classes to return
	 * the appropriate Pager object for the current Redis operation. Typically,
	 * implementations will delegate to {@link #getPager(Object)} with their
	 * specific pager field.
	 * 
	 * <p>The returned Pager object contains the offset and count values that will
	 * be used with Redis commands to implement server-side pagination.
	 * 
	 * @return The Pager object containing pagination parameters (offset and count)
	 * @throws OrangeRedisException if the pager is null or invalid
	 */
	Pager getPager();
}