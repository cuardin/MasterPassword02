package org.irrbloss.masterpassword.storage.test.webStore;

import org.irrbloss.masterpassword.storage.webStore.BadWebResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class WebStorageTestHelperTest {
	
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
	public void testCreateUser() throws BadWebResponse {		
		helper.createUser();	
		org.junit.Assert.assertTrue( helper.testAuthentication() );
	
	}
	
	@Test
	public void testEradicateUser() throws BadWebResponse {
		helper.createUser(); //First create a user.
		helper.eradicateTestUser();
		org.junit.Assert.assertFalse( helper.testAuthentication() );		
	}

}
