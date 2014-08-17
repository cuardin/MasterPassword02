package org.irrbloss.masterpassword.storage.test.webStore;

import java.util.List;

import org.irrbloss.masterpassword.storage.webStore.BadWebResponse;
import org.irrbloss.masterpassword.storage.webStore.FileListEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WebStorageTest {
	
	WebStorageTestHelper helper = null;
	
	@Before
	public void setUp() throws Exception {
		helper = new WebStorageTestHelper();				
		try {
			helper.eradicateTestUser();
		} catch ( Exception e ) {
			//Do nothing.
		}
		
	}

	@After
	public void tearDown() throws Exception {		
		try {
			helper.eradicateTestUser();
		} catch ( Exception e ) {
			//Do nothing.
		}		
	}
	
	@Test
	public void testUploadAndDownloadNewFile() throws BadWebResponse {				
		helper.createUser();	
		helper.uploadNewFile( "testFile", "testData" );
		List<FileListEntry> list = helper.listFiles();
		org.junit.Assert.assertEquals(list.size(), 1);
		org.junit.Assert.assertEquals(list.get(0).getFileName(), "testFile");
				
		String data = helper.getFile(list.get(0).getFileName());
		org.junit.Assert.assertEquals("testData", data);		
	}
	
	@Test
	public void testEradicateUser() throws BadWebResponse {
		helper.createUser(); //First create a user.
		helper.eradicateTestUser();
		org.junit.Assert.assertFalse( helper.testAuthentication() );		
	}

	@Test
	public void testDeleteFile() throws BadWebResponse {				
		helper.createUser();	
		helper.uploadNewFile( "testFile", "testData" );
		List<FileListEntry> list = helper.listFiles();
		org.junit.Assert.assertEquals(list.size(), 1);		
				
		helper.deleteFile(list.get(0).getFileName());
		list = helper.listFiles();
		org.junit.Assert.assertEquals(list.size(), 0);				
	}
	
	@Test
	public void testOvrerwriteFile() throws BadWebResponse {				
		helper.createUser();	
		helper.uploadNewFile( "testFile", "testData" );
		List<FileListEntry> list = helper.listFiles();
		org.junit.Assert.assertEquals(list.size(), 1);		
				
		helper.uploadFileAndOverwrite(list.get(0).getFileName(), "testData2");
						
		String data = helper.getFile(list.get(0).getFileName());
		org.junit.Assert.assertEquals("testData2", data);
	}

	@Test
	public void testAuthenticateUser() throws BadWebResponse {				
		helper.createUser();	
		org.junit.Assert.assertTrue( helper.testAuthentication() );		
		helper.setPassword( "newPass" );
		org.junit.Assert.assertFalse( helper.testAuthentication() );
	}
}