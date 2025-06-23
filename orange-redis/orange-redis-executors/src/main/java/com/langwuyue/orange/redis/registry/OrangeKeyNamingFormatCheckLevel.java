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

/**
 * Defines validation levels for Redis key naming format checks.
 * 
 * This interface provides constants representing different strictness levels
 * for validating Redis key naming patterns. These levels can be used to enforce
 * consistent key naming conventions across an application, helping to prevent
 * key collisions and improve maintainability.
 * 
 * The levels are ordered from least strict (level 1) to most strict (level 4).
 */
public interface OrangeKeyNamingFormatCheckLevel {

	/**
	 * Level 1 (Least strict): Ensures that each key is associated with only one operation.
	 * 
	 * This level prevents basic key collisions by ensuring that the same key pattern
	 * is not used for multiple different operations.
	 */
	int ONE_KEY_ONE_OPERATIONS = 1;
	
	/**
	 * Level 2: Ensures that variable parts of the key pattern appear only at the end.
	 * 
	 * This level enforces that any variable components in the key pattern (typically
	 * represented by placeholders) must be positioned at the end of the key.
	 * For example, "user:{userId}:profile" would be valid, but "user:{userId}:profile:{field}" 
	 * would not be valid if there are other keys with the same prefix but different variable positions.
	 */
	int VARIABLE_MUST_BE_LAST = 2;
	
	/**
	 * Level 3: Ensures that key patterns are distinct even after ignoring variable parts.
	 * 
	 * This level checks that key patterns remain unique when variable parts are removed.
	 * For example, "user:{userId}:profile" and "user:{id}:settings" would be considered
	 * similar after ignoring variables, and thus one of them would be invalid at this level.
	 */
	int SIMILAR_AFTER_IGNORE_VAR = 3;
	
	/**
	 * Level 4 (Most strict): Ensures strict prefix matching for all key patterns.
	 * 
	 * This level enforces that key patterns must have unique prefixes, preventing
	 * any potential for overlap. This is the most restrictive level and ensures
	 * complete separation between different key patterns.
	 */
	int STRICT_PREFIX_MATCH = 4;
}