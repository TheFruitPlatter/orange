package com.langwuyue.orange.zookeeper.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.curator.framework.api.CuratorEvent;

import com.langwuyue.orange.zookeeper.ACL;
import com.langwuyue.orange.zookeeper.BackgroundEvent;
import com.langwuyue.orange.zookeeper.DataTypeEnum;
import com.langwuyue.orange.zookeeper.OrangeZookeeperDataSerializer;
import com.langwuyue.orange.zookeeper.Stat;
import com.langwuyue.orange.zookeeper.TransactionResult;
import com.langwuyue.orange.zookeeper.WatchedEvent;
import com.langwuyue.orange.zookeeper.converter.OrangeACLConverter;
import com.langwuyue.orange.zookeeper.converter.OrangeStatConverter;
import com.langwuyue.orange.zookeeper.converter.OrangeTransactionResultConverter;
import com.langwuyue.orange.zookeeper.converter.OrangeWatchedEventConverter;

public class OrangeDefaultBackgroundEvent implements BackgroundEvent {
	
	private CuratorEvent curatorEvent;
	
	private Class<? extends Annotation> type;
	
	private Object context;
	
	private OrangeStatConverter statConverter;
	
	private OrangeACLConverter aclConverter;
	
	private OrangeTransactionResultConverter transactionResultConverter;
	
	private OrangeWatchedEventConverter watchedEventConverter;
	
	private Stat stat;
	
	private List<ACL> acls;
	
	private Object data;
	
	private OrangeZookeeperDataSerializer dataSerializer;
	
	private Type dataDeserializeType;
	
	private DataTypeEnum dataType;
	
	private List<TransactionResult> transactionResults;
	
	private WatchedEvent watchedEvent;
	
	
	public OrangeDefaultBackgroundEvent(
		CuratorEvent curatorEvent,
		Class<? extends Annotation> type,
		Object context,
		OrangeZookeeperDataSerializer dataSerializer,
		Type dataDeserializeType,
		DataTypeEnum dataType
	) {
		this.curatorEvent = curatorEvent;
		this.type = type;
		this.context = context;
		this.statConverter = new OrangeStatConverter();
		this.aclConverter = new OrangeACLConverter();
		this.dataSerializer = dataSerializer;
		this.dataDeserializeType = dataDeserializeType;
		this.dataType = dataType;
		this.transactionResultConverter = new OrangeTransactionResultConverter(this.statConverter);
	}
	
	@Override
	public Class<? extends Annotation> getType() {
		return this.type;
	}

	@Override
	public int getResultCode() {
		return this.curatorEvent.getResultCode();
	}

	@Override
	public String getPath() {
		return this.curatorEvent.getPath();
	}

	@Override
	public Object getContext() {
		return this.context;
	}

	@Override
	public Stat getStat() {
		if(this.stat != null) {
			return this.stat;
		}
		this.stat = this.statConverter.convert(this.curatorEvent.getStat());
		return stat;
	}

	@Override
	public Object getData() throws Exception {
		if(this.data != null) {
			return this.data;
		}
		byte[] bytes = this.curatorEvent.getData();
		if(bytes == null || bytes.length == 0) {
			return null;
		}
		this.data = this.dataSerializer.deserialize(bytes, this.dataType, this.dataDeserializeType);
		return this.data;
	}

	@Override
	public String getName() {
		return this.curatorEvent.getName();
	}

	@Override
	public List<String> getChildren() {
		return this.curatorEvent.getChildren();
	}

	@Override
	public List<ACL> getACLList() {
		if(this.acls != null) {
			return this.acls;
		}
		this.acls = this.aclConverter.convert(this.curatorEvent.getACLList());
		return this.acls;
	}

	@Override
	public List<TransactionResult> getOpResults() {
		if(this.transactionResults != null) {
			return this.transactionResults;
		}
		this.transactionResults = this.transactionResultConverter.convert(curatorEvent.getOpResults());
		return this.transactionResults;
	}

	@Override
	public WatchedEvent getWatchedEvent() {
		if(this.watchedEvent != null) {
			return this.watchedEvent;
		}
		this.watchedEvent = this.watchedEventConverter.convert(curatorEvent.getWatchedEvent());
		return this.watchedEvent;
	}
}
