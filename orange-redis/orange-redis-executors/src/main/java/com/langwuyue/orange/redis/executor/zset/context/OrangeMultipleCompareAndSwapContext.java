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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.OldScore;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.CASZSetEntry;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Context for handling multiple Compare-and-Swap (CAS) operations on Redis ZSet members.
 * 
 * <p>This class extends {@link OrangeAddMembersContext} to support atomic updates of multiple
 * ZSet member scores using CAS operations. It processes objects annotated with {@link Multiple}
 * and {@link OldScore} annotations to perform batch CAS operations on ZSet members.
 *
 * <p>The context ensures that each member's score is only updated if its current score
 * matches the expected old score, providing atomic multi-member updates.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeAddMembersContext
 * @see Multiple
 * @see OldScore
 * @see CASZSetEntry
 */
public class OrangeMultipleCompareAndSwapContext extends OrangeAddMembersContext {
	
	/**
	 * Constructs a new OrangeMultipleCompareAndSwapContext for batch CAS operations.
	 *
	 * <p>This constructor initializes the context for processing multiple ZSet member updates
	 * using Compare-and-Swap operations. It inherits the base functionality from
	 * {@link OrangeAddMembersContext} and adds CAS-specific processing.
	 *
	 * @param operationOwner The class that owns the Redis operation method
	 * @param operationMethod The method annotated with Redis operation annotations
	 * @param args The array of arguments passed to the operation method
	 * @param redisKey The Redis key wrapper containing the target ZSet key
	 * @param valueType The type of Redis value being operated on (should be ZSET)
	 */
	public OrangeMultipleCompareAndSwapContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, redisKey,valueType);
	}

	/**
	 * Converts a member object to a ZSetEntry for CAS operations.
	 * 
	 * <p>This method processes the member object to create a {@link CASZSetEntry} containing:
	 * <ul>
	 *   <li>The member value (from {@link RedisValue} annotated field)</li>
	 *   <li>The new score (from {@link Score} annotated field)</li>
	 *   <li>The expected old score (from {@link OldScore} annotated field)</li>
	 * </ul>
	 *
	 * @param member The object containing member data and score information
	 * @return A {@link CASZSetEntry} containing the member value and scores, or null if member is null
	 * @throws OrangeRedisException if required annotations are missing or score values are invalid
	 */
	protected ZSetEntry toZSetEntry(Object member) {
		if(member == null) {
			return null;
		}
		ZSetEntry entry = super.toZSetEntry(member,Multiple.class);
		if(entry == null) {
			entry = toEmptyZSetEntry(member);
		}
		CASZSetEntry casEntry = (CASZSetEntry) entry;
		casEntry.setOldScore(getOldScore(member));
		return casEntry;
	}

	@Override
	public ZSetEntry newZSetEntry(Object value, Object score) {
		return new CASZSetEntry(value, Double.valueOf(score.toString()));
	}
	
	private Double getNullableScore(Object score, Class<? extends Annotation> annotationClass) {
		if(score == null) {
			return null;
		}
		if(score instanceof Number || score instanceof String) {
			// It's risky for a long value greater than 2^53 when converting to double due to potential precision loss.
			return Double.valueOf(score.toString());
		}
		throw new OrangeRedisException(String.format("The argument annotated with @%s must be a number or a string", annotationClass));
	}
	
	private ZSetEntry toEmptyZSetEntry(Object member) {
		Field valueField = null;
		Field[] fields = member.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(RedisValue.class)) {
				valueField = field;
				break;
			}
		}
		Object value = OrangeReflectionUtils.getFieldValue(valueField, member);
		return new CASZSetEntry(value, null);
	}
	
	/**
	 * Extracts the expected old score from a member object.
	 * 
	 * <p>This method looks for a field annotated with {@link OldScore} in the member object
	 * and extracts its value. The value is then converted to a Double for use in the
	 * Compare-and-Swap operation.
	 *
	 * <p>The method supports various numeric types and will attempt to convert them to Double.
	 * If the field value is null or the field is not properly annotated, this method
	 * returns null.
	 *
	 * @param member The object containing the old score field
	 * @return The expected old score as a Double, or null if not available
	 * @throws OrangeRedisException if the old score value is present but cannot be converted to Double
	 */
	private Double getOldScore(Object member) {
		Field oldScoreField = null;
		Field[] fields = member.getClass().getDeclaredFields();
		for(Field field : fields) {
			if(field.isAnnotationPresent(OldScore.class)) {
				oldScoreField = field;
				break;
			}
		}
		if(oldScoreField == null) {
			throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", OldScore.class));
		}
		Object oldScore = OrangeReflectionUtils.getFieldValue(oldScoreField, member);
		return getNullableScore(oldScore,OldScore.class);
	}
}