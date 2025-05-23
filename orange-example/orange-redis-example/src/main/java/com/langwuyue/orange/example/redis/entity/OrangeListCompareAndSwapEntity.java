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
package com.langwuyue.orange.example.redis.entity;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeListCompareAndSwapEntity {
	
	private OrangeValueExampleEntity oldValue;
	
	private OrangeValueExampleEntity newValue;
	
	private Long index;

	public OrangeValueExampleEntity getOldValue() {
		return oldValue;
	}

	public void setOldValue(OrangeValueExampleEntity oldValue) {
		this.oldValue = oldValue;
	}

	public OrangeValueExampleEntity getNewValue() {
		return newValue;
	}

	public void setNewValue(OrangeValueExampleEntity newValue) {
		this.newValue = newValue;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}
}
