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
package com.langwuyue.orange.redis.timer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.context.OrangeRedisContext.Key;
import com.langwuyue.orange.redis.executor.hash.OrangeCompareAndSwapExecutor;
import com.langwuyue.orange.redis.logger.OrangeRedisDefaultLogger;

public class OrangeRenewTaskTest extends OrangeRenewTask {
	
	public OrangeRenewTaskTest(
		TimeUnit expirationTimeUnit,
		long expirationTime,
		int threshold
	) {
		super(null, null, null, null,threshold);
		this.setExpirationMillis(expirationTimeUnit.toMillis(expirationTime));
		this.setRenewThreshold(this.getExpirationMillis() / threshold * (threshold - 1));
		computeDeadlineMillis();
	}
	
	public void init(
		Key key,
		OrangeCompareAndSwapExecutor executor,
		Object value,
		RedisValueTypeEnum valueType
	) {}
	
	
	
	@Override
	public boolean finish() throws Exception {
		if(isRemove()) {
			return false;
		}
		System.out.println("finish:" + this.getRenewThreshold());
		return true;
	}
	
	public static void main(String[] args) throws InterruptedException{
		OrangeAutoRenewProperties props = new OrangeAutoRenewProperties();
		props.setWheelSize(60);
		props.setTickDuration(Duration.ofMillis(1000));
		OrangeRenewTimerWheel wheel = new OrangeRenewTimerWheel(props,new OrangeRedisDefaultLogger());
		for(int i = 1; i < 200; i++) {
			wheel.addRenewTask(new OrangeRenewTaskTest(TimeUnit.SECONDS,i,3));
		}
		Thread.sleep(10000000);
	}
}
