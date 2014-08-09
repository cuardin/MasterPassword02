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

	public FileSystemLowLevelWrapperUnreliable(int sleepTime, double reliability) {
		this(new FileSystemLowLevelWrapper(), sleepTime, reliability );		
	}

	@Override
	public void clearFolder(Path rootFolder) throws IOException {
		super.clearFolder(rootFolder);
	}

	@Override
	public boolean fileExists(Path filePath) throws IOException {
		this.mightFail();
		return super.fileExists(filePath);
	}

	@Override
	public Collection<Path> listFiles(Path folder) throws IOException {
		this.mightFail();
		return super.listFiles(folder);
	}

	@Override
	public String readFile(Path entry) throws IOException {
		this.mightFail();
		return super.readFile(entry);
	}

	@Override
	public void writeFile(Path entry, String content) throws IOException {
		this.mightFail();
		super.writeFile(entry, content);
	}

	@Override
	public void remove(Path fileName) throws IOException {
		this.mightFail();
		super.remove(fileName);
	}

	@Override
	public void createFolder(Path rootFolder) throws IOException {
		this.mightFail();
		super.createFolder(rootFolder);
	}

}
