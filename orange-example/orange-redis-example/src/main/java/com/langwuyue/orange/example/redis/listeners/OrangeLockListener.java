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

import org.springframework.stereotype.Component;

import com.langwuyue.orange.example.redis.api.value.OrangeRedisValueExample6Api;
import com.langwuyue.orange.redis.annotation.OrangeRedisIfAbsentListener;
import com.langwuyue.orange.redis.listener.value.OrangeRedisValueSetIfAbsentListener;
import com.langwuyue.orange.redis.listener.value.OrangeRemoveFailedEvent;
import com.langwuyue.orange.redis.listener.value.OrangeSetIfAbsentFailedEvent;
import com.langwuyue.orange.redis.listener.value.OrangeSetIfAbsentSuccessEvent;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Component
@OrangeRedisIfAbsentListener(keys = {OrangeRedisValueExample6Api.class})
public class OrangeLockListener implements OrangeRedisValueSetIfAbsentListener {

	@Override
	public void onFailure(OrangeSetIfAbsentFailedEvent event) {
		System.out.println("OrangeLockListener receive failed event");
		
	}

	@Override
	public void onSuccess(OrangeSetIfAbsentSuccessEvent event) {
		System.out.println("OrangeLockListener receive success event");
		try {
			Thread.sleep(10000);
		}catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("OrangeLockListener done");
		
	}

	@Override
	public void onRemoveFailed(OrangeRemoveFailedEvent event) {
		System.out.println("OrangeLockListener receive remove failed event");
		
	}

}
