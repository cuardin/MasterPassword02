package org.irrbloss.masterpassword.storage.test.SiteListImplRAM;

import java.util.Date;

import org.irrbloss.masterpassword.storage.ISiteListImpl;
import org.irrbloss.masterpassword.storage.SiteDescriptor;
import org.irrbloss.masterpassword.storage.Exceptions.PermanentSyncException;
import org.irrbloss.masterpassword.storage.SiteListImpl.SiteListRAM;
import org.junit.Test;

public class SiteListImplRAMTest {

	@Test
	public void testStoreSiteDescNormal() throws PermanentSyncException {
		ISiteListImpl list = new SiteListRAM();
		
		SiteDescriptor s1 = new SiteDescriptor("site.com");
		list.put( "site.com", s1 );
		SiteDescriptor s2 = list.get("site.com");
		
		org.junit.Assert.assertEquals(1, list.size());
		org.junit.Assert.assertEquals("site.com", s2.getSiteName() );
	}

	@Test
	public void testStoreSiteDescQuick() throws PermanentSyncException {
		ISiteListImpl list = new SiteListRAM();
		
		list.add( new SiteDescriptor("site.com"));
		
		org.junit.Assert.assertEquals(1, list.size());
		org.junit.Assert.assertEquals("site.com", list.keySet().iterator().next());
	}
	
	@Test
	public void testStoreSiteDescOverWrite() throws PermanentSyncException {
		ISiteListImpl list = new SiteListRAM();
		
		SiteDescriptor s1 = new SiteDescriptor("site.com",1,"pin", new Date(200) );
		list.add(s1);
		SiteDescriptor s2 = new SiteDescriptor("site.com",3,"long", new Date(100) );
		list.add(s2);
		
		org.junit.Assert.assertEquals(1, list.size());
		org.junit.Assert.assertEquals("site.com", list.keySet().iterator().next());
		org.junit.Assert.assertEquals(s1, list.get("site.com"));
	}

	@Test
	public void testStoreSiteDescPutAll() throws PermanentSyncException {
				
		SiteDescriptor s1 = new SiteDescriptor("site01.com",1,"pin", new Date(200) );		
		SiteDescriptor s2 = new SiteDescriptor("site02.com",3,"long", new Date(100) );
		ISiteListImpl list01 = new SiteListRAM();
		list01.add(s1);
		list01.add(s2);
		
		SiteDescriptor s4 = new SiteDescriptor("site02.com",3,"long", new Date(100) ); //== s2 above
		SiteDescriptor s3 = new SiteDescriptor("site03.com",1,"pin", new Date(200) );				
		SiteListRAM list02 = new SiteListRAM();
		list02.add(s3);
		list02.add(s4);
		
		//Now merge the lists.
		list01.putAll(list02);
		
		
		org.junit.Assert.assertEquals(3, list01.size());		
		org.junit.Assert.assertTrue(list01.containsKey("site01.com"));
		org.junit.Assert.assertTrue(list01.containsKey("site02.com"));
		org.junit.Assert.assertTrue(list01.containsKey("site03.com"));
	}

	@Test(expected=NullPointerException.class)
	public void testAddNull() throws PermanentSyncException {
		ISiteListImpl ram = new SiteListRAM();
				
		ram.put( "string", null );			
	}

}
