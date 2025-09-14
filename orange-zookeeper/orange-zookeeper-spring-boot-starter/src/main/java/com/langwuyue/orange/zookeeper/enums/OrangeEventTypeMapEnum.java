package com.langwuyue.orange.zookeeper.enums;

import java.util.EnumMap;
import java.util.Map;

import org.apache.zookeeper.Watcher.Event.EventType;

import com.langwuyue.orange.zookeeper.EventTypeEnum;

public enum OrangeEventTypeMapEnum {
	
	None(EventType.None,EventTypeEnum.None),
    NodeCreated(EventType.NodeCreated,EventTypeEnum.NodeCreated),
    NodeDeleted(EventType.NodeDeleted,EventTypeEnum.NodeDeleted),
    NodeDataChanged(EventType.NodeDataChanged,EventTypeEnum.NodeDataChanged),
    NodeChildrenChanged(EventType.NodeChildrenChanged,EventTypeEnum.NodeChildrenChanged),
    DataWatchRemoved(EventType.DataWatchRemoved,EventTypeEnum.DataWatchRemoved),
    ChildWatchRemoved(EventType.ChildWatchRemoved,EventTypeEnum.ChildWatchRemoved),
	;
	
	private EventType eventType;
	
	private EventTypeEnum orangeEventTypeEnum;
	
	private static Map<EventType, EventTypeEnum> MAP = new EnumMap<>(EventType.class);
	
	static {
		for(OrangeEventTypeMapEnum zNodeType2CreateModeEnum : OrangeEventTypeMapEnum.values()) {
			MAP.put(zNodeType2CreateModeEnum.eventType, zNodeType2CreateModeEnum.orangeEventTypeEnum);
		}
	}

	
	private OrangeEventTypeMapEnum(EventType eventType, EventTypeEnum orangeEventTypeEnum) {
		this.eventType = eventType;
		this.orangeEventTypeEnum = orangeEventTypeEnum;
	}
	

	public EventType getEventType() {
		return eventType;
	}

	public EventTypeEnum getOrangeEventTypeEnum() {
		return orangeEventTypeEnum;
	}

	public static EventTypeEnum getBy(EventType type) {
		return MAP.get(type);
	}
}
