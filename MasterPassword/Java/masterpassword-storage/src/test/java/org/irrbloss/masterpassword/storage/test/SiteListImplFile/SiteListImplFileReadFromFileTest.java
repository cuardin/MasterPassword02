package org.irrbloss.masterpassword.storage.test.SiteListImplFile;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.irrbloss.masterpassword.storage.SiteDescriptor;
import org.irrbloss.masterpassword.storage.Exceptions.PermanentSyncException;
import org.irrbloss.masterpassword.storage.SiteListImpl.IFileSystemWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.SiteListFile;
import org.junit.Ignore;
import org.junit.Test;

public class SiteListImplFileReadFromFileTest {

	private String fileContent01 = "736974652E636F6D\n1\nlong\n1000000";
	private String fileName01 = "7AEF11B08719DFB3B922E89EAC4B8D78";


	@Test
	public void readFromFileSuccess() throws IOException, PermanentSyncException
	{
		//*********************
		//Arrange
		IFileSystemWrapper fs = createMock(IFileSystemWrapper.class);
		Path testPath = Paths.get("/tmp/");
		Path rootPath = testPath.resolve(".mpw");
		SiteListFile file = new SiteListFile( testPath, fs);

		//********************
		//Anticipate

		//Check that the folder exists
		expect(fs.fileExists(rootPath)).andReturn(true);
		//Check that the file exists
		expect(fs.fileExists(rootPath.resolve(this.fileName01))).andReturn(true);
		//Read the file
		expect(fs.readFile(rootPath.resolve(this.fileName01))).andReturn(this.fileContent01);		

		//Act
		org.easymock.EasyMock.replay(fs);
		SiteDescriptor answer = file.readFromFile(this.fileName01);

		//Assert
		org.junit.Assert.assertEquals(SiteDescriptor.fromFormatedText(this.fileContent01), answer);
		org.easymock.EasyMock.verify(fs);

	}

	@Test
	public void readFromFileMissingRootFolder() throws IOException
	{
		//*********************
		//Arrange
		IFileSystemWrapper fs = createMock(IFileSystemWrapper.class);
		Path testPath = Paths.get("/tmp/");
		Path rootPath = testPath.resolve(".mpw");
		SiteListFile file = new SiteListFile( testPath, fs);

		//********************
		//Anticipate

		//Check that the folder does not exist
		expect(fs.fileExists(rootPath)).andReturn(false);
		//Don't need to check anything more because we should exit now.		

		//Act
		org.easymock.EasyMock.replay(fs);		
		try {
			file.readFromFile(this.fileName01);		
			org.junit.Assert.fail("An exception should have been thrown;");
		} catch (PermanentSyncException e) { }
		
		//Assert
		verify(fs);

	}

	@Test
	public void readFromFileMissingFile() throws IOException, PermanentSyncException
	{
		//*********************
		//Arrange
		IFileSystemWrapper fs = createMock(IFileSystemWrapper.class);
		Path testPath = Paths.get("/tmp/");
		Path rootPath = testPath.resolve(".mpw");
		SiteListFile file = new SiteListFile( testPath, fs);

		//********************
		//Anticipate

		//Check that the folder exists
		expect(fs.fileExists(rootPath)).andReturn(true);
		//Check that the file exists
		expect(fs.fileExists(rootPath.resolve(this.fileName01))).andReturn(false);
		//And now we should return false.		

		//Act
		org.easymock.EasyMock.replay(fs);
		SiteDescriptor answer = file.readFromFile(this.fileName01);

		//Assert
		org.junit.Assert.assertEquals(null, answer);
		org.easymock.EasyMock.verify(fs);

	}

	@Test
	public void readFromFileErrorDeterminingFileExistence() throws IOException
	{
		//*********************
		//Arrange
		IFileSystemWrapper fs = createMock(IFileSystemWrapper.class);
		Path testPath = Paths.get("/tmp/");
		Path rootPath = testPath.resolve(".mpw");
		SiteListFile file = new SiteListFile( testPath, fs);

		//********************
		//Anticipate

		//Check that the folder exists
		expect(fs.fileExists(rootPath)).andReturn(true);
		//Check that there is an error determining the files existence.
		expect(fs.fileExists(rootPath.resolve(this.fileName01))).andThrow(new IOException());

		//Act
		org.easymock.EasyMock.replay(fs);
		try {
			file.readFromFile(this.fileName01);
			org.junit.Assert.fail("An exception should have been thrown");
		} catch (PermanentSyncException e) {
		}

		//Assert		
		org.easymock.EasyMock.verify(fs);

	}

	@Test
	public void readFromFileErrorReadingFile() throws IOException
	{
		//*********************
		//Arrange
		IFileSystemWrapper fs = createMock(IFileSystemWrapper.class);
		Path testPath = Paths.get("/tmp/");
		Path rootPath = testPath.resolve(".mpw");
		SiteListFile file = new SiteListFile( testPath, fs);

		//********************
		//Anticipate

		//Check that the folder exists
		expect(fs.fileExists(rootPath)).andReturn(true);
		//Check that the file exists
		expect(fs.fileExists(rootPath.resolve(this.fileName01))).andReturn(true);
		//Read the file
		expect(fs.readFile(rootPath.resolve(this.fileName01))).andThrow(new IOException());		

		//Act
		org.easymock.EasyMock.replay(fs);				
		try {
			file.readFromFile(this.fileName01);
			org.junit.Assert.fail("An exception should have been thrown");
		} catch (PermanentSyncException e) {			
		}

		//Assert
		org.easymock.EasyMock.verify(fs);

	}
}
