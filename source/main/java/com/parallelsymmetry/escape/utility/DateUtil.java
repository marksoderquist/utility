package com.parallelsymmetry.escape.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

	/**
	 * Convenience method to get the current year for the UTC time zone.
	 * 
	 * @return
	 */
	public static final int getCurrentYear() {
		return getCurrentYear( "UTC" );
	}

	/**
	 * Convenience method to get the current year based on time zone.
	 * 
	 * @param timezone
	 * @return
	 */
	public static final int getCurrentYear( String timezone ) {
		return Calendar.getInstance( TimeZone.getTimeZone( timezone ) ).get( Calendar.YEAR );
	}

	/**
	 * Parse a date string with the given format using the UTC time zone.
	 * 
	 * @param format
	 * @param data
	 * @return
	 */
	public static final Date parse( String data, String format ) {
		return parse( data, format, "GMT" );
	}

	/**
	 * Parse a date string with the given format and time zone.
	 * 
	 * @param data
	 * @param format
	 * @param timeZone
	 * @return
	 */
	public static final Date parse( String data, String format, String timeZone ) {
		if( data == null ) return null;

		SimpleDateFormat formatter = new SimpleDateFormat( format );
		formatter.setTimeZone( TimeZone.getTimeZone( timeZone ) );

		try {
			return formatter.parse( data );
		} catch( ParseException exception ) {
			return null;
		}
	}

}
