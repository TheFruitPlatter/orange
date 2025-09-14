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
package com.langwuyue.orange.zookeeper.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

import com.langwuyue.orange.zookeeper.OrangeZookeeperException;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeReflectionUtils {
	
	private static final Set<Type> FLOAT_CLASSES = new LinkedHashSet<>(4);
	
	private static final Set<Type> INTEGER_CLASSES = new LinkedHashSet<>(8);
	
	static {
		FLOAT_CLASSES.add(Float.class);
		FLOAT_CLASSES.add(float.class);
		FLOAT_CLASSES.add(Double.class);
		FLOAT_CLASSES.add(double.class);
		
		INTEGER_CLASSES.add(Long.class);
		INTEGER_CLASSES.add(long.class);
		INTEGER_CLASSES.add(Integer.class);
		INTEGER_CLASSES.add(int.class);
		INTEGER_CLASSES.add(Short.class);
		INTEGER_CLASSES.add(short.class);
		INTEGER_CLASSES.add(Byte.class);
		INTEGER_CLASSES.add(byte.class);
	}
	
	public static boolean isInteger(Type integerClass) {
		return INTEGER_CLASSES.contains(integerClass);
	}
	
	public static boolean isFloat(Type floatClass) {
		return FLOAT_CLASSES.contains(floatClass);
	}
	
	
	public static void setFieldValue(Field field, Object obj, Object value) {
		try {
			field.setAccessible(true);
			field.set(obj,value);
		} catch (Exception e) {
			
			throw new OrangeZookeeperException(String.format("The field %s of %s is not accessible.", obj.getClass(),field), e);
		}
	}
	
	public static Object getFieldValue(Field field, Object obj) {
		try {
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			throw new OrangeZookeeperException(String.format("The field %s of %s is not accessible.", obj.getClass(),field), e);
		}
	}
	
	public static Field getFieldMarkedWithAnnotation(Class<?> memberClass, Class<? extends Annotation> annotationClass) {
		Field field = getFieldMarkedWithAnnotationNullable(memberClass,annotationClass);
		if(field == null) {
			throw new OrangeZookeeperException(String.format("The field of %s must be annotated with @%s.", memberClass,annotationClass));	
		}
		return field;
	}
	
	public static Field getFieldMarkedWithAnnotationNullable(Class<?> memberClass, Class<? extends Annotation> annotationClass) {
		Field[] fields = memberClass.getDeclaredFields();
		for(Field field : fields) {
			if(!field.isAnnotationPresent(annotationClass)) {
				continue;
				
			}
			return field;
		}
		return null;
	}
}
