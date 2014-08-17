package org.irrbloss.masterpassword.storage.test.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import org.irrbloss.masterpassword.storage.SiteListImpl.FileSystemHighLevelWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.FileSystemLowLevelWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.IFileSystemWrapper;

public class FileSystemLowLevelWrapperDelayed extends FileSystemHighLevelWrapper {

	@Override
	public void clearFolder() throws IOException {
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}

		super.clearFolder();
	}

	@Override
	public boolean fileExists(String fileName) throws IOException {
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}

		return super.fileExists(fileName);
	}

	private int sleepTime;

	public FileSystemLowLevelWrapperDelayed( Path rootFolder, int sleepTime ) {
		this(new FileSystemLowLevelWrapper(rootFolder), sleepTime);		
	}
	
	public FileSystemLowLevelWrapperDelayed(IFileSystemWrapper fs, int sleepTime) {
		super(fs);
		this.sleepTime = sleepTime;
	}

	@Override
	public Collection<String> listFiles() throws IOException {
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}
		return super.listFiles();
	}

	@Override
	public String readFile(String fileName) throws IOException {
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}

		return super.readFile(fileName);
	}

	@Override
	public void writeFile(String fileName, String content) throws IOException {
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}

		super.writeFile(fileName, content);
	}

	@Override
	public void remove(String fileName) throws IOException {
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}

		super.remove(fileName);
	}

	public void createFolder() throws IOException 
	{
		try {
			Thread.sleep(this.sleepTime);
		} catch (InterruptedException e) {
			throw new Error(e);
		}

		super.createFolder();
	}
}
