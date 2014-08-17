package org.irrbloss.masterpassword.storage.test.SiteListImplFile;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.irrbloss.masterpassword.storage.ISiteListImpl;
import org.irrbloss.masterpassword.storage.SiteDescriptor;
import org.irrbloss.masterpassword.storage.Exceptions.PermanentSyncException;
import org.irrbloss.masterpassword.storage.SiteListImpl.IFileSystemWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.SiteListFile;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class SiteListImplFileMiscTest {

	private String siteName = "Hello World";
	private String siteNameHash = "B10A8DB164E0754105B7A99BE72E3FE5";
	//private String fileContent = "736974652E636F6D\n1\nlong\n1000000";
	
	//private Path testPath = Paths.get("/tmp/");
	//private Path rootPath = testPath.resolve(".mpw");

	
	@Test
	public void testHash() {
		//Test that we encode the string to the same thing every time;

		//Arrange		
		SiteListFile list = new SiteListFile( null);

		//Act
		org.junit.Assert.assertEquals( list.encodeString(this.siteName), list.encodeString(this.siteName));

		//Assert
		org.junit.Assert.assertEquals( this.siteNameHash, list.encodeString(this.siteName));

	}

	@Test
	public void testInit() throws PermanentSyncException, IOException {		

		//Arrange
		//Create an FS mock
		IFileSystemWrapper fs = EasyMock.createMock(IFileSystemWrapper.class);		
		ISiteListImpl file = new SiteListFile(fs);

		//Set expectations
		fs.createFolder();

		//Act
		EasyMock.replay(fs);						
		file.init();

		//Assert
		EasyMock.verify(fs);

	}
	
	@Test
	public void testGet() throws PermanentSyncException, IOException {		

		//Arrange
		//Create an FS mock
		IFileSystemWrapper fs = EasyMock.createMock(IFileSystemWrapper.class);		

		SiteListFile file = EasyMock.createMockBuilder(SiteListFile.class)
				.withConstructor(fs)
				.addMockedMethod("readFromFile").createMock();		
		
		//Set expectations
		SiteDescriptor expectedAnswer = new SiteDescriptor(this.siteName);
		EasyMock.expect(file.readFromFile(this.siteNameHash))
			.andReturn(expectedAnswer );

		//Act
		EasyMock.replay(fs);
		EasyMock.replay(file);
		
		SiteDescriptor answer = file.get(this.siteName);

		//Assert
		EasyMock.verify(fs);
		EasyMock.verify(file);
		org.junit.Assert.assertEquals(expectedAnswer, answer);

	}
	
	@Test
	public void readRemoveSuccess() throws IOException, PermanentSyncException
	{
		//*********************
		//Arrange
		IFileSystemWrapper fs = EasyMock.createMock(IFileSystemWrapper.class);
		SiteListFile file = new SiteListFile(fs);

		//********************
		//Anticipate

		//Check that the file is deleted.
		fs.remove(this.siteNameHash);

		//Act
		 EasyMock.replay(fs);
		file.remove(siteName);

		//Assert		
		 EasyMock.verify(fs);

	}

	@Test
	public void readRemoveException() throws IOException
	{
		//*********************
		//Arrange
		IFileSystemWrapper fs = EasyMock.createMock(IFileSystemWrapper.class);
		SiteListFile file = new SiteListFile( fs);

		//********************
		//Anticipate

		//Try to delete a file that does not exist.
		fs.remove(this.siteNameHash);
		EasyMock.expectLastCall().andThrow(new IOException());

		//Act
		 EasyMock.replay(fs);		
		try {
			file.remove(siteName);
			org.junit.Assert.fail("An exception should have been thrown");
		} catch (PermanentSyncException e) {
		}

		//Assert		
		 EasyMock.verify(fs);

	}
	
	@Test
	public void clearSuccess() throws IOException, PermanentSyncException
	{		
		
		//*********************
		//Arrange
		IFileSystemWrapper fs = EasyMock.createMock(IFileSystemWrapper.class);
		SiteListFile file = new SiteListFile( fs);

		//********************
		//Anticipate

		//Check that the file is deleted.
		fs.clearFolder();

		//Act
		EasyMock.replay(fs);
		file.clear();

		//Assert		
		EasyMock.verify(fs);

	}

	@Test
	public void clearException() throws IOException
	{
		
		//*********************
		//Arrange
		IFileSystemWrapper fs = EasyMock.createMock(IFileSystemWrapper.class);
		SiteListFile file = new SiteListFile( fs);

		//********************
		//Anticipate

		//Try to delete a file that does not exist.
		fs.clearFolder();
		EasyMock.expectLastCall().andThrow(new IOException());

		//Act
		 EasyMock.replay(fs);		
		try {
			file.clear();
			org.junit.Assert.fail("An exception should have been thrown");
		} catch (PermanentSyncException e) {
		}

		//Assert		
		 EasyMock.verify(fs);

	}

	@Test
	public void testKeySetSuccess() throws PermanentSyncException, IOException {		

		//Arrange
		//Create an FS mock
		IFileSystemWrapper fs = EasyMock.createMock(IFileSystemWrapper.class);		

		SiteListFile file = EasyMock.createMockBuilder(SiteListFile.class)
				.withConstructor(fs)
				.addMockedMethod("readFromFile").createMock();		
		
		//Set expectations
		SortedSet<String> expectedAnswer01 = new TreeSet<String>();
		expectedAnswer01.add("site01encoded");
		expectedAnswer01.add("site02encoded");
		
		SiteDescriptor expectedAnswer02 = new SiteDescriptor("site01");
		SiteDescriptor expectedAnswer03 = new SiteDescriptor("site02");
		
		SortedSet<String> expectedAnswer04 = new TreeSet<String>();
		expectedAnswer04.add("site01");
		expectedAnswer04.add("site02");
		
		EasyMock.expect(fs.listFiles())
			.andReturn(expectedAnswer01);
		EasyMock.expect(file.readFromFile("site01encoded"))
			.andReturn(expectedAnswer02);
		EasyMock.expect(file.readFromFile("site02encoded"))
			.andReturn(expectedAnswer03);
		
		//Act
		EasyMock.replay(fs);
		EasyMock.replay(file);
		
		SortedSet<String> answer = file.keySet();

		//Assert
		EasyMock.verify(fs);		
		org.junit.Assert.assertEquals(expectedAnswer04, answer);

	}
	
	@Test
	public void testKeySetException() throws IOException {		

		//Arrange
		//Create an FS mock
		IFileSystemWrapper fs = EasyMock.createMock(IFileSystemWrapper.class);		

		SiteListFile file = new SiteListFile(fs);		
		
		//Anticipate
		
		EasyMock.expect(fs.listFiles()).andThrow(new IOException());

		//Act
		EasyMock.replay(fs);		
		
		try {
			file.keySet();
			Assert.fail("An exception should have been thrown.");
		} catch (PermanentSyncException e) {
		}

		//Assert
		EasyMock.verify(fs);				

	}	
	
	@Test
	public void testPutAll() throws PermanentSyncException 
	{
		//Arrange
		//Create a mock of the other SiteList.
		ISiteListImpl other = EasyMock.createMock(ISiteListImpl.class);
		IFileSystemWrapper fs = EasyMock.createMock(IFileSystemWrapper.class);
		
		SiteListFile file = EasyMock.createMockBuilder(SiteListFile.class)
				.withConstructor(fs)
				.addMockedMethod("put").addMockedMethod("get").createMock();		

		//Anticipate
		SortedSet<String> keySet = new TreeSet<String>();
		keySet.add("site01");
		keySet.add("site02");
		
		EasyMock.expect(other.keySet()).andReturn(keySet);
		EasyMock.expect(other.get("site01")).andReturn(new SiteDescriptor("site01"));
		file.add(new SiteDescriptor("site01"));
		EasyMock.expect(other.get("site02")).andReturn(new SiteDescriptor("site02"));
		file.add(new SiteDescriptor("site02"));
		
		//Act
		EasyMock.replay(file,other);
		file.putAll(other);
		
		//Assert
		EasyMock.verify(file,other);
		
	}
}
