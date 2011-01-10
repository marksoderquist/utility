package com.parallelsymmetry.escape.utility;

import java.lang.reflect.Method;

import junit.framework.TestCase;

public class OperatingSystemTest extends TestCase {

	public void testLinux() throws Exception {
		init( "Linux", "x86_64", "2.6.32_45" );
		assertTrue( OperatingSystem.isLinux() );
		assertFalse( OperatingSystem.isMacOsx() );
		assertFalse( OperatingSystem.isWindows() );
		assertEquals( "2.6.32_45", OperatingSystem.getVersion() );
		assertEquals( "x86_64", OperatingSystem.getArchitecture() );
		assertEquals( "LINUX", OperatingSystem.getFamily() );
	}

	public void testMacOs() throws Exception {
		init( "Mac OS X", "ppc", "10" );
		assertFalse( OperatingSystem.isLinux() );
		assertTrue( OperatingSystem.isMacOsx() );
		assertFalse( OperatingSystem.isWindows() );
		assertEquals( "10", OperatingSystem.getVersion() );
		assertEquals( "ppc", OperatingSystem.getArchitecture() );
		assertEquals( "MAC", OperatingSystem.getFamily() );
	}

	public void testWindows() throws Exception {
		init( "Windows 7", "x86", "6.1" );
		assertFalse( OperatingSystem.isLinux() );
		assertFalse( OperatingSystem.isMacOsx() );
		assertTrue( OperatingSystem.isWindows() );
		assertEquals( "6.1", OperatingSystem.getVersion() );
		assertEquals( "x86", OperatingSystem.getArchitecture() );
		assertEquals( "WINDOWS", OperatingSystem.getFamily() );
	}

	private void init( String name, String arch, String version ) throws Exception {
		Method initMethod = OperatingSystem.class.getDeclaredMethod( "init", String.class, String.class, String.class );
		initMethod.setAccessible( true );
		initMethod.invoke( null, name, arch, version );
	}

}
