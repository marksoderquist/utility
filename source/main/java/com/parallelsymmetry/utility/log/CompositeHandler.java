package com.parallelsymmetry.utility.log;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class CompositeHandler extends Handler {

	private Handler[] handlers;

	public CompositeHandler( Handler... handlers ) {
		this.handlers = handlers;
	}

	@Override
	public void publish( LogRecord record ) {
		if( !isLoggable( record ) ) return;

		for( Handler handler : handlers ) {
			handler.publish( record );
		}
	}

	@Override
	public void flush() {
		for( Handler handler : handlers ) {
			handler.flush();
		}
	}

	@Override
	public void close() throws SecurityException {
		SecurityException exception = null;

		for( Handler handler : handlers ) {
			try {
				handler.close();
			} catch( SecurityException closeException ) {
				if( exception != null ) exception = closeException;
			}
		}

		if( exception != null ) throw exception;
	}

	@Override
	public boolean isLoggable( LogRecord record ) {
		if( handlers == null || handlers.length == 0 ) return false;
		return super.isLoggable( record );
	}

}
