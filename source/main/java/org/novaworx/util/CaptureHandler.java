package org.novaworx.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class CaptureHandler extends StreamHandler {

	private OutputStream output;

	public CaptureHandler() {
		super();
		output = new ByteArrayOutputStream();
		setOutputStream( output );
		setFormatter( new CaptureFormatter() );
	}

	@Override
	public void publish( LogRecord record ) {
		super.publish( record );
		flush();
	}

	public String toString() {
		return output.toString();
	}

	private static class CaptureFormatter extends Formatter {

		@Override
		public String format( LogRecord record ) {
			Throwable thrown = record.getThrown();
			StringBuffer buffer = new StringBuffer();

			if( record.getMessage() != null ) {
				buffer.append( record.getMessage() );
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

}
