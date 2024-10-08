package com.parallelsymmetry.utility.log;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.servlet.ServletContext;

public class ServletContextHandler extends Handler {

	private ServletContext context;

	public ServletContextHandler( ServletContext context ) {
		this.context = context;
	}

	@Override
	public void publish( LogRecord record ) {
		if( !isLoggable( record ) ) return;

		if( record.getThrown() == null ) {
			context.log( record.getMessage() );
		} else {
			context.log( record.getMessage(), record.getThrown() );
		}
	}

	@Override
	public void flush() {
		// Intentionally do nothing.
	}

	@Override
	public void close() throws SecurityException {
		// Intentionally do nothing.
	}

	@Override
	public boolean isLoggable( LogRecord record ) {
		if( context == null ) return false;
		return super.isLoggable( record );
	}

}
