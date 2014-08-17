package org.irrbloss.masterpassword.storage.profilerApp;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import org.irrbloss.masterpassword.storage.SiteDescriptor;
import org.irrbloss.masterpassword.storage.Exceptions.PermanentSyncException;
import org.irrbloss.masterpassword.storage.SiteListImpl.FileSystemHighLevelWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.IFileSystemWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.SiteListFile;
import org.irrbloss.masterpassword.storage.robustnessTest.FileSystemLowLevelWrapperUnreliable;
import org.irrbloss.masterpassword.storage.test.util.TempFileManager;

public class ProfilerApp {

	public void tearDown() throws Exception {
		
	}
	
	public static void testWriteSynchronous(Path tempDir) throws NoSuchAlgorithmException, PermanentSyncException {
		//Generate 1000 text strings		
		
		IFileSystemWrapper low = new FileSystemLowLevelWrapperUnreliable(tempDir,0,0.9);
		IFileSystemWrapper high = new FileSystemHighLevelWrapper(low);
		SiteListFile siteList = new SiteListFile(high);
		siteList.init();
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		int n = 100;
		Vector<String>keys = new Vector<String>(n);
		
		for ( int i = 0; i < n; i++ ) {
			byte[] digest = md.digest(Integer.toBinaryString(i).getBytes());			
			keys.add(SiteDescriptor.bytesToHex(digest) );
			siteList.add(new SiteDescriptor(keys.get(i)));
			org.junit.Assert.assertEquals(keys.get(i), siteList.get(keys.get(i)).getSiteName() );
		}
		//System.out.println("Done");
	}

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, PermanentSyncException {
		System.out.println ( "Ready to start?");
		 //BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		 //String s = bufferRead.readLine();
		 System.in.read();		 
		   		
		    
		Path tempDir = null;
		try {
			tempDir = TempFileManager.setup();
			testWriteSynchronous(tempDir);
		} finally {
			TempFileManager.cleanup(tempDir);
		}
		

	}

}
