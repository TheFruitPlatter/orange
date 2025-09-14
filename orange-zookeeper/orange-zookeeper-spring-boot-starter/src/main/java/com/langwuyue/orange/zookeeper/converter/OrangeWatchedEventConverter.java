package com.langwuyue.orange.zookeeper.converter;

import org.apache.zookeeper.WatchedEvent;

import com.langwuyue.orange.zookeeper.enums.OrangeEventTypeMapEnum;
import com.langwuyue.orange.zookeeper.enums.OrangeKeeperStateMapEnum;

public class OrangeWatchedEventConverter {

	public com.langwuyue.orange.zookeeper.WatchedEvent convert(WatchedEvent watchedEvent) {
		return new com.langwuyue.orange.zookeeper.WatchedEvent(
			OrangeEventTypeMapEnum.getBy(watchedEvent.getType()),
			OrangeKeeperStateMapEnum.getBy(watchedEvent.getState()),
			watchedEvent.getPath()
		);
	}
}
