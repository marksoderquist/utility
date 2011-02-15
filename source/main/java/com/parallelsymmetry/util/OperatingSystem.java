package com.parallelsymmetry.util;

import java.io.File;

import com.parallelsymmetry.log.Log;

/**
 * Operating system detection routines.
 * 
 * @author Mark Soderquist
 */
public class OperatingSystem {

	public static enum Family {
		UNKNOWN, LINUX, UNIX, WINDOWS, OS2, MAC, OSX
	};

	public static enum Architecture {
		UNKNOWN, X86, X64, PPC
	}

	private static Family family;

	private static Architecture architecture;

	private static String name;

	@SuppressWarnings( "unused" )
	private static String arch;

	/**
	 * Initialize the class.
	 */
	static {
		init( System.getProperty( "os.name" ), System.getProperty( "os.arch" ) );
	}

	public static final void init( String name, String arch ) {
		OperatingSystem.name = name;

		OperatingSystem.arch = arch;

		// Determine the OS type.
		if( name.contains( "Linux" ) ) {
			family = Family.LINUX;
		} else if( name.contains( "Windows" ) ) {
			family = Family.WINDOWS;
		} else if( name.contains( "OS/2" ) ) {
			family = Family.OS2;
		} else if( name.contains( "Linux" ) | name.contains( "SunOS" ) | name.contains( "Solaris" ) | name.contains( "HP-UX" ) | name.contains( "AIX" ) | name.contains( "FreeBSD" ) ) {
			family = Family.UNIX;
		} else if( name.contains( "Mac OS" ) ) {
			if( name.contains( "Mac OS X" ) ) {
				family = Family.OSX;
			} else {
				family = Family.MAC;
			}
		} else {
			family = Family.UNKNOWN;
			Log.write( Log.WARN, "Undetermined operating system: " + name );
		}

		// Determine the OS architecture.
		if( arch.matches( "i.86" ) ) {
			OperatingSystem.architecture = Architecture.X86;
		} else if( "x86_64".equals( arch ) || "amd64".equals( arch ) ) {
			OperatingSystem.architecture = Architecture.X64;
		} else if( "ppc".equals( arch ) || "PowerPC".equals( arch ) ) {
			OperatingSystem.architecture = Architecture.PPC;
		} else {
			OperatingSystem.architecture = Architecture.UNKNOWN;
		}
	}
	
	public static final String getName() {
		return name;
	}

	public static final Family getFamily() {
		return family;
	}

	public static final Architecture getArchitecture() {
		return architecture;
	}

	public static final File getApplicationDataFolder( String identifier, String name ) {
		switch( family ) {
			case WINDOWS: {
				return new File( System.getenv( "APPDATA" ), name );
			}
			default: {
				return new File( System.getProperty( "user.home" ), "." + identifier );
			}
		}
	}
}
