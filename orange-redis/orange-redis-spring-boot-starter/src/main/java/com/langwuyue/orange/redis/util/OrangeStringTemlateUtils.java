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

import java.lang.reflect.Method;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Utility class for processing string templates with embedded expressions.
 * 
 * <p>This class provides methods to evaluate and process string templates that contain
 * expression language constructs. It uses Spring Expression Language (SpEL) to parse
 * and evaluate expressions within templates, making it useful for dynamic string generation
 * based on method parameters and other contextual information.
 * 
 * <p>The template expressions are delimited by the prefix and suffix defined in
 * {@link OrangeTemplateParserContext}. By default, these are typically "#{" and "}"
 * respectively.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeTemplateParserContext
 * @see OrangeAnnotationBaseEvaluationContext
 */
public abstract class OrangeStringTemlateUtils {
	
	/**
	 * The parser context used for template expressions.
	 * Defines the delimiters and behavior for parsing template expressions.
	 */
	private static final OrangeTemplateParserContext TEMPLATE_PARSER_CONTEXT = new OrangeTemplateParserContext();
	
	/**
	 * The expression parser used to parse SpEL expressions.
	 * Uses the standard SpEL parser implementation.
	 */
	private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
	
	/**
	 * The parameter name discoverer used to extract method parameter names.
	 * Used when evaluating expressions in the context of method invocations.
	 */
	private static final OrangeAnnotationBaseParameterNameDiscoverer ANNOATION_BASE_PARAMETER_NAME_DISCOVERER = new OrangeAnnotationBaseParameterNameDiscoverer();
	
	private OrangeStringTemlateUtils() {}
	
	/**
	 * Processes a string template by evaluating embedded expressions using method context.
	 * 
	 * <p>This method takes a template string containing SpEL expressions and evaluates them
	 * in the context of the provided method and its arguments. The expressions in the template
	 * can reference method parameters by name or index.
	 * 
	 * @param template the template string containing expressions to be evaluated
	 * @param method the method whose parameters will be available in the evaluation context
	 * @param args the argument values passed to the method
	 * @return the processed string with all expressions evaluated
	 */
	public static String getString(String template,Method method, Object[] args) {
		if(template.indexOf(TEMPLATE_PARSER_CONTEXT.getExpressionPrefix()) != -1) {
			Expression expr = EXPRESSION_PARSER.parseExpression(template, TEMPLATE_PARSER_CONTEXT);
	        OrangeAnnotationBaseEvaluationContext context = new OrangeAnnotationBaseEvaluationContext(
	        		method, 
	        		args,
	        		ANNOATION_BASE_PARAMETER_NAME_DISCOVERER
	        );
	        template = expr.getValue(context,String.class);
		}
		return template;
	}
	
	/**
	 * Replaces all expression variables in a template with a specified marker.
	 * 
	 * <p>This method identifies all expressions in the template (based on the expression prefix
	 * and suffix defined in {@link OrangeTemplateParserContext}) and replaces them with the
	 * provided variable marker. This is useful for preprocessing templates or creating
	 * placeholders for later substitution.
	 *
	 * @param template the template string containing expressions to be replaced
	 * @param variableMark the marker string to replace each expression with
	 * @return the template with all expressions replaced by the variable marker,
	 *         or {@code null} if the template doesn't contain any expressions
	 */
	public static String replaceVariable(String template,String variableMark) {
		if(template.indexOf(TEMPLATE_PARSER_CONTEXT.getExpressionPrefix()) != -1) {
			return TEMPLATE_PARSER_CONTEXT.getPattern().matcher(template).replaceAll(variableMark);
		}
		return null;
	}
}