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

import org.slf4j.Marker;


/**
 * @author Liang.Zhong
 * @since 1.0.0
 */
public interface OrangeRedisLogger {
	
	boolean isTraceEnabled();
	
	boolean isDebugEnabled();
	
	boolean isInfoEnabled();
	
	boolean isWarnEnabled();
	
	boolean isErrorEnabled();
	
	void trace(String msg);

	void trace(String format, Object arg);

	void trace(String format, Object arg1, Object arg2);

	void trace(String format, Object... arguments);

	void trace(String msg, Throwable t);

	void trace(Marker marker, String msg);

	void trace(Marker marker, String format, Object arg);

	void trace(Marker marker, String format, Object arg1, Object arg2);

	void trace(Marker marker, String format, Object... argArray);

	void trace(Marker marker, String msg, Throwable t);

	void debug(String msg);

	void debug(String format, Object arg);

	void debug(String format, Object arg1, Object arg2);

	void debug(String format, Object... arguments);

	void debug(String msg, Throwable t);

	void debug(Marker marker, String msg);

	void debug(Marker marker, String format, Object arg);

	void debug(Marker marker, String format, Object arg1, Object arg2);

	void debug(Marker marker, String format, Object... arguments);

	void debug(Marker marker, String msg, Throwable t);

	void info(String msg);

	void info(String format, Object arg);

	void info(String format, Object arg1, Object arg2);

	void info(String format, Object... arguments);

	void info(String msg, Throwable t);

	void info(Marker marker, String msg);

	void info(Marker marker, String format, Object arg);

	void info(Marker marker, String format, Object arg1, Object arg2);

	void info(Marker marker, String format, Object... arguments);

	void info(Marker marker, String msg, Throwable t);

	void warn(String msg);

	void warn(String format, Object arg);

	void warn(String format, Object... arguments);

	void warn(String format, Object arg1, Object arg2);

	void warn(String msg, Throwable t);

	void warn(Marker marker, String msg);

	void warn(Marker marker, String format, Object arg);

	void warn(Marker marker, String format, Object arg1, Object arg2);

	void warn(Marker marker, String format, Object... arguments);

	void warn(Marker marker, String msg, Throwable t);

	void error(String msg);

	void error(String format, Object arg);

	void error(String format, Object arg1, Object arg2);

	void error(String format, Object... arguments);

	void error(String msg, Throwable t);

	void error(Marker marker, String msg);

	void error(Marker marker, String format, Object arg);

	void error(Marker marker, String format, Object arg1, Object arg2);

	void error(Marker marker, String format, Object... arguments);

	void error(Marker marker, String msg, Throwable t);

	String getTraceId();

}
