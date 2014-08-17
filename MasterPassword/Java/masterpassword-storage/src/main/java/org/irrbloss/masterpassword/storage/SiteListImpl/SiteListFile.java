package org.irrbloss.masterpassword.storage.SiteListImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.irrbloss.masterpassword.storage.ISiteListImpl;
import org.irrbloss.masterpassword.storage.SiteDescriptor;
import org.irrbloss.masterpassword.storage.Exceptions.PermanentSyncException;

public class SiteListFile implements ISiteListImpl {
	
	private IFileSystemWrapper fs;
	private Lock lock;
	private MessageDigest md;


	@Override
	public void init() throws PermanentSyncException {
		try {
			fs.createFolder();
		} catch (IOException e) {
			throw new PermanentSyncException(e);
		}		
	}

	//Hidden constructor
	public SiteListFile( IFileSystemWrapper fs ) {
		
		this.fs = fs;
		this.lock = new ReentrantLock();
		try {
			//Yes, we use MD5 because this is not crypto, this is just generation sufficiently unique 
			//keys to use as filenames.
			this.md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			//MD5 is a requirement for all Java implementations.
			throw new Error(e);
		}
	}

	@Override
	public int size() throws PermanentSyncException {
		//Return the number of files in the root folder.
		this.lock.lock();
		try {
			return fs.listFiles().size();
		} catch (IOException e) {
			throw new PermanentSyncException(e);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public boolean containsKey(String  key) throws PermanentSyncException {
		String fileName = this.encodeString(key);
		this.lock.lock();		
		try {					
			return fs.fileExists(fileName);		
		} catch (IOException e) {
			throw new PermanentSyncException(e);	
		} finally {
			this.lock.unlock();
		}
	}

	public SiteDescriptor readFromFile( String fileName ) throws PermanentSyncException {
		//Read the fileContent from the file. This has to be locked for thread saefty.
		String content;		
		try {
			this.lock.lock();			
			if ( !this.fs.fileExists(".") ) {
				throw new IOException("Root folder does not exist." );
			} else if ( !this.fs.fileExists(fileName)) {
				return null;
			} else {
				//We are now ready to do the actual reading.
				content = fs.readFile(fileName);
			}
		} catch (IOException e) {
			throw new PermanentSyncException(e);
		} finally {
			this.lock.unlock();
		}
		
		try {
			return SiteDescriptor.fromFormatedText(content);
		} catch (PermanentSyncException e) {
			return null;
		}
	}
	
	@Override	
	public SiteDescriptor get(String key) throws PermanentSyncException {		
		String fileName = this.encodeString(key);
		return this.readFromFile(fileName);
	}

	@Override
	public void remove(String key) throws PermanentSyncException {		
		String fileName = this.encodeString(key);		
		try {
			this.lock.lock();
			fs.remove(fileName);
		} catch (IOException e) {
			throw new PermanentSyncException(e);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public void clear() throws PermanentSyncException {
		this.lock.lock(); 
		try {			
			this.fs.clearFolder();
		} catch (IOException e) {
			throw new PermanentSyncException(e);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public SortedSet<String> keySet() throws PermanentSyncException {
		//Return the number of files in the root folder.		
		SortedSet<String> rValue = new TreeSet<String>();
		Collection<Path> files = null;
		try {
			this.lock.lock();
			files = fs.listFiles();
		} catch (IOException e) {
			throw new PermanentSyncException(e);
		} finally {
			this.lock.unlock();
		}

		for ( Path p : files ) {
			SiteDescriptor site = this.readFromFile(p.getFileName().toString());			
			rValue.add( site.getSiteName() );
		}			
		return rValue;

	}


	@Override
	public void put(String key, SiteDescriptor value) throws PermanentSyncException {
		//We do not allow nulls in this map.
		if ( value == null ) {
			throw new NullPointerException("No nulls in this map");
		}

		//First check if we already have an entry by reading it.
		try {
			this.lock.lock(); //We lock over the entire function since we get and then write to FS.
		
			SiteDescriptor oldValue = this.get(key);
			if ( oldValue == null || oldValue.getCreationTime().before(value.getCreationTime())) {
				String fileName = this.encodeString(key);
				this.fs.writeFile(fileName, value.toFormatedText() );										
			}
		} catch (IOException e) {
			throw new PermanentSyncException(e);
		} finally {
			this.lock.unlock();
		}
	}

	@Override
	public void add(SiteDescriptor value)
			throws PermanentSyncException {
		this.put(value.getSiteName(), value);
	}

	@Override
	public void putAll(ISiteListImpl m) throws PermanentSyncException {
		//Since we behave slightly different with regards to insertion, we need to do this in an orderly fashion.
		//However, since it is a merge, we do not need to lock for the entire operation.
		for ( String key : m.keySet() ) {			
			this.add(m.get(key));			
		}		
	}

	@Override
	public Implementations implementationCode() {
		return Implementations.FILE;
	}

	@Override
	public String implementationName() {
		return "File";
	}

	public String encodeString( String in ) {
		this.lock.lock(); //Supposedly MessageDigest is not thread-safe.
		try {			
			byte[] digest = this.md.digest(in.getBytes());
			return SiteDescriptor.bytesToHex(digest);						
		} finally {
			this.lock.unlock();
		}
	}

}
