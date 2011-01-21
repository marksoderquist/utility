package com.parallelsymmetry.escape.utility;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

public class JavaUtilTest extends TestCase {

	public void testGetSimpleClassName() {
		assertEquals( "Object", JavaUtil.getSimpleClassName( "java.lang.Object" ) );
	}

	public void testParseSystemClasspath() throws Exception {
		List<URI> entries = JavaUtil.parseSystemClasspath( null );
		assertEquals( 0, entries.size() );

		String classpath = "test1.jar" + File.pathSeparator + "test2.jar";
		entries = JavaUtil.parseSystemClasspath( classpath );

		assertEquals( URI.create( "test1.jar" ), entries.get( 0 ) );
		assertEquals( URI.create( "test2.jar" ), entries.get( 1 ) );
	}

	public void testParseManifestClasspath() throws Exception {
		File home = new File( "." ).getCanonicalFile();
		URI base = home.toURI();
		String classpath = "test1.jar test2.jar test%203.jar";

		List<URL> entries = JavaUtil.parseManifestClasspath( base, null );
		assertEquals( 0, entries.size() );

		entries = JavaUtil.parseManifestClasspath( null, classpath );
		assertEquals( 0, entries.size() );

		entries = JavaUtil.parseManifestClasspath( base, classpath );

		assertEquals( new File( home.getCanonicalFile(), "test1.jar" ).toURI().toURL(), entries.get( 0 ) );
		assertEquals( new File( home.getCanonicalFile(), "test2.jar" ).toURI().toURL(), entries.get( 1 ) );
		assertEquals( new File( home.getCanonicalFile(), "test 3.jar" ).toURI().toURL(), entries.get( 2 ) );
	}

}
