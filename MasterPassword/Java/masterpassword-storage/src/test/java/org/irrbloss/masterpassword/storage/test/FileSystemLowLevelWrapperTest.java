package org.irrbloss.masterpassword.storage.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.irrbloss.masterpassword.storage.SiteListImpl.FileSystemLowLevelWrapper;
import org.irrbloss.masterpassword.storage.test.util.TempFileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileSystemLowLevelWrapperTest {

	private Path tempDir;
	String fileContent01 = "736974652E636F6D\n1\nlong";
	String fileName01 = "7AEF11B08719DFB3B922E89EAC4B8D78";

	//This is just a dummy file that is not consistent.
	String fileContent02 = "836974652E636F6D\n1\nlong";
	String fileName02 = "8AEF11B08719DFB3B922E89EAC4B8D78";

	@Before
	public void setUp() throws Exception {		
		this.tempDir = TempFileManager.setup();                        
	}

	@After
	public void tearDown() throws Exception {
		TempFileManager.cleanup(this.tempDir);
	}	
	
	private void writeTestFile() throws IOException {		
		
		File f = this.tempDir.resolve(fileName01).toFile();	
		try ( BufferedWriter w = new BufferedWriter(new FileWriter(f)) ) {
			w.write(fileContent01);
		}
		
		f = this.tempDir.resolve(fileName02).toFile();	
		try ( BufferedWriter w = new BufferedWriter(new FileWriter(f)) ) {
			w.write(fileContent02);
		}
	}

	private String readTestFile(Path testFile) throws IOException {		
		File f = testFile.toFile();
		
		try ( BufferedReader w = new BufferedReader(new FileReader(f)) ) {
			char buffer[] = new char[512];
			w.read(buffer);
			return new String(buffer).trim();
		}
	}

	@Test
	public void testListFiles() throws IOException {			
		//Arrange
		this.writeTestFile();
		FileSystemLowLevelWrapper fs = new FileSystemLowLevelWrapper();
		
		//Act
		Collection<Path> fileList = fs.listFiles(this.tempDir);
		
		//Assert
		org.junit.Assert.assertEquals( 2, fileList.size() );
		org.junit.Assert.assertEquals(this.tempDir.resolve(this.fileName01), 
				fileList.iterator().next() );		
	}
	
	@Test
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
		
	@Test
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
	
	@Test
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

	@Test
	public void testCreateFolder() throws IOException
	{
		//Arrange
		String folderName = "testFolder"; 
		FileSystemLowLevelWrapper fs = new FileSystemLowLevelWrapper();
		Path testFolder = this.tempDir.resolve(folderName);
		
		//Act
		fs.createFolder(testFolder);
		
		//Assert
		org.junit.Assert.assertTrue(Files.exists(testFolder));
		org.junit.Assert.assertTrue(Files.isDirectory(testFolder));
	
	}

	@Test
	public void testClearFolder() throws IOException
	{
		//Arrange
		this.writeTestFile();
		FileSystemLowLevelWrapper fs = new FileSystemLowLevelWrapper();		
		
		//Act
		fs.clearFolder(this.tempDir);
		
		//Assert
		//Check that our directory is now empty
		DirectoryStream<Path> stream = Files.newDirectoryStream(this.tempDir);
		org.junit.Assert.assertFalse( stream.iterator().hasNext() );

	}

}
