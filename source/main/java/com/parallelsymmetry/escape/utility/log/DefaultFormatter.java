package com.parallelsymmetry.escape.utility.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
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
				buffer.append( Log.getColorPrefix( record.getLevel() ) );
			}
			if( Log.isShowDate() ) {
				buffer.append( DATE_FORMAT.format( new Date( record.getMillis() ) ) );
				buffer.append( " " );
			}
			if( Log.isShowPrefix() ) {
				buffer.append( Log.getPrefix( record.getLevel() ) );
			}
			buffer.append( record.getMessage() );
			if( Log.isShowColor() ) {
				buffer.append( Log.getColorSuffix( record.getLevel() ) );
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

}
