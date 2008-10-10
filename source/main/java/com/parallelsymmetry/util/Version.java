package com.parallelsymmetry.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class Version implements Comparable<Version> {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss Z" );

	public static final int INVALID = -1;

	private static final Version UNKNOWN = new Version( true );

	private static final String SNAPSHOT = "SNAPSHOT";

	private String original;

	private boolean unknown;

	private int major = INVALID;

	private int minor = INVALID;

	private int micro = INVALID;

	private String state;

	private boolean snapshot;

	private Date date;

	private Version( boolean unknown ) {
		this.unknown = unknown;
	}

	public static final Version parse( String string ) {
		return parse( string, null );
	}

	public static final Version parse( String string, String timestamp ) {
		if( TextUtil.isEmpty( string ) ) return UNKNOWN;

		List<String> elements = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer( string, ".- " );
		while( tokenizer.hasMoreTokens() ) {
			elements.add( tokenizer.nextToken() );
		}

		Version version = new Version( false );

		version.original = string;

		for( String element : elements ) {
			checkElement( version, element );
		}

		if( timestamp != null ) {
			try {
				version.date = DATE_FORMAT.parse( timestamp );
			} catch( Exception exception ) {
				throw new RuntimeException( "Exception parsing version timestamp: " + timestamp, exception );
			}
		}

		return version;
	}

	public boolean isUnknown() {
		return unknown;
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
			if( minor != INVALID ) {
				buffer.append( "." );
				buffer.append( minor );
			}
		}

		return buffer.toString();
	}

	/**
	 * Get the version information.
	 * 
	 * @return A string representation of the version.
	 */
	public final String getFullVersion() {
		if( unknown ) return "Unknown";
		return original;
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
	public int compareTo( Version that ) {
		if( this.unknown != that.unknown ) return this.unknown ? 1 : -1;
		if( this.major != that.major ) return ObjectUtil.compare( this.major, that.major );
		if( this.minor != that.minor ) return ObjectUtil.compare( this.minor, that.minor );
		if( !TextUtil.areEqual( this.state, that.state ) ) return ObjectUtil.compare( this.state, that.state );
		if( this.micro != that.micro ) return ObjectUtil.compare( this.micro, that.micro );
		if( this.snapshot != that.snapshot ) return this.snapshot ? -1 : 1;

		return 0;
	}

	@Override
	public boolean equals( Object object ) {
		if( !( object instanceof Version ) ) return false;
		Version that = (Version)object;

		return this.micro == that.micro && TextUtil.areEqual( this.state, that.state ) && this.minor == that.minor && this.major == that.major && this.snapshot == that.snapshot && ObjectUtil.areEqual( this.date, that.date );
	}

	@Override
	public String toString() {
		return getVersion();
	}

	private static final void checkElement( Version version, String element ) {
		System.out.println( "Checking element: " + element );
		if( TextUtil.isInteger( element ) ) {
			if( version.major == INVALID ) {
				System.out.println( "Setting major version: " + element );
				version.major = Integer.parseInt( element );
			} else if( version.minor == INVALID ) {
				System.out.println( "Setting minor version: " + element );
				version.minor = Integer.parseInt( element );
			} else if( version.micro == INVALID ) {
				System.out.println( "Setting micro version: " + element );
				version.micro = Integer.parseInt( element );
			}
		} else if( SNAPSHOT.equals( element ) ) {
			if( version.snapshot ) throw new RuntimeException( "SNAPSHOT should only be declared once." );
			version.snapshot = true;
		} else if( version.state != null ) {
			throw new RuntimeException( "State should only be declared once." );
		} else {
			version.state = element;
		}
	}

}
