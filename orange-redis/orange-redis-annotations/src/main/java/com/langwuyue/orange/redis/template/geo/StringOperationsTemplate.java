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

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.geo.OrangeRedisGeoClient;

/**
 * A specialized template interface for Redis Geospatial operations using String identifiers.
 * This interface extends {@link JSONOperationsTemplate} but optimizes for cases where
 * locations are identified by simple string identifiers rather than complex location objects.
 * 
 * <p>Key Features:
 * <ul>
 *   <li>String-based location identifiers</li>
 *   <li>Reduced memory usage</li>
 *   <li>Better performance</li>
 *   <li>Simpler implementation</li>
 *   <li>Direct Redis string type storage</li>
 * </ul>
 * 
 * <p>Performance Advantages:
 * <ul>
 *   <li>No JSON serialization/deserialization overhead for location identifiers</li>
 *   <li>Reduced memory footprint compared to JSON storage</li>
 *   <li>Faster string comparison operations</li>
 *   <li>More efficient Redis memory usage</li>
 *   <li>Better cache utilization</li>
 * </ul>
 * 
 * <p>Implementation Example:
 * <pre>{@code
 * // 1. Define your member class (e.g., for vehicle tracking)
 * public class Vehicle {
 *     private String id;          // Vehicle identifier
 *     private String plateNumber; // License plate
 *     private VehicleType type;   // Vehicle type
 *     private double longitude;   // Current longitude
 *     private double latitude;    // Current latitude
 *     // getters and setters
 * }
 * 
 * // 2. Create your Redis Geo interface
 * {@code @OrangeRedisKey(
 *     expirationTime = @Timeout(value = 1, unit = TimeUnit.HOURS),
 *     key = "orange:geo:vehicles"
 * )}
 * public interface VehicleTracker extends StringOperationsTemplate{@code <Vehicle>} {
 *     // Inherit all operations from template
 * }
 * 
 * // 3. Use in your service
 * {@code @Service}
 * public class VehicleTrackingService {
 *     {@code @Autowired}
 *     private VehicleTracker tracker;
 *     
 *     public void updateVehicleLocation(Vehicle vehicle) {
 *         // Use vehicle ID as location identifier
 *         tracker.add(vehicle.getId(), vehicle.getLongitude(), vehicle.getLatitude());
 *     }
 *     
 *     public Map<Vehicle, Double> findNearbyVehicles(String vehicleId, double radius) {
 *         // Search using vehicle ID
 *         return tracker.getMembersInRadius(vehicleId, radius);
 *     }
 *     
 *     public Map<Vehicle, Double> findVehiclesInArea(
 *             double longitude, double latitude,
 *             double width, double height) {
 *         return tracker.getMembersInBox(longitude, latitude, width, height);
 *     }
 * }
 * }</pre>
 * 
 * <p>Common Use Cases:
 * <ul>
 *   <li>Vehicle tracking systems</li>
 *   <li>User location services</li>
 *   <li>Asset tracking</li>
 *   <li>Delivery management</li>
 *   <li>IoT device location tracking</li>
 * </ul>
 * 
 * <p>Best Practices:
 * <ul>
 *   <li>Use meaningful string identifiers (e.g., "vehicle:123" instead of just "123")</li>
 *   <li>Keep string identifiers reasonably short to minimize memory usage</li>
 *   <li>Consider using compound keys for better organization (e.g., "type:id")</li>
 *   <li>Implement a consistent identifier format across your application</li>
 *   <li>Document your identifier format for maintainability</li>
 * </ul>
 * 
 * <p>Implementation Notes:
 * <ul>
 *   <li>Location identifiers are stored as Redis strings</li>
 *   <li>All inherited methods use string identifiers for location parameters</li>
 *   <li>String comparison is used for location matching</li>
 *   <li>Thread-safe operations are guaranteed</li>
 * </ul>
 * 
 * <p>When to Use:
 * <ul>
 *   <li>When locations are identified by simple strings (IDs, codes, etc.)</li>
 *   <li>When performance is critical</li>
 *   <li>When memory usage needs to be optimized</li>
 *   <li>When location objects are simple identifiers</li>
 *   <li>When working with large numbers of locations</li>
 * </ul>
 * 
 * @param <M> The member type containing location information
 * @author Liang.Zhong
 * @since 1.0.0
 * @see JSONOperationsTemplate
 * @see OrangeRedisGeoClient
 */
@OrangeRedisGeoClient(valueType = RedisValueTypeEnum.STRING)
public interface StringOperationsTemplate<M> extends JSONOperationsTemplate<M,String> {
    // Inherits all methods from JSONOperationsTemplate
    // Uses String type for location identifiers
}