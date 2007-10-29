package com.parallelsymmetry.util.log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class FlushingObjectHandler extends Handler {

	private ObjectOutputStream output;

	public FlushingObjectHandler( OutputStream out ) throws IOException {
		this.output = new ObjectOutputStream( out );
	}

	@Override
	public void publish( LogRecord record ) {
		if( record.getLevel().intValue() < getLevel().intValue() ) return;

		try {
			output.writeObject( record );
		} catch( Exception e ) {
			// reportError( record.getMessage(), e, ErrorManager.WRITE_FAILURE );
		}
		flush();
	}

	@Override
	public void flush() {
		try {
			output.flush();
		} catch( Exception e ) {
			// reportError( e.getMessage(), e, ErrorManager.FLUSH_FAILURE );
		}
	}

	@Override
	public void close() throws SecurityException {
		try {
			output.close();
		} catch( Exception e ) {
			// reportError( e.getMessage(), e, ErrorManager.CLOSE_FAILURE );
		}
	}

}