package com.parallelsymmetry.escape.utility.log;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class TestLogHandler extends Handler {

	private LogRecord record;

	public TestLogHandler() {
		setLevel( Log.ALL );
	}

	public synchronized void reset() {
		record = null;
	}

	public synchronized LogRecord getLogRecord() {
		while( record == null ) {
			try {
				this.wait( 50 );
				return null;
			} catch( InterruptedException exception ) {
				return null;
			}
		}
		LogRecord record = this.record;
		reset();
		return record;
	}

	@Override
	public synchronized void publish( LogRecord record ) {
		if( record.getLevel().intValue() < getLevel().intValue() || getLevel().intValue() == Log.NONE.intValue() ) return;
		this.record = record;
		this.notifyAll();
	}

	@Override
	public void flush() {}

	@Override
	public void close() throws SecurityException {}

}
