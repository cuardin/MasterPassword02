package org.irrbloss.masterpassword.storage.test.webStore;

import java.util.List;

import org.irrbloss.masterpassword.storage.webStore.FileListEntry;
import org.irrbloss.masterpassword.storage.webStore.FileListParser;
import org.junit.Test;

public class FileListParserTest {

	@Test
	public void testParseFileList01() {
		String input = "<?xml version=\"1.0\"?><xml><file><fileID>2817</fileID><fileName>testFile</fileName></file></xml>";
		List<FileListEntry> parsed = FileListParser.parseFileList(input);
		
		org.junit.Assert.assertEquals(1, parsed.size());
		org.junit.Assert.assertEquals(2817, parsed.get(0).getFileID());
		org.junit.Assert.assertEquals("testFile", parsed.get(0).getFileName());
	}


}
