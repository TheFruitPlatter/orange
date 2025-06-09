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
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;
import com.langwuyue.orange.redis.utils.OrangeReflectionUtils;

/**
 * Base context class for Redis ZSet member operations.
 * 
 * <p>This class provides core functionality for handling Redis ZSet members, including:
 * <ul>
 *   <li>Member to ZSetEntry conversion</li>
 *   <li>Score and value field validation</li>
 *   <li>Annotation processing for member fields</li>
 * </ul>
 * 
 * <p>The class supports both direct ZSetEntry instances and annotated objects as members.
 * For annotated objects, it requires:
 * <ul>
 *   <li>A field annotated with {@link Score} for the score value</li>
 *   <li>A field annotated with {@link RedisValue} for the member value</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMemberContext extends OrangeRedisContext {

    /**
     * Constructs a new OrangeMemberContext.
     *
     * @param operationOwner The class that owns the Redis operation
     * @param operationMethod The method representing the Redis operation
     * @param args The arguments passed to the operation method
     * @param redisKey The Redis key for the operation
     * @param valueType The type of values stored in the Redis ZSet
     */
    public OrangeMemberContext(
        Class<?> operationOwner, 
        Method operationMethod, 
        Object[] args, 
        Key redisKey,
        RedisValueTypeEnum valueType
    ) {
        super(operationOwner, operationMethod, args, redisKey, valueType);
    }

    /**
     * Converts a member object to a ZSetEntry.
     * 
     * <p>This method handles three cases:
     * <ul>
     *   <li>null input - returns null</li>
     *   <li>ZSetEntry input - returns the input directly</li>
     *   <li>Annotated object - converts to ZSetEntry using reflection</li>
     * </ul>
     *
     * <p>For annotated objects, the method:
     * <ul>
     *   <li>Locates fields with {@link Score} and {@link RedisValue} annotations</li>
     *   <li>Validates field types and values</li>
     *   <li>Creates a new ZSetEntry with the extracted score and value</li>
     * </ul>
     *
     * @param member The member object to convert
     * @param annotationClass The annotation class used on the member parameter, for error messages
     * @return A ZSetEntry representing the member, or null if the input is null
     * @throws OrangeRedisException if the member object is invalid or missing required annotations
     */
    protected ZSetEntry toZSetEntry(Object member, Class<? extends Annotation> annotationClass) {
        if(member == null) {
            return null;
        }
        if(member instanceof ZSetEntry) {
            return (ZSetEntry)member;
        }
        Field scoreField = null;
        Field valueField = null;
        Field[] fields = member.getClass().getDeclaredFields();
        for(Field field : fields) {
            if(field.isAnnotationPresent(Score.class)) {
                scoreField = field;
            }
            else if(field.isAnnotationPresent(RedisValue.class)) {
                valueField = field;
            }
            if(scoreField != null && valueField != null) {
                break;
            }
        }
        if(scoreField == null || valueField == null) {
            throw new OrangeRedisException(String.format("The argument annotated with @%s must have two fields annotated with @%s and @%s", annotationClass,RedisValue.class,Score.class));
        }
        Object score = OrangeReflectionUtils.getFieldValue(scoreField, member);
        if(score == null) {
            return null;
        }
        if(!(score instanceof Number) && !(score instanceof String)) {
            throw new OrangeRedisException(String.format("The field annotated with @%s must be a number or a string", Score.class));
        }
        Object value = OrangeReflectionUtils.getFieldValue(valueField, member);
        if(value == null) {
            throw new OrangeRedisException(String.format("The field annotated with @%s cannot be null", RedisValue.class));
        }
        return newZSetEntry(value,score);
    }
    
    /**
     * Creates a new ZSetEntry instance with the given value and score.
     * 
     * <p>The score is converted to a Double value using its string representation.
     *
     * @param value The value for the ZSet entry
     * @param score The score for the ZSet entry, can be a Number or String that can be parsed to a Double
     * @return A new ZSetEntry instance
     */
    protected ZSetEntry newZSetEntry(Object value, Object score) {
        return new ZSetEntry(value, Double.valueOf(score.toString()));
    }
}