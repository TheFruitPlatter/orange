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
package com.langwuyue.orange.redis.template.set;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.ScanPattern;
import com.langwuyue.orange.redis.annotation.set.IsMembers;
import com.langwuyue.orange.redis.annotation.set.OrangeRedisSetClient;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate;

/**
 * Interface template for Redis Set operations. 
 * Developers should extend this interface and annotate the child interface with {@code OrangeRedisKey}.
 * 
 * <p>A example is:
 * <blockquote><pre>
 *  {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:set:example1")} 
 *  public interface OrangeRedisSetExample1Api extends JSONOperationsTemplate{@code<Value>} {
 *  	
 *  	{@code @Override}
 *  	Value getValue();
 *  
 *  	// Custom operations can be added here
 *  }
 * </pre></blockquote>
 * 
 * 
 * <p>Please review examples for more information.
 * 
 * @param <T> The type of elements stored in the Redis Set (will be serialized as JSON)
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisSetClient(valueType = RedisValueTypeEnum.JSON)
public interface JSONOperationsTemplate<T> extends GlobalOperationsTemplate {
	
	/**
	 * <h3>Add a member into the Redis Set</h3>
	 * 
	 * @param value the value to be added
	 * @return {@code true} if the value was successfully added,
	 * 		   {@code false} if the value already exists (or other failure conditions)
	 */
	@AddMembers
	Boolean add(@RedisValue T value);
	
	/**
	 * <h3>Add members into the Redis Set</h3>
	 * 
	 * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines whether the method adding remaining members to the Redis set upon exceptions.
	 * True: continue | False: break
	 * 
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @param members the members to be added.
	 * @return LinkedHashMap where keys are the members and values indicate whether each member was successfully added
	 */
	@AddMembers
	@ContinueOnFailure(true)
	Map<T, Boolean> add(@Multiple Set<T> members);
	
	/**
	 * <h3>Add a member into the Redis Set if the member not exists</h3>
	 * 
	 * <p>
	 * Once the addition process is completed, the {@code OrangeRedisSetAddMembersIfAbsentListener} component ​​will be triggered​​. 
	 * Developers should ​​configure​​ {@code OrangeRedisSetAddMembersIfAbsentListener} to manage post-addition business logic. 
	 * Note that the {@code OrangeRedisSetAddMembersIfAbsentListener} implementation class must be annotated with Spring’s {@code @Component}
	 * 
	 * 
	 * <p>
	 * The property {@code deleteInTheEnd} of {@code @IfAbsent} determines if the value is deleted after operation completion.
	 * True​​: Delete | ​​False​​: Keep
	 * 
	 * 
	 * <p>
	 * Please review examples for more information.
	 * 
	 * 
	 * @param value the value to be added.
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	void addIfAbsent(@RedisValue T value);
	
	/**
	 * <h3>Add members into the Redis Set if members not exists</h3>
	 * 
	 * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines whether the method adding remaining members to the Redis set upon exceptions.
	 * True: continue | False: break
	 * 
	 * 
	 * <p>
	 * Once the addition process is completed, the {@code OrangeRedisSetAddMembersIfAbsentListener} component ​​will be triggered​​. 
	 * Developers should ​​configure​​ {@code OrangeRedisSetAddMembersIfAbsentListener} to manage post-addition business logic. 
	 * Note that the {@code OrangeRedisSetAddMembersIfAbsentListener} implementation class must be annotated with Spring’s {@code @Component}
	 * 
	 * 
	 * <p>
	 * The property {@code deleteInTheEnd} of {@code @IfAbsent} determines if the value is deleted after operation completion.
	 * True​​: Delete | ​​False​​: Keep
	 * 
	 * 
	 * <p>
	 * Please review examples for more information.
	 * 
	 * 
	 * @param members is a set. The members to be added
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	@ContinueOnFailure(true)
	void addIfAbsent(@Multiple Set<T> members);
	
	/**
	 * <h3>Compare and swap</h3>
	 * <p>The {@code oldValue} will be overwritten by {@code newValue} only if it matches current value in the set.
	 * 
	 * 
	 * @param oldValue  The expected current value in the set.
	 * @param newValue  The new value to set if verification succeeds.
	 * @return Boolean  True if the value was updated, false otherwise (e.g., if {@code oldValue} did not match).
	 */
	@CAS
	Boolean compareAndSwap(@RedisOldValue T oldValue, @RedisValue T newValue);
	
	/**
	 * <h3>Random get a member from set</h3>
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @return the member.
	 */
	@GetMembers
	@Random
	T randomGetOne();
	
	/**
	 * <h3>Random get {@code count}  members from set</h3>
	 *
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @param count 		Number of members to return.
	 * @return a ArrayList. The returned list maintains the same order as the randomized sequence.
	 */
	@GetMembers
	@Random
	List<T> randomGetMembers(@Count Long count);
	
	
	/**
	 * <h3>Get {@code count} distinct random members from set.</h3>
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 *  
	 * 
	 * @param count 	Number of members to return.
	 * @return a LinkedHashSet,the returned set maintains the same order as the randomized sequence.
	 */
	@GetMembers
	@Distinct
	@Random
	Set<T> distinctRandomGetMembers(@Count Long count);
	
	/**
	 * <h3>Get {@code count} all members from set.</h3>
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 *  
	 * 
	 * @return a LinkedHashSet.
	 */
	@GetMembers
	Set<T> getMembers();
	
	/**
	 * <h3>Check whether the member exists.</h3>
	 * 
	 * 
	 * @return a boolean value, true if exists, false otherwise.
	 */
	@IsMembers
	Boolean isMember(@RedisValue T member);
	
	/**
	 * <h3>Check whether the members exist.</h3>
	 * 
	 * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines whether the method checking remaining members to the Redis set upon exceptions.
	 * True: continue | False: break
	 * 
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @param members the members to be checked.
	 * @return LinkedHashMap where keys are the members and values indicate whether each member exists.
	 */
	@IsMembers
	Map<T, Boolean> isMembers(@Multiple Set<T> members);
	
	/**
	 * <h3>Atomically removes and returns the member.</h3>
	 * 
	 * @return a member
	 */
	@PopMembers
	T pop();
	
	/**
	 * <h3>Atomically removes and returns up to {@code count} members.</h3>
	 * 
	 * @return members
	 */
	@PopMembers
	Set<T> pop(@Count Long count);
	
	/**
	 * <h3>Get the Redis set total size</h3>
	 * 
	 * @return Long
	 */
	@GetSize
	Long getSize();
	
	/**
	 * <h3>Remove member</h3>
	 * 
	 * @param value
	 * @return a boolean value, true if member was successfully removed, false otherwise(e.g., The member was already removed).
	 */
	@RemoveMembers
	Boolean remove(@RedisValue T value);
	
	/**
	 * <h3>Remove members from the Redis Set</h3>
	 * 
	 * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines whether the method adding remaining members to the Redis set upon exceptions.
	 * True: continue | False: break
	 * 
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * @param members the members to be removed.
	 * @return LinkedHashMap where keys are the members and values indicate whether each member was successfully removed
	 */
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<T,Boolean> remove(@Multiple Set<T> values);
	
	/**
	 * <h3> Scan members by pattern</h3>
	 * 
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * 
	 * 
	 * 
	 * @param pattern
	 * @param count
	 * @param pageNo
	 * @return members
	 */
	@GetMembers
	Set<T> scan(@ScanPattern String pattern,@Count Long count,@PageNo Long pageNo);
}
