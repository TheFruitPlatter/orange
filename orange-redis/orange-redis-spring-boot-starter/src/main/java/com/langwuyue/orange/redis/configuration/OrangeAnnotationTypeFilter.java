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
package com.langwuyue.orange.redis.configuration;

import java.lang.annotation.Annotation;

import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * A specialized annotation type filter for Orange Redis component scanning.
 * 
 * This class extends Spring's {@link AnnotationTypeFilter} to provide custom annotation-based
 * filtering capabilities for Orange Redis components. It allows the Spring container to identify
 * and register beans that are annotated with specific Orange Redis annotations.
 * 
 * The filter can be configured to consider meta-annotations and interfaces when determining
 * if a class matches the specified annotation type.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeAnnotationTypeFilter extends AnnotationTypeFilter {

	/**
	 * Constructs an OrangeAnnotationTypeFilter with full configuration options.
	 * 
	 * @param annotationType the annotation type to match against
	 * @param considerMetaAnnotations whether to consider meta-annotations (annotations on annotations)
	 *                               when determining if a class matches the filter
	 * @param considerInterfaces whether to consider interfaces when determining if a class matches the filter
	 */
	public OrangeAnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations,
			boolean considerInterfaces) {
		super(annotationType, considerMetaAnnotations, considerInterfaces);
	}

	/**
	 * Constructs an OrangeAnnotationTypeFilter with configuration for meta-annotations.
	 * 
	 * This constructor creates a filter that does not consider interfaces when determining
	 * if a class matches the specified annotation type.
	 * 
	 * @param annotationType the annotation type to match against
	 * @param considerMetaAnnotations whether to consider meta-annotations (annotations on annotations)
	 *                               when determining if a class matches the filter
	 */
	public OrangeAnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations) {
		super(annotationType, considerMetaAnnotations);
	}

	/**
	 * Constructs an OrangeAnnotationTypeFilter with default configuration.
	 * 
	 * This constructor creates a filter that considers meta-annotations but not interfaces
	 * when determining if a class matches the specified annotation type.
	 * 
	 * @param annotationType the annotation type to match against
	 */
	public OrangeAnnotationTypeFilter(Class<? extends Annotation> annotationType) {
		super(annotationType);
	}
}