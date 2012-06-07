package com.parallelsymmetry.escape.utility;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

public class ReducedProcessBuilderTest extends TestCase {

	private static final String user = "tester";

	@Test
	public void testCommandMac() throws Exception {
		OperatingSystemTest.init( "Mac OS X", "ppc", "10" );
		System.setProperty( ElevatedProcessBuilder.ELEVATED_PRIVILEGE_KEY, ElevatedProcessBuilder.ELEVATED_PRIVILEGE_VALUE );

		try {
			ReducedProcessBuilder builder = new ReducedProcessBuilder( user );
			fail();
		} catch( UnsupportedOperationException exception ) {

		}
		//builder.command().add( "textmate" );

		//		assertEquals( 2, builder.command().size() );
		//		assertEquals( elevate.getCanonicalPath(), builder.command().get( 0 ) );
		//		assertEquals( "textmate", builder.command().get( 1 ) );
	}

	@Test
	public void testCommandUnix() throws Exception {
		OperatingSystemTest.init( "Linux", "x86_64", "2.6.32_45" );
		System.setProperty( ElevatedProcessBuilder.ELEVATED_PRIVILEGE_KEY, ElevatedProcessBuilder.ELEVATED_PRIVILEGE_VALUE );

		ReducedProcessBuilder builder = new ReducedProcessBuilder( user );
		builder.command().add( "vi" );

		assertEquals( 3, builder.command().size() );
		assertEquals( "su", builder.command().get( 0 ) );
		assertEquals( user, builder.command().get( 1 ) );
		assertEquals( "vi", builder.command().get( 2 ) );
	}

	@Test
	public void testCommandWindows() throws Exception {
		OperatingSystemTest.init( "Windows 7", "x86", "6.1" );
		System.setProperty( ElevatedProcessBuilder.ELEVATED_PRIVILEGE_KEY, ElevatedProcessBuilder.ELEVATED_PRIVILEGE_VALUE );

		ReducedProcessBuilder builder = new ReducedProcessBuilder( user );
		builder.command().add( "notepad.exe" );

		assertEquals( 3, builder.command().size() );
		assertEquals( "runas", builder.command().get( 0 ) );
		assertEquals( "/trustlevel:0x20000", builder.command().get( 1 ) );
		assertEquals( "notepad.exe", builder.command().get( 2 ) );
	}

}
