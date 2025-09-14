package com.langwuyue.orange.zookeeper;

import java.util.Objects;

public class WatcherEvent {
	private int type;
	private int state;
	private String path;
	public WatcherEvent(int type, int state, String path) {
		super();
		this.type = type;
		this.state = state;
		this.path = path;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public int hashCode() {
		return Objects.hash(path, state, type);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WatcherEvent other = (WatcherEvent) obj;
		return Objects.equals(path, other.path) && state == other.state && type == other.type;
	}
	@Override
	public String toString() {
		return "WatcherEvent [type=" + type + ", state=" + state + ", path=" + path + "]";
	}
	
}
