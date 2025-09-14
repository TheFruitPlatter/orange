package com.langwuyue.orange.zookeeper.enums;

import java.util.HashMap;
import java.util.Map;

import org.apache.zookeeper.CreateMode;

import com.langwuyue.orange.zookeeper.ZNodeTypeEnum;

/*** 
 *  ZnodeTypeEnum is the same as Zookeeper CreateMode.
 *  CreateMode value determines how the znode is created on ZooKeeper.
 */
public enum OrangeCreateModeEnum {
	
	/**
     * The znode will not be automatically deleted upon client's disconnect.
     */
    PERSISTENT(ZNodeTypeEnum.PERSISTENT,CreateMode.PERSISTENT),
    /**
     * The znode will not be automatically deleted upon client's disconnect,
     * and its name will be appended with a monotonically increasing number.
     */
    PERSISTENT_SEQUENTIAL(ZNodeTypeEnum.PERSISTENT_SEQUENTIAL,CreateMode.PERSISTENT_SEQUENTIAL),
    /**
     * The znode will be deleted upon the client's disconnect.
     */
    EPHEMERAL(ZNodeTypeEnum.EPHEMERAL,CreateMode.EPHEMERAL),
    /**
     * The znode will be deleted upon the client's disconnect, and its name
     * will be appended with a monotonically increasing number.
     */
    EPHEMERAL_SEQUENTIAL(ZNodeTypeEnum.EPHEMERAL_SEQUENTIAL,CreateMode.EPHEMERAL_SEQUENTIAL),
    /**
     * The znode will be a container node. Container
     * nodes are special purpose nodes useful for recipes such as leader, lock,
     * etc. When the last child of a container is deleted, the container becomes
     * a candidate to be deleted by the server at some point in the future.
     * Given this property, you should be prepared to get
     * {@link org.apache.zookeeper.KeeperException.NoNodeException}
     * when creating children inside of this container node.
     */
    CONTAINER(ZNodeTypeEnum.CONTAINER,CreateMode.CONTAINER),
    /**
     * The znode will not be automatically deleted upon client's disconnect.
     * However if the znode has not been modified within the given TTL, it
     * will be deleted once it has no children.
     */
    PERSISTENT_WITH_TTL(ZNodeTypeEnum.PERSISTENT_WITH_TTL,CreateMode.PERSISTENT_WITH_TTL),
    /**
     * The znode will not be automatically deleted upon client's disconnect,
     * and its name will be appended with a monotonically increasing number.
     * However if the znode has not been modified within the given TTL, it
     * will be deleted once it has no children.
     */
    PERSISTENT_SEQUENTIAL_WITH_TTL(ZNodeTypeEnum.PERSISTENT_SEQUENTIAL_WITH_TTL,CreateMode.PERSISTENT_SEQUENTIAL_WITH_TTL),
	
	;
	
	private ZNodeTypeEnum zNodeTypeEnum;
	
	private CreateMode createMode;
	
	private static Map<ZNodeTypeEnum, CreateMode> MAP = new HashMap<>();
	
	static {
		for(OrangeCreateModeEnum zNodeType2CreateModeEnum : OrangeCreateModeEnum.values()) {
			MAP.put(zNodeType2CreateModeEnum.zNodeTypeEnum, zNodeType2CreateModeEnum.createMode);
		}
	}

	private OrangeCreateModeEnum(ZNodeTypeEnum zNodeTypeEnum, CreateMode createMode) {
		this.zNodeTypeEnum = zNodeTypeEnum;
		this.createMode = createMode;
	}

	public ZNodeTypeEnum getzNodeTypeEnum() {
		return zNodeTypeEnum;
	}

	public CreateMode getCreateMode() {
		return createMode;
	}
	
	public static CreateMode getByZNodeTypeEnum(ZNodeTypeEnum zNodeTypeEnum) {
		return MAP.get(zNodeTypeEnum);
	}
}
