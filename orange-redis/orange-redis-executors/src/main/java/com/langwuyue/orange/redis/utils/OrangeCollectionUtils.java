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
package com.langwuyue.orange.redis.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class providing collection-related helper methods for Orange Redis operations.
 * 
 * This abstract class contains static utility methods for working with collections
 * in the context of Redis operations. It is designed to be used as a static utility
 * class and cannot be instantiated.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeCollectionUtils {
	
	/**
	 * Private constructor to prevent instantiation of this utility class.
	 * This class is designed to be used via its static methods only.
	 */
	private OrangeCollectionUtils() {}
	
	/**
	 * Creates a new ArrayList containing the specified elements.
	 * 
	 * This method provides a convenient way to create and initialize an ArrayList
	 * in a single operation. It is similar to Arrays.asList() but returns an ArrayList
	 * that can be modified, unlike the fixed-size list returned by Arrays.asList().
	 *
	 * @param values the elements to be placed into the list
	 * @return a new ArrayList containing the specified elements
	 */
	public static List asList(Object... values) {
		List list = new ArrayList<>(values.length);
		Collections.addAll(list, values);
		return list;
	}

}