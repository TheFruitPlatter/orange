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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRenewTimerWheel {
	
	private Thread renewThread;
	
	private OrangeRenewTimerWorker worker;
	
	private OrangeRedisLogger logger;
	
	private boolean enabled;
	
	public OrangeRenewTimerWheel(OrangeAutoRenewProperties properties,OrangeRedisLogger logger) {
		this.enabled = properties.isEnabled();
		if(this.enabled) {
			this.worker = new OrangeRenewTimerWorker(properties,logger);
			this.logger = logger;
			this.renewThread = new Thread(this.worker,"orange-redis-auto-renew-ttl");
			this.renewThread.start();
		}
	}
	
	public void addRenewTask(OrangeRenewTask task) {
		if(this.enabled) {
			this.worker.addTask(task);
		}else{
			throw new OrangeRedisException("Invalid operation, auto-renewal has been disabled. To enable, set `orange.redis.auto-renew.enabled=true` in application.yml.");
		}
	}
	
	public void destory() {
		if(this.enabled) {
			this.worker.destory();
			this.renewThread.interrupt();
		}
	}
}
