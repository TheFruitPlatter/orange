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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeSlowOperationRegistry {
	
	private static final Map<Method,SoftReference<SlowInfo>> SLOW_OPERATION_REGISTRY = new ConcurrentHashMap<>();
	
	private static final Object lock = new Object();
	
	public static void register(Class<?> operationOwner, Method method, long cost) {
		SoftReference<SlowInfo> softReference = SLOW_OPERATION_REGISTRY.get(method);
		SlowInfo info = null;
		if(softReference == null) {
			synchronized (lock) {
				softReference = SLOW_OPERATION_REGISTRY.get(method);
				if(softReference == null) {
					info = new SlowInfo(operationOwner,method);
					softReference = new SoftReference<>(info);
					SLOW_OPERATION_REGISTRY.put(method, softReference);
				}
			}
		}else{
			info = softReference.get();
			if(info == null) {
				synchronized (lock) {
					softReference = SLOW_OPERATION_REGISTRY.get(method);
					info = softReference.get();
					if(info == null) {
						info = new SlowInfo(operationOwner,method);
						softReference = new SoftReference<>(info);
						SLOW_OPERATION_REGISTRY.put(method, softReference);
					}
				}
			}
		}
		info.increment(cost);
	}
	
	public static class SlowInfo {
		
		private long totalExecutionTime;
		private long count;
		private String method;
		private String operationOwnewr;
		private AtomicLong totalExecutionTimeAccumulator = new AtomicLong(0);
		private AtomicLong counter = new AtomicLong(0);

		public SlowInfo(Class<?> operationOwner,Method method) {
			super();
			this.method = method.toString();
			this.operationOwnewr = operationOwner.toString();
		}
		public void increment(long executionTime) {
			this.totalExecutionTime = this.totalExecutionTimeAccumulator.addAndGet(executionTime);
			this.count = this.counter.incrementAndGet();
		}
		public long getTotalExecutionTime() {
			return totalExecutionTime;
		}
		public long getCount() {
			return count;
		}
		public String getMethod() {
			return method;
		}
		public String getOperationOwnewr() {
			return operationOwnewr;
		}
	}

	public static List<SlowInfo> getRegistry() {
		return SLOW_OPERATION_REGISTRY.values().stream().map(SoftReference::get).collect(Collectors.toList());
	}
}
