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
package com.langwuyue.orange.redis.annotation.cross;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Aggregate {

	/**
	 * The weights of the keys.
	 * 
	 * <p>
	 * Weights define the multiplier applied to each input sorted set's scores during aggregation.
	 * 
	 * 
	 *  <p>
	 *  The weights and keys must have the same length.
	 *  
	 */
	double[] weights();
	
	/**
	 * Defines the aggregation operator to apply to scores during a sorted set operation.
	 * 
	 * @see Operator  Available aggregation operators (SUM, MIN, MAX).
	 */
	Operator operator(); 
	
	/**
	 * Supported aggregation operators for combining scores.
	 */
	enum Operator {
		/** Sum of all scores (default if not specified). */
	    SUM,
	    
	    /** Minimum score among all values. */
	    MIN,
	    
	    /** Maximum score among all values. */
	    MAX;
	}
}
