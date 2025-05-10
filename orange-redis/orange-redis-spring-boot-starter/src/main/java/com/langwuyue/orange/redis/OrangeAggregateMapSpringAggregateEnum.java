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
 * @author Liang.Zhong
 * @since 1.0.0
 */
public enum OrangeAggregateMapSpringAggregateEnum {
	
	MAX(Aggregate.Operator.MAX,RedisZSetCommands.Aggregate.MAX), 
	MIN(Aggregate.Operator.MIN,RedisZSetCommands.Aggregate.MIN), 
	SUM(Aggregate.Operator.SUM,RedisZSetCommands.Aggregate.SUM), 
	;
	private static final Map<Aggregate.Operator,RedisZSetCommands.Aggregate> MAPPING = new EnumMap<>(Aggregate.Operator.class);
	
	static {
		for (OrangeAggregateMapSpringAggregateEnum aggregate : OrangeAggregateMapSpringAggregateEnum.values()) {
			MAPPING.put(aggregate.operator, aggregate.aggregate);
		}
	}
	
	private Aggregate.Operator operator;
	
	private RedisZSetCommands.Aggregate aggregate;
	
	OrangeAggregateMapSpringAggregateEnum(Aggregate.Operator operator, RedisZSetCommands.Aggregate aggregate) {
		this.operator = operator;
		this.aggregate = aggregate;
	}

	public static Map<Aggregate.Operator, RedisZSetCommands.Aggregate> getMapping() {
		return MAPPING;
	}

	public Aggregate.Operator getOperator() {
		return operator;
	}

	public RedisZSetCommands.Aggregate getAggregate() {
		return aggregate;
	}

	public static RedisZSetCommands.Aggregate getByOrangeAggregateOperator(Aggregate.Operator operator) {
		return MAPPING.get(operator);
	}
}
