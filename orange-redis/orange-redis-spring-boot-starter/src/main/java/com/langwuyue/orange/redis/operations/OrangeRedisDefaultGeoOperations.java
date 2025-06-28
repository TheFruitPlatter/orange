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
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoSearchCommandArgs;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoSearchStoreCommandArgs;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.BoundingBox;
import org.springframework.data.redis.domain.geo.GeoReference;

import com.langwuyue.orange.redis.GeoDistanceUnitEnum;
import com.langwuyue.orange.redis.OrangeGeoDistanceMapSpringMetricEnum;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.configuration.OrangeRedisSerializer;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;

/**
 * Default implementation of {@link OrangeRedisGeoOperations} for Redis geo operations.
 * 
 * <p>This class provides thread-safe implementations of all Redis geo operations,
 * including adding locations, calculating distances, and searching within radius or bounding boxes.
 * It handles serialization/deserialization of keys and values transparently using the configured
 * {@link OrangeRedisSerializer}.
 * 
 * <p>All operations are logged at debug level when debug logging is enabled.
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see OrangeRedisGeoOperations
 * @see GeoOperations
 * @see OrangeRedisSerializer
 */
public class OrangeRedisDefaultGeoOperations extends OrangeRedisAbstractOperations implements OrangeRedisGeoOperations{
	
	/**
	 * The Spring Data Redis GeoOperations instance used to perform the actual Redis operations.
	 * This is the core component that executes the Redis geo commands.
	 */
	private GeoOperations<String,byte[]> operations;
	
	/**
	 * The serializer used to convert Java objects to byte arrays and vice versa.
	 * This enables storing complex Java objects in Redis geo indexes.
	 */
	private OrangeRedisSerializer redisSerializer;
	
	/**
	 * Logger for recording operation details and debugging information.
	 * Used to log method calls, parameters, and results when debug logging is enabled.
	 */
	private OrangeRedisLogger logger;
	
	/**
	 * Constructs a new OrangeRedisDefaultGeoOperations instance with the required dependencies.
	 *
	 * @param template The Spring Data Redis template that provides Redis operations
	 * @param redisSerializer The serializer to convert between Java objects and byte arrays
	 * @param logger The logger for recording operation details and debugging information
	 */
	public OrangeRedisDefaultGeoOperations(RedisTemplate<String,byte[]> template,OrangeRedisSerializer redisSerializer,OrangeRedisLogger logger) {
		super(template,logger);
		this.operations = template.opsForGeo();
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}

	/**
	 * Removes the specified members from the geo index stored at the given key.
	 *
	 * <p>This method removes one or more members from the geo index. If a member does not exist,
	 * it will be ignored. The operation is atomic - either all valid members are removed or none are.
	 *
	 * @param key The key of the geo index
	 * @param valueType The type of the values to be removed, used for serialization
	 * @param values The members to remove from the geo index
	 * @return The number of members that were removed from the geo index, not including non existing members
	 * @throws Exception if there is an error during serialization or Redis operation
	 * @see GeoOperations#remove(Object, Object...)
	 */
	@Override
	public Long remove(String key, RedisValueTypeEnum valueType,Object... values) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis geo 'remove' operation executing: remove(key:{},locations:{})", key, redisSerializer.serializeToJSONString(values));
		}
		byte[][] newArray = redisSerializer.serialize(values, valueType);
		Long results = this.operations.remove(key, newArray);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis geo 'remove' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Adds multiple geo points with their associated members to the geo index stored at the given key.
	 *
	 * <p>This method adds multiple geo points with their associated members to the geo index.
	 * If a member already exists, its position will be updated to the new point.
	 *
	 * @param key The key of the geo index
	 * @param members The list of GeoEntry objects containing location and coordinates to add
	 * @param valueType The type of the values to be added, used for serialization
	 * @return The number of elements added to the geo index, not including elements already existing
	 * @throws Exception if there is an error during serialization or Redis operation
	 * @see GeoOperations#add(Object, Iterable)
	 */
	@Override
	public Long add(String key, List<GeoEntry> members, RedisValueTypeEnum valueType) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis geo 'add' operation executing: add(key:{},members:{})", key, redisSerializer.serializeToJSONString(members));
		}
		List<GeoLocation<byte[]>> locations = new ArrayList<>();
		for(GeoEntry member : members) {
			GeoLocation<byte[]> location = new GeoLocation<>(
					redisSerializer.serialize(member.getLocation(), valueType),
					new Point(member.getLongitude(),member.getLatitude())
			);
			locations.add(location);
		}
		Long results = this.operations.add(key, locations);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis geo 'add' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Calculates the distance between two members in the geo index.
	 *
	 * <p>This method returns the distance between two members in the specified unit.
	 * The distance is calculated using the Haversine formula.
	 *
	 * @param key The key of the geo index
	 * @param location1 The first member
	 * @param location2 The second member
	 * @param geoUnit The unit of the returned distance
	 * @param valueType The type of the values, used for serialization
	 * @return The distance between the two members in the specified unit, or null if one or both members don't exist
	 * @throws Exception if there is an error during serialization or Redis operation
	 */
	@Override
	public Double distance(
		String key, 
		Object location1, 
		Object location2, 
		GeoDistanceUnitEnum geoUnit, 
		RedisValueTypeEnum valueType
	) throws Exception {
		byte[] location1Bytes = redisSerializer.serialize(location1, valueType);
		byte[] location2Bytes = redisSerializer.serialize(location2, valueType); 
		if(this.logger.isDebugEnabled()) {
			this.logger.debug(
				"Rdis geo 'distance' operation executing: distance(key:{},location1:{},location2:{})", 
				key, 
				new String(location1Bytes), 
				new String(location2Bytes)
			);
		}
		Distance distance = this.operations.distance(
			key, 
			location1Bytes,
			location2Bytes,
			OrangeGeoDistanceMapSpringMetricEnum.getByOrangeGeoUnit(geoUnit)
		);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis geo 'distance' operation returned {}", distance.getValue());
		}
		if(distance != null) {
			return distance.getValue();
		}
		return null;
	}
	
	/**
	 * Retrieves the positions (coordinates) for one or more members of the geo index.
	 *
	 * <p>This method returns the longitude and latitude of all the specified members as GeoEntry objects.
	 * For members that are not found in the geo index, null will be returned in their respective positions.
	 *
	 * @param key The key of the geo index
	 * @param valueType The type of the members, used for serialization
	 * @param values The members whose positions are to be retrieved
	 * @return A list of GeoEntry objects containing the positions and original values of the specified members,
	 *         in the same order as requested, with null entries for non-existent members
	 * @throws Exception if there is an error during serialization or Redis operation
	 * @see GeoOperations#position(Object, Object...)
	 */
	@Override
	public List<GeoEntry> position(String key, RedisValueTypeEnum valueType, Object... values) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug("Rdis geo 'position' operation executing: position(key:{},locations:{})", key, redisSerializer.serializeToJSONString(values));
		}
		byte[][] bytes = redisSerializer.serialize(values, valueType);
		List<Point> points = this.operations.position(key, bytes);
		List<GeoEntry> entries = new ArrayList<>();
		if(points == null) {
			return entries;
		}
		int size = points.size();
		for(int i = 0; i < size; i++) {
			Point point = points.get(i);
			if(point == null) {
				entries.add(null);
				continue;
			}
			GeoEntry entry = new GeoEntry(values[i],point.getX(),point.getY());
			entries.add(entry);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Redis geo 'position' operation returned {}", redisSerializer.serializeToJSONString(entries));
		}
		return entries;
	}

	/**
	 * Searches for members within a given radius from a member location in the geo index.
	 *
	 * <p>This method finds all members within the specified radius from a given member's location.
	 * The search can be customized using the SearchArguments parameter to include coordinates,
	 * distances, sorting preferences, and limit the number of results.
	 *
	 * @param key The key of the geo index
	 * @param location The member whose location is used as the center of the search
	 * @param radius The radius of the search area
	 * @param unit The unit of the radius (e.g., meters, kilometers)
	 * @param valueType The type of the values stored in the geo index, used for serialization
	 * @param returnType The expected return type for deserialization
	 * @param searchArguments Additional search parameters (coordinates inclusion, distance inclusion, sorting, limit)
	 * @return A list of GeoEntryInRadius objects containing the matching members and their details
	 * @throws Exception if there is an error during serialization or Redis operation
	 * @see GeoOperations#radius(Object, Object, Distance, GeoRadiusCommandArgs)
	 */
	@Override
	public List<GeoEntryInRadius> radius(
		String key, 
		Object location, 
		double radius, 
		GeoDistanceUnitEnum unit, 
		RedisValueTypeEnum valueType, 
		Type returnType,
		SearchArguments searchArguments
	) throws Exception {
		byte[] locationBytes = redisSerializer.serialize(location, valueType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug(
				"Rdis geo 'radius' operation executing: radius(key:{},location:{},radius:{},unit:{},includeCoordinates:{},isIncludeDistance:{},isSortAscending:{},count:{},any:{})", 
				key, 
				new String(locationBytes),
				radius,
				unit,
				searchArguments.isIncludeCoordinates(),
				searchArguments.isIncludeDistance(),
				searchArguments.isSortAscending(),
				searchArguments.getCount(),
				searchArguments.isAny()
			);
		}
		GeoRadiusCommandArgs args = GeoRadiusCommandArgs.newGeoRadiusArgs();
		if(searchArguments.isIncludeCoordinates()) {
			args.includeCoordinates();
		}
		if(searchArguments.isIncludeDistance()) {
			args.includeDistance();
		}
		if(searchArguments.isSortAscending()) {
			args.sortAscending();
		}else{
			args.sortDescending();
		}
		args.limit(searchArguments.getCount(),searchArguments.isAny());
		GeoResults<GeoLocation<byte[]>> resultsWithAvgDistance = this.operations.radius(
			key, 
			locationBytes, 
			new Distance(
				radius, 
				OrangeGeoDistanceMapSpringMetricEnum.getByOrangeGeoUnit(unit)
			),
			args
		);
		List<GeoEntryInRadius> results = toEntry(resultsWithAvgDistance,valueType,returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis geo 'radius' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}
	
	/**
	 * Searches for members within a given radius from a specific geographic point in the geo index.
	 *
	 * <p>This method finds all members within the specified radius from a given point defined by longitude and latitude.
	 * The search can be customized using the SearchArguments parameter to include coordinates,
	 * distances, sorting preferences, and limit the number of results.
	 *
	 * @param key The key of the geo index
	 * @param longitude The longitude of the center point of the search
	 * @param latitude The latitude of the center point of the search
	 * @param distance The radius of the search area
	 * @param unit The unit of the radius (e.g., meters, kilometers)
	 * @param valueType The type of the values stored in the geo index, used for serialization
	 * @param returnType The expected return type for deserialization
	 * @param searchArguments Additional search parameters (coordinates inclusion, distance inclusion, sorting, limit)
	 * @return A list of GeoEntryInRadius objects containing the matching members and their details
	 * @throws Exception if there is an error during serialization or Redis operation
	 * @see GeoOperations#radius(Object, Circle, GeoRadiusCommandArgs)
	 */
	@Override
	public List<GeoEntryInRadius> radius(
		String key, 
		Double longitude, 
		Double latitude, 
		Double distance, 
		GeoDistanceUnitEnum unit,
		RedisValueTypeEnum valueType, 
		Type returnType, 
		SearchArguments searchArguments
	) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug(
				"Rdis geo 'radius' operation executing: radius(key:{},longitude:{},latitude:{},distance:{},unit:{},includeCoordinates:{},isIncludeDistance:{},isSortAscending:{},count:{},any:{})", 
				key, 
				longitude,
				latitude,
				distance,
				unit,
				searchArguments.isIncludeCoordinates(),
				searchArguments.isIncludeDistance(),
				searchArguments.isSortAscending(),
				searchArguments.getCount(),
				searchArguments.isAny()
			);
		}
		Circle circle = new Circle(
			new Point(longitude, latitude),
			new Distance(
				distance, 
				OrangeGeoDistanceMapSpringMetricEnum.getByOrangeGeoUnit(unit)
			)
		);
		GeoRadiusCommandArgs args = GeoRadiusCommandArgs.newGeoRadiusArgs();
		if(searchArguments.isIncludeCoordinates()) {
			args.includeCoordinates();
		}
		if(searchArguments.isIncludeDistance()) {
			args.includeDistance();
		}
		if(searchArguments.isSortAscending()) {
			args.sortAscending();
		}else{
			args.sortDescending();
		}
		args.limit(searchArguments.getCount(),searchArguments.isAny());
		GeoResults<GeoLocation<byte[]>> resultsWithAvgDistance = this.operations.radius(key, circle,args);
		List<GeoEntryInRadius> results = toEntry(resultsWithAvgDistance,valueType,returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis geo 'radius' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}
	
	/**
	 * Searches for members within a given radius from a member location and stores the result in another key.
	 *
	 * <p>This method finds all members within the specified radius from a given member's location
	 * and stores the result in a destination key. The search can be customized using the SearchArguments
	 * parameter to include distances, sorting preferences, and limit the number of results.
	 *
	 * @param key The key of the geo index to search in
	 * @param destKey The key where the results will be stored
	 * @param location The member whose location is used as the center of the search
	 * @param distance The radius of the search area
	 * @param unit The unit of the radius (e.g., meters, kilometers)
	 * @param valueType The type of the values stored in the geo index, used for serialization
	 * @param searchArguments Additional search parameters (distance inclusion, sorting, limit)
	 * @return The number of members stored in the destination key
	 * @throws Exception if there is an error during serialization or Redis operation
	 * @see GeoOperations#searchAndStore(Object, Object, GeoReference, Distance, GeoSearchStoreCommandArgs)
	 */
	@Override
	public Long searchRadiusAndStore(
		String key, 
		String destKey,
		Object location, 
		Double distance, 
		GeoDistanceUnitEnum unit,
		RedisValueTypeEnum valueType, 
		SearchArguments searchArguments
	) throws Exception {
		byte[] locationBytes = redisSerializer.serialize(location, valueType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug(
				"Rdis geo 'searchRadiusAndStore' operation executing: searchRadiusAndStore(key:{},destKey:{},location:{},distance:{},unit:{},includeCoordinates:{},isIncludeDistance:{},isSortAscending:{},count:{},any:{})", 
				key, 
				destKey,
				new String(locationBytes),
				distance,
				unit,
				searchArguments.isIncludeCoordinates(),
				searchArguments.isIncludeDistance(),
				searchArguments.isSortAscending(),
				searchArguments.getCount(),
				searchArguments.isAny()
			);
		}
		GeoSearchStoreCommandArgs args = GeoSearchStoreCommandArgs.newGeoSearchStoreArgs();
		if(searchArguments.isIncludeDistance()) {
			args.storeDistance();
		}
		if(searchArguments.isSortAscending()) {
			args.sortAscending();
		}else{
			args.sortDescending();
		}
		args.limit(searchArguments.getCount(),searchArguments.isAny());
		Long results = this.operations.searchAndStore(
			key, 
			destKey,
			GeoReference.fromMember(locationBytes), 
			new Distance(distance, OrangeGeoDistanceMapSpringMetricEnum.getByOrangeGeoUnit(unit)),
			args
		);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis geo 'searchRadiusAndStore' operation returned {}", results);
		}
		return results;
	}
	
	/**
	 * Searches for members within a given radius from a specific geographic point and stores the result in another key.
	 *
	 * <p>This method finds all members within the specified radius from a given point defined by longitude and latitude,
	 * and stores the result in a destination key. The search can be customized using the SearchArguments
	 * parameter to include distances, sorting preferences, and limit the number of results.
	 *
	 * @param key The key of the geo index to search in
	 * @param destKey The key where the results will be stored
	 * @param longitude The longitude of the center point of the search
	 * @param latitude The latitude of the center point of the search
	 * @param distance The radius of the search area
	 * @param unit The unit of the radius (e.g., meters, kilometers)
	 * @param searchArguments Additional search parameters (distance inclusion, sorting, limit)
	 * @return The number of members stored in the destination key
	 * @throws Exception if there is an error during serialization or Redis operation
	 * @see GeoOperations#searchAndStore(Object, Object, GeoReference, Distance, GeoSearchStoreCommandArgs)
	 */
	@Override
	public Long searchRadiusAndStore(
		String key, 
		String destKey,
		Double longitude, 
		Double latitude, 
		Double distance, 
		GeoDistanceUnitEnum unit,
		SearchArguments searchArguments
	) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug(
				"Rdis geo 'searchRadiusAndStore' operation executing: searchRadiusAndStore(key:{},destKey:{},longitude:{},latitude:{},distance:{},unit:{},includeCoordinates:{},isIncludeDistance:{},isSortAscending:{},count:{},any:{})", 
				key, 
				destKey,
				longitude,
				latitude,
				distance,
				unit,
				searchArguments.isIncludeCoordinates(),
				searchArguments.isIncludeDistance(),
				searchArguments.isSortAscending(),
				searchArguments.getCount(),
				searchArguments.isAny()
			);
		}
		GeoSearchStoreCommandArgs args = GeoSearchStoreCommandArgs.newGeoSearchStoreArgs();
		if(searchArguments.isIncludeDistance()) {
			args.storeDistance();
		}
		if(searchArguments.isSortAscending()) {
			args.sortAscending();
		}else{
			args.sortDescending();
		}
		args.limit(searchArguments.getCount(),searchArguments.isAny());
		Long results = this.operations.searchAndStore(
			key, 
			destKey,
			GeoReference.fromCoordinate(longitude, latitude), 
			new Distance(distance, OrangeGeoDistanceMapSpringMetricEnum.getByOrangeGeoUnit(unit)),
			args
		);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis geo 'searchRadiusAndStore' operation returned {}", results);
		}
		return results;
	}

	/**
	 * Searches for members within a bounding box centered at a specific geographic point in the geo index.
	 *
	 * <p>This method finds all members within a rectangular area defined by width and height dimensions
	 * centered at the specified longitude and latitude. The search can be customized using the SearchArguments
	 * parameter to include coordinates, distances, sorting preferences, and limit the number of results.
	 *
	 * @param key The key of the geo index
	 * @param longitude The longitude of the center point of the bounding box
	 * @param latitude The latitude of the center point of the bounding box
	 * @param widthUnit The unit of the width dimension (e.g., meters, kilometers)
	 * @param width The width of the bounding box
	 * @param heightUnit The unit of the height dimension (e.g., meters, kilometers)
	 * @param height The height of the bounding box
	 * @param valueType The type of the values stored in the geo index, used for serialization
	 * @param returnType The expected return type for deserialization
	 * @param searchArguments Additional search parameters (coordinates inclusion, distance inclusion, sorting, limit)
	 * @return A list of GeoEntryInRadius objects containing the matching members and their details
	 * @throws Exception if there is an error during serialization or Redis operation
	 * @see GeoOperations#search(Object, GeoReference, BoundingBox, GeoSearchCommandArgs)
	 */
	@Override
	public List<GeoEntryInRadius> box(
		String key, 
		Double longitude, 
		Double latitude, 
		GeoDistanceUnitEnum widthUnit,
		Double width, 
		GeoDistanceUnitEnum heightUnit,
		Double height,
		RedisValueTypeEnum valueType, 
		Type returnType, 
		SearchArguments searchArguments
	)throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug(
				"Rdis geo 'box' operation executing: box(key:{},longitude:{},latitude:{},widthUnit:{},width:{},heightUnit:{},height:{},includeCoordinates:{},isIncludeDistance:{},isSortAscending:{},count:{},any:{})", 
				key, 
				longitude,
				latitude,
				widthUnit,
				width,
				heightUnit,
				height,
				searchArguments.isIncludeCoordinates(),
				searchArguments.isIncludeDistance(),
				searchArguments.isSortAscending(),
				searchArguments.getCount(),
				searchArguments.isAny()
			);
		}
		GeoSearchCommandArgs args = GeoSearchCommandArgs.newGeoSearchArgs();
		if(searchArguments.isIncludeCoordinates()) {
			args.includeCoordinates();
		}
		if(searchArguments.isIncludeDistance()) {
			args.includeDistance();
		}
		if(searchArguments.isSortAscending()) {
			args.sortAscending();
		}else{
			args.sortDescending();
		}
		args.limit(searchArguments.getCount(),searchArguments.isAny());
		GeoResults<GeoLocation<byte[]>> resultsWithAvgDistance = this.operations.search(
				key, 
				GeoReference.fromCoordinate(longitude, latitude), 
				new BoundingBox(
					new Distance(width, OrangeGeoDistanceMapSpringMetricEnum.getByOrangeGeoUnit(widthUnit)), 
					new Distance(height, OrangeGeoDistanceMapSpringMetricEnum.getByOrangeGeoUnit(heightUnit))
				),
				args
		);
		
		List<GeoEntryInRadius> results = toEntry(resultsWithAvgDistance,valueType,returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis geo 'box' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Searches for members within a bounding box centered at a member location in the geo index.
	 *
	 * <p>This method finds all members within a rectangular area defined by width and height dimensions
	 * centered at the location of the specified member. The search can be customized using the SearchArguments
	 * parameter to include coordinates, distances, sorting preferences, and limit the number of results.
	 *
	 * @param key The key of the geo index
	 * @param location The member whose location is used as the center of the bounding box
	 * @param width The width of the bounding box
	 * @param widthUnit The unit of the width dimension (e.g., meters, kilometers)
	 * @param height The height of the bounding box
	 * @param heightUnit The unit of the height dimension (e.g., meters, kilometers)
	 * @param valueType The type of the values stored in the geo index, used for serialization
	 * @param returnType The expected return type for deserialization
	 * @param searchArguments Additional search parameters (coordinates inclusion, distance inclusion, sorting, limit)
	 * @return A list of GeoEntryInRadius objects containing the matching members and their details
	 * @throws Exception if there is an error during serialization or Redis operation
	 * @see GeoOperations#search(Object, GeoReference, BoundingBox, GeoSearchCommandArgs)
	 */
	@Override
	public List<GeoEntryInRadius> box(
		String key, 
		Object location, 
		Double width, 
		GeoDistanceUnitEnum widthUnit,
		Double height,
		GeoDistanceUnitEnum heightUnit,
		RedisValueTypeEnum valueType, 
		Type returnType, 
		SearchArguments searchArguments
	)throws Exception {
		byte[] locationBytes = redisSerializer.serialize(location, valueType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug(
				"Rdis geo 'box' operation executing: box(key:{},location:{},widthUnit:{},width:{},heightUnit:{},height:{},includeCoordinates:{},isIncludeDistance:{},isSortAscending:{},count:{},any:{})", 
				key, 
				new String(locationBytes),
				widthUnit,
				width,
				heightUnit,
				height,
				searchArguments.isIncludeCoordinates(),
				searchArguments.isIncludeDistance(),
				searchArguments.isSortAscending(),
				searchArguments.getCount(),
				searchArguments.isAny()
			);
		}
		GeoSearchCommandArgs args = GeoSearchCommandArgs.newGeoSearchArgs();
		if(searchArguments.isIncludeCoordinates()) {
			args.includeCoordinates();
		}
		if(searchArguments.isIncludeDistance()) {
			args.includeDistance();
		}
		if(searchArguments.isSortAscending()) {
			args.sortAscending();
		}else{
			args.sortDescending();
		}
		args.limit(searchArguments.getCount(),searchArguments.isAny());
		GeoResults<GeoLocation<byte[]>> resultsWithAvgDistance = this.operations.search(
				key, 
				GeoReference.fromMember(locationBytes), 
				new BoundingBox(
					new Distance(width, OrangeGeoDistanceMapSpringMetricEnum.getByOrangeGeoUnit(widthUnit)), 
					new Distance(height, OrangeGeoDistanceMapSpringMetricEnum.getByOrangeGeoUnit(heightUnit))
				),
				args
		);
		List<GeoEntryInRadius> results = toEntry(resultsWithAvgDistance,valueType,returnType);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis geo 'box' operation returned {}", redisSerializer.serializeToJSONString(results));
		}
		return results;
	}

	/**
	 * Searches for members within a bounding box centered at a member location and stores the result in another key.
	 *
	 * <p>This method finds all members within a rectangular area defined by width and height dimensions
	 * centered at the location of the specified member, and stores the result in a destination key.
	 * The search can be customized using the SearchArguments parameter to include distances,
	 * sorting preferences, and limit the number of results.
	 *
	 * @param key The key of the geo index to search in
	 * @param destKey The key where the results will be stored
	 * @param location The member whose location is used as the center of the bounding box
	 * @param width The width of the bounding box
	 * @param widthUnit The unit of the width dimension (e.g., meters, kilometers)
	 * @param height The height of the bounding box
	 * @param heightUnit The unit of the height dimension (e.g., meters, kilometers)
	 * @param valueType The type of the values stored in the geo index, used for serialization
	 * @param searchArguments Additional search parameters (distance inclusion, sorting, limit)
	 * @return The number of members stored in the destination key
	 * @throws Exception if there is an error during serialization or Redis operation
	 * @see GeoOperations#searchAndStore(Object, Object, GeoReference, BoundingBox, GeoSearchStoreCommandArgs)
	 */
	@Override
	public Long searchBoxAndStore(
		String key, 
		String destKey,
		Object location, 
		Double width, 
		GeoDistanceUnitEnum widthUnit,
		Double height,
		GeoDistanceUnitEnum heightUnit,
		RedisValueTypeEnum valueType, 
		SearchArguments searchArguments
	) throws Exception {
		byte[] locationBytes = redisSerializer.serialize(location, valueType);
		if(this.logger.isDebugEnabled()) {
			this.logger.debug(
				"Rdis geo 'searchBoxAndStore' operation executing: searchBoxAndStore(key:{},destKey:{},location:{},widthUnit:{},width:{},heightUnit:{},height:{},includeCoordinates:{},isIncludeDistance:{},isSortAscending:{},count:{},any:{})", 
				key, 
				destKey,
				new String(locationBytes),
				widthUnit,
				width,
				heightUnit,
				height,
				searchArguments.isIncludeCoordinates(),
				searchArguments.isIncludeDistance(),
				searchArguments.isSortAscending(),
				searchArguments.getCount(),
				searchArguments.isAny()
			);
		}
		GeoSearchStoreCommandArgs args = GeoSearchStoreCommandArgs.newGeoSearchStoreArgs();
		if(searchArguments.isIncludeDistance()) {
			args.storeDistance();
		}
		if(searchArguments.isSortAscending()) {
			args.sortAscending();
		}else{
			args.sortDescending();
		}
		args.limit(searchArguments.getCount(),searchArguments.isAny());
		Long results = this.operations.searchAndStore(
			key, 
			destKey,
			GeoReference.fromMember(locationBytes), 
			new BoundingBox(
				new Distance(width, OrangeGeoDistanceMapSpringMetricEnum.getByOrangeGeoUnit(widthUnit)), 
				new Distance(height, OrangeGeoDistanceMapSpringMetricEnum.getByOrangeGeoUnit(heightUnit))
			),
			args
		);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis geo 'searchBoxAndStore' operation returned {}", results);
		}
		return results;
	}
	
	/**
	 * Searches for members within a bounding box centered at a specific geographic point and stores the result in another key.
	 *
	 * <p>This method finds all members within a rectangular area defined by width and height dimensions
	 * centered at the specified longitude and latitude, and stores the result in a destination key.
	 * The search can be customized using the SearchArguments parameter to include distances,
	 * sorting preferences, and limit the number of results.
	 *
	 * @param key The key of the geo index to search in
	 * @param destKey The key where the results will be stored
	 * @param longitude The longitude of the center point of the bounding box
	 * @param latitude The latitude of the center point of the bounding box
	 * @param widthUnit The unit of the width dimension (e.g., meters, kilometers)
	 * @param width The width of the bounding box
	 * @param heightUnit The unit of the height dimension (e.g., meters, kilometers)
	 * @param height The height of the bounding box
	 * @param searchArguments Additional search parameters (distance inclusion, sorting, limit)
	 * @return The number of members stored in the destination key
	 * @throws Exception if there is an error during serialization or Redis operation
	 * @see GeoOperations#searchAndStore(Object, Object, GeoReference, BoundingBox, GeoSearchStoreCommandArgs)
	 */
	@Override
	public Long searchBoxAndStore(
		String key, 
		String destKey,
		Double longitude, 
		Double latitude, 
		GeoDistanceUnitEnum widthUnit,
		Double width, 
		GeoDistanceUnitEnum heightUnit,
		Double height,
		SearchArguments searchArguments
	) throws Exception {
		if(this.logger.isDebugEnabled()) {
			this.logger.debug(
				"Rdis geo 'searchBoxAndStore' operation executing: searchBoxAndStore(key:{},destKey:{},longitude:{},latitude:{},widthUnit:{},width:{},heightUnit:{},height:{},includeCoordinates:{},isIncludeDistance:{},isSortAscending:{},count:{},any:{})", 
				key, 
				destKey,
				longitude,
				latitude,
				widthUnit,
				width,
				heightUnit,
				height,
				searchArguments.isIncludeCoordinates(),
				searchArguments.isIncludeDistance(),
				searchArguments.isSortAscending(),
				searchArguments.getCount(),
				searchArguments.isAny()
			);
		}
		GeoSearchStoreCommandArgs args = GeoSearchStoreCommandArgs.newGeoSearchStoreArgs();
		if(searchArguments.isIncludeDistance()) {
			args.storeDistance();
		}
		if(searchArguments.isSortAscending()) {
			args.sortAscending();
		}else{
			args.sortDescending();
		}
		args.limit(searchArguments.getCount(),searchArguments.isAny());
		Long results = this.operations.searchAndStore(
			key, 
			destKey,
			GeoReference.fromCoordinate(longitude, latitude), 
			new BoundingBox(
				new Distance(width, OrangeGeoDistanceMapSpringMetricEnum.getByOrangeGeoUnit(widthUnit)), 
				new Distance(height, OrangeGeoDistanceMapSpringMetricEnum.getByOrangeGeoUnit(heightUnit))
			),
			args
		);
		if(logger.isDebugEnabled()) {
			logger.debug("Redis geo 'searchBoxAndStore' operation returned {}", results);
		}
		return results;
	}
	
	/**
	 * Converts Spring Data Redis GeoResults to a list of GeoEntryInRadius objects.
	 *
	 * <p>This private helper method transforms the results from Spring Data Redis geo operations
	 * into a more user-friendly format. It deserializes the binary data into the requested type
	 * and extracts coordinates and distance information into GeoEntryInRadius objects.
	 *
	 * @param resultsWithAvgDistance The GeoResults from Spring Data Redis operations
	 * @param valueType The type of the values stored in the geo index, used for deserialization
	 * @param returnType The expected return type for deserialization
	 * @return A list of GeoEntryInRadius objects containing the deserialized members and their details
	 * @throws Exception if there is an error during deserialization
	 */
	private List<GeoEntryInRadius> toEntry(
		GeoResults<GeoLocation<byte[]>> resultsWithAvgDistance,
		RedisValueTypeEnum valueType,
		Type returnType
	) throws Exception {
		List<GeoResult<GeoLocation<byte[]>>> results = resultsWithAvgDistance.getContent();
		List<GeoEntryInRadius> entries = new ArrayList<>();
		for(GeoResult<GeoLocation<byte[]>> result : results) {
			GeoLocation<byte[]> location = result.getContent();
			Point point = location.getPoint();
			Object value = redisSerializer.deserialize(location.getName(), valueType, returnType);
			GeoEntryInRadius entry;
			if(point == null) {
				entry = new GeoEntryInRadius(
					value,
					null,
					null,
					result.getDistance().getValue()
				);
			}else {
				entry = new GeoEntryInRadius(
					value,
					point.getX(),
					point.getY(),
					result.getDistance().getValue()
				);
			}
			entries.add(entry);
		}
		return entries;
	}
}