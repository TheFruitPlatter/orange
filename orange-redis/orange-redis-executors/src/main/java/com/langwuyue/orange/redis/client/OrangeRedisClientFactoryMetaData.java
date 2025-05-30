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

/**
 * Metadata class that holds information about Redis client classes and their corresponding factory classes.
 * 
 * <p>This class serves as a container for metadata related to Redis client factories in the Orange Redis framework.
 * It stores the mapping between a client class and its factory class, which is essential for the dynamic
 * creation and management of Redis clients.
 * 
 * <p>The metadata typically includes:
 * <ul>
 *   <li>The client class - the actual Redis client implementation class</li>
 *   <li>The factory class - the class responsible for creating instances of the client</li>
 * </ul>
 * 
 * <p>This metadata is used by the framework to determine which factory should be used to create
 * a specific type of Redis client, enabling a flexible and extensible client creation mechanism.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisClientFactoryMetaDataBuilder
 */
public class OrangeRedisClientFactoryMetaData {

	/**
	 * The Redis client implementation class.
	 */
	private Class<?> clientClass;
	
	/**
	 * The factory class responsible for creating instances of the client.
	 */
	private Class<?> factoryClass;

	/**
	 * Constructs a new metadata object with the specified client and factory classes.
	 * 
	 * <p>This constructor creates a new metadata object that associates a Redis client class
	 * with its corresponding factory class. This association is used by the framework to
	 * determine which factory should be used to create instances of the client.
	 *
	 * @param clientClass the Redis client implementation class
	 * @param factoryClass the factory class responsible for creating instances of the client
	 */
	public OrangeRedisClientFactoryMetaData(Class<?> clientClass, Class<?> factoryClass) {
		super();
		this.clientClass = clientClass;
		this.factoryClass = factoryClass;
	}

	/**
	 * Returns the Redis client implementation class.
	 * 
	 * <p>This method provides access to the client class that this metadata object
	 * is associated with. The client class represents the actual Redis client implementation
	 * that will be created by the factory.
	 *
	 * @return the Redis client implementation class
	 */
	public Class<?> getClientClass() {
		return clientClass;
	}

	/**
	 * Returns the factory class responsible for creating instances of the client.
	 * 
	 * <p>This method provides access to the factory class that this metadata object
	 * is associated with. The factory class is responsible for creating instances of
	 * the Redis client implementation.
	 *
	 * @return the factory class for the Redis client
	 */
	public Class<?> getFactoryClass() {
		return factoryClass;
	}
}