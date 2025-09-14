package com.langwuyue.orange.zookeeper.context;

import java.lang.reflect.Method;

import com.langwuyue.orange.zookeeper.OrangeZookeeperException;
import com.langwuyue.orange.zookeeper.annotation.OrangeContextFieldValue;
import com.langwuyue.orange.zookeeper.annotations.OrangeZookeeperClient;
import com.langwuyue.orange.zookeeper.annotations.Version;
import com.langwuyue.orange.zookeeper.utils.OrangeReflectionUtils;

public class OrangeSetACLWithVersionContext extends OrangeSetACLContext {
	
	@OrangeContextFieldValue(binding = Version.class)
	private Object version;
	
	public OrangeSetACLWithVersionContext(
		ZNode zNode, 
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		OrangeZookeeperClient client
	) {
		super(zNode, operationOwner, operationMethod, args, client);
	}

	public Integer getVersion() {
		if(version == null) {
			throw new OrangeZookeeperException(String.format("The value of the parameter annotated with %s must not be null", Version.class));
		}
		if(!(version instanceof String && NUMBER.matcher(version.toString()).matches()) 
				&& !(OrangeReflectionUtils.isInteger(version.getClass()))) {
			throw new OrangeZookeeperException(String.format("The value of the parameter annotated with %s must be a number or a number string", Version.class));
		}
		return Integer.valueOf(version.toString());
	}
}
