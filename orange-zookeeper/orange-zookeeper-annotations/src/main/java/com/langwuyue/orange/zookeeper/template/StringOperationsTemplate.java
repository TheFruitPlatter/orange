package com.langwuyue.orange.zookeeper.template;

import com.langwuyue.orange.zookeeper.DataTypeEnum;
import com.langwuyue.orange.zookeeper.annotations.OrangeZookeeperClient;

@OrangeZookeeperClient(dataType = DataTypeEnum.STRING, compressed = true)
public interface StringOperationsTemplate extends JSONOperationsTemplate<String>{
	
	@Override
	String getData();
	
}
