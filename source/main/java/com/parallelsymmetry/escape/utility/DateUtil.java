package com.parallelsymmetry.escape.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone( "UTC" );

	/**
	 * Convenience method to get the current year for the UTC time zone.
	 * 
	 * @return
	 */
	public static final int getCurrentYear() {
		return getCurrentYear( DEFAULT_TIME_ZONE );
	}

	/**
	 * Convenience method to get the current year based on time zone.
	 * 
	 * @param timeZone
	 * @return
	 */
	public static final int getCurrentYear( String timeZone ) {
		return getCurrentYear( TimeZone.getTimeZone( timeZone ) );
	}

	/**
	 * Convenience method to get the current year based on time zone.
	 * 
	 * @param timezone
	 * @return
	 */
	public static final int getCurrentYear( TimeZone timeZone ) {
		return Calendar.getInstance( timeZone ).get( Calendar.YEAR );
	}

	/**
	 * Parse a date string with the given format using the standard time zone.
	 * 
	 * @param format
	 * @param data
	 * @return
	 */
	public static final Date parse( String data, String format ) {
		return parse( data, format, DEFAULT_TIME_ZONE );
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
		return parse( data, format, timeZone == null ? null : TimeZone.getTimeZone( timeZone ) );
	}

	/**
	 * Parse a date string with the given format and time zone.
	 * 
	 * @param data
	 * @param format
	 * @param timeZone
	 * @return
	 */
	public static final Date parse( String data, String format, TimeZone timeZone ) {
		if( data == null ) return null;

		SimpleDateFormat formatter = new SimpleDateFormat( format );
		formatter.setTimeZone( timeZone );

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
		return format( date, format, DEFAULT_TIME_ZONE );
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
		return format( date, format, timeZone );
	}

	/**
	 * Format a date with the given format and time zone.
	 * 
	 * @param date
	 * @param format
	 * @param timeZone
	 * @return
	 */
	public static final String format( Date date, String format, TimeZone timeZone ) {
		SimpleDateFormat formatter = new SimpleDateFormat( format );
		formatter.setTimeZone( timeZone );
		return formatter.format( date );
	}

}
