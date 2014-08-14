package org.irrbloss.masterpassword.storage.webStore;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

public class QueryBuilderClass {

	public static String httpGet( String path, HashMap<String,String> keys ) {
		
		String rValue = "";
		try {
			//Build the request
			String request = path + "?";
			for( String s : keys.keySet() ) {
				request += s + "=" + keys.get(s) + "&";
			}
			request = request.substring(0, request.length()-1);

			//Send the request			
			URL url = new URL(request);
			URLConnection conn = url.openConnection();

			//Read the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				rValue += line;
			}
			rd.close();

		} catch (MalformedURLException e) {		
			rValue = "FAIL: " + e.toString();
		} catch (IOException e) {
			rValue = "FAIL: " + e.toString();
		}

		return rValue;

	}

	public static String httpPost(String path, HashMap<String,String> data) {
		String rValue = "";
		try {
			//Build the request'
			String post = "";
			for ( String s : data.keySet() ) {
				post += URLEncoder.encode(s,"UTF-8") + "=" + URLEncoder.encode(data.get(s),"UTF-8") + "&";
			}
			post = post.substring(0, post.length()-1);

			URL url = new URL(path);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			wr.write(post);
			wr.flush();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				rValue += line;
			}
			wr.close();
			rd.close();
		} catch (MalformedURLException e) {		
			rValue = "FAIL: " + e.toString();
		} catch (IOException e) {
			rValue = "FAIL: " + e.toString();
		}
		return rValue;
	}

	public static String convertToBase64 ( Serializable obj ) {		
		try {			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bout);
			
			out.writeObject(obj);			
			out.close();
			
			byte[] rawData = bout.toByteArray();
			
			//Now convert to base64.
			return javax.xml.bind.DatatypeConverter.printBase64Binary(rawData);
		} catch (IOException e) {
			e.printStackTrace();
			return "FAIL: Error serializing object.";
		}
	}

	
	public static Object convertFromBase64 ( String data ) {		
		try {			
			
			//Convert from base64
			byte[] rawData = javax.xml.bind.DatatypeConverter.parseBase64Binary(data);
			
			//Then parse to an object.
			ByteArrayInputStream bin = new ByteArrayInputStream(rawData);
			ObjectInputStream in = new ObjectInputStream(bin);			
			Object copy = in.readObject();
			in.close();
			return copy;
									
		} catch ( Exception e) {
			e.printStackTrace();
			return null;
		}
	}


}
