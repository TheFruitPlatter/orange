package com.langwuyue.orange.zookeeper;

import java.util.List;

import com.langwuyue.orange.zookeeper.context.OrangeCreationAndSetDataContext;
import com.langwuyue.orange.zookeeper.context.OrangeCreationContext;
import com.langwuyue.orange.zookeeper.context.OrangeCreationWithACLAndDataContext;
import com.langwuyue.orange.zookeeper.context.OrangeCreationWithACLContext;
import com.langwuyue.orange.zookeeper.context.OrangeDeleteContext;
import com.langwuyue.orange.zookeeper.context.OrangeDeleteWithVersionContext;
import com.langwuyue.orange.zookeeper.context.OrangeSetACLContext;
import com.langwuyue.orange.zookeeper.context.OrangeSetACLWithVersionContext;
import com.langwuyue.orange.zookeeper.context.OrangeSetDataContext;
import com.langwuyue.orange.zookeeper.context.OrangeSetDataWitVersionContext;
import com.langwuyue.orange.zookeeper.context.OrangeZookeeperContext;

public interface OrangeZookeeperOperations {

	String create(OrangeCreationContext context) throws Exception;
	
	Stat createAndSetData(OrangeCreationAndSetDataContext context) throws Exception;

	String createWithACL(OrangeCreationWithACLContext context) throws Exception;

	Stat createWithACLAndData(OrangeCreationWithACLAndDataContext context) throws Exception;

	Stat setData(OrangeSetDataContext context) throws Exception;
	
	Stat setData(OrangeSetDataWitVersionContext context) throws Exception;

	Stat setACL(OrangeSetACLContext context) throws Exception;
	
	Stat setACL(OrangeSetACLWithVersionContext context) throws Exception;

	void delete(OrangeDeleteContext context) throws Exception;
	
	void delete(OrangeDeleteWithVersionContext context) throws Exception;

	Object getData(OrangeZookeeperContext context) throws Exception;

	List<ACL> getACL(OrangeZookeeperContext context) throws Exception;

	ACLAndStat getACLAndStat(OrangeZookeeperContext context) throws Exception;
	
	Stat getStat(OrangeZookeeperContext context) throws Exception;

	List<String> getChildren(OrangeZookeeperContext context) throws Exception;

	ChildrenAndParentStat getChildrenAndStat(OrangeZookeeperContext context) throws Exception;

	void sync(OrangeZookeeperContext context) throws Exception;

}
