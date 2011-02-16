package com.parallelsymmetry.escape.utility;

import java.io.File;
import java.lang.reflect.Method;

import junit.framework.TestCase;

public class OperatingSystemTest extends TestCase {

	public void testLinux() throws Exception {
		init( "Linux", "x86_64", "2.6.32_45" );
		assertTrue( OperatingSystem.isLinux() );
		assertFalse( OperatingSystem.isMac() );
		assertFalse( OperatingSystem.isWindows() );
		assertEquals( "2.6.32_45", OperatingSystem.getVersion() );
		assertEquals( "x86_64", OperatingSystem.getSystemArchitecture() );
		assertEquals( OperatingSystem.Family.LINUX, OperatingSystem.getFamily() );
		assertEquals( new File( System.getProperty( "user.home" ) ).getCanonicalFile(), OperatingSystem.getProgramDataFolder() );
		assertEquals( new File( "/usr/local/share/data" ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder() );
	}

	public void testMac() throws Exception {
		init( "Mac OS X", "ppc", "10" );
		assertFalse( OperatingSystem.isLinux() );
		assertTrue( OperatingSystem.isMac() );
		assertFalse( OperatingSystem.isWindows() );
		assertEquals( "10", OperatingSystem.getVersion() );
		assertEquals( "ppc", OperatingSystem.getSystemArchitecture() );
		assertEquals( OperatingSystem.Family.MAC, OperatingSystem.getFamily() );
		assertEquals( new File( System.getProperty( "user.home" ) ).getCanonicalFile(), OperatingSystem.getProgramDataFolder() );
		assertEquals( new File( System.getProperty( "user.home" ) ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder() );
	}

	public void testWindows() throws Exception {
		init( "Windows 7", "x86", "6.1" );
		assertFalse( OperatingSystem.isLinux() );
		assertFalse( OperatingSystem.isMac() );
		assertTrue( OperatingSystem.isWindows() );
		assertEquals( "6.1", OperatingSystem.getVersion() );
		assertEquals( "x86", OperatingSystem.getSystemArchitecture() );
		assertEquals( OperatingSystem.Family.WINDOWS, OperatingSystem.getFamily() );
		assertEquals( new File( System.getenv( "appdata" ) ).getCanonicalFile(), OperatingSystem.getProgramDataFolder() );
		assertEquals( new File( System.getenv( "allusersprofile" ) ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder() );
	}

	private void init( String name, String arch, String version ) throws Exception {
		Method initMethod = OperatingSystem.class.getDeclaredMethod( "init", String.class, String.class, String.class );
		initMethod.setAccessible( true );
		initMethod.invoke( null, name, arch, version );
	}

}
