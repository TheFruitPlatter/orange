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
package com.langwuyue.orange.redis;

/**
 * Redis list operation direction enum, defines directions for moving or inserting list elements.
 * 
 * <p>This enum is mainly used in Redis list operations to specify the direction
 * for moving or inserting elements, such as {@code LPOP}, {@code RPOP}, {@code LPUSH}, {@code RPUSH} operations.</p>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public enum ListMoveDirectionEnum {
	
	/**
	 * Left/head direction of the list
	 * <p>Example: Pop element from list head (LPOP), or insert element to list head (LPUSH)</p>
	 */
	LEFT, 
	
	/**
	 * Right/tail direction of the list
	 * <p>Example: Pop element from list tail (RPOP), or insert element to list tail (RPUSH)</p>
	 */
	RIGHT;
}