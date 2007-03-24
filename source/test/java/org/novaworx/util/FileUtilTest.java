package org.novaworx.util;

import java.io.File;

import junit.framework.TestCase;

public class FileUtilTest extends TestCase {

	public void testGetExtensionWithFile() throws Exception {
		assertEquals( "Incorrect extension.", null, FileUtil.getExtension( (File)null ) );
		assertEquals( "Incorrect extension.", "", FileUtil.getExtension( new File( "test" ) ) );
		assertEquals( "Incorrect extension.", "txt", FileUtil.getExtension( new File( "test.txt" ) ) );
	}

	public void testGetExtensionWithName() throws Exception {
		assertEquals( "Incorrect extension.", null, FileUtil.getExtension( (String)null ) );
		assertEquals( "Incorrect extension.", "", FileUtil.getExtension( "test" ) );
		assertEquals( "Incorrect extension.", "txt", FileUtil.getExtension( "test.txt" ) );
	}

}
