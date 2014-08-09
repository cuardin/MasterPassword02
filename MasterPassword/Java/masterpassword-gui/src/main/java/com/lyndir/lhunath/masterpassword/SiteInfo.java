package com.lyndir.lhunath.masterpassword;

import static com.lyndir.lhunath.opal.system.util.ObjectUtils.ifNotNullElse;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JOptionPane;

import com.google.common.base.Splitter;
import com.google.common.io.Files;

//A simple class that can read site info from a text file.
//Does not guard against all possible cases, most notably if 
//the file is edited externally while this program is running.
//Something more like a ultra-light-weight database is what should be used here.

public class SiteInfo {
	//First some static properties to handle the database-like characteristics.	
	private static File siteListFile;
	private static Vector<SiteInfo> siteList;
	private static Lock fileLock = new ReentrantLock();
	private static final int maxNameLength = 128;
	private static int lastLine;
	
	//Then the properties of this particular object.
	private String name;	
	private int passNumber;	
	private MPElementType type;		
	
	//Private constructors to disallow creation of objects not in the database.
	private SiteInfo() {
		this("",1,MPElementType.GeneratedLong, ++lastLine);
	}
		
	private SiteInfo(String name, int num, MPElementType type, int lineNum ) {		
		//Make sure we don't have too long sitenames.		
		this.name = name;
		this.passNumber = num;
		this.type = type;		
	}
	
	public String getName() {
		return name;
	}

	public int getNumber() {
		return passNumber;
	}

	public MPElementType getType() {
		return type;
	}				
	
	public void setName(String name) {
		//Make sure we don't have too long site-names.
		name = name.length()<maxNameLength?name:name.substring(1, maxNameLength);
		this.name = name;
	}

	public void setNumber(int passNumber) {
		this.passNumber = passNumber;
	}

	public void setType(MPElementType type) {
		this.type = type;
	}

	public static void writeBackToFile()
	{
		fileLock.lock(); //Lock to make sure we don't have read/write conflicts.
		try {
			

			//Now write the rawSiteData to disc
			try ( Writer mpwWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(siteListFile), "UTF8")); )
			{
				for ( SiteInfo s : siteList ) {
					String str = s.getName() + ":" + s.getNumber() + ":" + s.getType().getName();
					mpwWriter.append(str);
					mpwWriter.append('\n');
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			fileLock.unlock();
		}
	}
	
	public static SiteInfo getNewSiteInfo()
	{
		fileLock.lock();
		try {
			SiteInfo n = new SiteInfo();
			siteList.add(n);
			return n;
		} finally {
			fileLock.unlock();
		}
	}
	
	public static void remove(SiteInfo site)
	{
		fileLock.lock();
		try {
			siteList.remove(site);
		} finally {
			fileLock.unlock();
		}
	}
	
	public static Vector<SiteInfo> readFromFile( Component panel )
	{		
		siteList = new Vector<SiteInfo>();
		siteListFile = new File( System.getProperty( "user.home" ), "siteList.mpw.dat" );						
		fileLock.lock();
		int lineNumber = 0;
		try  {
        	List<String> lines = Files.readLines( siteListFile, Charset.forName("UTF-8") );        	           
        	for (String line : lines ) {  
        		        		
        		if (line.startsWith( "#" ) || line.startsWith( "//" ) || line.isEmpty()) {
                    continue;
                }
                
        		//Remove any trailing whitespaces in the string.
        		line = line.trim();
        		
                Iterator<String> fields = Splitter.on( ':' ).limit( 3 ).split( line ).iterator();
                String siteName = fields.next();
                int number = Integer.parseInt(fields.next());
                MPElementType type = MPElementType.forName(fields.next());
                siteList.add(new SiteInfo(siteName, number, type, lineNumber) );
                
                lineNumber++;
            }       
        	lastLine = lineNumber-1;
		}
        catch (FileNotFoundException e) {
        	//If we don't find a file, then we simply ignore this fact and move on anyways.
            /*JOptionPane.showMessageDialog( panel, "First create the config file at:\n" + siteListFile.getAbsolutePath() +
                                                 "\n\nIt should contain a line for each user of the following format:" +
                                                 "\nSiteName:Number:PasswordType\n",
                                           "Config File Not Found", JOptionPane.WARNING_MESSAGE );*/        	
        }
        catch (IOException | NoSuchElementException e) {
            e.printStackTrace();
            String error = ifNotNullElse( e.getLocalizedMessage(), ifNotNullElse( e.getMessage(), e.toString() ) );
            JOptionPane.showMessageDialog( panel, //
                                           "Problem reading config file:\n" + siteListFile.getAbsolutePath() + //
                                           "\n\nProblem on line " + (lineNumber+1) + "." + "\n\n" + //
                                           error, //
                                           "Config File Not Readable", JOptionPane.WARNING_MESSAGE );            
        }
		finally
		{
			fileLock.unlock();
		}
		
        
        //We don't return an empty list, if we didn't find anything, we return one element with default values.
        if ( siteList.isEmpty() ) {
        	siteList.add( new SiteInfo());
        }
        
        return siteList;

	}
	
}
