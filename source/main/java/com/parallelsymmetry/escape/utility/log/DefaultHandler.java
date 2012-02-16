package com.parallelsymmetry.escape.utility.log;

import java.io.OutputStream;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class DefaultHandler extends StreamHandler {

	private OutputStream stream;

	public DefaultHandler( OutputStream stream ) {
		super( stream, new DefaultFormatter() );
		this.stream = stream;
	}

	@Override
	public void publish( LogRecord record ) {
		super.publish( record );
		super.flush();
	}

	@Override
	public synchronized void close() throws SecurityException {
		if( stream == System.out || stream == System.err ) {
			// If the stream is one of the console streams just flush, don't close.
			super.flush();
		} else {
			super.close();
		}
	}

}
