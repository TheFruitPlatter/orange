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
package com.langwuyue.orange.redis.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;

/**
 * Base context class for Redis operations in the Orange framework.
 * 
 * <p>This class holds all the necessary information required to execute a Redis operation,
 * including the operation's owner class, method details, arguments, key information, and
 * value type. It serves as the foundation for more specific Redis operation contexts.
 *
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisContext {

	/**
	 * The Redis key information including the key value and expiration details.
	 */
	private Key redisKey;
	
	/**
	 * The class that owns the Redis operation method.
	 */
	private Class<?> operationOwner;
	
	/**
	 * The method that represents the Redis operation.
	 */
	private Method operationMethod;
	
	/**
	 * The arguments to be passed to the operation method.
	 */
	private Object[] args;
	
	/**
	 * The type of Redis value being operated on.
	 */
	private RedisValueTypeEnum valueType;
	
	/**
	 * Constructs a new Redis operation context with the specified parameters.
	 *
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments to be passed to the operation method
	 * @param redisKey the Redis key information including value and expiration details
	 * @param valueType the type of Redis value being operated on
	 */
	public OrangeRedisContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) {
		super();
		this.operationOwner = operationOwner;
		this.operationMethod = operationMethod;
		this.args = args;
		this.valueType = valueType;
		this.redisKey = redisKey;
	}
	
	/**
	 * Creates a new instance of a specific Redis context class using reflection.
	 * 
	 * <p>This factory method creates a new instance of the specified context class
	 * using its constructor that takes the standard Redis context parameters. This
	 * method is useful when you need to create a specific type of Redis context
	 * dynamically.
	 *
	 * @param contextClass the specific Redis context class to instantiate
	 * @param operationOwner the class that owns the Redis operation method
	 * @param operationMethod the method representing the Redis operation
	 * @param args the arguments to be passed to the operation method
	 * @param redisKey the Redis key information including value and expiration details
	 * @param valueType the type of Redis value being operated on
	 * @return a new instance of the specified context class
	 * @throws Exception if the context class cannot be instantiated
	 */
	public static OrangeRedisContext newInstance(
		Class<? extends OrangeRedisContext> contextClass,
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		Key redisKey,
		RedisValueTypeEnum valueType
	) throws Exception{
		Constructor<? extends OrangeRedisContext> constructor = contextClass.getConstructor(
				Class.class,
				Method.class,
				Object[].class,
				Key.class,
				RedisValueTypeEnum.class
		);
		return constructor.newInstance(operationOwner,operationMethod,args,redisKey,valueType);
	}

	/**
	 * Returns the Redis key information.
	 *
	 * @return the Redis key object containing key value and expiration details
	 */
	public Key getRedisKey() {
		return redisKey;
	}

	/**
	 * Returns the class that owns the Redis operation method.
	 *
	 * @return the operation owner class
	 */
	public Class<?> getOperationOwner() {
		return operationOwner;
	}

	/**
	 * Returns the method that represents the Redis operation.
	 *
	 * @return the operation method
	 */
	public Method getOperationMethod() {
		return operationMethod;
	}

	/**
	 * Returns the arguments to be passed to the operation method.
	 *
	 * @return the operation method arguments
	 */
	public Object[] getArgs() {
		return args;
	}
	
	/**
	 * Returns the type of Redis value being operated on.
	 *
	 * @return the Redis value type
	 */
	public RedisValueTypeEnum getValueType() {
		return valueType;
	}

	/**
	 * Represents a Redis key with its associated original pattern, value, and expiration settings.
	 * 
	 * <p>This class encapsulates all the information related to a Redis key, including:
	 * <ul>
	 *   <li>The original key pattern used for key generation or matching</li>
	 *   <li>The actual key value used in Redis operations</li>
	 *   <li>The expiration time value</li>
	 *   <li>The time unit for the expiration time</li>
	 * </ul>
	 * 
	 */
	public static class Key {
		/**
		 * The original key pattern used for key generation or matching.
		 * For example: "user:profile:${id}"
		 */
		private String originalKey;
		
		/**
		 * The actual key value used in Redis operations.
		 * For example: "user:profile:123"
		 */
		private String value;
		
		/**
		 * The expiration time value in the specified time unit.
		 */
		private long expirationTime;
		
		/**
		 * The time unit for the expiration time.
		 */
		private TimeUnit expirationTimeUnit;
		
		/**
		 * Constructs a new Redis key with the specified parameters.
		 *
		 * @param originalKey the original key pattern used for key generation or matching
		 * @param value the actual key value used in Redis operations
		 * @param expirationTime the expiration time value
		 * @param expirationTimeUnit the time unit for the expiration time
		 */
		public Key(String originalKey, String value, long expirationTime, TimeUnit expirationTimeUnit) {
			super();
			this.originalKey = originalKey;
			this.value = value;
			this.expirationTime = expirationTime;
			this.expirationTimeUnit = expirationTimeUnit;
		}
		
		/**
		 * Returns the actual key value used in Redis operations.
		 *
		 * @return the Redis key value
		 */
		public String getValue() {
			return value;
		}
		
		/**
		 * Returns the expiration time value.
		 *
		 * @return the expiration time in the specified time unit
		 */
		public long getExpirationTime() {
			return expirationTime;
		}
		
		/**
		 * Returns the time unit for the expiration time.
		 *
		 * @return the expiration time unit
		 */
		public TimeUnit getExpirationTimeUnit() {
			return expirationTimeUnit;
		}
		
		/**
		 * Returns the original key pattern used for key generation or matching.
		 *
		 * @return the original key pattern
		 */
		public String getOriginalKey() {
			return originalKey;
		}
		
		/**
		 * Sets the expiration time value.
		 *
		 * @param expirationTime the new expiration time value
		 */
		public void setExpirationTime(long expirationTime) {
			this.expirationTime = expirationTime;
		}
		
		/**
		 * Sets the time unit for the expiration time.
		 *
		 * @param expirationTimeUnit the new expiration time unit
		 */
		public void setExpirationTimeUnit(TimeUnit expirationTimeUnit) {
			this.expirationTimeUnit = expirationTimeUnit;
		}
		/**
		 * Returns a hash code value for this key.
		 * 
		 * <p>The hash code is computed based on the expiration time, expiration time unit,
		 * and the key value. The original key pattern is not considered in the hash code
		 * calculation.
		 *
		 * @return a hash code value for this key
		 */
		@Override
		public int hashCode() {
			return Objects.hash(expirationTime, expirationTimeUnit, value);
		}
		
		/**
		 * Compares this key to the specified object for equality.
		 * 
		 * <p>Two keys are considered equal if they have the same value, expiration time,
		 * and expiration time unit. The original key pattern is not considered in the
		 * equality check.
		 *
		 * @param obj the object to compare with
		 * @return true if the objects are equal, false otherwise
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			return expirationTime == other.expirationTime && expirationTimeUnit == other.expirationTimeUnit
					&& Objects.equals(value, other.value);
		}
		
		/**
		 * Returns a string representation of this key.
		 * 
		 * <p>The string representation includes all key properties: original key pattern,
		 * value, expiration time, and expiration time unit.
		 *
		 * @return a string representation of this key
		 */
		@Override
		public String toString() {
			return "Key [originalKey=" + originalKey + ", value=" + value + ", expirationTime=" + expirationTime
					+ ", expirationTimeUnit=" + expirationTimeUnit + "]";
		}
		
	}
}