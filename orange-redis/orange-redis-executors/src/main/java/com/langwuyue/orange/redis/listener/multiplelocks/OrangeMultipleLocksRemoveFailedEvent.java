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
package com.langwuyue.orange.redis.listener.multiplelocks;

import java.util.Collection;
import java.util.Map;

import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;

/**
 * Event representing failed attempts to remove multiple Redis locks.
 *
 * <p>This event extends {@link OrangeMultipleLocksEvent} to track specific
 * failure details when removing distributed locks, including:
 * <ul>
 *   <li>The number of locks actually removed vs expected</li>
 *   <li>The exception that caused the failure</li>
 *   <li>The specific Redis key involved</li>
 * </ul>
 *
 * <p>This class is immutable - all fields are final and collections are
 * stored as references to the original collections passed to the constructor.
 * Callers should not modify these collections after passing them to the event.
 *
 * <p>Typical usage includes:
 * <ul>
 *   <li>Logging lock removal failures</li>
 *   <li>Implementing retry logic</li>
 *   <li>Monitoring lock removal patterns</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMultipleLocksRemoveFailedEvent extends OrangeMultipleLocksEvent {

	/**
	 * The actual number of locks that were successfully removed before the failure.
	 * <p>
	 * This value will always be less than {@link #expceted} when the operation fails.
	 * The value is non-negative and represents the partial success of the removal
	 * operation before the failure occurred.
	 */
	private long removed;
	
	/**
	 * The expected number of locks that should have been removed (spelled as 'expceted').
	 * <p>
	 * This is the target count that was attempted but not achieved due to the failure.
	 * The value is always positive and greater than {@link #removed} in failure cases.
	 */
	private long expceted;
	
	/**
	 * The exception that caused the lock removal failure.
	 * <p>
	 * Contains the root cause of the failure, which could be from Redis operations,
	 * network issues, or other system problems. Never null in this event class.
	 */
	private Exception exception;
	
	/**
	 * The Redis key that was involved in the failed lock removal operation.
	 * <p>
	 * Identifies the specific lock key where the removal failed. Provides context
	 * for debugging and monitoring. Never null.
	 */
	private Key redisKey;

	/**
	 * Creates a new failed lock removal event with detailed operation results.
	 *
	 * @param args Additional arguments used in the lock operation 
	 * @param successMembers Collection of successfully locked resources 
	 * @param failedMembers Map of failed lock attempts with associated exceptions 
	 * @param unknownMembers Collection of resources with unknown lock status 
	 * @param removed Number of locks actually removed (must be &gt;= 0 and &lt;= expceted)
	 * @param expceted Expected number of locks to remove (must be &gt; 0 and &gt;= removed)
	 * @param exception The failure exception that occurred 
	 * @param redisKey The Redis key involved in the failure 
	 */
	public OrangeMultipleLocksRemoveFailedEvent(
		Object[] args, 
		Collection<Object> successMembers, 
		Map<Object,Exception> failedMembers,
		Collection<Object> unknownMembers,
		long removed,
		long expceted,
		Exception exception,
		Key redisKey
	) {
		super(args,successMembers,failedMembers,unknownMembers);
		this.removed = removed;
		this.expceted = expceted;
		this.redisKey = redisKey;
		this.exception = exception;
	}

	/**
	 * Returns the number of locks actually removed before the failure occurred.
	 *
	 * @return The count of successfully removed locks (always >= 0)
	 */
	public long getRemoved() {
		return removed;
	}

	/**
	 * Returns the expected number of locks that should have been removed.
	 *
	 * @return The target removal count (always > 0)
	 */
	public long getExpceted() {
		return expceted;
	}

	/**
	 * Returns the exception that caused the lock removal failure.
	 *
	 * @return The failure exception (never null)
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Returns the Redis key involved in the failed lock removal.
	 *
	 * @return The Redis key where removal failed (never null)
	 */
	public Key getRedisKey() {
		return redisKey;
	}
}