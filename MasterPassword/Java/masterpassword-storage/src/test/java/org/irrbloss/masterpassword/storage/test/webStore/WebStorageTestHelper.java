package org.irrbloss.masterpassword.storage.test.webStore;

import java.io.IOException;
import java.util.HashMap;

import org.irrbloss.masterpassword.storage.webStore.BadWebResponse;
import org.irrbloss.masterpassword.storage.webStore.QueryBuilderClass;
import org.irrbloss.masterpassword.storage.webStore.WebStorageClass;


public class WebStorageTestHelper extends WebStorageClass {

	public WebStorageTestHelper() {
		this.username = "testUserJava";
		this.password = "testPass";		
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return this.username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void eradicateTestUser() throws IOException {
		try {
			HashMap<String,String> map = this.buildBasicHashMap();			
			map.put("privateKey", NetworkSecrets.getPrivateKey());
			map.put("password1", this.password );
			map.put("password2", this.password );

			String rValue = QueryBuilderClass.httpPost(
					this.rootAddres + "eradicateUser.php", map);

			this.assertReturnValue(rValue);
		} catch ( BadWebResponse e ) {
			throw new IOException(e);
		}

	}




}
