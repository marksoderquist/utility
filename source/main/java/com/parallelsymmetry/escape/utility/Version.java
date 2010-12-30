package com.parallelsymmetry.escape.utility;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public final class Version implements Comparable<Version> {

	private static final List<String> QUALIFIERS = Arrays.asList( new String[] { "a", "b", "s", "u" } );

	private static final Map<String, String> QUALIFIER_NAMES;

	private static final String RELEASE_FORMAT = "00";

	private static final String SNAPSHOT = "s";

	private int major;

	private int minor;

	private int micro;

	private String qualifier;

	private int release;

	private boolean snapshot;

	static {
		QUALIFIER_NAMES = new HashMap<String, String>();
		QUALIFIER_NAMES.put( "a", "Alpha" );
		QUALIFIER_NAMES.put( "b", "Beta" );
		QUALIFIER_NAMES.put( "s", "Snapshot" );
		QUALIFIER_NAMES.put( "u", "Update" );
	}

	private Version() {}

	public boolean isSnapshot() {
		return snapshot;
	}

	public String getVersion() {
		return getVersion( 0 );
	}

	public String getVersion( int level ) {
		if( level == 0 ) level = Integer.MAX_VALUE;

		StringBuilder builder = new StringBuilder();

		if( level > 0 ) {
			builder.append( major );
		}

		if( level > 1 ) {
			builder.append( '.' );
			builder.append( minor );
		}

		if( level > 2 ) {
			builder.append( '.' );
			builder.append( micro );
		}

		if( level > 3 ) {
			builder.append( '-' );
			if( SNAPSHOT.equals( qualifier ) ) {
				builder.append( "SNAPSHOT" );
			} else {
				builder.append( qualifier );
			}

		}

		if( level > 4 && !isSnapshot() ) {
			DecimalFormat format = new DecimalFormat( RELEASE_FORMAT );
			builder.append( '-' );
			builder.append( format.format( release ) );
		}

		return builder.toString();
	}

	public String toHumanString() {
		return toHumanString( 0 );
	}

	public String toHumanString( int level ) {
		if( level == 0 ) level = Integer.MAX_VALUE;

		StringBuilder builder = new StringBuilder();

		if( level > 0 ) {
			builder.append( major );
		}

		if( level > 1 ) {
			builder.append( '.' );
			builder.append( minor );
		}

		if( level > 2 ) {
			builder.append( '.' );
			builder.append( micro );
		}

		if( level > 3 && !( "u".equals( qualifier ) && release == 0 ) ) {
			builder.append( ' ' );
			builder.append( QUALIFIER_NAMES.get( qualifier ) );
		}

		if( level > 4 && !isSnapshot() && !( "u".equals( qualifier ) && release == 0 ) ) {
			builder.append( ' ' );
			builder.append( release );
		}

		return builder.toString();
	}

	@Override
	public int compareTo( Version that ) {
		return compareVersions( this, that );
	}

	/**
	 * Get a String representation of the version.
	 * 
	 * @return a String representation of the version.
	 */
	@Override
	public String toString() {
		return toHumanString();
	}

	public static Version parse( String string ) {
		if( string == null ) return null;

		Version version = new Version();
		StringTokenizer tokenizer = new StringTokenizer( string, ".-" );

		// Parse the major version number.
		if( tokenizer.hasMoreTokens() ) {
			try {
				version.major = Integer.parseInt( tokenizer.nextToken() );
				if( version.major < 1 || version.major > 9 ) return null;
			} catch( NumberFormatException exception ) {
				return null;
			}
		} else {
			return null;
		}

		// Parse the minor version number.
		if( tokenizer.hasMoreTokens() ) {
			try {
				version.minor = Integer.parseInt( tokenizer.nextToken() );
				if( version.minor < 0 || version.minor > 9 ) return null;
			} catch( NumberFormatException exception ) {
				return null;
			}
		} else {
			return null;
		}

		// Parse the micro version number.
		if( tokenizer.hasMoreTokens() ) {
			try {
				version.micro = Integer.parseInt( tokenizer.nextToken() );
				if( version.micro < 0 || version.micro > 9 ) return null;
			} catch( NumberFormatException exception ) {
				return null;
			}
		} else {
			return null;
		}

		// Parse the qualifier.
		if( tokenizer.hasMoreTokens() ) {
			String qualifier = tokenizer.nextToken().toLowerCase().substring( 0, 1 );
			if( !QUALIFIERS.contains( qualifier ) ) return null;
			version.qualifier = qualifier;
			version.snapshot = SNAPSHOT.equals( qualifier );
		} else {
			return null;
		}

		// Parse the build number.
		if( !version.isSnapshot() && tokenizer.hasMoreTokens() ) {
			try {
				String release = tokenizer.nextToken();
				if( release.length() != 2 ) return null;
				version.release = Integer.parseInt( release );
			} catch( NumberFormatException exception ) {
				return null;
			}
		}

		return version;
	}

	/**
	 * Compare two version numbers.
	 * 
	 * @param version1 First version to check.
	 * @param version2 Second version to check.
	 * @return If version1 is newer (higher number), return 1. If version2 is
	 *         newer, return -1. If version1 equals version2, return 0.
	 */
	public static int compareVersions( Version version1, Version version2 ) {
		return compareVersions( version1, version2, 0 );
	}

	/**
	 * Compares two version numbers.
	 * 
	 * @param version1 First version to check.
	 * @param version2 Second version to check.
	 * @return If version1 is newer (higher number), return 1. If version2 is
	 *         newer, return -1. If version1 equals version2, return 0.
	 */
	public static int compareVersions( Version version1, Version version2, int level ) {
		if( level == 0 ) level = Integer.MAX_VALUE;

		// Check the major part.
		if( level >= 1 ) {
			int result = version1.major - version2.major;
			if( result != 0 ) return result > 0 ? 1 : -1;
		}

		// Check the minor part.
		if( level >= 2 ) {
			int result = version1.minor - version2.minor;
			if( result != 0 ) return result > 0 ? 1 : -1;
		}

		// Check the micro part.
		if( level >= 3 ) {
			int result = version1.micro - version2.micro;
			if( result != 0 ) return result > 0 ? 1 : -1;
		}

		// Check the qualifier part.
		if( level >= 4 ) {
			int result = comparableIndex( version1.qualifier ) - comparableIndex( version2.qualifier );
			if( result != 0 ) return result > 0 ? 1 : -1;
		}

		// Check the release part.
		if( level >= 5 && ( !version1.isSnapshot() & !version2.isSnapshot() ) ) {
			int result = version1.release - version2.release;
			if( result != 0 ) return result > 0 ? 1 : -1;
		}

		// Versions are equal.
		return 0;
	}

	private static int comparableIndex( String qualifier ) {
		int i = QUALIFIERS.indexOf( qualifier );
		return i == -1 ? QUALIFIERS.size() : i;
	}

}
