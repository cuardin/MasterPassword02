package org.irrbloss.masterpassword.storage.webStore;

import java.util.Date;

public class FileListEntry implements Comparable<FileListEntry> {
	private int FileID;
	private Date creationDate;
	private String fileName;
	
	public FileListEntry(int fileID, Date creationDate, String fileName ) {
		super();
		FileID = fileID;
		this.creationDate = creationDate;
		this.fileName = fileName;
	}

	public int getFileID() {
		return FileID;
	}

	public void setFileID(int fileID) {
		FileID = fileID;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "FileID=" + FileID + ", creationDate="
				+ creationDate + ", fileName=" + fileName;
	}

	@Override
	public int compareTo(FileListEntry o) {
		return this.fileName.compareTo(o.fileName);
	}
	
	
	
	
}
