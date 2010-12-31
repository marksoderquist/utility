package com.parallelsymmetry.escape.utility.log;

import java.io.OutputStream;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class DefaultHandler extends StreamHandler {

	public DefaultHandler( OutputStream stream ) {
		super( stream, new DefaultFormatter() );
	}

	@Override
	public void publish( LogRecord record ) {
		super.publish( record );
		super.flush();
	}

}
