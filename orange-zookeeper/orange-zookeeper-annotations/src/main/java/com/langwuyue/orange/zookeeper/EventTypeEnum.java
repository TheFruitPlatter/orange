package com.langwuyue.orange.zookeeper;


public enum EventTypeEnum {
	None(-1),
    NodeCreated(1),
    NodeDeleted(2),
    NodeDataChanged(3),
    NodeChildrenChanged(4),
    DataWatchRemoved(5),
    ChildWatchRemoved(6),
    PersistentWatchRemoved (7);

    private final int intValue;     // Integer representation of value
    // for sending over wire

    EventTypeEnum(int intValue) {
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public static EventTypeEnum fromInt(int intValue) {
        switch (intValue) {
        case -1:
            return EventTypeEnum.None;
        case 1:
            return EventTypeEnum.NodeCreated;
        case 2:
            return EventTypeEnum.NodeDeleted;
        case 3:
            return EventTypeEnum.NodeDataChanged;
        case 4:
            return EventTypeEnum.NodeChildrenChanged;
        case 5:
            return EventTypeEnum.DataWatchRemoved;
        case 6:
            return EventTypeEnum.ChildWatchRemoved;
        case 7:
            return EventTypeEnum.PersistentWatchRemoved;

        default:
            throw new RuntimeException("Invalid integer value for conversion to EventType");
        }
    }
}
