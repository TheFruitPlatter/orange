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
 * Metadata container for Redis key information.
 * 
 * This class stores metadata about Redis keys, including the actual key value,
 * variable key patterns, and associated operation classes. It's used by the
 * Redis key registry system to manage and validate key naming conventions.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisKeyMetaData {
	
	/**
	 * Pattern used to identify and process variable markers in key patterns.
	 * This pattern matches the variable marker character defined in OrangeRedisKeyRegistry.
	 */
	private static final Pattern VARIABLE_MARK_PAT_PATTERN = Pattern.compile("\\"+OrangeRedisKeyRegistry.VARIABLE_MARK_CHAR);
	
	/**
	 * The actual Redis key.
	 */
	private String key;
	
	/**
	 * The key pattern with variable placeholders.
	 * For example: "user:{userId}:profile"
	 */
	private String variableKey;
	
	/**
	 * The key pattern with variable placeholders removed.
	 * For example, if variableKey is "user:{userId}:profile", 
	 * variableKeyPrefix would be "user::profile"
	 */
	private String variableKeyPrefix;
	
	/**
	 * The operation class associated with this key.
	 * This represents the class that performs operations using this key.
	 */
	private Class<?> operationClass;
	
	/**
	 * Creates a new Redis key metadata instance.
	 * 
	 * @param key The actual Redis key
	 * @param variableKey The key pattern with variable placeholders
	 * @param operationClass The operation class associated with this key
	 */
	public OrangeRedisKeyMetaData(String key, String variableKey, Class<?> operationClass) {
		super();
		this.key = key;
		this.variableKey = variableKey;
		this.operationClass = operationClass;
		// Calculate the variable key prefix by removing all variable markers
		this.variableKeyPrefix = variableKey == null ? null : VARIABLE_MARK_PAT_PATTERN.matcher(variableKey).replaceAll("");
	}

	/**
	 * Gets the actual Redis key.
	 * 
	 * @return The Redis key string
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Gets the key pattern with variable placeholders.
	 * 
	 * @return The variable key pattern (e.g., "user:{userId}:profile")
	 */
	public String getVariableKey() {
		return variableKey;
	}

	/**
	 * Gets the key pattern with variable placeholders removed.
	 * 
	 * @return The variable key prefix (e.g., "user::profile" if variableKey is "user:{userId}:profile")
	 */
	public String getVariableKeyPrefix() {
		return variableKeyPrefix;
	}

	/**
	 * Gets the operation class associated with this key.
	 * 
	 * @return The class that performs operations using this key
	 */
	public Class<?> getOperationClass() {
		return operationClass;
	}

	/**
	 * Generates a hash code for this metadata object.
	 * The hash is based on the key and operation class.
	 * 
	 * @return The hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(key, operationClass);
	}

	/**
	 * Compares this metadata object with another object for equality.
	 * Two metadata objects are considered equal if they have the same key and operation class.
	 * 
	 * @param obj The object to compare with
	 * @return true if the objects are equal, false otherwise
	 */
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