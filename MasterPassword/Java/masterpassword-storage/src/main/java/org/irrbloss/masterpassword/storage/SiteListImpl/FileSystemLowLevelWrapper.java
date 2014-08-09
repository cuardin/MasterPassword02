package org.irrbloss.masterpassword.storage.SiteListImpl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.Vector;
/*
 * A class to handle calls to the underlying file system. This class is not thread safe.
 * This class gives up on any error and should therefore be wrapped into something that 
 * gives it a bit more robustness. 
 */
public class FileSystemLowLevelWrapper implements IFileSystemWrapper {	

	public Collection<Path> listFiles(Path folder) throws IOException {
		Vector<Path> rValue = new Vector<Path>();
		DirectoryStream<Path> stream = null;

		stream = Files.newDirectoryStream(folder);			
		for (Path entry: stream) {
			rValue.add(entry);
		}			
		return rValue;
	}

	public String readFile(Path entry) throws IOException 
	{
		try ( BufferedReader reader = new BufferedReader(new FileReader(entry.toFile())) ) {
			String rValue = "";
			while ( reader.ready() ) {
				rValue += reader.readLine() + "\n";
			}
			return rValue.trim();		
		} 
	}

	public void writeFile(Path entry, String content) throws IOException
	{		
		try ( BufferedWriter writer = new BufferedWriter(new FileWriter(entry.toFile())) ) {
			writer.write(content);
			return;
		}									
	}

	public void remove(Path fileName) throws IOException {		
		if ( this.fileExists(fileName) ) {			
			Files.delete(fileName);					
		}				
	}

	public void createFolder(Path rootFolder) throws IOException {		
		if ( !this.fileExists(rootFolder) ) {
			File f = rootFolder.toFile();			
			if ( !f.mkdirs() ) {
				throw new IOException("Could not create folder to store files.");
			}			
		}							
	}


	public void clearFolder(final Path rootFolder) throws IOException {		
		if ( this.fileExists(rootFolder) ) {
			Files.walkFileTree(rootFolder, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
						throws IOException
				{
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException e)
						throws IOException
				{
					if (e == null) {
						if ( !dir.equals(rootFolder) ) {
							Files.delete(dir);
						}
						return FileVisitResult.CONTINUE;							
					} else {
						// directory iteration failed
						throw e;
					}
				}
			});
		}
	}

	public boolean fileExists(Path filePath) throws IOException {
		boolean exists = Files.exists(filePath);
		boolean notExists = Files.notExists(filePath);
		if ( exists && !notExists ) {
			return true;
		} 
		if ( !exists && notExists ) {
			return false;			
		}		
		throw new IOException("FIle existence could not be determined");			
	}
}
