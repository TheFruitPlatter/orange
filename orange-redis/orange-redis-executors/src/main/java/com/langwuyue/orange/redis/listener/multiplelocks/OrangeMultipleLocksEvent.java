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

/**
 * Event representing the results of attempting to acquire multiple Redis locks.
 *
 * <p>This event class tracks the outcome of distributed lock operations across
 * multiple resources, categorizing them into three groups:
 * <ul>
 *   <li><b>Successfully locked</b> resources</li>
 *   <li><b>Failed to lock</b> resources (with associated exceptions)</li>
 *   <li><b>Unknown status</b> resources (where lock acquisition status couldn't be determined)</li>
 * </ul>
 *
 * <p>This class is immutable - all collections are stored as references to the
 * original collections passed to the constructor. Callers should not modify these
 * collections after passing them to the event.
 *
 * <p>Typical usage includes:
 * <ul>
 *   <li>Logging lock acquisition results</li>
 *   <li>Implementing retry logic for failed locks</li>
 *   <li>Monitoring lock contention patterns</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeMultipleLocksEvent {

	/**
	 * Collection of resources that were successfully locked.
	 * <p>
	 * The collection contains the resource identifiers that were successfully
	 * locked during the operation.
	 * <p>
	 * The collection is stored as-is from the constructor argument - modifications
	 * to the original collection will be visible through this reference.
	 */
	private Collection<Object> successMembers;
	
	/**
	 * Collection of resources with unknown lock status.
	 * <p>
	 * Contains resource identifiers where the lock acquisition status couldn't
	 * be determined (e.g., due to connection issues).
	 * <p>
	 * The collection is stored as-is from the constructor argument - modifications
	 * to the original collection will be visible through this reference.
	 */
	private Collection<Object> unknownMembers;
	
	/**
	 * Map of resources that failed to lock with associated exceptions.
	 * <p>
	 * The map keys are resource identifiers and values are the exceptions that
	 * caused the lock failures. 
	 * <p>
	 * The map is stored as-is from the constructor argument - modifications
	 * to the original map will be visible through this reference.
	 */
	private Map<Object,Exception> failedMembers;
	
	/**
	 * Additional arguments used in the lock operation.
	 * <p>
	 * Contains any additional context or parameters that were used during
	 * the lock acquisition attempt.
	 * <p>
	 * The array is stored as a direct reference - modifications to the
	 * original array will be visible through this reference.
	 */
	private Object[] args;

	/**
	 * Creates a new multiple locks event with the given lock operation results.
	 *
	 * @param args Additional arguments used in the lock operation 
	 * @param successMembers Collection of successfully locked resources 
	 * @param failedMembers Map of failed lock attempts with associated exceptions 
	 * @param unknownMembers Collection of resources with unknown lock status 
	 */
	public OrangeMultipleLocksEvent(
		Object[] args, 
		Collection<Object> successMembers, 
		Map<Object,Exception> failedMembers,
		Collection<Object> unknownMembers
	) {
		this.successMembers = successMembers;
		this.failedMembers = failedMembers;
		this.unknownMembers = unknownMembers;
		this.args = args;
	}

	/**
	 * Returns the collection of successfully locked resources.
	 *
	 * @return Unmodifiable view of the successfully locked resources
	 */
	public Collection<Object> getSuccessMembers() {
		return successMembers;
	}

	/**
	 * Returns the collection of resources with unknown lock status.
	 *
	 * @return Unmodifiable view of resources with unknown lock status
	 */
	public Collection<Object> getUnknownMembers() {
		return unknownMembers;
	}

	/**
	 * Returns the map of failed lock attempts with associated exceptions.
	 *
	 * @return Unmodifiable view of failed lock attempts
	 */
	public Map<Object, Exception> getFailedMembers() {
		return failedMembers;
	}

	/**
	 * Returns additional arguments used in the lock operation.
	 *
	 * @return Array of additional arguments
	 */
	public Object[] getArgs() {
		return args;
	}
	
}