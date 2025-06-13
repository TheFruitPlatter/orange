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
package com.langwuyue.orange.redis.annotation.zset;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a parameter as a rank range specification for Redis sorted set (ZSet) operations.
 * This annotation is used to query members based on their position (rank) in the sorted set.
 * 
 * <p>In Redis ZSet, ranks are 0-based indices representing the position of members when ordered by score:
 * <ul>
 *   <li>Rank 0 = member with the lowest score (in ascending order)</li>
 *   <li>Rank 1 = member with the second-lowest score</li>
 *   <li>And so on...</li>
 * </ul>
 * 
 * <p>The rank range is inclusive at both ends, meaning both the start and end ranks are included in the results.
 * 
 * <p>When used with {@link Reverse}, the ranks are counted from the highest score instead of the lowest score.
 * This is useful for getting top-N style results.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Reverse
 * @see WithScores
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RankRange {
	
}