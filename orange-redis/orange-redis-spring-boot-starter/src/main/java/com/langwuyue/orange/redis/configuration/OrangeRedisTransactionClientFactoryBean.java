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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import com.langwuyue.orange.redis.OrangeRedisCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisDefaultCircuitBreaker;
import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisKey;
import com.langwuyue.orange.redis.annotation.OrangeRedisTxTimeoutListener;
import com.langwuyue.orange.redis.annotation.transaction.OrangeRedisTransactionClient;
import com.langwuyue.orange.redis.executor.multiplelocks.OrangeExpirationTimeAutoInitializer;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionTimeoutListener;
import com.langwuyue.orange.redis.listener.OrangeRedisMultipleSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.OrangeRedisSetIfAbsentListener;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorsMapping;
import com.langwuyue.orange.redis.mapping.OrangeRedisTransactionExecutorIdGenerator;
import com.langwuyue.orange.redis.mapping.OrangeRedisTransactionExecutorsMapping;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultHashOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultScriptOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisDefaultZSetOperations;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.timer.OrangeRenewTimerWheel;

/**
 * Factory bean for creating Redis transaction clients.
 * This class extends the abstract factory bean to provide specific implementation
 * for transaction-related Redis operations. It manages transaction executors,
 * timeout listeners, and circuit breakers for Redis transactions.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisTransactionClientFactoryBean extends OrangeRedisClientAbstractFactoryBean {
	
	/**
	 * The transaction client annotation instance.
	 * This field stores the annotation metadata from {@link OrangeRedisTransactionClient}
	 * that configures this client's behavior, including circuit breaker settings
	 * and transaction timeout configuration.
	 */
	private OrangeRedisTransactionClient client;
	
	/**
	 * Static cache for the transaction executors mapping.
	 * This mapping is shared across all instances of the factory bean to avoid
	 * redundant creation of executors for the same operations. It is lazily
	 * initialized when first accessed through {@link #getExecutorsMapping()}.
	 *
	 */
	private static OrangeRedisTransactionExecutorsMapping EXECUTORS_MAPPING;
	
	/**
	 * Timer wheel instance for managing transaction timeouts.
	 * This component is responsible for tracking and handling transaction timeouts
	 * in an efficient manner using a timing wheel algorithm.
	 */
	private OrangeRenewTimerWheel wheel;
	
	/**
	 * Auto-initializer for expiration times in Redis transactions.
	 * This component ensures proper initialization of expiration times
	 * for keys involved in transactions.
	 */
	private OrangeExpirationTimeAutoInitializer expirationTimeAutoInitializer;
	
	/**
	 * Logger instance for transaction-related operations.
	 * Used to log transaction lifecycle events, errors, and other significant
	 * occurrences during transaction processing.
	 *
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new OrangeRedisTransactionClientFactoryBean with the specified parameters.
	 *
	 * @param operationOwner the class that owns the Redis operations
	 * @param clientDefinitionClass the class that defines the Redis client
	 * @param configuration the Redis configuration to use
	 */
	public OrangeRedisTransactionClientFactoryBean(
			Class<?> operationOwner,
			Class<?> clientDefinitionClass,
			OrangeRedisConfiguration configuration
	) {
		super(operationOwner, configuration, clientDefinitionClass);
	}
	
	/**
	 * Gets the mapping of Redis executors for transaction operations.
	 * This method initializes and returns a singleton instance of OrangeRedisTransactionExecutorsMapping
	 * which contains all necessary executors for Redis operations including hash, set, zset operations,
	 * and transaction management.
	 *
	 * @return the executors mapping for Redis transaction operations
	 */
	@Override
	protected OrangeRedisExecutorsMapping getExecutorsMapping() {
		if(EXECUTORS_MAPPING !=  null) {
			return EXECUTORS_MAPPING;
		}
		RedisTemplate<String, byte[]> template = this.getRedisTemplate();
		OrangeRedisScriptOperations scriptOperations = new OrangeRedisDefaultScriptOperations(
			this.getRedisTemplate(), 
			getRedisSerializer(),
			getLogger()
		);
		EXECUTORS_MAPPING = new OrangeRedisTransactionExecutorsMapping(
			new OrangeRedisDefaultHashOperations(template, getRedisSerializer(),getLogger()), 
			new OrangeRedisDefaultSetOperations(template,getRedisSerializer(),getLogger()),
			new OrangeRedisDefaultZSetOperations(template,getRedisSerializer(),getLogger()),
			new OrangeRedisTransactionExecutorIdGenerator(), 
			getListeners(), 
			scriptOperations,
			getMultipleListener(),
			getProperties().getTransaction(),
			this.wheel,
			this.expirationTimeAutoInitializer,
			getTransactionTimeoutCallback(),
			this.logger
		);
		return EXECUTORS_MAPPING;
	}
	
	/**
	 * Gets the collection of multiple set-if-absent listeners.
	 * This method provides listeners that handle multiple key set-if-absent operations
	 * in a transaction context. Currently returns an empty collection as default implementation.
	 *
	 * @return an empty collection of OrangeRedisMultipleSetIfAbsentListener
	 */
	@Override
	protected Collection<OrangeRedisMultipleSetIfAbsentListener> getMultipleListener() {
		return new ArrayList<>();
	}

	/**
	 * Gets the collection of set-if-absent listeners.
	 * This method provides listeners that handle set-if-absent operations
	 * in a transaction context. Currently returns an empty collection as default implementation.
	 *
	 * @return an empty collection of OrangeRedisSetIfAbsentListener
	 */
	@Override
	protected Collection<OrangeRedisSetIfAbsentListener> getListeners() {
		return new ArrayList<>();
	}
	
	/**
	 * Gets the transaction timeout callback listeners mapped by their keys.
	 * This method scans the application context for beans implementing OrangeRedisTransactionTimeoutListener
	 * and annotated with OrangeRedisTxTimeoutListener. It validates that each listener's key class
	 * is properly annotated with OrangeRedisKey and maps the listeners by their Redis keys.
	 *
	 * @return a map of Redis keys to their corresponding transaction timeout listeners
	 * @throws OrangeRedisException if a listener is not properly annotated or its key class is missing required annotations
	 */
	protected Map<String,OrangeRedisTransactionTimeoutListener> getTransactionTimeoutCallback() {
		Map<String, OrangeRedisTransactionTimeoutListener> beanMap = this.getApplicationContext().getBeansOfType(OrangeRedisTransactionTimeoutListener.class);
		if(beanMap == null || beanMap.isEmpty()) {
			return new HashMap<>();
		}
		Map<String,OrangeRedisTransactionTimeoutListener> callbacks = new HashMap<>();
		beanMap.forEach((k,v) -> {
			OrangeRedisTxTimeoutListener listener = v.getClass().getAnnotation(OrangeRedisTxTimeoutListener.class);
			if(listener == null) {
				throw new OrangeRedisException(
					String.format(
						"The class %s must be annotated with @%s", 
						v.getClass(),
						OrangeRedisTransactionTimeoutListener.class
					)
				);
			}
			Class<?> keyClass = listener.key();
			OrangeRedisKey redisKey = keyClass.getAnnotation(OrangeRedisKey.class);
			if(redisKey == null) {
				throw new OrangeRedisException(
					String.format(
						"The key of @%s must be annotated with @%s", 
						OrangeRedisTransactionTimeoutListener.class,
						OrangeRedisKey.class
					)
				);
			}
			callbacks.put(getOriginKey(redisKey.key()), v);
		});
		return callbacks;
	}

	/**
	 * Gets the Redis value type for this transaction client.
	 * This method returns the value type specified in the client annotation.
	 *
	 * @return the Redis value type enum for this transaction client
	 */
	@Override
	protected RedisValueTypeEnum getValueType() {
		return client.valueType();
	}
	
	/**
	 * Sets the application context and initializes required beans.
	 * This method retrieves and initializes the timer wheel, expiration time initializer,
	 * and logger beans from the application context.
	 *
	 * @param applicationContext the Spring application context
	 * @throws BeansException if the required beans cannot be found or initialized
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		super.setApplicationContext(applicationContext);
		this.wheel = applicationContext.getBean(OrangeRenewTimerWheel.class);
		this.expirationTimeAutoInitializer = applicationContext.getBean(OrangeExpirationTimeAutoInitializer.class);
		this.logger = applicationContext.getBean(OrangeRedisLogger.class);
	}
	
	/**
	 * Gets the circuit breaker class for this transaction client.
	 * This method retrieves the circuit breaker class from the client annotation.
	 * If a custom breaker class name is specified and the default breaker is used,
	 * it attempts to load the class by name.
	 *
	 * @return the circuit breaker class to use for this client
	 */
	@Override
	protected Class<? extends OrangeRedisCircuitBreaker> getCircuitBreakerClass(){
		this.client = this.getClientDefinitionClass().getAnnotation(OrangeRedisTransactionClient.class);
		Class<? extends OrangeRedisCircuitBreaker> clazz = client.breaker();
		if(clazz != OrangeRedisDefaultCircuitBreaker.class) {
			return clazz;
		}
		if(client.breakerClassName() == null || client.breakerClassName().trim().isEmpty()) {
			return clazz;
		}
		try {
			return (Class<? extends OrangeRedisCircuitBreaker>) Class.forName(client.breakerClassName());
		}catch (Exception e) {
			this.getLogger().warn(String.format(
				"Get circuit breaker Class error, operation:%s",
				this.getOperationOwner()
			),e);
			return clazz;
		}
	}

	/**
	 * Cleans up resources when the bean is being destroyed.
	 * This method ensures proper cleanup of the transaction manager and timer wheel
	 * when the factory bean is being destroyed by the Spring container.
	 *
	 * @throws Exception if an error occurs during cleanup
	 */
	protected void destroy() throws Exception {
		OrangeRedisDefaultTransactionManager transactionManager = getTransactionManger();
		if(transactionManager != null) {
			transactionManager.destory();
		}
		if(this.wheel != null) {
			this.wheel.destory();
		}
	}
	
	/**
	 * Retrieves a set of dead transactions that need to be cleaned up.
	 * Dead transactions are those that have timed out or failed but still hold resources.
	 * This method provides access to these transactions for monitoring and cleanup purposes.
	 *
	 * @return a set of dead transaction objects
	 * @throws Exception if there's an error retrieving the dead transactions
	 */
	public Set<Object> getDeadTransaction() throws Exception {
		return getTransactionManger().getDeadTransaction();
	}
	
	/**
	 * Retrieves the transaction manager instance for this factory bean.
	 * The transaction manager is responsible for coordinating Redis transactions
	 * and ensuring their proper execution and cleanup. This method returns null
	 * if the executors mapping hasn't been initialized yet.
	 *
	 * @return the transaction manager instance, or null if not initialized
	 */
	protected OrangeRedisDefaultTransactionManager getTransactionManger() {
		if(EXECUTORS_MAPPING != null) {
			return EXECUTORS_MAPPING.getTransactionManager();
		}
		return null;
	}
}