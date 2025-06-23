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
 * Worker implementation for the timer wheel that handles Redis key renewal tasks.
 * 
 * This class implements the core functionality of the timer wheel algorithm for scheduling
 * and executing Redis key renewal tasks. It maintains a circular array of task buckets (the wheel)
 * and processes tasks based on their scheduled execution time.
 * 
 * The worker runs in a dedicated thread and performs the following operations:
 * 1. Moves new tasks from the queue to the appropriate bucket in the wheel
 * 2. Executes tasks that are due in the current tick
 * 3. Reschedules tasks for future execution if needed
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRenewTimerWorker implements Runnable {
	
	/**
	 * Minimum allowed tick duration in milliseconds (1 second)
	 */
	private static final long MIN_TICK_DURATION_MILLIS = 1000;
	
	/**
	 * Size of the timer wheel (number of buckets)
	 */
	private int wheelSize;
	
	/**
	 * Queue for newly added tasks waiting to be scheduled into the wheel
	 */
	private LinkedBlockingQueue<OrangeRenewTask> newTaskQueue = new LinkedBlockingQueue<>();
	
	/**
	 * The timer wheel array, each element is a linked list of tasks
	 */
	private OrangeTaskLink[] wheel;
	
	/**
	 * Current tick counter
	 */
	private long tick;
	
	/**
	 * Duration of each tick in milliseconds
	 */
	private long tickDuration;
	
	/**
	 * Timestamp when the worker started
	 */
	private long startTime;
	
	/**
	 * Maximum number of tasks to poll from the queue in each round
	 */
	private int maxPollSizePerRound;
	
	/**
	 * Logger for recording events and errors
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Flag indicating whether the worker should be destroyed
	 * Note: Field name is misspelled (should be "destroy"), but kept as is for compatibility
	 */
	private boolean destory = false;
	
	/**
	 * Creates a new timer wheel worker with the specified configuration.
	 * 
	 * @param properties Configuration properties for the timer wheel, including wheel size and tick duration
	 * @param logger Logger for recording events and errors during operation
	 * @throws OrangeRedisException if the tick duration is invalid
	 */
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

	/**
	 * Main execution loop for the timer wheel worker.
	 * 
	 * This method runs continuously until the worker is destroyed, performing the following operations in each cycle:
	 * 1. Determines the next tick to process
	 * 2. Moves new tasks from the queue to the appropriate bucket in the wheel
	 * 3. Processes tasks that are due in the current tick
	 * 4. Monitors execution time and logs warnings if processing takes longer than the tick duration
	 * 
	 * The method handles all exceptions internally to prevent the worker thread from terminating unexpectedly.
	 */
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
	
	/**
	 * Calculates the next tick to process based on the current time.
	 * 
	 * This method determines how many ticks have elapsed since the worker started,
	 * taking into account the configured tick duration. If the next tick is not yet due,
	 * the method will sleep until it is time to process that tick.
	 * 
	 * @return The index in the wheel array for the next tick to process
	 * @throws InterruptedException if the thread is interrupted while sleeping
	 */
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

	/**
	 * Moves tasks from the new task queue to the appropriate buckets in the timer wheel.
	 * 
	 * This method processes up to {@link #maxPollSizePerRound} tasks from the queue in each call.
	 * For each task, it:
	 * 1. Calculates the appropriate bucket based on the task's renewal threshold
	 * 2. Determines how many complete wheel rotations will be needed before the task is due
	 * 3. Adds the task to the appropriate bucket in the wheel
	 * 
	 * Tasks that have been marked for removal are skipped.
	 */
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
	
	/**
	 * Adds a new renewal task to the worker's queue.
	 * 
	 * This method is thread-safe and can be called from multiple threads.
	 * The task will be added to the new task queue and will be scheduled into
	 * the timer wheel during the next processing cycle.
	 * 
	 * @param task The renewal task to add
	 * @return true if the task was successfully added to the queue, false otherwise
	 */
	public boolean addTask(OrangeRenewTask task) {
		long renewThreshold = task.getRenewThreshold();
		if(renewThreshold <= 0) {
			this.logger.debug("Task's 'renewThreshold' is less equals than zero, ignore this task.\n Task:{}",task);
			return false;
		}
		this.logger.debug("Adding a task to the pending queue.\n Task:{}",task);
		return this.newTaskQueue.add(task);
	}
	
	/**
	 * Initiates an orderly shutdown of the worker.
	 * 
	 * This method sets the destroy flag, which causes the worker's main loop
	 * to exit gracefully after completing its current tasks.
	 * 
	 * Note: The method name is intentionally spelled as "destory" (instead of "destroy")
	 * for backward compatibility reasons.
	 */
	public void destory() {
		this.logger.info("Auto-renewal thread pool is shutting down now.");
		this.destory = true;
	}
}