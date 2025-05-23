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
package com.langwuyue.orange.example.redis.testcase.transaction;

import org.springframework.stereotype.Service;

import com.langwuyue.orange.example.redis.api.value.OrangeRedisValueExample7Api;
import com.langwuyue.orange.example.redis.entity.OrangeValueExampleEntity;
import com.langwuyue.orange.redis.annotation.OrangeRedisIfAbsentListener;
import com.langwuyue.orange.redis.listener.value.OrangeRedisValueSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.value.OrangeRemoveFailedEvent;
import com.langwuyue.orange.redis.listener.value.OrangeSetIfAbsentFailedEvent;
import com.langwuyue.orange.redis.listener.value.OrangeSetIfAbsentSuccessEvent;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Service
@OrangeRedisIfAbsentListener(keys = {OrangeRedisValueExample7Api.class})
public class OrangeNormalService implements OrangeRedisValueSetIfAbsentListener{
	
	private OrangeRedisValueExample7Api example7Api;
	
	private OrangeTransactionService txService;
	
    public OrangeNormalService(OrangeRedisValueExample7Api example7Api) {
        this.example7Api = example7Api;
    }
    
    public void addUserWithTransaction(OrangeValueExampleEntity entity) {
    	// CAS (Compare-And-Swap) cannot be used for inserts in a database transaction.
    	// To ensure uniqueness of the insert operation, we must use a distributed lock.
    	example7Api.lock(entity);
    }

	@Override
	public void onFailure(OrangeSetIfAbsentFailedEvent event) {
		
	}

	@Override
	public void onSuccess(OrangeSetIfAbsentSuccessEvent event) {
		// Do insert when lock successfully.
		txService.addUserWithTransaction((OrangeValueExampleEntity)event.getArgs()[0]);
	}

	@Override
	public void onRemoveFailed(OrangeRemoveFailedEvent event) {
		
	}
}
