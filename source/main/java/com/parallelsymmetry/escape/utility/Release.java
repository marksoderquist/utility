package com.parallelsymmetry.escape.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Represents a software release which is a version and a date.
 * 
 * @author Mark Soderquist
 */
public class Release {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

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

	public Version getVersion() {
		return version;
	}

	public Date getDate() {
		return date;
	}

	public String toHumanString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat( DATE_FORMAT );
		dateFormat.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

		StringBuffer buffer = new StringBuffer();

		buffer.append( version.toHumanString() );
		if( date != null ) {
			buffer.append( "  " );
			buffer.append( dateFormat.format( date ) );
		}

		return buffer.toString();
	}

}
