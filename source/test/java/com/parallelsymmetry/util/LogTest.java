package com.parallelsymmetry.util;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.parallelsymmetry.log.Log;

import junit.framework.TestCase;

public class LogTest extends TestCase {

	private TestLogHandler handler;

	@Override
	public void setUp() {
		// Must set log level because it is in an unknown state from previous tests.
		Log.setLevel( Level.INFO );
		Log.removeHandler( Log.DEFAULT_HANDLER );
		handler = new TestLogHandler();
		handler.setLevel( Level.ALL );
		Log.addHandler( handler );
	}

	@Override
	public void tearDown() {
		Log.removeHandler( handler );
		Log.setLevel( null );
	}

	public void testWrite() {
		Log.write();
		LogRecord record = handler.getLogRecord();
		assertNotNull( "Log record is null.", record );
		assertEquals( "Incorrect log level.", Log.INFO, record.getLevel() );
		assertEquals( "Incorrect log message.", "", record.getMessage() );
	}

	public void testWriteWithString() {
		LogRecord record = null;

		Log.write( "Test" );
		record = handler.getLogRecord();
		assertNotNull( "Log record is null.", record );
		assertEquals( "Incorrect log level.", Log.INFO, record.getLevel() );
		assertEquals( "Incorrect log message.", "Test", record.getMessage() );
	}

	public void testWriteWithLevelAndString() {
		LogRecord record = null;

		Log.write( Log.ALL, "Test 1" );
		record = handler.getLogRecord();
		assertNull( "Log record is null.", record );

		Log.write( Log.NONE, "Test 2" );
		record = handler.getLogRecord();
		assertNotNull( "Log record is null.", record );
		assertEquals( "Incorrect log level.", Log.NONE, record.getLevel() );
		assertEquals( "Incorrect log message.", "Test 2", record.getMessage() );
	}

	public void testWriteWithThrowable() {
		LogRecord record = null;

		Throwable throwable = new Exception( "Test" );
		Log.write( throwable );
		record = handler.getLogRecord();
		assertNotNull( "Log record is null.", record );
		assertEquals( "Incorrect log level.", Log.ERROR, record.getLevel() );
		assertEquals( "Incorrect log throwable.", throwable, record.getThrown() );
	}

	public void testWriteWithStringAndThrowable() {
		LogRecord record = null;

		Throwable throwable = new Exception( "Test" );
		Log.write( throwable, "Test" );
		record = handler.getLogRecord();
		assertNotNull( "Log record is null.", record );
		assertEquals( "Incorrect log level.", Log.ERROR, record.getLevel() );
		assertEquals( "Incorrect log message.", "Test", record.getMessage() );
		assertEquals( "Incorrect log throwable.", throwable, record.getThrown() );
	}

	public void testWriteToLogger() {
		String name = "testWriteToLogger";

		Log.setDefaultHandler( name, handler );
		Log.writeToLogger( name );

		LogRecord record = handler.getLogRecord();
		assertNotNull( "Log record is null.", record );
		assertEquals( "Incorrect log level.", Log.INFO, record.getLevel() );
		assertEquals( "Incorrect log message.", "", record.getMessage() );
	}

	public void testWriteToLoggerUsingChangingLevel() {
		String name = "testWriteToLoggerUsingChangingLevel";

		Log.setDefaultHandler( name, handler );
		Log.setLevel( name, Log.NONE );
		Log.writeToLogger( name, "1" );
		Log.setLevel( name, Log.TRACE );
		Log.writeToLogger( name, Log.TRACE, "2" );
		Log.setLevel( name, Log.NONE );
		Log.writeToLogger( name, "3" );

		LogRecord record = handler.getLogRecord();
		assertNotNull( "Log record is null.", record );
		assertEquals( "Incorrect log message.", "2", record.getMessage() );
		assertEquals( "Incorrect log level.", Log.TRACE, record.getLevel() );
	}

	public void testParseLevel() {
		assertEquals( "Incorrect log level.", Log.INFO, Log.parseLevel( null ) );
		assertEquals( "Incorrect log level.", Log.INFO, Log.parseLevel( "" ) );
		assertEquals( "Incorrect log level.", Log.INFO, Log.parseLevel( "junk" ) );

		assertEquals( "Incorrect log level.", Log.NONE, Log.parseLevel( "none" ) );
		assertEquals( "Incorrect log level.", Log.NONE, Log.parseLevel( "None" ) );
		assertEquals( "Incorrect log level.", Log.NONE, Log.parseLevel( "NONE" ) );

		assertEquals( "Incorrect log level.", Log.ERROR, Log.parseLevel( "error" ) );
		assertEquals( "Incorrect log level.", Log.WARN, Log.parseLevel( "warn" ) );
		assertEquals( "Incorrect log level.", Log.INFO, Log.parseLevel( "info" ) );
		assertEquals( "Incorrect log level.", Log.TRACE, Log.parseLevel( "trace" ) );
		assertEquals( "Incorrect log level.", Log.DEBUG, Log.parseLevel( "debug" ) );
		assertEquals( "Incorrect log level.", Log.ALL, Log.parseLevel( "all" ) );
	}

	private class TestLogHandler extends Handler {

		private LogRecord record;

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
			this.record = null;
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

}
