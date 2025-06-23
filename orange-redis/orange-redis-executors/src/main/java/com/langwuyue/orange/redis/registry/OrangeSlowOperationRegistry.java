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
package com.langwuyue.orange.redis.registry;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Registry for tracking and monitoring slow Redis operations.
 * 
 * This class provides a centralized registry for collecting statistics about slow Redis
 * operations. It tracks methods that exceed the configured threshold duration, recording
 * their execution times and invocation counts. The registry uses soft references to allow
 * garbage collection of unused entries when memory is constrained.
 * 
 * The registry is thread-safe and designed for concurrent access in multi-threaded
 * environments. It provides methods to register slow operations and retrieve statistics
 * about all tracked operations.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeSlowOperationRegistry {
	
	/**
	 * Thread-safe registry mapping methods to their slow operation statistics.
	 * Uses soft references to allow garbage collection when memory is constrained.
	 */
	private static final Map<Method,SoftReference<SlowInfo>> SLOW_OPERATION_REGISTRY = new ConcurrentHashMap<>();
	
	/**
	 * Lock object for synchronizing creation of new SlowInfo instances.
	 */
	private static final Object LOCK_REF = new Object();
	
	/**
	 * Private constructor to prevent instantiation of this utility class.
	 */
	private OrangeSlowOperationRegistry() {}
	
	/**
	 * Registers a slow operation with its execution cost.
	 * 
	 * This method records information about a method that has been identified as a slow
	 * operation. It creates or updates statistics for the method, including its total
	 * execution time and invocation count.
	 * 
	 * The implementation uses a thread-safe approach with soft references to handle
	 * potential garbage collection of entries. If a soft reference has been cleared,
	 * a new SlowInfo instance is created under synchronization.
	 * 
	 * @param operationOwner The class that owns the slow operation method
	 * @param method The method that was executed slowly
	 * @param cost The execution time of the operation in milliseconds
	 */
	public static void register(Class<?> operationOwner, Method method, long cost) {
		SoftReference<SlowInfo> softRef = SLOW_OPERATION_REGISTRY.computeIfAbsent(
		    method, 
		    k -> new SoftReference<>(new SlowInfo(operationOwner, method))
		);
		SlowInfo info = softRef.get();
		if (info == null) {
		    // If the soft reference was cleared by GC, create a new one under synchronization
		    synchronized (LOCK_REF) {
		        softRef = SLOW_OPERATION_REGISTRY.computeIfAbsent(
		            method, 
		            k -> new SoftReference<>(new SlowInfo(operationOwner, method))
		        );
		        info = softRef.get(); 
		    }
		}
		info.increment(cost);
	}
	
	/**
	 * Container class for storing statistics about slow operations.
	 * 
	 * This class tracks information about methods that have been identified as slow,
	 * including their total execution time, invocation count, method signature, and
	 * the class that owns the method.
	 */
	public static class SlowInfo {
		
		/** Total execution time of all invocations (derived from accumulator) */
		private long totalExecutionTime;
		
		/** Total count of invocations (derived from counter) */
		private long count;
		
		/** String representation of the method */
		private String method;
		
		/** String representation of the operation owner class */
		private String operationOwnewr;
		
		/** Thread-safe accumulator for total execution time */
		private AtomicLong totalExecutionTimeAccumulator = new AtomicLong(0);
		
		/** Thread-safe counter for invocation count */
		private AtomicLong counter = new AtomicLong(0);

		/**
		 * Creates a new SlowInfo instance for the specified method.
		 * 
		 * @param operationOwner The class that owns the method
		 * @param method The method being tracked
		 */
		public SlowInfo(Class<?> operationOwner,Method method) {
			super();
			this.method = method.toString();
			this.operationOwnewr = operationOwner.toString();
		}
		
		/**
		 * Increments the execution time and invocation count for this method.
		 * 
		 * @param executionTime The execution time to add in milliseconds
		 */
		public void increment(long executionTime) {
			this.totalExecutionTime = this.totalExecutionTimeAccumulator.addAndGet(executionTime);
			this.count = this.counter.incrementAndGet();
		}
		
		/**
		 * Gets the total execution time of all invocations.
		 * 
		 * @return The total execution time in milliseconds
		 */
		public long getTotalExecutionTime() {
			return totalExecutionTime;
		}
		
		/**
		 * Gets the total count of invocations.
		 * 
		 * @return The invocation count
		 */
		public long getCount() {
			return count;
		}
		
		/**
		 * Gets the string representation of the method.
		 * 
		 * @return The method string
		 */
		public String getMethod() {
			return method;
		}
		
		/**
		 * Gets the string representation of the operation owner class.
		 * 
		 * @return The operation owner string
		 */
		public String getOperationOwnewr() {
			return operationOwnewr;
		}
	}

	public static List<SlowInfo> getRegistry() {
		return SLOW_OPERATION_REGISTRY.values().stream().map(SoftReference::get).collect(Collectors.toList());
	}
}