package com.parallelsymmetry.log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * This class is somewhat dangerous because it hides any exceptions while
 * publishing. It is strong recommended that this class not be used for any
 * streams that come from network sockets.
 * 
 * @author mvsoder
 */
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
			output.flush();
		} catch( IOException exception ) {
			exception.printStackTrace();
			//Log.removeHandler( this );
		}
	}

	@Override
	public void flush() {
		try {
			output.flush();
		} catch( Exception exception ) {
			exception.printStackTrace();
			//Log.removeHandler( this );
		}
	}

	@Override
	public void close() throws SecurityException {
		try {
			output.close();
		} catch( Exception exception ) {
			exception.printStackTrace();
			//Log.removeHandler( this );
		}
	}

}
