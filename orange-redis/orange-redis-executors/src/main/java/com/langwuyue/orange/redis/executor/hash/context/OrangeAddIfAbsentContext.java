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
package com.langwuyue.orange.redis.executor.hash.context;

import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;

/**
 * Context interface for Redis hash conditional add operations, used for scenarios where hash members are added only when the key doesn't exist.
 * 
 * <p>This interface defines the context information needed for conditional add operations, including whether to delete the key after the operation,
 * the member information to be added, and the key's data type.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeAddIfAbsentContext {
	
	/**
	 * Determines whether the key should be deleted after the operation is completed.
	 * 
	 * @return true if the key should be deleted after the operation, false otherwise
	 */
	boolean isDeleteInTheEnd();
	
	/**
	 * Gets the hash member to be added.
	 * 
	 * @return Map containing field-value pairs
	 */
	Map getMember();
	
	/**
	 * Gets the data type of the key.
	 * 
	 * @return Redis value type enum
	 */
	RedisValueTypeEnum getKeyType();
}