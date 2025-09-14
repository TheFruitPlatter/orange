package com.langwuyue.orange.zookeeper.enums;

import java.util.EnumMap;
import java.util.Map;

import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.langwuyue.orange.zookeeper.KeeperStateEnum;

public enum OrangeKeeperStateMapEnum {
	
	/** The client is in the disconnected state - it is not connected
     * to any server in the ensemble. */
    Disconnected (KeeperState.Disconnected,KeeperStateEnum.Disconnected),

    /** The client is in the connected state - it is connected
     * to a server in the ensemble (one of the servers specified
     * in the host connection parameter during ZooKeeper client
     * creation). */
    SyncConnected(KeeperState.Disconnected,KeeperStateEnum.Disconnected),

    /**
     * Auth failed state
     */
    AuthFailed(KeeperState.AuthFailed,KeeperStateEnum.AuthFailed),

    /**
     * The client is connected to a read-only server, that is the
     * server which is not currently connected to the majority.
     * The only operations allowed after receiving this state is
     * read operations.
     * This state is generated for read-only clients only since
     * read/write clients aren't allowed to connect to r/o servers.
     */
    ConnectedReadOnly(KeeperState.ConnectedReadOnly,KeeperStateEnum.ConnectedReadOnly),

    /**
      * SaslAuthenticated: used to notify clients that they are SASL-authenticated,
      * so that they can perform Zookeeper actions with their SASL-authorized permissions.
      */
    SaslAuthenticated(KeeperState.SaslAuthenticated,KeeperStateEnum.SaslAuthenticated),

    /** The serving cluster has expired this session. The ZooKeeper
     * client connection (the session) is no longer valid. You must
     * create a new client connection (instantiate a new ZooKeeper
     * instance) if you with to access the ensemble. */
    Expired(KeeperState.Expired,KeeperStateEnum.Expired);
	;
	
	private KeeperState keeperState;
	
	private KeeperStateEnum orangeKeeperState;
	
	private static Map<KeeperState, KeeperStateEnum> MAP = new EnumMap<>(KeeperState.class);
	
	static {
		for(OrangeKeeperStateMapEnum zNodeType2CreateModeEnum : OrangeKeeperStateMapEnum.values()) {
			MAP.put(zNodeType2CreateModeEnum.keeperState, zNodeType2CreateModeEnum.orangeKeeperState);
		}
	}
	
	private OrangeKeeperStateMapEnum(KeeperState keeperState, KeeperStateEnum orangeKeeperState) {
		this.keeperState = keeperState;
		this.orangeKeeperState = orangeKeeperState;
	}

	public KeeperState getKeeperState() {
		return keeperState;
	}

	public KeeperStateEnum getOrangeKeeperState() {
		return orangeKeeperState;
	}

	public static KeeperStateEnum getBy(KeeperState state) {
		return MAP.get(state);
	}
}
