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
package com.langwuyue.orange.redis.operations;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.RedisValueTypeEnum;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisSetOperations extends OrangeRedisOperations {

	Long add(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;

	Set<Object> members(String key,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Long size(String key);

	Map<Object, Boolean> isMember(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;

	Set<Object> distinctRandomMembers(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	List<Object> randomMembers(String key, long count,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Long remove(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;

	Set<Object> difference(Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Long differenceAndStore(Collection<String> comparisonKeys, String storeTo);

	Long unionAndStore(Collection<String> comparisonKeys, String storeTo);

	Set<Object> union(Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Set<Object> intersect(Collection<String> comparisonKeys,RedisValueTypeEnum valueType,Type returnType) throws Exception;

	Long intersectAndStore(Collection<String> comparisonKeys, String storeTo);

	List<Object> popMember(String key,long count,RedisValueTypeEnum valueType,Type returnType) throws Exception;
	
	Boolean move(String key, Object value, RedisValueTypeEnum valueType, String destKey) throws Exception;
	
	ScanResults scan(String key, String pattern, Integer count, Long pageNo, RedisValueTypeEnum valueType, Type returnType) throws Exception;
	
	public static class ScanResults {
		
		private Set<Object> members;
		
		private long cursor;
		
		public ScanResults(Set<Object> members, long cursor) {
			super();
			this.members = members;
			this.cursor = cursor;
		}
		public Set<Object> getMembers() {
			return members;
		}
		public void setMembers(Set<Object> members) {
			this.members = members;
		}
		public long getCursor() {
			return cursor;
		}
		public void setCursor(long cursor) {
			this.cursor = cursor;
		}
	}

}
