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

import com.langwuyue.orange.example.redis.entity.OrangeZSetExampleEntity.ZSetValue;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeZSetCASExampleEntity {
	
	private ZSetValue value;
	
	private Double oldScore;
	
	private Double newScore;

	public ZSetValue getValue() {
		return value;
	}

	public void setValue(ZSetValue value) {
		this.value = value;
	}

	public Double getOldScore() {
		return oldScore;
	}

	public void setOldScore(Double oldScore) {
		this.oldScore = oldScore;
	}

	public Double getNewScore() {
		return newScore;
	}

	public void setNewScore(Double newScore) {
		this.newScore = newScore;
	}
}
