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

import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.StartIndex;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRankRangeEntity {
	
	@StartIndex
	private long startIndex;
	@EndIndex
	private long endIndex;
	
	public OrangeRankRangeEntity(long startIndex, long endIndex) {
		super();
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	public long getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(long startIndex) {
		this.startIndex = startIndex;
	}
	public long getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(long endIndex) {
		this.endIndex = endIndex;
	}

}
