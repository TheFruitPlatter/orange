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
 * Marks a parameter as a score range for Redis sorted set (ZSet) operations.
 * This annotation is used to specify the score range criteria when querying members from a ZSet.
 * 
 * <p>The annotated parameter should represent a range of scores to filter ZSet members.
 * Score ranges are inclusive by default at both ends, meaning members with scores equal to
 * the minimum or maximum bounds will be included in the results.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see MinScore
 * @see MaxScore
 * @see WithScores
 * @see Reverse
 * @see Pager
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScoreRange {
	
}