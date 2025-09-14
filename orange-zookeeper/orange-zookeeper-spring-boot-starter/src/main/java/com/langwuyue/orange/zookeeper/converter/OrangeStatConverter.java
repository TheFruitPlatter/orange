package com.langwuyue.orange.zookeeper.converter;

import com.langwuyue.orange.zookeeper.Stat;

public class OrangeStatConverter {

	public Stat convert(org.apache.zookeeper.data.Stat stat) {
		if(stat == null) {
			return null;
		}
		return new Stat(
			stat.getCzxid(),
			stat.getMzxid(),
			stat.getCtime(),
			stat.getMtime(),
			stat.getVersion(),
			stat.getCversion(),
			stat.getAversion(),
			stat.getEphemeralOwner(),
			stat.getDataLength(),
			stat.getNumChildren(),
			stat.getPzxid()
		);
	}

}
