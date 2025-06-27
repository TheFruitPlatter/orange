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
package com.langwuyue.orange.redis.util;

import java.util.regex.Pattern;

import org.springframework.expression.ParserContext;

/**
 * Implementation of {@link ParserContext} for parsing template expressions with custom delimiters.
 * 
 * <p>This class defines the context for parsing template expressions, including the prefix and suffix
 * that delimit expressions in template strings. It also provides pattern matching capabilities for
 * identifying expressions within templates.
 * 
 * <p>By default, it uses "#{" as the prefix and "}" as the suffix, following the convention of
 * Spring Expression Language (SpEL). These delimiters can be customized through the constructor.
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see ParserContext
 * @see org.springframework.expression.ExpressionParser
 */
public class OrangeTemplateParserContext implements ParserContext{

	/**
	 * The prefix that marks the start of an expression in a template.
	 * Typically includes special characters to distinguish expressions from literal text.
	 */
	private final String expressionPrefix;

	/**
	 * The suffix that marks the end of an expression in a template.
	 * Used in conjunction with {@code expressionPrefix} to delimit expressions.
	 */
	private final String expressionSuffix;
	
	/**
	 * Compiled regular expression pattern used to identify expressions in templates.
	 * The pattern is built from the prefix and suffix during construction.
	 */
	private final Pattern pattern;


	/**
	 * Constructs a new parser context with default expression delimiters.
	 * 
	 * <p>The default configuration uses "$" as the beginning character, "{" as the prefix,
	 * and "}" as the suffix. This results in expressions of the form "${expression}".
	 * 
	 * <p>This format is compatible with many template systems and expression languages.
	 */
	public OrangeTemplateParserContext() {
		this("$","{", "}");
	}

	/**
	 * Constructs a new parser context with custom expression delimiters.
	 * 
	 * <p>This constructor allows full customization of the expression format:
	 * <ul>
	 *   <li>{@code expressionBeginChar} - The character that starts an expression (e.g., "$")</li>
	 *   <li>{@code expressionPrefix} - The prefix that follows the begin character (e.g., "{")</li>
	 *   <li>{@code expressionSuffix} - The suffix that marks the end of an expression (e.g., "}")</li>
	 * </ul>
	 * 
	 * <p>The complete expression format will be: beginChar + prefix + expression + suffix
	 * 
	 * @param expressionBeginChar the character that starts an expression (must not be null)
	 * @param expressionPrefix the prefix that follows the begin character (must not be null)
	 * @param expressionSuffix the suffix that marks the end of an expression (must not be null)
	 * @throws IllegalArgumentException if any parameter is null or empty
	 */
	public OrangeTemplateParserContext(String expressionBeginChar, String expressionPrefix, String expressionSuffix) {
		this.expressionPrefix = expressionBeginChar + expressionPrefix;
		this.expressionSuffix = expressionSuffix;
		if("$".equals(expressionBeginChar)) {
			expressionBeginChar = expressionBeginChar.replace("$", "\\$");
		}
		this.pattern = Pattern.compile(
				expressionBeginChar + 
				expressionPrefix.replace("{", "\\{")+
				"[^\\$\\{\\}]+"+
				expressionSuffix.replace("}", "\\}")
		);
	}
	
	/**
	 * Gets the compiled regular expression pattern used to identify expressions in templates.
	 * 
	 * <p>The pattern is built from the expression delimiters (begin character, prefix, and suffix)
	 * during construction. It matches the complete expression format including all delimiters.
	 * 
	 * @return the compiled Pattern object for matching expressions
	 */
	public Pattern getPattern() {
		return pattern;
	}

	/**
	 * Indicates whether the text should be treated as a template containing expressions.
	 * 
	 * <p>This implementation always returns {@code true}, indicating that all text processed
	 * by this parser context should be treated as templates that may contain expressions.
	 * 
	 * @return always {@code true} in this implementation
	 */
	@Override
	public final boolean isTemplate() {
		return true;
	}

	/**
	 * Gets the prefix used to identify the start of expressions in templates.
	 * 
	 * <p>The prefix consists of the begin character followed by the expression prefix.
	 * For example, with default settings this would return "${".
	 * 
	 * @return the expression prefix string
	 */
	@Override
	public final String getExpressionPrefix() {
		return this.expressionPrefix;
	}

	/**
	 * Gets the suffix used to identify the end of expressions in templates.
	 * 
	 * <p>The suffix marks where the expression ends and literal text resumes.
	 * For example, with default settings this would return "}".
	 * 
	 * @return the expression suffix string
	 */
	@Override
	public final String getExpressionSuffix() {
		return this.expressionSuffix;
	}

}