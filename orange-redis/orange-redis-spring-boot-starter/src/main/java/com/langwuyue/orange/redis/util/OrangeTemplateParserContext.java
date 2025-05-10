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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeTemplateParserContext implements ParserContext{

	private final String expressionPrefix;

	private final String expressionSuffix;
	
	private final Pattern pattern;


	/**
	 * Create a new TemplateParserContext with the default "#{" prefix and "}" suffix.
	 */
	public OrangeTemplateParserContext() {
		this("$","{", "}");
	}

	/**
	 * Create a new TemplateParserContext for the given prefix and suffix.
	 * @param expressionPrefix the expression prefix to use
	 * @param expressionSuffix the expression suffix to use
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
	
	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public final boolean isTemplate() {
		return true;
	}

	@Override
	public final String getExpressionPrefix() {
		return this.expressionPrefix;
	}

	@Override
	public final String getExpressionSuffix() {
		return this.expressionSuffix;
	}

}
