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
package com.langwuyue.orange.redis.listener.value;

/**
 * Event class representing a successful set-if-absent operation for Redis value type.
 * 
 * <p>This event is triggered when a Redis set-if-absent operation (similar to SETNX command)
 * successfully sets a value because the key did not previously exist. The event contains
 * the value that was set and any additional arguments that were provided during the operation.
 * 
 * <p>This class is used as a parameter type in {@link OrangeRedisValueSetIfAbsentListener#onSuccess(Object)}
 * to provide information about the successful operation to event handlers.
 * 
 * <p>Example usage:
 * <pre>{@code
 * @Override
 * public void onSuccess(OrangeSetIfAbsentSuccessEvent event) {
 *     Object value = event.getValue();
 *     Object[] args = event.getArgs();
 *     
 *     logger.info("Successfully set value: {}", value);
 *     // Process additional arguments if needed
 *     if (args.length > 0) {
 *         logger.info("Operation arguments: {}", Arrays.toString(args));
 *     }
 * }
 * }</pre>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisValueSetIfAbsentListener
 */
public class OrangeSetIfAbsentSuccessEvent {
	
	private Object value;
	
	private Object[] args;
	
	public OrangeSetIfAbsentSuccessEvent(Object[] args,Object value) {
		this.value = value;
		this.args = args;
	}

	public Object getValue() {
		return value;
	}

	public Object[] getArgs() {
		return args;
	}
	
}