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
package com.langwuyue.orange.redis.executor.multiplelocks;

import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.timer.OrangeAutoRenewProperties;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeDefaultExpirationTimeAutoInitializer implements OrangeExpirationTimeAutoInitializer {
	
	private OrangeAutoRenewProperties properties;
	
	public OrangeDefaultExpirationTimeAutoInitializer(OrangeAutoRenewProperties properties) {
		this.properties = properties;
	}

	@Override
	public Key init(Key originKey, int renewThreshold) {
		long expirationTime = (properties.getAutoInitValue().getSeconds() * renewThreshold) / (renewThreshold - 1);
		return new Key(originKey.getOriginalKey(),originKey.getValue(),expirationTime,TimeUnit.SECONDS);
	}
}
