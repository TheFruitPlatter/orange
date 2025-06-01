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
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.cross.StoreTo;
import com.langwuyue.orange.redis.annotation.geo.OrangeRedisGeoClient;
import com.langwuyue.orange.redis.annotation.zset.OrangeRedisZSetClient;
import com.langwuyue.orange.redis.client.OrangeClientFactoryProvider;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;

/**
 * Handler mapping for Redis Geo operations involving multiple keys.
 * 
 * <p>This class extends {@link OrangeCrossKeysOperationArgHandlerMapping} to provide
 * specialized handling for Redis Geo operations. It supports both Geo-specific operations
 * and ZSet operations, as Redis internally stores geo data using sorted sets.
 * 
 * <p>Key features include:
 * <ul>
 *   <li>Support for both {@link OrangeRedisGeoClient} and {@link OrangeRedisZSetClient} annotations</li>
 *   <li>Automatic validation of value types across geo operations</li>
 *   <li>Handling of store operations for geo query results</li>
 *   <li>Dual-mode client type checking for both geo and zset operations</li>
 * </ul>
 * 
 * <p>This handler is particularly useful for operations that involve:
 * <ul>
 *   <li>Geo radius searches across multiple location sets</li>
 *   <li>Storing geo operation results in new keys</li>
 *   <li>Cross-key operations involving both geo and sorted set data structures</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisGeoClient
 * @see OrangeRedisZSetClient
 * @see OrangeCrossKeysOperationArgHandlerMapping
 */
public class OrangeGeoCrossKeysOperationArgHandlerMapping extends OrangeCrossKeysOperationArgHandlerMapping {

	/**
	 * Constructs a new OrangeGeoCrossKeysOperationArgHandlerMapping instance.
	 * 
	 * <p>Initializes a handler mapping for Redis Geo operations that work with
	 * multiple keys. This constructor sets up the necessary components for handling
	 * both geo-specific operations and the underlying sorted set operations that
	 * Redis uses to implement geo functionality.
	 *
	 * @param executorsMapping the mapping of executors for Redis operations
	 * @param operationOwner the class that owns the Redis operations
	 * @param valueHandlerMap map of value handlers for different argument types
	 * @param provider provider for Redis client factories
	 * @param clientAnnotationClass the annotation class used to mark Redis Geo clients
	 */
	public OrangeGeoCrossKeysOperationArgHandlerMapping(
			OrangeRedisExecutorsMapping executorsMapping,
			Class operationOwner,
			Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> valueHandlerMap,
			OrangeClientFactoryProvider provider, Class<? extends Annotation> clientAnnotationClass) {
		super(executorsMapping, operationOwner, valueHandlerMap, provider, clientAnnotationClass);
	}

	/**
	 * Extracts the Redis value type from either a ZSet or Geo client annotation.
	 * 
	 * <p>This method handles two types of client annotations:
	 * <ul>
	 *   <li>{@link OrangeRedisZSetClient} - For sorted set operations</li>
	 *   <li>{@link OrangeRedisGeoClient} - For geo-specific operations</li>
	 * </ul>
	 * 
	 * <p>Since Redis stores geo data in sorted sets internally, this method supports
	 * both annotation types to allow seamless integration between geo and zset
	 * operations. The value type determines how member data is serialized and
	 * deserialized during Redis operations.
	 *
	 * @param annotation the client annotation to extract value type from
	 * @return the {@link RedisValueTypeEnum} specified in the annotation
	 */
	@Override
	protected RedisValueTypeEnum getValueType(Annotation annotation) {
		if(annotation instanceof OrangeRedisZSetClient) {
			OrangeRedisZSetClient zsetClient = (OrangeRedisZSetClient)annotation;
			return zsetClient.valueType();
		}
		OrangeRedisGeoClient geoClient = (OrangeRedisGeoClient)annotation;
		return geoClient.valueType();
	}
	
	/**
	 * Validates and retrieves the StoreTo annotation for geo operations.
	 * 
	 * <p>This method checks if a method has a {@link StoreTo} annotation and validates
	 * that the target key class is properly configured for storing geo operation results.
	 * For geo operations, results are stored in sorted sets, so this method specifically
	 * validates against {@link OrangeRedisZSetClient} configuration.
	 * 
	 * <p>The validation process includes:
	 * <ul>
	 *   <li>Checking if the StoreTo annotation is present</li>
	 *   <li>Verifying that the target key class is not null</li>
	 *   <li>Ensuring the target key class has proper client configuration</li>
	 *   <li>Validating value type compatibility between source and target</li>
	 * </ul>
	 *
	 * @param actualMethod the method to check for StoreTo annotation
	 * @param valueTypes set of value types to validate against
	 * @return the StoreTo annotation if present and valid, null otherwise
	 */
	@Override
	protected StoreTo checkStoreToAndGet(Method actualMethod,Set<RedisValueTypeEnum> valueTypes) {
		StoreTo storeTo = actualMethod.getAnnotation(StoreTo.class);
		if(storeTo == null) {
			return null;
		}
		Class<?> keyClass = storeTo.value();
		if(keyClass == null) {
			throw new OrangeRedisException(String.format("The value of @%s cannot be null", StoreTo.class));
		}
		checkClientType(keyClass,valueTypes,OrangeRedisZSetClient.class);
		return storeTo;
	}

	/**
	 * Validates client type configuration with flexible geo/zset type checking.
	 * 
	 * <p>This method implements a specialized validation strategy that accommodates
	 * the dual nature of geo operations in Redis. Since Redis implements geo operations
	 * using sorted sets internally, this method provides flexible validation that allows
	 * keys to be configured with either {@link OrangeRedisZSetClient} or 
	 * {@link OrangeRedisGeoClient} annotations.
	 * 
	 * <p>The validation logic follows these rules:
	 * <ul>
	 *   <li>For non-ZSet client types, performs standard validation</li>
	 *   <li>For ZSet client type:
	 *     <ul>
	 *       <li>First attempts to validate as a ZSet client</li>
	 *       <li>If ZSet validation fails, attempts to validate as a Geo client</li>
	 *       <li>If both validations fail, the original exception is propagated</li>
	 *     </ul>
	 *   </li>
	 * </ul>
	 * 
	 * <p>This flexible validation enables:
	 * <ul>
	 *   <li>Seamless integration between geo and zset operations</li>
	 *   <li>Reuse of existing key configurations across operation types</li>
	 *   <li>Compatibility with Redis's internal implementation details</li>
	 * </ul>
	 *
	 * @param keyClass the key class to validate
	 * @param valueTypes set of value types to check against
	 * @param clientAnnotationClass the client annotation class to validate against
	 * @throws OrangeRedisException if the key class fails both zset and geo validation
	 */
	@Override
	protected void checkClientType(Class<?> keyClass,Set<RedisValueTypeEnum> valueTypes,Class<? extends Annotation> clientAnnotationClass) {
		if(clientAnnotationClass != OrangeRedisZSetClient.class) {
			super.checkClientType(keyClass, valueTypes, clientAnnotationClass);
		}else{
			try {
				super.checkClientType(keyClass, valueTypes, clientAnnotationClass);	
			}catch (Exception e) {
				super.checkClientType(keyClass, valueTypes, OrangeRedisGeoClient.class);	
			}
		}
	}
}