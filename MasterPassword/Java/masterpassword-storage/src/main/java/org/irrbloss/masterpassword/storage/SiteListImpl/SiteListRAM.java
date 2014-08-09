package org.irrbloss.masterpassword.storage.SiteListImpl;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.irrbloss.masterpassword.storage.ISiteListImpl;
import org.irrbloss.masterpassword.storage.SiteDescriptor;
import org.irrbloss.masterpassword.storage.Exceptions.PermanentSyncException;

//TODO: Change the lock to a read.write-lock
/**
 * This class is basically a thread-safe wrapper around the HashMap class, 
 * with the minimal changes needed for it to conform to the ISiteListImpl
 * interface rather than the classical Map interface.
 *
 */
public class SiteListRAM implements ISiteListImpl {

	private HashMap<String,SiteDescriptor> siteStorage;
	private Lock lock;
	
	public SiteListRAM() {	
		this.siteStorage = new HashMap<String,SiteDescriptor>();
		this.lock = new ReentrantLock();
	}

	@Override
	public int size() {
		int s;
		this.lock.lock();
		try {
			 s = siteStorage.size();
		} finally {
			this.lock.unlock();
		}
		return s;
	}

	@Override
	public boolean containsKey(String key) {
		boolean b;
		this.lock.lock();
		try {
			b = this.siteStorage.containsKey(key);
		} finally {
			this.lock.unlock();
		}
		return b;
	}

	@Override
	public SiteDescriptor get(String key) {
		SiteDescriptor s;
		try {
			this.lock.lock();
			s = this.siteStorage.get(key);
		} finally {
			this.lock.unlock();
		}
		return s;
	}

	/**
	 * This put works a bit different than the usual set put. It only stores 
	 * the value provided, assuming it is newer than what we already have 
	 * stored. It will then not return the old value, but whatever is currently 
	 * stored in the location. 
	 * @throws PermanentSyncException 
	 */
	@Override
	public void put(String key, SiteDescriptor value) {
		//We do not allow null values in these maps.
		if ( value == null ) {
			throw new NullPointerException("No nulls in this map");
		}
		
		//Only add if there is no newer with the same key.
		SiteDescriptor old = this.get(key);
		if ( old == null || value.getCreationTime().after(old.getCreationTime()) ) {
			try {
				this.lock.lock();
				this.siteStorage.put(key, value);
			} finally {
				this.lock.unlock();
			}			
		}	
	}


	/* (non-Javadoc)
	 * @see org.irrbloss.masterpassword.storage.ISiteListImpl#remove(java.lang.Object)
	 */
	@Override
	public void remove(String key) {
		this.lock.lock();
		try {
			this.siteStorage.remove(key);
		} finally {
			this.lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see org.irrbloss.masterpassword.storage.ISiteListImpl#clear()
	 */
	@Override
	public void clear() {
		this.lock.lock();
		try {
			this.siteStorage.clear();
		} finally {
			this.lock.unlock();
		}
	}

	/* (non-Javadoc)
	 * @see org.irrbloss.masterpassword.storage.ISiteListImpl#keySet()
	 */
	@Override
	public Set<String> keySet() {
		Set<String> s;
		this.lock.lock();
		try {
			s = this.siteStorage.keySet();
		} finally {
			this.lock.unlock();
		}
		return s;
	}

	@Override
	public void add(SiteDescriptor value)
			throws PermanentSyncException {
		this.put(value.getSiteName(), value);
	}

	@Override
	public void putAll(ISiteListImpl m) throws PermanentSyncException {		
		//Since this is a merge operation, we do not need to lock.
		for ( String key : m.keySet() ) {			
			this.add(m.get(key));			
		}	
	}

	@Override
	public Implementations implementationCode() {
		return Implementations.RAM;
	}

	@Override
	public String implementationName() {
		return "Ram";
	}

	@Override
	public void init() throws PermanentSyncException {
		//For this implementation, there is nothing to do here.
	}


}
