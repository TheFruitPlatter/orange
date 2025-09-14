package com.langwuyue.orange.zookeeper.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.data.Id;

import com.langwuyue.orange.zookeeper.ACL;

public class OrangeACLConverter {

	public org.apache.zookeeper.data.ACL convert(ACL acl) {
		if(acl == null) {
			return null;
		}
		return new org.apache.zookeeper.data.ACL(
			acl.getPerms(),
			new Id(
				acl.getId().getScheme(),
				acl.getId().getId()
			)
		);
	}
	
	public ACL convert(org.apache.zookeeper.data.ACL acl) {
		if(acl == null) {
			return null;
		}
		return new ACL(
			acl.getPerms(),
			new com.langwuyue.orange.zookeeper.Id(
				acl.getId().getScheme(),
				acl.getId().getId()
			)
		);
	}

	public List<org.apache.zookeeper.data.ACL> convert(ACL[] acls) {
		if(acls == null || acls.length == 0) {
			return new ArrayList<>();
		}
		List<org.apache.zookeeper.data.ACL> result = new  ArrayList<>();
		for(ACL acl:acls) {
			org.apache.zookeeper.data.ACL zkAcl = convert(acl);
			if(zkAcl == null) {
				continue;
			}
			result.add(zkAcl);
		}
		return result;
	}

	public List<ACL> convert(List<org.apache.zookeeper.data.ACL> acls) {
		if(acls == null || acls.isEmpty()) {
			return new ArrayList<>();
		}
		List<ACL> result = new  ArrayList<>();
		for(org.apache.zookeeper.data.ACL acl:acls) {
			ACL orangeAcl = convert(acl);
			if(orangeAcl == null) {
				continue;
			}
			result.add(orangeAcl);
		}
		return result;
	}
}
