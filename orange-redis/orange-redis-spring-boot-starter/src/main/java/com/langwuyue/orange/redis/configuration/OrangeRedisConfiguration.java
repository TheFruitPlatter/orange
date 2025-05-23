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
package com.langwuyue.orange.redis.configuration;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.FactoryBean;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.langwuyue.orange.redis.client.OrangeClientFactoryProvider;
import com.langwuyue.orange.redis.context.builder.OrangeOperationArgHandler;

/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisConfiguration implements OrangeClientFactoryProvider {
	
	private static final Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> VALUE_HANDLERS = new ConcurrentHashMap();

	private static final Map<Class<? extends Annotation>, Class<? extends FactoryBean>> EXECUTOR_FACTORY_MAPPING = new ConcurrentHashMap<>();
	
	private ObjectMapper objectMapper;
	
	public OrangeRedisConfiguration() {
		this.objectMapper = getDefaultObjectMapper();
	}
	
	public void config(Class<? extends Annotation> clientClass,Class<? extends FactoryBean> factoryClass) {
		EXECUTOR_FACTORY_MAPPING.put(clientClass, factoryClass);
	}
	
	public Set<Class<? extends Annotation>> getRedisClientAnnotationClasses() {
		return EXECUTOR_FACTORY_MAPPING.keySet().stream().collect(Collectors.toSet());
	}
	
	@Override
	public Class<?> getFactoryClass(Class<? extends Annotation> clientClass) {
		return EXECUTOR_FACTORY_MAPPING.get(clientClass);
	}
	
	public void config(OrangeOperationArgHandler handler) {
		VALUE_HANDLERS.put(handler.getClass(), handler);
	}
	
	public Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> getValueHandlerMap(){
		Map<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> map = new HashMap<>();
		for(Entry<Class<? extends OrangeOperationArgHandler>, OrangeOperationArgHandler> entry : VALUE_HANDLERS.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	
	public void config(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	public ObjectMapper getObjectMapper() {
		return this.objectMapper;
	}
	
	private ObjectMapper getDefaultObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        objectMapper.setDateFormat(dateFormat);
        objectMapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,true);
        return objectMapper;
	}
}
