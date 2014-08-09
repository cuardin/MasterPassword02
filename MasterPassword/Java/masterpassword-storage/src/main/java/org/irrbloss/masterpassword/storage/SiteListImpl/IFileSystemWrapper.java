package org.irrbloss.masterpassword.storage.SiteListImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public interface IFileSystemWrapper {

	public abstract Collection<Path> listFiles(Path folder) throws IOException;

	public abstract String readFile(Path entry) throws IOException;

	public abstract void writeFile(Path entry, String content)
			throws IOException;

	public abstract void remove(Path fileName) throws IOException;

	public abstract void createFolder(Path rootFolder) throws IOException;

	public abstract void clearFolder(Path rootFolder) throws IOException;

	public abstract boolean fileExists(Path filePath) throws IOException;

}