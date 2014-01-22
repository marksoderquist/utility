package com.parallelsymmetry.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import com.parallelsymmetry.utility.log.Log;

public class OperatingSystem {

	public static enum Family {
		UNKNOWN, LINUX, UNIX, WINDOWS, OS2, MAC, MAC_OLD
	};

	public static enum Architecture {
		UNKNOWN, X86, X64, PPC
	}

	static final String ELEVATED_PRIVILEGE_KEY = "escape.process.privilege";

	static final String ELEVATED_PRIVILEGE_VALUE = "elevated";

	private static Architecture architecture;

	private static Family family;

	private static String version;

	private static String name;

	private static String arch;

	private static Boolean elevated;

	private static boolean fileSystemCaseSensitive;

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
		} else if( name.contains( "SunOS" )
			| name.contains( "Solaris" )
			| name.contains( "HP-UX" )
			| name.contains( "AIX" )
			| name.contains( "FreeBSD" ) ) {
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

		// Case sensitive file system.
		File fileOne = new File( "TeStFiLe" );
		File fileTwo = new File( "tEsTfIlE" );
		fileSystemCaseSensitive = !fileOne.equals( fileTwo );
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

	public static final boolean isUnix() {
		return isLinux() || family == Family.UNIX;
	}

	public static final boolean isWindows() {
		return family == Family.WINDOWS;
	}

	/**
	 * Check if the process has elevated privileges.
	 * 
	 * @return true if the process has elevated privileges.
	 */
	public static final boolean isProcessElevated() {
		if( elevated == null && isElevatedFlagSet() ) elevated = Boolean.TRUE;

		if( elevated == null ) {
			if( isWindows() ) {
				elevated = new Boolean( canWriteToProgramFiles() );
			} else {
				elevated = new Boolean( System.getProperty( "user.name" ).equals( "root" ) );
			}
		}

		return elevated.booleanValue();
	}

	public static final boolean isElevateProcessSupported() {
		return OperatingSystem.isMac() || OperatingSystem.isUnix() || OperatingSystem.isWindows();
	}

	public static final boolean isReduceProcessSupported() {
		return OperatingSystem.isMac() || OperatingSystem.isUnix() || OperatingSystem.isWindows();
	}

	/**
	 * Test the file system for case sensitivity.
	 */
	public static final boolean isFileSystemCaseSensitive() {
		return fileSystemCaseSensitive;
	}

	public static final Process startProcessElevated( String programName, ProcessBuilder builder ) throws IOException {
		if( !OperatingSystem.isProcessElevated() ) elevateProcessBuilder( programName, builder );
		return builder.start();
	}

	public static final Process startProcessReduced( ProcessBuilder builder ) throws IOException {
		if( OperatingSystem.isProcessElevated() ) reduceProcessBuilder( builder );
		return builder.start();
	}

	/**
	 * Modify the process builder to attempt to elevate the process privileges
	 * when the process is started. The returned ProcessBuilder should not be
	 * modified after this call to avoid problems even though this cannot be
	 * enforced.
	 * 
	 * @param programName The name of the program requesting elevated privileges
	 * @param builder
	 * @return
	 * @throws IOException
	 */
	public static final ProcessBuilder elevateProcessBuilder( String programName, ProcessBuilder builder ) throws IOException {
		List<String> command = getElevateCommands( programName );
		command.addAll( builder.command() );
		builder.command( command );
		return builder;
	}

	/**
	 * Modify the process builder to reduce the process privileges when the
	 * process is started. The returned ProcessBuilder should not be modified
	 * after this call to avoid problems even though this cannot be enforced.
	 * 
	 * @param builder
	 * @return
	 * @throws IOException
	 */
	public static final ProcessBuilder reduceProcessBuilder( ProcessBuilder builder ) throws IOException {
		List<String> command = getReduceCommands();

		if( isWindows() ) {
			StringBuilder inner = new StringBuilder();

			for( String c : builder.command() ) {
				if( c.contains( " " ) ) {
					inner.append( "\\\"" );
					inner.append( c );
					inner.append( "\\\"" );
				} else {
					inner.append( c );
				}
				inner.append( " " );
			}

			StringBuilder outer = new StringBuilder();
			outer.append( "\"" );
			outer.append( inner.toString().trim() );
			outer.append( "\"" );

			command.add( outer.toString() );
		} else {
			command.addAll( builder.command() );
			builder.command( command );
		}

		builder.command( command );

		return builder;
	}

	public static final String getJavaExecutableName() {
		return isWindows() ? "javaw" : "java";
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
	 * Get the program data folder for the operating system. On Windows systems
	 * this is the %APPDATA% location. On other systems this is $HOME.
	 * <p>
	 * Exapmles:
	 * <p>
	 * Windows 7: C:\Users\&lt;username&gt;\AppData\Roaming<br/>
	 * Linux: /home/&lt;username&gt;
	 * 
	 * @param vendor The vendor name to include in the path.
	 * @param product The product name to include in the path.
	 * @return
	 */
	public static final File getUserProgramDataFolder() {
		File folder = null;
		switch( family ) {
			case WINDOWS: {
				folder = new File( System.getenv( "appdata" ) );
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
	 * Get the program data folder for the operating system using the program
	 * identifier and/or name. The program identifier is normally all lower case
	 * with no spaces. The name can be mixed case with spaces. Windows systems use
	 * the name instead of the identifier to generate the program data folder
	 * path.
	 * 
	 * @param identifier
	 * @param name
	 * @return
	 */
	public static final File getUserProgramDataFolder( String identifier, String name ) {
		File folder = null;
		switch( family ) {
			case WINDOWS: {
				folder = new File( getUserProgramDataFolder(), name );
				break;
			}
			default: {
				folder = new File( getUserProgramDataFolder(), "." + identifier );
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

	/**
	 * Get the shared program data folder for the operating system using the
	 * program identifier and/or name. The program identifier is normally all
	 * lower case with no spaces. The name can be mixed case with spaces. Windows
	 * systems use the name instead of the identifier to generate the program data
	 * folder path.
	 * 
	 * @param identifier
	 * @param name
	 * @return
	 */
	public static final File getSharedProgramDataFolder( String identifier, String name ) {
		File folder = null;
		switch( family ) {
			case WINDOWS: {
				folder = new File( getSharedProgramDataFolder(), name );
				break;
			}
			case LINUX: {
				folder = new File( getSharedProgramDataFolder(), identifier );
				break;
			}
			default: {
				folder = new File( getSharedProgramDataFolder(), "." + identifier );
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

	static final boolean isElevatedFlagSet() {
		return ELEVATED_PRIVILEGE_VALUE.equals( System.getenv( ELEVATED_PRIVILEGE_KEY ) )
			|| ELEVATED_PRIVILEGE_VALUE.equals( System.getProperty( ELEVATED_PRIVILEGE_KEY ) );
	}

	static final void clearProcessElevatedCache() {
		elevated = null;
	}

	private static final boolean canWriteToProgramFiles() {
		if( !OperatingSystem.isWindows() ) return false;
		try {
			String programFilesFolder = System.getenv( "ProgramFiles" );
			if( programFilesFolder == null ) programFilesFolder = "C:\\Program Files";
			File privilegeCheckFile = new File( programFilesFolder, "privilege.check.txt" );
			return privilegeCheckFile.createNewFile() && privilegeCheckFile.delete();
		} catch( IOException exception ) {
			return false;
		}
	}

	private static final List<String> getElevateCommands( String programName ) throws IOException {
		List<String> commands = new ArrayList<String>();

		if( isMac() ) {
			commands.add( extractMacElevate().getPath() );
		} else if( isUnix() ) {
			File gksudo = new File( "/usr/bin/gksudo" );
			File kdesudo = new File( "/usr/bin/kdesudo" );
			if( gksudo.exists() ) {
				commands.add( "/usr/bin/gksudo" );
				commands.add( "-D" );
				commands.add( programName );
				commands.add( "--" );
			} else if( kdesudo.exists() ) {
				commands.add( "/usr/bin/kdesudo" );
				commands.add( "--" );
			} else {
				commands.add( "xterm" );
				commands.add( "-title" );
				commands.add( programName );
				commands.add( "-e" );
				commands.add( "sudo" );
			}
		} else if( isWindows() ) {
			commands.add( "wscript" );
			commands.add( extractWinElevate().getPath() );
		}

		return commands;
	}

	private static final List<String> getReduceCommands() throws IOException {
		List<String> commands = new ArrayList<String>();

		if( isWindows() ) {
			commands.add( "runas" );
			commands.add( "/trustlevel:0x20000" );
		} else {
			commands.add( "su" );
			commands.add( "-" );
			commands.add( System.getenv( "SUDO_USER" ) );
		}

		return commands;
	}

	private static final File extractWinElevate() throws IOException {
		File elevator = new File( System.getProperty( "java.io.tmpdir" ), "elevate.js" ).getCanonicalFile();
		InputStream source = OperatingSystem.class.getResourceAsStream( "/elevate/win-elevate.js" );
		FileOutputStream target = new FileOutputStream( elevator );
		try {
			IoUtil.copy( source, target );
		} finally {
			source.close();
			target.close();
		}

		elevator.setExecutable( true );

		return elevator;
	}

	private static final File extractMacElevate() throws IOException {
		File elevator = new File( System.getProperty( "java.io.tmpdir" ), "elevate" ).getCanonicalFile();
		InputStream source = OperatingSystem.class.getResourceAsStream( "/elevate/mac-elevate" );
		FileOutputStream target = new FileOutputStream( elevator );
		try {
			IoUtil.copy( source, target );
		} finally {
			source.close();
			target.close();
		}

		elevator.setExecutable( true );

		return elevator;
	}

}
