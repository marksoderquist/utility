package com.parallelsymmetry.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class Version implements Comparable<Version> {

	public static final DateFormat PARSE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss Z" );

	public static final DateFormat PRINT_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

	public static final String UNKNOWN = "Unknown";

	private static final String SNAPSHOT = "SNAPSHOT";

	private static final int INVALID = -1;

	private String original;

	private boolean unknown;

	private int major = INVALID;

	private int minor = INVALID;

	private int micro = INVALID;

	private int build = INVALID;

	private String state;

	private Date date;

	private Version() {
		this( false );
	}

	private Version( boolean unknown ) {
		if( unknown ) this.unknown = unknown;
	}

	public static final Version parse( String string ) {
		return parse( string, null );
	}

	public static final Version parse( String string, String timestamp ) {
		Version version = new Version( TextUtil.isEmpty( string ) );

		if( !TextUtil.isEmpty( string ) ) {
			// Parse the version elements.
			List<String> elements = new ArrayList<String>();
			StringTokenizer tokenizer = new StringTokenizer( string, ".- " );
			while( tokenizer.hasMoreTokens() ) {
				elements.add( tokenizer.nextToken() );
			}

			// Set the original version string. 
			version.original = string;

			// Check each element for use.
			for( String element : elements ) {
				checkElement( version, element );
			}
		}

		if( !TextUtil.isEmpty( timestamp ) ) {
			try {
				version.date = PARSE_FORMAT.parse( timestamp );
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
		return major == INVALID ? 0 : major;
	}

	public int getMinor() {
		return minor == INVALID ? 0 : minor;
	}

	public int getMicro() {
		return micro == INVALID ? 0 : micro;
	}

	public String getState() {
		return state;
	}

	public int getBuild() {
		return build == INVALID ? 0 : build;
	}

	public boolean isSnapshot() {
		return SNAPSHOT.equalsIgnoreCase( state );
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
			buffer.append( UNKNOWN );
		} else {
			buffer.append( major );
			if( minor != INVALID ) {
				buffer.append( "." );
				buffer.append( minor );
				if( micro != INVALID ) {
					buffer.append( "." );
					buffer.append( micro );
				}
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
		if( unknown ) return UNKNOWN;
		return original;
	}

	public final String getCodedVersion() {
		StringBuffer buffer = new StringBuffer();

		if( unknown ) {
			buffer.append( UNKNOWN );
		} else {
			buffer.append( major );
			if( minor != INVALID ) {
				buffer.append( "-" );
				buffer.append( minor );
				if( micro != INVALID ) {
					buffer.append( "-" );
					buffer.append( micro );
				}
			}
			if( state != null ) {
				buffer.append( "-" );
				buffer.append( state );
				if( build != INVALID ) {
					buffer.append( "-" );
					buffer.append( build );
				}
			}
		}

		return buffer.toString();
	}

	public final String getDateString() {
		return date == null ? UNKNOWN : PRINT_FORMAT.format( date );
	}

	@Override
	public int compareTo( Version that ) {
		if( this.unknown != that.unknown ) return this.unknown ? -1 : 1;
		if( !this.unknown ) {
			if( this.major != INVALID && this.major != that.major ) return ObjectUtil.compare( this.major, that.major );
			if( this.minor != INVALID && this.minor != that.minor ) return ObjectUtil.compare( this.minor, that.minor );
			if( this.micro != INVALID && this.micro != that.micro ) return ObjectUtil.compare( this.micro, that.micro );
			if( this.state == null && that.state != null ) return 1;
			if( this.state != null && that.state == null ) return -1;
			if( this.state != null ) {
				if( !TextUtil.areEqual( this.state, that.state ) ) return ObjectUtil.compare( this.state, that.state );
				if( this.build != that.build ) return ObjectUtil.compare( this.build, that.build );
			}
		}

		System.out.println( "This: " + this.date + "  that: " + that.date );
		if( this.date != null ) {
			if( ObjectUtil.compare( this.date, that.date ) != 0 ) return ObjectUtil.compare( this.date, that.date );
		}

		return 0;
	}

	@Override
	public boolean equals( Object object ) {
		if( !( object instanceof Version ) ) return false;
		Version that = (Version)object;
		return this.major == that.major && this.minor == that.minor && this.micro == that.micro && TextUtil.areEqual( this.state, that.state ) && this.build == that.build;
	}

	@Override
	public String toString() {
		return getVersion();
	}

	private static final void checkElement( Version version, String element ) {
		if( TextUtil.isInteger( element ) ) {
			if( version.state == null ) {
				if( version.major == INVALID ) {
					version.major = Integer.parseInt( element );
				} else if( version.minor == INVALID ) {
					version.minor = Integer.parseInt( element );
				} else if( version.micro == INVALID ) {
					version.micro = Integer.parseInt( element );
				}
			} else if( version.build == INVALID ) {
				version.build = Integer.parseInt( element );
			}
		} else {
			if( version.state != null ) throw new RuntimeException( "State should only be declared once." );
			version.state = element;
		}
	}

}
