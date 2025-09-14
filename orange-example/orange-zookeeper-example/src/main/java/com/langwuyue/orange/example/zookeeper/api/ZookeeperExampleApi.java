package com.langwuyue.orange.example.zookeeper.api;

import com.langwuyue.orange.example.zookeeper.entity.ZookeeperExampleEntity;
import com.langwuyue.orange.zookeeper.ZNodeTypeEnum;
import com.langwuyue.orange.zookeeper.annotations.ZookeeperZNode;
import com.langwuyue.orange.zookeeper.template.JSONOperationsTemplate;

@ZookeeperZNode(path="/orange/example/testcase1",zNodeType=ZNodeTypeEnum.PERSISTENT)
public interface ZookeeperExampleApi extends JSONOperationsTemplate<ZookeeperExampleEntity>{

}
