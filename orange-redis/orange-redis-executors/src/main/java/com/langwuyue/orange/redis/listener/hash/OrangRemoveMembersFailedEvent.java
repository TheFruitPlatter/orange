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
package com.langwuyue.orange.redis.listener.hash;

import java.util.Collection;
import java.util.Map;

/**
 * Event representing failures in batch Redis HASH field removal operations.
 *
 * <p>This event extends {@link OrangeAddMembersIfAbsentEvent} to track failures when
 * attempting to remove multiple fields from a Redis hash in a single operation.
 * It provides detailed information about the batch operation results.
 *
 * @see OrangeAddMembersIfAbsentEvent for base functionality
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangRemoveMembersFailedEvent extends OrangeAddMembersIfAbsentEvent {

	/**
	 * The actual number of fields successfully removed from the Redis hash.
	 * This value is always non-negative and less than or equal to the expected count.
	 * The difference between expected and removed counts indicates the number of failed
	 * or unknown status removals.
	 */
	private long removed;
	
	/**
	 * The number of fields that were expected to be removed from the Redis hash.
	 * This value represents the total size of the field collection passed to the
	 * original remove operation. It should be equal to the sum of successful removals,
	 * failed removals, and unknown status removals.
	 */
	private long expceted;
	
	/**
	 * The overall exception that caused the batch removal operation to fail, if any.
	 * This may be null if individual removals failed but the overall operation completed.
	 * When this is non-null, it indicates a catastrophic failure that prevented proper
	 * execution of the batch operation (e.g., connection failure, timeout).
	 */
	private Exception exception;

	/**
	 * Constructs a new batch remove hash fields failure event.
	 *
	 * @param args Original operation arguments, typically contains:
	 *             <ul>
	 *               <li>Redis hash key at index 0</li>
	 *               <li>Collection of fields to remove at index 1</li>
	 *             </ul>
	 * @param successMembers Collection of fields that were successfully removed
	 * @param failedMembers Map of fields that failed to remove with their corresponding exceptions
	 * @param unknownMembers Collection of fields with unknown removal status
	 * @param removed Actual number of fields removed (may be less than expected)
	 * @param expceted Expected number of fields to remove
	 * @param exception Overall exception if the entire operation failed
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
	 * Returns the actual number of fields successfully removed from the Redis hash.
	 *
	 * <p>This count can be used to determine the partial success rate of the operation
	 * by comparing it with the expected count.
	 *
	 * @return the number of successfully removed fields, always non-negative
	 */
	public long getRemoved() {
		return removed;
	}

	/**
	 * Sets the actual number of fields successfully removed from the Redis hash.
	 *
	 * @param removed the number of successfully removed fields, should be non-negative
	 */
	public void setRemoved(long removed) {
		this.removed = removed;
	}

	/**
	 * Returns the number of fields that were expected to be removed from the Redis hash.
	 *
	 * <p>This represents the total size of the field collection passed to the original
	 * remove operation. The difference between this value and {@link #getRemoved()} indicates
	 * the number of fields that failed to be removed.
	 *
	 * @return the expected number of fields to remove, always non-negative
	 */
	public long getExpceted() {
		return expceted;
	}

	/**
	 * Sets the number of fields that were expected to be removed from the Redis hash.
	 *
	 * @param expceted the expected number of fields to remove, should be non-negative
	 */
	public void setExpceted(long expceted) {
		this.expceted = expceted;
	}

	/**
	 * Returns the overall exception that caused the batch removal operation to fail, if any.
	 *
	 * <p>This may be null if individual removals failed but the overall operation completed.
	 * When non-null, it indicates a catastrophic failure that prevented proper execution
	 * of the batch operation (e.g., connection failure, timeout).
	 *
	 * <p>For individual field removal failures, use {@code getFailedMembers()} inherited
	 * from the parent class.
	 *
	 * @return the exception that caused the overall operation to fail, or null if no overall exception occurred
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * Sets the overall exception that caused the batch removal operation to fail.
	 *
	 * @param exception the exception that caused the overall operation to fail, may be null
	 *                  if only individual field removals failed
	 */
	public void setException(Exception exception) {
		this.exception = exception;
	}
}