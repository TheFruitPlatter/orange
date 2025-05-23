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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisDefaultGeoOperations extends OrangeRedisAbstractOperations implements OrangeRedisGeoOperations{
	
	private GeoOperations<String,byte[]> operations;
	
	private OrangeRedisSerializer redisSerializer;
	
	private OrangeRedisLogger logger;
	
	public OrangeRedisDefaultGeoOperations(RedisTemplate<String,byte[]> template,OrangeRedisSerializer redisSerializer,OrangeRedisLogger logger) {
		super(template,logger);
		this.operations = template.opsForGeo();
		this.redisSerializer = redisSerializer;
		this.logger = logger;
	}

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
