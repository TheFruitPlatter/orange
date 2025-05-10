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

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.zset.PageNo;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangePagerEntity {
	
	@PageNo
	private long pageNo;
	@Count
	private long count;
	
	public OrangePagerEntity(long pageNo, long count) {
		super();
		this.pageNo = pageNo;
		this.count = count;
	}
	public long getPageNo() {
		return pageNo;
	}
	public void setPageNo(long pageNo) {
		this.pageNo = pageNo;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
}
