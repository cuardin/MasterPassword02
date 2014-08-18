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

	private Path rootFolder;
	
	public FileSystemLowLevelWrapper(Path rootFolder) {
		this.rootFolder = rootFolder.resolve(".mpw");
	}
	
	public Collection<String> listFiles() throws IOException {
		Vector<String> rValue = new Vector<String>();
		DirectoryStream<Path> stream = null;

		stream = Files.newDirectoryStream(this.rootFolder);			
		for (Path entry: stream) {
			rValue.add(entry.getFileName().toString());
		}			
		return rValue;
	}

	public String readFile(String fileName) throws IOException 
	{
		Path entry = this.rootFolder.resolve(fileName);
		try ( BufferedReader reader = new BufferedReader(new FileReader(entry.toFile())) ) {
			String rValue = "";
			while ( reader.ready() ) {
				rValue += reader.readLine() + "\n";
			}
			return rValue.trim();		
		} 
	}

	public void writeFile(String fileName, String content) throws IOException
	{		
		Path entry = this.rootFolder.resolve(fileName);
		try ( BufferedWriter writer = new BufferedWriter(new FileWriter(entry.toFile())) ) {
			writer.write(content);
			return;
		}									
	}

	public void remove(String fileName) throws IOException {
		Path entry = this.rootFolder.resolve(fileName);
		if ( this.doFileExists(entry) ) {			
			Files.delete(entry);					
		}				
	}

	public void initialize() throws IOException {		
		if ( !this.doFileExists(this.rootFolder) ) {
			File f = this.rootFolder.toFile();			
			if ( !f.mkdirs() ) {
				throw new IOException("Could not create folder to store files.");
			}			
		}							
	}


	public void cleanUp() throws IOException {
		final Path rootFolder = this.rootFolder;
		
		if ( this.doFileExists(rootFolder) ) {
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

	public boolean fileExists(String fileName) throws IOException {
		Path filePath = this.rootFolder.resolve(fileName);
		return this.doFileExists(filePath);
	}
	
	private boolean doFileExists(Path filePath) throws IOException {
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
