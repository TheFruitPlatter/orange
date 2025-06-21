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

/**
 * Event representing a failed member removal operation in a Redis sorted set (ZSET).
 * <p>
 * This event extends {@link OrangeAddMemberIfAbsentFailedEvent} to represent failures
 * that occur during removal operations rather than addition operations.
 * <p>
 * Contains information about:
 * <ul>
 *   <li>The original operation arguments</li>
 *   <li>The member value that failed to be removed</li>
 *   <li>The member's score in the sorted set</li>
 *   <li>The failure reason (either as Exception or String)</li>
 * </ul>
 * <p>
 * Typically triggered by {@link OrangeRedisZSetAddMemberIfAbsentListener} implementations
 * when removal operations fail during "add if absent" processing.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRemoveMemberFailedEvent extends OrangeAddMemberIfAbsentFailedEvent {

	/**
	 * Creates a new removal failure event with an Exception as the failure reason.
	 *
	 * @param args The original arguments passed to the remove operation
	 * @param value The member value that failed to be removed
	 * @param score The member's score in the sorted set
	 * @param exception The exception that caused the removal failure
	 */
	public OrangeRemoveMemberFailedEvent(Object[] args, Object value, Double score, Exception exception) {
		super(args, value, score, exception);
	}

	/**
	 * Creates a new removal failure event with a String description of the failure reason.
	 *
	 * @param args The original arguments passed to the remove operation
	 * @param value The member value that failed to be removed
	 * @param score The member's score in the sorted set
	 * @param reason Description of why the removal failed (when no Exception is available)
	 */
	public OrangeRemoveMemberFailedEvent(Object[] args, Object value, Double score, String reason) {
		super(args, value, score, reason);
	}
}