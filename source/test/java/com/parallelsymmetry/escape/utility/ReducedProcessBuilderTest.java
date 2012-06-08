package com.parallelsymmetry.escape.utility;

import junit.framework.TestCase;

import org.junit.Test;

public class ReducedProcessBuilderTest extends TestCase {

	@Test
	public void testConstructorWithProcessBuilder() throws Exception {
		OperatingSystemTest.init( "Windows 7", "x86", "6.1" );
		System.setProperty( ElevatedProcessBuilder.ELEVATED_PRIVILEGE_KEY, ElevatedProcessBuilder.ELEVATED_PRIVILEGE_VALUE );

		ReducedProcessBuilder outer = new ReducedProcessBuilder( new ProcessBuilder( "java" ) );
		assertEquals( 3, outer.command().size() );
		assertEquals( "runas", outer.command().get( 0 ) );
		assertEquals( "/trustlevel:0x20000", outer.command().get( 1 ) );
		assertEquals( "java", outer.command().get( 2 ) );
	}

	@Test
	public void testCommandMac() throws Exception {
		OperatingSystemTest.init( "Mac OS X", "ppc", "10" );
		System.setProperty( ElevatedProcessBuilder.ELEVATED_PRIVILEGE_KEY, ElevatedProcessBuilder.ELEVATED_PRIVILEGE_VALUE );

		try {
			ReducedProcessBuilder builder = new ReducedProcessBuilder();
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

		ReducedProcessBuilder builder = new ReducedProcessBuilder();
		builder.command().add( "vi" );

		assertEquals( 3, builder.command().size() );
		assertEquals( "su", builder.command().get( 0 ) );
		assertEquals( null, builder.command().get( 1 ) );
		assertEquals( "vi", builder.command().get( 2 ) );
	}

	/**
	 * The resulting command line should look similar to this:
	 * <p><code>runas /trustlevel:0x20000 "javaw -jar \"C:\Program Files\Escape\program.jar\" -update false"</code>

	 * @throws Exception
	 */
	@Test
	public void testCommandWindows() throws Exception {
		OperatingSystemTest.init( "Windows 7", "x86", "6.1" );
		System.setProperty( ElevatedProcessBuilder.ELEVATED_PRIVILEGE_KEY, ElevatedProcessBuilder.ELEVATED_PRIVILEGE_VALUE );

		ReducedProcessBuilder builder = new ReducedProcessBuilder();
		builder.command().add( "javaw" );
		builder.command().add( "-jar" );
		builder.command().add( "C:\\Program Files\\Escape\\program.jar" );
		builder.command().add( "-update" );
		builder.command().add( "false" );

		int index = -1;
		assertEquals( 7, builder.command().size() );
		assertEquals( "runas", builder.command().get( index++ ) );
		assertEquals( "/trustlevel:0x20000", builder.command().get(  index++ ) );
		assertEquals( "\"javaw -jar \\\"C:\\Program Files\\Escape\\program.jar\\\" -update false\"", builder.command().get(  index++ ) );
		
	}

}
