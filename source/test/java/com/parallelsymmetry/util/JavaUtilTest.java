package com.parallelsymmetry.util;

import java.io.File;
import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

public class JavaUtilTest extends TestCase {

	public void testParseSystemClasspath() {
		String classpath = "test1.jar" + File.pathSeparator + "test2.jar";
		String[] entries = JavaUtil.parseSystemClasspath( classpath );

		assertEquals( "test1.jar", entries[0] );
		assertEquals( "test2.jar", entries[1] );
	}

	public void testParseManifestClasspath() throws Exception {
		File home = new File( "" );
		URI base = new File( "" ).toURI();
		String classpath = "test1.jar test2.jar test%203.jar";
		URL[] entries = JavaUtil.parseManifestClasspath( base, classpath );

		assertEquals( new File( home.getCanonicalFile(), "test1.jar" ).toURI().toURL(), entries[0] );
		assertEquals( new File( home.getCanonicalFile(), "test2.jar" ).toURI().toURL(), entries[1] );
		assertEquals( new File( home.getCanonicalFile(), "test 3.jar" ).toURI().toURL(), entries[2] );
	}

}
