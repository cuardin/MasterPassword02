package org.irrbloss.masterpassword.storage.test.webStore;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

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
	public void testUploadAndDownloadNewFile() throws IOException {				
		helper.initialize();
		//Check that the file does not exist
		org.junit.Assert.assertFalse ( helper.fileExists("testFile"));
		
		helper.writeFile( "testFile", "testData" );
		Collection<String> list = helper.listFiles();
		org.junit.Assert.assertEquals(list.size(), 1);
		org.junit.Assert.assertEquals(list.iterator().next(), "testFile");
		
		//Check that the file does exist now
		org.junit.Assert.assertTrue ( helper.fileExists("testFile"));
				
		String data = helper.readFile(list.iterator().next());
		org.junit.Assert.assertEquals("testData", data);		
	}
	
	@Test
	public void testEradicateUser() throws IOException  {
		helper.initialize(); //First create a user.
		helper.eradicateTestUser();
		org.junit.Assert.assertFalse( helper.testAuthentication() );		
	}

	@Test
	public void testDeleteFile() throws IOException {				
		helper.initialize();	
		helper.writeFile( "testFile", "testData" );
		Collection<String> list = helper.listFiles();
		org.junit.Assert.assertEquals(list.size(), 1);		
				
		helper.remove(list.iterator().next());
		list = helper.listFiles();
		org.junit.Assert.assertEquals(list.size(), 0);				
	}
	
	@Test
	public void testOvrerwriteFile() throws IOException {				
		helper.initialize();	
		helper.writeFile( "testFile", "testData" );
		Collection<String> list = helper.listFiles();
		org.junit.Assert.assertEquals(list.size(), 1);		
				
		helper.writeFile(list.iterator().next(), "testData2");
						
		String data = helper.readFile(list.iterator().next());
		org.junit.Assert.assertEquals("testData2", data);
	}

	@Test
	public void testAuthenticateUser() throws IOException {				
		helper.initialize();	
		org.junit.Assert.assertTrue( helper.testAuthentication() );		
		helper.setPassword( "newPass" );
		org.junit.Assert.assertFalse( helper.testAuthentication() );
	}
}
