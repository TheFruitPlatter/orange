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
import com.langwuyue.orange.redis.listener.zset.OrangeAddMemberIfAbsentFailedEvent;
import com.langwuyue.orange.redis.listener.zset.OrangeAddMemberIfAbsentSuccessEvent;
import com.langwuyue.orange.redis.listener.zset.OrangeRedisZSetAddMemberIfAbsentListener;
import com.langwuyue.orange.redis.listener.zset.OrangeRemoveMemberFailedEvent;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Component
@OrangeRedisIfAbsentListener(keys = {OrangeRedisZSetExample1Api.class})
public class OrangeZSetAddMemberIfAbsentListener implements OrangeRedisZSetAddMemberIfAbsentListener {


	@Override
	public void onFailure(OrangeAddMemberIfAbsentFailedEvent event) {
		System.out.println("OrangeZSetAddMemberIfAbsentListener receive failed event");
		
	}

	@Override
	public void onSuccess(OrangeAddMemberIfAbsentSuccessEvent event) {
		System.out.println("OrangeZSetAddMemberIfAbsentListener receive success event");
	}

	@Override
	public void onRemoveFailed(OrangeRemoveMemberFailedEvent event) {
		System.out.println("OrangeRemoveMemberFailedEvent receive remove failed event");
		
	}
}
