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
package com.langwuyue.orange.example.redis.entity;

/**
 * Entity representing a Compare-And-Swap (CAS) operation for Redis values.
 * 
 * <p>This class is used to implement optimistic concurrency control by comparing
 * the current value (oldValue) with an expected value before updating to a new value.
 * If the oldValue matches the current value in Redis, the update is performed atomically.</p>
 * 
 * <p>Typical usage involves:
 * <ol>
 *   <li>Reading the current value from Redis</li>
 *   <li>Creating this entity with the read value as oldValue and desired changes as newValue</li>
 *   <li>Passing this entity to a CAS operation method</li>
 * </ol>
 * </p>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeValueExampleEntity The value type being compared and swapped
 */
public class OrangeCompareAndSwapEntity {
	
	private OrangeValueExampleEntity oldValue;
	
	private OrangeValueExampleEntity newValue;

	/**
	 * Gets the expected old value for the CAS operation.
	 * 
	 * @return The expected value that should be currently stored in Redis
	 */
	public OrangeValueExampleEntity getOldValue() {
		return oldValue;
	}

	/**
	 * Sets the expected old value for the CAS operation.
	 * 
	 * @param oldValue The expected value that should be currently stored in Redis
	 */
	public void setOldValue(OrangeValueExampleEntity oldValue) {
		this.oldValue = oldValue;
	}

	/**
	 * Gets the new value to be set if the CAS condition is met.
	 * 
	 * @return The new value to set if oldValue matches current Redis value
	 */
	public OrangeValueExampleEntity getNewValue() {
		return newValue;
	}

	/**
	 * Sets the new value to be set if the CAS condition is met.
	 * 
	 * @param newValue The new value to set if oldValue matches current Redis value
	 */
	public void setNewValue(OrangeValueExampleEntity newValue) {
		this.newValue = newValue;
	}
}