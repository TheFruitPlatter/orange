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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeStringTemlateUtils {
	
	private static final OrangeTemplateParserContext TEMPLATE_PARSER_CONTEXT = new OrangeTemplateParserContext();
	
	private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
	
	private static final OrangeAnnoationBaseParameterNameDiscoverer ANNOATION_BASE_PARAMETER_NAME_DISCOVERER = new OrangeAnnoationBaseParameterNameDiscoverer();
	
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
	
	public static String replaceVariable(String template,String variableMark) {
		if(template.indexOf(TEMPLATE_PARSER_CONTEXT.getExpressionPrefix()) != -1) {
			return TEMPLATE_PARSER_CONTEXT.getPattern().matcher(template).replaceAll(variableMark);
		}
		return null;
	}
}
