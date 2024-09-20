package com.parallelsymmetry.utility;

import org.junit.jupiter.api.Test;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JavaUtilTest extends BaseTestCase {

	@Test
	public void testGetCallingClassName() {
		assertEquals( JavaUtilTest.class.getName(), JavaUtil.getCallingClassName() );
	}

	@Test
	public void testGetCallingClassNameWithLevel() {
		assertEquals( Thread.class.getName(), JavaUtil.getCallingClassName( 0 ) );
		assertEquals( JavaUtil.class.getName(), JavaUtil.getCallingClassName( 1 ) );
	}

	@Test
	public void testGetClassNameWithString() {
		assertEquals( "Object", JavaUtil.getClassName( "java.lang.Object" ) );
	}

	@Test
	public void testGetClassNameWithClass() {
		assertEquals( "Object", JavaUtil.getClassName( Object.class ) );
	}

	@Test
	public void testGetKeySafeClassNameWithString() {
		assertEquals( "java.awt.geom.Rectangle2D.Double", JavaUtil.getKeySafeClassName( "java.awt.geom.Rectangle2D$Double" ) );
	}

	@Test
	public void testGetKeySafeClassNameWithClass() {
		assertEquals( "java.awt.geom.Rectangle2D.Double", JavaUtil.getKeySafeClassName( Rectangle2D.Double.class ) );
	}

	@Test
	public void testGetPackageNameWithString() {
		assertEquals( "java.lang", JavaUtil.getPackageName( "java.lang.Object" ) );
	}

	@Test
	public void testGetPackageNameWithClass() {
		assertEquals( "java.lang", JavaUtil.getPackageName( Object.class ) );
	}

	@Test
	public void testGetPackagePathWithString() {
		assertEquals( "/java/lang", JavaUtil.getPackagePath( "java.lang.Object" ) );
	}

	@Test
	public void testGetPackagePathWithClass() {
		assertEquals( "/java/lang", JavaUtil.getPackagePath( Object.class ) );
	}

	@Test
	public void testParseClasspath() throws Exception {
		List<URI> entries = JavaUtil.parseClasspath( null );
		assertEquals( 0, entries.size() );

		String separator = ";";
		String classpath = "test1.jar";
		classpath += separator + "test2.jar";
		classpath += separator + URLEncoder.encode( "http://www.parallelsymmetry.com/software/test3.jar", "UTF-8" );
		entries = JavaUtil.parseClasspath( classpath, separator );

		assertEquals( new File( "test1.jar" ).toURI(), entries.get( 0 ) );
		assertEquals( new File( "test2.jar" ).toURI(), entries.get( 1 ) );
		assertEquals( URI.create( "http://www.parallelsymmetry.com/software/test3.jar" ), entries.get( 2 ) );

		separator = ":";
		classpath = "test1.jar";
		classpath += separator + "test2.jar";
		classpath += separator + URLEncoder.encode( "http://www.parallelsymmetry.com/software/test3.jar", "UTF-8" );
		entries = JavaUtil.parseClasspath( classpath, separator );

		assertEquals( new File( "test1.jar" ).toURI(), entries.get( 0 ) );
		assertEquals( new File( "test2.jar" ).toURI(), entries.get( 1 ) );
		assertEquals( URI.create( "http://www.parallelsymmetry.com/software/test3.jar" ), entries.get( 2 ) );
	}

	@Test
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

	@Test
	public void testGetRootCause() {
		assertNull( JavaUtil.getRootCause( null ) );

		Throwable one = new Throwable();
		Throwable two = new Throwable( one );
		Throwable three = new Throwable( two );

		assertEquals( one, JavaUtil.getRootCause( one ) );
		assertEquals( one, JavaUtil.getRootCause( two ) );
		assertEquals( one, JavaUtil.getRootCause( three ) );
	}

	@Test
	public void testCompareJavaVersion() {
		assertTrue( JavaUtil.compareJavaVersion( "8", "1.8" ) == 0 );
		assertTrue( JavaUtil.compareJavaVersion( "8", "1.8.0_u11" ) < 0 );
		assertTrue( JavaUtil.compareJavaVersion( "8", "10.0.2+13-Ubuntu-1ubuntu0.18.04.1" ) < 0 );
		assertTrue( JavaUtil.compareJavaVersion( "10", "10.0.2+13-Ubuntu-1ubuntu0.18.04.1" ) < 0 );
	}

}
