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

import java.util.Collection;
import java.util.Map;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangRemoveMembersFailedEvent extends OrangeAddMembersIfAbsentEvent {

	private long removed;
	
	private long expceted;
	
	private Exception exception;

	public OrangRemoveMembersFailedEvent(
		Object[] args, 
		Collection<Object> successMembers, 
		Map<Object,Exception> failedMembers,
		Collection<Object> unknownMembers,
		long removed, 
		long expceted,
		Exception exception
	) {
		super(args,successMembers,failedMembers,unknownMembers);
		this.removed = removed;
		this.expceted = expceted;
		this.exception = exception;
	}

	public long getRemoved() {
		return removed;
	}

	public void setRemoved(long removed) {
		this.removed = removed;
	}

	public long getExpceted() {
		return expceted;
	}

	public void setExpceted(long expceted) {
		this.expceted = expceted;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
}
