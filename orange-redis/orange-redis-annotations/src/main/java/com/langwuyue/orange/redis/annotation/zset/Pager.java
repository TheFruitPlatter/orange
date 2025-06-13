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
 * Indicates that a parameter should be treated as pagination information for Redis sorted set (ZSet) operations.
 * This annotation enables paginated access to ZSet members, allowing retrieval of members in smaller, manageable chunks.
 * The pagination is implemented using Redis ZRANGE command with LIMIT option, providing efficient access to large datasets.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see PageNo
 * @see ScoreRange
 * @see WithScores
 * @see Reverse
 * @see MinScore
 * @see MaxScore
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Pager {
	
}