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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisContext {

	private Key redisKey;
	
	private Class<?> operationOwner;
	
	private Method operationMethod;
	
	private Object[] args;
	
	private RedisValueTypeEnum valueType;
	
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

	public Key getRedisKey() {
		return redisKey;
	}

	public Class<?> getOperationOwner() {
		return operationOwner;
	}

	public Method getOperationMethod() {
		return operationMethod;
	}

	public Object[] getArgs() {
		return args;
	}
	
	public RedisValueTypeEnum getValueType() {
		return valueType;
	}

	public static class Key {
		private String originalKey;
		private String value;
		private long expirationTime;
		private TimeUnit expirationTimeUnit;
		
		public Key(String originalKey, String value, long expirationTime, TimeUnit expirationTimeUnit) {
			super();
			this.originalKey = originalKey;
			this.value = value;
			this.expirationTime = expirationTime;
			this.expirationTimeUnit = expirationTimeUnit;
		}
		public String getValue() {
			return value;
		}
		public long getExpirationTime() {
			return expirationTime;
		}
		public TimeUnit getExpirationTimeUnit() {
			return expirationTimeUnit;
		}
		public String getOriginalKey() {
			return originalKey;
		}
		public void setExpirationTime(long expirationTime) {
			this.expirationTime = expirationTime;
		}
		public void setExpirationTimeUnit(TimeUnit expirationTimeUnit) {
			this.expirationTimeUnit = expirationTimeUnit;
		}
		@Override
		public int hashCode() {
			return Objects.hash(expirationTime, expirationTimeUnit, value);
		}
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
		@Override
		public String toString() {
			return "Key [originalKey=" + originalKey + ", value=" + value + ", expirationTime=" + expirationTime
					+ ", expirationTimeUnit=" + expirationTimeUnit + "]";
		}
		
	}
}
