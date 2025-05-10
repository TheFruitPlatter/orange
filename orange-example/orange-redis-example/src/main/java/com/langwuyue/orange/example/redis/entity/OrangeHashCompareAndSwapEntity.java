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

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeHashCompareAndSwapEntity {
	
	@NotNull
	@Valid
	private OrangeValueExampleEntity oldValue;
	
	@NotNull
	@Valid
	private OrangeValueExampleEntity newValue;
	
	@NotBlank
	private String key;

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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
