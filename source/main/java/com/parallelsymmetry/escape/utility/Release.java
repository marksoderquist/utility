package com.parallelsymmetry.escape.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Represents a software release which is a version and a date.
 * 
 * @author Mark Soderquist
 */
public class Release implements Comparable<Release> {

	/**
	 * All release dates are expected to be in UTC so no time zone is given in the
	 * date format.
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private Version version;

	private Date date;

	public Release( Version version ) {
		this( version, null );
	}

	public Release( Version version, Date date ) {
		if( version == null ) throw new NullPointerException( "Version cannot be null." );
		this.version = version;
		this.date = date;
	}

	public Release( String version ) {
		this( new Version( version ), null );
	}

	public Release( String version, Date timestamp ) {
		this( new Version( version ), timestamp );
	}

	public Release( String version, String timestamp ) {
		if( version == null ) throw new NullPointerException( "Version cannot be null." );
		this.version = new Version( version );

		DateFormat formatter = new SimpleDateFormat( DATE_FORMAT );
		formatter.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
		try {
			this.date = formatter.parse( timestamp );
		} catch( ParseException exception ) {

		}
	}

	public Version getVersion() {
		return version;
	}

	public Date getDate() {
		return date;
	}

	public String toString() {
		return format( version.toString() );
	}

	public String toHumanString() {
		return format( version.toHumanString() );
	}

	@Override
	public boolean equals( Object object ) {
		if( !( object instanceof Release ) ) return false;

		Release that = (Release)object;
		return this.compareTo( that ) == 0;
	}

	@Override
	public int compareTo( Release that ) {
		int result = this.getVersion().compareTo( that.getVersion() );
		if( result != 0 ) return result;

		if( this.date == null && that.date == null ) return 0;
		if( this.date == null && that.date != null ) return -1;
		if( this.date != null && that.date == null ) return 1;
		return this.date.compareTo( that.date );
	}

	private String format( String version ) {
		SimpleDateFormat dateFormat = new SimpleDateFormat( DATE_FORMAT );
		dateFormat.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

		StringBuffer buffer = new StringBuffer();

		buffer.append( version );
		if( date != null ) {
			buffer.append( "  " );
			buffer.append( dateFormat.format( date ) );
		}

		return buffer.toString();
	}

}
