package com.parallelsymmetry.escape.utility;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import com.parallelsymmetry.escape.utility.log.Log;

public class OperatingSystem {

	public static enum Family {
		UNKNOWN, LINUX, UNIX, WINDOWS, OS2, MAC, MAC_OLD
	};

	public static enum Architecture {
		UNKNOWN, X86, X64, PPC
	}

	private static Architecture architecture;

	private static Family family;

	private static String version;

	private static String name;

	private static String arch;

	/**
	 * Initialize the class.
	 */
	static {
		init( System.getProperty( "os.name" ), System.getProperty( "os.arch" ), System.getProperty( "os.version" ) );
	}

	/**
	 * The init() method is intentionally private, and separate from the static
	 * initializer, so the initializing logic can be tested.
	 * 
	 * @param name The os name from System.getProperty( "os.name" ).
	 * @param arch The os arch from System.getProperty( "os.arch" ).
	 * @param version The os version from System.getProperty( "os.version" ).
	 */
	private static final void init( String name, String arch, String version ) {
		OperatingSystem.name = name;
		OperatingSystem.arch = arch;

		// Determine the OS family.
		if( name.contains( "Linux" ) ) {
			family = Family.LINUX;
		} else if( name.contains( "Windows" ) ) {
			family = Family.WINDOWS;
		} else if( name.contains( "OS/2" ) ) {
			family = Family.OS2;
		} else if( name.contains( "SunOS" ) | name.contains( "Solaris" ) | name.contains( "HP-UX" ) | name.contains( "AIX" ) | name.contains( "FreeBSD" ) ) {
			family = Family.UNIX;
		} else if( name.contains( "Mac OS" ) ) {
			if( name.contains( "Mac OS X" ) ) {
				family = Family.MAC;
			} else {
				family = Family.MAC_OLD;
			}
		} else {
			family = Family.UNKNOWN;
		}

		// Determine the OS architecture.
		if( arch.matches( "x86" ) || arch.matches( "i.86" ) ) {
			OperatingSystem.architecture = Architecture.X86;
		} else if( "x86_64".equals( arch ) || "amd64".equals( arch ) ) {
			OperatingSystem.architecture = Architecture.X64;
		} else if( "ppc".equals( arch ) || "PowerPC".equals( arch ) ) {
			OperatingSystem.architecture = Architecture.PPC;
		} else {
			OperatingSystem.architecture = Architecture.UNKNOWN;
		}

		// Store the version.
		OperatingSystem.version = version;
	}

	public static final String getName() {
		return name;
	}

	public static final Family getFamily() {
		return family;
	}

	public static final String getVersion() {
		return version;
	}

	public static final Architecture getArchitecture() {
		return architecture;
	}

	public static final String getSystemArchitecture() {
		return arch;
	}

	public static final boolean isLinux() {
		return family == Family.LINUX;
	}

	public static final boolean isMac() {
		return family == Family.MAC;
	}

	public static final boolean isWindows() {
		return family == Family.WINDOWS;
	}

	/**
	 * Returns the total system memory in bytes or -1 if it cannot be determined.
	 * 
	 * @return The total system memory in bytes or -1 if it cannot be determined.
	 */
	@SuppressWarnings( "restriction" )
	public static final long getTotalSystemMemory() {
		long memory = -1;
		try {
			memory = ( (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean() ).getTotalPhysicalMemorySize();
		} catch( Throwable throwable ) {
			// Intentionally ignore exception.
		}
		return memory;
	}

	/**
	 * Get the program data folder for the operating system. On Windows
	 * systems this is the %APPDATA% location. On Linux systems this is
	 * $HOME.
	 * <p>
	 * Exapmles:
	 * <p>
	 * Windows 7: C:/ProgramData/<br/>
	 * Linux: /usr/local/share/data/
	 * 
	 * @param vendor The vendor name to include in the path.
	 * @param product The product name to include in the path.
	 * @return
	 */
	public static final File getProgramDataFolder() {
		File folder = null;
		switch( family ) {
			case WINDOWS: {
				folder = new File( System.getenv( "appdata" ) );
				break;
			}
			case LINUX: {
				folder = new File( System.getProperty( "user.home" ) );
				break;
			}
			default: {
				folder = new File( System.getProperty( "user.home" ) );
				break;
			}
		}

		try {
			return folder.getCanonicalFile();
		} catch( IOException exception ) {
			Log.write( exception );
		}

		return null;
	}

	/**
	 * Get the shared program data folder for the operating system. On Windows
	 * systems this is the %ALLUSERSPROFILE% location. On Linux systems this is
	 * /usr/local/share/data.
	 * <p>
	 * Exapmles:
	 * <p>
	 * Windows 7: C:/ProgramData/<br/>
	 * Linux: /usr/local/share/data/
	 * 
	 * @param vendor The vendor name to include in the path.
	 * @param product The product name to include in the path.
	 * @return
	 */
	public static final File getSharedProgramDataFolder() {
		File folder = null;
		switch( family ) {
			case WINDOWS: {
				folder = new File( System.getenv( "allusersprofile" ) );
				break;
			}
			case LINUX: {
				folder = new File( "/usr/local/share/data" );
				break;
			}
			default: {
				folder = new File( System.getProperty( "user.home" ) );
				break;
			}
		}

		try {
			return folder.getCanonicalFile();
		} catch( IOException exception ) {
			Log.write( exception );
		}

		return null;
	}

	public static final Process runElevated( ProcessBuilder builder ) {
		// Elevate does not come with Windows it must be provided.
		// In Windows: elevate <program>
		return null;
	}

	public static final Process runNormally( ProcessBuilder builder ) {
		// In Windows: runas /trustlevel:0x20000 <program>
		return null;
	}

}
