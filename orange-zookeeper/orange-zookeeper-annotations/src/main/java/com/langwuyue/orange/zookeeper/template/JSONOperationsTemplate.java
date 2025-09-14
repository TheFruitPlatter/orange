package com.langwuyue.orange.zookeeper.template;

import java.util.List;

import com.langwuyue.orange.zookeeper.ACL;
import com.langwuyue.orange.zookeeper.ACLAndStat;
import com.langwuyue.orange.zookeeper.ChildrenAndParentStat;
import com.langwuyue.orange.zookeeper.DataTypeEnum;
import com.langwuyue.orange.zookeeper.Stat;
import com.langwuyue.orange.zookeeper.annotations.Create;
import com.langwuyue.orange.zookeeper.annotations.Data;
import com.langwuyue.orange.zookeeper.annotations.Delete;
import com.langwuyue.orange.zookeeper.annotations.GetACL;
import com.langwuyue.orange.zookeeper.annotations.GetChildren;
import com.langwuyue.orange.zookeeper.annotations.GetData;
import com.langwuyue.orange.zookeeper.annotations.GetStat;
import com.langwuyue.orange.zookeeper.annotations.OrangeZookeeperClient;
import com.langwuyue.orange.zookeeper.annotations.Perms;
import com.langwuyue.orange.zookeeper.annotations.SetACL;
import com.langwuyue.orange.zookeeper.annotations.SetData;
import com.langwuyue.orange.zookeeper.annotations.Sync;
import com.langwuyue.orange.zookeeper.annotations.Version;

@OrangeZookeeperClient(dataType = DataTypeEnum.JSON, compressed = true)
public interface JSONOperationsTemplate<T> {
	
	@Create
	String create();
	
	@Create(idempotent = false)
	@SetData
	Stat createAndSetData(@Data T data);
	
	@Create(idempotent = false)
	@SetACL
	String createWithACL(@Perms ACL... perms);
	
	@Create(idempotent = false)
	@SetData
	@SetACL
	String createWithACLAndSetData(@Data T data, @Perms int... perms);
	
	@Create
	@SetData
	Stat createAndAlwaysSetData(@Data T data);
	
	@Create
	@SetACL
	String createAndAlwaysSetACL(@Perms ACL... perms);
	
	@Create
	@SetData
	@SetACL
	String createAndAlwaysSetACLAndSetData(@Data T data, @Perms int... perms);
	
	@SetData
	Stat setData(@Data T data);

	@SetData
	Stat setData(@Data T data, @Version int version);
	
	@SetACL
	Stat setACL(@Perms int... perms);
	
	@SetACL
	Stat setACL(@Version int version, @Perms int... perms);
	
	@Delete
	void delete();
	
	@Delete
	void delete(@Version int version);
	
	@GetData
	T getData();
	
	@GetACL
	List<ACL> getACL();
	
	@GetACL
	@GetStat
	ACLAndStat getACLWithStat();
	
	@GetStat
	Stat getStat();
	
	@GetChildren
	List<String> getChildren();
	
	@GetChildren
	@GetStat
	ChildrenAndParentStat getChildrenAndStat();
	
	@Sync
	void sync();
}
