package com.langwuyue.orange.zookeeper.template;

import com.langwuyue.orange.zookeeper.DataTypeEnum;
import com.langwuyue.orange.zookeeper.annotations.Create;
import com.langwuyue.orange.zookeeper.annotations.Data;
import com.langwuyue.orange.zookeeper.annotations.Delete;
import com.langwuyue.orange.zookeeper.annotations.GetACL;
import com.langwuyue.orange.zookeeper.annotations.GetChildren;
import com.langwuyue.orange.zookeeper.annotations.GetData;
import com.langwuyue.orange.zookeeper.annotations.GetStat;
import com.langwuyue.orange.zookeeper.annotations.InBackground;
import com.langwuyue.orange.zookeeper.annotations.OrangeZookeeperClient;
import com.langwuyue.orange.zookeeper.annotations.Perms;
import com.langwuyue.orange.zookeeper.annotations.SetACL;
import com.langwuyue.orange.zookeeper.annotations.SetData;
import com.langwuyue.orange.zookeeper.annotations.Version;
import com.langwuyue.orange.zookeeper.annotations.Watch;

@OrangeZookeeperClient(dataType = DataTypeEnum.JSON, compressed = true)
public interface JSONAsyncOperationsTemplate<T> {

	@Create
	@InBackground
	void create();

	@SetData
	@InBackground
	void setData(@Data T data);

	@SetData
	@InBackground
	void setData(@Data T data, @Version int version);

	@SetACL
	void setACL(@Perms int... perms);

	@SetACL
	void setACL(@Version int version, @Perms int... perms);

	@Delete
	@InBackground
	void delete();

	@Delete
	@InBackground
	void delete(@Version int version);

	@GetData
	@InBackground
	void getData();

	@GetData
	@Watch
	void listenDataChange();

	@GetACL
	@InBackground
	void getACL();

	@GetStat
	@InBackground
	void getStat();
	
	@GetStat
	@Watch
	void listenStatChange();

	@GetChildren
	@InBackground
	void getChildren();
	
	@GetChildren
	@Watch
	void listenChildrenChange();
}
