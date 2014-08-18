package org.irrbloss.masterpassword.storage.webStore;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.irrbloss.masterpassword.storage.test.webStore.NetworkSecrets;

public class WebStorageClass   
{

	protected String username = null; //"cuardin";
	protected String password = null;
	protected boolean needLogin = true;
	protected String rootAddres = "http://masterpassword.armyr.se/php_scripts/";
	private String userCreationKey = "UPP7fXLerV";

	public boolean getNeedLogin() {
		return this.needLogin;
	}
	
	public void setUserAndPass( String username, String password ) 
	{
		this.username = username;
		this.password = password;	
	}
	
	public boolean testAuthentication() 
	{
		HashMap<String,String> map = this.buildBasicHashMap();
		if ( map == null ) //In case no username or password has been supplied, there is no point in checking.
			return false;
	
		String rValue = QueryBuilderClass.httpPost(
				this.rootAddres + "authenticateUser.php", map);		
		
		try {
			this.assertReturnValue(rValue);
			return true;
		} catch (BadWebResponse e) {			
			//e.printStackTrace();
			return false;
		}
	}
	
	protected HashMap<String, String> buildBasicHashMap() {
		if ( username == null || password == null ) {
			return null;
		}
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("username", username );
		map.put("password", password );		
		return map;
	}
	
	public void writeFile( String fileName, String data ) throws BadWebResponse 
	{
		HashMap<String,String> map = this.buildBasicHashMap();	
		map.put("fileName", fileName );
		map.put("fileContents", data );
		
		String rValue = QueryBuilderClass.httpPost(
				this.rootAddres + "uploadFile.php", map);
		
		this.assertReturnValue(rValue);

	}
	
	public void deleteFile ( String fileName ) throws BadWebResponse 
	{
		HashMap<String,String> map = this.buildBasicHashMap();	
		map.put("fileName", fileName );
				
		String rValue = QueryBuilderClass.httpPost(
				this.rootAddres + "deleteFile.php", map);
		
		this.assertReturnValue(rValue);
	
	}
	
	public List<String> listFiles( ) throws BadWebResponse 
	{
		HashMap<String,String> map = this.buildBasicHashMap();	
		
		String rValue = QueryBuilderClass.httpPost(
				this.rootAddres  + "listFiles.php", map);
		
		
		this.assertReturnValue(rValue);
		
		//TODO: Considder if the list of strings should be generated directly.
		List<FileListEntry> fileList = FileListParser.parseFileList(rValue);
		List<String> rList = new LinkedList<String>();
		for ( FileListEntry e : fileList ) {
			rList.add(e.getFileName());
		}
		return rList;
		
	}
			
	public String getFile(String fileName) throws BadWebResponse {
		HashMap<String,String> map = this.buildBasicHashMap();	
		map.put("fileName", fileName);
				
		String rValue = QueryBuilderClass.httpPost(
				this.rootAddres + "getFile.php", map);
		
		this.assertReturnValue(rValue);
		
		//Drop the first 4 characters that are header "OK: ".		
		return rValue.substring(4);

	}

	protected void assertReturnValue( String str ) throws BadWebResponse 
	{
		if ( !(str.startsWith("OK") || str.startsWith("<?xml" ) ) ) {
			throw new BadWebResponse ( str );
		}
	}
	
	public void createUser() throws BadWebResponse {		
		HashMap<String,String> map = this.buildBasicHashMap();			
		map.put("userCreationKey", this.userCreationKey  );
		map.put("email", "test@armyr.se");		
		
		String rValue = QueryBuilderClass.httpPost(
				this.rootAddres + "createUser.php", map);
		this.assertReturnValue(rValue);
		
		map.put("privateKey", NetworkSecrets.getPrivateKey()  );
		rValue = QueryBuilderClass.httpPost(
				this.rootAddres + "verifyEmail.php", map);
		this.assertReturnValue(rValue);
	}

}
