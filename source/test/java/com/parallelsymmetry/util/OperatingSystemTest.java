package com.parallelsymmetry.util;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;

public class OperatingSystemTest extends TestCase {

	@Test
	public void testWindows() {
		OperatingSystem.init( "Windows 95", System.getProperty( "os.arch" ) );
		assertEquals( OperatingSystem.Family.WINDOWS, OperatingSystem.getFamily() );

		OperatingSystem.init( "Windows 98", System.getProperty( "os.arch" ) );
		assertEquals( OperatingSystem.Family.WINDOWS, OperatingSystem.getFamily() );

		OperatingSystem.init( "Windows NT", System.getProperty( "os.arch" ) );
		assertEquals( OperatingSystem.Family.WINDOWS, OperatingSystem.getFamily() );

		OperatingSystem.init( "Windows 2000", System.getProperty( "os.arch" ) );
		assertEquals( OperatingSystem.Family.WINDOWS, OperatingSystem.getFamily() );

		OperatingSystem.init( "Windows XP", System.getProperty( "os.arch" ) );
		assertEquals( OperatingSystem.Family.WINDOWS, OperatingSystem.getFamily() );

		OperatingSystem.init( "Windows 2003", System.getProperty( "os.arch" ) );
		assertEquals( OperatingSystem.Family.WINDOWS, OperatingSystem.getFamily() );

		OperatingSystem.init( "Windows Vista", System.getProperty( "os.arch" ) );
		assertEquals( OperatingSystem.Family.WINDOWS, OperatingSystem.getFamily() );
	}

	@Test
	public void testLinux() {
		OperatingSystem.init( "Linux", System.getProperty( "os.arch" ) );
		assertEquals( OperatingSystem.Family.LINUX, OperatingSystem.getFamily() );
	}

	@Test
	public void testUnix() {
		OperatingSystem.init( "SunOS", System.getProperty( "os.arch" ) );
		assertEquals( OperatingSystem.Family.UNIX, OperatingSystem.getFamily() );

		OperatingSystem.init( "Solaris", System.getProperty( "os.arch" ) );
		assertEquals( OperatingSystem.Family.UNIX, OperatingSystem.getFamily() );

		OperatingSystem.init( "HP-UX", System.getProperty( "os.arch" ) );
		assertEquals( OperatingSystem.Family.UNIX, OperatingSystem.getFamily() );

		OperatingSystem.init( "AIX", System.getProperty( "os.arch" ) );
		assertEquals( OperatingSystem.Family.UNIX, OperatingSystem.getFamily() );

		OperatingSystem.init( "FreeBSD", System.getProperty( "os.arch" ) );
		assertEquals( OperatingSystem.Family.UNIX, OperatingSystem.getFamily() );
	}

	@Test
	public void test32BitArchitecture() {
		OperatingSystem.init( System.getProperty( "os.name" ), "i386" );
		assertEquals( OperatingSystem.Architecture.X86, OperatingSystem.getArchitecture() );

		OperatingSystem.init( System.getProperty( "os.name" ), "i486" );
		assertEquals( OperatingSystem.Architecture.X86, OperatingSystem.getArchitecture() );

		OperatingSystem.init( System.getProperty( "os.name" ), "i586" );
		assertEquals( OperatingSystem.Architecture.X86, OperatingSystem.getArchitecture() );

		OperatingSystem.init( System.getProperty( "os.name" ), "i686" );
		assertEquals( OperatingSystem.Architecture.X86, OperatingSystem.getArchitecture() );
	}

	@Test
	public void test64BitArchitecture() {
		OperatingSystem.init( System.getProperty( "os.name" ), "amd64" );
		assertEquals( OperatingSystem.Architecture.X64, OperatingSystem.getArchitecture() );

		OperatingSystem.init( System.getProperty( "os.name" ), "x86_64" );
		assertEquals( OperatingSystem.Architecture.X64, OperatingSystem.getArchitecture() );
	}

	@Test
	public void testPpcArchitecture() {
		OperatingSystem.init( System.getProperty( "os.name" ), "ppc" );
		assertEquals( OperatingSystem.Architecture.PPC, OperatingSystem.getArchitecture() );

		OperatingSystem.init( System.getProperty( "os.name" ), "PowerPC" );
		assertEquals( OperatingSystem.Architecture.PPC, OperatingSystem.getArchitecture() );
	}

	@Test
	public void testGetApplicationDataFolder() {
		String name = "Test";
		String identifier = name.toLowerCase();

		OperatingSystem.init( "Linux", System.getProperty( "os.arch" ) );
		assertEquals( new File( System.getProperty( "user.dir" ), "." + identifier ), OperatingSystem.getApplicationDataFolder( identifier, name ) );

		OperatingSystem.init( "Windows Vista", System.getProperty( "os.arch" ) );
		assertEquals( new File( System.getProperty( "user.dir" ), "Application Data/" + name ), OperatingSystem.getApplicationDataFolder( identifier, name ) );
	}
}
