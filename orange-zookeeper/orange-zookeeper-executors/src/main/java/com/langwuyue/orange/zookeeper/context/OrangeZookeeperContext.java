package com.langwuyue.orange.zookeeper.context;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.langwuyue.orange.zookeeper.DataTypeEnum;
import com.langwuyue.orange.zookeeper.ZNodeTypeEnum;
import com.langwuyue.orange.zookeeper.annotations.OrangeZookeeperClient;

public class OrangeZookeeperContext {
	
	public static final Pattern NUMBER = Pattern.compile("\\d+");
	
	private ZNode zNode;
	
	private Class<?> operationOwner;
	
	private Method operationMethod;
	
	private Object[] args;
	
	private OrangeZookeeperClient client;
	
	public OrangeZookeeperContext(
		ZNode zNode, 
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		OrangeZookeeperClient client
	) {
		super();
		this.zNode = zNode;
		this.operationOwner = operationOwner;
		this.operationMethod = operationMethod;
		this.args = args;
		this.client = client;
	}


	public static OrangeZookeeperContext newInstance(
		Class<? extends OrangeZookeeperContext> contextClass,
		ZNode zNode, 
		Class<?> operationOwner, 
		Method operationMethod, 
		Object[] args,
		OrangeZookeeperClient client
	) throws Exception {
		return contextClass.getConstructor(
					ZNode.class,
					Class.class,
					Method.class,
					Object[].class,
					DataTypeEnum.class
		).newInstance(zNode,operationOwner,operationMethod,args,client);
	}
	

	public ZNode getZNode() {
		return zNode;
	}

	public void setZNode(ZNode zNode) {
		this.zNode = zNode;
	}

	public Class<?> getOperationOwner() {
		return operationOwner;
	}

	public void setOperationOwner(Class<?> operationOwner) {
		this.operationOwner = operationOwner;
	}

	public Method getOperationMethod() {
		return operationMethod;
	}

	public void setOperationMethod(Method operationMethod) {
		this.operationMethod = operationMethod;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public OrangeZookeeperClient getClient() {
		return client;
	}

	public void setClient(OrangeZookeeperClient client) {
		this.client = client;
	}

	public static class ZNode {
		
		private String originPath;
		private String path;
		private long ttl;
		private TimeUnit ttlUnit;
		private ZNodeTypeEnum zNodeType;
		
		public ZNode(String originPath, String path, long ttl, TimeUnit ttlUnit, ZNodeTypeEnum zNodeType) {
			super();
			this.originPath = originPath;
			this.path = path;
			this.ttl = ttl;
			this.ttlUnit = ttlUnit;
			this.zNodeType = zNodeType;
		}
		public String getOriginPath() {
			return originPath;
		}
		public void setOriginPath(String originPath) {
			this.originPath = originPath;
		}
		public String getPath() {
			return path;
		}
		public void setPath(String path) {
			this.path = path;
		}
		public long getTtl() {
			return ttl;
		}
		public void setTtl(long ttl) {
			this.ttl = ttl;
		}
		public TimeUnit getTtlUnit() {
			return ttlUnit;
		}
		public void setTtlUnit(TimeUnit ttlUnit) {
			this.ttlUnit = ttlUnit;
		}
		public ZNodeTypeEnum getZNodeType() {
			return zNodeType;
		}
		public void setZNodeType(ZNodeTypeEnum zNodeType) {
			this.zNodeType = zNodeType;
		}
	}
}
