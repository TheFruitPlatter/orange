package com.langwuyue.orange.zookeeper.mapping;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.langwuyue.orange.zookeeper.OrangeZookeeperException;
import com.langwuyue.orange.zookeeper.annotations.Callback;
import com.langwuyue.orange.zookeeper.annotations.CallbackContext;
import com.langwuyue.orange.zookeeper.annotations.Create;
import com.langwuyue.orange.zookeeper.annotations.Data;
import com.langwuyue.orange.zookeeper.annotations.Delete;
import com.langwuyue.orange.zookeeper.annotations.GetACL;
import com.langwuyue.orange.zookeeper.annotations.GetChildren;
import com.langwuyue.orange.zookeeper.annotations.GetData;
import com.langwuyue.orange.zookeeper.annotations.GetStat;
import com.langwuyue.orange.zookeeper.annotations.InBackground;
import com.langwuyue.orange.zookeeper.annotations.Perms;
import com.langwuyue.orange.zookeeper.annotations.SetACL;
import com.langwuyue.orange.zookeeper.annotations.SetData;
import com.langwuyue.orange.zookeeper.annotations.Sync;
import com.langwuyue.orange.zookeeper.annotations.Version;
import com.langwuyue.orange.zookeeper.annotations.Watch;
import com.langwuyue.orange.zookeeper.annotations.Watcher;


public class OrangeDefaultZookeeperExecutorIdGenerator implements OrangeZookeeperExecutorIdGenerator {
	
	private List<Class<? extends Annotation>> supportedAnnotationClasses;
	
	private Map<Class<? extends Annotation>,Integer> supportedAnnotationClassesIdMap;
	
	public OrangeDefaultZookeeperExecutorIdGenerator() {
		this.supportedAnnotationClasses = new ArrayList<>(64);
		this.supportedAnnotationClassesIdMap = new LinkedHashMap<>(64);
	}

	@Override
	public long generate(List<Class<? extends Annotation>> annotationClasses) {
		long id = 0;
		for(Class<? extends Annotation> annotationClass : annotationClasses) {
			long index = getSupportedAnnotationIndex(annotationClass);
			id = id ^ (1 << index);
		}
		return id;
	}
	
	private int getSupportedAnnotationIndex(Class<? extends Annotation> annotationClass) {
		Integer index = supportedAnnotationClassesIdMap.get(annotationClass);
		if(index == null) {
			throw new OrangeZookeeperException(String.format("The annotation @%s is not supported", annotationClass));
		}
		return index;
	}

	@Override
	public void initExecutorIdGenerator() {
		this.supportedAnnotationClasses.add(Create.class);
		this.supportedAnnotationClasses.add(SetData.class);
		this.supportedAnnotationClasses.add(Data.class);
		this.supportedAnnotationClasses.add(SetACL.class);
		this.supportedAnnotationClasses.add(Perms.class);
		this.supportedAnnotationClasses.add(Version.class);
		this.supportedAnnotationClasses.add(Delete.class);
		this.supportedAnnotationClasses.add(GetData.class);
		this.supportedAnnotationClasses.add(GetACL.class);
		this.supportedAnnotationClasses.add(GetStat.class);
		this.supportedAnnotationClasses.add(GetChildren.class);
		this.supportedAnnotationClasses.add(Sync.class);
		this.supportedAnnotationClasses.add(InBackground.class);
		this.supportedAnnotationClasses.add(Callback.class);
		this.supportedAnnotationClasses.add(CallbackContext.class);
		this.supportedAnnotationClasses.add(Watch.class);
		this.supportedAnnotationClasses.add(Watcher.class);
		
		for(int i = 0; i < this.supportedAnnotationClasses.size(); i++) {
			this.supportedAnnotationClassesIdMap.put(this.supportedAnnotationClasses.get(i), i);
		}
	}
}
