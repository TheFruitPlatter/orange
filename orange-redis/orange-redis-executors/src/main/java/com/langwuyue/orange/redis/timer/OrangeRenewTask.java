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
import com.langwuyue.orange.redis.executor.hash.OrangeCompareAndSwapExecutor;

/**
 * A task that manages the automatic renewal of Redis keys before they expire.
 * 
 * This class implements a mechanism to periodically renew Redis keys using a Compare-and-Swap (CAS)
 * operation to ensure atomic updates. It tracks various timing parameters such as expiration time,
 * renewal threshold, and deadline, and can be linked in a doubly-linked list structure for
 * efficient task management.
 * 
 * The renewal process uses CAS operations to safely update key expiration times without losing
 * updates in concurrent scenarios. Each task can be configured with a threshold that determines
 * when the renewal should occur before the actual expiration time.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRenewTask {
	
	/** Flag indicating whether this task should be removed from the renewal queue */
	private boolean remove;
	
	/** Executor for performing Compare-and-Swap operations in Redis */
	private OrangeCompareAndSwapExecutor executor;
	
	/** The Redis key associated with this renewal task */
	private Key key;
	
	/** The type of value stored in Redis */
	private RedisValueTypeEnum valueType;
	
	/** The value associated with the Redis key */
	private Object value;
	
	/** The timestamp when the key will expire (in milliseconds) */
	private long deadlineMillis;
	
	/** The timestamp when this renewal task started (in milliseconds) */
	private long startMillis;
	
	/** The threshold time before expiration when renewal should occur */
	private long renewThreshold;
	
	/** The total expiration time in milliseconds */
	private long expirationMillis;
	
	/** Counter for tracking renewal rounds */
	private long round;
	
	/** Reference to the task link that manages this task */
	private OrangeTaskLink link;
	
	/** Reference to the next task in the linked list */
	private OrangeRenewTask next;
	
	/** Reference to the previous task in the linked list */
	private OrangeRenewTask prev;
	
	/**
	 * Constructs a new renewal task with the specified parameters.
	 * 
	 * @param key The Redis key to be renewed
	 * @param executor The executor for performing CAS operations
	 * @param value The value associated with the key
	 * @param valueType The type of the value stored in Redis
	 * @param threshold The divisor used to calculate the renewal threshold (e.g., if threshold=4, 
	 *                  renewal occurs after 75% of the expiration time has elapsed)
	 */
	public OrangeRenewTask(
		Key key,
		OrangeCompareAndSwapExecutor executor,
		Object value,
		RedisValueTypeEnum valueType,
		int threshold
	) {
		init(key,executor,value,valueType,threshold);
	}
	
	/**
	 * Initializes or reinitializes this renewal task with the specified parameters.
	 * 
	 * This method sets up all the necessary parameters for the renewal task, including
	 * calculating the expiration time in milliseconds and the renewal threshold based
	 * on the provided threshold divisor.
	 * 
	 * @param key The Redis key to be renewed
	 * @param executor The executor for performing CAS operations
	 * @param value The value associated with the key
	 * @param valueType The type of the value stored in Redis
	 * @param threshold The divisor used to calculate the renewal threshold
	 */
	public void init(
		Key key,
		OrangeCompareAndSwapExecutor executor,
		Object value,
		RedisValueTypeEnum valueType,
		int threshold
	) {
		this.executor = executor;
		this.key = key;
		this.value = value;
		this.valueType = valueType;
		this.expirationMillis = this.key.getExpirationTimeUnit().toMillis(this.key.getExpirationTime());
		// Calculate the threshold time as a fraction of the total expiration time
		// For example, if threshold=4, renewal occurs after 75% of expiration time
		this.renewThreshold = this.expirationMillis / threshold * (threshold - 1);
		computeDeadlineMillis();
	}
	
	/**
	 * Computes the deadline timestamp for this key's expiration.
	 * 
	 * This method sets the start time to the current system time and calculates
	 * the deadline by adding the expiration duration to the start time.
	 * 
	 * @return The calculated deadline timestamp in milliseconds
	 */
	long computeDeadlineMillis() {
		this.startMillis = System.currentTimeMillis(); 
		this.deadlineMillis = this.startMillis + this.expirationMillis;
		return this.deadlineMillis;
	}
	
	/**
	 * Completes the renewal task by performing a final CAS operation.
	 * 
	 * This method performs a final Compare-and-Swap operation to update the key's value
	 * and expiration time. It will not execute if the task is marked for removal.
	 * The operation compares the current deadline with a newly computed deadline to
	 * ensure atomic updates.
	 *
	 * @return true if the CAS operation was successful, false if the task is marked for removal
	 *         or if the CAS operation failed
	 * @throws Exception if there's an error during the CAS operation
	 */
	public boolean finish() throws Exception {
		if(remove) {
			return false;
		}
		return executor.doCAS(
			this.key.getValue(), 
			this.value, 
			this.valueType, 
			this.deadlineMillis, 
			computeDeadlineMillis(), 
			RedisValueTypeEnum.LONG
		);
	}

	/**
	 * Sets the removal flag for this task.
	 * When set to true, the task will be removed from the renewal queue.
	 *
	 * @param remove true to mark this task for removal, false otherwise
	 */
	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	/**
	 * Gets the timestamp when this renewal task started.
	 *
	 * @return the start time in milliseconds since epoch
	 */
	public long getStartMillis() {
		return startMillis;
	}

	/**
	 * Checks if this task is marked for removal.
	 *
	 * @return true if the task should be removed from the renewal queue, false otherwise
	 */
	public boolean isRemove() {
		return remove;
	}

	/**
	 * Gets the CAS executor associated with this task.
	 *
	 * @return the executor for performing Compare-and-Swap operations
	 */
	public OrangeCompareAndSwapExecutor getExecutor() {
		return executor;
	}

	/**
	 * Gets the Redis key associated with this task.
	 *
	 * @return the Redis key object
	 */
	public Key getKey() {
		return key;
	}

	/**
	 * Gets the type of value stored in Redis.
	 *
	 * @return the Redis value type enum
	 */
	public RedisValueTypeEnum getValueType() {
		return valueType;
	}

	/**
	 * Gets the value associated with the Redis key.
	 *
	 * @return the value object
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Gets the timestamp when the key will expire.
	 *
	 * @return the deadline in milliseconds since epoch
	 */
	public long getDeadlineMillis() {
		return deadlineMillis;
	}

	/**
	 * Gets the threshold time before expiration when renewal should occur.
	 *
	 * @return the renewal threshold in milliseconds
	 */
	public long getRenewThreshold() {
		return renewThreshold;
	}

	/**
	 * Gets the total expiration time for the key.
	 *
	 * @return the expiration time in milliseconds
	 */
	public long getExpirationMillis() {
		return expirationMillis;
	}

	/**
	 * Gets the number of successful renewals performed.
	 *
	 * @return the round counter
	 */
	public long getRound() {
		return round;
	}

	/**
	 * Sets the round counter for tracking renewal attempts.
	 *
	 * @param round the new round value
	 */
	void setRound(long round) {
		this.round = round;
	}

	/**
	 * Sets the CAS executor for this task.
	 *
	 * @param executor the executor to set
	 */
	void setExecutor(OrangeCompareAndSwapExecutor executor) {
		this.executor = executor;
	}

	/**
	 * Sets the Redis key for this task.
	 *
	 * @param key the key to set
	 */
	void setKey(Key key) {
		this.key = key;
	}

	/**
	 * Sets the type of value stored in Redis.
	 *
	 * @param valueType the value type to set
	 */
	void setValueType(RedisValueTypeEnum valueType) {
		this.valueType = valueType;
	}

	/**
	 * Sets the value associated with the Redis key.
	 *
	 * @param value the value to set
	 */
	void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Sets the timestamp when the key will expire.
	 *
	 * @param deadlineMillis the deadline in milliseconds since epoch
	 */
	void setDeadlineMillis(long deadlineMillis) {
		this.deadlineMillis = deadlineMillis;
	}

	/**
	 * Sets the timestamp when this renewal task started.
	 *
	 * @param startMillis the start time in milliseconds since epoch
	 */
	void setStartMillis(long startMillis) {
		this.startMillis = startMillis;
	}

	/**
	 * Sets the threshold time before expiration when renewal should occur.
	 *
	 * @param renewThreshold the renewal threshold in milliseconds
	 */
	void setRenewThreshold(long renewThreshold) {
		this.renewThreshold = renewThreshold;
	}

	/**
	 * Sets the total expiration time for the key.
	 *
	 * @param expirationMillis the expiration time in milliseconds
	 */
	void setExpirationMillis(long expirationMillis) {
		this.expirationMillis = expirationMillis;
	}

	/**
	 * Gets the task link that manages this renewal task.
	 * The task link provides the connection to the task management system
	 * that coordinates multiple renewal tasks.
	 *
	 * @return the OrangeTaskLink instance managing this task
	 */
	public OrangeTaskLink getLink() {
		return link;
	}

	/**
	 * Sets the task link that manages this renewal task.
	 * This method is used when integrating the task into the task management system.
	 *
	 * @param link the OrangeTaskLink instance to manage this task
	 */
	void setLink(OrangeTaskLink link) {
		this.link = link;
	}

	/**
	 * Gets the next task in the doubly-linked list.
	 * This is used for traversing the task chain in forward direction.
	 *
	 * @return the next OrangeRenewTask in the list, or null if this is the last task
	 */
	public OrangeRenewTask getNext() {
		return next;
	}

	/**
	 * Sets the next task in the doubly-linked list.
	 * This method is used when inserting or removing tasks from the list.
	 *
	 * @param next the OrangeRenewTask to set as the next task
	 */
	void setNext(OrangeRenewTask next) {
		this.next = next;
	}

	/**
	 * Gets the previous task in the doubly-linked list.
	 * This is used for traversing the task chain in reverse direction.
	 *
	 * @return the previous OrangeRenewTask in the list, or null if this is the first task
	 */
	public OrangeRenewTask getPrev() {
		return prev;
	}

	/**
	 * Sets the previous task in the doubly-linked list.
	 * This method is used when inserting or removing tasks from the list.
	 *
	 * @param prev the OrangeRenewTask to set as the previous task
	 */
	void setPrev(OrangeRenewTask prev) {
		this.prev = prev;
	}

	/**
	 * Returns a string representation of this renewal task.
	 * 
	 * Includes all relevant task information:
	 * - Removal status
	 * - Executor reference
	 * - Key details
	 * - Value type and value
	 * - Timing information (deadline, start time, threshold, expiration)
	 * - Round counter
	 *
	 * @return a string containing the task's current state
	 */
	@Override
	public String toString() {
		return "OrangeRenewTask [remove=" + remove + ", executor=" + executor + ", key=" + key + ", valueType="
				+ valueType + ", value=" + value + ", deadlineMillis=" + deadlineMillis + ", startMillis=" + startMillis
				+ ", renewThreshold=" + renewThreshold + ", expirationMillis=" + expirationMillis + ", round=" + round
				+ "]";
	}
	
	
}