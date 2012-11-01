package com.parallelsymmetry.escape.utility;

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

	public Release( String version ) {
		this( new Version( version ), null );
	}

	public Release( String version, Date timestamp ) {
		this( new Version( version ), timestamp );
	}

	public Release( Version version ) {
		this( version, null );
	}

	public Release( Version version, Date date ) {
		if( version == null ) throw new NullPointerException( "Version cannot be null." );
		this.version = version;
		this.date = date;
	}

	public Version getVersion() {
		return version;
	}

	public Date getDate() {
		return date;
	}

	public String getDateString() {
		return getDateString( DateUtil.DEFAULT_TIME_ZONE );
	}

	public String getDateString( TimeZone zone ) {
		if( date == null ) return "";

		StringBuilder builder = new StringBuilder( DateUtil.format( date, Release.DATE_FORMAT, zone ) );
		if( !zone.equals( DateUtil.DEFAULT_TIME_ZONE ) ) {
			builder.append( " " );
			builder.append( zone.getDisplayName( zone.inDaylightTime( date ), TimeZone.SHORT ) );
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return format( version.toString() );
	}

	public String toHumanString() {
		return format( version.toHumanString() );
	}

	public String toHumanString( TimeZone zone ) {
		return format( version.toHumanString(), zone );
	}

	@Override
	public boolean equals( Object object ) {
		if( !( object instanceof Release ) ) return false;

		Release that = (Release)object;
		return this.compareTo( that ) == 0;
	}

	@Override
	public int hashCode() {
		return version.hashCode() ^ ( date == null ? 0 : date.hashCode() );
	}

	@Override
	public int compareTo( Release that ) {
		int result = this.getVersion().compareTo( that.getVersion() );
		if( result != 0 ) return result;

		if( this.date == null || that.date == null ) return 0;
		return this.date.compareTo( that.date );
	}

	private String format( String version ) {
		return format( version, DateUtil.DEFAULT_TIME_ZONE );
	}

	private String format( String version, TimeZone zone ) {
		SimpleDateFormat dateFormat = new SimpleDateFormat( DATE_FORMAT );
		dateFormat.setTimeZone( zone );

		StringBuffer buffer = new StringBuffer();

		buffer.append( version );
		if( date != null ) {
			buffer.append( "  " );
			buffer.append( getDateString( zone ) );
		}

		return buffer.toString();
	}

}
