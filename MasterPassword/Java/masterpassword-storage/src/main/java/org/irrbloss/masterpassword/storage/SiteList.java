package org.irrbloss.masterpassword.storage;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.irrbloss.masterpassword.storage.ISiteListImpl.Implementations;
import org.irrbloss.masterpassword.storage.Exceptions.PermanentSyncException;

public class SiteList implements Runnable {

	//The worker thread that does all of the async stuff (The write operations to be precise).
	private Thread worker;

	//A queue of async tasks to perform. This queue will be accessed from the 
	//worker thread above so all access must be thread-safe.
	//TODO: Change this back to a blocking queue.
	private final Queue<WorkPacket> queue;

	//A lock that keeps the access to the queue above thread-safe.
	private Lock queueLock;

	//A list of the implementations backing this storage up. These will all be synced to contain the
	//Union of their fileContent. The implementations must manage their own thread-safety.
	private Set<ISiteListImpl> implementations;
	private Lock implLock;

	//One implementation is used to read from. Reads are done synchronously, 
	//so this implementation should be a RAM implementation.
	private ISiteListImpl readImpl;

	public SiteList() {
		this.implementations = new HashSet<ISiteListImpl>();		
		//TODO: Replace this with a Read-Write queueLock in the future. Not essential.
		this.queueLock = new ReentrantLock();
		this.implLock = new ReentrantLock();
		this.queue = new LinkedList<WorkPacket>();
		this.worker = new Thread(this);
		this.worker.start();

	}

	public int size() throws PermanentSyncException {		
		int size;
		try {
			this.queueLock.lock();		
			size = this.readImpl.size();
		} finally {
			this.queueLock.unlock();
		}
		return size;
	}

	public SiteDescriptor get(String key) throws PermanentSyncException {		
		return this.readImpl.get(key);
	}

	public void put(String key, SiteDescriptor value) throws PermanentSyncException {
		if ( value == null ) {
			throw new NullPointerException("No nulls in this map");
		}

		WorkPacket w = WorkPacket.CreatePut( key, value );		

		this.queueLock.lock();
		try {					
			this.queue.add(w);
		} finally {
			this.queueLock.unlock();
		}		
	}

	public void remove(String key) throws PermanentSyncException {
		WorkPacket w = WorkPacket.createRemove(key);
		this.queueLock.lock();
		try {
			this.queue.add(w);
		} finally {
			this.queueLock.unlock();
		}
	}

	public void clear() throws PermanentSyncException {
		WorkPacket w = WorkPacket.createClear();
		this.queueLock.lock();
		try {
			this.queue.add(w);
		} finally {
			this.queueLock.unlock();
		}

	}

	public Set<String> keySet() throws PermanentSyncException {		
		Set<String> rValue = this.readImpl.keySet();						
		return rValue;
	}

	public void put(SiteDescriptor value) throws PermanentSyncException 
	{
		this.put(value.getSiteName(), value);
	}

	public Implementations[] listImplementations() {						
		Implementations[] rValue;

		//Make a thread-safe copy of the current list of implementations. Better safe....
		Collection<ISiteListImpl> implCopy = getSafeImplList();

		rValue = new Implementations[implCopy.size()];
		int i = 0;
		for ( ISiteListImpl impl: implCopy ) {						
			rValue[i++] = impl.implementationCode();
		}
		
		return rValue;
	}

	public boolean isInSync() {		
		boolean sync; 
		this.queueLock.lock();
		try {
			sync = this.queue.isEmpty();
		} finally {
			this.queueLock.unlock();
		}

		return sync;

	}

	public void waitForSync() throws PermanentSyncException {
		while(!this.isInSync()) {
			try {
				synchronized(this) {
					this.wait();
				}
			} catch (InterruptedException e) {
				//Do nothing.
			}
		}
	}


	@Override
	public void run() {
		try {
			while(true) {
				boolean isEmpty = false;
				try {
					this.queueLock.lock();
					isEmpty = this.queue.isEmpty();
				} finally {
					this.queueLock.unlock();
				}

				if ( isEmpty ) {
					Thread.sleep(11);
				} else {					
					WorkPacket w = null;
					try {
						this.queueLock.lock();
						w = this.queue.peek();						
					} finally {
						this.queueLock.unlock();
					}

					this.process( w );

					try {
						this.queueLock.lock();
						this.queue.remove();
					} finally {
						this.queueLock.unlock();
					}
					
					synchronized(this) {
						this.notify();
					}
				}
			}
		} catch (InterruptedException | PermanentSyncException e) {
			System.err.println ( "An error occurred. Given up. This thread will stop now.");
			
			try {
				this.queueLock.lock();
				this.queue.clear();
			} finally {
				this.queueLock.unlock();				
			}
			
			synchronized(this) {				
				this.notify();
			}
			
			e.printStackTrace();
		}
	}

	private void process( WorkPacket w ) throws PermanentSyncException {
		switch ( w.op ) {		
		case PUT:
		{
			//Make a thread-safe copy of the current list of implementations. Better safe....
			Set<ISiteListImpl> impl = getSafeImplList();

			for ( ISiteListImpl inst : impl ) {				
				inst.put(w.key, w.value);
			}
			break;
		}		
		case INIT:
		{
			w.implementation.init();
			if ( this.readImpl != null && this.readImpl != w.implementation ) {
				w.implementation.putAll(this.readImpl);
			}

			this.implLock.lock();
			try {					
				this.implementations.add(w.implementation);
			} finally {
				this.implLock.unlock();
			}
			break;
		}

		/* Not currently used. Init does the sync as well.
		  case SYNC:
			this.readImpl.putAll(w.implementation);
			w.implementation.putAll(this.readImpl);
			break; */
		case REMOVE:
		{
			//Make a thread-safe copy of the current list of implementations. Better safe....
			Collection<ISiteListImpl> impl = getSafeImplList();

			for ( ISiteListImpl inst : impl ) {				
				inst.remove(w.key);
			}
			break;
		}
		case CLEAR:
		{
			//Make a thread-safe copy of the current list of implementations. Better safe....
			Collection<ISiteListImpl> impl = getSafeImplList();

			for ( ISiteListImpl inst : impl ) {				
				inst.clear();
			}
			break;
		}
		default:
		{
			throw new Error("Not implemented");
		}
		}
	}

	private Set<ISiteListImpl> getSafeImplList() {
		Set<ISiteListImpl> rValue = new HashSet<ISiteListImpl>();
		this.implLock.lock();
		try {
			for ( ISiteListImpl impl : this.implementations ) {
				rValue.add(impl);
			}
		} finally {
			this.implLock.unlock();
		}
		return rValue;
	}

	public void addImpl(ISiteListImpl instance ) { 
		this.addImpl(instance, false);
	}

	/**
	 * This function adds a backing implementation. After adding an implementation, make 
	 * sure to wait for sync before running any other operations as some behavior is 
	 * undefined before the sync is complete.  
	 * @param instance The instance to add
	 * @param isReadImpl If this instance should be the one used for reads.
	 */

	public void addImpl(ISiteListImpl instance, boolean isReadImpl) {
		WorkPacket w;		

		w = WorkPacket.CreateInit( instance );		
		this.queueLock.lock();
		this.queue.add(w);
		this.queueLock.unlock();		

		Collection<ISiteListImpl> impl = this.getSafeImplList();
		if ( isReadImpl || impl.isEmpty() ) {
			this.readImpl = instance;
		}		
	}

	public Implementations getReadImplType() {
		return this.readImpl.implementationCode();
	}

}
