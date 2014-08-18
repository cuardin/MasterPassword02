package org.irrbloss.masterpassword.storage.test.webStore;

import java.net.URL;
import java.util.List;

import org.irrbloss.masterpassword.storage.webStore.BadWebResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WebStorageTest {
	
	WebStorageTestHelper helper = null;
	URL baseURL;
	
	@Before
	public void setUp() throws Exception {
		helper = new WebStorageTestHelper();
		baseURL = new URL("http://masterpassword.armyr.se/php_scripts/");
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
		helper.writeFile( "testFile", "testData" );
		List<String> list = helper.listFiles();
		org.junit.Assert.assertEquals(list.size(), 1);
		org.junit.Assert.assertEquals(list.get(0), "testFile");
				
		String data = helper.getFile(list.get(0));
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
		helper.writeFile( "testFile", "testData" );
		List<String> list = helper.listFiles();
		org.junit.Assert.assertEquals(list.size(), 1);		
				
		helper.deleteFile(list.get(0));
		list = helper.listFiles();
		org.junit.Assert.assertEquals(list.size(), 0);				
	}
	
	@Test
	public void testOvrerwriteFile() throws BadWebResponse {				
		helper.createUser();	
		helper.writeFile( "testFile", "testData" );
		List<String> list = helper.listFiles();
		org.junit.Assert.assertEquals(list.size(), 1);		
				
		helper.writeFile(list.get(0), "testData2");
						
		String data = helper.getFile(list.get(0));
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
