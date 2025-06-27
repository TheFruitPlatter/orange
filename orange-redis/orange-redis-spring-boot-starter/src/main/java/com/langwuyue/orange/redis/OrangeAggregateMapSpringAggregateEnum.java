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

import org.springframework.data.redis.connection.RedisZSetCommands;

import com.langwuyue.orange.redis.annotation.cross.Aggregate;

/**
 * Maps Orange framework aggregate operators to Spring Redis ZSet aggregate commands.
 * 
 * <p>This enum provides a mapping between Orange's {@link Aggregate.Operator} and 
 * Spring's {@link RedisZSetCommands.Aggregate} for sorted set operations. It ensures
 * consistent translation between the two aggregation types when performing Redis operations.
 * 
 * <p>The mapping supports the following aggregate operations:
 * <ul>
 *   <li>MAX - Maximum value aggregation</li>
 *   <li>MIN - Minimum value aggregation</li>
 *   <li>SUM - Sum of values aggregation</li>
 * </ul>
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Aggregate.Operator
 * @see RedisZSetCommands.Aggregate
 */
public enum OrangeAggregateMapSpringAggregateEnum {
	
	/**
	 * Maximum value aggregation.
	 * Maps {@link Aggregate.Operator#MAX} to {@link RedisZSetCommands.Aggregate#MAX}.
	 */
	MAX(Aggregate.Operator.MAX, RedisZSetCommands.Aggregate.MAX),
	
	/**
	 * Minimum value aggregation.
	 * Maps {@link Aggregate.Operator#MIN} to {@link RedisZSetCommands.Aggregate#MIN}.
	 */
	MIN(Aggregate.Operator.MIN, RedisZSetCommands.Aggregate.MIN),
	
	/**
	 * Sum of values aggregation.
	 * Maps {@link Aggregate.Operator#SUM} to {@link RedisZSetCommands.Aggregate#SUM}.
	 */
	SUM(Aggregate.Operator.SUM, RedisZSetCommands.Aggregate.SUM),
	;
	private static final Map<Aggregate.Operator,RedisZSetCommands.Aggregate> MAPPING = new EnumMap<>(Aggregate.Operator.class);
	
	static {
		for (OrangeAggregateMapSpringAggregateEnum aggregate : OrangeAggregateMapSpringAggregateEnum.values()) {
			MAPPING.put(aggregate.operator, aggregate.aggregate);
		}
	}
	
	/**
	 * The Orange framework aggregate operator.
	 */
	private Aggregate.Operator operator;
	
	/**
	 * The corresponding Spring Redis ZSet aggregate command.
	 */
	private RedisZSetCommands.Aggregate aggregate;
	
	/**
	 * Constructs a new enum constant with the given Orange operator and Spring aggregate.
	 * 
	 * @param operator the Orange framework aggregate operator
	 * @param aggregate the corresponding Spring Redis ZSet aggregate command
	 */
	OrangeAggregateMapSpringAggregateEnum(Aggregate.Operator operator, RedisZSetCommands.Aggregate aggregate) {
		this.operator = operator;
		this.aggregate = aggregate;
	}

	public static Map<Aggregate.Operator, RedisZSetCommands.Aggregate> getMapping() {
		return MAPPING;
	}

	/**
	 * Gets the Orange framework aggregate operator for this enum constant.
	 * 
	 * @return the Orange framework aggregate operator
	 */
	public Aggregate.Operator getOperator() {
		return operator;
	}

	/**
	 * Gets the Spring Redis ZSet aggregate command for this enum constant.
	 * 
	 * @return the Spring Redis ZSet aggregate command
	 */
	public RedisZSetCommands.Aggregate getAggregate() {
		return aggregate;
	}

	/**
	 * Gets the corresponding Spring Redis ZSet aggregate command for the given Orange operator.
	 * 
	 * @param operator the Orange framework aggregate operator to look up
	 * @return the corresponding Spring Redis ZSet aggregate command
	 * @throws IllegalArgumentException if the operator is not supported
	 */
	public static RedisZSetCommands.Aggregate getByOrangeAggregateOperator(Aggregate.Operator operator) {
		return MAPPING.get(operator);
	}
}