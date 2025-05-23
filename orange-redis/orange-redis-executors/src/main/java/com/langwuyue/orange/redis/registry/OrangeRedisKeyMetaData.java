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
package com.langwuyue.orange.redis.registry;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisKeyMetaData {
	
	private static final Pattern VARIABLE_MARK_PAT_PATTERN = Pattern.compile("\\"+OrangeRedisKeyRegistry.VARIABLE_MARK_CHAR);
	
	private String key;
	
	private String variableKey;
	
	private String variableKeyPrefix;
	
	private Class<?> operationClass;
	
	
	public OrangeRedisKeyMetaData(String key, String variableKey, Class<?> operationClass) {
		super();
		this.key = key;
		this.variableKey = variableKey;
		this.operationClass = operationClass;
		this.variableKeyPrefix = variableKey == null ? null : VARIABLE_MARK_PAT_PATTERN.matcher(variableKey).replaceAll("");
	}

	public String getKey() {
		return key;
	}

	public String getVariableKey() {
		return variableKey;
	}


	public String getVariableKeyPrefix() {
		return variableKeyPrefix;
	}


	public Class<?> getOperationClass() {
		return operationClass;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, operationClass);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrangeRedisKeyMetaData other = (OrangeRedisKeyMetaData) obj;
		return Objects.equals(key, other.key) && Objects.equals(operationClass, other.operationClass);
	}
}
