package com.langwuyue.orange.zookeeper;

import java.util.Objects;

public class ACL {

	private int perms;
	private Id id;
	
	public ACL(int perms, Id id) {
		super();
		this.perms = perms;
		this.id = id;
	}
	public int getPerms() {
		return perms;
	}
	public void setPerms(int perms) {
		this.perms = perms;
	}
	public Id getId() {
		return id;
	}
	public void setId(Id id) {
		this.id = id;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id, perms);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ACL other = (ACL) obj;
		return Objects.equals(id, other.id) && perms == other.perms;
	}
	@Override
	public String toString() {
		return "ACL [perms=" + perms + ", id=" + id + "]";
	}
	
	public int compareTo (Object peer_) throws ClassCastException {
	    if (!(peer_ instanceof ACL)) {
	      throw new ClassCastException("Comparing different types of records.");
	    }
	    ACL peer = (ACL) peer_;
	    int ret = 0;
	    ret = (perms == peer.perms)? 0 :((perms<peer.perms)?-1:1);
	    if (ret != 0) return ret;
	    ret = id.compareTo(peer.id);
	    if (ret != 0) return ret;
	     return ret;
	}
}
