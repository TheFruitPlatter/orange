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
package com.langwuyue.orange.redis.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionTimeoutListener;

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
	
	public static Map newMap(Class returnClass) {
		if(returnClass.isInterface()) {
			if(returnClass == Map.class) {
				return new LinkedHashMap<>();
			}
			throw new OrangeRedisException("The return value must be a concrete implementation of Map (e.g., HashMap)");
		}
		int modifiers = returnClass.getModifiers();
		if(Modifier.isAbstract(modifiers)) {
			if(returnClass.isAssignableFrom(LinkedHashMap.class)) {
				return new LinkedHashMap<>();
			}
			throw new OrangeRedisException("The return value must be a concrete implementation of Map (e.g., LinkedHashMap)");
		}
		try {
			return (Map) returnClass.getConstructor().newInstance();
		}catch (Exception e) {
			throw new OrangeRedisException("The return value must be a concrete implementation of Map (e.g., LinkedHashMap). And must have a constructor withou any argument");
		}
	}
	
	public static void setFieldValue(Field field, Object obj, Object value) {
		try {
			field.setAccessible(true);
			field.set(obj,value);
		} catch (Exception e) {
			
			throw new OrangeRedisException(String.format("The field %s of %s is not accessible.", obj.getClass(),field), e);
		}finally {
			field.setAccessible(false);
		}
	}
	
	public static Object getFieldValue(Field field, Object obj) {
		try {
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			throw new OrangeRedisException(String.format("The field %s of %s is not accessible.", obj.getClass(),field), e);
		}finally {
			field.setAccessible(false);
		}
	}
	
	public static Field getFieldMarkedWithAnnotation(Class<?> memberClass, Class<? extends Annotation> annotationClass) {
		Field field = getFieldMarkedWithAnnotationNullable(memberClass,annotationClass);
		if(field == null) {
			throw new OrangeRedisException(String.format("The field of %s must be annotated with @%s.", memberClass,annotationClass));	
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
	
	public static Type getCollectionOrArrayArgumentType(Type genericType){
		if(!(genericType instanceof ParameterizedType)) {
			if(genericType instanceof GenericArrayType) {
				GenericArrayType genericArrayType = (GenericArrayType) genericType;
				return genericArrayType.getGenericComponentType();
			}
			Class<?> returnType = (Class<?>) genericType;
			if(returnType.isArray()) {
				return returnType.getComponentType();
			}
			throw new OrangeRedisException("Expected a Collection or an array.");
		}
		ParameterizedType parameterizedType = (ParameterizedType) genericType;
		Class<?> rawType = (Class<?>)parameterizedType.getRawType();
		if(!Collection.class.isAssignableFrom(rawType)) {
			throw new OrangeRedisException("Expected a Collection or an array.");
		}
		Type[] types = parameterizedType.getActualTypeArguments();
		if(types == null || types.length == 0) {
			throw new OrangeRedisException("Actual type arguments cannot be determined.");
		}
		return types[0];
	}
	
	public static Type[] getSuperIntrefaceArgumentTypes(Object obj,Class<?> targetInterfaces) {
		Type[] types = obj.getClass().getGenericInterfaces();
		for(Type type : types) {
			if(!(type instanceof ParameterizedType)) {
				continue;
			}
			ParameterizedType parameterizedType = (ParameterizedType) type;
			if(parameterizedType.getRawType() == targetInterfaces) {
				return parameterizedType.getActualTypeArguments();
			}
		}
		return null;
	}
	
	public static Class<?> getRawType(Type type){
		if(!(type instanceof ParameterizedType)) {
			return (Class<?>)type;
		}else{
			return (Class<?>) ((ParameterizedType)type).getRawType();
		}
	}
	
	public static Type[] getMapActaulTypeArguments(Type genericType) {
		if(!(genericType instanceof ParameterizedType)) {
			throw new OrangeRedisException("Expected a ParameterizedType, found raw type.");
		}
		ParameterizedType parameterizedType = (ParameterizedType) genericType;
		Class<?> rawType = (Class<?>)parameterizedType.getRawType();
		if(!Map.class.isAssignableFrom(rawType)) {
			throw new OrangeRedisException("Expected a map.");
		}
		Type[] types = parameterizedType.getActualTypeArguments();
		if(types == null || types.length != 2) {
			throw new OrangeRedisException("Actual type arguments cannot be determined.");
		}
		return types;
	}

	public static Type getMapValueType(Type genericType) {
		return getMapActaulTypeArguments(genericType)[1];
	}
	
	public static Type getMapKeyType(Type genericType) {
		return getMapActaulTypeArguments(genericType)[0];
	}
	
	public static Object getArrayOrCollectionInstance(Class<?> returnType,int size) {
		if(!Collection.class.isAssignableFrom(returnType) 
				&& !returnType.isArray()) {
			throw new OrangeRedisException("The return value must be a Collection or an Array.");
		}
		if(ArrayList.class.isAssignableFrom(returnType) || returnType.isAssignableFrom(ArrayList.class)) {
			return new ArrayList<>(size);
		}
		if(returnType.isAssignableFrom(LinkedHashSet.class) || LinkedHashSet.class.isAssignableFrom(returnType)) {
			return new LinkedHashSet<>();
		}
		if(returnType.isArray()) {
			return Array.newInstance(returnType.getComponentType(), size);
		}
		if(returnType.isInterface()) {
			throw new OrangeRedisException("The return value must be either a concrete implementation of Collection (e.g., ArrayList), an Array instance, or a subclass of these types.");
		}
		int modifiers = returnType.getModifiers();
		if(Modifier.isAbstract(modifiers)) {
			throw new OrangeRedisException("The return value must be either a concrete implementation of Collection (e.g., ArrayList), an Array instance, or a subclass of these types.");
		}
		try {
			return returnType.getConstructor(int.class).newInstance(size);
		} catch (Exception e) {
			try {
				return returnType.getConstructor().newInstance();
			} catch (Exception e1) {
				throw new OrangeRedisException(
					"The return value must have a public constructor without any arguments.", 
					e
				);
			}
		}
	}
	
	/*
	public static void classVarTypeMapping(Class parentClass, Map<Class,Map<Type,Type>> mapping){
		Map<Type,Type> subVarTypeMapping = mapping.get(parentClass);
		Map<Type,Type> varTypeMapping = new LinkedHashMap<>();
		Type[] interfaces = parentClass.getGenericInterfaces();
		for(Type interfaceType : interfaces) {
			if(!(interfaceType instanceof ParameterizedType)) {
				mapping.put((Class)interfaceType, varTypeMapping);	
				classVarTypeMapping((Class)interfaceType, mapping);	
				continue;
			}
			ParameterizedType parameterizedType = (ParameterizedType)interfaceType;
			Type[] actualTypes = parameterizedType.getActualTypeArguments();
			Type[] parameterizedTypesVariables = ((Class)parameterizedType.getRawType()).getTypeParameters();
			for (int i = 0; i < actualTypes.length; i++) {
				Type actaulType = actualTypes[i];
				if(!(actaulType instanceof TypeVariable)) {
					varTypeMapping.put(parameterizedTypesVariables[i], actaulType);
					continue;
				}
				if(subVarTypeMapping == null) {
					
					varTypeMapping.put(parameterizedTypesVariables[i], actaulType);
					continue;
				}
				
				Type subClassActualType = subVarTypeMapping.get(actaulType);
				if(subClassActualType == null) {
					continue;
				}
				varTypeMapping.put(parameterizedTypesVariables[i], subClassActualType);
			}
			mapping.put((Class)parameterizedType.getRawType(), varTypeMapping);
			classVarTypeMapping((Class)parameterizedType.getRawType(), mapping);
		}
		
	}*/

}
