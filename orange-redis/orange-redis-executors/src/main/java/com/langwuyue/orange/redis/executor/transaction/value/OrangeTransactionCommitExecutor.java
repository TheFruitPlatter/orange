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
package com.langwuyue.orange.redis.executor.transaction.value;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.redis.RedisValueTypeEnum;
import com.langwuyue.orange.redis.annotation.transaction.Commit;
import com.langwuyue.orange.redis.annotation.transaction.Version;
import com.langwuyue.orange.redis.context.OrangeRedisContext;
import com.langwuyue.orange.redis.executor.OrangeRedisAbstractExecutor;
import com.langwuyue.orange.redis.executor.script.ScriptConstants;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisDefaultTransactionManager;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionCommitExecutor;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionKeyConstants;
import com.langwuyue.orange.redis.executor.transaction.context.OrangeTransactionVersionContext;
import com.langwuyue.orange.redis.logger.OrangeRedisLogger;
import com.langwuyue.orange.redis.mapping.OrangeRedisExecutorIdGenerator;
import com.langwuyue.orange.redis.operations.OrangeRedisScriptOperations;
import com.langwuyue.orange.redis.utils.OrangeCollectionUtils;

/**
 * Executor for manually committing Redis transactions using CAS (Compare-And-Swap) mechanism.
 * 
 * <p>This executor handles the manual commit phase of Redis transactions by atomically updating
 * the current effective version of data. It implements an optimistic locking strategy using
 * version numbers to ensure data consistency without requiring explicit locks.
 * 
 * <p>The commit process works as follows:
 * <ol>
 *   <li>Verifies that the version to be committed exists in the Redis hash structure</li>
 *   <li>Compares the new version number with the current effective version</li>
 *   <li>Only updates the effective version if the new version is greater than the current one</li>
 *   <li>Performs the entire operation atomically using a Lua script</li>
 * </ol>
 * 
 * <p>This approach ensures that:
 * <ul>
 *   <li>Only newer versions can become the effective version</li>
 *   <li>Concurrent commits are handled safely without race conditions</li>
 *   <li>The version update operation is atomic and consistent</li>
 * </ul>
 * 
 * <p>The executor supports methods annotated with {@link Commit} and {@link Version} annotations,
 * and notifies the transaction manager when a manual commit has been performed.
 *
 * @author Liang.Zhong
 * @since 1.0.0
 * @see <a href="https://orange.langwuyue.com/redis/advanced/transaction">Orange Redis Transaction Documentation</a>
 */
public class OrangeTransactionCommitExecutor extends OrangeRedisAbstractExecutor implements OrangeRedisTransactionCommitExecutor {
	/**
	 * Lua script that implements the CAS (Compare-And-Swap) mechanism for version control.
	 * 
	 * <p>The script performs the following steps atomically:
	 * <ol>
	 *   <li>Constructs the version key using the prefix and new version number</li>
	 *   <li>Verifies the existence of the new version in the hash structure</li>
	 *   <li>Retrieves the current effective version</li>
	 *   <li>Updates to the new version if:</li>
	 *     <ul>
	 *       <li>No current version exists (first commit), or</li>
	 *       <li>The new version is greater than the current version</li>
	 *     </ul>
	 * </ol>
	 * 
	 * <p>Returns:
	 * <ul>
	 *   <li>'1' - Commit successful (version was updated)</li>
	 *   <li>'0' - Commit failed (version doesn't exist or is not newer)</li>
	 * </ul>
	 */
	private static final String COMMIT_LUA_SCRIPT = String.join("\n",
		"local valueKeyPrefix = ARGV[3];",
	    "local newValue = ARGV[2];",
	    "local newNumber = tonumber(newValue);",
	    "local hashKey = ARGV[1];",
	    "local key = KEYS[1];",
	    "local valueKey = tostring(valueKeyPrefix) .. tostring(newValue);",
	    "local exists = redis.call('HEXISTS', key, valueKey);",
	    "if exists == 0 then",
	    "    return '0';",
	    "end",
	    "local currentValue = redis.call('HGET', key, hashKey);",
	    "if currentValue == nil or currentValue == false then",
	    "    redis.call('HSET', key, hashKey, newValue);",
	    "    return '1';",
	    "else",
	    "    if tonumber(currentValue) < newNumber then",
	    "        redis.call('HSET', key, hashKey, newValue);",
	    "        return '1';",
	    "    else",
	    "        return '0';",
	    "    end;",
	    "end;"
	);
	/**
	 * Debug version of the CAS Lua script with extensive logging.
	 * 
	 * <p>This version includes detailed Redis log statements at each step of execution,
	 * providing visibility into:
	 * <ul>
	 *   <li>Input parameters and their values</li>
	 *   <li>Key construction and manipulation</li>
	 *   <li>Version existence checks</li>
	 *   <li>Current version retrieval</li>
	 *   <li>Version comparison results</li>
	 *   <li>Commit decision points</li>
	 * </ul>
	 * 
	 * <p>The script uses a traceId parameter to correlate log entries across multiple
	 * Redis operations, making it easier to track transaction flow in complex scenarios.
	 * 
	 * <p>This script is only used when debug logging is enabled.
	 */
	private static final String COMMIT_LUA_SCRIPT_DEBUG = String.join("\n",
		"local traceId = tostring(ARGV[4]);",
		"redis.log(redis.LOG_NOTICE, string.format('traceId: %s, VALUE Transaction OrangeTransactionCommitExecutor executing', traceId));",
		"local valueKeyPrefix = ARGV[3];",
		"redis.log(redis.LOG_NOTICE, string.format('traceId: %s, valueKeyPrefix: %s', traceId, tostring(valueKeyPrefix)));",
	    "local newValue = ARGV[2];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, valueKeyPrefix: %s', traceId, tostring(newValue)));",
	    "local hashKey = ARGV[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, hashKey: %s', traceId, tostring(hashKey)));",
	    "local key = KEYS[1];",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, key: %s', traceId, tostring(key)));",
	    "local valueKey = tostring(valueKeyPrefix) .. tostring(newValue);",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, valueKey: %s', traceId, valueKey));",
	    "local exists = redis.call('HEXISTS', key, valueKey);",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, whether the new version exists: %s', traceId, tostring(exists)));",
	    "if exists == 0 then",
	    "    return '0';",
	    "end",
	    "local currentValue = redis.call('HGET', key, hashKey);",
	    "redis.log(redis.LOG_NOTICE, string.format('traceId: %s, currentValue: %s', traceId, tostring(currentValue)));",
	    "if currentValue == nil or currentValue == false then",
	    "    redis.log(redis.LOG_NOTICE, string.format('traceId: %s, currentValue is nil', traceId));",
	    "    redis.call('HSET', key, hashKey, newValue);",
	    "    return '1';",
	    "else",
	    "    if tonumber(currentValue) < tonumber(newValue) then",
	    "        redis.log(redis.LOG_NOTICE, string.format('traceId: %s, new version committed', traceId));",
	    "        redis.call('HSET', key, hashKey, newValue);",
	    "        return '1';",
	    "    else",
	    "        return '0';",
	    "    end;",
	    "end;"
	);
	/**
	 * Operations for executing Redis Lua scripts.
	 * 
	 * <p>This component provides:
	 * <ul>
	 *   <li>Atomic execution of complex multi-step operations via Lua scripts</li>
	 *   <li>Type-safe conversion between Java and Redis data types</li>
	 *   <li>Consistent error handling and result mapping</li>
	 *   <li>Script caching and optimization for improved performance</li>
	 * </ul>
	 * 
	 * <p>Used to execute the atomic CAS-based version checking and commit script.
	 * This component ensures that the entire version comparison and update process
	 * is performed as a single atomic operation in Redis, preventing race conditions
	 * in concurrent transaction scenarios.
	 * 
	 * <p>The script operations handle both the standard and debug versions of the
	 * commit script, selecting the appropriate one based on the current logging level.
	 */
	private OrangeRedisScriptOperations scriptOperations;
	
	/**
	 * Transaction manager that handles the overall transaction lifecycle.
	 * 
	 * <p>This component is responsible for:
	 * <ul>
	 *   <li>Maintaining the overall transaction state</li>
	 *   <li>Coordinating version updates across different operations</li>
	 *   <li>Handling cleanup of old versions after successful commits</li>
	 *   <li>Managing transaction boundaries and isolation</li>
	 * </ul>
	 * 
	 * <p>This executor notifies the manager when a commit has been manually performed,
	 * allowing it to update internal state and perform any necessary post-commit operations.
	 */
	private OrangeRedisDefaultTransactionManager transactionManager;
	
	/**
	 * Logger for recording transaction operations and debugging information.
	 * 
	 * <p>This component provides:
	 * <ul>
	 *   <li>Detailed transaction operation logging</li>
	 *   <li>Trace ID generation and propagation for distributed tracing</li>
	 *   <li>Debug mode control for Lua script execution</li>
	 *   <li>Performance monitoring and troubleshooting capabilities</li>
	 * </ul>
	 * 
	 * <p>When debug mode is enabled, the Lua script includes extensive logging
	 * statements that help track the execution flow and state changes during
	 * transaction commits.
	 */
	private OrangeRedisLogger logger;

	/**
	 * Constructs a new transaction commit executor with the required dependencies.
	 * 
	 * <p>This constructor initializes the executor with:
	 * <ul>
	 *   <li>Script operations for executing the atomic CAS Lua scripts</li>
	 *   <li>ID generator for creating unique executor identifiers</li>
	 *   <li>Logger for transaction tracing and debugging</li>
	 * </ul>
	 * 
	 * <p>Note that the transaction manager is not provided in the constructor
	 * but is instead injected later via {@link #setTransactionManager(OrangeRedisDefaultTransactionManager)}
	 * to avoid circular dependencies in the Spring context.
	 *
	 * @param scriptOperations Operations for executing Redis Lua scripts
	 * @param idGenerator Generator for creating unique executor identifiers
	 * @param logger Logger for transaction tracing and debugging
	 */
	public OrangeTransactionCommitExecutor(
		OrangeRedisScriptOperations scriptOperations,
		OrangeRedisExecutorIdGenerator idGenerator,
		OrangeRedisLogger logger
	) {
		super(idGenerator);
		this.scriptOperations = scriptOperations;
		this.logger = logger;
	}

	/**
	 * Executes the transaction commit operation using the provided Redis context.
	 * 
	 * <p>This method serves as the entry point for the commit operation and:
	 * <ul>
	 *   <li>Safely casts the generic context to the specific transaction version context</li>
	 *   <li>Extracts the Redis key and version number from the context</li>
	 *   <li>Delegates to the {@link #commit(String, Long)} method to perform the atomic CAS operation</li>
	 *   <li>Returns the result of the commit operation to the caller</li>
	 * </ul>
	 * 
	 * <p>This method is typically invoked by the framework when a method annotated with
	 * {@link Commit} is called, allowing for declarative transaction commits in application code.
	 *
	 * @param context The Redis context containing transaction information
	 * @return Boolean result indicating whether the commit was successful
	 * @throws Exception if an error occurs during the commit operation, such as Redis connection issues
	 *                   or invalid context parameters
	 */
	@Override
	public Object execute(OrangeRedisContext context) throws Exception {
		OrangeTransactionVersionContext ctx = (OrangeTransactionVersionContext) context;
		return commit(ctx.getRedisKey().getValue(),ctx.getVersion());
	}

	/**
	 * Returns the list of annotation classes that this executor supports.
	 * 
	 * <p>This executor handles methods annotated with:
	 * <ul>
	 *   <li>{@link Commit} - For manual transaction commit operations</li>
	 *   <li>{@link Version} - For version-related operations in transactions</li>
	 * </ul>
	 *
	 * @return List of supported annotation classes
	 */
	@Override
	protected List<Class<? extends Annotation>> getSupportedAnnotationClasses() {
		return OrangeCollectionUtils.asList(Commit.class,Version.class);
	}
	
	/**
	 * Returns the context class that this executor requires for operation.
	 * 
	 * <p>This executor specifically works with {@link OrangeTransactionVersionContext},
	 * which provides:
	 * <ul>
	 *   <li>Access to the Redis key that stores the versioned data</li>
	 *   <li>The version number to be committed</li>
	 *   <li>Other transaction-related metadata needed for the commit operation</li>
	 * </ul>
	 *
	 * @return The context class required by this executor
	 */
	@Override
	public Class<? extends OrangeRedisContext> getContextClass() {
		return OrangeTransactionVersionContext.class;
	}

	/**
	 * Commits a transaction by atomically updating the current effective version using CAS mechanism.
	 * 
	 * <p>This method executes a Lua script that performs the following operations atomically:
	 * <ol>
	 *   <li>Verifies that the specified version exists in the Redis hash structure</li>
	 *   <li>Retrieves the current effective version from Redis</li>
	 *   <li>Compares the new version with the current effective version</li>
	 *   <li>Updates the effective version only if the new version is greater</li>
	 * </ol>
	 * 
	 * <p>After execution, it notifies the transaction manager that a manual commit has been performed.
	 *
	 * @param key The Redis key that stores the hash structure containing versioned data
	 * @param version The version number to commit as the new effective version
	 * @return true if the commit was successful (version was updated), false otherwise
	 *         (e.g., if the version doesn't exist or is not newer than the current version)
	 * @throws Exception if an error occurs during the Redis operation
	 */
	@Override
	public boolean commit(String key,Long version) throws Exception {
		Map<Object, RedisValueTypeEnum> argsValueTypes = new LinkedHashMap<>();
		argsValueTypes.put(OrangeRedisTransactionKeyConstants.CURRENT_VERSION, RedisValueTypeEnum.STRING);
		argsValueTypes.put(OrangeRedisTransactionKeyConstants.VERSION_PREFIX, RedisValueTypeEnum.STRING);
		argsValueTypes.put(version, RedisValueTypeEnum.LONG);
		// Change script when debug is enabled.
		String script = COMMIT_LUA_SCRIPT;
		if(this.logger.isDebugEnabled()) {
			script = COMMIT_LUA_SCRIPT_DEBUG;
		}
		// Id for trace 
		if(this.logger.getTraceId() == null) {
			argsValueTypes.put(ScriptConstants.NIL, RedisValueTypeEnum.STRING);
		}else{
			argsValueTypes.put(this.logger.getTraceId(), RedisValueTypeEnum.STRING);
		}
		Object result = this.scriptOperations.execute(
			script, 
			argsValueTypes, 
			RedisValueTypeEnum.STRING, 
			String.class,
			OrangeCollectionUtils.asList(key), 
			OrangeRedisTransactionKeyConstants.CURRENT_VERSION,
			version,
			OrangeRedisTransactionKeyConstants.VERSION_PREFIX,
			this.logger.getTraceId() == null ?  ScriptConstants.NIL : this.logger.getTraceId()
		);
		transactionManager.alreadyCommittedManually();
		return "1".equals(result);
	}

	/**
	 * Sets the transaction manager for this executor.
	 * 
	 * <p>This method is called by the Spring container during dependency injection to:
	 * <ul>
	 *   <li>Establish the bidirectional relationship between executor and manager</li>
	 *   <li>Enable transaction state notifications from executor to manager</li>
	 *   <li>Allow the executor to inform the manager about manual commit operations</li>
	 * </ul>
	 * 
	 * <p>The transaction manager is a critical component that coordinates the overall
	 * transaction lifecycle and ensures proper cleanup of transaction resources.
	 *
	 * @param transactionManager The transaction manager to be used by this executor
	 */
	public void setTransactionManager(OrangeRedisDefaultTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}