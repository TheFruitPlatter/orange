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

import javax.validation.constraints.NotBlank;

import com.langwuyue.orange.redis.annotation.zset.MaxLex;
import com.langwuyue.orange.redis.annotation.zset.MinLex;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeLexRangeEntity {
	@NotBlank
	@MaxLex
	private String maxLex;
	@NotBlank
	@MinLex
	private String minLex;
	public String getMaxLex() {
		return maxLex;
	}
	public void setMaxLex(String maxLex) {
		this.maxLex = maxLex;
	}
	public String getMinLex() {
		return minLex;
	}
	public void setMinLex(String minLex) {
		this.minLex = minLex;
	}
}
