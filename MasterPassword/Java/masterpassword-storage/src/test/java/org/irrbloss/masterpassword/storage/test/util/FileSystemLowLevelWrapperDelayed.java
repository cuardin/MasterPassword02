package org.irrbloss.masterpassword.storage.test.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import org.irrbloss.masterpassword.storage.SiteListImpl.FileSystemHighLevelWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.FileSystemLowLevelWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.IFileSystemWrapper;

public class FileSystemLowLevelWrapperDelayed extends FileSystemHighLevelWrapper {

	@Override
	public void clearFolder(Path rootFolder) throws IOException {
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}

		super.clearFolder(rootFolder);
	}

	@Override
	public boolean fileExists(Path filePath) throws IOException {
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}

		return super.fileExists(filePath);
	}

	private int sleepTime;

	public FileSystemLowLevelWrapperDelayed( int sleepTime ) {
		this(new FileSystemLowLevelWrapper(), sleepTime);		
	}
	
	public FileSystemLowLevelWrapperDelayed(IFileSystemWrapper fs, int sleepTime) {
		super(fs);
		this.sleepTime = sleepTime;
	}

	@Override
	public Collection<Path> listFiles(Path folder) throws IOException {
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}
		return super.listFiles(folder);
	}

	@Override
	public String readFile(Path entry) throws IOException {
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}

		return super.readFile(entry);
	}

	@Override
	public void writeFile(Path entry, String content) throws IOException {
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}

		super.writeFile(entry, content);
	}

	@Override
	public void remove(Path fileName) throws IOException {
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}

		super.remove(fileName);
	}

	public void createFolder(Path rootFolder) throws IOException 
	{
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}

		super.createFolder(rootFolder);
	}
}
