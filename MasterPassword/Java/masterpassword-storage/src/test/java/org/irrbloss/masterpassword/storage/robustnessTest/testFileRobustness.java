package org.irrbloss.masterpassword.storage.robustnessTest;

import java.nio.file.Path;
import org.junit.Ignore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import org.irrbloss.masterpassword.storage.SiteDescriptor;
import org.irrbloss.masterpassword.storage.Exceptions.PermanentSyncException;
import org.irrbloss.masterpassword.storage.SiteListImpl.FileSystemHighLevelWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.IFileSystemWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.SiteListFile;
import org.irrbloss.masterpassword.storage.test.util.TempFileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@Ignore
public class testFileRobustness {

	private Path tempDir;
	
	@Before
	public void setUp() throws Exception {		
		this.tempDir = TempFileManager.setup();                      
	}

	@After
	public void tearDown() throws Exception {
		TempFileManager.cleanup(this.tempDir);
	}

	@Test 
	public void testWriteSynchronous() throws NoSuchAlgorithmException, PermanentSyncException {
		//Generate 1000 text strings		
		
		IFileSystemWrapper low = new FileSystemLowLevelWrapperUnreliable(0,0.95);
		IFileSystemWrapper high = new FileSystemHighLevelWrapper(low);
		SiteListFile siteList = new SiteListFile(this.tempDir, high);
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

}
