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

import com.langwuyue.orange.example.redis.api.multiplelocks.OrangeRedisMultipleLocksExample1Api;
import com.langwuyue.orange.redis.annotation.OrangeRedisIfAbsentListener;
import com.langwuyue.orange.redis.listener.multiplelocks.OrangeMultipleLocksEvent;
import com.langwuyue.orange.redis.listener.multiplelocks.OrangeMultipleLocksRemoveFailedEvent;
import com.langwuyue.orange.redis.listener.multiplelocks.OrangeRedisMultipleLocksListener;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Component
@OrangeRedisIfAbsentListener(keys = {OrangeRedisMultipleLocksExample1Api.class})
public class OrangeRedisOrangeRedisMultipleLocksExample1ApiListener implements OrangeRedisMultipleLocksListener {

	@Override
	public void onCompleted(OrangeMultipleLocksEvent event) {
		System.out.println("OrangeRedisTransactionExample1ApiMultipleLocksListener receive completed event");
		if(event.getUnknownMembers().isEmpty() && event.getFailedMembers().isEmpty()) {
			try {
				Thread.sleep(10000);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("OrangeRedisTransactionExample1ApiMultipleLocksListener done");
	}

	@Override
	public void onRemoveFailed(OrangeMultipleLocksRemoveFailedEvent event) {
		System.out.println("OrangeMultipleLocksRemoveFailedEvent receive remove failed event");
	}

}
