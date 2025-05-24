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
package com.langwuyue.orange.redis.template.multiplelocks;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AutoRenew;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.global.Delete;
import com.langwuyue.orange.redis.annotation.global.GetExpiration;
import com.langwuyue.orange.redis.annotation.global.SetExpiration;
import com.langwuyue.orange.redis.annotation.multiplelocks.MultipleLocks;
import com.langwuyue.orange.redis.annotation.multiplelocks.OrangeRedisMultipleLocksClient;
import com.langwuyue.orange.redis.annotation.transaction.Release;

/**
 * Interface template for Redis multiple locks operations. 
 * Developers should extend this interface and annotate the child interface with {@code OrangeRedisKey}.
 * 
 * <p>A example is:
 * <blockquote><pre>
 *  {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:multiplelocks:example1")} 
 *  public interface OrangeRedisMultipleLocksExample1Api extends JSONOperationsTemplate{@code<Value>} {
 *  	// Custom operations can be added here
 *  }
 * </pre></blockquote>
 * 
 * 
 * <p>Please review examples for more information.
 * 
 * @param <T> The type of the lock key (will be serialized as JSON)
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisMultipleLocksClient(valueType = RedisValueTypeEnum.JSON)
public interface JSONOperationsTemplate<T> {
	
	/**
	 * Get locks
	 * 
	 * 
	 * <p>
	 * Once the set operation process is completed, the {@code OrangeRedisMultipleLocksListener} component ​​will be triggered​​. 
	 * Developers should ​​configure​​ {@code OrangeRedisMultipleLocksListener} to manage post-setting business logic. 
	 * Note that the {@code OrangeRedisMultipleLocksListener} implementation class must be annotated with Spring’s {@code @Component}
	 * 
	 * 
	 * <p>
	 * This method automatically extends the TTL if the listeners are still executing when the current TTL approaches expiration.
	 * Automatically extends the TTL when:
	 * - Remaining TTL ≤ {@link AutoRenew#threshold()} * {@link com.langwuyue.orange.redis.annotation.OrangeRedisKey#expirationTime()}  
	 * 
	 * 
	 * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines whether the method lock remaining keys upon exceptions.
	 * True: continue | False: break
	 * 
	 * 
	 * <p>
	 * The {@code @SetExpiration} annotation will automatically set a TTL (time-to-live) for each lock.
	 * 
	 * 
	 * 
	 * @param targets the lock keys
	 */
	@MultipleLocks
	@SetExpiration
	@ContinueOnFailure(true)
	@AutoRenew(autoInitKeyExpirationTime = true)
	void lock(@Multiple Collection<T> targets);
	
	/**
	 * Releases all locks forcibly.</3> 
	 * 
	 * 
	 * @return a boolean value, true if all the locks were successfully released.
	 */
	@Delete
	Boolean releaseAll();
	
	/**
	 * Release locks
	 * 
	 * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines 
	 * whether the method continues to release subsequent locks individually 
	 * even if an exception occurs or some releases fail.
	 * True: continue | False: break
	 * 
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @param targets the locks to be released.
	 * @return LinkedHashMap where keys are the lock keys and values indicate whether each lock was successfully released
	 */
	@Release
	@ContinueOnFailure(true)
	Map<T,Boolean> release(@Multiple Collection<T> targets);
	
	/**
	 * Retrieves the remaining time-to-live (TTL) values for the specified locks
	 * 
	 * <p>
	 * The {@code @ContinueOnFailure} annotation determines 
	 * whether the method continues to retrieve TTL values for subsequent locks 
	 * even if an exception occurs or some keys get fail.
	 * True: continue | False: break
	 * 
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @param targets the targets need to get expiration.
	 * @return LinkedHashMap where keys are the lock keys and values are the expirations of the lock keys.
	 */
	@GetExpiration
	@ContinueOnFailure(true)
	Map<T,Long> getExpiration(@Multiple Collection<T> targets);
	
	/**
	 * Retrieves the remaining time-to-live (TTL) values for the specified locks
	 * 
	 * <p>
	 * The {@code @ContinueOnFailure} annotation determines 
	 * whether the method continues to retrieve TTL values for subsequent locks 
	 * even if an exception occurs or some keys get fail.
	 * True: continue | False: break
	 * 
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @param targets the targets need to get expiration.
	 * @param unit 	  the unit of the returned TTL values.
	 * @return LinkedHashMap where keys are the lock keys and values are the expirations of the lock keys.
	 */
	@GetExpiration
	@ContinueOnFailure(true)
	Map<T,Long> getExpiration(@Multiple Collection<T> targets,@TimeoutUnit TimeUnit unit);
}
