package org.irrbloss.masterpassword.storage.test.SiteListImplWeb;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.irrbloss.masterpassword.storage.SiteListImpl.IFileSystemWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.WebFileSystemLowLevelWrapper;
import org.irrbloss.masterpassword.storage.webStore.BadWebResponse;
import org.irrbloss.masterpassword.storage.webStore.WebStorageClass;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class WebFileSystemMiscTest {	
	
	@Test
	public void testListFiles() throws BadWebResponse, IOException 
	{
		//Arrange
		WebStorageClass web = EasyMock.createMock(WebStorageClass.class);
		IFileSystemWrapper fs = new WebFileSystemLowLevelWrapper(web);
		
		List<String> rValue = new LinkedList<String>();
		rValue.add("filename01");
		rValue.add("filename02");
		
		//Anticipate
		EasyMock.expect(web.listFiles()).andReturn(rValue);
		EasyMock.replay(web);
		
		//Act
		Collection<String> fileList = fs.listFiles();
		
		//Assert
		Assert.assertEquals(rValue, fileList);
		EasyMock.verify(web);
					
	}
	
	@Test
	public void testListFilesError() throws BadWebResponse 
	{
		//Arrange
		WebStorageClass web = EasyMock.createMock(WebStorageClass.class);
		IFileSystemWrapper fs = new WebFileSystemLowLevelWrapper(web);
		
		List<String> rValue = new LinkedList<String>();
		rValue.add("filename01");
		rValue.add("filename02");
		
		//Anticipate
		EasyMock.expect(web.listFiles()).andThrow(new BadWebResponse("Web b0rked"));
		EasyMock.replay(web);
		
		//Act		
		Collection<String> fileList;
		try {
			fileList = fs.listFiles();
			Assert.fail("An exception should have been thrown");
		} catch (IOException e) {
		}
		
		//Assert			
		EasyMock.verify(web);	
	}
	
}
