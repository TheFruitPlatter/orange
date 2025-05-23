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
package com.langwuyue.orange.redis.logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;


/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public class OrangeRedisDefaultLogger implements OrangeRedisLogger {
	
	private static final Map<String, Logger> LOGGER_MAP = new ConcurrentHashMap<>();
	
	
	public OrangeRedisDefaultLogger() {
		
	}
	
	public void trace(String msg) {
		Logger logger = getLogger();
		if(logger.isTraceEnabled()) {
			logger.trace(msg);
		}
	}

	
	public void trace(String format, Object arg) {
		Logger logger = getLogger();
		if(logger.isTraceEnabled()) {
			logger.trace(format,arg);
		}
		
	}

	
	public void trace(String format, Object arg1, Object arg2) {
		Logger logger = getLogger();
		if(logger.isTraceEnabled()) {
			logger.trace(format,arg1,arg2);
		}
	}

	
	public void trace(String format, Object... arguments) {
		Logger logger = getLogger();
		if(logger.isTraceEnabled()) {
			logger.trace(format,arguments);
		}
	}

	
	public void trace(String msg, Throwable t) {
		Logger logger = getLogger();
		if(logger.isTraceEnabled()) {
			logger.trace(msg,t);
		}
	}

	
	
	public void trace(Marker marker, String msg) {
		Logger logger = getLogger();
		if(logger.isTraceEnabled(marker)) {
			logger.trace(marker,msg);
		}
	}

	
	public void trace(Marker marker, String format, Object arg) {
		Logger logger = getLogger();
		if(logger.isTraceEnabled(marker)) {
			logger.trace(marker,format,arg);
		}
	}

	
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		Logger logger = getLogger();
		if(logger.isTraceEnabled(marker)) {
			logger.trace(marker,format,arg1,arg2);
		}
	}

	
	public void trace(Marker marker, String format, Object... argArray) {
		Logger logger = getLogger();
		if(logger.isTraceEnabled(marker)) {
			logger.trace(marker,format,argArray);
		}
	}

	
	public void trace(Marker marker, String msg, Throwable t) {
		Logger logger = getLogger();
		if(logger.isTraceEnabled(marker)) {
			logger.trace(marker,msg,t);
		}
	}

	
	public void debug(String msg) {
		Logger logger = getLogger();
		if(logger.isDebugEnabled()) {
			logger.debug(msg);
		}
	}

	
	public void debug(String format, Object arg) {
		Logger logger = getLogger();
		if(logger.isDebugEnabled()) {
			logger.debug(format,arg);
		}
	}

	
	public void debug(String format, Object arg1, Object arg2) {
		Logger logger = getLogger();
		if(logger.isDebugEnabled()) {
			logger.debug(format,arg1,arg2);
		}
	}

	
	public void debug(String format, Object... arguments) {
		Logger logger = getLogger();
		if(logger.isDebugEnabled()) {
			logger.debug(format,arguments);
		}
	}

	
	public void debug(String msg, Throwable t) {
		Logger logger = getLogger();
		if(logger.isDebugEnabled()) {
			logger.debug(msg,t);
		}
	}
	
	public void debug(Marker marker, String msg) {
		Logger logger = getLogger();
		if(logger.isDebugEnabled(marker)) {
			logger.debug(marker,msg);
		}
	}

	
	public void debug(Marker marker, String format, Object arg) {
		Logger logger = getLogger();
		if(logger.isDebugEnabled(marker)) {
			logger.debug(marker,format,arg);
		}
	}

	
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		Logger logger = getLogger();
		if(logger.isDebugEnabled(marker)) {
			logger.debug(marker,format,arg1,arg2);
		}
	}

	
	public void debug(Marker marker, String format, Object... arguments) {
		Logger logger = getLogger();
		if(logger.isDebugEnabled(marker)) {
			logger.debug(marker,format,arguments);
		}
	}

	
	public void debug(Marker marker, String msg, Throwable t) {
		Logger logger = getLogger();
		if(logger.isDebugEnabled(marker)) {
			logger.debug(marker,msg,t);
		}
	}
	
	public void info(String msg) {
		Logger logger = getLogger();	
		if(logger.isInfoEnabled()) {
			logger.info(msg);
		}
	}

	
	public void info(String format, Object arg) {
		Logger logger = getLogger();
		if(logger.isInfoEnabled()) {
			logger.info(format,arg);
		}
	}

	
	public void info(String format, Object arg1, Object arg2) {
		Logger logger = getLogger();
		if(logger.isInfoEnabled()) {
			logger.info(format,arg1,arg2);
		}
	}

	
	public void info(String format, Object... arguments) {
		Logger logger = getLogger();
		if(logger.isInfoEnabled()) {
			logger.info(format,arguments);
		}
	}

	
	public void info(String msg, Throwable t) {
		Logger logger = getLogger();
		if(logger.isInfoEnabled()) {
			logger.info(msg,t);
		}
	}

	
	public void info(Marker marker, String msg) {
		Logger logger = getLogger();
		if(logger.isInfoEnabled(marker)) {
			logger.info(marker,msg);
		}
	}

	
	public void info(Marker marker, String format, Object arg) {
		Logger logger = getLogger();
		if(logger.isInfoEnabled(marker)) {
			logger.info(marker,format,arg);
		}
	}

	
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		Logger logger = getLogger();
		if(logger.isInfoEnabled(marker)) {
			logger.info(marker,format,arg1,arg2);
		}
	}

	
	public void info(Marker marker, String format, Object... arguments) {
		Logger logger = getLogger();
		if(logger.isInfoEnabled(marker)) {
			logger.info(marker,format,arguments);
		}
	}

	
	public void info(Marker marker, String msg, Throwable t) {
		Logger logger = getLogger();
		if(logger.isInfoEnabled(marker)) {
			logger.info(marker,msg,t);
		}
	}
	
	public void warn(String msg) {
		Logger logger = getLogger();
		if(logger.isWarnEnabled()) {
			logger.warn(msg);
		}
	}

	
	public void warn(String format, Object arg) {
		Logger logger = getLogger();
		if(logger.isWarnEnabled()) {
			logger.warn(format,arg);
		}
	}

	
	public void warn(String format, Object... arguments) {
		Logger logger = getLogger();
		if(logger.isWarnEnabled()) {
			logger.warn(format,arguments);
		}
	}
	
	public void warn(String format, Object arg1, Object arg2) {
		Logger logger = getLogger();
		if(logger.isWarnEnabled()) {
			logger.warn(format,arg1,arg2);
		}
	}

	
	public void warn(String msg, Throwable t) {
		Logger logger = getLogger();
		if(logger.isWarnEnabled()) {
			logger.warn(msg,t);
		}
	}
	
	public void warn(Marker marker, String msg) {
		Logger logger = getLogger();
		if(logger.isWarnEnabled(marker)) {
			logger.warn(marker,msg);
		}
	}

	
	public void warn(Marker marker, String format, Object arg) {
		Logger logger = getLogger();
		if(logger.isWarnEnabled(marker)) {
			logger.warn(marker, format,arg);
		}
	}

	
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		Logger logger = getLogger();
		if(logger.isWarnEnabled(marker)) {
			logger.warn(marker, format,arg1,arg2);
		}
	}

	
	public void warn(Marker marker, String format, Object... arguments) {
		Logger logger = getLogger();
		if(logger.isWarnEnabled(marker)) {
			logger.warn(marker, format, arguments);
		}
	}
	
	public void warn(Marker marker, String msg, Throwable t) {
		Logger logger = getLogger();
		if(logger.isWarnEnabled(marker)) {
			logger.warn(marker, msg, t);
		}
	}

	
	public void error(String msg) {
		Logger logger = getLogger();
		if(logger.isErrorEnabled()) {
			logger.error(msg);
		}
	}

	
	public void error(String format, Object arg) {
		Logger logger = getLogger();
		if(logger.isErrorEnabled()) {
			logger.error(format,arg);
		}
	}

	
	public void error(String format, Object arg1, Object arg2) {
		Logger logger = getLogger();
		if(logger.isErrorEnabled()) {
			logger.error(format,arg1,arg2);
		}
	}

	
	public void error(String format, Object... arguments) {
		Logger logger = getLogger();
		if(logger.isErrorEnabled()) {
			logger.error(format,arguments);
		}
	}

	
	public void error(String msg, Throwable t) {
		Logger logger = getLogger();
		if(logger.isErrorEnabled()) {
			logger.error(msg,t);
		}
	}

	
	public void error(Marker marker, String msg) {
		Logger logger = getLogger();
		if(logger.isErrorEnabled(marker)) {
			logger.error(marker,msg);
		}
	}

	
	public void error(Marker marker, String format, Object arg) {
		Logger logger = getLogger();
		if(logger.isErrorEnabled(marker)) {
			logger.error(marker,format,arg);
		}
	}

	
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		Logger logger = getLogger();
		if(logger.isErrorEnabled(marker)) {
			logger.error(marker,format,arg1,arg2);
		}
	}

	
	public void error(Marker marker, String format, Object... arguments) {
		Logger logger = getLogger();
		if(logger.isErrorEnabled(marker)) {
			logger.error(marker,format,arguments);
		}
	}
	
	public void error(Marker marker, String msg, Throwable t) {
		Logger logger = getLogger();
		if(logger.isErrorEnabled(marker)) {
			logger.error(marker,msg,t);
		}
	}
	
	private static Logger getLogger() {
		StackTraceElement[] stacks = new RuntimeException().getStackTrace();
		StackTraceElement caller;
		if(stacks.length >= 3) {
			caller = stacks[2];
		}
		else {
			caller = stacks[stacks.length - 1];
		}
		String className = caller.getClassName();
		Logger logger = LOGGER_MAP.get(caller.getClassName());
		if(logger == null) {
			logger = LoggerFactory.getLogger(className);
			LOGGER_MAP.put(className, logger);
		}
		return logger;
	}

	@Override
	public boolean isTraceEnabled() {
		return getLogger().isTraceEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return getLogger().isDebugEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return getLogger().isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return getLogger().isWarnEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return getLogger().isErrorEnabled();
	}

	@Override
	public String getTraceId() {
		return null;
	}
}
