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
	
	public FileSystemHighLevelWrapper( )
	{
		this(new FileSystemLowLevelWrapper());
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
	
	public Collection<Path> listFiles(Path folder) throws IOException  {
		for ( int i = 0; i <= n; i++ ) {			
			try {	
				return this.fs.listFiles(folder);
			} catch (IOException e) {
				if ( i == n ) {
					throw e;
				}
				this.simpleSleep();
			}						
		}		
		throw new Error("This should never happen");
	}

	public String readFile(Path entry) throws IOException
	{
		for ( int i = 0; i <= n; i++ ) {			
			try {
				return this.fs.readFile(entry);
			} catch (IOException e) {
				if ( i == n ) {
					throw e;
				}
				this.simpleSleep();
			}						
		}		
		throw new Error("This should never happen");
	}
	
	public void writeFile(Path entry, String content) throws IOException 
	{
		for ( int i = 0; i <= n; i++ ) {						
			try {
				this.fs.writeFile(entry, content);
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
	
	public void remove(Path fileName) throws IOException 
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


	public void createFolder(Path rootFolder) throws IOException
	{
		for ( int i = 0; i <= n; i++ ) {						
			try {
				this.fs.createFolder(rootFolder);
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

	public void clearFolder(final Path rootFolder) throws IOException
	{
		for ( int i = 0; i <= n; i++ ) {						
			try {
				this.fs.clearFolder(rootFolder);
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

	public boolean fileExists(Path filePath) throws IOException 
	{
		for ( int i = 0; i <= n; i++ ) {						
			try {
				return this.fs.fileExists(filePath);
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
