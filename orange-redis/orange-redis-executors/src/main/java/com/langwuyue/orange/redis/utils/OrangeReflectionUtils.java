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

/**
 * Utility class providing reflection-related helper methods for Orange Redis operations.
 * 
 * This abstract class contains static utility methods for working with Java reflection
 * in the context of Redis operations. It provides functionality for type checking,
 * field access, collection and map handling, and other reflection-based operations
 * commonly needed in the Orange Redis framework.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public abstract class OrangeReflectionUtils {
	
	/**
	 * Set of floating-point number types supported by the framework.
	 * Includes both primitive types and their wrapper classes (float, Float, double, Double).
	 */
	private static final Set<Type> FLOAT_CLASSES = new LinkedHashSet<>(4);
	
	/**
	 * Set of integer number types supported by the framework.
	 * Includes both primitive types and their wrapper classes (byte, Byte, short, Short, 
	 * int, Integer, long, Long).
	 */
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
	
	/**
	 * Checks if the given type is an integer type.
	 * 
	 * This method determines whether the provided type is one of the supported integer types,
	 * including both primitive types (byte, short, int, long) and their wrapper classes
	 * (Byte, Short, Integer, Long).
	 *
	 * @param integerClass the type to check
	 * @return true if the type is an integer type, false otherwise
	 */
	public static boolean isInteger(Type integerClass) {
		return INTEGER_CLASSES.contains(integerClass);
	}
	
	/**
	 * Checks if the given type is a floating-point type.
	 * 
	 * This method determines whether the provided type is one of the supported floating-point types,
	 * including both primitive types (float, double) and their wrapper classes
	 * (Float, Double).
	 *
	 * @param floatClass the type to check
	 * @return true if the type is a floating-point type, false otherwise
	 */
	public static boolean isFloat(Type floatClass) {
		return FLOAT_CLASSES.contains(floatClass);
	}
	
	/**
	 * Creates a new Map instance based on the specified class.
	 * 
	 * This method attempts to create a new instance of the specified Map class.
	 * If the class is an interface or abstract, it will try to create an appropriate
	 * concrete implementation (LinkedHashMap).
	 *
	 * @param returnClass the class of Map to create
	 * @return a new instance of the specified Map class
	 * @throws OrangeRedisException if the class is not a concrete Map implementation
	 *         or does not have a no-arg constructor
	 */
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
	
	/**
	 * Sets the value of a field on the specified object.
	 * 
	 * This method uses reflection to set the value of a field, temporarily making
	 * the field accessible if necessary. The field's accessibility is restored
	 * to its original state after the operation.
	 *
	 * @param field the field to set
	 * @param obj the object whose field should be modified
	 * @param value the new value for the field
	 * @throws OrangeRedisException if the field cannot be accessed or set
	 */
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
	
	/**
	 * Gets the value of a field from the specified object.
	 * 
	 * This method uses reflection to get the value of a field, temporarily making
	 * the field accessible if necessary. The field's accessibility is restored
	 * to its original state after the operation.
	 *
	 * @param field the field to get
	 * @param obj the object whose field should be read
	 * @return the value of the field
	 * @throws OrangeRedisException if the field cannot be accessed or read
	 */
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
	
	/**
	 * Gets a field marked with the specified annotation from the given class.
	 * 
	 * This method searches for a field in the specified class that is annotated
	 * with the given annotation type. If no such field is found, an exception is thrown.
	 *
	 * @param memberClass the class to search for the annotated field
	 * @param annotationClass the annotation class to look for
	 * @return the Field object representing the annotated field
	 * @throws OrangeRedisException if no field with the specified annotation is found
	 */
	public static Field getFieldMarkedWithAnnotation(Class<?> memberClass, Class<? extends Annotation> annotationClass) {
		Field field = getFieldMarkedWithAnnotationNullable(memberClass,annotationClass);
		if(field == null) {
			throw new OrangeRedisException(String.format("The field of %s must be annotated with @%s.", memberClass,annotationClass));	
		}
		return field;
	}
	
	/**
	 * Gets a field marked with the specified annotation from the given class, or null if not found.
	 * 
	 * This method searches for a field in the specified class that is annotated
	 * with the given annotation type. Unlike getFieldMarkedWithAnnotation, this method
	 * returns null instead of throwing an exception if no matching field is found.
	 *
	 * @param memberClass the class to search for the annotated field
	 * @param annotationClass the annotation class to look for
	 * @return the Field object representing the annotated field, or null if not found
	 */
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
	
	/**
	 * Gets the component type of a collection or array type.
	 * 
	 * This method extracts the component type (element type) from either a Collection
	 * type (with generic parameters) or an array type (either generic or concrete).
	 *
	 * @param genericType the type to analyze (Collection or array type)
	 * @return the component type of the collection or array
	 * @throws OrangeRedisException if the input is neither a Collection nor an array type
	 */
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
	
	/**
	 * Gets the actual type arguments of a super interface implemented by the object's class.
	 * 
	 * This method searches for a specific interface in the object's class hierarchy
	 * and returns its actual type arguments if found. Returns null if the interface
	 * is not found or is not parameterized.
	 *
	 * @param obj the object whose class hierarchy to search
	 * @param targetInterfaces the interface to look for
	 * @return array of actual type arguments, or null if not found
	 */
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
	
	/**
	 * Gets the raw class type from a Type object.
	 * 
	 * This method extracts the raw class from a Type object. If the type is a
	 * ParameterizedType (like List&lt;String&gt;), it returns the raw type (List).
	 * Otherwise, it returns the type cast as a Class.
	 *
	 * @param type the type to get the raw class from
	 * @return the raw class of the given type
	 */
	public static Class<?> getRawType(Type type){
		if(!(type instanceof ParameterizedType)) {
			return (Class<?>)type;
		}else{
			return (Class<?>) ((ParameterizedType)type).getRawType();
		}
	}
	
	/**
	 * Gets the actual type arguments of a Map type.
	 * 
	 * This method extracts the key and value type parameters from a parameterized Map type.
	 * It verifies that the type is a ParameterizedType, that its raw type is assignable to Map,
	 * and that it has exactly two type arguments.
	 *
	 * @param genericType the parameterized Map type to analyze
	 * @return an array containing the key type at index 0 and the value type at index 1
	 * @throws OrangeRedisException if the type is not a parameterized Map type or doesn't have exactly two type arguments
	 */
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

	/**
	 * Gets the value type parameter of a Map type.
	 * 
	 * This method extracts the value type parameter (second type argument) from a 
	 * parameterized Map type. It uses getMapActaulTypeArguments internally and 
	 * returns the second type argument.
	 *
	 * @param genericType the parameterized Map type to analyze
	 * @return the value type parameter of the Map
	 * @throws OrangeRedisException if the type is not a valid parameterized Map type
	 */
	public static Type getMapValueType(Type genericType) {
		return getMapActaulTypeArguments(genericType)[1];
	}
	
	/**
	 * Gets the key type parameter of a Map type.
	 * 
	 * This method extracts the key type parameter (first type argument) from a 
	 * parameterized Map type. It uses getMapActaulTypeArguments internally and 
	 * returns the first type argument.
	 *
	 * @param genericType the parameterized Map type to analyze
	 * @return the key type parameter of the Map
	 * @throws OrangeRedisException if the type is not a valid parameterized Map type
	 */
	public static Type getMapKeyType(Type genericType) {
		return getMapActaulTypeArguments(genericType)[0];
	}
	
	/**
	 * Creates a new instance of an array or collection with the specified size.
	 * 
	 * This method creates instances of arrays or collections based on the provided type:
	 * - For ArrayList or types assignable to ArrayList: creates an ArrayList with the specified initial capacity
	 * - For LinkedHashSet or types assignable to LinkedHashSet: creates a LinkedHashSet
	 * - For arrays: creates an array of the specified size
	 * - For other concrete collection types: attempts to create using constructor with size parameter or no-arg constructor
	 *
	 * @param returnType the class of the array or collection to create
	 * @param size the size/capacity of the array or collection
	 * @return a new instance of the specified type
	 * @throws OrangeRedisException if the type is not a collection or array type,
	 *         or if a concrete instance cannot be created
	 */
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