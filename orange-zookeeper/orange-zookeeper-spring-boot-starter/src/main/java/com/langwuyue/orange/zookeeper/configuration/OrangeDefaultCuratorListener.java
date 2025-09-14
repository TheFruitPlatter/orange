package com.langwuyue.orange.zookeeper.configuration;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;

import com.langwuyue.orange.zookeeper.BackgroundEvent;
import com.langwuyue.orange.zookeeper.OrangeZookeeperBackgroundCallback;
import com.langwuyue.orange.zookeeper.OrangeZookeeperDataSerializer;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;
import com.langwuyue.orange.zookeeper.enums.OrangeOperationAnnotationMapEnum;

public class OrangeDefaultCuratorListener implements CuratorListener {
	
	private Map<String,Map<Class<? extends Annotation>,List<OrangeZookeeperBackgroundCallback>>> callbackMap;
	
	private OrangeZookeeperDataSerializer dataSerializer;
	
	public OrangeDefaultCuratorListener(OrangeZookeeperDataSerializer dataSerializer,List<OrangeZookeeperBackgroundCallback> callbacks) {
		this.dataSerializer = dataSerializer;
		if(callbacks != null) {
			this.callbackMap = new HashMap<>();
			for(OrangeZookeeperBackgroundCallback callback : callbacks) {
				Class<?> targetClass = callback.getTargetClass();
				Class<? extends Annotation> operationAnnotationClass = callback.getOperationAnnotationClass();
				
			}
		}
	}
	
	@Override
	public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
		OrangeZookeeperContext context = (OrangeZookeeperContext) event.getContext();
		Map<Class<? extends Annotation>, List<OrangeZookeeperBackgroundCallback>> operationCallbackMap = callbackMap.get(context.getZNode().getOriginPath());
		if(operationCallbackMap == null || operationCallbackMap.isEmpty()) {
			return;
		}
		Class<? extends Annotation> annotationClass = OrangeOperationAnnotationMapEnum.getBy(event.getType()).getOperationAnnotationClass();
		if(annotationClass == null) {
			// Not support!
			return;
		}
		List<OrangeZookeeperBackgroundCallback> callbacks = operationCallbackMap.get(annotationClass);
		if(callbacks == null || callbacks.isEmpty()) {
			return;
		}
		for(OrangeZookeeperBackgroundCallback callback : callbacks) {
			BackgroundEvent backgroundEvent = new OrangeDefaultBackgroundEvent(
				event,
				annotationClass,
				context.getArgs(),
				this.dataSerializer,
				callback.getDataType(),
				context.getClient().dataType()
			);
			callback.processResult(backgroundEvent);
		}
	}
}
