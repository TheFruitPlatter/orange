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
package com.langwuyue.orange.redis.context.builder;

import java.util.Arrays;

/**
 * A specialized implementation of {@link OrangeOperationArgHandler} that handles multiple arguments
 * and varargs parameters in Redis operations.
 * 
 * <p>This handler is designed to handle two specific scenarios:
 * <ul>
 *   <li>Regular parameters: Handled similarly to {@link OrangeOperationArgSimpleHandler}</li>
 *   <li>Varargs parameters: When the last parameter is a varargs, all remaining arguments
 *       are collected into an array</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeOperationArgHandler
 * @see OrangeOperationArgSimpleHandler
 */
public class OrangeOperationArgMultipleHandler implements OrangeOperationArgHandler {

	/**
	 * Retrieves argument value(s) from the method parameters, with special handling for varargs.
	 * 
	 * <p>This method implements two different behaviors based on the parameter position:
	 * <ul>
	 *   <li>For regular parameters: Returns the single argument at the specified index</li>
	 *   <li>For the last parameter when it's varargs: Returns an array containing all remaining arguments</li>
	 * </ul>
	 * 
	 * <p>The varargs detection is based on comparing the actual number of arguments ({@code args.length})
	 * with the declared parameter count ({@code parameterCount}). If there are more actual arguments
	 * than declared parameters, and we're processing the last parameter, all remaining arguments are
	 * collected into an array.
	 *
	 * @param parameterCount the number of declared parameters in the method
	 * @param parameterIndex the current parameter index being processed (0-based)
	 * @param args the actual arguments passed to the method
	 * @return either a single argument value, or an array of values for varargs
	 * 
	 * @see Arrays#copyOfRange(Object[], int, int)
	 */
	@Override
	public Object getArgValue(int parameterCount, int parameterIndex, Object[] args) {
		int len = args.length;
		if(len > parameterCount && parameterIndex == parameterCount - 1) {
			return Arrays.copyOfRange(args, parameterIndex, len);
		}
		return args[parameterIndex];
	}
}