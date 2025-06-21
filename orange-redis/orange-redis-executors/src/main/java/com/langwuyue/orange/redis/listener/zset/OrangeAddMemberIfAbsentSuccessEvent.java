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
 * Represents a success event when a member is successfully added to a Redis sorted set.
 * <p>
 * This event triggers when the 'add if absent' operation completes successfully.
 * It contains all relevant information about the successful operation including:
 * <ul>
 *   <li>The member value that was added</li>
 *   <li>The score associated with the member</li>
 *   <li>The original method arguments</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentSuccessEvent {

	/** The member value that was successfully added to the sorted set */
	private Object value;
	
	/** The score associated with the member in the sorted set */
	private Double score;

	/** Original method arguments that led to this operation */
	private Object[] args;

	/**
	 * Creates a new success event for a sorted set addition operation.
	 *
	 * @param args Original method arguments
	 * @param value The member value that was added
	 * @param score The score associated with the member
	 */
	public OrangeAddMemberIfAbsentSuccessEvent(Object[] args, Object value, Double score) {
		this.value = value;
		this.args = args;
		this.score = score;
	}

	/**
	 * Gets the member value that was successfully added to the sorted set.
	 *
	 * @return The member value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Gets the score that was associated with the member in the sorted set.
	 *
	 * @return The score value, or null if no score was specified
	 */
	public Double getScore() {
		return score;
	}

	/**
	 * Gets the original method arguments that led to this operation.
	 *
	 * @return Array of arguments passed to the original method call
	 */
	public Object[] getArgs() {
		return args;
	}
}