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
 * Context class for handling Redis Sorted Set (ZSet) operations with weights.
 * 
 * <p>This class extends {@link OrangeAggregateContext} to provide specific functionality
 * for managing weighted operations on multiple Redis Sorted Sets. It processes the weights
 * defined by the {@link Weights} annotation when performing operations like ZUNIONSTORE
 * and ZINTERSTORE with weighted scores.
 * 
 * <p>The weights are used to multiply the score of each input sorted set before performing
 * the aggregation operation. This allows for weighted combinations of scores from different
 * sets, giving more importance to some sets over others in the final result.
 * 
 * <p>This context supports weights provided as:
 * <ul>
 *   <li>Collections of numbers or strings (convertible to doubles)</li>
 *   <li>Arrays of numbers or strings (convertible to doubles)</li>
 * </ul>
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see Weights
 * @see OrangeAggregateContext
 */
public class OrangeWeightsContext extends OrangeAggregateContext {
    
    /**
     * The weights to be applied to each sorted set in the operation.
     * 
     * <p>This field holds the value bound from the {@link Weights} annotation on the operation method.
     * It can be a Collection or an Array of numbers or strings that can be converted to double values.
     * 
     * <p>Each weight is used to multiply the score of the corresponding sorted set before
     * the aggregation operation is performed. This allows for weighted combinations of scores,
     * giving more importance to some sets over others.
     * 
     * <p>If this field is null, the default weights from the {@link Aggregate} annotation will be used.
     */
    @OrangeRedisOperationArg(binding = Weights.class)
    private Object weights;

    /**
     * Constructs a new OrangeWeightsContext with the specified parameters.
     * 
     * <p>This constructor initializes a context for handling Redis Sorted Set operations with weights.
     * It sets up the necessary context for performing operations that combine multiple sorted sets
     * using weights and a specific aggregation strategy.
     *
     * @param operationOwner the class that owns the operation method
     * @param operationMethod the method representing the Redis operation
     * @param args the arguments passed to the operation method
     * @param keys the list of Redis keys to operate on
     * @param storeTo the destination key where the result will be stored
     * @param valueType the type of Redis value being operated on
     */
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
    
    /**
     * Gets the weights to be applied to each sorted set in the operation.
     * 
     * <p>This method processes the weights value that was bound from the {@link Weights} annotation
     * on the operation method and converts it to an array of double values. If no weights were
     * specified, it falls back to the default weights from the {@link com.langwuyue.orange.redis.annotation.cross.Aggregate} annotation.
     * 
     * <p>The method supports weights provided as:
     * <ul>
     *   <li>Collections of numbers or strings (convertible to doubles)</li>
     *   <li>Arrays of numbers or strings (convertible to doubles)</li>
     * </ul>
     * 
     * <p>Each weight corresponds to a sorted set in the operation and is used to
     * multiply the scores in that set before aggregation.
     *
     * @return an array of double values representing the weights for each sorted set
     * @throws OrangeRedisException if the weights are not in a supported format or contain invalid values
     */
    public double[] getWeights() {
        if(this.weights == null) {
            return this.getAggregate().weights();
        }
        
        if(this.weights instanceof Collection) {
            Collection<?> collection = (Collection<?>)weights;
            double[] result = new double[collection.size()];
            int i = 0;
            for(Object weight : collection) {
                if(weight instanceof Number || weight instanceof String) {
                    result[i] = Double.valueOf(weight.toString());
                }else{
                    throw new OrangeRedisException(String.format("The member of the argument annotated with @%s must be a number or a string", Weights.class.getSimpleName()));
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
                    throw new OrangeRedisException(String.format("The member of the argument annotated with @%s must be a number or a string", Weights.class.getSimpleName()));
                }
            }
            return result;
        }
        
        throw new OrangeRedisException(String.format("The argument annotated with @%s must be a collection or an array", Weights.class.getSimpleName()));
    }
}