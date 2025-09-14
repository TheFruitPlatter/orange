package com.langwuyue.orange.zookeeper;

import java.util.Objects;

public class Id {

	private String scheme;
	private String id;
	
	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Id(String scheme, String id) {
		super();
		this.scheme = scheme;
		this.id = id;
	}
	
	public int compareTo (Object peer_) throws ClassCastException {
	    if (!(peer_ instanceof Id)) {
	      throw new ClassCastException("Comparing different types of records.");
	    }
	    Id peer = (Id) peer_;
	    int ret = 0;
	    ret = scheme.compareTo(peer.scheme);
	    if (ret != 0) return ret;
	    ret = id.compareTo(peer.id);
	    if (ret != 0) return ret;
	     return ret;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, scheme);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Id other = (Id) obj;
		return Objects.equals(id, other.id) && Objects.equals(scheme, other.scheme);
	}

	@Override
	public String toString() {
		return "Id [scheme=" + scheme + ", id=" + id + "]";
	}
	
}
