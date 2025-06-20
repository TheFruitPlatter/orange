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
package com.langwuyue.orange.redis.executor.hash;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.hash.context.OrangeCountHashContext;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisHashOperations;

/**
 * Executor for randomly retrieving multiple members from a Redis Hash (may contain duplicates).
 *
 * <p>This executor provides functionality to get multiple random key-value pairs from a Redis Hash.
 * The operation is useful for scenarios requiring multiple random samples where duplicates are acceptable.
 *
 * <p>The executor supports the following annotations:
 * <ul>
 *   <li>{@link com.langwuyue.orange.redis.annotation.Random} - Marks this as a random member retrieval operation</li>
 *   <li>{@link Count} - Specifies the number of random members to retrieve</li>
 * </ul>
 *
 * <p>Key features:
 * <ul>
 *   <li>Returns multiple random members from the hash (may contain duplicates)</li>
 *   <li>Uses multiple HRANDFIELD commands internally (one per member)</li>
 *   <li>Supports both simple and complex return types</li>
 *   <li>Automatically handles type conversion of returned values</li>
 * </ul>
 *
 * <p>Return value handling:
 * <ul>
 *   <li>For collection/array return types: returns all random members in the collection</li>
 *   <li>For non-collection return types: returns the last random member</li>
 * </ul>
 *
 * <p>Performance considerations:
 * Each HRANDFIELD operation has a time complexity of O(1), but multiple calls are made
 * (one per requested member), so performance scales linearly with the count.
 *
 * <p>Note: 
 * <ul>
 *   <li>For retrieving a single random member, use {@link OrangeRandomMemberExecutor}</li>
 *   <li>For retrieving multiple distinct random members, use {@link OrangeRandomAndDistinctMembersExecutor}</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/hash">Orange Redis Hash Documentation</a>
 */
public class OrangeRandomMembersExecutor extends OrangeRandomMemberExecutor {
	
	/**
	 * Constructs a new OrangeRandomMembersExecutor instance.
	 * 
	 * @param operations the Redis hash operations instance
	 * @param idGenerator the executor ID generator
	 */
	public OrangeRandomMembersExecutor(OrangeRedisHashOperations operations,OrangeRedisExecutorIdGenerator idGenerator) {
		super(operations,idGenerator);
	}

	/**
	 * Gets the list of supported annotation classes for this executor.
	 * Adds {@link Count} annotation to the parent class's supported annotations.
	 *
	 * @return list of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		List classes = super.getSupportedAnnotationClasses();
		classes.add(Count.class);
		return classes;
	}

	/**
	 * Gets the context class used by this executor.
	 * This executor uses {@link OrangeCountHashContext} as its context class.
	 *
	 * @return the context class
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeCountHashContext.class;
	}
	
	/**
	 * Executes the random members retrieval operation.
	 * Randomly selects multiple members from the hash based on the count parameter.
	 *
	 * <p>Operation details:
	 * <ul>
	 *   <li>Makes multiple calls to parent class's execute() method (one per requested member)</li>
	 *   <li>Handles both collection/array and single value return types</li>
	 *   <li>Returns all results combined in a single list</li>
	 * </ul>
	 *
	 * @param context the Redis operation context containing count parameter
	 * @return list containing all randomly selected members
	 * @throws Exception if there's an error during Redis operation or type conversion
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeCountHashContext ctx = (OrangeCountHashContext) context;
		Integer count = ctx.getCount();
		if(count <= 0) {
			return new ArrayList<>();
		}
		List<Object> results = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			Object object = super.execute(context);
			if(object instanceof Collection) {
				Collection collection = (Collection)object;
				results.addAll(collection);
			}
			else if(object.getClass().isArray()) {
				results.add(Array.get(object, 0));
			}else{
				results.add(object);
			}
		}
		return results;
	}
}