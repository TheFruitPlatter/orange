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
package com.langwuyue.orange.redis.executor.transaction;

import java.time.Duration;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisTransactionProperties {
	
	/**
     * The delay before first execution after the GC thread is ready.
     */
	private	Duration gcThreadInitialDelay = Duration.ofMinutes(1);
	
	/**
	 * The GC thread scheduling interval impacts latency.
	 */
    private Duration gcThreadPeriod = Duration.ofMinutes(10);
    
    /**
     * The delay before first execution after the transaction callback thread is ready.
     */
    private Duration timeoutCallbackThreadInitialDelay = Duration.ofMinutes(1);
    
    /**
     * The callback thread scheduling interval impacts latency.
     */
    private Duration timeoutCallbackThreadPeriod = Duration.ofSeconds(1);
    
    /**
     * The threshold for begin callback.
     */
    private Duration timeoutThreshold = Duration.ofSeconds(6);
    
    /**
     * Period for callback.
     */
    private Duration timeoutCallbackPeriod = Duration.ofSeconds(1);
    
    /**
     * 
     * Max attempts.
     */
    private int timeoutCallbackTimes = 294;
    
    /**
     * Dead transaction retention threshold:
     * - Transactions no retry attempts
     *   will be kept for this duration before deletion.
     * - Set to 0 to disable retention (immediate cleanup).
     */
    private Duration deadTransactionKeepThreshold = Duration.ofDays(1);
    
    /**
     * ${spring.application.name}
     */
    private String serviceName;
    
    private boolean enabled = true;

	public Duration getGcThreadInitialDelay() {
		return gcThreadInitialDelay;
	}

	public void setGcThreadInitialDelay(Duration gcThreadInitialDelay) {
		this.gcThreadInitialDelay = gcThreadInitialDelay;
	}

	public Duration getGcThreadPeriod() {
		return gcThreadPeriod;
	}

	public void setGcThreadPeriod(Duration gcThreadPeriod) {
		this.gcThreadPeriod = gcThreadPeriod;
	}

	public Duration getTimeoutCallbackThreadInitialDelay() {
		return timeoutCallbackThreadInitialDelay;
	}

	public void setTimeoutCallbackThreadInitialDelay(Duration timeoutCallbackThreadInitialDelay) {
		this.timeoutCallbackThreadInitialDelay = timeoutCallbackThreadInitialDelay;
	}

	public Duration getTimeoutCallbackThreadPeriod() {
		return timeoutCallbackThreadPeriod;
	}

	public void setTimeoutCallbackThreadPeriod(Duration timeoutCallbackThreadPeriod) {
		this.timeoutCallbackThreadPeriod = timeoutCallbackThreadPeriod;
	}

	public Duration getTimeoutThreshold() {
		return timeoutThreshold;
	}

	public void setTimeoutThreshold(Duration timeoutThreshold) {
		this.timeoutThreshold = timeoutThreshold;
	}

	public Duration getTimeoutCallbackPeriod() {
		return timeoutCallbackPeriod;
	}

	public void setTimeoutCallbackPeriod(Duration timeoutCallbackPeriod) {
		this.timeoutCallbackPeriod = timeoutCallbackPeriod;
	}

	public int getTimeoutCallbackTimes() {
		return timeoutCallbackTimes;
	}

	public void setTimeoutCallbackTimes(int timeoutCallbackTimes) {
		this.timeoutCallbackTimes = timeoutCallbackTimes;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Duration getDeadTransactionKeepThreshold() {
		return deadTransactionKeepThreshold;
	}

	public void setDeadTransactionKeepThreshold(Duration deadTransactionKeepThreshold) {
		this.deadTransactionKeepThreshold = deadTransactionKeepThreshold;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
