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

import java.time.Duration;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.langwuyue.orange.redis.OrangeRedisOutOfServiceProperties;
import com.langwuyue.orange.redis.executor.transaction.OrangeRedisTransactionProperties;
import com.langwuyue.orange.redis.registry.OrangeKeyNamingFormatCheckLevel;
import com.langwuyue.orange.redis.registry.OrangeSlowOperationProperties;
import com.langwuyue.orange.redis.timer.OrangeAutoRenewProperties;

/**
 * copy {@code org.springframework.boot.autoconfigure.data.redis.RedisProperties}
 * 
 * @author Liang.Zhong
 * @since 1.0.0
 */
@Validated
@ConfigurationProperties(prefix = "orange.redis")
class OrangeRedisProperties {
	
	/**
	 * Database index used by the connection factory.
	 */
	private int database = 0;

	/**
	 * Connection URL. Overrides host, port, username, and password. Example:
	 * redis://user:password@example.com:6379
	 */
	private String url;

	/**
	 * Redis server host.
	 */
	private String host = "localhost";

	/**
	 * Login username of the redis server.
	 */
	@JsonSerialize(using = OrangeUserSerializer.class)
	private String username;

	/**
	 * Login password of the redis server.
	 */
	@JsonSerialize(using = OrangePasswordSerializer.class)
	private String password;

	/**
	 * Redis server port.
	 */
	private int port = 6379;

	/**
	 * Whether to enable SSL support.
	 */
	private boolean ssl;

	/**
	 * Read timeout.
	 */
	private Duration timeout;

	/**
	 * Connection timeout.
	 */
	private Duration connectTimeout;
	
	/**
	 * Validates a key's naming format against specified strictness levels:
	 * - 1: one key one operation
	 * - 2: variable must be last, include 1.
	 * - 3: cannot be similar after ignore variables, include 1,2.
	 * - 4: strict prefix (default), include 1,2,3.
	 * 
	 */
	private int keyCheckLevel = OrangeKeyNamingFormatCheckLevel.STRICT_PREFIX_MATCH;
	
	/**
	 * The prefix of the Redis key
	 */
	@NotBlank
	private String keyPrefix;
	
	/**
	 * Client name to be set on connections with CLIENT SETNAME.
	 */
	private String clientName;

	/**
	 * Type of client to use. By default, auto-detected according to the classpath.
	 */
	private ClientType clientType;
	
	private Sentinel sentinel;

	private Cluster cluster;
	
	private final Jedis jedis = new Jedis();

	private final Lettuce lettuce = new Lettuce();
	
	private final OrangeAutoRenewProperties autoRenew = new OrangeAutoRenewProperties();
	
	private final OrangeRedisTransactionProperties transaction = new OrangeRedisTransactionProperties();
	
	private final OrangeRedisOutOfServiceProperties outOfService = new OrangeRedisOutOfServiceProperties();
	
	private final OrangeSlowOperationProperties slowOperation = new OrangeSlowOperationProperties();
	
	private final Metrics metrics = new Metrics();
	
	public int getDatabase() {
		return this.database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isSsl() {
		return this.ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public void setTimeout(Duration timeout) {
		this.timeout = timeout;
	}

	public Duration getTimeout() {
		return this.timeout;
	}

	public Duration getConnectTimeout() {
		return this.connectTimeout;
	}

	public void setConnectTimeout(Duration connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public String getClientName() {
		return this.clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public ClientType getClientType() {
		return this.clientType;
	}

	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}

	public Sentinel getSentinel() {
		return this.sentinel;
	}

	public void setSentinel(Sentinel sentinel) {
		this.sentinel = sentinel;
	}

	public Cluster getCluster() {
		return this.cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public Jedis getJedis() {
		return this.jedis;
	}

	public Lettuce getLettuce() {
		return this.lettuce;
	}
	
	public int getKeyCheckLevel() {
		return keyCheckLevel;
	}

	public void setKeyCheckLevel(int keyCheckLevel) {
		this.keyCheckLevel = keyCheckLevel;
	}

	public OrangeAutoRenewProperties getAutoRenew() {
		return autoRenew;
	}
	
	public OrangeRedisTransactionProperties getTransaction() {
		return transaction;
	}

	public OrangeRedisOutOfServiceProperties getOutOfService() {
		return outOfService;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public OrangeSlowOperationProperties getSlowOperation() {
		return slowOperation;
	}
	
	public Metrics getMetrics() {
		return metrics;
	}

	/**
	 * Type of Redis client to use.
	 */
	public enum ClientType {

		/**
		 * Use the Lettuce redis client.
		 */
		LETTUCE,

		/**
		 * Use the Jedis redis client.
		 */
		JEDIS

	}

	/**
	 * Pool properties.
	 */
	public static class Pool {

		/**
		 * Whether to enable the pool. Enabled automatically if "commons-pool2" is
		 * available. With Jedis, pooling is implicitly enabled in sentinel mode and this
		 * setting only applies to single node setup.
		 */
		private Boolean enabled;

		/**
		 * Maximum number of "idle" connections in the pool. Use a negative value to
		 * indicate an unlimited number of idle connections.
		 */
		private int maxIdle = 8;

		/**
		 * Target for the minimum number of idle connections to maintain in the pool. This
		 * setting only has an effect if both it and time between eviction runs are
		 * positive.
		 */
		private int minIdle = 0;

		/**
		 * Maximum number of connections that can be allocated by the pool at a given
		 * time. Use a negative value for no limit.
		 */
		private int maxActive = 8;

		/**
		 * Maximum amount of time a connection allocation should block before throwing an
		 * exception when the pool is exhausted. Use a negative value to block
		 * indefinitely.
		 */
		private Duration maxWait = Duration.ofMillis(-1);

		/**
		 * Time between runs of the idle object evictor thread. When positive, the idle
		 * object evictor thread starts, otherwise no idle object eviction is performed.
		 */
		private Duration timeBetweenEvictionRuns;

		public Boolean getEnabled() {
			return this.enabled;
		}

		public void setEnabled(Boolean enabled) {
			this.enabled = enabled;
		}

		public int getMaxIdle() {
			return this.maxIdle;
		}

		public void setMaxIdle(int maxIdle) {
			this.maxIdle = maxIdle;
		}

		public int getMinIdle() {
			return this.minIdle;
		}

		public void setMinIdle(int minIdle) {
			this.minIdle = minIdle;
		}

		public int getMaxActive() {
			return this.maxActive;
		}

		public void setMaxActive(int maxActive) {
			this.maxActive = maxActive;
		}

		public Duration getMaxWait() {
			return this.maxWait;
		}

		public void setMaxWait(Duration maxWait) {
			this.maxWait = maxWait;
		}

		public Duration getTimeBetweenEvictionRuns() {
			return this.timeBetweenEvictionRuns;
		}

		public void setTimeBetweenEvictionRuns(Duration timeBetweenEvictionRuns) {
			this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
		}

	}

	/**
	 * Cluster properties.
	 */
	public static class Cluster {

		/**
		 * Comma-separated list of "host:port" pairs to bootstrap from. This represents an
		 * "initial" list of cluster nodes and is required to have at least one entry.
		 */
		private List<String> nodes;

		/**
		 * Maximum number of redirects to follow when executing commands across the
		 * cluster.
		 */
		private Integer maxRedirects;

		public List<String> getNodes() {
			return this.nodes;
		}

		public void setNodes(List<String> nodes) {
			this.nodes = nodes;
		}

		public Integer getMaxRedirects() {
			return this.maxRedirects;
		}

		public void setMaxRedirects(Integer maxRedirects) {
			this.maxRedirects = maxRedirects;
		}

	}

	/**
	 * Redis sentinel properties.
	 */
	public static class Sentinel {

		/**
		 * Name of the Redis server.
		 */
		private String master;

		/**
		 * Comma-separated list of "host:port" pairs.
		 */
		private List<String> nodes;

		/**
		 * Login username for authenticating with sentinel(s).
		 */
		private String username;

		/**
		 * Password for authenticating with sentinel(s).
		 */
		private String password;

		public String getMaster() {
			return this.master;
		}

		public void setMaster(String master) {
			this.master = master;
		}

		public List<String> getNodes() {
			return this.nodes;
		}

		public void setNodes(List<String> nodes) {
			this.nodes = nodes;
		}

		public String getUsername() {
			return this.username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return this.password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

	/**
	 * Jedis client properties.
	 */
	public static class Jedis {

		/**
		 * Jedis pool configuration.
		 */
		private final Pool pool = new Pool();

		public Pool getPool() {
			return this.pool;
		}

	}

	/**
	 * Lettuce client properties.
	 */
	public static class Lettuce {

		/**
		 * Shutdown timeout.
		 */
		private Duration shutdownTimeout = Duration.ofMillis(100);

		/**
		 * Lettuce pool configuration.
		 */
		private final Pool pool = new Pool();

		private final Cluster cluster = new Cluster();

		public Duration getShutdownTimeout() {
			return this.shutdownTimeout;
		}

		public void setShutdownTimeout(Duration shutdownTimeout) {
			this.shutdownTimeout = shutdownTimeout;
		}

		public Pool getPool() {
			return this.pool;
		}

		public Cluster getCluster() {
			return this.cluster;
		}

		public static class Cluster {

			private final Refresh refresh = new Refresh();

			public Refresh getRefresh() {
				return this.refresh;
			}

			public static class Refresh {

				/**
				 * Whether to discover and query all cluster nodes for obtaining the
				 * cluster topology. When set to false, only the initial seed nodes are
				 * used as sources for topology discovery.
				 */
				private boolean dynamicRefreshSources = true;

				/**
				 * Cluster topology refresh period.
				 */
				private Duration period;

				/**
				 * Whether adaptive topology refreshing using all available refresh
				 * triggers should be used.
				 */
				private boolean adaptive;

				public boolean isDynamicRefreshSources() {
					return this.dynamicRefreshSources;
				}

				public void setDynamicRefreshSources(boolean dynamicRefreshSources) {
					this.dynamicRefreshSources = dynamicRefreshSources;
				}

				public Duration getPeriod() {
					return this.period;
				}

				public void setPeriod(Duration period) {
					this.period = period;
				}

				public boolean isAdaptive() {
					return this.adaptive;
				}

				public void setAdaptive(boolean adaptive) {
					this.adaptive = adaptive;
				}

			}

		}
	}
	
	public static class Metrics {
		
		private Boolean enabled = Boolean.FALSE;

		public Boolean getEnabled() {
			return enabled;
		}

		public void setEnabled(Boolean enabled) {
			this.enabled = enabled;
		}
	}
}
