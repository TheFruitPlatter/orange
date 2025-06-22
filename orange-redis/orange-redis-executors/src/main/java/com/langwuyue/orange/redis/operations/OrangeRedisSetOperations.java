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
 * Interface for Redis set operations providing methods to manipulate set values in Redis.
 * Supports standard set operations like union, intersection, difference, and membership checks.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisSetOperations extends OrangeRedisOperations {

	/**
	 * Adds one or more members to a set.
	 * Equivalent to Redis SADD command.
	 *
	 * @param key the set key
	 * @param valueType the type of the values
	 * @param values the values to add
	 * @return the number of elements added to the set
	 * @throws Exception if any error occurs
	 */
	Long add(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;

	/**
	 * Gets all members of a set.
	 * Equivalent to Redis SMEMBERS command.
	 *
	 * @param key the set key
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return all members of the set
	 * @throws Exception if any error occurs
	 */
	Set<Object> members(String key, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	/**
	 * Gets the number of members in a set.
	 * Equivalent to Redis SCARD command.
	 *
	 * @param key the set key
	 * @return the number of members in the set
	 */
	Long size(String key);

	/**
	 * Checks if one or more values are members of a set.
	 * Equivalent to Redis SISMEMBER command for multiple values.
	 *
	 * @param key the set key
	 * @param valueType the type of the values
	 * @param values the values to check
	 * @return map of values to their membership status (true if member)
	 * @throws Exception if any error occurs
	 */
	Map<Object, Boolean> isMember(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;

	/**
	 * Gets distinct random members from a set.
	 * Equivalent to Redis SRANDMEMBER command with count.
	 *
	 * @param key the set key
	 * @param count the number of distinct members to return
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return distinct random members from the set
	 * @throws Exception if any error occurs
	 */
	Set<Object> distinctRandomMembers(String key, long count, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	/**
	 * Gets random members from a set (may contain duplicates).
	 * Equivalent to Redis SRANDMEMBER command with count.
	 *
	 * @param key the set key
	 * @param count the number of members to return
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return random members from the set (may contain duplicates)
	 * @throws Exception if any error occurs
	 */
	List<Object> randomMembers(String key, long count, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	/**
	 * Removes one or more members from a set.
	 * Equivalent to Redis SREM command.
	 *
	 * @param key the set key
	 * @param valueType the type of the values
	 * @param values the values to remove
	 * @return the number of members removed
	 * @throws Exception if any error occurs
	 */
	Long remove(String key, RedisValueTypeEnum valueType, Object... values) throws Exception;

	/**
	 * Gets the difference between sets.
	 * Equivalent to Redis SDIFF command.
	 *
	 * @param comparisonKeys the keys of sets to compare
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return members of the difference between sets
	 * @throws Exception if any error occurs
	 */
	Set<Object> difference(Collection<String> comparisonKeys, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	/**
	 * Gets the difference between sets and stores it in a new set.
	 * Equivalent to Redis SDIFFSTORE command.
	 *
	 * @param comparisonKeys the keys of sets to compare
	 * @param storeTo the destination key to store the result
	 * @return the number of elements in the resulting set
	 */
	Long differenceAndStore(Collection<String> comparisonKeys, String storeTo);

	/**
	 * Gets the union of sets and stores it in a new set.
	 * Equivalent to Redis SUNIONSTORE command.
	 *
	 * @param comparisonKeys the keys of sets to union
	 * @param storeTo the destination key to store the result
	 * @return the number of elements in the resulting set
	 */
	Long unionAndStore(Collection<String> comparisonKeys, String storeTo);

	/**
	 * Gets the union of sets.
	 * Equivalent to Redis SUNION command.
	 *
	 * @param comparisonKeys the keys of sets to union
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return members of the union of sets
	 * @throws Exception if any error occurs
	 */
	Set<Object> union(Collection<String> comparisonKeys, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	/**
	 * Gets the intersection of sets.
	 * Equivalent to Redis SINTER command.
	 *
	 * @param comparisonKeys the keys of sets to intersect
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return members of the intersection of sets
	 * @throws Exception if any error occurs
	 */
	Set<Object> intersect(Collection<String> comparisonKeys, RedisValueTypeEnum valueType, Type returnType) throws Exception;

	/**
	 * Gets the intersection of sets and stores it in a new set.
	 * Equivalent to Redis SINTERSTORE command.
	 *
	 * @param comparisonKeys the keys of sets to intersect
	 * @param storeTo the destination key to store the result
	 * @return the number of elements in the resulting set
	 */
	Long intersectAndStore(Collection<String> comparisonKeys, String storeTo);

	/**
	 * Removes and returns one or more random members from a set.
	 * Equivalent to Redis SPOP command with count.
	 *
	 * @param key the set key
	 * @param count the number of members to pop
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return the popped members
	 * @throws Exception if any error occurs
	 */
	List<Object> popMember(String key, long count, RedisValueTypeEnum valueType, Type returnType) throws Exception;
	
	/**
	 * Moves a member from one set to another.
	 * Equivalent to Redis SMOVE command.
	 *
	 * @param key the source set key
	 * @param value the member to move
	 * @param valueType the type of the value
	 * @param destKey the destination set key
	 * @return true if the member was moved, false if not a member of source set
	 * @throws Exception if any error occurs
	 */
	Boolean move(String key, Object value, RedisValueTypeEnum valueType, String destKey) throws Exception;
	
	/**
	 * Incrementally iterates over members of a set matching a pattern.
	 * Similar to Redis SSCAN command.
	 *
	 * @param key the set key
	 * @param pattern the pattern to match
	 * @param count the approximate number of elements to return per call
	 * @param pageNo the page number (cursor) for pagination
	 * @param valueType the type of the values
	 * @param returnType the expected return type
	 * @return scan results containing members and next cursor position
	 * @throws Exception if any error occurs
	 */
	ScanResults scan(String key, String pattern, Integer count, Long pageNo, RedisValueTypeEnum valueType, Type returnType) throws Exception;
	
	/**
	 * Container for set scan results including members and cursor position.
	 */
	public static class ScanResults {
		
		private Set<Object> members;
		
		private long cursor;
		
		/**
		 * Creates a new ScanResults instance.
		 *
		 * @param members the set members found in this scan
		 * @param cursor the cursor position for next scan
		 */
		public ScanResults(Set<Object> members, long cursor) {
			super();
			this.members = members;
			this.cursor = cursor;
		}
		
		/**
		 * Gets the members found in this scan.
		 *
		 * @return the set members
		 */
		public Set<Object> getMembers() {
			return members;
		}
		
		/**
		 * Sets the members for this scan.
		 *
		 * @param members the set members to set
		 */
		public void setMembers(Set<Object> members) {
			this.members = members;
		}
		
		/**
		 * Gets the cursor position for next scan.
		 *
		 * @return the cursor position
		 */
		public long getCursor() {
			return cursor;
		}
		
		/**
		 * Sets the cursor position for next scan.
		 *
		 * @param cursor the cursor position to set
		 */
		public void setCursor(long cursor) {
			this.cursor = cursor;
		}
	}
}