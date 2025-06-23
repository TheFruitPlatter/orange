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
package com.langwuyue.orange.redis.timer;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.operations.OrangeRedisValueOperations;

/**
 * A specialized renewal task for Redis value-type locks.
 * 
 * This class extends OrangeRenewTask to handle the renewal of locks that are stored
 * as values in Redis. It uses OrangeRedisValueOperations to perform the actual
 * expiration time updates in Redis.
 * 
 * The task will attempt to extend the lock's expiration time when executed,
 * unless it has been marked for removal.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeValueLockRenewTask extends OrangeRenewTask {
	
	/**
	 * Redis value operations used to renew the lock's expiration time.
	 */
	private OrangeRedisValueOperations operations;
	
	/**
	 * Constructs a new value lock renewal task.
	 *
	 * @param key The Redis key object containing key name and expiration information
	 * @param operations The Redis value operations to use for lock renewal
	 * @param value The value stored in the lock, used for verification
	 * @param valueType The enum indicating the type of value stored in Redis
	 * @param threshold The number of renewal rounds to perform before the task expires
	 */
	public OrangeValueLockRenewTask(
		Key key,
		OrangeRedisValueOperations operations,
		Object value,
		RedisValueTypeEnum valueType,
		int threshold
	) {
		super(key, null, value, valueType, threshold);
		this.operations = operations;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * For value-type locks, this method attempts to extend the lock's expiration time in Redis
	 * using the configured operations. If the task is marked for removal, it will not attempt
	 * to renew the lock.
	 *
	 * @return true if the expiration time was successfully updated, false if the task is marked
	 *         for removal or the update failed
	 * @throws Exception if there is an error while attempting to update the expiration time
	 */
	@Override
	public boolean finish() throws Exception {
		if(isRemove()) {
			return false;
		}
		return this.operations.expire(this.getKey().getValue(), this.getKey().getExpirationTime(), this.getKey().getExpirationTimeUnit());
	}
}