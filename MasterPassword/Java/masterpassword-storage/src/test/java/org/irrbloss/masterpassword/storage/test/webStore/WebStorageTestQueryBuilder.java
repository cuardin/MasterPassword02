package org.irrbloss.masterpassword.storage.test.webStore;

import java.util.HashMap;

import org.irrbloss.masterpassword.storage.webStore.QueryBuilderClass;
import org.junit.Test;

public class WebStorageTestQueryBuilder {

	private String baseURL = "http://masterpassword.armyr.se/php_scripts/";
	
	@Test
	public void testConvertFromAndToBase64() {
		//Circular argument, but who cares.
		String a = "This is a test string";
		String base64 = QueryBuilderClass.convertToBase64(a);
		String b = (String)QueryBuilderClass.convertFromBase64(base64);
		
		org.junit.Assert.assertEquals ( a, b );
		org.junit.Assert.assertEquals ( 
				"rO0ABXQAFVRoaXMgaXMgYSB0ZXN0IHN0cmluZw==", base64 );
	}

	@Test
	public void testQueryGet()
	{
		HashMap<String,String> map = new HashMap<String,String>();
			    
		map.put("username", "uname");		
		
		String result = QueryBuilderClass.httpPost( this.baseURL + "testFile.php", map );
		org.junit.Assert.assertEquals("uname ", result); //Not sure why the extra white-space, but who cares.
	}
	
	@Test
	public void testQueryGetFail()
	{
		HashMap<String,String> map = new HashMap<String,String>();
			    
		map.put("--", "--");		
		
		String result = QueryBuilderClass.httpPost( this.baseURL + "testFile.php", map );
		org.junit.Assert.assertEquals("<h1>FAIL</h1>", result.substring(0, 13));
	}
	
	@Test
	public void testQueryPost()
	{
		HashMap<String,String> map = new HashMap<String,String>();
			    
		map.put("username", "uname");		
		
		String result = QueryBuilderClass.httpPost( this.baseURL + "testFile.php", map );
		org.junit.Assert.assertEquals("uname ", result); //Not sure why the extra white-space, but who cares.
	}
	
	@Test
	public void testQueryPostFail()
	{
		HashMap<String,String> map = new HashMap<String,String>();
			    
		map.put("--", "--");		
		
		String result = QueryBuilderClass.httpPost( this.baseURL + "testFile.php", map );
		org.junit.Assert.assertEquals("<h1>FAIL</h1>", result.substring(0, 13));
	}
	
}
