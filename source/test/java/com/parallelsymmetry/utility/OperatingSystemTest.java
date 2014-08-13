package com.parallelsymmetry.utility;

import java.io.File;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.junit.Test;

public class OperatingSystemTest extends TestCase {

	@Override
	public void setUp() throws Exception {
		System.clearProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY );
	}

	public void testLinux() throws Exception {
		init( "Linux", "x86_64", "2.6.32_45" );
		assertTrue( OperatingSystem.isLinux() );
		assertFalse( OperatingSystem.isMac() );
		assertTrue( OperatingSystem.isUnix() );
		assertFalse( OperatingSystem.isWindows() );
		assertEquals( "2.6.32_45", OperatingSystem.getVersion() );
		assertEquals( "x86_64", OperatingSystem.getSystemArchitecture() );
		assertEquals( OperatingSystem.Family.LINUX, OperatingSystem.getFamily() );
		assertEquals( "java", OperatingSystem.getJavaExecutableName() );
		// assertEquals( new File( System.getProperty( "user.home" )
		// ).getCanonicalFile(), OperatingSystem.getProgramDataFolder() );
		// assertEquals( new File( "/usr/local/share/data" ).getCanonicalFile(),
		// OperatingSystem.getSharedProgramDataFolder() );
		// assertEquals( new File( System.getProperty( "user.home" ), ".test"
		// ).getCanonicalFile(), OperatingSystem.getProgramDataFolder( "test",
		// "Test" ) );
		// assertEquals( new File( "/usr/local/share/data", "test"
		// ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder(
		// "test", "Test" ) );
	}

	public void testMac() throws Exception {
		init( "Mac OS X", "ppc", "10" );
		assertFalse( OperatingSystem.isLinux() );
		assertTrue( OperatingSystem.isMac() );
		assertTrue( OperatingSystem.isUnix() );
		assertFalse( OperatingSystem.isWindows() );
		assertEquals( "10", OperatingSystem.getVersion() );
		assertEquals( "ppc", OperatingSystem.getSystemArchitecture() );
		assertEquals( OperatingSystem.Family.MAC, OperatingSystem.getFamily() );
		assertEquals( "java", OperatingSystem.getJavaExecutableName() );
		// assertEquals( new File( System.getProperty( "user.home" )
		// ).getCanonicalFile(), OperatingSystem.getProgramDataFolder() );
		// assertEquals( new File( System.getProperty( "user.home" )
		// ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder() );
		// assertEquals( new File( System.getProperty( "user.home" ), ".test"
		// ).getCanonicalFile(), OperatingSystem.getProgramDataFolder( "test",
		// "Test" ) );
		// assertEquals( new File( System.getProperty( "user.home" ), ".test"
		// ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder(
		// "test", "Test" ) );
	}

	public void testWindows() throws Exception {
		init( "Windows 7", "x86", "6.1" );
		assertFalse( OperatingSystem.isLinux() );
		assertFalse( OperatingSystem.isMac() );
		assertFalse( OperatingSystem.isUnix() );
		assertTrue( OperatingSystem.isWindows() );
		assertEquals( "6.1", OperatingSystem.getVersion() );
		assertEquals( "x86", OperatingSystem.getSystemArchitecture() );
		assertEquals( OperatingSystem.Family.WINDOWS, OperatingSystem.getFamily() );
		assertEquals( "javaw", OperatingSystem.getJavaExecutableName() );
		// assertEquals( new File( System.getenv( "appdata" )
		// ).getCanonicalFile(), OperatingSystem.getProgramDataFolder() );
		// assertEquals( new File( System.getenv( "allusersprofile" )
		// ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder() );
		// assertEquals( new File( System.getenv( "appdata" ), "Test"
		// ).getCanonicalFile(), OperatingSystem.getProgramDataFolder( "test",
		// "Test" ) );
		// assertEquals( new File( System.getenv( "allusersprofile" ), "Test"
		// ).getCanonicalFile(), OperatingSystem.getSharedProgramDataFolder(
		// "test", "Test" ) );
	}

	@Test
	public void testIsProcessElevatedMac() throws Exception {
		OperatingSystemTest.init( "Mac OS X", "ppc", "10" );
		OperatingSystem.clearProcessElevatedCache();
		assertFalse( OperatingSystem.isProcessElevated() );

		System.setProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY, OperatingSystem.ELEVATED_PRIVILEGE_VALUE );
		OperatingSystem.clearProcessElevatedCache();
		assertTrue( OperatingSystem.isProcessElevated() );
	}

	@Test
	public void testIsProcessElevatedUnix() throws Exception {
		OperatingSystemTest.init( "Linux", "x86_64", "2.6.32_45" );
		OperatingSystem.clearProcessElevatedCache();
		assertFalse( OperatingSystem.isProcessElevated() );

		System.setProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY, OperatingSystem.ELEVATED_PRIVILEGE_VALUE );
		OperatingSystem.clearProcessElevatedCache();
		assertTrue( OperatingSystem.isProcessElevated() );
	}

	@Test
	public void testIsProcessElevatedWindows() throws Exception {
		OperatingSystemTest.init( "Windows 7", "x86", "6.1" );
		OperatingSystem.clearProcessElevatedCache();
		assertFalse( OperatingSystem.isProcessElevated() );

		System.setProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY, OperatingSystem.ELEVATED_PRIVILEGE_VALUE );
		OperatingSystem.clearProcessElevatedCache();
		assertTrue( OperatingSystem.isProcessElevated() );
	}

	@Test
	public void testElevateProcessMac() throws Exception {
		OperatingSystemTest.init( "Mac OS X", "ppc", "10" );
		ProcessBuilder builder = new ProcessBuilder( "textmate" );
		File elevate = new File( System.getProperty( "java.io.tmpdir" ), "elevate" );

		OperatingSystem.elevateProcessBuilder( "textmate", builder );
		assertEquals( 2, builder.command().size() );
		assertEquals( elevate.getCanonicalPath(), builder.command().get( 0 ) );
		assertEquals( "textmate", builder.command().get( 1 ) );
	}

	@Test
	public void testElevateProcessUnix() throws Exception {
		String program = "vi";
		OperatingSystemTest.init( "Linux", "x86_64", "2.6.32_45" );
		ProcessBuilder builder = new ProcessBuilder( program );
		OperatingSystem.elevateProcessBuilder( program, builder );

		File gksudo = new File( "/usr/bin/gksudo" );
		File kdesudo = new File( "/usr/bin/kdesudo" );
		if( gksudo.exists() ) {
			assertEquals( 5, builder.command().size() );
			assertEquals( gksudo.toString(), builder.command().get( 0 ) );
			assertEquals( program, builder.command().get( 4 ) );
		} else if( kdesudo.exists() ) {
			assertEquals( 3, builder.command().size() );
			assertEquals( kdesudo.toString(), builder.command().get( 0 ) );
			assertEquals( program, builder.command().get( 2 ) );
		} else {
			assertEquals( 6, builder.command().size() );
			assertEquals( "xterm", builder.command().get( 0 ) );
			assertEquals( "-title", builder.command().get( 1 ) );
			assertEquals( program, builder.command().get( 2 ) );
			assertEquals( "-e", builder.command().get( 3 ) );
			assertEquals( "sudo", builder.command().get( 4 ) );
			assertEquals( program, builder.command().get( 5 ) );
		}
	}

	@Test
	public void testElevateProcessWindows() throws Exception {
		OperatingSystemTest.init( "Windows 7", "x86", "6.1" );
		ProcessBuilder builder = new ProcessBuilder( "notepad.exe" );
		File elevate = new File( System.getProperty( "java.io.tmpdir" ), "elevate.js" );

		OperatingSystem.elevateProcessBuilder( "Notepad", builder );

		int index = 0;
		assertEquals( 3, builder.command().size() );
		assertEquals( "wscript", builder.command().get( index++ ) );
		assertEquals( elevate.getCanonicalPath(), builder.command().get( index++ ) );
		assertEquals( "notepad.exe", builder.command().get( index++ ) );
	}

	@Test
	public void testReduceProcessMac() throws Exception {
		OperatingSystemTest.init( "Mac OS X", "ppc", "10" );
		System.setProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY, OperatingSystem.ELEVATED_PRIVILEGE_VALUE );
		ProcessBuilder builder = new ProcessBuilder( "textmate" );

		OperatingSystem.reduceProcessBuilder( builder );

		int index = 0;
		assertEquals( 5, builder.command().size() );
		assertEquals( "su", builder.command().get( index++ ) );
		assertEquals( "-", builder.command().get( index++ ) );
		assertEquals( System.getenv( "SUDO_USER" ), builder.command().get( index++ ) );
		assertEquals( "--", builder.command().get( index++ ) );
		assertEquals( "textmate", builder.command().get( index++ ) );
	}

	@Test
	public void testReduceProcessUnix() throws Exception {
		OperatingSystemTest.init( "Linux", "x86_64", "2.6.32_45" );
		System.setProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY, OperatingSystem.ELEVATED_PRIVILEGE_VALUE );
		ProcessBuilder builder = new ProcessBuilder( "vi" );

		OperatingSystem.reduceProcessBuilder( builder );

		int index = 0;
		assertEquals( 5, builder.command().size() );
		assertEquals( "su", builder.command().get( index++ ) );
		assertEquals( "-", builder.command().get( index++ ) );
		assertEquals( System.getenv( "SUDO_USER" ), builder.command().get( index++ ) );
		assertEquals( "--", builder.command().get( index++ ) );
		assertEquals( "vi", builder.command().get( index++ ) );
	}

	@Test
	public void testReduceProcessWindows() throws Exception {
		OperatingSystemTest.init( "Windows 7", "x86", "6.1" );
		System.setProperty( OperatingSystem.ELEVATED_PRIVILEGE_KEY, OperatingSystem.ELEVATED_PRIVILEGE_VALUE );
		ProcessBuilder builder = new ProcessBuilder( "javaw", "-jar", "C:\\Program Files\\Escape\\program.jar", "-update", "false" );

		OperatingSystem.reduceProcessBuilder( builder );

		int index = 0;
		assertEquals( 5, builder.command().size() );
		assertEquals( "javaw", builder.command().get( index++ ) );
		assertEquals( "-jar", builder.command().get( index++ ) );
		assertEquals( "C:\\Program Files\\Escape\\program.jar", builder.command().get( index++ ) );
		assertEquals( "-update", builder.command().get( index++ ) );
		assertEquals( "false", builder.command().get( index++ ) );
	}

	public static final void init( String name, String arch, String version ) throws Exception {
		Method initMethod = OperatingSystem.class.getDeclaredMethod( "init", String.class, String.class, String.class );
		initMethod.setAccessible( true );
		initMethod.invoke( null, name, arch, version );
	}

}
