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
 * Indicates that the Redis sorted set (ZSet) operation should return results in reverse order.
 * When applied to a method, the results will be returned in descending order instead of the default ascending order.
 * 
 * <p>This annotation is particularly useful when you need to:
 * <ul>
 *   <li>Get highest scoring members first (descending score order)</li>
 *   <li>Retrieve members in reverse lexicographical order</li>
 *   <li>Get results from bottom to top of the sorted set</li>
 *   <li>Implement leaderboards with highest scores first</li>
 *   <li>Display most recent items (when scores are timestamps)</li>
 * </ul>
 * 
 * <p>This annotation can be combined with other ZSet operations like {@link ScoreRange},
 * {@link WithScores}, {@link RankRange}, and {@link Pager} to modify the order of the returned results.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see WithScores
 * @see ScoreRange
 * @see RankRange
 * @see Pager
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Reverse {

}