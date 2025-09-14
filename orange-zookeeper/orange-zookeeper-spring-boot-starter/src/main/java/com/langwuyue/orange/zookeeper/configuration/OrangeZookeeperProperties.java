package com.langwuyue.orange.zookeeper.configuration;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix="orange.zookeeper")
public class OrangeZookeeperProperties {

	private boolean enabled;
    
    private String connectionString;
    
    private String namespace;
    
    private Charset charset = StandardCharsets.UTF_8;
    
    private Duration sessionTimeout = Duration.ofSeconds(60);
    
    private Duration connectionTimeout = Duration.ofSeconds(15);
    
    private Duration closeTimeout = Duration.ofSeconds(1);
    
    private String defaultData = "";
    
    private boolean canBeReadOnly = false;
    
    private boolean useContainerParentsIfAvailable = true;
    
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getConnectionString() {
		return connectionString;
	}
	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public Charset getCharset() {
		return charset;
	}
	public void setCharset(Charset charset) {
		this.charset = charset;
	}
	public Duration getSessionTimeout() {
		return sessionTimeout;
	}
	public void setSessionTimeout(Duration sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	public Duration getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(Duration connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public Duration getCloseTimeout() {
		return closeTimeout;
	}
	public void setCloseTimeout(Duration closeTimeout) {
		this.closeTimeout = closeTimeout;
	}
	public String getDefaultData() {
		return defaultData;
	}
	public void setDefaultData(String defaultData) {
		this.defaultData = defaultData;
	}
	public boolean isCanBeReadOnly() {
		return canBeReadOnly;
	}
	public void setCanBeReadOnly(boolean canBeReadOnly) {
		this.canBeReadOnly = canBeReadOnly;
	}
	public boolean isUseContainerParentsIfAvailable() {
		return useContainerParentsIfAvailable;
	}
	public void setUseContainerParentsIfAvailable(boolean useContainerParentsIfAvailable) {
		this.useContainerParentsIfAvailable = useContainerParentsIfAvailable;
	}
}
