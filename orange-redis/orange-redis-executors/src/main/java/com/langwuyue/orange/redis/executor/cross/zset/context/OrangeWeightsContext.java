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
package com.langwuyue.orange.redis.executor.cross.zset.context;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import com.langwuyue.orange.redis.OrangeRedisException;
import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.OrangeRedisOperationArg;
import com.langwuyue.orange.redis.annotation.cross.Weights;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeWeightsContext extends OrangeAggregateContext {
	
	@OrangeRedisOperationArg(binding = Weights.class)
	private Object weights;

	public OrangeWeightsContext(
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		List<String> keys,
		String storeTo,
		RedisValueTypeEnum valueType
	) {
		super(operationOwner, operationMethod, args, keys, storeTo, valueType);
	}
	
	public double[] getWeights() {
		if(this.weights == null) {
			return this.getAggregate().weights();
		}
		
		if(this.weights instanceof Collection) {
			Collection collection = (Collection)weights;
			double[] result = new double[collection.size()];
			int i = 0;
			for(Object weight : collection) {
				if(weight instanceof Number || weight instanceof String) {
					result[i] = Double.valueOf(weight.toString());
				}else{
					throw new OrangeRedisException(String.format("The member of the argument annotated with @%s must be a number or a string", Weights.class));
				}
				i++;
			}
			return result;
		}
		
		if(this.weights.getClass().isArray()) {
			int len = Array.getLength(this.weights);
			double[] result = new double[len];
			for (int i = 0; i < len; i++) {
				Object weight = Array.get(this.weights, i);
				if(weight instanceof Number || weight instanceof String) {
					result[i] = Double.valueOf(weight.toString());
				}else{
					throw new OrangeRedisException(String.format("The member of the argument annotated with @%s must be a number or a string", Weights.class));
				}
			}
			return result;
		}
		
		throw new OrangeRedisException(String.format("The argument annotated with @%s must be a collection or an array", Weights.class));
	}
}
