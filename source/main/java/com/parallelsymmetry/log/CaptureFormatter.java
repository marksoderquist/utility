/**
 * 
 */
package com.parallelsymmetry.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CaptureFormatter extends Formatter {

	@Override
	public String format( LogRecord record ) {
		Throwable thrown = record.getThrown();
		StringBuffer buffer = new StringBuffer();

		if( record.getMessage() != null ) {
			buffer.append( record.getMessage() );
		}
		buffer.append( "\n" );

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
