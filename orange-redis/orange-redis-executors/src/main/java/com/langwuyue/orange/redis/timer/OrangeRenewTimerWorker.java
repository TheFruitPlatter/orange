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

import java.util.concurrent.LinkedBlockingQueue;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRenewTimerWorker implements Runnable {
	
	private static final long MIN_TICK_DURATION_MILLIS = 1000;
	
	private int wheelSize;
	
	private LinkedBlockingQueue<OrangeRenewTask> newTaskQueue = new LinkedBlockingQueue<>();
	
	private OrangeTaskLink[] wheel;
	
	private long tick;
	
	private long tickDuration;
	
	private long startTime;
	
	private int maxPollSizePerRound;
	
	private OrangeRedisLogger logger;
	
	private boolean destory = false;
	
	public OrangeRenewTimerWorker(OrangeAutoRenewProperties properties,OrangeRedisLogger logger) {
		// Init timer wheel
		this.wheelSize = properties.getWheelSize();
		this.wheel = new OrangeTaskLink[wheelSize];
		for (int i = 0; i < wheelSize; i++) {
			this.wheel[i] = new OrangeTaskLink(logger);
		}
		
		// Check tick duration
		this.tickDuration = properties.getTickDuration().toMillis();
		if (this.tickDuration >= Long.MAX_VALUE / wheelSize) {
            throw new OrangeRedisException(String.format("The property 'tickDuration' must less than %s", Long.MAX_VALUE / wheelSize));
        }
        if (this.tickDuration < MIN_TICK_DURATION_MILLIS) {
            this.tickDuration = MIN_TICK_DURATION_MILLIS;
        }
        
		this.maxPollSizePerRound = wheelSize * 1000;
		this.logger = logger;
	}

	@Override
	public void run() {
		this.startTime = System.currentTimeMillis();
		while(true) {
			if(this.destory) {
				break;
			}
			try {
				
				this.logger.debug("Getting next tick, current tick is {} ", this.tick - 1);
				int tick = getNextTick();
				this.logger.debug("The tick for auto-renew is {}", tick);
				
				long start = System.currentTimeMillis();
				
				// Move new tasks to timer wheel. 
				newTaskQueue2Wheel();
				
				// Get link of current tick
				OrangeTaskLink link = this.wheel[tick];
				
				// Finish task
				link.expire((t) -> {
					this.logger.debug("{} auto-renew successfully", t);
					this.newTaskQueue.add(t);
				});
				
				this.logger.debug("The tick {} for auto-renew done", tick);
				
				// Calculate the execution time of this tick.
				// And warn the developers when execution time is greater than tick duration.
				long cost = System.currentTimeMillis() - start;
				if(cost >= this.tickDuration) {
					this.logger.warn("Tick {} execution time ({}ms) exceeded max tick duration ({}ms). Subsequent ticks may be delayed. Suggested Solutions: Increase tickDuration or set 'autoInitKeyExpirationTime'=true and set 'autoInitValue'>10s.", 
							tick, 
							cost, 
							this.tickDuration
					);
				}
			}catch (Exception e) {
				logger.error("Auto renew timer error", e);
			}
		}
	}
	
	private int getNextTick() throws InterruptedException {
		long tick = this.tick;
		long diff = (tick * this.tickDuration) - (System.currentTimeMillis() - this.startTime);
		if(diff <= 0) {
			++this.tick;
			return (int)(tick % this.wheelSize);
		}
		Thread.sleep(diff);
		++this.tick;
		return (int)(tick % this.wheelSize);
	}

	private void newTaskQueue2Wheel() {
		this.logger.debug("Scheduling new tasks into the timing wheel");
		for(int i = 0; i < this.maxPollSizePerRound; i++) {
			OrangeRenewTask task = this.newTaskQueue.poll();
			if(task == null) {
				break;
			}
			if(task.isRemove()) {
				this.logger.debug("{} was removed already, skip it.", task);
				continue;
			}
			int tick = (int)(task.getRenewThreshold()/this.tickDuration);
			int round = tick / this.wheelSize;
			task.setRound(round);
			int index = (int)((tick + this.tick - 1) % this.wheelSize);
			OrangeTaskLink link = this.wheel[index];
			link.add(task);
			this.logger.debug("{} successfully added to bucket with index {}", task, index);
		}
		this.logger.debug("All new tasks have been scheduled into the timing wheel");
	}
	
	public boolean addTask(OrangeRenewTask task) {
		long renewThreshold = task.getRenewThreshold();
		if(renewThreshold <= 0) {
			this.logger.debug("Task's 'renewThreshold' is less equals than zero, ignore this task.\n Task:{}",task);
			return false;
		}
		this.logger.debug("Adding a task to the pending queue.\n Task:{}",task);
		return this.newTaskQueue.add(task);
	}
	
	public void destory() {
		this.logger.info("Auto-renewal thread pool is shutting down now.");
		this.destory = true;
	}
}
