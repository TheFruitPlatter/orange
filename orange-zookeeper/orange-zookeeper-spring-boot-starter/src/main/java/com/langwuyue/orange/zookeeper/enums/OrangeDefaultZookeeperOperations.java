package com.langwuyue.orange.zookeeper.enums;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.SetACLBuilder;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.data.Stat;

import com.langwuyue.orange.zookeeper.ACL;
import com.langwuyue.orange.zookeeper.ACLAndStat;
import com.langwuyue.orange.zookeeper.ChildrenAndParentStat;
import com.langwuyue.orange.zookeeper.OrangeZookeeperDataSerializer;
import com.langwuyue.orange.zookeeper.OrangeZookeeperException;
import com.langwuyue.orange.zookeeper.OrangeZookeeperOperations;
import com.langwuyue.orange.zookeeper.annotations.OrangeZookeeperClient;
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
import com.langwuyue.orange.zookeeper.converter.OrangeACLConverter;
import com.langwuyue.orange.zookeeper.converter.OrangeStatConverter;

public class OrangeDefaultZookeeperOperations implements OrangeZookeeperOperations {
	
	private CuratorFramework curatorFramework;
	
	private OrangeZookeeperDataSerializer serializer;
	
	private OrangeStatConverter statConverter;
	
	private OrangeACLConverter aclConverter;
	
	public OrangeDefaultZookeeperOperations(
		CuratorFramework curatorFramework,
		OrangeZookeeperDataSerializer serializer
	) {
		super();
		this.curatorFramework = curatorFramework;
		this.serializer = serializer;
		this.statConverter = new OrangeStatConverter();
		this.aclConverter = new OrangeACLConverter();
	}

	@Override
	public String create(OrangeCreationContext context) throws Exception {
		CreateBuilder builder = initCreateBuilder(curatorFramework.create(),context);
		try {
			return builder.forPath(context.getZNode().getPath());
		}catch (NodeExistsException e) {
			if(context.getCreateAnnotation().idempotent()) {
				return e.getPath();
			}
			throw new OrangeZookeeperException(e);
		}
	}

	@Override
	public com.langwuyue.orange.zookeeper.Stat createAndSetData(OrangeCreationAndSetDataContext context) throws Exception {
		try {
			CreateBuilder builder = initCreateBuilder(curatorFramework.create(),context);
			builder.forPath(
				context.getZNode().getPath(), 
				serializer.serialize(
				context.getData(), 
				context.getClient().dataType()
			));
			Stat stat = curatorFramework.checkExists().forPath(context.getZNode().getPath());
			return statConverter.convert(stat);
		}catch (NodeExistsException e) {
			if(!context.getCreateAnnotation().idempotent()) {
				throw new OrangeZookeeperException(e);
			}
			OrangeSetDataContext ctx = new OrangeSetDataContext(
				context.getZNode(),
				context.getOperationOwner(),
				context.getOperationMethod(),
				context.getArgs(),
				context.getClient()
			);
			ctx.setSetDataAnnotation(context.getSetDataAnnotation());
			ctx.setData(context.getData());
			return setData(ctx);
		}
	}
	
	@Override
	public String createWithACL(OrangeCreationWithACLContext context) throws Exception {
		CreateBuilder builder = initCreateBuilder(curatorFramework.create(),context);
		List<org.apache.zookeeper.data.ACL> acls = this.aclConverter.convert(context.getPerms());
		builder.withACL(
			acls,
			context.getSetACLAnnotation().applyParents()
		);
		try {
			return builder.forPath(context.getZNode().getPath());
		}catch (NodeExistsException e) {
			if(!context.getCreateAnnotation().idempotent()) {
				throw new OrangeZookeeperException(e);
			}
			setACL(
				context.getZNode().getPath(),
				acls,
				context.getSetACLAnnotation().applyParents(),
				null
			);
			return e.getPath();
		}
	}
	
	@Override
	public com.langwuyue.orange.zookeeper.Stat createWithACLAndData(OrangeCreationWithACLAndDataContext context) throws Exception {
		try {
			CreateBuilder builder = initCreateBuilder(curatorFramework.create(),context);
			builder.withACL(
				this.aclConverter.convert(context.getPerms()),
				context.getSetACLAnnotation().applyParents()
			);
			Stat stat = curatorFramework.checkExists().forPath(context.getZNode().getPath());
			return statConverter.convert(stat);
		}catch (NodeExistsException e) {
			OrangeSetDataContext ctx = new OrangeSetDataContext(
				context.getZNode(),
				context.getOperationOwner(),
				context.getOperationMethod(),
				context.getArgs(),
				context.getClient()
			);
			ctx.setSetDataAnnotation(context.getSetDataAnnotation());
			ctx.setData(context.getData());
			return setData(ctx);
		}
	}
	
	@Override
	public com.langwuyue.orange.zookeeper.Stat setData(OrangeSetDataContext context) throws Exception{
		SetDataBuilder builder = initSetDataBuilder(context.getClient());
		Stat stat = builder.forPath(
			context.getZNode().getPath(),
			serializer.serialize(
				context.getData(), 
				context.getClient().dataType()
			)
		);
		return statConverter.convert(stat);
	}
	
	@Override
	public com.langwuyue.orange.zookeeper.Stat setData(OrangeSetDataWitVersionContext context) throws Exception {
		SetDataBuilder builder = initSetDataBuilder(context.getClient());
		builder.withVersion(context.getVersion());
		Stat stat = builder.forPath(
			context.getZNode().getPath(),
			serializer.serialize(
				context.getData(), 
				context.getClient().dataType()
			)
		);
		return statConverter.convert(stat);
	}
	
	@Override
	public com.langwuyue.orange.zookeeper.Stat setACL(OrangeSetACLContext context) throws Exception {
		List<org.apache.zookeeper.data.ACL> acls = this.aclConverter.convert(context.getPerms());
		return setACL(
			context.getZNode().getPath(),
			acls,
			context.getSetACLAnnotation().applyParents(),
			null
		);
	}
	
	@Override
	public com.langwuyue.orange.zookeeper.Stat setACL(OrangeSetACLWithVersionContext context) throws Exception {
		List<org.apache.zookeeper.data.ACL> acls = this.aclConverter.convert(context.getPerms());
		return setACL(
			context.getZNode().getPath(),
			acls,
			context.getSetACLAnnotation().applyParents(),
			context.getVersion()
		);
	}
	
	@Override
	public void delete(OrangeDeleteContext context) throws Exception {
		initDeleteBuilder(context).forPath(context.getZNode().getPath());
	}
	
	@Override
	public void delete(OrangeDeleteWithVersionContext context) throws Exception {
		initDeleteBuilder(context).withVersion(context.getVersion()).forPath(context.getZNode().getPath());
	}
	
	@Override
	public Object getData(OrangeZookeeperContext context) throws Exception {
		byte[] bytes = curatorFramework.getData().forPath(context.getZNode().getPath());
		return this.serializer.deserialize(bytes, context.getClient().dataType(), context.getOperationMethod().getGenericReturnType());
	}
	
	@Override
	public List<ACL> getACL(OrangeZookeeperContext context) throws Exception {
		List<org.apache.zookeeper.data.ACL> acls = curatorFramework.getACL().forPath(context.getZNode().getPath());
		return this.aclConverter.convert(acls);
	}
	
	@Override
	public ACLAndStat getACLAndStat(OrangeZookeeperContext context) throws Exception {
		Stat stat = new Stat();
		List<org.apache.zookeeper.data.ACL> acls = curatorFramework.getACL().storingStatIn(stat).forPath(context.getZNode().getPath());
		ACLAndStat aclAndStat = new ACLAndStat();
		aclAndStat.setAcls(this.aclConverter.convert(acls));
		aclAndStat.setStat(this.statConverter.convert(stat));
		return aclAndStat;
	}
	
	@Override
	public com.langwuyue.orange.zookeeper.Stat getStat(OrangeZookeeperContext context) throws Exception {
		Stat stat = curatorFramework.checkExists().forPath(context.getZNode().getPath());
		return this.statConverter.convert(stat);
	}
	
	@Override
	public List<String> getChildren(OrangeZookeeperContext context) throws Exception {
		return curatorFramework.getChildren().forPath(context.getZNode().getPath());
	}
	
	@Override
	public ChildrenAndParentStat getChildrenAndStat(OrangeZookeeperContext context) throws Exception {
		Stat stat = new Stat();
		List<String> children = curatorFramework.getChildren().storingStatIn(stat).forPath(context.getZNode().getPath());
		ChildrenAndParentStat childrenAndParentStat = new ChildrenAndParentStat();
		childrenAndParentStat.setChildren(children);
		childrenAndParentStat.setParentStat(this.statConverter.convert(stat));
		return childrenAndParentStat;
	}
	
	@Override
	public void sync(OrangeZookeeperContext context) throws Exception {
		curatorFramework.sync().forPath(context.getZNode().getPath());
	}
	
	private DeleteBuilder initDeleteBuilder(OrangeDeleteContext context) {
		DeleteBuilder builder = curatorFramework.delete();
		if(context.getDeleteAnnotation().deletingChildrenIfNeeded()) {
			builder.deletingChildrenIfNeeded();
		}
		if(context.getDeleteAnnotation().guaranteed()) {
			builder.guaranteed();
		}
		if(context.getDeleteAnnotation().quietly()) {
			builder.quietly();
		}
		return builder;
	}
	
	
	private SetDataBuilder initSetDataBuilder(OrangeZookeeperClient client) {
		SetDataBuilder builder = curatorFramework.setData();
		if(client.compressed()) {
			builder.compressed();
		}
		return builder;
	}
	
	private com.langwuyue.orange.zookeeper.Stat setACL(String path,List<org.apache.zookeeper.data.ACL> acls, boolean applyParents,Integer version) throws Exception {
		SetACLBuilder builder = curatorFramework.setACL();
		if(version != null) {
			builder.withVersion(version);
		}
		Stat stat = builder.withACL(acls).forPath(path);
		if(stat == null) {
			return null;
		}
		if(applyParents) {
			return this.statConverter.convert(stat);
		}
		String parent = path;
		for(;;) {
			int lastIndex = parent.lastIndexOf("/");
			if(lastIndex == 0) {
				break;
			}
			parent = path.substring(0, lastIndex);
			curatorFramework.setACL().withACL(acls).forPath(parent);
		}
		return this.statConverter.convert(stat);
	}
	
	private CreateBuilder initCreateBuilder(CreateBuilder builder,OrangeCreationContext context) {
		CreateMode mode = OrangeCreateModeEnum.getByZNodeTypeEnum(context.getZNode().getZNodeType());
		builder.withMode(mode);
		if(context.getCreateAnnotation().withProtection()) {
			// When protection is enabled,
			// Curator inserts a UUID into the node path.
			builder.withProtection();
		}
		if(context.getCreateAnnotation().creatingParentContainersIfNeeded()) {
			// Create parent in 'CONTAINER' mode
			// Note: If 'CONTAINER' node has no children, it will be deleted automatically. 
			builder.creatingParentContainersIfNeeded();
		}else if(context.getCreateAnnotation().creatingParentsIfNeeded()){
			// Create parent in 'PERSISTENT' mode
			builder.creatingParentsIfNeeded();
		}
		return builder;
	}
}
