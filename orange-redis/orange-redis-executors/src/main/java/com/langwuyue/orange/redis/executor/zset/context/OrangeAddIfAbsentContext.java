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
package com.langwuyue.orange.redis.executor.zset.context;

import com.langwuyue.orange.redis.operations.OrangeRedisZSetOperations.ZSetEntry;

/**
 * Interface defining the contract for conditional addition operations to Redis ZSets.
 * 
 * <p>This interface specifies the methods required for implementing conditional
 * "add if absent" operations on Redis sorted sets. It provides:
 * <ul>
 *   <li>Methods to determine cleanup behavior after operations</li>
 *   <li>Access to the member being conditionally added</li>
 * </ul>
 * 
 * <p>Implementations of this interface handle the specific logic for:
 * <ul>
 *   <li>Adding a member only if it doesn't already exist in the ZSet</li>
 *   <li>Managing temporary keys created during conditional operations</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeAddIfAbsentContext {
	
    /**
     * Determines whether temporary keys should be deleted after the operation completes.
     * 
     * <p>This method is used to control the cleanup behavior of temporary keys that
     * might be created during conditional addition operations. When implementing
     * atomic conditional additions, temporary keys may be needed to ensure
     * consistency, and this method determines whether those keys should be
     * cleaned up afterward.
     *
     * @return true if temporary keys should be deleted after the operation completes,
     *         false if temporary keys should be preserved
     */
    boolean isDeleteInTheEnd();
    
    /**
     * Gets the member that should be conditionally added to the ZSet.
     * 
     * <p>This method provides access to the ZSetEntry that represents the member
     * to be added if it doesn't already exist in the sorted set. The ZSetEntry
     * contains both:
     * <ul>
     *   <li>The member value to be added</li>
     *   <li>The score associated with the member</li>
     * </ul>
     *
     * @return the ZSetEntry representing the member to be conditionally added,
     *         never null
     */
    ZSetEntry getMember();
	
}