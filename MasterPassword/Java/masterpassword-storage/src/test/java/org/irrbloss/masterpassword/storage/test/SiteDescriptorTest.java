package org.irrbloss.masterpassword.storage.test;
import java.util.Date;

import org.irrbloss.masterpassword.storage.SiteDescriptor;
import org.junit.Test;


public class SiteDescriptorTest {

	@Test
	public void testSiteDescriptorCreate01() {
		SiteDescriptor desc = new SiteDescriptor("site.com", 1, "long");
		
		org.junit.Assert.assertEquals("site.com", desc.getSiteName());
		org.junit.Assert.assertEquals(1, desc.getCounter());
		org.junit.Assert.assertEquals("long", desc.getType() );
	}
	
	@Test
	public void testSiteDescriptorCreate02() {
		SiteDescriptor desc = new SiteDescriptor("site.com");
		
		org.junit.Assert.assertEquals("site.com", desc.getSiteName());
		org.junit.Assert.assertEquals(1, desc.getCounter());
		org.junit.Assert.assertEquals("long", desc.getType() );
	}
	
	@Test
	public void testSiteDescriptorEquals() {
		//Two site descriptors that differ only in date are considered equal;
		SiteDescriptor desc1 = new SiteDescriptor("site.com",1,"pin",new Date((long) 1e6));
		SiteDescriptor desc2 = new SiteDescriptor("site.com",1,"pin",new Date((long) 2e6));
		
		org.junit.Assert.assertEquals(desc1, desc2);
		
	}
	
	@Test
	public void testSiteDescriptorToFormatText() {
		//Two site descriptors that differ only in date are considered equal;
		SiteDescriptor desc1 = new SiteDescriptor("site.com",1,"pin",new Date((long) 1e6));
		
		org.junit.Assert.assertEquals("736974652E636F6D\n1\npin\n1000000", desc1.toFormatedText());
		
	}

	@Test
	public void testSiteDescriptorHashCode() {
		SiteDescriptor desc1 = new SiteDescriptor("site.com",1,"pin",new Date((long) 1e6));
		SiteDescriptor desc2 = new SiteDescriptor("site.com",1,"pin",new Date((long) 2e6));
		
		//Two objects that have identical fileContent except for creation date should be considered equal.
		org.junit.Assert.assertEquals(desc1.hashCode(), desc2.hashCode());	
	}

	@Test
	public void testSiteDescriptorSpecialCharacters() {
		String siteName = "site\t\n.com";
		SiteDescriptor desc1 = new SiteDescriptor(siteName);
		org.junit.Assert.assertEquals(siteName, desc1.getSiteName());		
		
	}

}
