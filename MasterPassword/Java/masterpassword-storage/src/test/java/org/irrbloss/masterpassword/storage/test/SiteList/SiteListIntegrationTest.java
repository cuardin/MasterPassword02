package org.irrbloss.masterpassword.storage.test.SiteList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import org.irrbloss.masterpassword.storage.ISiteListImpl;
import org.irrbloss.masterpassword.storage.ISiteListImpl.Implementations;
import org.irrbloss.masterpassword.storage.SiteDescriptor;
import org.irrbloss.masterpassword.storage.SiteList;
import org.irrbloss.masterpassword.storage.Exceptions.PermanentSyncException;
import org.irrbloss.masterpassword.storage.SiteListImpl.FileSystemHighLevelWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.IFileSystemWrapper;
import org.irrbloss.masterpassword.storage.SiteListImpl.SiteListFile;
import org.irrbloss.masterpassword.storage.SiteListImpl.SiteListRAM;
import org.irrbloss.masterpassword.storage.test.util.FileSystemLowLevelWrapperDelayed;
import org.irrbloss.masterpassword.storage.test.util.TempFileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SiteListIntegrationTest {

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
	public void testListImplementations01() {
		SiteList list = new SiteList();	
		
		Implementations[] impl = list.listImplementations();
		
		org.junit.Assert.assertEquals(0, impl.length );		
	}
	
	@Test
	public void testAddFirstList() throws PermanentSyncException {
		SiteList list = new SiteList();
		IFileSystemWrapper low = new FileSystemLowLevelWrapperDelayed(this.tempDir,200);
		IFileSystemWrapper high = new FileSystemHighLevelWrapper(low);
		SiteListFile siteList = new SiteListFile( high);

		//Add an implementation. Check for asynchronicity
		long startTime = System.currentTimeMillis();
		list.addImpl( siteList );					
		long runTime = System.currentTimeMillis() - startTime;		
		org.junit.Assert.assertTrue( runTime < 100 );
		
		list.waitForSync();
		
		Implementations[] impl = list.listImplementations();
		
		org.junit.Assert.assertEquals(1, impl.length );		
		org.junit.Assert.assertEquals( Implementations.FILE, impl[0] );
		
		//Check that it was initialized properly too. By reading the keyset we find that out. If it wasn't initialized, we get an error.
		@SuppressWarnings("unused")
		Set<String> keys = list.keySet();
		
		//And check that our impl was set as the reading impl.
		org.junit.Assert.assertEquals(Implementations.FILE, list.getReadImplType() );
		
	}
		
	@Test 
	public void testStoreSiteDescNormal() throws PermanentSyncException, InterruptedException, IOException {
		SiteList list = new SiteList();
		IFileSystemWrapper low = new FileSystemLowLevelWrapperDelayed(this.tempDir,200);
		IFileSystemWrapper high = new FileSystemHighLevelWrapper(low);

		ISiteListImpl file = new SiteListFile(high);
		ISiteListImpl ram = new SiteListRAM();
		list.addImpl(file);
		list.addImpl(ram, true );
		
		list.waitForSync();
		
		SiteDescriptor s1 = new SiteDescriptor("site.com");

		//Check that we can add data without blocking.
		long startTime = System.currentTimeMillis();
		list.put( s1 );					
		long runTime = System.currentTimeMillis() - startTime;		
		org.junit.Assert.assertTrue( runTime < 100 );
		
		//Allow the system to sync.
		list.waitForSync();

		//Check that we can read data without blocking (Assuming we are in sync)
		startTime = System.currentTimeMillis();
		SiteDescriptor s2 = list.get("site.com");
		runTime = System.currentTimeMillis() - startTime;		
		org.junit.Assert.assertTrue( runTime < 100 );
		
		org.junit.Assert.assertEquals(1, list.size());
		org.junit.Assert.assertEquals("site.com", s2.getSiteName() );
		
		//Check that the data exist in both file and ram implementations.
		org.junit.Assert.assertEquals(1, ram.size());
		org.junit.Assert.assertTrue( ram.containsKey("site.com"));
		
		//Check that the data exist in both file and ram implementations.
		org.junit.Assert.assertEquals(1, file.size());
		org.junit.Assert.assertTrue( file.containsKey("site.com"));

	}

	
	@Test
	public void testSiteMergeOnAdd() throws PermanentSyncException {
		SiteList list = new SiteList();
		
		ISiteListImpl ram = new SiteListRAM();		
		list.addImpl(ram);
		list.put( new SiteDescriptor("Site02.com"));
		list.put( new SiteDescriptor("Site03.com"));				
		list.waitForSync();
		org.junit.Assert.assertEquals(2, list.size() );
		
		IFileSystemWrapper low = new FileSystemLowLevelWrapperDelayed(this.tempDir,200);
		IFileSystemWrapper high = new FileSystemHighLevelWrapper(low);
		ISiteListImpl file = new SiteListFile( high );
		list.addImpl(file);		
		list.put( new SiteDescriptor("Site01.com"));			
		list.waitForSync();		
				
		org.junit.Assert.assertEquals(3, list.size() );
		org.junit.Assert.assertEquals(3, ram.size());
		org.junit.Assert.assertEquals(3, file.size());				
		
	}
	
	@Test
	public void testGet() throws PermanentSyncException {
		SiteList list = new SiteList();
		
		ISiteListImpl ram = new SiteListRAM();
		IFileSystemWrapper low = new FileSystemLowLevelWrapperDelayed(this.tempDir,200);
		IFileSystemWrapper high = new FileSystemHighLevelWrapper(low);

		ISiteListImpl file = new SiteListFile( high );
		list.addImpl(ram); list.waitForSync();
		list.addImpl(file);	list.waitForSync();
		
		list.put( new SiteDescriptor("Site01.com"));
		list.put( new SiteDescriptor("Site02.com"));				
		list.waitForSync();
		
		Set<String> keys = list.keySet();
		org.junit.Assert.assertEquals(2, keys.size());
		org.junit.Assert.assertTrue(keys.contains("Site01.com"));
		org.junit.Assert.assertTrue(keys.contains("Site02.com"));
		
	}
	
	@Test(expected=NullPointerException.class)
	public void testAddNull() throws PermanentSyncException {
		SiteList list = new SiteList();
		
		ISiteListImpl ram = new SiteListRAM();
		
		IFileSystemWrapper low = new FileSystemLowLevelWrapperDelayed(this.tempDir,200);
		IFileSystemWrapper high = new FileSystemHighLevelWrapper(low);
		ISiteListImpl file = new SiteListFile( high );
		list.addImpl(ram); list.waitForSync();
		list.addImpl(file); list.waitForSync();
		
		list.put( "string", null );
				
	}

	@Test
	public void testRemove() throws PermanentSyncException {
		SiteList list = new SiteList();
		
		ISiteListImpl ram = new SiteListRAM();
		
		IFileSystemWrapper low = new FileSystemLowLevelWrapperDelayed(this.tempDir, 200);
		IFileSystemWrapper high = new FileSystemHighLevelWrapper(low);
		ISiteListImpl file = new SiteListFile( high );
		list.addImpl(ram); list.waitForSync();
		list.addImpl(file); list.waitForSync();
		
		list.put( new SiteDescriptor("Site01.com"));
		list.put( new SiteDescriptor("Site02.com"));								
		
		list.waitForSync();
		org.junit.Assert.assertEquals(2, list.size());
		
		//Remove while checking for asynchronicity.
		long startTime = System.currentTimeMillis();
		list.remove("Site01.com");
		list.remove("Site02.com");
		list.remove("Site03.com"); //Make sure we can remove keys that do not exist as well.		
		long runTime = System.currentTimeMillis() - startTime;		
		org.junit.Assert.assertTrue( runTime < 100 );
		
		list.waitForSync();		
		org.junit.Assert.assertEquals(0, list.size());
		org.junit.Assert.assertEquals(0, ram.size());
		org.junit.Assert.assertEquals(0, file.size());
	}
	
	@Test
	public void testClear() throws PermanentSyncException {
		SiteList list = new SiteList();
		
		ISiteListImpl ram = new SiteListRAM();

		IFileSystemWrapper low = new FileSystemLowLevelWrapperDelayed(this.tempDir, 200);
		IFileSystemWrapper high = new FileSystemHighLevelWrapper(low);
		ISiteListImpl file = new SiteListFile( high );
		list.addImpl(ram); list.waitForSync();
		list.addImpl(file); list.waitForSync();
		
		list.put( new SiteDescriptor("Site01.com"));
		list.put( new SiteDescriptor("Site02.com"));								
		
		list.waitForSync();
		org.junit.Assert.assertEquals(2, list.size());
		
		//Clear and check that it is asynchronous.
		long startTime = System.currentTimeMillis();
		list.clear();
		long runTime = System.currentTimeMillis() - startTime;		
		org.junit.Assert.assertTrue( runTime < 100 );
		
		list.waitForSync();		
		org.junit.Assert.assertEquals(0, list.size());
		org.junit.Assert.assertEquals(0, ram.size());
		org.junit.Assert.assertEquals(0, file.size());
	}

}
