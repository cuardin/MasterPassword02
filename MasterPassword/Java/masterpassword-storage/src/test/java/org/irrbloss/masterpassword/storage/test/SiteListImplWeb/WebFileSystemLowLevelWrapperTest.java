package org.irrbloss.masterpassword.storage.test.SiteListImplWeb;

import org.irrbloss.masterpassword.storage.test.webStore.WebStorageTestHelper;
import org.junit.After;
import org.junit.Before;

public class WebFileSystemLowLevelWrapperTest {

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

	String fileContent01 = "736974652E636F6D\n1\nlong";
	String fileName01 = "7AEF11B08719DFB3B922E89EAC4B8D78";

	//This is just a dummy file that is not consistent.
	String fileContent02 = "836974652E636F6D\n1\nlong";
	String fileName02 = "8AEF11B08719DFB3B922E89EAC4B8D78";

	
	/*private void writeTestFile() throws BadWebResponse {		
		this.helper.uploadNewFile(fileName01, fileContent01);
	}*/

	/*private String readTestFile(String fileName) throws BadWebResponse {		
		return helper.getFile(fileName);		
	}*/
	
	/*

	@Test
	public void testListFiles() throws IOException {			
		//Arrange
		this.writeTestFile();
		
		
		//Act
		Collection<Path> fileList = fs.listFiles();
		
		//Assert
		org.junit.Assert.assertEquals( 2, fileList.size() );
		org.junit.Assert.assertEquals(this.tempDir.resolve(this.fileName01), 
				fileList.iterator().next() );		
	}
	*/
	/*
	@Test @Ignore
	public void testReadFile() throws IOException
	{
		//Arrange
		this.writeTestFile();
		FileSystemLowLevelWrapper fs = new FileSystemLowLevelWrapper();
		
		//Act 
		String content = fs.readFile(this.tempDir.resolve(this.fileName01) );
		
		//Assert
		org.junit.Assert.assertEquals(this.fileContent01, content);
	}
		
	@Test @Ignore
	public void testWriteFile() throws IOException
	{
		//Arrange
		FileSystemLowLevelWrapper fs = new FileSystemLowLevelWrapper();
		Path testFile = this.tempDir.resolve(this.fileName01);
		
		//Act
		fs.writeFile(testFile, this.fileContent01);
		
		//Assert
		String readContent = this.readTestFile(testFile);
		org.junit.Assert.assertEquals(this.fileContent01, readContent );
	}
	
	@Test @Ignore
	public void testRemoveFile() throws IOException
	{
		//Arrange
		this.writeTestFile();
		FileSystemLowLevelWrapper fs = new FileSystemLowLevelWrapper();
		Path testFile = this.tempDir.resolve(this.fileName01);
		
		//Act
		fs.remove(testFile);
		
		//Assert
		//We have to test both for existence and nonexistens.
		org.junit.Assert.assertTrue(Files.notExists(testFile));
		org.junit.Assert.assertFalse(Files.exists(testFile));
	
	}

	@Test @Ignore
	public void testCreateFolder() throws IOException
	{
		//Arrange
		String folderName = "testFolder"; 
		FileSystemLowLevelWrapper fs = new FileSystemLowLevelWrapper();
		//Path testFolder = this.tempDir.resolve(folderName);
		
		//Act
		fs.createFolder(testFolder);
		
		//Assert
		org.junit.Assert.assertTrue(Files.exists(testFolder));
		org.junit.Assert.assertTrue(Files.isDirectory(testFolder));
	
	}

	@Test @Ignore
	public void testClearFolder() throws IOException
	{
		//Arrange
		this.writeTestFile();
		FileSystemLowLevelWrapper fs = new FileSystemLowLevelWrapper();		
		
		//Act
		//fs.clearFolder(this.tempDir);
		
		//Assert
		//Check that our directory is now empty
		DirectoryStream<Path> stream = Files.newDirectoryStream(this.tempDir);
		org.junit.Assert.assertFalse( stream.iterator().hasNext() );

	}
*/
}