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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public enum GeoDistanceUnitEnum {
	
	METERS(6378137, "m"), KILOMETERS(6378.137, "km"), MILES(3963.191, "mi"), FEET(20925646.325, "ft");

	private final double multiplier;
	private final String abbreviation;

	private GeoDistanceUnitEnum(double multiplier, String abbreviation) {
		this.multiplier = multiplier;
		this.abbreviation = abbreviation;
	}

	public double getMultiplier() {
		return multiplier;
	}

	public String getAbbreviation() {
		return abbreviation;
	}
}
