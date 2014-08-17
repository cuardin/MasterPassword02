package org.irrbloss.masterpassword.storage.test.SiteListImplFile;

import java.io.IOException;
import java.util.Date;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.irrbloss.masterpassword.storage.SiteDescriptor;
import org.irrbloss.masterpassword.storage.Exceptions.PermanentSyncException;
import org.irrbloss.masterpassword.storage.SiteListImpl.IFileSystemWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.SiteListFile;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class SiteListImplFilePutTest {
	
	//private Path testPath = Paths.get("/tmp/");
	//private Path rootPath = testPath.resolve(".mpw");

	@Test(expected=NullPointerException.class)
	public void putTestNull() throws PermanentSyncException 
	{
		SiteListFile file = new SiteListFile(null);
		
		file.put("key", null);		
	}

	@Test
	public void testPutSuccessNoOldValue() throws PermanentSyncException, IOException {		

		//Arrange
		//Create an FS mock
		IFileSystemWrapper fs = EasyMock.createMock(IFileSystemWrapper.class);		

		SiteListFile file = EasyMock.createMockBuilder(SiteListFile.class)
				.withConstructor(fs)
				.addMockedMethod("get").createMock();
		
		String siteName = "site01";
		String siteNameEncoded = file.encodeString("site01");
		SiteDescriptor newEntry = new SiteDescriptor(siteName,1,"long",new Date((long) 2e6));
		
		//Anticipate
		EasyMock.expect(file.get(siteName)).andReturn(null);	
		fs.writeFile(siteNameEncoded,newEntry.toFormatedText());
		
		//Act
		EasyMock.replay(file,fs);
		file.put(siteName, newEntry);
		
		EasyMock.verify(file,fs);
	}

	@Test
	public void testPutSuccessReplace() throws PermanentSyncException, IOException {		

		//Arrange
		//Create an FS mock
		IFileSystemWrapper fs = EasyMock.createMock(IFileSystemWrapper.class);		

		SiteListFile file = EasyMock.createMockBuilder(SiteListFile.class)
				.withConstructor(fs)
				.addMockedMethod("get").createMock();
		
		String siteName = "site01";
		String siteNameEncoded = file.encodeString("site01");
		SiteDescriptor newEntry = new SiteDescriptor(siteName,1,"long",new Date((long) 2e6));
		SiteDescriptor oldEntry = new SiteDescriptor(siteName,1,"long",new Date((long) 1e6));
		
		//Anticipate
		EasyMock.expect(file.get(siteName)).andReturn(oldEntry);	
		fs.writeFile(siteNameEncoded,newEntry.toFormatedText());
		
		//Act
		EasyMock.replay(file,fs);
		file.put(siteName, newEntry);
		
		EasyMock.verify(file,fs);
	}
	
	@Test
	public void testPutNoOpNewerValue() throws PermanentSyncException, IOException {		

		//Arrange
		//Create an FS mock
		IFileSystemWrapper fs = EasyMock.createMock(IFileSystemWrapper.class);		

		SiteListFile file = EasyMock.createMockBuilder(SiteListFile.class)
				.withConstructor(fs)
				.addMockedMethod("get").createMock();
		
		String siteName = "site01";
		//String siteNameEncoded = file.encodeString("site01");
		SiteDescriptor newEntry = new SiteDescriptor(siteName,1,"long",new Date((long) 2e6));
		SiteDescriptor oldEntry = new SiteDescriptor(siteName,1,"long",new Date((long) 1e6));
		
		//Anticipate
		EasyMock.expect(file.get(siteName)).andReturn(newEntry);	
		//No write should be done.
		
		//Act
		EasyMock.replay(file,fs);
		file.put(siteName, oldEntry);
		
		EasyMock.verify(file,fs);
	}
	
	
	@Test
	public void testPutExceptionThrown() throws IOException, PermanentSyncException {		

		//Arrange
		//Create an FS mock
		IFileSystemWrapper fs = EasyMock.createMock(IFileSystemWrapper.class);		

		SiteListFile file = EasyMock.createMockBuilder(SiteListFile.class)
				.withConstructor(fs)
				.addMockedMethod("get").createMock();
		
		String siteName = "site01";
		String siteNameEncoded = file.encodeString("site01");
		SiteDescriptor newEntry = new SiteDescriptor(siteName,1,"long",new Date((long) 2e6));
		
		//Anticipate
		EasyMock.expect(file.get(siteName)).andReturn(null);	
		fs.writeFile(siteNameEncoded,newEntry.toFormatedText());
		EasyMock.expectLastCall().andThrow(new IOException());
		
		//Act
		EasyMock.replay(file,fs);		
		try {
			file.put(siteName, newEntry);
			Assert.fail("An Exception should have been thrown");
		} catch (PermanentSyncException e) {
		}
		
		EasyMock.verify(file,fs);
	}



}
