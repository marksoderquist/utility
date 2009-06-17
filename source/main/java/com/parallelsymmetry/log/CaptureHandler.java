package com.parallelsymmetry.log;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

import com.parallelsymmetry.util.Log;

public class CaptureHandler extends StreamHandler {

	private OutputStream output;

	public CaptureHandler() {
		super();
		try {
			setEncoding( "UTF-8" );
		} catch( Exception exception ) {
			exception.printStackTrace();
		}
		output = new ByteArrayOutputStream();
		setOutputStream( output );
		setFormatter( new CaptureFormatter() );
	}

	@Override
	public void publish( LogRecord record ) {
		// Don't capture trace and debug messages.
		if( record.getLevel().intValue() >= Log.INFO.intValue() || record.getLevel().intValue() <= Log.NONE.intValue() ) {
			super.publish( record );
			flush();
		}
	}

	public String toString() {
		return output.toString();
	}

}
