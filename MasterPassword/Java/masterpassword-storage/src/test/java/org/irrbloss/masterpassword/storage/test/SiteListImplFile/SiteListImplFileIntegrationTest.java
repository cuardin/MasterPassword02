package org.irrbloss.masterpassword.storage.test.SiteListImplFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

import org.irrbloss.masterpassword.storage.ISiteListImpl;
import org.irrbloss.masterpassword.storage.SiteDescriptor;
import org.irrbloss.masterpassword.storage.Exceptions.PermanentSyncException;
import org.irrbloss.masterpassword.storage.SiteListImpl.FileSystemHighLevelWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.SiteListFile;
import org.irrbloss.masterpassword.storage.SiteListImpl.SiteListRAM;
import org.irrbloss.masterpassword.storage.test.util.FileSystemLowLevelWrapperDelayed;
import org.irrbloss.masterpassword.storage.test.util.TempFileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SiteListImplFileIntegrationTest {

	private Path tempDir;

	@Before
	public void setUp() throws Exception {		
		this.tempDir = TempFileManager.setup();                        
	}

	@After
	public void tearDown() throws Exception {
		TempFileManager.cleanup(this.tempDir);
	}

	@Test
	public void testCreate() throws PermanentSyncException {
		//Check that creation does not take time. The init function should do anything that takes time.
		FileSystemHighLevelWrapper fs = new FileSystemLowLevelWrapperDelayed(tempDir,200);
		long startTime = System.currentTimeMillis();
		ISiteListImpl file = new SiteListFile( new FileSystemHighLevelWrapper(fs) );
		long runTime = System.currentTimeMillis() - startTime;
		org.junit.Assert.assertTrue( runTime < 100 );
		
		file.init();
				
		//Make sure a folder was created		
		File f = tempDir.resolve(".mpw").toFile();
		org.junit.Assert.assertTrue( f.exists() );
		org.junit.Assert.assertTrue( f.isDirectory() );

		
	}
	
	@Test
	public void testStoreSiteDescNormal() throws IOException, PermanentSyncException {
		SiteListFile list = new SiteListFile(new FileSystemHighLevelWrapper(tempDir) );
		list.init();

		SiteDescriptor s1 = new SiteDescriptor("site.com");

		list.add(s1);
		
		//Make sure a folder was created		
		File f = tempDir.resolve(".mpw").resolve(list.encodeString("site.com")).toFile();
		org.junit.Assert.assertTrue( f.exists() );
		org.junit.Assert.assertTrue( !f.isDirectory() );
		
		//Make sure we can read the value.
		org.junit.Assert.assertEquals(s1, list.get("site.com"));
		
		org.junit.Assert.assertEquals(1, list.size());
		org.junit.Assert.assertEquals("site.com", list.keySet().iterator().next());
		
			

	}

	@Test
	public void testGetNonexistentDesc() throws IOException, PermanentSyncException {
		ISiteListImpl list = new SiteListFile( new FileSystemHighLevelWrapper(tempDir) );
		list.init();

		//Make sure we can read a single value as well.
		org.junit.Assert.assertNull( list.get("site.com") );

	}

	@Test 
	public void testStoreSiteDescOverWrite() throws IOException, PermanentSyncException {
		ISiteListImpl list = new SiteListFile( new FileSystemHighLevelWrapper(this.tempDir));
		list.init();
		
		SiteDescriptor s1 = new SiteDescriptor("site.com",1,"pin", new Date((long) 2e6) );
		list.add(s1);
		SiteDescriptor s2 = new SiteDescriptor("site.com",3,"long", new Date((long) 1e6) );
		list.add(s2);

		org.junit.Assert.assertEquals(1, list.size());
		org.junit.Assert.assertEquals("site.com", list.keySet().iterator().next());
		org.junit.Assert.assertEquals(s1, list.get("site.com"));
	}
	
	@Test
	public void testStoreSiteDescPutAll() throws IOException, PermanentSyncException 
	{

		SiteDescriptor s1 = new SiteDescriptor("site01.com",1,"pin", new Date(200) );		
		SiteDescriptor s2 = new SiteDescriptor("site02.com",3,"long", new Date(100) );
		SiteListFile list01 = new SiteListFile( new FileSystemHighLevelWrapper(tempDir));
		list01.init();
		
		list01.add(s1);
		list01.add(s2);

		SiteDescriptor s4 = new SiteDescriptor("site02.com",3,"long", new Date(100) ); //== s2 above
		SiteDescriptor s3 = new SiteDescriptor("site03.com",1,"pin", new Date(200) );				
		ISiteListImpl list02 = new SiteListRAM();
		list02.init();
		list02.add(s3);
		list02.add(s4);

		//Now merge the lists.
		list01.putAll(list02);


		org.junit.Assert.assertEquals(3, list01.size());		
		org.junit.Assert.assertTrue(list01.containsKey("site01.com"));
		org.junit.Assert.assertTrue(list01.containsKey("site02.com"));
		org.junit.Assert.assertTrue(list01.containsKey("site03.com"));
		
		//Also check the values
		org.junit.Assert.assertEquals(s1, list01.get("site01.com"));
		org.junit.Assert.assertEquals(s2, list01.get("site02.com"));
		org.junit.Assert.assertEquals(s3, list01.get("site03.com"));
		
	}
	
	@Test
	public void testRemoveSiteDescNormal() throws IOException, PermanentSyncException {
		ISiteListImpl list = new SiteListFile( new FileSystemHighLevelWrapper(tempDir) );
		list.init();
		
		SiteDescriptor s1 = new SiteDescriptor("site.com");

		list.add(s1);
				
		list.remove("site.com");
		
		org.junit.Assert.assertEquals(0, list.size());

	}

	@Test
	public void testClearSiteDescNormal() throws IOException, PermanentSyncException {
		ISiteListImpl list = new SiteListFile( new FileSystemHighLevelWrapper(tempDir) );
		list.init();

		SiteDescriptor s1 = new SiteDescriptor("site01.com");
		SiteDescriptor s2 = new SiteDescriptor("site02.com");

		list.add(s1);
		list.add(s2);
				
		list.clear();
		
		org.junit.Assert.assertEquals(0, list.size());

	}

	@Test(expected=NullPointerException.class)
	public void testAddNull() throws PermanentSyncException {
		ISiteListImpl file = new SiteListFile(new FileSystemHighLevelWrapper(this.tempDir));
				
		file.put( "string", null );
				
	}

}
