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
 * Custom runtime exception for handling errors in the Orange Redis module.
 * 
 * As a subclass of RuntimeException, OrangeRedisException is an unchecked exception,
 * which means callers can choose to catch and handle it or let it propagate upward.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an OrangeRedisException with no detail message.
	 * 
	 * This constructor creates an exception instance without specifying an error message or cause.
	 * It is typically used when no specific error information is needed.
	 */
	public OrangeRedisException() {
		super();
		
	}

	/**
	 * Constructs an OrangeRedisException with detail message, cause, suppression enabled
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
	public OrangeRedisException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		
	}

	/**
	 * Constructs an OrangeRedisException with a detail message and cause.
	 * 
	 * This constructor creates an exception instance containing both an error message
	 * and the original exception that caused this exception. This is the most complete
	 * constructor, providing the most detailed information about the exception.
	 * 
	 * @param message the detail message describing the exception
	 * @param cause the cause of this exception (saved for later retrieval by the Throwable.getCause() method)
	 */
	public OrangeRedisException(String message, Throwable cause) {
		super(message, cause);
		
	}

	/**
	 * Constructs an OrangeRedisException with a detail message.
	 * 
	 * This constructor creates an exception instance with a specified error message,
	 * providing more information about the cause of the exception.
	 * 
	 * @param message the detail message describing the exception
	 */
	public OrangeRedisException(String message) {
		super(message);
		
	}

	/**
	 * Constructs an OrangeRedisException with a cause.
	 * 
	 * This constructor creates an exception instance containing the original exception
	 * that caused this exception, used for exception chaining and preserving the complete
	 * exception stack trace.
	 * 
	 * @param cause the cause of this exception (saved for later retrieval by the Throwable.getCause() method)
	 */
	public OrangeRedisException(Throwable cause) {
		super(cause);
		
	}
}