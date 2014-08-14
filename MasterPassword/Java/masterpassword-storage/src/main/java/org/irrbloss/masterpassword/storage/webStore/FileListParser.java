package org.irrbloss.masterpassword.storage.webStore;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FileListParser {
	public static List<FileListEntry> parseFileList( String xmlString ) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				
		LinkedList<FileListEntry> rValue = new LinkedList<FileListEntry>();
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			org.w3c.dom.Document dom = db.parse( new ByteArrayInputStream(xmlString.getBytes("UTF-8")) );
			
			//Get root element
			Element docEle = dom.getDocumentElement();
			
			//GEt all the file nodes.
			NodeList nl = docEle.getElementsByTagName("file");
			if ( nl != null && nl.getLength() > 0 ) {
				for ( int i = 0; i < nl.getLength(); i++ ) {
					Element el = (Element)nl.item(i);
					
					int fileID = FileListParser.getIntValue(el, "fileID");
					Date creationDate = FileListParser.getDateValue( el, "creationDate" );					
					String fileName = FileListParser.getTextValue(el, "fileName");
					rValue.add(new FileListEntry(fileID, creationDate, fileName ));
				}
			}
			
		} catch ( Exception e) {
			e.printStackTrace();
		}
		
		//Finally sort the saved files by date.
		Collections.sort(rValue);
		
		return rValue;
		
	}
	
	/**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content
	 * i.e for <employee><name>John</name></employee> xml snippet if
	 * the Element points to employee node and tagName is 'name' I will return John
	 */
	private static String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}


	/**
	 * Calls getTextValue and returns a int value
	 */
	private static int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}
	
	private static Date getDateValue( Element ele, String tagName ) {
		String str = FileListParser.getTextValue(ele, tagName);		
		DateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		try {
			return df.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
