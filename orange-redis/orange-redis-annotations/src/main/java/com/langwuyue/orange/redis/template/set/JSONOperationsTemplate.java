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
 * Interface template for Redis Set operations with JSON serialization.
 * <p>
 * This template provides thread-safe operations for Redis Set data structure,
 * with automatic JSON serialization/deserialization of elements.
 *
 * <p>Key characteristics:
 * <ul>
 *   <li>Thread-safe: All operations are safe for concurrent use</li>
 *   <li>Non-blocking: Operations do not wait for other threads</li>
 *   <li>JSON serialization: All values are automatically serialized as JSON</li>
 *   <li>Atomic operations: Critical operations like CAS are atomic</li>
 * </ul>
 *
 * <p>Performance considerations:
 * <ul>
 *   <li>Small to medium sets (up to 10,000 elements): All operations efficient</li>
 *   <li>Large sets (over 10,000 elements): Prefer scan() over getMembers()</li>
 *   <li>Batch operations preferred for multiple elements</li>
 * </ul>
 *
 * <p>Implementation requirements:
 * <ul>
 *   <li>Child interfaces must be annotated with {@code @OrangeRedisKey}</li>
 *   <li>Must override getValue() method</li>
 *   <li>Type T must be JSON-serializable</li>
 * </ul>
 *
 * <p>Example implementation:
 * <blockquote><pre>
 * {@code @OrangeRedisKey(expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS), key = "orange:set:example1")} 
 * public interface OrangeRedisSetExample1Api extends JSONOperationsTemplate{@code<User>} {
 *  
 *      {@code @Override}
 *      User getValue();
 *
 *      // Custom operations can be added here
 * }
 * </pre></blockquote>
 *
 *
 * @param <T> The type of elements stored in the Redis Set (must be JSON-serializable)
 * @author Liang.Zhong
 * @since 1.0.0
 * @see GlobalOperationsTemplate
 * @see StringOperationsTemplate
 * @see <a href="https://redis.io/topics/data-types#sets">Redis Set documentation</a>
 */
@OrangeRedisSetClient(valueType = RedisValueTypeEnum.JSON)
public interface JSONOperationsTemplate<T> extends GlobalOperationsTemplate {
	
	/**
	 * Add a single member to the Redis Set.
	 * <p>
	 * This method corresponds to Redis' SADD command for single element addition.
	 * It adds the specified value to the set if it is not already present.
	 *
	 * <p>Behavior details:
	 * <ul>
	 *   <li>If the value is already in the set, no operation is performed</li>
	 *   <li>Redis Sets do not allow duplicate elements</li>
	 *   <li>Operation is atomic</li>
	 *   <li>Returns true if the element was added, false if it already existed</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Add a new user to active users set
	 * boolean added = template.add(newUser);
	 * if (added) {
	 *     System.out.println("New user added to set");
	 * } else {
	 *     System.out.println("User already exists in set");
	 * }
	 * }</pre>
	 *
	 * @param value the element to add to the set (must not be null)
	 * @return {@code true} if the element was added to the set,
	 *         {@code false} if the element already existed
	 *
	 * @see <a href="https://redis.io/commands/sadd">Redis SADD command</a>
	 */
	@AddMembers
	Boolean add(@RedisValue T value);
	
	/**
	 * Batch add members to the Redis Set.
	 * <p>
	 * This method corresponds to Redis' SADD command for multiple elements addition.
	 * It adds all specified values to the set, skipping any that already exist.
	 *
	 * <p>Behavior details:
	 * <ul>
	 *   <li>Returns a map indicating success/failure for each element</li>
	 *   <li>Elements are processed in the order provided</li>
	 *   <li>Operation is atomic when {@code @ContinueOnFailure} is false</li>
	 *   <li>Existing elements are skipped (not treated as errors)</li>
	 * </ul>
	 *
	 * <p>{@code @ContinueOnFailure} behavior:
	 * <ul>
	 *   <li>When true: continues processing remaining elements after an error</li>
	 *   <li>When false: stops processing at first error (default)</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Batch add users to active users set
	 * Set<User> newUsers = getNewUsers();
	 * Map<User, Boolean> results = template.add(newUsers);
	 * 
	 * // Check which users were successfully added
	 * results.forEach((user, success) -> {
	 *     if (success) {
	 *         System.out.println(user + " added successfully");
	 *     } else {
	 *         System.out.println(user + " already exists or failed to add");
	 *     }
	 * });
	 * }</pre>
	 *
	 * @param members the elements to add to the set (must not be null or contain null)
	 * @return LinkedHashMap where keys are the members and values indicate whether each 
	 *         member was successfully added (true) or already existed/failed (false)
	 *
	 * Node:
	 * - Time complexity: O(N) where N is number of elements
	 * - For single element addition, use {@link #add(Object)}
	 * - To check existence before adding, use {@link #isMember(Object)} or {@link #isMembers(Set)}
	 *
	 * @see #add(Object)
	 * @see #isMember(Object)
	 * @see #isMembers(Set)
	 * @see <a href="https://redis.io/commands/sadd">Redis SADD command</a>
	 */
	@AddMembers
	@ContinueOnFailure(true)
	Map<T, Boolean> add(@Multiple Set<T> members);
	
	/**
	 * Atomically add a member to the Redis Set only if it does not already exist.
	 * <p>
	 * This method provides thread-safe conditional add operation with listener support.
	 * It combines Redis' SADD command with existence check in a single atomic operation.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic operation - check and add happen as single transaction</li>
	 *   <li>Thread-safe - can be safely called from multiple threads</li>
	 *   <li>Non-blocking - does not wait for other operations</li>
	 *   <li>Triggers {@code OrangeRedisSetAddMembersIfAbsentListener} post-addition</li>
	 *   <li>Value retention controlled by {@code deleteInTheEnd} flag</li>
	 * </ul>
	 *
	 * <p>Listener behavior:
	 * <ul>
	 *   <li>{@code OrangeRedisSetAddMembersIfAbsentListener} is triggered after successful addition</li>
	 *   <li>Listener implementation must be a Spring {@code @Component}</li>
	 *   <li>Listener receives both added value and operation result</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Thread-safe unique item addition
	 * template.addIfAbsent(newItem);
	 * 
	 * // Listener will handle post-addition logic
	 * }</pre>
	 *
	 * @param value the member to add (must not be null)
	 *
	 * Node:
	 * - Time complexity: O(1)
	 * - For unconditional add, use {@link #add(Object)}
	 * - For batch conditional add, use {@link #addIfAbsent(Set)}
	 * - Listener must be properly configured as Spring component
	 * - {@code deleteInTheEnd=false} preserves the added value (recommended)
	 *
	 * @see #add(Object)
	 * @see #addIfAbsent(Set)
	 * @see <a href="https://redis.io/commands/sadd">Redis SADD command</a>
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	void addIfAbsent(@RedisValue T value);
	
	/**
	 * Atomically add multiple members to the Redis Set only if they do not already exist.
	 * <p>
	 * This method provides thread-safe batch conditional add operation with listener support.
	 * It combines Redis' SADD command with existence checks in a single atomic operation.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic operation - checks and adds happen as single transaction</li>
	 *   <li>Thread-safe - can be safely called from multiple threads</li>
	 *   <li>Non-blocking - does not wait for other operations</li>
	 *   <li>Triggers {@code OrangeRedisSetAddMembersIfAbsentListener} post-addition</li>
	 *   <li>Value retention controlled by {@code deleteInTheEnd} flag</li>
	 * </ul>
	 *
	 * <p>{@code @ContinueOnFailure} behavior:
	 * <ul>
	 *   <li>When true: continues processing remaining members after an error</li>
	 *   <li>When false: stops processing at first error (default)</li>
	 *   <li>Errors are logged but not propagated</li>
	 * </ul>
	 *
	 * <p>Listener behavior:
	 * <ul>
	 *   <li>Listener is triggered once after all additions complete</li>
	 *   <li>Receives all successfully added members</li>
	 *   <li>Implementation must be a Spring {@code @Component}</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Batch add new users if they don't exist
	 * Set<User> newUsers = getNewUsers();
	 * template.addIfAbsent(newUsers);
	 * 
	 * // Listener will handle post-addition logic
	 * }</pre>
	 *
	 * @param members set of members to add (must not be null or contain null)
	 *
	 * Node:
	 * - Time complexity: O(N) where N is number of members
	 * - For single element conditional add, use {@link #addIfAbsent(Object)}
	 * - For unconditional batch add, use {@link #add(Set)}
	 * - {@code deleteInTheEnd=false} preserves the added values (recommended)
	 *
	 * @see #addIfAbsent(Object)
	 * @see #add(Set)
	 * @see <a href="https://redis.io/commands/sadd">Redis SADD command</a>
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	@ContinueOnFailure(true)
	void addIfAbsent(@Multiple Set<T> members);
	
	/**
	 * Atomically compare and swap a value in the Redis Set (CAS operation).
	 * <p>
	 * This method implements the compare-and-swap pattern for atomic value replacement.
	 * It will only replace {@code oldValue} with {@code newValue} if {@code oldValue}
	 * is currently present in the set.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic operation - comparison and swap happen as single transaction</li>
	 *   <li>Thread-safe - can be safely called from multiple threads</li>
	 *   <li>Non-blocking - does not wait for other operations</li>
	 *   <li>Triggers {@code OrangeRedisSetCompareAndSwapListener} on success</li>
	 * </ul>
	 *
	 * <p>Operation details:
	 * <ul>
	 *   <li>Returns true if {@code oldValue} was found and replaced with {@code newValue}</li>
	 *   <li>Returns false if {@code oldValue} was not found in the set</li>
	 *   <li>Listener is only triggered on successful swap</li>
	 *   <li>Original set is modified only on successful swap</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Atomically update user information
	 * User oldUser = getOldUser();
	 * User newUser = getUpdatedUser();
	 * if (template.compareAndSwap(oldUser, newUser)) {
	 *     System.out.println("User updated successfully");
	 * } else {
	 *     System.out.println("User not found or modified by another thread");
	 * }
	 * }</pre>
	 *
	 * @param oldValue the expected current value (must not be null)
	 * @param newValue the new value to set (must not be null)
	 * @return true if swap was successful, false otherwise
	 *
	 * Node:
	 * - Time complexity: O(1) for the comparison and O(N) for the swap where N is set size
	 * - For simple value addition, use {@link #add(Object)}
	 * - For conditional addition, use {@link #addIfAbsent(Object)}
	 * - Listener must be properly configured as Spring component
	 *
	 * @see #add(Object)
	 * @see #addIfAbsent(Object)
	 * @see <a href="https://en.wikipedia.org/wiki/Compare-and-swap">CAS pattern</a>
	 */
	@CAS
	Boolean compareAndSwap(@RedisOldValue T oldValue, @RedisValue T newValue);
	
	/**
	 * Randomly retrieve a single member from the Redis Set without removing it.
	 * <p>
	 * This method corresponds to Redis' SRANDMEMBER command with count=1.
	 * It provides a non-destructive way to sample a random set member.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Returns null if the set is empty</li>
	 *   <li>Member selection is random but uniform</li>
	 *   <li>Original set remains unchanged</li>
	 *   <li>Non-atomic operation - set may change between call and return</li>
	 * </ul>
	 *
	 * <p>Comparison with pop():
	 * <ul>
	 *   <li>randomGetOne: leaves set unchanged (non-atomic)</li>
	 *   <li>pop: removes member (atomic)</li>
	 *   <li>Both provide uniform random selection</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get a random user for preview
	 * User randomUser = template.randomGetOne();
	 * if (randomUser != null) {
	 *     System.out.println("Previewing user: " + randomUser);
	 * } else {
	 *     System.out.println("Set is empty");
	 * }
	 * }</pre>
	 *
	 * @return randomly selected member, or null if set is empty
	 *
	 * Node:
	 * - Time complexity: O(1)
	 * - For getting with removal, use {@link #pop()}
	 * - For multiple random members, use {@link #randomGetMembers(Long)} or {@link #distinctRandomGetMembers(Long)}
	 * - Developers must override this method in child interfaces
	 *
	 * @see #pop()
	 * @see #randomGetMembers(Long)
	 * @see #distinctRandomGetMembers(Long)
	 * @see <a href="https://redis.io/commands/srandmember">Redis SRANDMEMBER command</a>
	 */
	@GetMembers
	@Random
	T randomGetOne();
	
	/**
	 * Randomly retrieve members from the Redis Set, possibly with duplicates.
	 * <p>
	 * This method corresponds to Redis' SRANDMEMBER command with count parameter.
	 * It returns randomly selected elements which may contain duplicates when
	 * the absolute value of count exceeds the set size.
	 *
	 * <p>Behavior details:
	 * <ul>
	 *   <li>When count &gt; 0: returns up to count elements (may contain duplicates if count > set size)</li>
	 *   <li>When count &lt; 0: returns exactly |count| elements (always contains duplicates if |count| > set size)</li>
	 *   <li>When count = 0: returns empty list</li>
	 *   <li>Elements are selected with uniform probability</li>
	 *   <li>Return order matches selection sequence</li>
	 *   <li>Original set remains unchanged</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get up to 5 random members (may contain duplicates)
	 * List<User> sample1 = randomGetMembers(5L);
	 * 
	 * // Get exactly 3 random members (may contain duplicates)
	 * List<Product> sample2 = randomGetMembers(-3L);
	 * }</pre>
	 *
	 * @param count number of members to return:
	 *              - positive: return up to count elements
	 *              - negative: return exactly |count| elements
	 *              - zero: return empty list
	 * @return ArrayList of randomly selected members (may contain duplicates)
	 *
	 * Node:
	 * - Time complexity: O(N) where N is absolute value of count
	 * - For distinct random elements, use {@link #distinctRandomGetMembers(Long)}
	 * - For single random element, use {@link #randomGetOne()}
	 * - For getting with removal, use {@link #pop(Long)}
	 * - Developers must override this method in child interfaces
	 *
	 * @see #distinctRandomGetMembers(Long)
	 * @see #randomGetOne()
	 * @see #pop(Long)
	 * @see <a href="https://redis.io/commands/srandmember">Redis SRANDMEMBER command</a>
	 */
	@GetMembers
	@Random
	List<T> randomGetMembers(@Count Long count);
	
	
	/**
	 * Get multiple distinct random members from the Redis Set without removing them.
	 * <p>
	 * This method provides distinct random sampling from the set, corresponding to
	 * Redis' SRANDMEMBER command with count parameter, but ensuring no duplicates.
	 *
	 * <p>Behavior details:
	 * <ul>
	 *   <li>Returns empty set if count &le; 0 or set is empty</li>
	 *   <li>Returns fewer members than requested if set has insufficient members</li>
	 *   <li>Member selection is random but uniform</li>
	 *   <li>Original set remains unchanged</li>
	 *   <li>Return order matches the random selection sequence</li>
	 *   <li>Guaranteed no duplicate members in result</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get 5 distinct random users for sampling
	 * Set<User> sampleUsers = template.distinctRandomGetMembers(5L);
	 * sampleUsers.forEach(user -> 
	 *     System.out.println("Sample user: " + user.getName()));
	 * }</pre>
	 *
	 * @param count number of distinct members to retrieve (must be positive)
	 * @return LinkedHashSet of randomly selected distinct members (may be empty)
	 *
	 * Node:
	 * - Time complexity: O(N) where N is count
	 * - For getting with possible duplicates, use {@link #randomGetMembers(Long)}
	 * - For single random member, use {@link #randomGetOne()}
	 * - For getting with removal, use {@link #pop(Long)}
	 * - Developers must override this method in child interfaces
	 *
	 * @see #randomGetMembers(Long)
	 * @see #randomGetOne()
	 * @see #pop(Long)
	 * @see <a href="https://redis.io/commands/srandmember">Redis SRANDMEMBER command</a>
	 */
	@GetMembers
	@Distinct
	@Random
	Set<T> distinctRandomGetMembers(@Count Long count);
	
	/**
	 * Get all members from the Redis Set.
	 * <p>
	 * This method corresponds to Redis' SMEMBERS command. It retrieves all elements
	 * currently stored in the set.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Returns empty set if the Redis Set is empty</li>
	 *   <li>Return order is undefined (Redis Sets are unordered)</li>
	 *   <li>Returned set is a snapshot - subsequent changes to Redis Set won't affect it</li>
	 *   <li>Non-atomic operation - set may change between call and return</li>
	 * </ul>
	 *
	 * <p>Performance considerations:
	 * <ul>
	 *   <li>Avoid using on very large sets (millions of elements)</li>
	 *   <li>For large sets, consider {@link #scan(String, Long, Long)} with pagination</li>
	 *   <li>Network transfer time grows linearly with set size</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get all active users
	 * Set<User> allUsers = template.getMembers();
	 * if (!allUsers.isEmpty()) {
	 *     System.out.println("Total active users: " + allUsers.size());
	 * } else {
	 *     System.out.println("No active users");
	 * }
	 * }</pre>
	 *
	 * @return LinkedHashSet containing all members (may be empty)
	 *
	 * Node:
	 * - Time complexity: O(N) where N is set size
	 * - For paginated access, use {@link #scan(String, Long, Long)}
	 * - For checking existence, use {@link #isMember(Object)} or {@link #isMembers(Set)}
	 * - Developers must override this method in child interfaces due to generic type T
	 *
	 * @see #scan(String, Long, Long)
	 * @see #isMember(Object)
	 * @see #isMembers(Set)
	 * @see <a href="https://redis.io/commands/smembers">Redis SMEMBERS command</a>
	 */
	@GetMembers
	Set<T> getMembers();
	
	/**
	 * Check if a member exists in the Redis Set.
	 * <p>
	 * This method corresponds to Redis' SISMEMBER command. It checks whether
	 * the specified value is a member of the set.
	 *
	 * <p>Behavior details:
	 * <ul>
	 *   <li>Returns true if the member exists in the set</li>
	 *   <li>Returns false if the member does not exist</li>
	 *   <li>Comparison is based on JSON serialized value equality</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * User admin = new User("admin", "Admin User");
	 * if (template.isMember(admin)) {
	 *     System.out.println("Admin exists in the set");
	 * } else {
	 *     System.out.println("Admin not found in the set");
	 * }
	 * }</pre>
	 *
	 * @param member the value to check for existence (must not be null)
	 * @return true if the member exists in the set, false otherwise
	 *
	 * Node:
	 * - Time complexity: O(1)
	 * - For checking multiple members at once, use {@link #isMembers(Set)}
	 * - For String values, consider using {@link StringOperationsTemplate}
	 *
	 * @see #isMembers(Set)
	 * @see StringOperationsTemplate
	 * @see <a href="https://redis.io/commands/sismember">Redis SISMEMBER command</a>
	 */
	@IsMembers
	Boolean isMember(@RedisValue T member);
	
	/**
	 * Batch check if multiple members exist in the Redis Set.
	 * <p>
	 * This method efficiently checks the existence of multiple members in a single
	 * operation, reducing network round trips compared to multiple isMember() calls.
	 * It corresponds to multiple Redis SISMEMBER commands executed in a pipeline.
	 *
	 * <p>Behavior details:
	 * <ul>
	 *   <li>Returns a map with original members as keys</li>
	 *   <li>Map values indicate existence (true) or non-existence (false)</li>
	 *   <li>Order of results may differ from input order</li>
	 *   <li>Null or empty input returns empty map</li>
	 *   <li>Comparison is based on JSON serialized value equality</li>
	 * </ul>
	 *
	 * <p>{@code @ContinueOnFailure} behavior:
	 * <ul>
	 *   <li>When true: continues checking remaining members after an error</li>
	 *   <li>When false: stops checking at first error (default)</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * Set<User> usersToCheck = getUsersToCheck();
	 * Map<User, Boolean> results = template.isMembers(usersToCheck);
	 * 
	 * results.forEach((user, exists) -> {
	 *     if (exists) {
	 *         System.out.println(user + " exists in the set");
	 *     } else {
	 *         System.out.println(user + " does not exist in the set");
	 *     }
	 * });
	 * }</pre>
	 *
	 * @param members set of values to check (may be null or empty)
	 * @return LinkedHashMap where keys are the input members and values indicate
	 *         whether each exists in the set
	 *
	 * Node:
	 * - Time complexity: O(N) where N is number of members
	 * - More efficient than multiple isMember() calls for N > 1
	 * - For String values, consider using {@link StringOperationsTemplate}
	 * - Developers must override this method in child interfaces
	 *
	 * @see #isMember(Object)
	 * @see StringOperationsTemplate
	 * @see <a href="https://redis.io/commands/sismember">Redis SISMEMBER command</a>
	 */
	@IsMembers
	Map<T, Boolean> isMembers(@Multiple Set<T> members);
	
	/**
	 * Atomically remove and return a random member from the Redis Set.
	 * <p>
	 * This method corresponds to Redis' SPOP command with count=1. It performs
	 * an atomic remove-and-return operation on a random set member.
	 *
	 * <p>Behavior details:
	 * <ul>
	 *   <li>Operation is atomic - either fully succeeds or fails</li>
	 *   <li>Returns null if the set is empty</li>
	 *   <li>Member selection is random but uniform</li>
	 *   <li>Removes the returned member from the set</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get and remove a random member
	 * User randomUser = template.pop();
	 * if (randomUser != null) {
	 *     System.out.println("Processed user: " + randomUser);
	 * } else {
	 *     System.out.println("Set is empty");
	 * }
	 * }</pre>
	 *
	 * @return the removed member, or null if the set is empty
	 *
	 * Node:
	 * - Time complexity: O(1)
	 * - For removing multiple members at once, use {@link #pop(Long)}
	 * - To get without removing, use {@link #randomGetOne()}
	 *
	 * @see #pop(Long)
	 * @see #randomGetOne()
	 * @see <a href="https://redis.io/commands/spop">Redis SPOP command</a>
	 */
	@PopMembers
	T pop();
	
	/**
	 * Atomically remove and return multiple random members from the Redis Set.
	 * <p>
	 * This method corresponds to Redis' SPOP command with count > 1. It performs
	 * an atomic remove-and-return operation on multiple random set members.
	 *
	 * <p>Behavior details:
	 * <ul>
	 *   <li>Operation is atomic - either fully succeeds or fails</li>
	 *   <li>Returns empty set if input count &le; 0 or set is empty</li>
	 *   <li>Returns fewer members than requested if set has insufficient members</li>
	 *   <li>Member selection is random but uniform</li>
	 *   <li>Removes all returned members from the set</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get and remove 5 random members
	 * Set<User> randomUsers = template.pop(5L);
	 * if (!randomUsers.isEmpty()) {
	 *     System.out.println("Processed " + randomUsers.size() + " users");
	 * } else {
	 *     System.out.println("No users removed (set may be empty)");
	 * }
	 * }</pre>
	 *
	 * @param count number of members to pop (must be positive)
	 * @return LinkedHashSet of removed members (may be empty)
	 *
	 * Node:
	 * - Time complexity: O(N) where N is count
	 * - More efficient than multiple pop() calls for N > 1
	 *
	 * @see #pop()
	 * @see <a href="https://redis.io/commands/spop">Redis SPOP command</a>
	 */
	@PopMembers
	Set<T> pop(@Count Long count);
	
	/**
	 * Get the current size (cardinality) of the Redis Set.
	 * <p>
	 * This method corresponds to Redis' SCARD command. It returns the number
	 * of elements currently stored in the set.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Returns 0 if the Redis Set is empty or doesn't exist</li>
	 *   <li>Non-atomic operation - size may change immediately after call</li>
	 *   <li>Thread-safe - can be safely called from multiple threads</li>
	 *   <li>Lightweight operation - doesn't transfer set elements</li>
	 * </ul>
	 *
	 * <p>Performance considerations:
	 * <ul>
	 *   <li>Very efficient even for large sets (constant time operation)</li>
	 *   <li>Preferred over {@link #getMembers()}.size() for size checks</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Check if set has reached capacity
	 * if (template.getSize() >= MAX_CAPACITY) {
	 *     System.out.println("Set is full");
	 * } else {
	 *     System.out.println("Available slots: " + (MAX_CAPACITY - template.getSize()));
	 * }
	 * }</pre>
	 *
	 * @return the current size of the set (0 if empty or non-existent)
	 *
	 * Node:
	 * - Time complexity: O(1)
	 * - For checking existence of specific elements, use {@link #isMember(Object)} or {@link #isMembers(Set)}
	 * - For getting actual elements, use {@link #getMembers()} or {@link #scan(String, Long, Long)}
	 *
	 * @see #isMember(Object)
	 * @see #isMembers(Set)
	 * @see #getMembers()
	 * @see <a href="https://redis.io/commands/scard">Redis SCARD command</a>
	 */
	@GetSize
	Long getSize();
	
	/**
	 * Remove a single member from the Redis Set.
	 * <p>
	 * This method corresponds to Redis' SREM command for single element removal.
	 * It removes the specified value from the set if it exists.
	 *
	 * <p>Behavior details:
	 * <ul>
	 *   <li>Returns true if the member existed and was removed</li>
	 *   <li>Returns false if the member did not exist in the set</li>
	 *   <li>Operation is atomic</li>
	 *   <li>Comparison is based on JSON serialized value equality</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * User inactiveUser = getInactiveUser();
	 * boolean removed = template.remove(inactiveUser);
	 * if (removed) {
	 *     System.out.println("User removed from set");
	 * } else {
	 *     System.out.println("User was not in set");
	 * }
	 * }</pre>
	 *
	 * @param value the element to remove from the set (must not be null)
	 * @return true if the member was removed, false if it did not exist
	 *
	 * Node:
	 * - Time complexity: O(1)
	 * - For removing multiple members at once, use {@link #remove(Set)}
	 * - For String values, consider using {@link StringOperationsTemplate}
	 *
	 * @see #remove(Set)
	 * @see StringOperationsTemplate
	 * @see <a href="https://redis.io/commands/srem">Redis SREM command</a>
	 */
	@RemoveMembers
	Boolean remove(@RedisValue T value);
	
	/**
	 * Batch remove members from the Redis Set with detailed results.
	 * <p>
	 * This method corresponds to Redis' SREM command for multiple elements removal,
	 * but provides per-element success/failure status instead of just a count.
	 *
	 * <p>Behavior details:
	 * <ul>
	 *   <li>Returns a map with original members as keys</li>
	 *   <li>Map values indicate success (true) or failure (false) for each removal</li>
	 *   <li>Non-existent members are considered successful removals (false)</li>
	 *   <li>Order of results matches input order</li>
	 *   <li>Null or empty input returns empty map</li>
	 * </ul>
	 *
	 * <p>{@code @ContinueOnFailure} behavior:
	 * <ul>
	 *   <li>When true: continues processing remaining members after an error</li>
	 *   <li>When false: stops processing at first error (default)</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * Set<User> inactiveUsers = getInactiveUsers();
	 * Map<User, Boolean> results = template.remove(inactiveUsers);
	 * 
	 * results.forEach((user, success) -> {
	 *     if (success) {
	 *         System.out.println(user + " removed successfully");
	 *     } else {
	 *         System.out.println(user + " was not in set");
	 *     }
	 * });
	 * }</pre>
	 *
	 * @param values set of elements to remove (may be null or empty)
	 * @return LinkedHashMap where keys are input members and values indicate
	 *         whether each was successfully removed (true) or didn't exist (false)
	 *
	 * Node:
	 * - Time complexity: O(N) where N is number of members
	 * - For single element removal, use {@link #remove(Object)}
	 * - For simple count of removed elements, use Redis native SREM command
	 *
	 * @see #remove(Object)
	 * @see <a href="https://redis.io/commands/srem">Redis SREM command</a>
	 */
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<T,Boolean> remove(@Multiple Set<T> values);
	
	/**
	 * Paginated scan of Redis Set members matching a glob-style pattern.
	 * <p>
	 * This method provides efficient iteration over large sets using Redis' SCAN semantics,
	 * with support for pattern matching and pagination.
	 *
	 * <p>Pattern matching details:
	 * <ul>
	 *   <li>* - matches any sequence of characters</li>
	 *   <li>? - matches any single character</li>
	 *   <li>[abc] - matches any one of the enclosed characters</li>
	 *   <li>[a-z] - matches any character in the specified range</li>
	 *   <li>\ - escapes special characters</li>
	 * </ul>
	 *
	 * <p>Pagination behavior:
	 * <ul>
	 *   <li>count - hint for number of elements per page (actual may vary)</li>
	 *   <li>pageNo - 1-based page number</li>
	 *   <li>Returns empty set when page exceeds available results</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // First page of users starting with "admin"
	 * Set<User> admins = scan("admin*", 20L, 0L);
	 *
	 * // Second page of products matching pattern
	 * Set<Product> page2 = scan("prod_2023-??-*", 50L, 1L);
	 * }</pre>
	 *
	 * <p>Important notes:
	 * <ul>
	 *   <li>Scanning is not atomic - set may change during iteration</li>
	 *   <li>No guarantees about completeness when set is modified during scan</li>
	 *   <li>Pattern matching is case-sensitive</li>
	 *   <li>Total count of matches is not returned</li>
	 *   <li>Performance degrades with complex patterns on large sets</li>
	 * </ul>
	 *
	 * @param pattern glob-style pattern to match (required)
	 * @param count approximate number of elements per page
	 * @param pageNo 1-based page number
	 * @return LinkedHashSet of matching members (maintaining scan order)
	 *
	 * Node:
	 * - Time complexity: O(N) where N is set size
	 * - For small sets, consider {@link #getMembers()} with client-side filtering
	 * - For simple existence checks, use {@link #isMember(Object)}
	 *
	 * @see #getMembers()
	 * @see #isMember(Object)
	 * @see <a href="https://redis.io/commands/scan">Redis SCAN command</a>
	 */
	@GetMembers
	Set<T> scan(@ScanPattern String pattern, @Count Long count, @PageNo Long pageNo);
}