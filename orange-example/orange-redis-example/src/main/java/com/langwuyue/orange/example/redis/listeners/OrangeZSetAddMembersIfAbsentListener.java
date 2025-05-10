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

import com.langwuyue.orange.example.redis.api.zset.OrangeRedisZSetExample1Api;
import com.langwuyue.orange.redis.annotation.OrangeRedisIfAbsentListener;
import com.langwuyue.orange.redis.listener.zset.OrangRemoveMembersFailedEvent;
import com.langwuyue.orange.redis.listener.zset.OrangeAddMembersIfAbsentEvent;
import com.langwuyue.orange.redis.listener.zset.OrangeRedisZSetAddMembersIfAbsentListener;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Component
@OrangeRedisIfAbsentListener(keys = {OrangeRedisZSetExample1Api.class})
public class OrangeZSetAddMembersIfAbsentListener implements OrangeRedisZSetAddMembersIfAbsentListener {

	@Override
	public void onCompleted(OrangeAddMembersIfAbsentEvent event) {
		System.out.println("OrangeZSetAddMembersIfAbsentListener receive completed event");
	}

	@Override
	public void onRemoveFailed(OrangRemoveMembersFailedEvent event) {
		System.out.println("OrangRemoveMembersFailedEvent receive remove failed event");
	}
}
