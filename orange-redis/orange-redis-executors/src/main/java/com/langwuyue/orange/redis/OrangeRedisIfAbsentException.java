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
 * Custom runtime exception for handling "if absent" scenarios in Orange Redis operations.
 * 
 * As a subclass of RuntimeException, OrangeRedisIfAbsentException is an unchecked exception,
 * allowing developers to handle or propagate it based on their application's needs.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisIfAbsentException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an OrangeRedisIfAbsentException with no detail message.
	 * 
	 * This constructor creates an exception instance without specifying an error message or cause.
	 * It is typically used when the context of the exception is clear from where it's thrown.
	 */
	public OrangeRedisIfAbsentException() {
		super();
		
	}

	/**
	 * Constructs an OrangeRedisIfAbsentException with detail message, cause, suppression enabled
	 * or disabled, and writable stack trace enabled or disabled.
	 * 
	 * This constructor provides complete control over the exception's behavior, allowing
	 * customization of suppression and stack trace writability.
	 * 
	 * @param message the detail message describing the exception
	 * @param cause the cause of this exception (saved for later retrieval by the Throwable.getCause() method)
	 * @param enableSuppression whether suppression is enabled or disabled
	 * @param writableStackTrace whether the stack trace should be writable
	 */
	public OrangeRedisIfAbsentException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	/**
	 * Constructs an OrangeRedisIfAbsentException with a detail message and cause.
	 * 
	 * This constructor creates an exception instance containing both an error message
	 * and the original exception that caused this exception. This is the most complete
	 * constructor, providing the most detailed information about the exception.
	 * 
	 * @param message the detail message describing the exception
	 * @param cause the cause of this exception (saved for later retrieval by the Throwable.getCause() method)
	 */
	public OrangeRedisIfAbsentException(String message, Throwable cause) {
		super(message, cause);
		
	}

	/**
	 * Constructs an OrangeRedisIfAbsentException with a detail message.
	 * 
	 * This constructor creates an exception instance with a specified error message,
	 * typically describing which key or value was expected to be absent but was found.
	 * 
	 * @param message the detail message describing the exception
	 */
	public OrangeRedisIfAbsentException(String message) {
		super(message);
		
	}

	/**
	 * Constructs an OrangeRedisIfAbsentException with a cause.
	 * 
	 * This constructor creates an exception instance containing the original exception
	 * that caused this exception, used for exception chaining and preserving the complete
	 * exception stack trace.
	 * 
	 * @param cause the cause of this exception (saved for later retrieval by the Throwable.getCause() method)
	 */
	public OrangeRedisIfAbsentException(Throwable cause) {
		super(cause);
		
	}
}