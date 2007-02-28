package org.novaworx.util;

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class FlushingStreamHandler extends StreamHandler {

	public FlushingStreamHandler() {
		super();
	}

	public FlushingStreamHandler( OutputStream out, Formatter formatter ) {
		super( out, formatter );
	}

	@Override
	public void publish( LogRecord record ) {
		super.publish( record );
		flush();
	}

}
