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

import java.util.EnumMap;
import java.util.Map;

import org.springframework.data.geo.Metric;

/**
 * Maps Orange framework geo distance units to Spring Data Redis Metric units.
 * <p>
 * This enum provides a bridge between the Orange framework's geo distance units
 * and Spring Data Redis's Metric interface, allowing seamless integration between
 * the two frameworks.
 * </p>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 */
public enum OrangeGeoDistanceMapSpringMetricEnum implements Metric {
	
	/**
	 * Represents distance in meters.
	 */
	METERS(GeoDistanceUnitEnum.METERS),
	
	/**
	 * Represents distance in kilometers.
	 */
	KILOMETERS(GeoDistanceUnitEnum.KILOMETERS),
	
	/**
	 * Represents distance in miles.
	 */
	MILES(GeoDistanceUnitEnum.MILES),
	
	/**
	 * Represents distance in feet.
	 */
	FEET(GeoDistanceUnitEnum.FEET);
	
	/**
	 * Mapping between Orange geo distance units and this enum's constants.
	 */
	private static final Map<GeoDistanceUnitEnum,OrangeGeoDistanceMapSpringMetricEnum> MAPPING = new EnumMap<>(GeoDistanceUnitEnum.class);
	
	/**
	 * Static initializer to populate the mapping between Orange geo distance units
	 * and this enum's constants.
	 */
	static {
		for (OrangeGeoDistanceMapSpringMetricEnum unitEnum : OrangeGeoDistanceMapSpringMetricEnum.values()) {
			MAPPING.put(unitEnum.getOrangeUnit(), unitEnum);
		}
	}
	
	/**
	 * The Orange framework geo distance unit associated with this enum constant.
	 */
	private final GeoDistanceUnitEnum orangeUnit;

	/**
	 * Constructs a new enum constant with the specified Orange geo distance unit.
	 * 
	 * @param orangeUnit the Orange framework geo distance unit
	 */
	private OrangeGeoDistanceMapSpringMetricEnum(GeoDistanceUnitEnum orangeUnit) {
		this.orangeUnit = orangeUnit;
	}

	/**
	 * Gets the Orange framework geo distance unit associated with this enum constant.
	 * 
	 * @return the Orange framework geo distance unit
	 */
	public GeoDistanceUnitEnum getOrangeUnit() {
		return orangeUnit;
	}

	/**
	 * Gets the multiplier value for converting distances between different units.
	 * 
	 * @return the multiplier value for distance conversion
	 */
	@Override
	public double getMultiplier() {
		return this.getOrangeUnit().getMultiplier();
	}

	/**
	 * Gets the abbreviation string representation of this distance unit.
	 * 
	 * @return the abbreviation string for this distance unit
	 */
	@Override
	public String getAbbreviation() {
		return this.getOrangeUnit().getAbbreviation();
	}
	
	/**
	 * Gets the enum constant corresponding to the specified Orange geo distance unit.
	 * 
	 * @param unitEnum the Orange framework geo distance unit to look up
	 * @return the corresponding enum constant, or null if not found
	 */
	public static OrangeGeoDistanceMapSpringMetricEnum getByOrangeGeoUnit(GeoDistanceUnitEnum unitEnum) {
		return MAPPING.get(unitEnum);
	}
}