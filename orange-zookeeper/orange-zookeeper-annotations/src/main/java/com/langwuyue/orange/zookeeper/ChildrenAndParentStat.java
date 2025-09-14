package com.langwuyue.orange.zookeeper;

import java.util.List;

public class ChildrenAndParentStat {
	
	private List<String> children;

	private Stat parentStat;

	public List<String> getChildren() {
		return children;
	}

	public void setChildren(List<String> children) {
		this.children = children;
	}

	public Stat getParentStat() {
		return parentStat;
	}

	public void setParentStat(Stat parentStat) {
		this.parentStat = parentStat;
	}
	
}
