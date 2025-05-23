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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeSetIfAbsentFailedEvent {
	
	private Object value;
	
	private Object[] args;
	
	private Exception exception;
	
	private String reason;

	public OrangeSetIfAbsentFailedEvent(Object[] args, Object value,Exception exception) {
		this.args = args;
		this.value = value;
		this.exception = exception;
		this.reason = exception.getMessage();
	}
	
	public OrangeSetIfAbsentFailedEvent(Object[] args, Object value, String reason) {
		this.args = args;
		this.value = value;
		this.reason = reason;
	}

	public Object getValue() {
		return value;
	}

	public Exception getException() {
		return exception;
	}

	public String getReason() {
		return reason;
	}

	public Object[] getArgs() {
		return args;
	}
}
