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
 * Indicates that the method should return both members and their scores from a Redis sorted set (ZSet).
 * When this annotation is present, the method will return a collection of member-score pairs instead of
 * just the members.
 * 
 * <p>This annotation can be used in combination with other ZSet operations like range queries or
 * member retrieval methods. The return type of the annotated method should be capable of holding
 * both member and score information.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see ScoreRange
 * @see RankRange
 * @see Reverse
 * @see Pager
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WithScores {

}