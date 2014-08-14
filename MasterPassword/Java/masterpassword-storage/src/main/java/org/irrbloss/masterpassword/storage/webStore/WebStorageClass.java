package org.irrbloss.masterpassword.storage.webStore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.irrbloss.masterpassword.storage.test.webStore.NetworkSecrets;

public class WebStorageClass {

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

	public void uploadNewFile( String fileName, Serializable data ) throws BadWebResponse
	{		
		HashMap<String,String> map = this.buildBasicHashMap();
		map.put("fileName", fileName );
		map.put("fileContents", QueryBuilderClass.convertToBase64(data) );
		
		String rValue = QueryBuilderClass.httpPost(
				this.rootAddres + "createNewFile.php", map);

		this.assertReturnValue(rValue);
	}
	
	public void uploadFileAndOverwrite ( FileListEntry fileID, Serializable data ) throws BadWebResponse 
	{
		HashMap<String,String> map = this.buildBasicHashMap();	
		map.put("fileID", Integer.toString(fileID.getFileID()) );
		map.put("fileContents", QueryBuilderClass.convertToBase64(data) );
		
		String rValue = QueryBuilderClass.httpPost(
				this.rootAddres + "overwriteFile.php", map);
		
		this.assertReturnValue(rValue);

	}
	
	public void deleteFile ( FileListEntry fileID ) throws BadWebResponse 
	{
		HashMap<String,String> map = this.buildBasicHashMap();	
		map.put("fileID", Integer.toString(fileID.getFileID()) );
				
		String rValue = QueryBuilderClass.httpPost(
				this.rootAddres + "deleteFile.php", map);
		
		this.assertReturnValue(rValue);
	
	}
	
	public List<FileListEntry> listFiles( ) throws BadWebResponse 
	{
		HashMap<String,String> map = this.buildBasicHashMap();	
		
		String rValue = QueryBuilderClass.httpPost(
				this.rootAddres  + "listFiles.php", map);
		
		
		this.assertReturnValue(rValue);
		
		
		return FileListParser.parseFileList(rValue);
		
	}
	
	public Object getFile ( FileListEntry fileID ) throws BadWebResponse 
	{
		HashMap<String,String> map = this.buildBasicHashMap();	
		map.put("fileID", Integer.toString(fileID.getFileID()) );
				
		String rValue = QueryBuilderClass.httpPost(
				this.rootAddres + "getFile.php", map);
		
		this.assertReturnValue(rValue);
		
		//Drop the first 4 characters that are header "OK: ".		
		return QueryBuilderClass.convertFromBase64(rValue.substring(4));
	
	}
		
	protected void assertReturnValue( String str ) throws BadWebResponse 
	{
		if ( !(str.startsWith("OK") || str.startsWith("<?xml" ) ) ) {
			throw new BadWebResponse ( str );
		}
	}
	
	public void createUser() throws BadWebResponse {		
		HashMap<String,String> map = this.buildBasicHashMap();			
		map.put("privateKey", this.userCreationKey  );
		map.put("email", "test@armyr.se");
		map.put("password1", this.password );
		map.put("password2", this.password );
		
		String rValue = QueryBuilderClass.httpPost(
				this.rootAddres + "createUser.php", map);
		this.assertReturnValue(rValue);
		
		map.put("privateKey", NetworkSecrets.getPrivateKey()  );
		rValue = QueryBuilderClass.httpPost(
				this.rootAddres + "verifyEmail.php", map);
		this.assertReturnValue(rValue);
	}

}
