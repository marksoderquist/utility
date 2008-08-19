package com.parallelsymmetry.util;

import java.io.File;

/**
 * Operating system detection routines.
 * 
 * @author Mark Soderquist
 */
public class OperatingSystem {

	private static final int UNKNOWN = 0;

	private static final int UNIX = 1;

	private static final int WINDOWS_9X = 2;

	private static final int WINDOWS_NT = 3;

	private static final int OS2 = 4;

	private static final int MAC_OS_X = 5;

	private static int miOS;

	private static boolean mbFileSystemCaseSensitive;

	/**
	 * Initialize the class.
	 */
	static {
		String sOSName = System.getProperty( "os.name" );

		if( sOSName.indexOf( "Windows 9" ) != -1 || sOSName.indexOf( "Windows ME" ) != -1 ) {
			miOS = WINDOWS_9X;
		} else if( sOSName.indexOf( "Windows" ) != -1 ) {
			miOS = WINDOWS_NT;
		} else if( sOSName.indexOf( "OS/2" ) != -1 ) {
			miOS = OS2;
		} else if( File.separatorChar == '/' && new File( "/dev" ).isDirectory() ) {
			if( sOSName.indexOf( "Mac OS X" ) != -1 ) {
				miOS = MAC_OS_X;
			} else {
				miOS = UNIX;
			}
		} else {
			miOS = UNKNOWN;
			Log.write( Log.WARN, "Unknown operating system: " + sOSName );
		}

		File oFileOne = new File( "TestFile" );
		File oFileTwo = new File( "testfile" );
		mbFileSystemCaseSensitive = !oFileOne.equals( oFileTwo );
	}

	/**
	 * Returns if we're running Windows 95/98/ME/NT/2000/XP, or OS/2.
	 */
	public static final boolean isDOSDerived() {
		return isWindows() || isOS2();
	}

	/**
	 * Returns if we're running Windows 95/98/ME/NT/2000/XP.
	 */
	public static final boolean isWindows() {
		return miOS == WINDOWS_9X || miOS == WINDOWS_NT;
	}

	/**
	 * Returns if we're running Windows 95/98/ME.
	 */
	public static final boolean isWindows9x() {
		return miOS == WINDOWS_9X;
	}

	/**
	 * Returns if we're running Windows NT/2000/XP.
	 */
	public static final boolean isWindowsNT() {
		return miOS == WINDOWS_NT;
	}

	/**
	 * Returns if we're running OS/2.
	 */
	public static final boolean isOS2() {
		return miOS == OS2;
	}

	/**
	 * Returns if we're running Unix (this includes MacOS X).
	 */
	public static final boolean isUnix() {
		return miOS == UNIX || miOS == MAC_OS_X;
	}

	/**
	 * Returns if we're running MacOS X.
	 */
	public static final boolean isMacOS() {
		return miOS == MAC_OS_X;
	}

	/**
	 * Test the file system for case sensitivity.
	 */
	public static final boolean isFileSystemCaseSensitive() {
		return mbFileSystemCaseSensitive;
	}

}
