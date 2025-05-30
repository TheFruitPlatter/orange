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
package com.langwuyue.orange.redis.client;

import java.lang.annotation.Annotation;

/**
 * Provider interface for obtaining Redis client factory classes based on annotations.
 * 
 * <p>This interface serves as a factory provider that maps annotation types to their
 * corresponding factory classes. It is used in the Orange Redis framework to dynamically
 * create appropriate Redis client factories based on the annotations used in the code.
 * 
 * <p>Implementations of this interface should maintain a mapping between annotation
 * classes and their corresponding factory classes. This allows for flexible and
 * extensible client factory creation based on different annotation configurations.
 * 
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeClientFactoryProvider {

    /**
     * Returns the factory class associated with the given annotation class.
     * 
     * <p>This method is responsible for determining which factory class should be used
     * to create Redis clients based on the annotation present on the client class.
     * Implementations should maintain a mapping of annotation types to their corresponding
     * factory classes.
     * 
     *
     * @param annotationClass the annotation class to look up the factory for
     * @return the factory class associated with the annotation, or null if no factory is found
     */
    Class getFactoryClass(Class<? extends Annotation> annotationClass);

}