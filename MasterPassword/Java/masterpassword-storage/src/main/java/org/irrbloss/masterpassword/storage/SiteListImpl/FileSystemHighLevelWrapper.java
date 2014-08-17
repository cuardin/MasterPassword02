package org.irrbloss.masterpassword.storage.SiteListImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
/*
 * A class to handle calls to the underlying file system. This class is not thread safe.
 * This class is expected to deal with any glitches or errors in communication with the 
 * file system. This class should not give up and throw an error until the situation is 
 * completely hopeless.
 */
public class FileSystemHighLevelWrapper implements IFileSystemWrapper {
	private int n;
	private int t;
	private IFileSystemWrapper fs;
	
	public FileSystemHighLevelWrapper( Path rootFolder )
	{
		this(new FileSystemLowLevelWrapper(rootFolder));
	}

	public FileSystemHighLevelWrapper( IFileSystemWrapper fs )
	{
		this.fs = fs;
		this.n = 1;
		this.t = 100;
	}
	
	
	private void simpleSleep() {
		try {
			Thread.sleep(this.t);
		} catch (InterruptedException e1) {
			//Do nothing.
		}				
	}
	
	public Collection<String> listFiles() throws IOException  {
		for ( int i = 0; i <= n; i++ ) {			
			try {	
				return this.fs.listFiles();
			} catch (IOException e) {
				if ( i == n ) {
					throw e;
				}
				this.simpleSleep();
			}						
		}		
		throw new Error("This should never happen");
	}

	public String readFile(String fileName) throws IOException
	{
		for ( int i = 0; i <= n; i++ ) {			
			try {
				return this.fs.readFile(fileName);
			} catch (IOException e) {
				if ( i == n ) {
					throw e;
				}
				this.simpleSleep();
			}						
		}		
		throw new Error("This should never happen");
	}
	
	public void writeFile(String fileName, String content) throws IOException 
	{
		for ( int i = 0; i <= n; i++ ) {						
			try {
				this.fs.writeFile(fileName, content);
				return;
			} catch (IOException e) {
				if ( i == n ) {
					throw e;
				}
				this.simpleSleep();
			}						
		}		
		throw new Error("This should never happen");
	}
	
	public void remove(String fileName) throws IOException 
	{		
		for ( int i = 0; i <= n; i++ ) {						
			try {
				this.fs.remove(fileName);
				return;
			} catch (IOException e) {
				if ( i == n ) {
					throw e;
				}
				this.simpleSleep();
			}						
		}		
		throw new Error("This should never happen");
	}


	public void createFolder() throws IOException
	{
		for ( int i = 0; i <= n; i++ ) {						
			try {
				this.fs.createFolder();
				return;
			} catch (IOException e) {
				if ( i == n ) {
					throw e;
				}
				this.simpleSleep();
			}						
		}		
		throw new Error("This should never happen");
	}

	public void clearFolder() throws IOException
	{
		for ( int i = 0; i <= n; i++ ) {						
			try {
				this.fs.clearFolder();
				return;
			} catch (IOException e) {
				if ( i == n ) {
					throw e;
				}
				this.simpleSleep();
			}						
		}		
		throw new Error("This should never happen");
	}

	public boolean fileExists(String fileName) throws IOException 
	{
		for ( int i = 0; i <= n; i++ ) {						
			try {
				return this.fs.fileExists(fileName);
			} catch (IOException e) {
				if ( i == n ) {
					throw e;
				}
				this.simpleSleep();
			}						
		}		
		throw new Error("This should never happen");	
	}	
}
