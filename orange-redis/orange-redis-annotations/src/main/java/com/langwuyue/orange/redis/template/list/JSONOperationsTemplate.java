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
 * This template provides a comprehensive API for managing Redis Lists where values
 * are automatically serialized to and deserialized from JSON format.
 * 
 * <p>Key features:
 * <ul>
 *   <li>Type-safe Redis List operations</li>
 *   <li>Automatic JSON serialization/deserialization</li>
 *   <li>Bi-directional list operations (left/right)</li>
 *   <li>Atomic operations support (CAS)</li>
 *   <li>Blocking operations with timeout</li>
 *   <li>Batch operations with failure handling</li>
 *   <li>Random element selection</li>
 *   <li>Range-based operations</li>
 * </ul>
 * 
 * <p>Usage example:
 * <pre>{@code
 * // 1. Define your value type
 * public class UserActivity {
 *     private String userId;
 *     private String action;
 *     private long timestamp;
 *     // getters, setters, etc.
 * }
 * 
 * // 2. Define your Redis List interface
 * {@code @OrangeRedisKey(
 *     expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS),
 *     key = "orange:list:user:activities"
 * )}
 * public interface UserActivityList extends JSONOperationsTemplate{@code <UserActivity>} {
 *     // Inherit all operations from template
 * }
 * 
 * // 3. Use in your service
 * {@code @Service}
 * public class UserActivityService {
 *     {@code @Autowired}
 *     private UserActivityList activityList;
 *     
 *     public void recordActivity(UserActivity activity) {
 *         // Add to the right of the list
 *         activityList.rightPush(activity);
 *     }
 *     
 *     public List<UserActivity> getRecentActivities(int count) {
 *         // Get most recent activities (from right end)
 *         return activityList.rightPop(Long.valueOf(count));
 *     }
 * }
 * }</pre>
 * 
 * <p>Implementation notes:
 * <ul>
 *   <li>Uses Redis LIST data type for storage</li>
 *   <li>Values are stored as JSON strings in Redis</li>
 *   <li>Supports all Redis List operations (LPUSH, RPUSH, LPOP, etc.)</li>
 *   <li>Thread-safe operations</li>
 *   <li>Automatic connection and error handling</li>
 * </ul>
 * 
 * @param <T> The type of elements stored in the Redis List (will be serialized as JSON)
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.annotation.list.OrangeRedisListClient
 * @see com.langwuyue.orange.redis.annotation.OrangeRedisKey
 * @see com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate
 */
@OrangeRedisListClient(valueType = RedisValueTypeEnum.JSON)
public interface JSONOperationsTemplate<T> extends GlobalOperationsTemplate {
    
    /**
     * Sets the element at the specified position in the list.
     * This operation replaces the existing element at the given index.
     * 
     * <p>Example:
     * <pre>{@code
     * UserActivity updatedActivity = new UserActivity();
     * // Set activity at index 5
     * activityList.set(5L, updatedActivity);
     * }</pre>
     * 
     * @param index the index at which to set the element (0-based)
     * @param member the element to set at the specified position
     */
    @AddMembers
    void set(@Index Long index, @RedisValue T member);
    
    /**
     * Atomically compares and swaps the element at the specified index.
     * This operation provides atomic compare-and-set functionality for list elements.
     * 
     * <p>Example:
     * <pre>{@code
     * UserActivity oldActivity = activityList.get(5L);
     * UserActivity newActivity = new UserActivity();
     * // Attempt to update only if the old value matches
     * boolean success = activityList.compareAndSwap(5L, oldActivity, newActivity);
     * if (success) {
     *     log.info("Activity updated successfully");
     * }
     * }</pre>
     * 
     * @param index the index at which to perform the CAS operation
     * @param oldMember the expected current value
     * @param newMember the new value to set if the current value matches oldMember
     * @return true if the swap was successful (current value matched oldMember), false otherwise
     */
    @CAS
    Boolean compareAndSwap(@Index Long index, @RedisOldValue T oldMember, @RedisValue T newMember);

    /**
     * Inserts an element at the head (left) of the list.
     * This operation is equivalent to the Redis LPUSH command.
     * 
     * <p>Example:
     * <pre>{@code
     * UserActivity newActivity = new UserActivity();
     * Long newSize = activityList.leftPush(newActivity);
     * log.info("List size after push: {}", newSize);
     * }</pre>
     * 
     * @param member the element to add at the head of the list
     * @return the length of the list after the push operation
     */
    @AddMembers
    @Left
    Long leftPush(@RedisValue T member);
    
    /**
     * Inserts an element before the pivot element in the list.
     * This operation is equivalent to the Redis LINSERT command with BEFORE option.
     * 
     * <p>Example:
     * <pre>{@code
     * UserActivity pivotActivity = getCurrentActivity();
     * UserActivity newActivity = new UserActivity();
     * Long newSize = activityList.leftPush(pivotActivity, newActivity);
     * if (newSize > 0) {
     *     log.info("Activity inserted successfully");
     * }
     * }</pre>
     * 
     * @param pivot the reference element before which to insert
     * @param member the element to insert
     * @return the length of the list after the insert operation, or -1 if pivot was not found
     */
    @AddMembers
    @Left
    Long leftPush(@Pivot T pivot, @RedisValue T member);
    
    /**
     * Inserts multiple elements at the head (left) of the list.
     * This operation is equivalent to a batch LPUSH command.
     * 
     * <p>The {@code @ContinueOnFailure(true)} annotation indicates that the operation
     * will continue attempting to insert remaining elements even if some insertions fail.
     * 
     * <p>Example:
     * <pre>{@code
     * List<UserActivity> activities = Arrays.asList(
     *     new UserActivity("user1", "login"),
     *     new UserActivity("user2", "logout")
     * );
     * Map<UserActivity, Boolean> results = activityList.leftPush(activities);
     * 
     * // Check results
     * results.forEach((activity, success) -> {
     *     if (!success) {
     *         log.warn("Failed to insert activity: {}", activity);
     *     }
     * });
     * }</pre>
     * 
     * @param members collection of elements to add at the head of the list
     * @return map indicating success/failure for each element
     */
    @AddMembers
    @Left
    @ContinueOnFailure(true)
    Map<T,Boolean> leftPush(@Multiple Collection<T> members);
    
    /**
     * Inserts an element at the tail (right) of the list.
     * This operation is equivalent to the Redis RPUSH command.
     * 
     * <p>Example:
     * <pre>{@code
     * UserActivity newActivity = new UserActivity();
     * Long newSize = activityList.rightPush(newActivity);
     * log.info("List size after push: {}", newSize);
     * }</pre>
     * 
     * @param member the element to add at the tail of the list
     * @return the length of the list after the push operation
     */
    @AddMembers
    @Right
    Long rightPush(@RedisValue T member);
    
    /**
     * Inserts an element after the pivot element in the list.
     * This operation is equivalent to the Redis LINSERT command with AFTER option.
     * 
     * <p>Example:
     * <pre>{@code
     * UserActivity pivotActivity = getCurrentActivity();
     * UserActivity newActivity = new UserActivity();
     * Long newSize = activityList.rightPush(pivotActivity, newActivity);
     * if (newSize > 0) {
     *     log.info("Activity inserted successfully");
     * }
     * }</pre>
     * 
     * @param pivot the reference element after which to insert
     * @param member the element to insert
     * @return the length of the list after the insert operation, or -1 if pivot was not found
     */
    @AddMembers
    @Right
    Long rightPush(@Pivot T pivot, @RedisValue T member);
    
    /**
     * Inserts multiple elements at the tail (right) of the list.
     * This operation is equivalent to a batch RPUSH command.
     * 
     * <p>The {@code @ContinueOnFailure(true)} annotation indicates that the operation
     * will continue attempting to insert remaining elements even if some insertions fail.
     * 
     * <p>Example:
     * <pre>{@code
     * List<UserActivity> activities = Arrays.asList(
     *     new UserActivity("user1", "login"),
     *     new UserActivity("user2", "logout")
     * );
     * Map<UserActivity, Boolean> results = activityList.rightPush(activities);
     * 
     * // Check results
     * results.forEach((activity, success) -> {
     *     if (!success) {
     *         log.warn("Failed to insert activity: {}", activity);
     *     }
     * });
     * }</pre>
     * 
     * @param members collection of elements to add at the tail of the list
     * @return map indicating success/failure for each element
     */
    @AddMembers
    @Right
    @ContinueOnFailure(true)
    Map<T,Boolean> rightPush(@Multiple Collection<T> members);
    
    /**
     * Gets the current size (length) of the list.
     * This operation is equivalent to the Redis LLEN command.
     * 
     * <p>Example:
     * <pre>{@code
     * Long size = activityList.getSize();
     * log.info("Current list size: {}", size);
     * }</pre>
     * 
     * @return the length of the list
     */
    @GetSize
    Long getSize();
    
    /**
     * Gets the first index of the specified element in the list.
     * This operation is equivalent to the Redis LPOS command.
     * 
     * <p>Example:
     * <pre>{@code
     * UserActivity activity = getCurrentActivity();
     * Long index = activityList.getIndex(activity);
     * if (index != null) {
     *     log.info("Activity found at index: {}", index);
     * } else {
     *     log.info("Activity not found in list");
     * }
     * }</pre>
     * 
     * @param member the element to search for
     * @return the index of the element, or null if not found
     */
    @GetIndexs
    Long getIndex(@RedisValue T member);
    
    /**
     * Gets the first index for each specified element in the list.
     * This operation is equivalent to a batch LPOS command.
     * 
     * <p>The {@code @ContinueOnFailure(true)} annotation indicates that the operation
     * will continue searching for remaining elements even if some searches fail.
     * 
     * <p>Example:
     * <pre>{@code
     * List<UserActivity> activities = Arrays.asList(activity1, activity2);
     * Map<UserActivity, Long> indices = activityList.getIndex(activities);
     * 
     * indices.forEach((activity, index) -> {
     *     if (index != null) {
     *         log.info("Activity {} found at index {}", activity, index);
     *     } else {
     *         log.info("Activity {} not found", activity);
     *     }
     * });
     * }</pre>
     * 
     * @param members collection of elements to search for
     * @return map of elements to their indices (null if not found)
     */
    @GetIndexs
    @ContinueOnFailure(true)
    Map<T, Long> getIndex(@Multiple Collection<T> members);
    
    /**
     * Gets the last index of the specified element in the list.
     * This operation is equivalent to the Redis LPOS command with reverse search.
     * 
     * <p>Example:
     * <pre>{@code
     * UserActivity activity = getCurrentActivity();
     * Long lastIndex = activityList.getLastIndex(activity);
     * if (lastIndex != null) {
     *     log.info("Activity last found at index: {}", lastIndex);
     * } else {
     *     log.info("Activity not found in list");
     * }
     * }</pre>
     * 
     * @param member the element to search for
     * @return the last index of the element, or null if not found
     */
    @GetIndexs
    @Reverse
    Long getLastIndex(@RedisValue T member);
    
    /**
     * Gets the last index for each specified element in the list.
     * This operation is equivalent to a batch LPOS command with reverse search.
     * 
     * <p>The {@code @ContinueOnFailure(true)} annotation indicates that the operation
     * will continue searching for remaining elements even if some searches fail.
     * 
     * <p>Example:
     * <pre>{@code
     * List<UserActivity> activities = Arrays.asList(activity1, activity2);
     * Map<UserActivity, Long> lastIndices = activityList.getLastIndex(activities);
     * 
     * lastIndices.forEach((activity, index) -> {
     *     if (index != null) {
     *         log.info("Activity {} last found at index {}", activity, index);
     *     } else {
     *         log.info("Activity {} not found", activity);
     *     }
     * });
     * }</pre>
     * 
     * @param member collection of elements to search for
     * @return map of elements to their last indices (null if not found)
     */
    @GetIndexs
    @Reverse
    @ContinueOnFailure(true)
    Map<T, Long> getLastIndex(@Multiple Collection<T> member);
    
    /**
     * Gets the element at the specified index.
     * This operation is equivalent to the Redis LINDEX command.
     * 
     * <p>Example:
     * <pre>{@code
     * UserActivity activity = activityList.get(5L);
     * if (activity != null) {
     *     log.info("Found activity: {}", activity);
     * }
     * }</pre>
     * 
     * @param index the index to retrieve (0-based)
     * @return the element at the index, or null if index is out of range
     */
    @GetMembers
    T get(@Index Long index);
    
    /**
     * Gets a range of elements from the list.
     * This operation is equivalent to the Redis LRANGE command.
     * 
     * <p>Example:
     * <pre>{@code
     * // Get the first 10 elements
     * List<UserActivity> activities = activityList.getByIndexRange(0L, 9L);
     * log.info("Found {} activities", activities.size());
     * }</pre>
     * 
     * @param start the starting index (inclusive, 0-based)
     * @param end the ending index (inclusive, can be negative to count from end)
     * @return list of elements in the specified range
     */
    @GetMembers
    List<T> getByIndexRange(@StartIndex Long start, @EndIndex Long end);
    
    /**
     * Gets a random element from the list.
     * This operation is equivalent to the Redis LRANDMEMBER command with count=1.
     * 
     * <p>Example:
     * <pre>{@code
     * UserActivity randomActivity = activityList.randomOne();
     * if (randomActivity != null) {
     *     log.info("Random activity: {}", randomActivity);
     * }
     * }</pre>
     * 
     * @return a random element from the list, or null if the list is empty
     */
    @GetMembers
    @Random
    T randomOne();
    
    /**
     * Gets multiple random elements from the list (may contain duplicates).
     * This operation is equivalent to the Redis LRANDMEMBER command.
     * 
     * <p>Example:
     * <pre>{@code
     * // Get 5 random activities (may include duplicates)
     * List<UserActivity> randomActivities = activityList.random(5L);
     * log.info("Random activities: {}", randomActivities);
     * }</pre>
     * 
     * @param count number of random elements to get (can be negative for duplicates)
     * @return list of random elements
     */
    @GetMembers
    @Random
    List<T> random(@Count Long count);
    
    /**
     * Gets multiple distinct random elements from the list.
     * This operation is equivalent to the Redis LRANDMEMBER command with DISTINCT option.
     * 
     * <p>Example:
     * <pre>{@code
     * // Get 5 unique random activities
     * List<UserActivity> uniqueActivities = activityList.randomAndDistinct(5L);
     * log.info("Unique random activities: {}", uniqueActivities);
     * }</pre>
     * 
     * @param count number of distinct random elements to get
     * @return list of distinct random elements
     */
    @GetMembers
    @Random
    @Distinct
    List<T> randomAndDistinct(@Count Long count);
    
    /**
     * Removes and returns the first element from the list.
     * This operation is equivalent to the Redis LPOP command.
     * 
     * <p>Example:
     * <pre>{@code
     * UserActivity firstActivity = activityList.leftPop();
     * if (firstActivity != null) {
     *     log.info("Processed activity: {}", firstActivity);
     * }
     * }</pre>
     * 
     * @return the removed element, or null if the list is empty
     */
    @PopMembers
    @Left
    T leftPop();
    
    /**
     * Removes and returns multiple elements from the head of the list.
     * This operation is equivalent to the Redis LPOP command with count.
     * 
     * <p>Example:
     * <pre>{@code
     * // Process the first 5 activities
     * List<UserActivity> activities = activityList.leftPop(5L);
     * activities.forEach(activity -> {
     *     processActivity(activity);
     * });
     * }</pre>
     * 
     * @param count number of elements to remove and return
     * @return list of removed elements
     */
    @PopMembers
    @Left
    List<T> leftPop(@Count Long count);

    /**
     * Blocks until an element is available to pop from the head of the list.
     * This operation is equivalent to the Redis BLPOP command.
     * 
     * <p>Example:
     * <pre>{@code
     * // Wait up to 30 seconds for an activity
     * List<UserActivity> activities = activityList.leftPop(30L, TimeUnit.SECONDS);
     * if (!activities.isEmpty()) {
     *     processActivity(activities.get(0));
     * } else {
     *     log.info("No activity available within timeout");
     * }
     * }</pre>
     * 
     * @param value timeout duration
     * @param unit timeout time unit
     * @return list containing the removed element (or empty if timeout)
     */
    @PopMembers
    @Left
    List<T> leftPop(@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
    
    /**
     * Removes and returns the last element from the list.
     * This operation is equivalent to the Redis RPOP command.
     * 
     * <p>Example:
     * <pre>{@code
     * UserActivity lastActivity = activityList.rightPop();
     * if (lastActivity != null) {
     *     log.info("Processed last activity: {}", lastActivity);
     * }
     * }</pre>
     * 
     * @return the removed element, or null if the list is empty
     */
    @PopMembers
    @Right
    T rightPop();
    
    /**
     * Removes and returns multiple elements from the tail of the list.
     * This operation is equivalent to the Redis RPOP command with count.
     * 
     * <p>Example:
     * <pre>{@code
     * // Process the last 5 activities
     * List<UserActivity> activities = activityList.rightPop(5L);
     * activities.forEach(activity -> {
     *     processActivity(activity);
     * });
     * }</pre>
     * 
     * @param count number of elements to remove and return
     * @return list of removed elements
     */
    @PopMembers
    @Right
    List<T> rightPop(@Count Long count);

    /**
     * Blocks until an element is available to pop from the tail of the list.
     * This operation is equivalent to the Redis BRPOP command.
     * 
     * <p>Example:
     * <pre>{@code
     * // Wait up to 30 seconds for an activity
     * List<UserActivity> activities = activityList.rightPop(30L, TimeUnit.SECONDS);
     * if (!activities.isEmpty()) {
     *     processActivity(activities.get(0));
     * } else {
     *     log.info("No activity available within timeout");
     * }
     * }</pre>
     * 
     * @param value timeout duration
     * @param unit timeout time unit
     * @return list containing the removed element (or empty if timeout)
     */
    @PopMembers
    @Right
    List<T> rightPop(@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
    
    /**
     * Removes occurrences of an element from the list.
     * This operation is equivalent to the Redis LREM command.
     * 
     * <p>Example:
     * <pre>{@code
     * UserActivity activity = getCurrentActivity();
     * // Remove all occurrences
     * Long removed = activityList.remove(activity, 0L);
     * log.info("Removed {} occurrences", removed);
     * 
     * // Remove 2 occurrences from head
     * removed = activityList.remove(activity, 2L);
     * log.info("Removed {} occurrences from head", removed);
     * 
     * // Remove 2 occurrences from tail
     * removed = activityList.remove(activity, -2L);
     * log.info("Removed {} occurrences from tail", removed);
     * }</pre>
     * 
     * @param member the element to remove
     * @param count number of occurrences to remove:
     *        <ul>
     *          <li>count &gt; 0: remove count occurrences from head to tail</li>
     *          <li>count &lt; 0: remove count occurrences from tail to head</li>
     *          <li>count = 0: remove all occurrences</li>
     *        </ul>
     * @return number of elements removed
     */
    @RemoveMembers
    Long remove(@RedisValue T member, @Count Long count);
    
    /**
     * Trims the list to contain only elements within the specified range.
     * This operation is equivalent to the Redis LTRIM command.
     * 
     * <p>Example:
     * <pre>{@code
     * // Keep only the first 1000 activities
     * activityList.trim(0L, 999L);
     * 
     * // Keep only the last 1000 activities
     * Long size = activityList.getSize();
     * activityList.trim(size - 1000L, size - 1L);
     * }</pre>
     * 
     * @param start the starting index (inclusive, 0-based)
     * @param end the ending index (inclusive, can be negative to count from end)
     */
    @RemoveMembers
    @Reverse
    void trim(@StartIndex Long start, @EndIndex Long end);
}