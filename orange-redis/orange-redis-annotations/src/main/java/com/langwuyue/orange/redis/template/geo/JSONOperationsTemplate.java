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
package com.langwuyue.orange.redis.template.geo;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.AddMembers;
import com.langwuyue.orange.redis.annotation.ContinueOnFailure;
import com.langwuyue.orange.redis.annotation.Count;
import com.langwuyue.orange.redis.annotation.GetMembers;
import com.langwuyue.orange.redis.annotation.Member;
import com.langwuyue.orange.redis.annotation.Multiple;
import com.langwuyue.orange.redis.annotation.RedisValue;
import com.langwuyue.orange.redis.annotation.RemoveMembers;
import com.langwuyue.orange.redis.annotation.geo.Distance;
import com.langwuyue.orange.redis.annotation.geo.Height;
import com.langwuyue.orange.redis.annotation.geo.Latitude;
import com.langwuyue.orange.redis.annotation.geo.Longitude;
import com.langwuyue.orange.redis.annotation.geo.OrangeRedisGeoClient;
import com.langwuyue.orange.redis.annotation.geo.SearchArgs;
import com.langwuyue.orange.redis.annotation.geo.Width;
import com.langwuyue.orange.redis.template.global.GlobalOperationsTemplate;

/**
 * A comprehensive template interface for Redis Geospatial operations with JSON serialization support.
 * This interface provides methods for storing, retrieving, and searching location-based data in Redis.
 * 
 * <p>Type Parameters:
 * <ul>
 *   <li>{@code M} - Member type, represents the entity containing location information (e.g., Store, Restaurant)</li>
 *   <li>{@code L} - Location type, represents the location data structure (e.g., Location)</li>
 * </ul>
 * 
 * <p>Key Features:
 * <ul>
 *   <li>JSON serialization for complex objects</li>
 *   <li>Radius and rectangular area searches</li>
 *   <li>Distance calculations</li>
 *   <li>Batch operations support</li>
 *   <li>Flexible search options</li>
 * </ul>
 * 
 * <p>Implementation Example:
 * <pre>{@code
 * // 1. Define your location class
 * public class StoreLocation {
 *     private String id;
 *     private String name;
 *     private String address;
 *     // getters and setters
 * }
 * 
 * // 2. Define your member class
 * public class Store {
 *     private String id;
 *     private StoreLocation location;
 *     private double longitude;
 *     private double latitude;
 *     // getters and setters
 * }
 * 
 * // 3. Create your Redis Geo interface
 * {@code @OrangeRedisKey(
 *     expirationTime = @Timeout(value = 1, unit = TimeUnit.DAYS),
 *     key = "orange:geo:stores"
 * )}
 * public interface StoreGeoOperations extends JSONOperationsTemplate<Store, StoreLocation> {
 *     // Inherit all operations from template
 * }
 * 
 * // 4. Use in your service
 * {@code @Service}
 * public class StoreService {
 *     {@code @Autowired}
 *     private StoreGeoOperations storeGeo;
 *     
 *     public void addStore(Store store) {
 *         storeGeo.add(store);
 *     }
 *     
 *     public Map<Store, Double> findNearbyStores(double lat, double lon, double radius) {
 *         return storeGeo.getMembersInRadius(lon, lat, radius);
 *     }
 *     
 *     public Map<Store, Double> findStoresInArea(double lat, double lon, double width, double height) {
 *         return storeGeo.getMembersInBox(lon, lat, width, height);
 *     }
 * }
 * }</pre>
 * 
 * <p>Performance Considerations:
 * <ul>
 *   <li>Uses Redis GEOADD and GEOSEARCH commands internally</li>
 *   <li>JSON serialization overhead for complex objects</li>
 *   <li>Efficient for proximity searches</li>
 *   <li>Optimized for real-time location queries</li>
 *   <li>Supports batch operations for better performance</li>
 * </ul>
 * 
 * <p>Common Use Cases:
 * <ul>
 *   <li>Store/restaurant locators</li>
 *   <li>Delivery service areas</li>
 *   <li>Location-based recommendations</li>
 *   <li>Geofencing applications</li>
 *   <li>Real-time vehicle tracking</li>
 * </ul>
 * 
 * <p>Implementation Notes:
 * <ul>
 *   <li>Uses WGS84 coordinate system</li>
 *   <li>Coordinates are stored as 52-bit geohashes</li>
 *   <li>Distance calculations use Haversine formula</li>
 *   <li>Supports up to 13 decimal places precision</li>
 *   <li>Thread-safe operations</li>
 * </ul>
 * 
 * @param <M> The member type containing location information
 * @param <L> The location type representing geographical coordinates
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisGeoClient
 * @see GlobalOperationsTemplate
 */
@OrangeRedisGeoClient(valueType = RedisValueTypeEnum.JSON)
public interface JSONOperationsTemplate<M,L> extends GlobalOperationsTemplate {
	
	/**
	 * Adds a location with specified coordinates to Redis geospatial index.
	 * This operation is equivalent to the Redis GEOADD command.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * StoreLocation location = new StoreLocation("store123", "Downtown Store", "123 Main St");
	 * boolean added = storeGeo.add(location, -122.27652, 37.80574);
	 * }</pre>
	 * 
	 * @param location The location object to store
	 * @param longitude The longitude coordinate (-180 to 180)
	 * @param latitude The latitude coordinate (-85.05112878 to 85.05112878)
	 * @return true if the location was successfully added
	 */
	@AddMembers
	Boolean add(
		@RedisValue L location, 
		@Longitude Double longitude, 
		@Latitude Double latitude
	);
	
	/**
	 * Adds a member object containing location information to Redis geospatial index.
	 * The member object must contain longitude and latitude information.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * Store store = new Store();
	 * store.setId("store123");
	 * store.setLocation(new StoreLocation("Downtown Store", "123 Main St"));
	 * store.setLongitude(-122.27652);
	 * store.setLatitude(37.80574);
	 * boolean added = storeGeo.add(store);
	 * }</pre>
	 * 
	 * @param member The member object containing location data
	 * @return true if the member was successfully added
	 */
	@AddMembers
	Boolean add(@Member M member);
	
	/**
	 * Adds multiple member objects containing location information to Redis geospatial index.
	 * This method supports batch operations for better performance.
	 * 
	 * <p>The {@code @ContinueOnFailure(true)} annotation indicates that the operation
	 * will continue processing remaining members even if some additions fail. This is useful
	 * for handling large batches where some failures are acceptable.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * List<Store> stores = new ArrayList<>();
	 * stores.add(new Store("store1", -122.27652, 37.80574));
	 * stores.add(new Store("store2", -122.27423, 37.80412));
	 * 
	 * Map<Store, Boolean> results = storeGeo.add(stores);
	 * for (Map.Entry<Store, Boolean> entry : results.entrySet()) {
	 *     if (!entry.getValue()) {
	 *         log.error("Failed to add store: {}", entry.getKey().getId());
	 *     }
	 * }
	 * }</pre>
	 * 
	 * <p>Note: This method must be overridden in implementing interfaces due to
	 * generic type erasure limitations.
	 * 
	 * @param members Collection of member objects to add
	 * @return Map of member objects to their add operation status (true for success)
	 */
	@AddMembers
	@ContinueOnFailure(true)
	Map<M,Boolean> add(@Multiple Collection<M> members);
	
	/**
	 * Calculates the distance between multiple locations using the Haversine formula.
	 * The distance is calculated as the sum of distances between consecutive points
	 * in the provided list.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * List<StoreLocation> route = Arrays.asList(
	 *     new StoreLocation("Store A", -122.27652, 37.80574),
	 *     new StoreLocation("Store B", -122.27423, 37.80412),
	 * );
	 * Double totalDistance = storeGeo.distance(route);
	 * System.out.printf("Total route distance: %.2f km%n", totalDistance);
	 * }</pre>
	 * 
	 * <p>Note: The size of locations List must be 2.
	 * 
	 * @param locations List of locations to calculate distances between
	 * @return The total distance in meters, or null if calculation fails
	 */
	@Distance
	Double distance(@Multiple List<L> locations);
	
	/**
	 * Retrieves member information for a single location.
	 * This operation fetches the complete member object associated with the given location.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * StoreLocation location = new StoreLocation("Downtown Store");
	 * Store store = storeGeo.getMember(location);
	 * if (store != null) {
	 *     System.out.println("Found store: " + store.getName());
	 * }
	 * }</pre>
	 * 
	 * <p>Note: This method must be overridden in implementing interfaces due to
	 * generic type erasure limitations.
	 * 
	 * @param location The location to query
	 * @return The member object associated with the location, or null if not found
	 */
	@GetMembers
	M getMember(@RedisValue L location);

	/**
	 * Retrieves member information for multiple locations.
	 * This operation efficiently fetches multiple member objects in a single request.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * List<StoreLocation> locations = Arrays.asList(
	 *     new StoreLocation("Downtown Store"),
	 *     new StoreLocation("Airport Store")
	 * );
	 * List<Store> stores = storeGeo.getMembers(locations);
	 * stores.forEach(store -> 
	 *     System.out.println("Found store: " + store.getName())
	 * );
	 * }</pre>
	 * 
	 * <p>Note: This method must be overridden in implementing interfaces due to
	 * generic type erasure limitations.
	 * 
	 * @param locations List of locations to query
	 * @return List of member objects associated with the locations
	 */
	@GetMembers
	List<M> getMembers(@Multiple List<L> locations);
	
	/**
	 * Finds members within a specified radius of a location.
	 * This operation performs a circular area search using Redis GEORADIUS.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * StoreLocation center = new StoreLocation("Downtown Store");
	 * // Search within 5 kilometers
	 * Map<Store, Double> nearbyStores = storeGeo.getMembersInRadius(center, 5000.0);
	 * nearbyStores.forEach((store, distance) -> 
	 *     System.out.printf("Store %s is %.2f meters away%n", 
	 *         store.getName(), distance)
	 * );
	 * }</pre>
	 * 
	 * <p>Note: This method must be overridden in implementing interfaces due to
	 * generic type erasure limitations.
	 * 
	 * @param location The center location
	 * @param distance The search radius in meters
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInRadius(@RedisValue L location,@Distance Double distance);
	
	/**
	 * Finds members within a specified radius of a location with result limit.
	 * This operation performs a circular area search and returns up to the specified
	 * number of results, ordered by distance.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * StoreLocation center = new StoreLocation("Downtown Store");
	 * // Search for nearest 5 stores within 5 kilometers
	 * Map<Store, Double> nearestStores = storeGeo.getMembersInRadius(
	 *     center, 5000.0, 5L
	 * );
	 * nearestStores.forEach((store, distance) -> 
	 *     System.out.printf("Store %s is %.2f meters away%n", 
	 *         store.getName(), distance)
	 * );
	 * }</pre>
	 * 
	 * <p>Note: This method must be overridden in implementing interfaces due to
	 * generic type erasure limitations.
	 * 
	 * @param location The center location
	 * @param distance The search radius in meters
	 * @param count Maximum number of results to return
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInRadius(@RedisValue L location,@Distance Double distance, @Count Long count);
	
	/**
	 * Finds members within a specified radius of given coordinates.
	 * This operation performs a circular area search using explicit coordinates.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * // Search within 5 kilometers of San Francisco
	 * Map<Store, Double> sfStores = storeGeo.getMembersInRadius(
	 *     -122.4194, 37.7749, 5000.0
	 * );
	 * sfStores.forEach((store, distance) -> 
	 *     System.out.printf("Store %s is %.2f meters away%n", 
	 *         store.getName(), distance)
	 * );
	 * }</pre>
	 * 
	 * <p>Note: This method must be overridden in implementing interfaces due to
	 * generic type erasure limitations.
	 * 
	 * @param longitude The center longitude (-180 to 180)
	 * @param latitude The center latitude (-85.05112878 to 85.05112878)
	 * @param distance The search radius in meters
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInRadius(@Longitude Double longitude, @Latitude Double latitude, @Distance Double distance);
	
	/**
	 * Finds members within a specified radius of given coordinates with result limit.
	 * This operation performs a circular area search using explicit coordinates and
	 * returns up to the specified number of results, ordered by distance.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * // Search for nearest 5 stores within 5 kilometers of San Francisco
	 * Map<Store, Double> nearestSfStores = storeGeo.getMembersInRadius(
	 *     -122.4194, 37.7749, 5000.0, 5L
	 * );
	 * nearestSfStores.forEach((store, distance) -> 
	 *     System.out.printf("Store %s is %.2f meters away%n", 
	 *         store.getName(), distance)
	 * );
	 * }</pre>
	 * 
	 * <p>Note: This method must be overridden in implementing interfaces due to
	 * generic type erasure limitations.
	 * 
	 * @param longitude The center longitude (-180 to 180)
	 * @param latitude The center latitude (-85.05112878 to 85.05112878)
	 * @param distance The search radius in meters
	 * @param count Maximum number of results to return
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInRadius(@Longitude Double longitude, @Latitude Double latitude, @Distance Double distance, @Count Long count);
	
	/**
	 * Finds members within a rectangular area defined by coordinates and dimensions.
	 * This operation performs a box-shaped area search using Redis GEOSEARCH with BOX option.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * // Search in a 2km x 1km box centered on San Francisco
	 * Map<Store, Double> boxStores = storeGeo.getMembersInBox(
	 *     -122.4194, 37.7749, 2000.0, 1000.0
	 * );
	 * boxStores.forEach((store, distance) -> 
	 *     System.out.printf("Store %s is %.2f meters away%n", 
	 *         store.getName(), distance)
	 * );
	 * }</pre>
	 * 
	 * <p>Note: This method must be overridden in implementing interfaces due to
	 * generic type erasure limitations.
	 * 
	 * @param longitude The center longitude (-180 to 180)
	 * @param latitude The center latitude (-85.05112878 to 85.05112878)
	 * @param width The width of the search area in meters
	 * @param height The height of the search area in meters
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInBox(@Longitude Double longitude, @Latitude Double latitude, @Width Double width, @Height Double height);
	
	/**
	 * Finds members within a rectangular area with result limit.
	 * This operation performs a box-shaped area search and returns up to the
	 * specified number of results, ordered by distance from the center.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * // Search for nearest 5 stores in a 2km x 1km box centered on San Francisco
	 * Map<Store, Double> nearestBoxStores = storeGeo.getMembersInBox(
	 *     -122.4194, 37.7749, 2000.0, 1000.0, 5L
	 * );
	 * nearestBoxStores.forEach((store, distance) -> 
	 *     System.out.printf("Store %s is %.2f meters away%n", 
	 *         store.getName(), distance)
	 * );
	 * }</pre>
	 * 
	 * <p>Note: This method must be overridden in implementing interfaces due to
	 * generic type erasure limitations.
	 * 
	 * @param longitude The center longitude (-180 to 180)
	 * @param latitude The center latitude (-85.05112878 to 85.05112878)
	 * @param width The width of the search area in meters
	 * @param height The height of the search area in meters
	 * @param count Maximum number of results to return
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInBox(@Longitude Double longitude, @Latitude Double latitude, @Width Double width, @Height Double height, @Count Long count);
	
	/**
	 * Finds members within a rectangular area around a location.
	 * This operation performs a box-shaped area search using a location object
	 * as the center point.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * StoreLocation center = new StoreLocation("Downtown Store");
	 * // Search in a 2km x 1km box
	 * Map<Store, Double> boxStores = storeGeo.getMembersInBox(
	 *     center, 2000.0, 1000.0
	 * );
	 * boxStores.forEach((store, distance) -> 
	 *     System.out.printf("Store %s is %.2f meters away%n", 
	 *         store.getName(), distance)
	 * );
	 * }</pre>
	 * 
	 * <p>Note: This method must be overridden in implementing interfaces due to
	 * generic type erasure limitations.
	 * 
	 * @param location The center location
	 * @param width The width of the search area in meters
	 * @param height The height of the search area in meters
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInBox(@RedisValue L location, @Width Double width, @Height Double height);
	
	/**
	 * Finds members within a rectangular area around a location with result limit.
	 * This operation performs a box-shaped area search using a location object
	 * as the center point and returns up to the specified number of results.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * StoreLocation center = new StoreLocation("Downtown Store");
	 * // Search for nearest 5 stores in a 2km x 1km box
	 * Map<Store, Double> nearestBoxStores = storeGeo.getMembersInBox(
	 *     center, 2000.0, 1000.0, 5L
	 * );
	 * nearestBoxStores.forEach((store, distance) -> 
	 *     System.out.printf("Store %s is %.2f meters away%n", 
	 *         store.getName(), distance)
	 * );
	 * }</pre>
	 * 
	 * <p>Note: This method must be overridden in implementing interfaces due to
	 * generic type erasure limitations.
	 * 
	 * @param location The center location
	 * @param width The width of the search area in meters
	 * @param height The height of the search area in meters
	 * @param count Maximum number of results to return
	 * @return Map of members to their distances from the center
	 */
	@GetMembers
	@SearchArgs
	Map<M,Double> getMembersInBox(@RedisValue L location, @Width Double width, @Height Double height, @Count Long count);
	
	/**
	 * Removes a location from the Redis geospatial index.
	 * This operation is equivalent to the Redis ZREM command on the underlying sorted set.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * StoreLocation location = new StoreLocation("Closed Store");
	 * boolean removed = storeGeo.remove(location);
	 * if (removed) {
	 *     System.out.println("Store location removed successfully");
	 * }
	 * }</pre>
	 * 
	 * @param location The location to remove
	 * @return true if the location was successfully removed
	 */
	@RemoveMembers
	Boolean remove(@RedisValue L location);
	
	/**
	 * Removes multiple locations from the Redis geospatial index.
	 * This operation is equivalent to multiple Redis ZREM commands executed atomically.
	 * 
	 * <p>The {@code @ContinueOnFailure(true)} annotation indicates that the operation
	 * will continue removing subsequent locations even if some removals fail. This is
	 * useful for handling large batches where some failures are acceptable.
	 * 
	 * <p>Example:
	 * <pre>{@code
	 * List<StoreLocation> closedStores = Arrays.asList(
	 *     new StoreLocation("Closed Store 1"),
	 *     new StoreLocation("Closed Store 2")
	 * );
	 * Map<StoreLocation, Boolean> results = storeGeo.remove(closedStores);
	 * results.forEach((location, success) -> {
	 *     if (!success) {
	 *         log.error("Failed to remove location: {}", location.getName());
	 *     }
	 * });
	 * }</pre>
	 * 
	 * <p>Note: This method must be overridden in implementing interfaces due to
	 * generic type erasure limitations.
	 * 
	 * @param locations Collection of locations to remove
	 * @return Map of locations to their remove operation status (true for success)
	 */
	@RemoveMembers
	@ContinueOnFailure(true)
	Map<L,Boolean> remove(@Multiple Collection<L> locations);
}