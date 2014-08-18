package org.irrbloss.masterpassword.storage.robustnessTest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import org.irrbloss.masterpassword.storage.SiteListImpl.FileSystemLowLevelWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.IFileSystemWrapper;
import org.irrbloss.masterpassword.storage.test.util.FileSystemLowLevelWrapperDelayed;

public class FileSystemLowLevelWrapperUnreliable extends FileSystemLowLevelWrapperDelayed {

	private double reliability;
	
	private int nSinceLastPass = 0;
	private final int maxFailsInRow = 5;
	
	private void mightFail() throws IOException 
	{
		
		double rand = Math.random();

		if ( rand > this.reliability && this.nSinceLastPass < this.maxFailsInRow ) {
			nSinceLastPass++;
			throw new IOException("File system b0rked!");
		}
		this.nSinceLastPass = 0;
		
	}

	public FileSystemLowLevelWrapperUnreliable(IFileSystemWrapper fs,
			int sleepTime, double reliability) {
		super(fs, sleepTime);
		this.reliability = reliability;
	}

	public FileSystemLowLevelWrapperUnreliable(Path rootPath, int sleepTime, double reliability) {
		this(new FileSystemLowLevelWrapper(rootPath), sleepTime, reliability );		
	}

	@Override
	public void cleanUp() throws IOException {
		super.cleanUp();
	}

	@Override
	public boolean fileExists(String fileName) throws IOException {
		this.mightFail();
		return super.fileExists(fileName);
	}

	@Override
	public Collection<String> listFiles() throws IOException {
		this.mightFail();
		return super.listFiles();
	}

	@Override
	public String readFile(String fileName) throws IOException {
		this.mightFail();
		return super.readFile(fileName);
	}

	@Override
	public void writeFile(String fileName, String content) throws IOException {
		this.mightFail();
		super.writeFile(fileName, content);
	}

	@Override
	public void remove(String fileName) throws IOException {
		this.mightFail();
		super.remove(fileName);
	}

	@Override
	public void initialize() throws IOException {
		this.mightFail();
		super.initialize();
	}

}
