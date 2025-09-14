package com.langwuyue.orange.zookeeper;


/**
 *  A WatchedEvent represents a change on the ZooKeeper that a Watcher
 *  is able to respond to.  The WatchedEvent includes exactly what happened,
 *  the current state of the ZooKeeper, and the path of the znode that
 *  was involved in the event.
 */
public class WatchedEvent {

	private final KeeperStateEnum keeperState;
    private final EventTypeEnum eventType;
    private String path;

    /**
     * Create a WatchedEvent with specified type, state and path
     */
    public WatchedEvent(EventTypeEnum eventType, KeeperStateEnum keeperState, String path) {
        this.keeperState = keeperState;
        this.eventType = eventType;
        this.path = path;
    }

    /**
     * Convert a WatcherEvent sent over the wire into a full-fledged WatcherEvent
     */
    public WatchedEvent(WatcherEvent eventMessage) {
    	keeperState = KeeperStateEnum.fromInt(eventMessage.getState());
    	eventType = EventTypeEnum.fromInt(eventMessage.getType());
        path = eventMessage.getPath();
    }

    public KeeperStateEnum getState() {
        return keeperState;
    }

    public EventTypeEnum getType() {
        return eventType;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "WatchedEvent state:" + keeperState + " type:" + eventType + " path:" + path;
    }

    /**
     *  Convert WatchedEvent to type that can be sent over network
     */
    public WatcherEvent getWrapper() {
        return new WatcherEvent(eventType.getIntValue(), keeperState.getIntValue(), path);
    }
}
