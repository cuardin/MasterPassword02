package org.irrbloss.masterpassword.storage.SiteListImpl;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.irrbloss.masterpassword.storage.webStore.BadWebResponse;
import org.irrbloss.masterpassword.storage.webStore.WebStorageClass;
/*
 * A class to handle calls to the underlying file system. This class is not thread safe.
 * This class gives up on any error and should therefore be wrapped into something that 
 * gives it a bit more robustness. 
 */
public class WebFileSystemLowLevelWrapper implements IFileSystemWrapper {	
		
	private WebStorageClass netInterface;
	
	public WebFileSystemLowLevelWrapper(WebStorageClass net) {				
		this.netInterface = net;
	}

	@Override
	public Collection<String> listFiles() throws IOException {
		try {
			List<String> listFiles = this.netInterface.listFiles();
			return listFiles;
		} catch (BadWebResponse e) {
			throw new IOException(e);
		}
	}

	@Override
	public String readFile(String fileName) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void writeFile(String fileName, String content) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(String fileName) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createFolder() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearFolder() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean fileExists(String fileName) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

}
