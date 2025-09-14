package com.langwuyue.orange.zookeeper.enums;

import java.lang.annotation.Annotation;
import java.util.EnumMap;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.Watchable;
import org.apache.zookeeper.Watcher;

import com.langwuyue.orange.zookeeper.annotations.Create;
import com.langwuyue.orange.zookeeper.annotations.Delete;
import com.langwuyue.orange.zookeeper.annotations.GetACL;
import com.langwuyue.orange.zookeeper.annotations.GetChildren;
import com.langwuyue.orange.zookeeper.annotations.GetData;
import com.langwuyue.orange.zookeeper.annotations.GetStat;
import com.langwuyue.orange.zookeeper.annotations.SetACL;
import com.langwuyue.orange.zookeeper.annotations.SetData;
import com.langwuyue.orange.zookeeper.annotations.Sync;
import com.langwuyue.orange.zookeeper.annotations.Watch;

public enum OrangeOperationAnnotationMapEnum {

	/**
     * Corresponds to {@link CuratorFramework#create()}
     */
    CREATE(CuratorEventType.CREATE,Create.class),

    /**
     * Corresponds to {@link CuratorFramework#delete()}
     */
    DELETE(CuratorEventType.DELETE,Delete.class),

    /**
     * Corresponds to {@link CuratorFramework#checkExists()}
     */
    EXISTS(CuratorEventType.EXISTS,GetStat.class),

    /**
     * Corresponds to {@link CuratorFramework#getData()}
     */
    GET_DATA(CuratorEventType.GET_DATA,GetData.class),

    /**
     * Corresponds to {@link CuratorFramework#setData()}
     */
    SET_DATA(CuratorEventType.SET_DATA,SetData.class),

    /**
     * Corresponds to {@link CuratorFramework#getChildren()}
     */
    CHILDREN(CuratorEventType.CHILDREN,GetChildren.class),

    /**
     * Corresponds to {@link CuratorFramework#sync(String, Object)}
     */
    SYNC(CuratorEventType.SYNC,Sync.class),

    /**
     * Corresponds to {@link CuratorFramework#getACL()}
     */
    GET_ACL(CuratorEventType.GET_ACL,GetACL.class),

    /**
     * Corresponds to {@link CuratorFramework#setACL()}
     */
    SET_ACL(CuratorEventType.SET_ACL,SetACL.class),

    /**
     * Corresponds to {@link CuratorFramework#transaction()}
     */
    TRANSACTION(CuratorEventType.TRANSACTION,null),

    /**
     * Corresponds to {@link CuratorFramework#getConfig()}
     */
    GET_CONFIG(CuratorEventType.GET_CONFIG,null),

    /**
     * Corresponds to {@link CuratorFramework#reconfig()}
     */
    RECONFIG(CuratorEventType.RECONFIG,null),

    /**
     * Corresponds to {@link Watchable#usingWatcher(Watcher)} or {@link Watchable#watched()}
     */
    WATCHED(CuratorEventType.WATCHED,Watch.class),

    /**
     * Corresponds to {@link CuratorFramework#watches()} ()}
     */
    REMOVE_WATCHES(CuratorEventType.REMOVE_WATCHES,null),

    /**
     * Event sent when client is being closed
     */
    CLOSING(CuratorEventType.CLOSING,null),
    
    ;
    
    private CuratorEventType type;
    
    private Class<? extends Annotation> operationAnnotationClass;
    
    private static final Map<CuratorEventType, OrangeOperationAnnotationMapEnum> typeMap = new EnumMap<>(CuratorEventType.class);
    
    static {
    	for(OrangeOperationAnnotationMapEnum curatorEventType2OperationAnnotationEnum : OrangeOperationAnnotationMapEnum.values()) {
    		typeMap.put(curatorEventType2OperationAnnotationEnum.getType(), curatorEventType2OperationAnnotationEnum);
    	}
    }

	private OrangeOperationAnnotationMapEnum(CuratorEventType type,
			Class<? extends Annotation> operationAnnotationClass) {
		this.type = type;
		this.operationAnnotationClass = operationAnnotationClass;
	}

	public CuratorEventType getType() {
		return type;
	}

	public Class<? extends Annotation> getOperationAnnotationClass() {
		return operationAnnotationClass;
	}
	
	public static OrangeOperationAnnotationMapEnum getBy(CuratorEventType type) {
		return typeMap.get(type);
	} 
    
    
}
