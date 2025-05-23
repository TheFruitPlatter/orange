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
package com.langwuyue.orange.redis.listener.hash;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentFailedEvent {
	
	private Object[] args;
	
	private Exception exception;
	
	private String message;
	
	public OrangeAddMemberIfAbsentFailedEvent(Object[] args, Exception exception) {
		super();
		this.args = args;
		this.exception = exception;
		this.message = exception.getMessage();
	}

	public Object[] getArgs() {
		return args;
	}

	public Exception getException() {
		return exception;
	}

	public String getMessage() {
		return message;
	}
	
}
