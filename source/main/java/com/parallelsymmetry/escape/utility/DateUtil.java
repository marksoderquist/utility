package com.parallelsymmetry.escape.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

	public static final String STANDARD_TIME_ZONE = "GMT";

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * Convenience method to get the current year for the UTC time zone.
	 * 
	 * @return
	 */
	public static final int getCurrentYear() {
		return getCurrentYear( STANDARD_TIME_ZONE );
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
	 * Parse a date string with the given format using the standard time zone.
	 * 
	 * @param format
	 * @param data
	 * @return
	 */
	public static final Date parse( String data, String format ) {
		return parse( data, format, STANDARD_TIME_ZONE );
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

	/**
	 * Format a date with the given format using the standard time zone.
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public static final String format( Date date, String format ) {
		return format( date, format, STANDARD_TIME_ZONE );
	}

	/**
	 * Format a date with the given format and time zone.
	 * 
	 * @param date
	 * @param format
	 * @param timeZone
	 * @return
	 */
	public static final String format( Date date, String format, String timeZone ) {
		SimpleDateFormat formatter = new SimpleDateFormat( format );
		formatter.setTimeZone( TimeZone.getTimeZone( timeZone ) );
		return formatter.format( date );
	}

}
