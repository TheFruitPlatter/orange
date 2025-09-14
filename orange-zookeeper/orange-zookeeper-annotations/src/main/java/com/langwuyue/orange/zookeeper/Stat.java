package com.langwuyue.orange.zookeeper;

import java.util.Objects;

public class Stat {
	
	private long czxid;
	private long mzxid;
	private long ctime;
	private long mtime;
	private int version;
	private int cversion;
	private int aversion;
	private long ephemeralOwner;
	private int dataLength;
	private int numChildren;
	private long pzxid;
	
	public Stat(
		long czxid, 
		long mzxid, 
		long ctime, 
		long mtime, 
		int version, 
		int cversion, 
		int aversion,
		long ephemeralOwner, 
		int dataLength, 
		int numChildren, 
		long pzxid
	) {
		super();
		this.czxid = czxid;
		this.mzxid = mzxid;
		this.ctime = ctime;
		this.mtime = mtime;
		this.version = version;
		this.cversion = cversion;
		this.aversion = aversion;
		this.ephemeralOwner = ephemeralOwner;
		this.dataLength = dataLength;
		this.numChildren = numChildren;
		this.pzxid = pzxid;
	}
	
	public long getCzxid() {
		return czxid;
	}

	public void setCzxid(long czxid) {
		this.czxid = czxid;
	}

	public long getMzxid() {
		return mzxid;
	}

	public void setMzxid(long mzxid) {
		this.mzxid = mzxid;
	}

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}

	public long getMtime() {
		return mtime;
	}

	public void setMtime(long mtime) {
		this.mtime = mtime;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getCversion() {
		return cversion;
	}

	public void setCversion(int cversion) {
		this.cversion = cversion;
	}

	public int getAversion() {
		return aversion;
	}

	public void setAversion(int aversion) {
		this.aversion = aversion;
	}

	public long getEphemeralOwner() {
		return ephemeralOwner;
	}

	public void setEphemeralOwner(long ephemeralOwner) {
		this.ephemeralOwner = ephemeralOwner;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public int getNumChildren() {
		return numChildren;
	}

	public void setNumChildren(int numChildren) {
		this.numChildren = numChildren;
	}

	public long getPzxid() {
		return pzxid;
	}

	public void setPzxid(long pzxid) {
		this.pzxid = pzxid;
	}
	
	public int compareTo (Object peer_) throws ClassCastException {
	    if (!(peer_ instanceof Stat)) {
	      throw new ClassCastException("Comparing different types of records.");
	    }
	    Stat peer = (Stat) peer_;
	    int ret = 0;
	    ret = (czxid == peer.czxid)? 0 :((czxid<peer.czxid)?-1:1);
	    if (ret != 0) return ret;
	    ret = (mzxid == peer.mzxid)? 0 :((mzxid<peer.mzxid)?-1:1);
	    if (ret != 0) return ret;
	    ret = (ctime == peer.ctime)? 0 :((ctime<peer.ctime)?-1:1);
	    if (ret != 0) return ret;
	    ret = (mtime == peer.mtime)? 0 :((mtime<peer.mtime)?-1:1);
	    if (ret != 0) return ret;
	    ret = (version == peer.version)? 0 :((version<peer.version)?-1:1);
	    if (ret != 0) return ret;
	    ret = (cversion == peer.cversion)? 0 :((cversion<peer.cversion)?-1:1);
	    if (ret != 0) return ret;
	    ret = (aversion == peer.aversion)? 0 :((aversion<peer.aversion)?-1:1);
	    if (ret != 0) return ret;
	    ret = (ephemeralOwner == peer.ephemeralOwner)? 0 :((ephemeralOwner<peer.ephemeralOwner)?-1:1);
	    if (ret != 0) return ret;
	    ret = (dataLength == peer.dataLength)? 0 :((dataLength<peer.dataLength)?-1:1);
	    if (ret != 0) return ret;
	    ret = (numChildren == peer.numChildren)? 0 :((numChildren<peer.numChildren)?-1:1);
	    if (ret != 0) return ret;
	    ret = (pzxid == peer.pzxid)? 0 :((pzxid<peer.pzxid)?-1:1);
	    if (ret != 0) return ret;
	     return ret;
	}

	@Override
	public int hashCode() {
		return Objects.hash(aversion, ctime, cversion, czxid, dataLength, ephemeralOwner, mtime, mzxid, numChildren,
				pzxid, version);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Stat other = (Stat) obj;
		return aversion == other.aversion && ctime == other.ctime && cversion == other.cversion && czxid == other.czxid
				&& dataLength == other.dataLength && ephemeralOwner == other.ephemeralOwner && mtime == other.mtime
				&& mzxid == other.mzxid && numChildren == other.numChildren && pzxid == other.pzxid
				&& version == other.version;
	}
	@Override
	public String toString() {
		return "Stat [czxid=" + czxid + ", mzxid=" + mzxid + ", ctime=" + ctime + ", mtime=" + mtime + ", version="
				+ version + ", cversion=" + cversion + ", aversion=" + aversion + ", ephemeralOwner=" + ephemeralOwner
				+ ", dataLength=" + dataLength + ", numChildren=" + numChildren + ", pzxid=" + pzxid + "]";
	}
	
}
