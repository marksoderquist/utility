package com.parallelsymmetry.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class Version {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss Z" );

	private static final Version UNKNOWN = new Version( true );

	private boolean unknown;

	private int major;

	private int minor;

	private String state;

	private int micro;

	private boolean snapshot;

	private Date date;

	private Version( boolean unknown ) {
		this.unknown = unknown;
	}

	public static final Version parse( String string, String timestamp ) {
		if( TextUtil.isEmpty( string ) ) return UNKNOWN;

		// TODO Write a multi-pass parser.

		Version version = new Version( false );
		StringTokenizer tokenizer = new StringTokenizer( string, "" );
		try {
			version.major = Integer.parseInt( tokenizer.nextToken( ".-" ) );
			version.minor = Integer.parseInt( tokenizer.nextToken( ".-" ) );
			version.state = tokenizer.nextToken( ".-" );
			version.micro = Integer.parseInt( tokenizer.nextToken( " .-" ) );

			if( string.contains( "SNAPSHOT" ) ) {
				try {
					version.snapshot = true;
					tokenizer.nextToken( " " );
				} catch( Exception exception ) {
					// Intentionally ignore the exception.
				}
			}
		} catch( Exception exception ) {
			throw new RuntimeException( "Exception parsing version string: " + string, exception );
		}

		if( timestamp != null ) {
			StringTokenizer timestampTokenizer = new StringTokenizer( timestamp, "" );
			try {
				if( timestampTokenizer.hasMoreTokens() ) {
					String date = timestampTokenizer.nextToken( " " );
					String time = timestampTokenizer.nextToken( " " );
					String zone = timestampTokenizer.nextToken( " " );

					version.date = DATE_FORMAT.parse( date + " " + time + " " + zone );
				}
			} catch( Exception exception ) {
				throw new RuntimeException( "Exception parsing version timestamp: " + timestamp, exception );
			}
		}

		return version;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getMicro() {
		return micro;
	}

	public String getState() {
		return state;
	}

	public boolean isSnapshot() {
		return snapshot;
	}

	public Date getDate() {
		return date;
	}

	/**
	 * Get the version information.
	 * 
	 * @return A string representation of the version.
	 */
	public final String getVersion() {
		StringBuffer buffer = new StringBuffer();

		if( unknown ) {
			buffer.append( "Unknown" );
		} else {
			buffer.append( major );
			buffer.append( "." );
			buffer.append( minor );
		}

		return buffer.toString();
	}

	/**
	 * Get the version information.
	 * 
	 * @return A string representation of the version.
	 */
	public final String getFullVersion() {
		StringBuffer buffer = new StringBuffer();

		if( unknown ) {
			buffer.append( "Unknown" );
		} else {
			buffer.append( major );
			buffer.append( "." );
			buffer.append( minor );
			buffer.append( " " );
			buffer.append( state );
			buffer.append( " " );
			buffer.append( micro );
			if( isSnapshot() ) buffer.append( " SNAPSHOT" );
		}

		return buffer.toString();
	}

	public final String getCodedVersion() {
		StringBuffer buffer = new StringBuffer();

		if( unknown ) {
			buffer.append( "Unknown" );
		} else {
			buffer.append( major );
			buffer.append( "-" );
			buffer.append( minor );
			buffer.append( "-" );
			buffer.append( state );
			buffer.append( "-" );
			buffer.append( micro );
			if( isSnapshot() ) buffer.append( "-SNAPSHOT" );
		}

		return buffer.toString();
	}

	public final String getDateString() {
		return date == null ? "Unknown" : DATE_FORMAT.format( date );
	}

	@Override
	public String toString() {
		return getFullVersion();
	}

	@Override
	public boolean equals( Object object ) {
		if( !( object instanceof Version ) ) return false;
		Version that = (Version)object;

		return this.micro == that.micro && TextUtil.areEqual( this.state, that.state ) && this.minor == that.minor && this.major == that.major && this.snapshot == that.snapshot && ObjectUtil.areEqual( this.date, that.date );
	}

}
