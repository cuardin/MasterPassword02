package org.irrbloss.masterpassword.storage.SiteListImpl;

import java.io.IOException;
import java.util.Collection;

public interface IFileSystemWrapper {

	public abstract Collection<String> listFiles() throws IOException;

	public abstract String readFile(String fileName) throws IOException;

	public abstract void writeFile(String fileName, String content)
			throws IOException;

	public abstract void remove(String fileName) throws IOException;

	public abstract void initialize() throws IOException;

	public abstract void cleanUp() throws IOException;

	public abstract boolean fileExists(String fileName) throws IOException;

}