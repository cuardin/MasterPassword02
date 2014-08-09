package org.irrbloss.masterpassword.storage;

public class WorkPacket {
	//GET is missing from the list since we don't do async reads.
	public enum Operations { INIT, PUT, SYNC, REMOVE, CLEAR };
	public String key;
	public SiteDescriptor value;
	public ISiteListImpl implementation;
	public Operations op;
	
	//PRivate constructor to force factory methods below.
	private WorkPacket() {		
	}
	
	public static WorkPacket CreateInit(ISiteListImpl instance) {
		WorkPacket w = new WorkPacket();
		w.op = Operations.INIT;
		w.implementation = instance;
		return w;		
	}

	//Sync packages are currently not used. Init does the sync instead.
	public static WorkPacket CreateSync(ISiteListImpl instance) {
		WorkPacket w = new WorkPacket();
		w.op = Operations.SYNC;
		w.implementation = instance;
		return w;		
	}

	public static WorkPacket createRemove(String key) {
		WorkPacket w = new WorkPacket();
		w.op = Operations.REMOVE;
		w.key = key;
		return w;		
	}

	public static WorkPacket createClear() {
		WorkPacket w = new WorkPacket();
		w.op = Operations.CLEAR;		
		return w;		
	}

	public static WorkPacket CreatePut(String key, SiteDescriptor value) {
		WorkPacket w = new WorkPacket();
		w.op = Operations.PUT;
		w.key = key;
		w.value = value;
		return w;
	}

}
