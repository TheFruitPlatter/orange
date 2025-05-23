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
package com.langwuyue.orange.redis.listener.set;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentFailedEvent {

	private Object member;
	
	private String reason;
	
	private Exception exception;

	private Object[] args;

	public OrangeAddMemberIfAbsentFailedEvent(Object[] args, Object member, String reason) {
		this.member = member;
		this.args = args;
		this.reason = reason;
	}
	
	public OrangeAddMemberIfAbsentFailedEvent(Object[] args, Object member, Exception exception) {
		this.member = member;
		this.args = args;
		this.reason = exception.getMessage();
		this.exception = exception;
	}

	public Object getMember() {
		return member;
	}

	public String getReason() {
		return reason;
	}

	public Exception getException() {
		return exception;
	}

	public Object[] getArgs() {
		return args;
	}
}
