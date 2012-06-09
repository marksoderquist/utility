package com.parallelsymmetry.escape.utility;

import java.io.File;
import java.lang.reflect.Method;

import org.junit.Test;

import junit.framework.TestCase;

public class OperatingSystemTest extends TestCase {

	@Override
	public void setUp() throws Exception {
		System.clearProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY );
	}

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

	@Test
	public void testIsProcessElevatedMac() throws Exception {
		OperatingSystemTest.init( "Mac OS X", "ppc", "10" );
		assertFalse( OperatingSystem.isProcessElevated() );

		System.setProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY, OperatingSystem.ELEVATED_PRIVILEGE_VALUE );
		assertTrue( OperatingSystem.isProcessElevated() );
	}

	@Test
	public void testIsProcessElevatedUnix() throws Exception {
		OperatingSystemTest.init( "Linux", "x86_64", "2.6.32_45" );
		assertFalse( OperatingSystem.isProcessElevated() );

		System.setProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY, OperatingSystem.ELEVATED_PRIVILEGE_VALUE );
		assertTrue( OperatingSystem.isProcessElevated() );
	}

	@Test
	public void testIsProcessElevatedWindows() throws Exception {
		OperatingSystemTest.init( "Windows 7", "x86", "6.1" );
		assertFalse( OperatingSystem.isProcessElevated() );

		System.setProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY, OperatingSystem.ELEVATED_PRIVILEGE_VALUE );
		assertTrue( OperatingSystem.isProcessElevated() );
	}

	@Test
	public void testElevateProcessMac() throws Exception {
		OperatingSystemTest.init( "Mac OS X", "ppc", "10" );
		ProcessBuilder builder = new ProcessBuilder( "textmate" );
		File elevate = new File( System.getProperty( "java.io.tmpdir" ), "elevate" );

		OperatingSystem.elevateProcessBuilder( builder );
		assertEquals( 2, builder.command().size() );
		assertEquals( elevate.getCanonicalPath(), builder.command().get( 0 ) );
		assertEquals( "textmate", builder.command().get( 1 ) );
	}

	@Test
	public void testElevateProcessUnix() throws Exception {
		OperatingSystemTest.init( "Linux", "x86_64", "2.6.32_45" );
		ProcessBuilder builder = new ProcessBuilder( "vi" );

		OperatingSystem.elevateProcessBuilder( builder );
		assertEquals( 6, builder.command().size() );
		assertEquals( "xterm", builder.command().get( 0 ) );
		assertEquals( "-title", builder.command().get( 1 ) );
		assertEquals( "elevate", builder.command().get( 2 ) );
		assertEquals( "-e", builder.command().get( 3 ) );
		assertEquals( "sudo", builder.command().get( 4 ) );
		assertEquals( "vi", builder.command().get( 5 ) );
	}

	@Test
	public void testElevateProcessWindows() throws Exception {
		OperatingSystemTest.init( "Windows 7", "x86", "6.1" );
		ProcessBuilder builder = new ProcessBuilder( "notepad.exe" );
		File elevate = new File( System.getProperty( "java.io.tmpdir" ), "elevate.js" );

		OperatingSystem.elevateProcessBuilder( builder );
		assertEquals( 3, builder.command().size() );
		assertEquals( "wscript", builder.command().get( 0 ) );
		assertEquals( elevate.getCanonicalPath(), builder.command().get( 1 ) );
		assertEquals( "notepad.exe", builder.command().get( 2 ) );
	}

	@Test
	public void testReduceProcessMac() throws Exception {
		OperatingSystemTest.init( "Mac OS X", "ppc", "10" );
		System.setProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY, OperatingSystem.ELEVATED_PRIVILEGE_VALUE );
		ProcessBuilder builder = new ProcessBuilder( "textmate" );

		OperatingSystem.reduceProcessBuilder( builder );

		int index = 0;
		assertEquals( 4, builder.command().size() );
		assertEquals( "su", builder.command().get( index++ ) );
		assertEquals( "-", builder.command().get( index++ ) );
		assertEquals( null, builder.command().get( index++ ) );
		assertEquals( "textmate", builder.command().get( index++ ) );
	}

	@Test
	public void testReduceProcessUnix() throws Exception {
		OperatingSystemTest.init( "Linux", "x86_64", "2.6.32_45" );
		System.setProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY, OperatingSystem.ELEVATED_PRIVILEGE_VALUE );
		ProcessBuilder builder = new ProcessBuilder( "vi" );

		OperatingSystem.reduceProcessBuilder( builder );

		int index = 0;
		assertEquals( 4, builder.command().size() );
		assertEquals( "su", builder.command().get( index++ ) );
		assertEquals( "-", builder.command().get( index++ ) );
		assertEquals( null, builder.command().get( index++ ) );
		assertEquals( "vi", builder.command().get( index++ ) );
	}

	@Test
	public void testReduceProcessWindows() throws Exception {
		OperatingSystemTest.init( "Windows 7", "x86", "6.1" );
		System.setProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY, OperatingSystem.ELEVATED_PRIVILEGE_VALUE );
		ProcessBuilder builder = new ProcessBuilder( "javaw", "-jar", "C:\\Program Files\\Escape\\program.jar", "-update", "false" );

		OperatingSystem.reduceProcessBuilder( builder );

		int index = 0;
		assertEquals( 3, builder.command().size() );
		assertEquals( "runas", builder.command().get( index++ ) );
		assertEquals( "/trustlevel:0x20000", builder.command().get( index++ ) );
		assertEquals( "\"javaw -jar \\\"C:\\Program Files\\Escape\\program.jar\\\" -update false\"", builder.command().get( index++ ) );

	}

	public static final void init( String name, String arch, String version ) throws Exception {
		Method initMethod = OperatingSystem.class.getDeclaredMethod( "init", String.class, String.class, String.class );
		initMethod.setAccessible( true );
		initMethod.invoke( null, name, arch, version );
	}

}
