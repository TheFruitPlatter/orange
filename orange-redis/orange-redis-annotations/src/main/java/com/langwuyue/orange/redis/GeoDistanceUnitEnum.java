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
package com.langwuyue.orange.redis;


/**
 * Redis geospatial distance unit enum, defines units supported for geo distance calculation and queries.
 * 
 * <p>This enum is mainly used in Redis GEO commands to specify distance units,
 * such as {@code GEODIST}, {@code GEORADIUS} commands.</p>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
public enum GeoDistanceUnitEnum {
	
	/**
	 * Meters, international standard unit
	 * <p>Example: GEODIST key member1 member2 m</p>
	 */
	METERS(6378137, "m"), 
	
	/**
	 * Kilometers, 1 kilometer = 1000 meters
	 * <p>Example: GEORADIUS key longitude latitude 10 km</p>
	 */
	KILOMETERS(6378.137, "km"), 
	
	/**
	 * Miles, 1 mile ≈ 1609.34 meters
	 * <p>Example: GEORADIUS key longitude latitude 5 mi</p>
	 */
	MILES(3963.191, "mi"), 
	
	/**
	 * Feet, 1 foot ≈ 0.3048 meters
	 * <p>Example: GEORADIUS key longitude latitude 1000 ft</p>
	 */
	FEET(20925646.325, "ft");

	private final double multiplier;
	private final String abbreviation;

	private GeoDistanceUnitEnum(double multiplier, String abbreviation) {
		this.multiplier = multiplier;
		this.abbreviation = abbreviation;
	}

	/**
	 * Gets the unit conversion multiplier, used to convert Earth's radius to current unit scale factor.
	 * 
	 * @return Unit conversion multiplier
	 */
	public double getMultiplier() {
		return multiplier;
	}

	/**
	 * Gets the scientific abbreviation of the unit, used for Redis command parameters.
	 * 
	 * @return Unit abbreviation string
	 */
	public String getAbbreviation() {
		return abbreviation;
	}
}