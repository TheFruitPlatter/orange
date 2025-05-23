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
package com.langwuyue.orange.redis.template.value;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.value.GetValue;
import com.langwuyue.orange.redis.annotation.value.OrangeRedisValueClient;
import com.langwuyue.orange.redis.annotation.value.SetValue;
import com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate;

/**
 * Interface template for Redis Value operations. 
 * Developers should extend this interface and annotate the child interface with {@code OrangeRedisKey}.
 * 
 * <p>A example is:
 * <blockquote><pre>
 *  {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:value:example1")} 
 *  public interface OrangeRedisValueExample1Api extends JSONOperationsTemplate{@code<Value>} {
 *  	
 *  	{@code @Override}
 *  	Value getValue();
 *  
 *  	// Custom operations can be added here
 *  }
 * </pre></blockquote>
 * </p>
 * 
 * <p>Please review examples for more information.</p>
 * 
 * @param <T> The type of elements stored in the Redis Value (will be serialized as JSON)
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisValueClient(valueType = RedisValueTypeEnum.JSON)
public interface JSONOperationsTemplate<T> extends GlobalOperationsTemplate {
	
	/**
	 * Set value
	 * 
	 * 
	 * @param value
	 */
	@SetValue
	void setValue(@RedisValue T value);
	
	/**
	 * Set value with with a time-to-live (TTL).
	 * 
	 * @param value
	 */
	@SetValue
	@SetExpiration
	void setValueWithExpiration(@RedisValue T value);
	
	
	/**
	 * <h3>Set {@code value} if it does not already exists.</h3>
	 * <p>
	 * This method is present for setting a default value.
	 * </p>
	 * <p>
	 * To set a time-to-live (TTL) for the value at the same time, invoke {@link JSONOperationsTemplate#setValueIfAbsentWithExpiration(Object)} instead.
	 * </p>
	 * <p>
	 * Once the set operation process is completed, the {@code OrangeRedisValueSetIfAbsentListener} component ​​will be triggered​​. 
	 * Developers should ​​configure​​ {@code OrangeRedisValueSetIfAbsentListener} to manage post-setting business logic. 
	 * Note that the {@code OrangeRedisValueSetIfAbsentListener} implementation class must be annotated with Spring’s {@code @Component}
	 * </p>
	 * 
	 * <p>
	 * The property {@code deleteInTheEnd} of {@code @IfAbsent} determines if the value is deleted after operation completion.
	 * True​​: Delete | ​​False​​: Keep
	 * </p>
	 * 
	 * <p>
	 * Please review examples for more information.
	 * </p>
	 * 
	 * @param value the value.
	 */
	@SetValue
	@IfAbsent(deleteInTheEnd = false)
	void setValueIfAbsent(@RedisValue T value);
	
	/**
	 * <h3>Set {@code value} if it does not already exists, and set a time-to-live (TTL) for the value at the same time.</h3>
	 * 
	 * <p>
	 * Once the set operation process is completed, the {@code OrangeRedisValueSetIfAbsentListener} component ​​will be triggered​​. 
	 * Developers should ​​configure​​ {@code OrangeRedisValueSetIfAbsentListener} to manage post-setting business logic. 
	 * Note that the {@code OrangeRedisValueSetIfAbsentListener} implementation class must be annotated with Spring’s {@code @Component}
	 * </p>
	 * 
	 * <p>
	 * The property {@code deleteInTheEnd} of {@code @IfAbsent} determines if the value is deleted after operation completion.
	 * True​​: Delete | ​​False​​: Keep
	 * </p>
	 * 
	 * <p>
	 * Please review examples for more information.
	 * </p>
	 * 
	 * @param value the value.
	 */
	@SetValue
	@IfAbsent(deleteInTheEnd = true)
	@SetExpiration
	void setValueIfAbsentWithExpiration(@RedisValue T value);
	
	/**
	 * The {@code oldValue} will be overwritten by {@code newValue} only if it matches current value.
	 * 
	 * 
	 * @param oldValue  The expected current value.
	 * @param newValue  The new value to set if verification succeeds.
	 * @return Boolean  True if the value was updated, false otherwise (e.g., if {@code oldValue} did not match).
	 */
	@CAS
	Boolean compareAndSwap(@RedisOldValue T oldValue, @RedisValue T newValue);
	
	/**
	 * Get value.
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
	 * @return value
	 */
	@GetValue
	T getValue();

}
