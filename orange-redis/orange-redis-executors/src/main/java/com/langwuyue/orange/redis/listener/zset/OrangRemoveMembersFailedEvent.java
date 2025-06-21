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
package com.langwuyue.orange.redis.listener.zset;

import java.util.Collection;
import java.util.Map;

/**
 * Event representing failed batch member removal operations in a Redis sorted set (ZSET).
 * <p>
 * This event extends {@link OrangeAddMembersIfAbsentEvent} to track batch removal failures,
 * including metrics about the expected vs actual number of removed members.
 * <p>
 * Contains information about:
 * <ul>
 *   <li>The original operation arguments</li>
 *   <li>Collections of successfully processed members</li>
 *   <li>Members that failed with exceptions</li>
 *   <li>Members with unknown status</li>
 *   <li>Count of actually removed members</li>
 *   <li>Count of expected removals</li>
 *   <li>The root cause exception (if any)</li>
 * </ul>
 * <p>
 * Typically triggered by {@link OrangeRedisZSetAddMembersIfAbsentListener} implementations
 * when batch removal operations fail during "add if absent" processing.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangRemoveMembersFailedEvent extends OrangeAddMembersIfAbsentEvent {

	/** 
	 * The actual number of members successfully removed in the batch operation.
	 * This may be less than the expected count due to failures.
	 */
	private long removed;
	
	/** 
	 * The expected number of members to be removed in the batch operation.
	 * Note: Field name contains typo (should be 'expected').
	 */
	private long expceted;
	
	/**
	 * The root cause exception that triggered this failure event.
	 * May be null if the failure wasn't caused by a specific exception.
	 */
	private Exception exception;

	/**
	 * Creates a new batch removal failure event.
	 *
	 * @param args Original arguments passed to the batch operation
	 * @param successMembers Collection of members that were successfully processed
	 * @param failedMembers Map of members that failed with their corresponding exceptions
	 * @param unknownMembers Collection of members with unknown processing status
	 * @param removed Actual number of members removed (may be less than expected)
	 * @param expceted Expected number of members to be removed
	 * @param exception Root cause exception for the batch failure (may be null)
	 */
	public OrangRemoveMembersFailedEvent(
		Object[] args, 
		Collection<Object> successMembers, 
		Map<Object,Exception> failedMembers,
		Collection<Object> unknownMembers,
		long removed, 
		long expceted,
		Exception exception
	) {
		super(args,successMembers,failedMembers,unknownMembers);
		this.removed = removed;
		this.expceted = expceted;
		this.exception = exception;
	}

	/**
	 * Gets the actual number of members removed in the batch operation.
	 * 
	 * @return Count of successfully removed members (may be less than expected)
	 */
	public long getRemoved() {
		return removed;
	}

	/**
	 * Gets the expected number of members to be removed in the batch operation.
	 * 
	 * @return Expected count of members to remove (may differ from actual removed count)
	 */
	public long getExpceted() {
		return expceted;
	}

	/**
	 * Gets the root cause exception for this batch removal failure.
	 * <p>
	 * May return null if the failure wasn't caused by a specific exception,
	 * such as when members simply weren't found in the sorted set.
	 *
	 * @return The exception that caused the failure, or null if no specific exception
	 */
	public Exception getException() {
		return exception;
	}
}