package com.parallelsymmetry.escape.utility;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

public class ElevatedProcessBuilderTest extends TestCase {

	/*
	 * Keep this test commented for normal use, uncomment to test manually.
	 */
	//	@Test
	//	public void testStart() throws Exception {
	//		ElevatedProcessBuilder builder = new ElevatedProcessBuilder();
	//
	//		builder.command().add( "javaw" );
	//		builder.command().add( "-jar" );
	//		builder.command().add( "C:\\Program Files\\Escape\\program.jar" );
	//		
	//		builder.start().waitFor();
	//	}

	@Test
	public void testCommandMac() throws Exception {
		OperatingSystemTest.init( "Mac OS X", "ppc", "10" );
		ElevatedProcessBuilder builder = new ElevatedProcessBuilder();

		builder.command().add( "textmate" );

		File elevate = new File( System.getProperty( "java.io.tmpdir" ), "elevate" );

		assertEquals( 2, builder.command().size() );
		assertEquals( elevate.getCanonicalPath(), builder.command().get( 0 ) );
		assertEquals( "textmate", builder.command().get( 1 ) );
	}

	@Test
	public void testCommandUnix() throws Exception {
		OperatingSystemTest.init( "Linux", "x86_64", "2.6.32_45" );
		ElevatedProcessBuilder builder = new ElevatedProcessBuilder();

		builder.command().add( "vi" );

		assertEquals( 6, builder.command().size() );
		assertEquals( "xterm", builder.command().get( 0 ) );
		assertEquals( "-title", builder.command().get( 1 ) );
		assertEquals( "elevate", builder.command().get( 2 ) );
		assertEquals( "-e", builder.command().get( 3 ) );
		assertEquals( "sudo", builder.command().get( 4 ) );
		assertEquals( "vi", builder.command().get( 5 ) );
	}

	@Test
	public void testCommandWindows() throws Exception {
		OperatingSystemTest.init( "Windows 7", "x86", "6.1" );
		ElevatedProcessBuilder builder = new ElevatedProcessBuilder();

		builder.command().add( "notepad.exe" );

		File elevate = new File( System.getProperty( "java.io.tmpdir" ), "elevate.js" );

		assertEquals( 3, builder.command().size() );
		assertEquals( "wscript", builder.command().get( 0 ) );
		assertEquals( elevate.getCanonicalPath(), builder.command().get( 1 ) );
		assertEquals( "notepad.exe", builder.command().get( 2 ) );
	}

}
