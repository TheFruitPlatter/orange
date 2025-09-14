package com.langwuyue.orange.zookeeper;

import java.util.List;

public class ACLAndStat {
	
	private List<ACL> acls;

	private Stat stat;

	public List<ACL> getAcls() {
		return acls;
	}

	public void setAcls(List<ACL> acls) {
		this.acls = acls;
	}

	public Stat getStat() {
		return stat;
	}

	public void setStat(Stat stat) {
		this.stat = stat;
	}
}
