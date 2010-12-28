package com.parallelsymmetry.escape.utility.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class DefaultFormatter extends Formatter {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat( Log.DEFAULT_DATE_FORMAT );

	@Override
	public String format( LogRecord record ) {
		if( record == null ) return null;

		Throwable thrown = record.getThrown();
		StringBuilder buffer = new StringBuilder();

		if( record.getMessage() != null ) {
			if( Log.isShowColor() ) {
				buffer.append( getColorPrefix( record.getLevel() ) );
			}
			if( Log.isShowDate() ) {
				buffer.append( DATE_FORMAT.format( new Date( record.getMillis() ) ) );
				buffer.append( " " );
			}
			if( Log.isShowPrefix() ) {
				buffer.append( getPrefix( record.getLevel() ) );
			}
			buffer.append( record.getMessage() );
			if( Log.isShowColor() ) {
				buffer.append( getColorSuffix( record.getLevel() ) );
			}
			buffer.append( "\n" );
		}

		if( thrown != null ) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter( stringWriter );
			thrown.printStackTrace( printWriter );
			printWriter.close();
			buffer.append( stringWriter.toString() );
		}

		return buffer.toString();
	}

	protected static final String getPrefix( Level level ) {
		int index = level.intValue() / 100;

		if( index > 11 ) index = 11;
		if( index < 0 ) index = 0;

		switch( index ) {
			case 11: {
				return "";
			}
				// ERROR
			case 10: {
				return "*";
			}
				// WARN
			case 9: {
				return "-";
			}
				// INFO
			case 8: {
				return " ";
			}
				// TRACE
			case 7: {
				return "  ";
			}
				// DEBUG
			case 6: {
				return "   ";
			}
		}

		return "    ";
	}

	/**
	 * Add ANSI color tags to the specified message for the specified level.
	 */
	protected static final String getColorPrefix( Level level ) {
		int index = level.intValue() / 100;

		if( index > 11 ) index = 11;
		if( index < 0 ) index = 0;

		switch( index ) {
			// ERROR
			case 10: {
				return "\u001b[31m";
			}
				// WARN
			case 9: {
				return "\u001b[33m";
			}
				// INFO
			case 8: {
				return "\u001b[37m";
			}
				// TRACE
			case 7: {
				return "\u001b[1m\u001b[30m";
			}
				// DEBUG
			case 6: {
				return "\u001b[34m";
			}
		}

		return "";
	}

	protected static final String getColorSuffix( Level level ) {
		int index = level.intValue() / 100;

		if( index > 11 ) index = 11;
		if( index < 0 ) index = 0;

		return ( index > 5 && index < 11 ) ? "\u001b[0m" : "";
	}

}
