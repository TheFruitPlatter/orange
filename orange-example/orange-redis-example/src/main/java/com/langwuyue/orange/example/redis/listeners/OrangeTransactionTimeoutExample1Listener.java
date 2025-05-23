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
package com.langwuyue.orange.example.redis.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.langwuyue.orange.example.redis.api.transaction.OrangeRedisTransactionExample1Api;
import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.annotation.OrangeRedisTxTimeoutListener;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager.OrangeRedisTransactionKey;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionState;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionTimeoutCallbackExecutor.OrangeTransactionTimeoutCallbackMetric;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionTimeoutListener;

@Component
@OrangeRedisTxTimeoutListener(key = OrangeRedisTransactionExample1Api.class)
public class OrangeTransactionTimeoutExample1Listener implements OrangeRedisTransactionTimeoutListener<OrangeValueExampleEntity> {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private static final OrangeRedisTransactionState[] STATES = new OrangeRedisTransactionState[] {
			OrangeRedisTransactionState.SUCCESS,
			OrangeRedisTransactionState.FAILED,
			OrangeRedisTransactionState.UNKNOW
	};

	@Override
	public OrangeRedisTransactionState callback(
		OrangeRedisTransactionKey key, 
		OrangeValueExampleEntity value,
		OrangeTransactionTimeoutCallbackMetric metric
	) {
		try {
			// OrangeRedisTransactionState.SUCCESS,
		    // OrangeRedisTransactionState.FAILED,
		    // OrangeRedisTransactionState.UNKNOW
		    // Prefer 'UNKNOWN' over 'FAILED' for missing data, except after deadline.
		    // Expired/invalid transactions will be purged automatically.
			OrangeRedisTransactionState state = STATES[(int)Math.round((Math.random() * 2))];
			if(OrangeRedisTransactionState.FAILED == state) {
				return OrangeRedisTransactionState.UNKNOW;
			}
			System.out.println("tx timeout callback,key" + key + ", value:" + objectMapper.writeValueAsString(value) + ", transaction info:" + objectMapper.writeValueAsString(metric) + ",state:" + state);
			return state;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return OrangeRedisTransactionState.UNKNOW;
	}

}
