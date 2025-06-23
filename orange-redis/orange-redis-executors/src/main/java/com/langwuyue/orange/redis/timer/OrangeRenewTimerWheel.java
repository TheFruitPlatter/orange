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

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * Timer wheel implementation for managing automatic renewal tasks for Redis keys.
 * 
 * This class is responsible for creating and managing a background thread that periodically
 * executes registered renewal tasks. When auto-renewal is enabled, a worker thread is created
 * to handle all renewal operations.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRenewTimerWheel {
	
	/**
	 * Background thread for executing renewal tasks
	 */
	private Thread renewThread;
	
	/**
	 * Worker for renewal tasks, responsible for actual task scheduling and execution
	 */
	private OrangeRenewTimerWorker worker;
	
	/**
	 * Logger for recording events and errors during the renewal process
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Flag indicating whether auto-renewal is enabled
	 */
	private boolean enabled;
	
	/**
	 * Creates a new timer wheel instance.
	 * 
	 * If auto-renewal is enabled (determined by properties.isEnabled()),
	 * a background thread will be created and started to execute renewal tasks.
	 * 
	 * @param properties Configuration properties for auto-renewal, including whether auto-renewal is enabled
	 * @param logger Logger for recording log information during the renewal process
	 */
	public OrangeRenewTimerWheel(OrangeAutoRenewProperties properties,OrangeRedisLogger logger) {
		this.enabled = properties.isEnabled();
		if(this.enabled) {
			this.worker = new OrangeRenewTimerWorker(properties,logger);
			this.logger = logger;
			this.renewThread = new Thread(this.worker,"orange-redis-auto-renew-ttl");
			this.renewThread.start();
		}
	}
	
	/**
	 * Adds a renewal task to the timer wheel.
	 * 
	 * Tasks can only be added when auto-renewal is enabled.
	 * An exception will be thrown if auto-renewal is disabled.
	 * 
	 * @param task The renewal task to be added
	 * @throws OrangeRedisException if auto-renewal is disabled
	 */
	public void addRenewTask(OrangeRenewTask task) {
		if(this.enabled) {
			this.worker.addTask(task);
		}else{
			throw new OrangeRedisException("Invalid operation, auto-renewal has been disabled. To enable, set `orange.redis.auto-renew.enabled=true` in application.yml.");
		}
	}
	
	/**
	 * Destroys the timer wheel instance and stops all renewal tasks.
	 * 
	 * Note: Method name is misspelled (should be "destroy"), but kept as is for compatibility.
	 * This method performs the following operations:
	 * 1. If auto-renewal is enabled:
	 *    - Stops the worker thread
	 *    - Waits for the worker thread to fully terminate
	 * 2. If auto-renewal is disabled, no action is taken
	 */
	public void destory() {
		if(this.enabled) {
			this.worker.destory();
			this.renewThread.interrupt();
		}
	}
}