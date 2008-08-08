package com.parallelsymmetry.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class Version {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss Z" );

	private int major;

	private int minor;

	private int state;

	private int micro;

	private int build;

	private Date date;

	public static final Version parse( String string ) {
		if( TextUtil.isEmpty( string ) ) return null;

		StringTokenizer tokenizer = new StringTokenizer( string, "" );
		Version version = new Version();
		try {
			version.major = Integer.parseInt( tokenizer.nextToken( "-" ) );
			version.minor = Integer.parseInt( tokenizer.nextToken( "-" ) );
			version.state = ( (int)tokenizer.nextToken( "-" ).charAt( 0 ) ) - 65;
			version.micro = Integer.parseInt( tokenizer.nextToken( "-" ) );
			version.build = Integer.parseInt( tokenizer.nextToken( "- " ) );

			if( tokenizer.hasMoreTokens() ) {
				String date = tokenizer.nextToken( " " );
				String time = tokenizer.nextToken( " " );
				String zone = tokenizer.nextToken( " " );

				version.date = DATE_FORMAT.parse( date + " " + time + " " + zone );
			}
		} catch( Exception exception ) {
			throw new RuntimeException( "Exception parsing version string: " + string, exception );
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

	public int getState() {
		return state;
	}

	public int getBuild() {
		return build;
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

		buffer.append( major );
		buffer.append( "." );
		buffer.append( minor );

		return buffer.toString();
	}

	/**
	 * Get the version information.
	 * 
	 * @return A string representation of the version.
	 */
	public final String getFullVersion() {
		StringBuffer buffer = new StringBuffer();

		buffer.append( major );
		buffer.append( "." );
		buffer.append( minor );
		buffer.append( " " );
		buffer.append( getStateDescription() );
		buffer.append( " " );
		buffer.append( micro );

		return buffer.toString();
	}

	/**
	 * Get the version information.
	 * 
	 * @return A string representation of the version.
	 */
	public final String getBuildVersion() {
		StringBuffer buffer = new StringBuffer();

		buffer.append( major );
		buffer.append( "." );
		buffer.append( minor );
		buffer.append( " " );
		buffer.append( getStateDescription() );
		buffer.append( " " );
		buffer.append( micro );
		buffer.append( " Build " );
		buffer.append( build );

		return buffer.toString();
	}

	public final String getCodedVersion() {
		StringBuffer buffer = new StringBuffer();

		buffer.append( major );
		buffer.append( "-" );
		buffer.append( minor );
		buffer.append( "-" );
		buffer.append( (char)( state + 65 ) );
		buffer.append( "-" );
		buffer.append( micro );
		buffer.append( "-" );
		buffer.append( build );
		buffer.append( " " );
		buffer.append( getDateString() );

		return buffer.toString();
	}

	public final String getDateString() {
		return DATE_FORMAT.format( date );
	}

	/**
	 * Get the state as a string.
	 * 
	 * @return The state as a string.
	 */
	private final String getStateDescription() {
		switch( state ) {
			case 0:
				return "Alpha";
			case 1:
				return "Beta";
			default:
				return "Update";
		}
	}

}
