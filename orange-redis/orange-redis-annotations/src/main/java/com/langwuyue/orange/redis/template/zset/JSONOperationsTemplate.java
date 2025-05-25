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
package com.langwuyue.orange.redis.template.zset;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.CAS;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.Decrement;
import com.langwuyue.orange.redis.annotation.Distinct;
import com.langwuyue.orange.redis.annotation.EndIndex;
import com.langwuyue.orange.redis.annotation.GetIndexs;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.GetSize;
import com.langwuyue.orange.redis.annotation.IfAbsent;
import com.langwuyue.orange.redis.annotation.Increment;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.PopMembers;
import com.langwuyue.orange.redis.annotation.Random;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.StartIndex;
import com.langwuyue.orange.redis.annotation.TimeoutUnit;
import com.langwuyue.orange.redis.annotation.TimeoutValue;
import com.langwuyue.orange.redis.annotation.zset.GetScores;
import com.langwuyue.orange.redis.annotation.zset.MaxScore;
import com.langwuyue.orange.redis.annotation.zset.MinScore;
import com.langwuyue.orange.redis.annotation.zset.OldScore;
import com.langwuyue.orange.redis.annotation.zset.OrangeRedisZSetClient;
import com.langwuyue.orange.redis.annotation.zset.PageNo;
import com.langwuyue.orange.redis.annotation.zset.Reverse;
import com.langwuyue.orange.redis.annotation.zset.Score;
import com.langwuyue.orange.redis.annotation.zset.WithScores;
import com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate;

/**
 * Interface template for Redis Sorted Set (ZSet) operations with JSON serialization.
 * Provides comprehensive operations for managing sorted sets in Redis where values are serialized as JSON.
 * 
 * <p>Key features include:
 * <ul>
 *   <li>Element addition and score updates</li>
 *   <li>Range queries by score or rank</li>
 *   <li>Conditional operations (add-if-absent, compare-and-swap)</li> 
 *   <li>Atomic pop operations for highest/lowest scored elements</li>
 *   <li>Score manipulation (increment/decrement)</li>
 *   <li>Random element selection</li>
 * </ul>
 *
 * <p>Usage example:
 * <blockquote><pre>
 * {@code @OrangeRedisKey(
 *     expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS),
 *     key = "orange:zset:example1"
 * )}
 * public interface OrangeRedisZSetExample1Api extends JSONOperationsTemplate{@code <ZSetValue>} {
 *     // Custom methods can be added here
 * }
 * </pre></blockquote>
 *
 * @param <T> The type of value stored in the Redis ZSet (will be serialized as JSON)
 * @author Liang.Zhong
 * @since 1.0.0
 * @see com.langwuyue.orange.redis.annotation.zset.OrangeRedisZSetClient
 * @see com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate
 */
@OrangeRedisZSetClient(valueType = RedisValueTypeEnum.JSON)
public interface JSONOperationsTemplate<T> extends GlobalOperationsTemplate {

	/**
	 * Adds or updates an element in the sorted set.
	 * <p>
	 * If the element does not exist in the set, it will be added with the specified score.
	 * If the element already exists, its score will be updated to the new value.
	 *
	 * @param value the element value to add/update (will be serialized as JSON)
	 * @param score the score to associate with the element
	 * @return {@code true} if the element was added as a new member,
	 *         {@code false} if only the score was updated
	 */
	@AddMembers
	Boolean add(@RedisValue T value, @Score Double score);
	
	/**
	 * Adds or updates multiple elements in the sorted set.
	 * <p>
	 * Processes a map of elements where:
	 * <ul>
	 *   <li>Keys are the element values (serialized as JSON)</li>
	 *   <li>Values are the corresponding scores</li>
	 * </ul>
	 *
	 * <p>Behavior control:
	 * <ul>
	 *   <li>{@code @ContinueOnFailure(true)}: Continues processing after errors</li>
	 *   <li>{@code @ContinueOnFailure(false)}: Stops at first error</li>
	 * </ul>
	 *
	 * <p>Implementation note:
	 * Must be overridden by extending interfaces due to generic type constraints.
	 *
	 * @param members map of elements to add/update (key=value, value=score)
	 * @return map indicating success/failure for each element
	 */
	@AddMembers
	@ContinueOnFailure(true)
	Map<T, Boolean> add(@Multiple Map<T, Double> members);
	
	/**
	 * Adds an element to the sorted set only if it does not already exist.
	 * <p>
	 * Triggers the {@code OrangeRedisZSetAddMemberIfAbsentListener} upon completion.
	 * The listener implementation must be annotated with Spring's {@code @Component}.
	 *
	 * <p>Configuration options:
	 * <ul>
	 *   <li>{@code deleteInTheEnd=true}: Removes the element after operation</li>
	 *   <li>{@code deleteInTheEnd=false}: Keeps the element after operation</li>
	 * </ul>
	 *
	 * @param value the element value to add (serialized as JSON)
	 * @param score the score to associate with the element
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	void addIfAbsent(@RedisValue T value, @Score Double score);
	
	/**
	 * Adds multiple elements to the sorted set only if they do not already exist.
	 * <p>
	 * Behavior control:
	 * <ul>
	 *   <li>{@code @ContinueOnFailure(true)}: 
	 *     <ul>
	 *       <li>Continues processing remaining elements after errors</li>
	 *       <li>Partial failures will not interrupt the entire batch</li>
	 *       <li>Failed operations will trigger listener's onFailure callback</li>
	 *     </ul>
	 *   </li>
	 *   <li>{@code @IfAbsent(deleteInTheEnd=false)}: Keeps elements after operation</li>
	 * </ul>
	 *
	 * <p>Triggers {@code OrangeRedisZSetAddMembersIfAbsentListener} upon completion.
	 * Listener implementation must be annotated with Spring's {@code @Component}.
	 *
	 * <p>Error handling:
	 * <ul>
	 *   <li>When {@code @ContinueOnFailure=true}:
	 *     <ul>
	 *       <li>Individual failures are logged</li>
	 *       <li>Operation continues with remaining elements</li>
	 *       <li>Listener receives completion events for operation</li>
	 *     </ul>
	 *   </li>
	 *   <li>When {@code @ContinueOnFailure=false}:
	 *     <ul>
	 *       <li>First failure stops the entire batch</li>
	 *       <li>Listener receives completion events for operation</li>
	 *     </ul>
	 *   </li>
	 * </ul>
	 *
	 * @param members map of elements to add (key=value, value=score)
	 * @see com.langwuyue.orange.redis.annotation.ContinueOnFailure
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=false)
	@ContinueOnFailure(true)
	void addIfAbsent(@Multiple Map<T, Double> members);
	
	/**
	 * Atomically acquires a distributed lock by adding a value to the sorted set if it doesn't exist.
	 * <p>
	 * This method implements a Redis-based distributed lock using ZSET operations with JSON serialization.
	 * The lock is automatically released when the operation completes ({@code deleteInTheEnd=true}).
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic operation using Redis ZADD with NX option</li>
	 *   <li>Automatic lock release via {@code deleteInTheEnd=true}</li>
	 *   <li>Thread-safe for concurrent access</li>
	 *   <li>Supports JSON-serialized lock values</li>
	 * </ul>
	 *
	 * <p>Performance considerations:
	 * <ul>
	 *   <li>Time complexity: O(log(N)) where N is number of elements in set</li>
	 *   <li>Score should typically be a timestamp for fair lock acquisition</li>
	 *   <li>Serialization overhead for complex lock values</li>
	 * </ul>
	 *
	 * <p>Listener requirements:
	 * <ul>
	 *   <li>Must implement {@code OrangeRedisZSetAddMemberIfAbsentListener}</li>
	 *   <li>Must be annotated with Spring {@code @Component}</li>
	 *   <li>Listener logic should be idempotent</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // 1. Define lock interface
	 * @OrangeRedisKey("order:lock:${orderId}")
	 * public interface OrderLockService extends JSONOperationsTemplate&lt;String&gt; {
	 *     void acquireLock(String orderId, double score);
	 * }
	 *
	 * // 2. Implement listener
	 * {@code @Component}
	 * public class OrderLockListener implements OrangeRedisZSetAddMemberIfAbsentListener {
	 *     {@code @Override}
	 *     public void onSuccess(OrangeSetIfAbsentSuccessEvent event) {
	 *         // Process locked resource
	 *     }
	 *     
	 *     {@code @Override}
	 *     public void onFailure(OrangeSetIfAbsentFailedEvent event) {
	 *         // Handle lock acquisition failure
	 *     }
	 * }
	 * }</pre>
	 *
	 * @param value the value to use as lock identifier (will be JSON-serialized)
	 * @param score the score to associate with the lock (typically current timestamp)
	 * @see com.langwuyue.orange.redis.annotation.IfAbsent#deleteInTheEnd()
	 * @see <a href="https://redis.io/commands/zadd">Redis ZADD command</a>
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=true)
	void acquire(@RedisValue T value, @Score Double score);
	
	/**
	 * Atomically acquires multiple distributed locks by adding values to the sorted set if they don't exist.
	 * <p>
	 * This method implements a Redis-based batch distributed lock using ZSET operations with JSON serialization.
	 * All locks are automatically released when the operation completes ({@code deleteInTheEnd=true}).
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic batch operation using Redis pipeline</li>
	 *   <li>All-or-nothing semantics for lock acquisition</li>
	 *   <li>Automatic lock release via {@code deleteInTheEnd=true}</li>
	 *   <li>Thread-safe for concurrent access</li>
	 *   <li>Supports JSON-serialized lock values</li>
	 *   <li>Controlled failure handling via {@code @ContinueOnFailure(true)}:
	 *     <ul>
	 *       <li>Continues processing remaining locks after errors</li>
	 *       <li>Partial failures will not interrupt the entire batch</li>
	 *       <li>Failed lock acquisitions will trigger listener's onFailure callback</li>
	 *     </ul>
	 *   </li>
	 * </ul>
	 *
	 * <p>Performance considerations:
	 * <ul>
	 *   <li>Time complexity: O(M*log(N)) where M is number of elements added, N is set size</li>
	 *   <li>Reduced network overhead compared to individual operations</li>
	 *   <li>Score should typically be a timestamp for fair lock acquisition</li>
	 *   <li>Serialization overhead for complex lock values</li>
	 * </ul>
	 *
	 * <p>Listener requirements:
	 * <ul>
	 *   <li>Must implement {@code OrangeRedisZSetAddMembersIfAbsentListener}</li>
	 *   <li>Must be annotated with Spring {@code @Component}</li>
	 *   <li>Listener logic should be idempotent</li>
	 * </ul>
	 *
	 * <p>Error handling:
	 * <ul>
	 *   <li>When {@code @ContinueOnFailure=true}:
	 *     <ul>
	 *       <li>Individual lock acquisition failures are logged</li>
	 *       <li>Operation continues with remaining lock acquisitions</li>
	 *       <li>Listener receives completion events for operation</li>
	 *     </ul>
	 *   </li>
	 *   <li>When {@code @ContinueOnFailure=false}:
	 *     <ul>
	 *       <li>First failure stops the entire batch</li>
	 *       <li>Listener receives completion events for operation</li>
	 *     </ul>
	 *   </li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // 1. Define batch lock interface
	 * @OrangeRedisKey("order:batch:lock")
	 * public interface OrderBatchLockService extends JSONOperationsTemplate&lt;String&gt; {
	 *     void acquireBatchLocks(List&lt;String&gt; orderIds, double score);
	 * }
	 *
	 * // 2. Implement listener (same as single lock)
	 * {@code @Component}
	 * public class OrderLockListener implements OrangeRedisZSetAddMembersIfAbsentListener {
	 *     // ... same implementation as single lock
	 * }
	 * }</pre>
	 *
	 * @param members map of elements to add (key=value, value=score)
	 * @see com.langwuyue.orange.redis.annotation.IfAbsent#deleteInTheEnd()
	 * @see com.langwuyue.orange.redis.annotation.ContinueOnFailure
	 * @see <a href="https://redis.io/commands/zadd">Redis ZADD command</a>
	 * @see <a href="https://redis.io/topics/pipelining">Redis Pipeline</a>
	 */
	@AddMembers
	@IfAbsent(deleteInTheEnd=true)
	@ContinueOnFailure(true)
	void acquire(@Multiple Map<T,Double> members);
	
	/**
	 * Atomically compares and swaps the score of an element in the sorted set (CAS operation).
	 * <p>
	 * Compares the current score of {@code value} with {@code oldScore} and, if they match,
	 * atomically updates it to {@code newScore}. The entire operation is performed atomically
	 * at the Redis server.
	 *
	 * <p>Return value interpretation:
	 * <ul>
	 *   <li>{@code true} - the score was successfully updated</li>
	 *   <li>{@code false} - the score was not updated (current score did not match expected)</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Attempt atomic score update from 10.5 to 15.0
	 * boolean success = compareAndSwap(myValue, 10.5, 15.0);
	 * if (success) {
	 *     // Score was successfully updated
	 * } else {
	 *     // Score was changed by another process
	 * }
	 * }</pre>
	 *
	 * @param value the element whose score to update (cannot be null)
	 * @param oldScore the expected current score of the element
	 * @param newScore the new score to set if expectation matches
	 * @return true if the score was updated, false otherwise
	 */
	@CAS
	Boolean compareAndSwap(@RedisValue T value, @OldScore Double oldScore, @Score Double newScore);
	
	/**
	 * Randomly selects one element from the sorted set.
	 * <p>
	 * Returns a single randomly selected element from the set. The selection is
	 * uniformly random across all elements in the set. This corresponds to Redis'
	 * ZRANDMEMBER command behavior.
	 *
	 * <p>Implementation note:
	 * Developers must override this method when another interface extends this template,
	 * because the method's return type involves a generic argument T.
	 *
	 * @return the randomly selected element, or null if set is empty
	 *
	 * @example
	 * // Get a random player
	 * {@code Player randomPlayer = randomGetValue();}
	 *
	 * @note
	 * - Time complexity: O(1)
	 * - For random selection with score, use {@link #randomGetMember}
	 * - For multiple random selections, use {@link #randomGetValues}
	 *
	 * @see #randomGetMember
	 * @see #randomGetValues
	 */
	@GetMembers
	@Random
	T randomGetValue();
	
	/**
	 * Randomly retrieves a single element with its score from the sorted set.
	 * <p>
	 * The selection is uniformly random across all elements in the set.
	 * Returns an empty map if the set is empty.
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: the randomly selected element</li>
	 *   <li>Value: the element's score</li>
	 * </ul>
	 *
	 * <p>Implementation note:
	 * Developers must override this method when another interface extends this template,
	 * because the method's return type involves a generic argument T.
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get a random element with its score
	 * Map<MyValue, Double> randomEntry = randomGetMember();
	 * if (!randomEntry.isEmpty()) {
	 *     MyValue element = randomEntry.keySet().iterator().next();
	 *     Double score = randomEntry.values().iterator().next();
	 *     // Process the element and score
	 * }
	 * }</pre>
	 *
	 * @return a map containing the randomly selected element and its score,
	 *         or an empty map if the set is empty

	 */
	@GetMembers
	@WithScores
	@Random
	Map<T, Double> randomGetMember();
	
	/**
	 * Randomly retrieves multiple elements from the sorted set.
	 * <p>
	 * Returns a list of {@code count} randomly selected elements.
	 * The selection is uniformly random across all elements in the set.
	 * If the set contains fewer elements than requested, returns all available elements.
	 *
	 * <p>Implementation note:
	 * Developers must override this method when another interface extends this template,
	 * because the method's return type involves a generic argument T.
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get 5 random elements
	 * List<MyValue> randomElements = randomGetValues(5);
	 * for (MyValue element : randomElements) {
	 *     // Process each element
	 * }
	 * }</pre>
	 *
	 * @param count the number of elements to retrieve (must be positive)
	 * @return list of randomly selected elements (may contain fewer than requested)
	 */
	@GetMembers
	@Random
	List<T> randomGetValues(@Count Integer count);
	
	/**
	 * Randomly retrieves multiple elements with their scores from the sorted set.
	 * <p>
	 * Returns a list of randomly selected elements, each represented as a single-entry map
	 * with the element as key and its score as value. The selection is uniformly random
	 * and elements may be duplicated if the set contains fewer elements than requested.
	 *
	 * <p>Implementation note:
	 * Developers must override this method when another interface extends this template,
	 * because the method's return type involves a generic argument T.
	 *
	 * @param count number of elements to return (must be positive)
	 * @return list of {@code {value: score}} maps in random selection order,
	 *         or empty list if set is empty
	 *
	 * @example
	 * // Get 3 random players with their scores
	 * {@code List<Map<Player, Double>> randomPlayers = randomGetMembers(3);}
	 * 
	 * @note
	 * - Time complexity: O(N) where N is the number of elements returned
	 * - Elements may be duplicated if count > set size
	 * - For distinct random selection, use {@link #distinctRandomGetMembers}
	 *
	 * @see #randomGetMember
	 * @see #distinctRandomGetMembers
	 */
	@GetMembers
	@WithScores
	@Random
	List<Map<T, Double>> randomGetMembers(@Count Integer count);
	
	/**
	 * Randomly selects distinct elements from the sorted set.
	 * <p>
	 * Returns a set of randomly selected unique elements, maintaining the random selection order.
	 * If the requested count exceeds the set size, returns all elements in random order.
	 *
	 * <p>Implementation note:
	 * Developers must override this method when another interface extends this template,
	 * because the method's return type involves a generic argument T.
	 *
	 * @param count number of distinct elements to return (must be positive)
	 * @return LinkedHashSet containing randomly selected distinct elements,
	 *         preserving selection order, or empty set if input is invalid
	 *
	 * @example
	 * // Get 5 unique random players
	 * {@code Set<Player> randomPlayers = distinctRandomGetValues(5);}
	 *
	 * @note
	 * - Time complexity: O(N) where N is the number of elements in the set
	 * - Returns at most all elements in the set (when count >= set size)
	 * - For non-distinct random selection, use {@link #randomGetMembers}
	 *
	 * @see #randomGetValue
	 * @see #randomGetMembers
	 */
	@GetMembers
	@Distinct
	@Random
	Set<T> distinctRandomGetValues(@Count Integer count);
	
	/**
	 * Randomly retrieves multiple distinct elements with their scores from the sorted set.
	 * <p>
	 * Returns a map of {@code count} randomly selected distinct elements with their scores.
	 * The selection is uniformly random across all elements in the set.
	 * If the set contains fewer elements than requested, returns all available elements.
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: element value</li>
	 *   <li>Value: element score</li>
	 * </ul>
	 *
	 * <p>Implementation note:
	 * Developers must override this method when another interface extends this template,
	 * because the method's return type involves a generic argument T.
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get 3 distinct random elements with their scores
	 * Map<MyValue, Double> randomElements = distinctRandomGetMembers(3);
	 * randomElements.forEach((value, score) -> {
	 *     // Process each distinct element and its score
	 * });
	 * }</pre>
	 *
	 * @param count the number of distinct elements to retrieve (must be positive)
	 * @return map of distinct randomly selected elements to their scores 
	 *         (may contain fewer than requested)
	 */
	@GetMembers
	@WithScores
	@Distinct
	@Random
	Map<T, Double> distinctRandomGetMembers(@Count Integer count);
	
	/**
	 * Retrieves elements from the sorted set by score range.
	 * <p>
	 * Returns elements with scores between minScore (inclusive) and maxScore (inclusive),
	 * sorted by score in ascending order.
	 *
	 * <p>Special values:
	 * <ul>
	 *   <li>Use Double.NEGATIVE_INFINITY for unbounded minimum score</li>
	 *   <li>Use Double.POSITIVE_INFINITY for unbounded maximum score</li>
	 * </ul>
	 *
	 * @param maxScore maximum score (inclusive)
	 * @param minScore minimum score (inclusive)
	 * @return set of matching elements (empty if none found)
	 */
	@GetMembers
	Set<T> getByScoreRange(@MaxScore Double maxScore, @MinScore Double minScore);
	
	/**
	 * Retrieves paginated elements from the sorted set by score range.
	 * <p>
	 * Returns elements with scores between minScore (inclusive) and maxScore (inclusive),
	 * sorted by score in ascending order, with pagination support.
	 *
	 * <p>Pagination parameters:
	 * <ul>
	 *   <li>{@code pageNo}: page number (1-based)</li>
	 *   <li>{@code count}: maximum number of elements per page</li>
	 * </ul>
	 *
	 * <p>Special values:
	 * <ul>
	 *   <li>Use Double.NEGATIVE_INFINITY for unbounded minimum score</li>
	 *   <li>Use Double.POSITIVE_INFINITY for unbounded maximum score</li>
	 * </ul>
	 *
	 * @param maxScore maximum score (inclusive)
	 * @param minScore minimum score (inclusive)
	 * @param pageNo page number (0-based)
	 * @param count maximum number of elements per page
	 * @return set of matching elements for the requested page (empty if none found)
	 */
	@GetMembers
	Set<T> getByScoreRange(@MaxScore Double maxScore, @MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	/**
	 * Retrieves elements from the sorted set with ranks within the specified range.
	 * <p>
	 * This method corresponds to Redis' ZRANGE command and returns all elements
	 * with ranks between startIndex (inclusive) and endIndex (inclusive), ordered
	 * from lowest to highest score.
	 *
	 * <p>Rank specifications:
	 * <ul>
	 *   <li>Both startIndex and endIndex are inclusive bounds</li>
	 *   <li>Ranks are 0-based (0 is the element with the lowest score)</li>
	 *   <li>Negative indices count from the end (-1 is the element with the highest score)</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get top 10 elements (ranks 0-9)
	 * Set<MyValue> top10 = getByRankRange(0L, 9L);
	 * 
	 * // Get the 5 highest scoring elements
	 * Set<MyValue> topFive = getByRankRange(-1L, -5L);
	 * 
	 * // Get elements ranked 100-120
	 * Set<MyValue> midRange = getByRankRange(100L, 120L);
	 * }</pre>
	 *
	 * @param startIndex the start rank (0-based, inclusive)
	 * @param endIndex the end rank (0-based, inclusive)
	 * @return set of matching elements ordered by score (ascending),
	 *         or empty set if no elements in range
	 *
	 * @note
	 * - Time complexity: O(log(N)+M) where N is set size and M is number of elements returned
	 * - For retrieving elements with scores, use {@link #getByRankRangeWithScores}
	 * - For retrieving elements in reverse order, use {@link #reverseRankRange}
	 * - For retrieving elements by score range, use {@link #getByScoreRange}
	 *
	 * @see #getByRankRangeWithScores
	 * @see #reverseRankRange
	 * @see #getByScoreRange
	 * @see <a href="https://redis.io/commands/zrange">Redis ZRANGE command</a>
	 */
	@GetMembers
	Set<T> getByRankRange(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	/**
	 * Retrieves elements with their scores from the sorted set by score range.
	 * <p>
	 * Returns elements with scores between minScore (inclusive) and maxScore (inclusive),
	 * along with their associated scores, sorted by score in ascending order.
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: element value</li>
	 *   <li>Value: element score</li>
	 * </ul>
	 *
	 * <p>Special values:
	 * <ul>
	 *   <li>Use Double.NEGATIVE_INFINITY for unbounded minimum score</li>
	 *   <li>Use Double.POSITIVE_INFINITY for unbounded maximum score</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get all elements with scores between 10 and 100
	 * Map<MyValue, Double> results = getByScoreRangeWithScores(100.0, 10.0);
	 * }</pre>
	 *
	 * @param maxScore maximum score (inclusive)
	 * @param minScore minimum score (inclusive)
	 * @return map of elements to their scores (empty if no matches)
	 */
	@GetMembers
	@WithScores
	Map<T, Double> getByScoreRangeWithScores(@MaxScore Double maxScore, @MinScore Double minScore);
	
	/**
	 * Retrieves paginated elements with their scores from the sorted set by score range.
	 * <p>
	 * Returns elements with scores between minScore (inclusive) and maxScore (inclusive),
	 * along with their associated scores, sorted by score in ascending order, with pagination support.
	 *
	 * <p>Pagination parameters:
	 * <ul>
	 *   <li>{@code pageNo}: page number (1-based)</li>
	 *   <li>{@code count}: maximum number of elements per page</li>
	 * </ul>
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: element value</li>
	 *   <li>Value: element score</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get first page (10 items) of elements with scores between 50 and 100
	 * Map<MyValue, Double> page1 = getByScoreRangeWithScores(100.0, 50.0, 0L, 10L);
	 * }</pre>
	 *
	 * @param maxScore maximum score (inclusive)
	 * @param minScore minimum score (inclusive)
	 * @param pageNo page number (0-based)
	 * @param count maximum number of elements per page
	 * @return map of elements to their scores for the requested page (empty if no matches)
	 */
	@GetMembers
	@WithScores
	Map<T, Double> getByScoreRangeWithScores(@MaxScore Double maxScore, @MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	/**
	 * Retrieves elements with their scores from the sorted set by rank range.
	 * <p>
	 * Returns elements with ranks between startIndex (inclusive) and endIndex (inclusive),
	 * along with their associated scores, sorted by score in ascending order.
	 * Ranks are 0-based, with 0 being the element with the lowest score.
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: element value</li>
	 *   <li>Value: element score</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get top 10 elements with their scores (ranks 0-9)
	 * Map<MyValue, Double> top10 = getByRankRangeWithScores(0L, 9L);
	 * }</pre>
	 *
	 * @param startIndex the start rank (0-based, inclusive)
	 * @param endIndex the end rank (0-based, inclusive)
	 * @return map of elements to their scores (empty if no elements in range)
	 */
	@GetMembers
	@WithScores
	Map<T, Double> getByRankRangeWithScores(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	/**
	 * Retrieves elements from the sorted set by score range in reverse order.
	 * <p>
	 * Returns elements with scores between minScore (inclusive) and maxScore (inclusive),
	 * sorted by score in descending order (highest scores first).
	 *
	 * <p>Special values:
	 * <ul>
	 *   <li>Use Double.NEGATIVE_INFINITY for unbounded minimum score</li>
	 *   <li>Use Double.POSITIVE_INFINITY for unbounded maximum score</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get all elements with scores between 50 and 100, highest first
	 * Set<Order> topOrders = reverseScoreRange(100.0, 50.0);
	 * }</pre>
	 *
	 * @param maxScore maximum score (inclusive)
	 * @param minScore minimum score (inclusive)
	 * @return set of matching elements sorted in descending score order
	 */
	@GetMembers
	@Reverse
	Set<T> reverseScoreRange(@MaxScore Double maxScore, @MinScore Double minScore);
	
	/**
	 * Retrieves paginated elements from the sorted set by score range in reverse order.
	 * <p>
	 * This method corresponds to Redis' ZREVRANGEBYSCORE command with LIMIT and OFFSET options.
	 * It returns elements with scores between minScore (inclusive) and maxScore (inclusive),
	 * ordered from highest to lowest score, with pagination support.
	 *
	 * <p>Score range specifications:
	 * <ul>
	 *   <li>Both minScore and maxScore are inclusive bounds</li>
	 *   <li>Use Double.NEGATIVE_INFINITY for unbounded minimum score</li>
	 *   <li>Use Double.POSITIVE_INFINITY for unbounded maximum score</li>
	 *   <li>Elements with equal scores are ordered lexicographically</li>
	 * </ul>
	 *
	 * <p>Pagination parameters:
	 * <ul>
	 *   <li>{@code pageNo}: page number (0-based)</li>
	 *   <li>{@code count}: maximum number of elements per page</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get first page (10 items) of players with scores between 1000 and 2000, highest first
	 * Set<Player> page1 = reverseScoreRange(2000.0, 1000.0, 0L, 10L);
	 * 
	 * // Get second page
	 * Set<Player> page2 = reverseScoreRange(2000.0, 1000.0, 1L, 10L);
	 * 
	 * // Process players in descending score order
	 * for (Player player : page1) {
	 *     // Handle each player, starting with highest score
	 * }
	 * }</pre>
	 *
	 * @param maxScore maximum score bound (inclusive)
	 * @param minScore minimum score bound (inclusive)
	 * @param pageNo page number (0-based)
	 * @param count maximum number of elements per page
	 * @return LinkedHashSet containing matching elements for the requested page,
	 *         ordered by score (descending), or empty set if no elements in range
	 *
	 * @note
	 * - Time complexity: O(log(N)+M) where N is set size and M is number of elements returned
	 * - For non-paginated results, use {@link #reverseScoreRange(Double, Double)}
	 * - For ascending order, use {@link #getByScoreRange(Double, Double, Long, Long)}
	 * - For paginated results with scores, use {@link #reverseScoreRangeWithScores(Double, Double, Long, Long)}
	 *
	 * @see #reverseScoreRange(Double, Double)
	 * @see #getByScoreRange(Double, Double, Long, Long)
	 * @see #reverseScoreRangeWithScores(Double, Double, Long, Long)
	 * @see <a href="https://redis.io/commands/zrevrangebyscore">Redis ZREVRANGEBYSCORE command</a>
	 */
	@GetMembers
	@Reverse
	Set<T> reverseScoreRange(@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	/**
	 * Retrieves elements from the sorted set by reverse rank range.
	 * <p>
	 * Returns elements with ranks between startIndex (inclusive) and endIndex (inclusive),
	 * sorted by score in descending order (highest scores first). This corresponds to Redis'
	 * ZREVRANGE command behavior where rank 0 is the element with the highest score.
	 *
	 * <p>Special rank values:
	 * <ul>
	 *   <li>Negative ranks count from the highest score (e.g. -1 is last element)</li>
	 *   <li>startIndex > endIndex returns empty set</li>
	 * </ul>
	 *
	 * @param startIndex the start rank (0-based, inclusive)
	 * @param endIndex the end rank (0-based, inclusive)
	 * @return LinkedHashSet containing elements in the specified reverse rank range,
	 *         ordered from highest to lowest score
	 *
	 * @example
	 * // Get top 10 players (reverse ranks 0-9)
	 * {@code Set<Player> top10 = reverseRankRange(0L, 9L);}
	 *
	 * @note
	 * - Time complexity: O(log(N)+M) where N is set size and M is number of elements returned
	 * - For score-based reverse range queries, use {@link #reverseScoreRange}
	 * - For forward rank ranges, use {@link #getByRankRange}
	 *
	 * @see #reverseRankRangeWithScores
	 * @see #reverseScoreRange
	 * @see #getByRankRange
	 */
	@GetMembers
	@Reverse
	Set<T> reverseRankRange(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	/**
	 * Get members with their scores in the score range [{@code minScore}, {@code maxScore}] in reverse order.
	 * <p>
	 * Returns elements with scores between minScore (inclusive) and maxScore (inclusive),
	 * along with their associated scores, sorted by score in descending order (highest scores first).
	 * 
	 * @param maxScore maximum score (inclusive)
	 * @param minScore minimum score (inclusive)
	 * @return a LinkedHashMap mapping values to their scores, sorted by score in descending order
	 */
	@GetMembers
	@Reverse
	@WithScores
	Map<T,Double> reverseScoreRangeWithScores(@MaxScore Double maxScore,@MinScore Double minScore);
	
	/**
	 * Get paginated members with their scores in the score range [{@code minScore}, {@code maxScore}] in reverse order.
	 * 
	 * @param maxScore maximum score (inclusive)
	 * @param minScore minimum score (inclusive)
	 * @param pageNo page number (0-based)
	 * @param count maximum number of elements per page
	 * @return a LinkedHashMap mapping values to their scores, sorted by score in descending order
	 */
	@GetMembers
	@Reverse
	@WithScores
	Map<T,Double> reverseScoreRangeWithScores(@MaxScore Double maxScore,@MinScore Double minScore, @PageNo Long pageNo, @Count Long count);
	
	/**
	 * Retrieves elements with their scores from the sorted set by rank range in reverse order.
	 * <p>
	 * This method corresponds to Redis' ZREVRANGE command with the WITHSCORES option and returns
	 * all elements with ranks between startIndex (inclusive) and endIndex (inclusive), along
	 * with their associated scores, ordered from highest to lowest score.
	 *
	 * <p>Rank specifications:
	 * <ul>
	 *   <li>Both startIndex and endIndex are inclusive bounds</li>
	 *   <li>Ranks are 0-based (0 is the element with the highest score)</li>
	 *   <li>Negative indices count from the end (-1 is the element with the lowest score)</li>
	 *   <li>Elements with equal scores are ordered lexicographically</li>
	 * </ul>
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: element value</li>
	 *   <li>Value: element score</li>
	 *   <li>Order: descending by score</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get top 10 highest scoring players with their scores
	 * Map<Player, Double> topPlayers = reverseRankRangeWithScores(0L, 9L);
	 * 
	 * // Print leaderboard
	 * int rank = 1;
	 * for (Map.Entry<Player, Double> entry : topPlayers.entrySet()) {
	 *     System.out.printf("#%d: %s (Score: %.2f)%n",
	 *         rank++, entry.getKey().getName(), entry.getValue());
	 * }
	 * 
	 * // Get the 5 lowest scoring players with their scores
	 * Map<Player, Double> bottomFive = reverseRankRangeWithScores(-5L, -1L);
	 * }</pre>
	 *
	 * @param startIndex the start rank (0-based, inclusive)
	 * @param endIndex the end rank (0-based, inclusive)
	 * @return LinkedHashMap containing matching elements to their scores ordered by score (descending),
	 *         or empty map if no elements in range
	 *
	 * @note
	 * - Time complexity: O(log(N)+M) where N is set size and M is number of elements returned
	 * - For retrieving elements without scores, use {@link #reverseRankRange}
	 * - For retrieving elements in ascending order with scores, use {@link #getByRankRangeWithScores}
	 * - For retrieving elements by score range in reverse order with scores, use {@link #reverseScoreRangeWithScores}
	 *
	 * @see #reverseRankRange
	 * @see #getByRankRangeWithScores
	 * @see #reverseScoreRangeWithScores
	 * @see <a href="https://redis.io/commands/zrevrange">Redis ZREVRANGE command</a>
	 */
	@GetMembers
	@Reverse
	@WithScores
	Map<T,Double> reverseRankRangeWithScores(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	/**
	 * Atomically removes and returns the member with the highest score from the sorted set.
	 * <p>
	 * This operation is atomic and thread-safe, ensuring only one consumer can pop the highest scored element.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic operation using Redis ZPOPMAX semantics</li>
	 *   <li>Returns both the value and its score</li>
	 *   <li>Thread-safe for concurrent access</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get and remove the highest scored element
	 * Map<Order, Double> highestOrder = popMax();
	 * if (!highestOrder.isEmpty()) {
	 *     Order order = highestOrder.keySet().iterator().next();
	 *     Double score = highestOrder.values().iterator().next();
	 *     // Process the highest priority order
	 * }
	 * }</pre>
	 *
	 * @return map containing the popped element and its score,
	 *         or empty map if the set is empty
	 */
	@PopMembers
	@MaxScore
	Map<T,Double> popMax();
	
	/**
	 * Atomically removes and returns the member with the lowest score from the sorted set.
	 * <p>
	 * This operation is atomic and thread-safe, ensuring only one consumer can pop the lowest scored element.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic operation using Redis ZPOPMIN semantics</li>
	 *   <li>Returns both the value and its score</li>
	 *   <li>Thread-safe for concurrent access</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Get and remove the lowest scored element
	 * Map<Order, Double> lowestOrder = popMin();
	 * if (!lowestOrder.isEmpty()) {
	 *     Order order = lowestOrder.keySet().iterator().next();
	 *     Double score = lowestOrder.values().iterator().next();
	 *     // Process the lowest priority order
	 * }
	 * }</pre>
	 *
	 * @return map containing the popped element and its score,
	 *         or empty map if the set is empty
	 */
	@PopMembers
	@MinScore
	Map<T,Double> popMin();
	
	/**
	 * Atomically removes and returns multiple members with the highest scores.
	 * <p>
	 * This operation is atomic and thread-safe, removing up to {@code count} members
	 * with the highest scores from the sorted set. The returned elements are ordered
	 * from highest to lowest score.
	 *
	 * <p>Behavior notes:
	 * <ul>
	 *   <li>If count exceeds set size, returns all elements</li>
	 *   <li>Elements are removed from the set during this operation</li>
	 *   <li>Operation is atomic and thread-safe</li>
	 * </ul>
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: element value</li>
	 *   <li>Value: element score</li>
	 *   <li>Order: descending by score</li>
	 * </ul>
	 *
	 * @param count maximum number of elements to pop (must be positive)
	 * @return map of popped elements to their scores, ordered by descending score,
	 *         or empty map if set is empty
	 *
	 * @example
	 * // Pop top 3 highest scored players
	 * {@code Map<Player, Double> top3 = popMax(3);}
	 *
	 * @note
	 * - Time complexity: O(log(N)+M) where N is set size and M is count
	 * - For single element pop, use {@link #popMax()}
	 * - For lowest score pops, use {@link #popMin()} or {@link #popMin(Integer)}
	 *
	 * @see #popMax()
	 * @see #popMin()
	 * @see #popMin(Integer)
	 */
	@PopMembers
	@MaxScore
	Map<T,Double> popMax(@Count Integer count);
	
	/**
	 * Atomically removes and returns multiple members with the lowest scores.
	 * <p>
	 * This operation is atomic and thread-safe, removing up to {@code count} members
	 * with the lowest scores from the sorted set. The returned elements are ordered
	 * from lowest to highest score.
	 *
	 * <p>Behavior notes:
	 * <ul>
	 *   <li>If count exceeds set size, returns all elements</li>
	 *   <li>Elements are removed from the set during this operation</li>
	 *   <li>Operation is atomic and thread-safe</li>
	 * </ul>
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: element value</li>
	 *   <li>Value: element score</li>
	 *   <li>Order: ascending by score</li>
	 * </ul>
	 *
	 * @param count maximum number of elements to pop (must be positive)
	 * @return map of popped elements to their scores, ordered by ascending score,
	 *         or empty map if set is empty
	 *
	 * @example
	 * // Pop 3 lowest scored players
	 * {@code Map<Player, Double> bottom3 = popMin(3);}
	 *
	 * @note
	 * - Time complexity: O(log(N)+M) where N is set size and M is count
	 * - For single element pop, use {@link #popMin()}
	 * - For highest score pops, use {@link #popMax()} or {@link #popMax(Integer)}
	 *
	 * @see #popMin()
	 * @see #popMax()
	 * @see #popMax(Integer)
	 */
	@PopMembers
	@MinScore
	Map<T,Double> popMin(@Count Integer count);
	
	/**
	 * Blocks and waits to pop the member with the highest score, with timeout.
	 * <p>
	 * Atomically removes and returns the member with the highest score from the sorted set.
	 * If the set is empty, blocks until either:
	 * <ul>
	 *   <li>An element is added to the set (returns it immediately)</li>
	 *   <li>The specified timeout elapses (returns null)</li>
	 * </ul>
	 *
	 * <p>Behavior notes:
	 * <ul>
	 *   <li>Timeout of 0 means wait indefinitely</li>
	 *   <li>Negative timeout values are treated as 0</li>
	 *   <li>Operation is atomic and thread-safe</li>
	 * </ul>
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: the popped element</li>
	 *   <li>Value: the element's score</li>
	 * </ul>
	 *
	 * @param value the maximum time to wait (must be non-negative)
	 * @param unit the time unit of the timeout argument (cannot be null)
	 * @return map containing the popped element and its score,
	 *         or empty map if timeout occurs
	 *
	 * @example
	 * // Wait up to 5 seconds for a high score player
	 * {@code Map<Player, Double> topPlayer = timeLimitedPopMax(5, TimeUnit.SECONDS);}
	 *
	 * @note
	 * - Time complexity: O(log(N)) for the pop operation
	 * - For non-blocking version, use {@link #popMax()}
	 * - For lowest score version, use {@link #timeLimitedPopMin}
	 *
	 * @see #popMax()
	 * @see #timeLimitedPopMin
	 */
	@PopMembers
	@MaxScore
	Map<T,Double> timeLimitedPopMax(@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
	
	/**
	 * Blocks and waits to pop the member with the lowest score, with timeout.
	 * <p>
	 * Atomically removes and returns the member with the lowest score from the sorted set.
	 * If the set is empty, blocks until either:
	 * <ul>
	 *   <li>An element is added to the set (returns it immediately)</li>
	 *   <li>The specified timeout elapses (returns null)</li>
	 * </ul>
	 *
	 * <p>Behavior notes:
	 * <ul>
	 *   <li>Timeout of 0 means wait indefinitely</li>
	 *   <li>Negative timeout values are treated as 0</li>
	 *   <li>Operation is atomic and thread-safe</li>
	 * </ul>
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: the popped element</li>
	 *   <li>Value: the element's score</li>
	 * </ul>
	 *
	 * @param value the maximum time to wait (must be non-negative)
	 * @param unit the time unit of the timeout argument (cannot be null)
	 * @return map containing the popped element and its score,
	 *         or empty map if timeout occurs
	 *
	 * @example
	 * // Wait up to 5 seconds for a low score player
	 * {@code Map<Player, Double> bottomPlayer = timeLimitedPopMin(5, TimeUnit.SECONDS);}
	 *
	 * @note
	 * - Time complexity: O(log(N)) for the pop operation
	 * - For non-blocking version, use {@link #popMin()}
	 * - For highest score version, use {@link #timeLimitedPopMax}
	 *
	 * @see #popMin()
	 * @see #timeLimitedPopMax
	 */
	@PopMembers
	@MinScore
	Map<T,Double> timeLimitedPopMin(@TimeoutValue Long value, @TimeoutUnit TimeUnit unit);
	
	/**
	 * Returns the rank of an element in the sorted set, with scores ordered from low to high.
	 * <p>
	 * The rank (or index) is 0-based, which means:
	 * <ul>
	 *   <li>Rank 0 is the element with the lowest score</li>
	 *   <li>Elements with equal scores are ordered lexicographically</li>
	 *   <li>Returns null if the element does not exist in the set</li>
	 * </ul>
	 *
	 * <p>This method corresponds to Redis' ZRANK command.
	 *
	 * @param value the element whose rank to return (cannot be null)
	 * @return the rank of the element (0-based) or null if element does not exist
	 *
	 * @example
	 * // Get player's rank in leaderboard
	 * {@code Long rank = getRank(player);}
	 * {@code if (rank != null) {
	 *     System.out.println("Player is ranked #" + (rank + 1));
	 * }}
	 *
	 * @note
	 * - Time complexity: O(log(N)) where N is the number of elements
	 * - For reverse ranking (high to low), use {@link #reverseRank}
	 * - For multiple element ranks, use {@link #getRanks}
	 *
	 * @see #reverseRank
	 * @see #getRanks
	 * @see #getScore
	 */
	@GetIndexs
	Long getRank(@RedisValue T value);
	
	/**
	 * Returns the ranks of multiple elements in the sorted set, with scores ordered from low to high.
	 * <p>
	 * Retrieves ranks for multiple elements in a single operation. For each element:
	 * <ul>
	 *   <li>Rank is 0-based (0 = lowest score)</li>
	 *   <li>Elements with equal scores are ordered lexicographically</li>
	 *   <li>Non-existent elements are excluded from the result map</li>
	 * </ul>
	 *
	 * <p>Behavior notes:
	 * <ul>
	 *   <li>Operation continues on individual element failures ({@code @ContinueOnFailure(true)})</li>
	 *   <li>Return map maintains input set's iteration order</li>
	 *   <li>Thread-safe operation</li>
	 * </ul>
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: element value</li>
	 *   <li>Value: element's rank (0-based)</li>
	 * </ul>
	 *
	 * @param value set of elements whose ranks to return (cannot be null)
	 * @return LinkedHashMap mapping elements to their ranks,
	 *         excluding non-existent elements
	 *
	 * @example
	 * // Get ranks for multiple players
	 * {@code Set<Player> players = Set.of(player1, player2, player3);
	 * Map<Player, Long> ranks = getRanks(players);
	 * ranks.forEach((player, rank) ->
	 *     System.out.println(player.getName() + " is ranked #" + (rank + 1))
	 * );}
	 *
	 * @note
	 * - Time complexity: O(log(N)) for each element
	 * - For reverse ranking, use {@link #reverseRanks}
	 * - For single element rank, use {@link #getRank}
	 *
	 * @see #getRank
	 * @see #reverseRanks
	 * @see #getScores
	 */
	@GetIndexs
	@ContinueOnFailure(true)
	Map<T,Long> getRanks(@Multiple Set<T> value);
	
	/**
	 * Retrieves the score associated with the specified element in the sorted set.
	 * <p>
	 * This method corresponds to Redis' ZSCORE command and returns the exact score
	 * of the element if it exists in the set.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Returns null if the element does not exist in the set</li>
	 *   <li>Score is returned with full Double precision</li>
	 *   <li>Thread-safe operation</li>
	 * </ul>
	 *
	 * @param value the element whose score to retrieve (cannot be null)
	 * @return the score of the element as Double, or null if element does not exist
	 *
	 * @example
	 * // Get a player's current score
	 * {@code Double score = getScore(player);
	 * if (score != null) {
	 *     System.out.println("Player score: " + score);
	 * }}
	 *
	 * @note
	 * - Time complexity: O(1)
	 * - For retrieving scores of multiple elements, use {@link #getScores}
	 * - To update scores, use {@link #increment} or {@link #decrement}
	 *
	 * @see #getScores
	 * @see #increment
	 * @see #decrement
	 * @see #getRank
	 */
	@GetScores
	Double getScore(@RedisValue T value);
	
	/**
	 * Retrieves scores for multiple elements in the sorted set in a single operation.
	 * <p>
	 * This method efficiently retrieves scores for all specified elements, corresponding
	 * to multiple Redis ZSCORE commands executed in a pipeline.
	 *
	 * <p>Behavior notes:
	 * <ul>
	 *   <li>Non-existent elements are excluded from the result map</li>
	 *   <li>Operation continues on individual element failures ({@code @ContinueOnFailure(true)})</li>
	 *   <li>Return map maintains input set's iteration order</li>
	 *   <li>Thread-safe operation</li>
	 * </ul>
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: element value</li>
	 *   <li>Value: element score (Double)</li>
	 * </ul>
	 *
	 * @param value set of elements whose scores to retrieve (cannot be null)
	 * @return LinkedHashMap mapping existing elements to their scores,
	 *         preserving input order
	 *
	 * @example
	 * // Get scores for multiple players
	 * {@code Set<Player> players = Set.of(player1, player2, player3);
	 * Map<Player, Double> scores = getScores(players);
	 * scores.forEach((player, score) ->
	 *     System.out.println(player.getName() + ": " + score)
	 * );}
	 *
	 * @note
	 * - Time complexity: O(N) where N is number of elements requested
	 * - For single element score retrieval, use {@link #getScore}
	 * - To update scores, use {@link #increment} or {@link #decrement}
	 *
	 * @see #getScore
	 * @see #increment
	 * @see #decrement
	 * @see #getRanks
	 */
	@GetScores
	Map<T, Double> getScores(@Multiple Set<T> value);
	
	/**
	 * Returns the reverse rank of an element in the sorted set, with scores ordered from high to low.
	 * <p>
	 * The reverse rank (or index) is 0-based, which means:
	 * <ul>
	 *   <li>Rank 0 is the element with the highest score</li>
	 *   <li>Elements with equal scores are ordered lexicographically</li>
	 *   <li>Returns null if the element does not exist in the set</li>
	 * </ul>
	 *
	 * <p>This method corresponds to Redis' ZREVRANK command.
	 *
	 * @param value the element whose reverse rank to return (cannot be null)
	 * @return the reverse rank of the element (0-based) or null if element does not exist
	 *
	 * @example
	 * // Get player's reverse rank in leaderboard
	 * {@code Long revRank = reverseRank(player);}
	 * {@code if (revRank != null) {
	 *     System.out.println("Player is ranked #" + (revRank + 1) + " from top");
	 * }}
	 *
	 * @note
	 * - Time complexity: O(log(N)) where N is the number of elements
	 * - For standard ranking (low to high), use {@link #getRank}
	 * - For multiple element reverse ranks, use {@link #reverseRanks}
	 *
	 * @see #getRank
	 * @see #reverseRanks
	 * @see #reverseRankRangeWithScores
	 */
	@GetIndexs
	@Reverse
	Long reverseRank(@RedisValue T value);
	
	/**
	 * Returns the reverse ranks of multiple elements in the sorted set, with scores ordered from high to low.
	 * <p>
	 * Retrieves reverse ranks for multiple elements in a single operation. For each element:
	 * <ul>
	 *   <li>Rank is 0-based (0 = highest score)</li>
	 *   <li>Elements with equal scores are ordered lexicographically</li>
	 *   <li>Non-existent elements are excluded from the result map</li>
	 * </ul>
	 *
	 * <p>Behavior notes:
	 * <ul>
	 *   <li>Operation continues on individual element failures ({@code @ContinueOnFailure(true)})</li>
	 *   <li>Return map maintains input set's iteration order</li>
	 *   <li>Thread-safe operation</li>
	 * </ul>
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: element value</li>
	 *   <li>Value: element's reverse rank (0-based)</li>
	 * </ul>
	 *
	 * @param value set of elements whose reverse ranks to return (cannot be null)
	 * @return LinkedHashMap mapping elements to their reverse ranks,
	 *         excluding non-existent elements
	 *
	 * @example
	 * // Get reverse ranks for multiple players
	 * {@code Set<Player> players = Set.of(player1, player2, player3);
	 * Map<Player, Long> revRanks = reverseRanks(players);
	 * revRanks.forEach((player, rank) ->
	 *     System.out.println(player.getName() + " is ranked #" + (rank + 1) + " from top")
	 * );}
	 *
	 * @note
	 * - Time complexity: O(log(N)) for each element
	 * - For single element reverse rank, use {@link #reverseRank}
	 * - For standard ranking (low to high), use {@link #getRanks}
	 *
	 * @see #reverseRank
	 * @see #getRanks
	 * @see #reverseRankRange
	 */
	@GetIndexs
	@Reverse
	@ContinueOnFailure(true)
	Map<T,Long> reverseRanks(@Multiple Set<T> value);
	
	/**
	 * Returns the number of elements in the sorted set.
	 * <p>
	 * This method corresponds to Redis' ZCARD command and returns the exact
	 * cardinality (number of elements) in the sorted set.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Returns 0 for empty sets</li>
	 *   <li>Thread-safe operation</li>
	 *   <li>Constant time complexity</li>
	 * </ul>
	 *
	 * @return the number of elements in the sorted set
	 *
	 * @example
	 * // Get total number of players in leaderboard
	 * {@code Long totalPlayers = getSize();}
	 *
	 * @note
	 * - Time complexity: O(1)
	 * - For size within score range, use {@link #getSizeByScoreRange}
	 *
	 * @see #getSizeByScoreRange
	 * @see <a href="https://redis.io/commands/zcard">Redis ZCARD command</a>
	 */
	@GetSize
	Long getSize();
	
	/**
	 * Returns the number of elements in the sorted set with scores between minScore and maxScore.
	 * <p>
	 * This method corresponds to Redis' ZCOUNT command and counts elements with scores
	 * within the specified inclusive range [minScore, maxScore].
	 *
	 * <p>Special values:
	 * <ul>
	 *   <li>Use Double.NEGATIVE_INFINITY for unbounded minimum score</li>
	 *   <li>Use Double.POSITIVE_INFINITY for unbounded maximum score</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Count players with scores between 1000 and 2000
	 * Long count = getSizeByScoreRange(2000.0, 1000.0);
	 * 
	 * // Count players with scores above 1000
	 * Long highScorers = getSizeByScoreRange(Double.POSITIVE_INFINITY, 1000.0);
	 * }</pre>
	 *
	 * @param maxScore maximum score (inclusive)
	 * @param minScore minimum score (inclusive)
	 * @return the number of elements with scores in the specified range
	 * 
	 * @see #getSize()
	 * @see #getByScoreRange(Double, Double)
	 * @see <a href="https://redis.io/commands/zcount">Redis ZCOUNT command</a>
	 */
	@GetSize
	Long getSizeByScoreRange(@MaxScore Double maxScore,@MinScore Double minScore);
	
	/**
	 * Removes a single element from the sorted set by its value.
	 * <p>
	 * This method corresponds to Redis' ZREM command and atomically removes the specified
	 * element from the sorted set if it exists.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic operation</li>
	 *   <li>Thread-safe</li>
	 *   <li>Returns immediately if element doesn't exist</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Remove a player from the leaderboard
	 * boolean removed = remove(player);
	 * if (removed) {
	 *     // Player was successfully removed
	 * }
	 * }</pre>
	 *
	 * @param value the element to remove from the sorted set (cannot be null)
	 * @return true if the element was present and removed, false if it was not present
	 *
	 * @note
	 * - Time complexity: O(log(N)) where N is the number of elements
	 * - For removing multiple elements, use {@link #remove(Set)}
	 * - For removing by score range, use {@link #removeByScoreRange}
	 *
	 * @see #remove(Set)
	 * @see #removeByScoreRange
	 * @see #removeByRankRange
	 * @see <a href="https://redis.io/commands/zrem">Redis ZREM command</a>
	 */
	@RemoveMembers
	Boolean remove(@RedisValue T value);
	
	/**
	 * Removes multiple elements from the sorted set in a single operation.
	 * <p>
	 * This method corresponds to Redis' ZREM command with multiple members and atomically
	 * removes all specified elements that exist in the sorted set.
	 *
	 * <p>Behavior notes:
	 * <ul>
	 *   <li>Operation continues on individual element failures ({@code @ContinueOnFailure(true)})</li>
	 *   <li>Return map maintains input set's iteration order</li>
	 *   <li>Thread-safe operation</li>
	 * </ul>
	 *
	 * <p>Return value structure:
	 * <ul>
	 *   <li>Key: element value</li>
	 *   <li>Value: boolean indicating removal success (true) or failure (false)</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Remove multiple players from the leaderboard
	 * Set<Player> playersToRemove = Set.of(player1, player2, player3);
	 * Map<Player, Boolean> results = remove(playersToRemove);
	 * 
	 * // Check which players were successfully removed
	 * results.forEach((player, success) -> {
	 *     if (success) {
	 *         System.out.println(player.getName() + " was removed");
	 *     } else {
	 *         System.out.println(player.getName() + " was not in the set");
	 *     }
	 * });
	 * }</pre>
	 *
	 * @param values set of elements to remove from the sorted set (cannot be null)
	 * @return LinkedHashMap mapping each input element to a boolean indicating whether it was removed
	 *
	 * @note
	 * - Time complexity: O(M*log(N)) where N is set size and M is number of elements to remove
	 * - For removing a single element, use {@link #remove(Object)}
	 * - For removing by score or rank range, use {@link #removeByScoreRange} or {@link #removeByRankRange}
	 *
	 * @see #remove(Object)
	 * @see #removeByScoreRange
	 * @see #removeByRankRange
	 * @see <a href="https://redis.io/commands/zrem">Redis ZREM command</a>
	 */
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<T,Boolean> remove(@Multiple Set<T> values);
	
	/**
	 * Removes all elements from the sorted set with ranks within the specified range.
	 * <p>
	 * This method corresponds to Redis' ZREMRANGEBYRANK command and removes all elements
	 * with ranks between startIndex (inclusive) and endIndex (inclusive), where ranks
	 * are 0-based (0 is the element with the lowest score).
	 *
	 * <p>Rank specifications:
	 * <ul>
	 *   <li>Both startIndex and endIndex are inclusive bounds</li>
	 *   <li>Ranks are 0-based (0 is the element with the lowest score)</li>
	 *   <li>Negative indices count from the end (-1 is the element with the highest score)</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Remove the 10 lowest-scoring players
	 * Long removedCount = removeByRankRange(0L, 9L);
	 * 
	 * // Remove the 5 highest-scoring players
	 * Long removedTopPlayers = removeByRankRange(-1L, -5L);
	 * }</pre>
	 *
	 * @param startIndex start rank (inclusive, 0-based)
	 * @param endIndex end rank (inclusive, 0-based)
	 * @return the number of elements removed
	 *
	 * @note
	 * - Time complexity: O(log(N)+M) where N is set size and M is number of elements removed
	 * - For removing by score range, use {@link #removeByScoreRange}
	 * - For removing specific elements, use {@link #remove(Object)} or {@link #remove(Set)}
	 *
	 * @see #removeByScoreRange
	 * @see #remove(Object)
	 * @see #remove(Set)
	 * @see <a href="https://redis.io/commands/zremrangebyrank">Redis ZREMRANGEBYRANK command</a>
	 */
	@RemoveMembers
	Long removeByRankRange(@StartIndex Long startIndex, @EndIndex Long endIndex);
	
	/**
	 * Removes all elements from the sorted set with scores within the specified range.
	 * <p>
	 * This method corresponds to Redis' ZREMRANGEBYSCORE command and removes all elements
	 * with scores between minScore (inclusive) and maxScore (inclusive).
	 *
	 * <p>Score range specifications:
	 * <ul>
	 *   <li>Both minScore and maxScore are inclusive bounds</li>
	 *   <li>Use Double.NEGATIVE_INFINITY for unbounded minimum</li>
	 *   <li>Use Double.POSITIVE_INFINITY for unbounded maximum</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Remove all players with scores between 1000 and 2000
	 * Long removedCount = removeByScoreRange(2000.0, 1000.0);
	 * 
	 * // Remove all players with scores below 1000
	 * Long removedLowScorers = removeByScoreRange(999.99, Double.NEGATIVE_INFINITY);
	 * }</pre>
	 *
	 * @param maxScore maximum score bound (inclusive)
	 * @param minScore minimum score bound (inclusive)
	 * @return the number of elements removed
	 *
	 * @note
	 * - Time complexity: O(log(N)+M) where N is set size and M is number of elements removed
	 * - For removing by rank range, use {@link #removeByRankRange}
	 * - For removing specific elements, use {@link #remove(Object)} or {@link #remove(Set)}
	 *
	 * @see #removeByRankRange
	 * @see #remove(Object)
	 * @see #remove(Set)
	 * @see <a href="https://redis.io/commands/zremrangebyscore">Redis ZREMRANGEBYSCORE command</a>
	 */
	@RemoveMembers
	Long removeByScoreRange(@MaxScore Double maxScore,@MinScore Double minScore);
	
	/**
	 * Increments the score of an element in the sorted set by 1.0.
	 * <p>
	 * This method corresponds to Redis' ZINCRBY command with a fixed increment of 1.0.
	 * If the element does not exist, it is added with a score of 1.0.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic operation</li>
	 *   <li>Creates element if it doesn't exist</li>
	 *   <li>Thread-safe</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Increment player's score by 1
	 * Double newScore = increment(player);
	 * System.out.println("Player's new score: " + newScore);
	 * }</pre>
	 *
	 * @param value the element whose score to increment (cannot be null)
	 * @return the new score after incrementing
	 *
	 * @note
	 * - Time complexity: O(log(N)) where N is the number of elements
	 * - For custom increment values, use {@link #increment(Object, Double)}
	 * - For decrementing, use {@link #decrement}
	 *
	 * @see #increment(Object, Double)
	 * @see #decrement
	 * @see #getScore
	 * @see <a href="https://redis.io/commands/zincrby">Redis ZINCRBY command</a>
	 */
	@Increment
	Double increment(@RedisValue T value);
	
	/**
	 * Decrements the score of an element in the sorted set by 1.0.
	 * <p>
	 * This method corresponds to Redis' ZINCRBY command with a fixed decrement of 1.0
	 * (implemented as incrementing by -1.0). If the element does not exist,
	 * it is added with a score of -1.0.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic operation</li>
	 *   <li>Creates element if it doesn't exist</li>
	 *   <li>Thread-safe</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Decrement player's score by 1
	 * Double newScore = decrement(player);
	 * System.out.println("Player's new score: " + newScore);
	 * }</pre>
	 *
	 * @param value the element whose score to decrement (cannot be null)
	 * @return the new score after decrementing
	 *
	 * @note
	 * - Time complexity: O(log(N)) where N is the number of elements
	 * - For custom decrement values, use {@link #decrement(Object, Double)}
	 * - For incrementing, use {@link #increment}
	 *
	 * @see #decrement(Object, Double)
	 * @see #increment
	 * @see #getScore
	 * @see <a href="https://redis.io/commands/zincrby">Redis ZINCRBY command</a>
	 */
	@Decrement
	Double decrement(@RedisValue T value);
	
	/**
	 * Increments the score of an element in the sorted set by a specified amount.
	 * <p>
	 * This method corresponds to Redis' ZINCRBY command and allows incrementing
	 * an element's score by any positive or negative value. If the element does
	 * not exist, it is added with the specified score.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic operation</li>
	 *   <li>Creates element if it doesn't exist</li>
	 *   <li>Supports both positive and negative increments</li>
	 *   <li>Thread-safe</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Add 100 points to player's score
	 * Double newScore = increment(player, 100.0);
	 * 
	 * // Subtract 50 points using negative increment
	 * Double reducedScore = increment(player, -50.0);
	 * }</pre>
	 *
	 * @param value the element whose score to increment (cannot be null)
	 * @param delta the amount to increment the score by (can be positive or negative)
	 * @return the new score after incrementing
	 *
	 * @note
	 * - Time complexity: O(log(N)) where N is the number of elements
	 * - For fixed increment of 1.0, use {@link #increment(Object)}
	 * - For decrementing by custom values, use negative delta or {@link #decrement(Object, Double)}
	 *
	 * @see #increment(Object)
	 * @see #decrement(Object, Double)
	 * @see #getScore
	 * @see <a href="https://redis.io/commands/zincrby">Redis ZINCRBY command</a>
	 */
	@Increment
	Double increment(@RedisValue T value, @Score Double delta);
	
	/**
	 * Decrements the score of an element in the sorted set by a specified amount.
	 * <p>
	 * This method corresponds to Redis' ZINCRBY command with a negative increment
	 * (implemented as incrementing by -delta). If the element does not exist,
	 * it is added with a score of -delta.
	 *
	 * <p>Key characteristics:
	 * <ul>
	 *   <li>Atomic operation</li>
	 *   <li>Creates element if it doesn't exist</li>
	 *   <li>Thread-safe</li>
	 * </ul>
	 *
	 * <p>Example usage:
	 * <pre>{@code
	 * // Subtract 100 points from player's score
	 * Double newScore = decrement(player, 100.0);
	 * 
	 * // Penalize player with variable amount
	 * Double penaltyScore = decrement(player, penaltyAmount);
	 * }</pre>
	 *
	 * @param value the element whose score to decrement (cannot be null)
	 * @param delta the amount to decrement the score by (must be positive)
	 * @return the new score after decrementing
	 *
	 * @note
	 * - Time complexity: O(log(N)) where N is the number of elements
	 * - For fixed decrement of 1.0, use {@link #decrement(Object)}
	 * - For incrementing by custom values, use {@link #increment(Object, Double)}
	 *
	 * @see #decrement(Object)
	 * @see #increment(Object, Double)
	 * @see #getScore
	 * @see <a href="https://redis.io/commands/zincrby">Redis ZINCRBY command</a>
	 */
	@Decrement
	Double decrement(@RedisValue T value, @Score Double delta);
}
	