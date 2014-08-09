package org.irrbloss.masterpassword.storage;

import java.util.Set;

import org.irrbloss.masterpassword.storage.Exceptions.PermanentSyncException;

public interface ISiteListImpl {
	
	public enum Implementations {
		RAM, FILE, NET
	}
	
	//This function should do any initialization that can take time (FS and net access).
	//This function needs to check if the object is allready initialized and behave 
	//non-destructively in that case.
	public abstract void init() throws PermanentSyncException;
	
	public abstract Implementations implementationCode();
	
	public abstract String implementationName();
	
	public abstract int size() throws PermanentSyncException;	

	public abstract SiteDescriptor get(String key) throws PermanentSyncException;

	/**
	 * This put works a bit different than the usual set put. It only stores 
	 * the value provided if it is newer than what we already have 
	 * stored. 
	 * @throws PermanentSyncException 
	 */
	public abstract void put(String key, SiteDescriptor value) throws PermanentSyncException;

	public abstract void remove(String key) throws PermanentSyncException;

	public abstract void clear() throws PermanentSyncException;

	public abstract Set<String> keySet() throws PermanentSyncException;

	public abstract void add(SiteDescriptor value)
			throws PermanentSyncException;

	public abstract void putAll(ISiteListImpl list02)
			throws PermanentSyncException;

	public abstract boolean containsKey(String key) throws PermanentSyncException;
}