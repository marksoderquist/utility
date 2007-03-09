package org.novaworx.util;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import junit.framework.TestCase;

public class LogTest extends TestCase {

	private TestLogHandler handler;

	@Override
	public void setUp() {
		Log.setLevel( Log.NONE );
		handler = new TestLogHandler();
		Log.addHandler( handler );
	}

	@Override
	public void tearDown() {
		Log.removeHandler( handler );
		Log.setLevel( null );
	}

	public void testWrite() {
		Log.write();
		assertEquals( "Incorrect log message.", "", handler.getLogRecord().getMessage() );
	}

	public void testWriteWithString() {
		LogRecord record = null;

		Log.write( "Test" );
		record = handler.getLogRecord();
		assertEquals( "Incorrect log level.", Log.INFO, record.getLevel() );
		assertEquals( "Incorrect log message.", "Test", record.getMessage() );
	}

	public void testWriteWithLevelAndString() {
		LogRecord record = null;

		Log.write( Log.ALL, "Test 1" );
		record = handler.getLogRecord();
		assertEquals( "Incorrect log level.", Log.ALL, record.getLevel() );
		assertEquals( "Incorrect log message.", "Test 1", record.getMessage() );

		Log.write( Log.NONE, "Test 2" );
		record = handler.getLogRecord();
		assertEquals( "Incorrect log level.", Log.NONE, record.getLevel() );
		assertEquals( "Incorrect log message.", "Test 2", record.getMessage() );
	}

	public void testWriteWithThrowable() {
		LogRecord record = null;

		Throwable throwable = new Exception( "Test" );
		Log.write( throwable );
		record = handler.getLogRecord();
		assertEquals( "Incorrect log level.", Log.ERROR, record.getLevel() );
		assertEquals( "Incorrect log throwable.", throwable, record.getThrown() );
	}

	public void testWriteWithStringAndThrowable() {
		LogRecord record = null;

		Throwable throwable = new Exception( "Test" );
		Log.write( "Test", throwable );
		record = handler.getLogRecord();
		assertEquals( "Incorrect log level.", Log.ERROR, record.getLevel() );
		assertEquals( "Incorrect log message.", "Test", record.getMessage() );
		assertEquals( "Incorrect log throwable.", throwable, record.getThrown() );
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
		assertEquals( "Incorrect log level.", Log.CONFIG, Log.parseLevel( "config" ) );
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
			this.record = record;
			this.notifyAll();
		}

		@Override
		public void flush() {}

		@Override
		public void close() throws SecurityException {}

	}

}
