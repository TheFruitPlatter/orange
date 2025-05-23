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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRenewTask {
	
	private boolean remove;
	
	private OrangeCompareAndSwapExecutor executor;
	
	private Key key;
	
	private RedisValueTypeEnum valueType;
	
	private Object value;
	
	private long deadlineMillis;
	
	private long startMillis;
	
	private long renewThreshold;
	
	private long expirationMillis;
	
	private long round;
	
	private OrangeTaskLink link;
	
	private OrangeRenewTask next;
	
	private OrangeRenewTask prev;
	
	public OrangeRenewTask(
		Key key,
		OrangeCompareAndSwapExecutor executor,
		Object value,
		RedisValueTypeEnum valueType,
		int threshold
	) {
		init(key,executor,value,valueType,threshold);
	}
	
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
		this.renewThreshold = this.expirationMillis / threshold * (threshold - 1);
		computeDeadlineMillis();
	}
	
	long computeDeadlineMillis() {
		this.startMillis = System.currentTimeMillis(); 
		this.deadlineMillis = this.startMillis + this.expirationMillis;
		return this.deadlineMillis;
	}
	
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

	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	public long getStartMillis() {
		return startMillis;
	}

	public boolean isRemove() {
		return remove;
	}

	public OrangeCompareAndSwapExecutor getExecutor() {
		return executor;
	}

	public Key getKey() {
		return key;
	}

	public RedisValueTypeEnum getValueType() {
		return valueType;
	}

	public Object getValue() {
		return value;
	}

	public long getDeadlineMillis() {
		return deadlineMillis;
	}

	public long getRenewThreshold() {
		return renewThreshold;
	}

	public long getExpirationMillis() {
		return expirationMillis;
	}

	public long getRound() {
		return round;
	}

	void setRound(long round) {
		this.round = round;
	}

	void setExecutor(OrangeCompareAndSwapExecutor executor) {
		this.executor = executor;
	}

	void setKey(Key key) {
		this.key = key;
	}

	void setValueType(RedisValueTypeEnum valueType) {
		this.valueType = valueType;
	}

	void setValue(Object value) {
		this.value = value;
	}

	void setDeadlineMillis(long deadlineMillis) {
		this.deadlineMillis = deadlineMillis;
	}

	void setStartMillis(long startMillis) {
		this.startMillis = startMillis;
	}

	void setRenewThreshold(long renewThreshold) {
		this.renewThreshold = renewThreshold;
	}

	void setExpirationMillis(long expirationMillis) {
		this.expirationMillis = expirationMillis;
	}

	public OrangeTaskLink getLink() {
		return link;
	}

	void setLink(OrangeTaskLink link) {
		this.link = link;
	}

	public OrangeRenewTask getNext() {
		return next;
	}

	void setNext(OrangeRenewTask next) {
		this.next = next;
	}

	public OrangeRenewTask getPrev() {
		return prev;
	}

	void setPrev(OrangeRenewTask prev) {
		this.prev = prev;
	}

	@Override
	public String toString() {
		return "OrangeRenewTask [remove=" + remove + ", executor=" + executor + ", key=" + key + ", valueType="
				+ valueType + ", value=" + value + ", deadlineMillis=" + deadlineMillis + ", startMillis=" + startMillis
				+ ", renewThreshold=" + renewThreshold + ", expirationMillis=" + expirationMillis + ", round=" + round
				+ "]";
	}
	
	
}
