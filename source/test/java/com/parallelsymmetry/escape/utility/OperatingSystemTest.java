package com.parallelsymmetry.escape.utility;

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
		assertEquals( "java", OperatingSystem.getJavaExecutableName() );
		//		assertEquals( new File( System.getProperty( "user.home" ) ).getCanonicalFile(), OperatingSystem.getProgramDataFolder() );
		//		assertEquals( new File( "/usr/local/share/data" ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder() );
		//		assertEquals( new File( System.getProperty( "user.home" ), ".test" ).getCanonicalFile(), OperatingSystem.getProgramDataFolder( "test", "Test" ) );
		//		assertEquals( new File( "/usr/local/share/data", "test" ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder( "test", "Test" ) );
	}

	public void testMac() throws Exception {
		init( "Mac OS X", "ppc", "10" );
		assertFalse( OperatingSystem.isLinux() );
		assertTrue( OperatingSystem.isMac() );
		assertFalse( OperatingSystem.isWindows() );
		assertEquals( "10", OperatingSystem.getVersion() );
		assertEquals( "ppc", OperatingSystem.getSystemArchitecture() );
		assertEquals( OperatingSystem.Family.MAC, OperatingSystem.getFamily() );
		assertEquals( "java", OperatingSystem.getJavaExecutableName() );
		//		assertEquals( new File( System.getProperty( "user.home" ) ).getCanonicalFile(), OperatingSystem.getProgramDataFolder() );
		//		assertEquals( new File( System.getProperty( "user.home" ) ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder() );
		//		assertEquals( new File( System.getProperty( "user.home" ), ".test" ).getCanonicalFile(), OperatingSystem.getProgramDataFolder( "test", "Test" ) );
		//		assertEquals( new File( System.getProperty( "user.home" ), ".test" ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder( "test", "Test" ) );
	}

	public void testWindows() throws Exception {
		init( "Windows 7", "x86", "6.1" );
		assertFalse( OperatingSystem.isLinux() );
		assertFalse( OperatingSystem.isMac() );
		assertTrue( OperatingSystem.isWindows() );
		assertEquals( "6.1", OperatingSystem.getVersion() );
		assertEquals( "x86", OperatingSystem.getSystemArchitecture() );
		assertEquals( OperatingSystem.Family.WINDOWS, OperatingSystem.getFamily() );
		assertEquals( "javaw", OperatingSystem.getJavaExecutableName() );
		//		assertEquals( new File( System.getenv( "appdata" ) ).getCanonicalFile(), OperatingSystem.getProgramDataFolder() );
		//		assertEquals( new File( System.getenv( "allusersprofile" ) ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder() );
		//		assertEquals( new File( System.getenv( "appdata" ), "Test" ).getCanonicalFile(), OperatingSystem.getProgramDataFolder( "test", "Test" ) );
		//		assertEquals( new File( System.getenv( "allusersprofile" ), "Test" ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder( "test", "Test" ) );
	}

	public static final void init( String name, String arch, String version ) throws Exception {
		Method initMethod = OperatingSystem.class.getDeclaredMethod( "init", String.class, String.class, String.class );
		initMethod.setAccessible( true );
		initMethod.invoke( null, name, arch, version );
	}

}
