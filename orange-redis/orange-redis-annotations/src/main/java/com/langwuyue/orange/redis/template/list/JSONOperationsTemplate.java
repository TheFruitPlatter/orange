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
package com.langwuyue.orange.redis.template.list;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.GetIndexs;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisOldValue;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.annotation.list.Index;
import com.langwuyue.orange.redis.annotation.list.Left;
import com.langwuyue.orange.redis.annotation.list.OrangeRedisListClient;
import com.langwuyue.orange.redis.annotation.list.Pivot;
import com.langwuyue.orange.redis.annotation.list.Right;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate;

/**
 * Interface template for Redis List operations with JSON value serialization.
 * 
 * <p>This template provides a type-safe way to perform various Redis List operations
 * where values are stored as JSON. To use, extend this interface and annotate the 
 * child interface with {@code @OrangeRedisKey} to specify Redis key configuration.</p>
 * 
 * <p>Example usage:
 * <pre>
 * {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:list:example1")}
 * public interface OrangeRedisListExample1Api extends JSONOperationsTemplate{@code<Value>} {
 *     // Custom operations can be added here
 * }</pre>
 * </p>
 * 
 * <p>Please review examples for more information.</p>
 * 
 * @param <T> The type of elements stored in the Redis List (will be serialized as JSON)
 * @author Liang.Zhong
 * @since 1.0.0
 */
@OrangeRedisListClient(valueType = RedisValueTypeEnum.JSON)
public interface JSONOperationsTemplate<T> extends GlobalOperationsTemplate {
    
    /**
     * Sets the element at the specified position in the list
     * @param index the index to set
     * @param member the element to set
     */
    @AddMembers
    void set(@Index Long index, @RedisValue T member);
    
    /**
     * Atomically compares and swaps the element at the specified index
     * @param index the index to modify
     * @param oldMember the expected current value
     * @param newMember the new value to set
     * @return true if the swap was successful, false otherwise
     */
    @CAS
    Boolean compareAndSwap(@Index Long index, @RedisOldValue T oldMember, @RedisValue T newMember);

    /**
     * Inserts an element at the head (left) of the list
     * @param member the element to add
     * @return the length of the list after the operation
     */
    @AddMembers
    @Left
    Long leftPush(@RedisValue T member);
    
    /**
     * Inserts an element before the pivot element in the list
     * @param pivot the reference element
     * @param member the element to add
     * @return the length of the list after the operation
     */
    @AddMembers
    @Left
    Long leftPush(@Pivot T pivot, @RedisValue T member);
    
    /**
     * Inserts multiple elements at the head (left) of the list
     * 
     * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines 
	 * whether the method continues to insert subsequent members at the head (left) of the list
	 * even if an exception occurs or some members fail.
	 * True: continue | False: break
	 * </p>
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
     * @param members collection of elements to add
     * @return map indicating success/failure for each element
     */
    @AddMembers
    @Left
    @ContinueOnFailure(true)
    Map<T,Boolean> leftPush(@Multiple Collection<T> members);
    
    /**
     * Inserts an element at the tail (right) of the list
     * @param member the element to add
     * @return the length of the list after the operation
     */
    @AddMembers
    @Right
    Long rightPush(@RedisValue T member);
    
    /**
     * Inserts an element after the pivot element in the list
     * @param pivot the reference element
     * @param member the element to add
     * @return the length of the list after the operation
     */
    @AddMembers
    @Right
    Long rightPush(@Pivot T pivot, @RedisValue T member);
    
    /**
     * Inserts multiple elements at the tail (right) of the list
     * 
     * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines 
	 * whether the method continues to insert subsequent members at the tail (right) of the list
	 * even if an exception occurs or some members fail.
	 * True: continue | False: break
	 * </p>
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
     * @param members collection of elements to add
     * @return map indicating success/failure for each element
     */
    @AddMembers
    @Right
    @ContinueOnFailure(true)
    Map<T,Boolean> rightPush(@Multiple Collection<T> members);
    
    /**
     * Gets the current size (length) of the list
     * @return the length of the list
     */
    @GetSize
    Long getSize();
    
    /**
     * Gets the first index of the specified element in the list
     * @param member the element to search for
     * @return the index of the element, or null if not found
     */
    @GetIndexs
    Long getIndex(@RedisValue T member);
    
    /**
     * Gets the first index for each specified element in the list
     * 
     * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines 
	 * whether the method continues to get subsequent members's first index in the list
	 * even if an exception occurs or some members fail.
	 * True: continue | False: break
	 * </p>
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
     * @param members collection of elements to search for
     * @return map of elements to their indices (null if not found)
     */
    @GetIndexs
    @ContinueOnFailure(true)
    Map<T, Long> getIndex(@Multiple Collection<T> members);
    
    /**
     * Gets the last index of the specified element in the list
     * @param member the element to search for
     * @return the last index of the element, or null if not found
     */
    @GetIndexs
    @Reverse
    Long getLastIndex(@RedisValue T member);
    
    /**
     * Gets the last index for each specified element in the list
     * 
     * <p>
	 * The {@code @ContinueOnFailure} annotation's value determines 
	 * whether the method continues to get subsequent members's last index in the list
	 * even if an exception occurs or some members fail.
	 * True: continue | False: break
	 * </p>
	 * 
	 * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
     * 
     * @param member collection of elements to search for
     * @return map of elements to their last indices (null if not found)
     */
    @GetIndexs
    @Reverse
    @ContinueOnFailure(true)
    Map<T, Long> getLastIndex(@Multiple Collection<T> member);
    
    /**
     * Gets the element at the specified index
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
     * @param index the index to retrieve
     * @return the element at the index
     */
    @GetMembers
    T get(@Index Long index);
    
    /**
     * Gets a range of elements from the list
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
     * @param start the starting index (inclusive)
     * @param end the ending index (inclusive)
     * @return list of elements in the specified range
     */
    @GetMembers
    List<T> getByIndexRange(@StartIndex Long start, @EndIndex Long end);
    
    /**
     * Gets a random element from the list
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
     * @return a random element
     */
    @GetMembers
    @Random
    T randomOne();
    
    /**
     * Gets multiple random elements from the list (may contain duplicates)
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
     * @param count number of random elements to get
     * @return list of random elements
     */
    @GetMembers
    @Random
    List<T> random(@Count Long count);
    
    /**
     * Gets multiple distinct random elements from the list
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
     * @param count number of distinct random elements to get
     * @return list of distinct random elements
     */
    @GetMembers
    @Random
    @Distinct
    List<T> randomAndDistinct(@Count Long count);
    
    /**
     * Removes and returns the first element from the list
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
     * @return the removed element
     */
    @PopMembers
    @Left
    T leftPop();
    
    /**
     * Removes and returns multiple elements from the head of the list
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
     * @param count number of elements to remove
     * @return list of removed elements
     */
    @PopMembers
    @Left
    List<T> leftPop(@Count Long count);

    /**
     * Blocks until an element is available to pop from the head of the list
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
	 * 
     * @param value timeout duration
     * @param unit timeout time unit
     * @return list containing the removed element (or empty if timeout)
     */
    @PopMembers
    @Left
    List<T> leftPop(@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
    
    /**
     * Removes and returns the last element from the list
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
	 * 
     * @return the removed element
     */
    @PopMembers
    @Right
    T rightPop();
    
    /**
     * Removes and returns multiple elements from the tail of the list
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
	 * 
     * @param count number of elements to remove
     * @return list of removed elements
     */
    @PopMembers
    @Right
    List<T> rightPop(@Count Long count);

    /**
     * Blocks until an element is available to pop from the tail of the list
     * 
     * <p>
	 * Developers must override this method when another interface extends this template; 
	 * otherwise, an exception will occur, 
	 * because the method's return type involves a generic argument T.
	 * </p>
     * 
     * @param value timeout duration
     * @param unit timeout time unit
     * @return list containing the removed element (or empty if timeout)
     */
    @PopMembers
    @Right
    List<T> rightPop(@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
    
    /**
     * Removes occurrences of an element from the list
     * @param member the element to remove
     * @param count number of occurrences to remove (positive=remove from head, negative=remove from tail, 0=remove all)
     * @return number of elements removed
     */
    @RemoveMembers
    Long remove(@RedisValue T member, @Count Long count);
    
    /**
     * Trims the list to contain only elements within the specified range
     * @param start the starting index (inclusive)
     * @param end the ending index (inclusive)
     */
    @RemoveMembers
    @Reverse
    void trim(@StartIndex Long start, @EndIndex Long end);
}