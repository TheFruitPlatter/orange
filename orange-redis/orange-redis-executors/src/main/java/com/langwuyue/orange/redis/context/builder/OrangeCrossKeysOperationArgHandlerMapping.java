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
package com.langwuyue.orange.redis.context.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.cross.Aggregate;
import com.langwuyue.orange.redis.annotation.cross.CrossOperationKeys;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.client.OrangeClientFactoryProvider;
import com.langwuyue.orange.redis.client.OrangeRedisClientFactoryMetaData;
import com.langwuyue.orange.redis.client.OrangeRedisClientFactoryMetaDataBuilder;
import com.langwuyue.orange.redis.executor.OrangeRedisExecutor;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;

/**
 * A specialized handler mapping for Redis operations that work with multiple keys,
 * particularly for set operations like UNION, INTERSECTION, and DIFFERENCE.
 * 
 * <p>This class extends {@link OrangeOperationArgHandlerMapping} to provide support
 * for cross-key operations, where a single Redis command needs to operate on multiple
 * Redis keys. It handles:
 * <ul>
 *   <li>Validation of key classes and their annotations</li>
 *   <li>Management of value type consistency across keys</li>
 *   <li>Support for aggregation operations with weights</li>
 *   <li>Result storage configuration for operations</li>
 * </ul>
 * 
 * <p>Key features:
 * <ul>
 *   <li>Supports {@link CrossOperationKeys} annotation for specifying multiple key classes</li>
 *   <li>Handles {@link Aggregate} annotation for weighted set operations</li>
 *   <li>Manages {@link StoreTo} annotation for result storage configuration</li>
 *   <li>Ensures value type consistency across all involved keys</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see CrossOperationKeys
 * @see Aggregate
 * @see StoreTo
 * @see OrangeOperationArgHandlerMapping
 */
public class OrangeCrossKeysOperationArgHandlerMapping extends OrangeOperationArgHandlerMapping {
	
	private OrangeClientFactoryProvider provider;
	
	private Class<? extends Annotation> clientAnnotationClass;
	
	private static final Map<Method, RedisValueTypeEnum> OPEATION_VALUE_TYPE_MAPPING = new ConcurrentHashMap<>();
	
	private static final Map<Method, List<Class<?>>> OPEATION_KEYS_MAPPING = new ConcurrentHashMap<>();
	
	private static final Map<Method, Class<?>> OPEATION_STORE_TO_MAPPING = new ConcurrentHashMap<>();
	
	/**
	 * Constructs a new OrangeCrossKeysOperationArgHandlerMapping instance.
	 * 
	 * <p>This constructor initializes a handler mapping for Redis operations that work
	 * with multiple keys. It sets up the necessary components for managing cross-key
	 * operations, including:
	 * <ul>
	 *   <li>Executor mappings for Redis operations</li>
	 *   <li>Operation ownership and scope management</li>
	 *   <li>Value handler configurations for different argument types</li>
	 *   <li>Client factory provider for Redis connections</li>
	 *   <li>Client annotation type validation</li>
	 * </ul>
	 * 
	 * <p>The constructor performs the following initializations:
	 * <ol>
	 *   <li>Calls the parent class constructor with base configuration</li>
	 *   <li>Sets up the client factory provider for Redis connections</li>
	 *   <li>Configures the expected client annotation class for validation</li>
	 * </ol>
	 *
	 * @param executorsMapping the mapping of executors for Redis operations
	 * @param operationOwner the class that owns the Redis operations
	 * @param valueHandlerMap map of value handlers for different argument types
	 * @param provider provider for Redis client factories
	 * @param clientAnnotationClass the annotation class used to mark Redis clients
	 * @see OrangeOperationArgHandlerMapping
	 * @see OrangeClientFactoryProvider
	 */
	public OrangeCrossKeysOperationArgHandlerMapping(
		OrangeRedisExecutorsMapping executorsMapping, 
		Class<?> operationOwner,
		Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap,
		OrangeClientFactoryProvider provider,
		Class<? extends Annotation> clientAnnotationClass
	) {
		super(executorsMapping,operationOwner,valueHandlerMap);
		this.provider = provider;
		this.clientAnnotationClass = clientAnnotationClass;
	}
	
	/**
	 * Retrieves and configures the Redis executor for cross-key operations.
	 * 
	 * <p>This method extends the base executor retrieval process to handle cross-key
	 * specific validations and configurations. It performs the following steps:
	 * <ol>
	 *   <li>Gets the base executor from the parent class</li>
	 *   <li>Retrieves the actual method being executed</li>
	 *   <li>Validates cross-key operation annotations and configurations</li>
	 *   <li>Checks value type consistency across all keys</li>
	 *   <li>Configures storage destination if specified</li>
	 *   <li>Caches operation metadata for future use</li>
	 * </ol>
	 *
	 * <p>The method performs several important validations:
	 * <ul>
	 *   <li>Ensures all specified key classes are properly annotated</li>
	 *   <li>Verifies that there are no duplicate key classes</li>
	 *   <li>Validates that weights (if specified) match the number of keys</li>
	 *   <li>Confirms value type consistency across all involved keys</li>
	 * </ul>
	 *
	 * @param method the method to get the executor for
	 * @return configured {@link OrangeRedisExecutor} for the cross-key operation
	 * @throws OrangeRedisException if validation fails or configuration is invalid
	 * @see CrossOperationKeys
	 * @see Aggregate
	 * @see StoreTo
	 */
	@Override
	protected OrangeRedisExecutor getOrangeRedisExecutor(Method method) {
		OrangeRedisExecutor executor = super.getOrangeRedisExecutor(method);
		Method actualMethod = getExecutorsMapping().getActualMethod(method);
		Set<RedisValueTypeEnum> valueTypes = new LinkedHashSet<>();
		List<Class<?>> keyClasses = new ArrayList<>();
		int keyClassesLength = checkCrossOperationKeysAndGetKeysLength(actualMethod,valueTypes,keyClasses);
		checkWeightsLength(actualMethod,keyClassesLength);
		StoreTo storeTo = checkStoreToAndGet(actualMethod,valueTypes);
		if(storeTo != null) {
			OPEATION_STORE_TO_MAPPING.put(method, storeTo.value());
		}
		checkValueType(valueTypes);
		OPEATION_VALUE_TYPE_MAPPING.put(method, valueTypes.iterator().next());
		OPEATION_KEYS_MAPPING.put(method, keyClasses);
		return executor;
	}
	
	/**
	 * Validates that all keys involved in the cross-key operation have the same value type.
	 * 
	 * <p>This method ensures value type consistency across all Redis keys involved in
	 * the operation. It checks:
	 * <ul>
	 *   <li>That at least one value type is specified</li>
	 *   <li>That all keys have the same value type</li>
	 * </ul>
	 * 
	 * <p>Value type consistency is critical for cross-key operations to ensure that
	 * the operation produces meaningful results. For example, when performing a set
	 * union, all sets should contain the same type of values.
	 *
	 * @param valueTypes the set of value types collected from all involved keys
	 * @throws OrangeRedisException if no value types are found or if multiple different value types are detected
	 */
	protected void checkValueType(Set<RedisValueTypeEnum> valueTypes) {
		if(valueTypes.isEmpty()) {
			throw new OrangeRedisException("The value types of keys cannot be null");
		}
		if(valueTypes.size() != 1) {
			throw new OrangeRedisException("The value types of keys are not the same");
		}
	}
	
	/**
	 * Validates cross-operation keys configuration and collects value type information.
	 * 
	 * <p>This method performs comprehensive validation of the keys involved in a cross-key
	 * operation while collecting their value types. It:
	 * <ul>
	 *   <li>Validates the presence and content of {@link CrossOperationKeys} annotation</li>
	 *   <li>Checks for duplicate key classes in the operation</li>
	 *   <li>Validates each key class's client type annotation</li>
	 *   <li>Collects value types from all keys for consistency checking</li>
	 *   <li>Maintains the order of key classes for operation execution</li>
	 * </ul>
	 *
	 * @param actualMethod the method being validated
	 * @param valueTypes set to collect value types from all keys
	 * @param keyClassList list to collect validated key classes in order
	 * @return the number of key classes involved in the operation
	 * @throws OrangeRedisException if validation fails due to:
	 *         <ul>
	 *           <li>Empty or null key classes array</li>
	 *           <li>Duplicate key classes</li>
	 *           <li>Invalid client type annotations</li>
	 *         </ul>
	 * @see CrossOperationKeys
	 */
	protected int checkCrossOperationKeysAndGetKeysLength(Method actualMethod,Set<RedisValueTypeEnum> valueTypes,List<Class<?>> keyClassList) {
		CrossOperationKeys crossOperationKeys = actualMethod.getAnnotation(CrossOperationKeys.class);
		Class<?>[] keyClasses = crossOperationKeys.value();
		if(keyClasses == null || keyClasses.length == 0) {
			throw new OrangeRedisException("The value of @%s cannot be empty");
		}
		for(Class<?> keyClass : keyClasses) {
			if(keyClassList.contains(keyClass)) {
				throw new OrangeRedisException(String.format("Depucated key found, key class: %s", keyClass));
			}
			checkClientType(keyClass,valueTypes,this.clientAnnotationClass);
			keyClassList.add(keyClass);
		}
		return keyClasses.length;
	}
	
	/**
	 * Validates the {@link StoreTo} annotation and collects the destination key's value type.
	 * 
	 * <p>This method checks if a result storage destination is specified for the cross-key
	 * operation and validates its configuration. It:
	 * <ul>
	 *   <li>Checks for the presence of {@link StoreTo} annotation</li>
	 *   <li>Validates that the destination key class is properly specified</li>
	 *   <li>Ensures the destination key has the correct client type annotation</li>
	 *   <li>Adds the destination key's value type to the set of value types for consistency checking</li>
	 * </ul>
	 * 
	 * <p>The {@link StoreTo} annotation is used to specify where the result of operations
	 * like UNIONSTORE or INTERSTORE should be saved.
	 *
	 * @param actualMethod the method being validated
	 * @param valueTypes set to collect value types, including the destination key's type
	 * @return the StoreTo annotation if present, null otherwise
	 * @throws OrangeRedisException if the StoreTo annotation is present but invalid
	 * @see StoreTo
	 */
	protected StoreTo checkStoreToAndGet(Method actualMethod,Set<RedisValueTypeEnum> valueTypes) {
		StoreTo storeTo = actualMethod.getAnnotation(StoreTo.class);
		if(storeTo == null) {
			return null;
		}
		Class<?> keyClass = storeTo.value();
		if(keyClass == null) {
			throw new OrangeRedisException(String.format("The value of @%s cannot be null", StoreTo.class));
		}
		checkClientType(keyClass,valueTypes,this.clientAnnotationClass);
		return storeTo;
	}
	
	/**
	 * Validates the weights configuration for weighted set operations.
	 * 
	 * <p>This method ensures that when weights are specified for a cross-key operation
	 * (such as weighted UNION or INTERSECTION), the number of weights matches the
	 * number of input keys. It:
	 * <ul>
	 *   <li>Checks for the presence of {@link Aggregate} annotation with weights</li>
	 *   <li>Validates that the number of weights matches the number of input keys</li>
	 *   <li>Ensures weights are properly configured for weighted set operations</li>
	 * </ul>
	 * 
	 * @param actualMethod the method being validated
	 * @param keyClassesLen the number of key classes involved in the operation
	 * @throws OrangeRedisException if weights are specified but their count doesn't match the key count
	 * @see Aggregate
	 * @see CrossOperationKeys
	 */
	protected void checkWeightsLength(Method actualMethod,int keyClassesLen) {
		Aggregate aggregate = actualMethod.getAnnotation(Aggregate.class);
		if(aggregate == null) {
			return;
		}
		int len = aggregate.weights().length;
		if(len != keyClassesLen) {
			throw new OrangeRedisException(String.format("The length of @%s's weights must equal the length of @%s's value", Aggregate.class,CrossOperationKeys.class));
		}
	}
	
	/**
	 * Validates the Redis client type configuration and collects value type information.
	 * 
	 * <p>This method performs comprehensive validation of a key class's client configuration
	 * and collects its value type information. It:
	 * <ul>
	 *   <li>Builds client factory metadata for the key class</li>
	 *   <li>Validates the presence of required client type annotation</li>
	 *   <li>Extracts and collects value type information if available</li>
	 *   <li>Ensures proper client configuration for Redis operations</li>
	 * </ul>
	 * 
	 * <p>The method uses {@link OrangeRedisClientFactoryMetaDataBuilder} to construct
	 * and validate client configuration metadata, ensuring that each key class is
	 * properly configured for Redis operations.
	 *
	 * @param keyClass the key class to validate
	 * @param valueTypes set to collect value types from validated keys
	 * @param clientAnnotationClass the expected client annotation class
	 * @throws OrangeRedisException if the key class lacks proper client configuration
	 * @see OrangeRedisClientFactoryMetaData
	 * @see OrangeRedisClientFactoryMetaDataBuilder
	 */
	protected void checkClientType(Class<?> keyClass,Set<RedisValueTypeEnum> valueTypes,Class<? extends Annotation> clientAnnotationClass) {
		OrangeRedisClientFactoryMetaDataBuilder builder = new OrangeRedisClientFactoryMetaDataBuilder(keyClass,this.provider);
		OrangeRedisClientFactoryMetaData metaData = builder.build();
		Annotation annotation = metaData.getClientClass().getAnnotation(clientAnnotationClass);
		if(annotation == null) {
			throw new OrangeRedisException(String.format("Key not support, %s must be annotated with @%s", keyClass, clientAnnotationClass));
		}
		RedisValueTypeEnum valueType = getValueType(annotation);
		if(valueType != null) {
			valueTypes.add(valueType);
		}
	}
	
	/**
	 * Extracts the Redis value type from a client annotation.
	 * 
	 * <p>This protected method is designed to be overridden by subclasses to provide
	 * specific value type extraction logic based on the client annotation type. The
	 * default implementation returns null, and subclasses should:
	 * <ul>
	 *   <li>Implement specific logic to extract value type information</li>
	 *   <li>Handle different client annotation types appropriately</li>
	 *   <li>Return the correct {@link RedisValueTypeEnum} for the key</li>
	 * </ul>
	 *
	 * @param annotation the client annotation to extract value type from
	 * @return the Redis value type specified by the annotation, or null if not specified
	 * @see RedisValueTypeEnum
	 */
	protected RedisValueTypeEnum getValueType(Annotation annotation) {
		return null;
	}
	
	/**
	 * Retrieves the cached Redis value type for a given method.
	 * 
	 * <p>This method provides access to the pre-validated and cached value type
	 * information for cross-key operations. The value types are cached during
	 * the initial validation process in {@link #getOrangeRedisExecutor(Method)}.
	 * It returns:
	 * <ul>
	 *   <li>The common value type for all keys involved in the operation</li>
	 *   <li>null if no value type information is cached for the method</li>
	 * </ul>
	 * 
	 * <p>The value type represents the type of data stored in the Redis keys,
	 * ensuring type consistency across all keys involved in cross-key operations.
	 *
	 * @param method the method to get the value type for
	 * @return the cached {@link RedisValueTypeEnum} for the operation, or null if not found
	 * @see RedisValueTypeEnum
	 * @see #getOrangeRedisExecutor(Method)
	 */
	public RedisValueTypeEnum getValueType(Method method) {
		return OPEATION_VALUE_TYPE_MAPPING.get(method);
	}
	
	/**
	 * Retrieves the list of key classes associated with a given method.
	 * 
	 * <p>This method provides access to the cached list of key classes involved
	 * in a cross-key operation. The key classes are cached during the initial
	 * validation process in {@link #getOrangeRedisExecutor(Method)}. It returns:
	 * <ul>
	 *   <li>An ordered list of key classes as specified in {@link CrossOperationKeys}</li>
	 *   <li>null if no key classes are cached for the method</li>
	 * </ul>
	 * 
	 * <p>The order of key classes is significant for operations like weighted unions
	 * where the position of each key corresponds to its weight in the
	 * {@link Aggregate} annotation.
	 *
	 * @param method the method to get key classes for
	 * @return the cached list of key classes for the operation, or null if not found
	 * @see CrossOperationKeys
	 * @see #getOrangeRedisExecutor(Method)
	 */
	public List<Class<?>> getKeyClasses(Method method){
		return OPEATION_KEYS_MAPPING.get(method);
	}

	/**
	 * Retrieves the destination key class for storing operation results.
	 * 
	 * <p>This method provides access to the cached destination key class for
	 * operations that store their results in a new Redis key. The destination
	 * key class is cached during the initial validation process in
	 * {@link #getOrangeRedisExecutor(Method)}. It returns:
	 * <ul>
	 *   <li>The key class specified by {@link StoreTo} annotation</li>
	 *   <li>null if no destination key is configured for the method</li>
	 * </ul>
	 * 
	 * <p>The destination key class is used for operations that need to store
	 * their results, such as set unions or intersections that save their
	 * output to a new Redis key instead of returning it directly.
	 *
	 * @param method the method to get the destination key class for
	 * @return the cached destination key class for the operation, or null if not configured
	 * @see StoreTo
	 * @see #getOrangeRedisExecutor(Method)
	 */
	public Class<?> getStoreTo(Method method) {
		return OPEATION_STORE_TO_MAPPING.get(method);
	}

	/**
	 * Retrieves the OrangeClientFactoryProvider instance.
	 * 
	 * <p>This protected accessor method provides access to the client factory provider
	 * that was configured during initialization. The provider is responsible for:
	 * <ul>
	 *   <li>Creating and managing Redis client factory instances</li>
	 *   <li>Supporting client configuration validation</li>
	 *   <li>Facilitating client creation for Redis operations</li>
	 * </ul>
	 * 
	 * <p>This method is primarily used internally by validation methods to verify
	 * client configurations and build client factory metadata.
	 *
	 * @return the configured {@link OrangeClientFactoryProvider} instance
	 * @see OrangeClientFactoryProvider
	 * @see #checkClientType(Class, Set, Class)
	 */
	protected OrangeClientFactoryProvider getProvider() {
		return provider;
	}
	
}