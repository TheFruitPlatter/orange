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
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.geo.Metric;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public enum OrangeGeoDistanceMapSpringMetricEnum implements Metric {
	
	METERS(GeoDistanceUnitEnum.METERS), 
	KILOMETERS(GeoDistanceUnitEnum.KILOMETERS), 
	MILES(GeoDistanceUnitEnum.MILES), 
	FEET(GeoDistanceUnitEnum.FEET);
	
	private static final Map<GeoDistanceUnitEnum,OrangeGeoDistanceMapSpringMetricEnum> MAPPING = new EnumMap<>(GeoDistanceUnitEnum.class);
	
	static {
		for (OrangeGeoDistanceMapSpringMetricEnum unitEnum : OrangeGeoDistanceMapSpringMetricEnum.values()) {
			MAPPING.put(unitEnum.getOrangeUnit(), unitEnum);
		}
	}
	
	private final GeoDistanceUnitEnum orangeUnit;

	private OrangeGeoDistanceMapSpringMetricEnum(GeoDistanceUnitEnum orangeUnit) {
		this.orangeUnit = orangeUnit;
	}

	public GeoDistanceUnitEnum getOrangeUnit() {
		return orangeUnit;
	}

	@Override
	public double getMultiplier() {
		return this.getOrangeUnit().getMultiplier();
	}

	@Override
	public String getAbbreviation() {
		return this.getOrangeUnit().getAbbreviation();
	}
	
	public static OrangeGeoDistanceMapSpringMetricEnum getByOrangeGeoUnit(GeoDistanceUnitEnum unitEnum) {
		return MAPPING.get(unitEnum);
	}
}
