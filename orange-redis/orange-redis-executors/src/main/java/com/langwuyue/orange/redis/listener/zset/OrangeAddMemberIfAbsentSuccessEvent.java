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
package com.langwuyue.orange.redis.listener.zset;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAddMemberIfAbsentSuccessEvent {

	private Object value;
	
	private Double score;

	private Object[] args;

	public OrangeAddMemberIfAbsentSuccessEvent(Object[] args, Object value, Double score) {
		this.value = value;
		this.args = args;
		this.score = score;
	}

	public Object getValue() {
		return value;
	}

	public Double getScore() {
		return score;
	}

	public Object[] getArgs() {
		return args;
	}
}
