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
package com.langwuyue.orange.example.redis.response;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisExampleResponse {
	
	private int errorCode;
	
	private Object data;
	
	private String errorMessage;
	
	public OrangeRedisExampleResponse() {}

	public OrangeRedisExampleResponse(int errorCode, String errorMessage) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public OrangeRedisExampleResponse(Object data) {
		super();
		this.data = data;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public Object getData() {
		return data;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	

}
