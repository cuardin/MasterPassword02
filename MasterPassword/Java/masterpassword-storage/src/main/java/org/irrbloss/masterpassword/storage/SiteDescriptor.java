package org.irrbloss.masterpassword.storage;

import java.util.Date;

import org.irrbloss.masterpassword.storage.Exceptions.PermanentSyncException;

public class SiteDescriptor {
	private String siteName;
	private int counter;
	private String type;
	private Date creationTime;
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	public SiteDescriptor( String siteName, int counter, String type ) 
	{
		this(siteName, counter, type, new Date());
	}

	public SiteDescriptor( String siteName, int counter, String type, 
			Date creationTime )
	{
		this.siteName = siteName;
		this.counter = counter;
		this.type = type;
		this.creationTime = creationTime;
	}

	public SiteDescriptor(String siteName) {
		this(siteName,1,"long");
	}

	public String getSiteName() {
		return siteName;
	}

	public int getCounter() {
		return counter;
	}

	public String getType() {
		return type;
	}

	public Date getCreationTime() {
		return this.creationTime;
	}

	@Override
	public String toString() {
		return this.siteName + ":" + this.counter + "/" + this.type + "/" + this.creationTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + counter;
		result = prime * result
				+ ((siteName == null) ? 0 : siteName.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SiteDescriptor))
			return false;
		SiteDescriptor other = (SiteDescriptor) obj;
		if (counter != other.counter)
			return false;
		if (siteName == null) {
			if (other.siteName != null)
				return false;
		} else if (!siteName.equals(other.siteName))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public String toFormatedText() {		
		return SiteDescriptor.bytesToHex(this.siteName.getBytes()) + "\n" + 
				this.counter + "\n" + this.type + "\n" + this.creationTime.getTime();
	}

	public static SiteDescriptor fromFormatedText(String content) throws PermanentSyncException {		
		//Now parse the fileContent of said file.
		String[] splitContent = content.split("\n");
		if ( splitContent.length != 4 ) {
			throw new PermanentSyncException();
		}
		String siteName = new String(SiteDescriptor.bytesFromHex(splitContent[0]));
		int counter = Integer.parseInt(splitContent[1]);
		String type = splitContent[2];

		Date creationDate = new Date(Long.parseLong(splitContent[3]));		
		return new SiteDescriptor(siteName, counter, type, creationDate);		
	}

	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}

	public static byte[] bytesFromHex(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];

        for(int i = 0; i < len; i+=2){
            data[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }

        return data;
    }
	
	
}
