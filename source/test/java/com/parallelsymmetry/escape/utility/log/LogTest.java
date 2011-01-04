package com.parallelsymmetry.escape.utility.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import junit.framework.TestCase;

public class LogTest extends TestCase {

	private MockLogHandler handler;

	@Override
	public void setUp() {
		// Must set log level because it is in an unknown state from previous tests.
		Log.setLevel( Log.INFO );
		Log.removeHandler( Log.DEFAULT_HANDLER );
		handler = new MockLogHandler();
		Log.addHandler( handler );
	}

	@Override
	public void tearDown() {
		Log.removeHandler( handler );
		Log.setLevel( null );
	}

	public void testSetLevel() {
		Level level = Log.getLevel();

		Log.setLevel( Log.DEBUG );
		assertEquals( Log.DEBUG, Log.getLevel() );

		Log.setLevel( Log.TRACE );
		assertEquals( Log.TRACE, Log.getLevel() );

		Log.setLevel( Log.INFO );
		assertEquals( Log.INFO, Log.getLevel() );

		Log.setLevel( Log.WARN );
		assertEquals( Log.WARN, Log.getLevel() );

		Log.setLevel( Log.ERROR );
		assertEquals( Log.ERROR, Log.getLevel() );

		Log.setLevel( null );
		assertEquals( Log.ERROR, Log.getLevel() );

		Log.setLevel( level );
	}

	public void testWrite() {
		Log.write();
		LogRecord record = handler.getLogRecord();
		assertNotNull( "Log record is null.", record );
		assertEquals( "Incorrect log level.", Log.INFO, record.getLevel() );
		assertEquals( "Incorrect log message.", "", record.getMessage() );
	}

	public void testWriteWithColor() {
		String message = "Should be surrounded by color tags.";

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream( output );

		Handler oldHandler = Log.getDefaultHandler();
		Handler newHandler = new DefaultHandler( stream );

		try {
			Log.setShowColor( true );
			Log.setDefaultHandler( newHandler );

			Log.write( message );
			LogRecord record = handler.getLogRecord();
			assertNotNull( "Log record is null.", record );
			assertEquals( "Incorrect log level.", Log.INFO, record.getLevel() );
			assertEquals( "Incorrect log message.", "\u001b[0m" + message + "\u001b[0m\n", output.toString() );
		} finally {
			Log.setShowColor( false );
			Log.setDefaultHandler( oldHandler );
		}
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
		assertNotNull( "Log record should not be null.", record );

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

	public void testWriteWithChangingLevel() {
		Log.setDefaultHandler( Log.DEFAULT_LOGGER_NAME, handler );

		Log.write( Log.DEBUG, "debug" );
		assertNull( handler.getLogRecord() );

		Log.write( "info" );
		LogRecord record = handler.getLogRecord();
		assertEquals( Log.INFO, record.getLevel() );
		assertEquals( "info", record.getMessage() );
	}

	public void testWriteToLogger() {
		String name = "testWriteToLogger";

		Log.setDefaultHandler( name, handler );
		Log.writeTo( name );

		LogRecord record = handler.getLogRecord();
		assertNotNull( "Log record is null.", record );
		assertEquals( "Incorrect log level.", Log.INFO, record.getLevel() );
		assertEquals( "Incorrect log message.", "", record.getMessage() );
	}

	public void testWriteToLoggerUsingChangingLevel() {
		String name = "testWriteToLoggerUsingChangingLevel";

		Log.setDefaultHandler( name, handler );
		Log.setLevel( name, Log.NONE );
		Log.writeTo( name, "1" );
		Log.setLevel( name, Log.TRACE );
		Log.writeTo( name, Log.TRACE, "2" );
		Log.setLevel( name, Log.NONE );
		Log.writeTo( name, "3" );

		LogRecord record = handler.getLogRecord();
		assertNotNull( "Log record is null.", record );
		assertEquals( "Incorrect log message.", "2", record.getMessage() );
		assertEquals( "Incorrect log level.", Log.TRACE, record.getLevel() );
	}

	public void testParseLevel() {
		assertEquals( "Incorrect log level.", null, Log.parseLevel( null ) );
		assertEquals( "Incorrect log level.", null, Log.parseLevel( "" ) );
		assertEquals( "Incorrect log level.", null, Log.parseLevel( "junk" ) );

		assertEquals( "Incorrect log level.", Log.NONE, Log.parseLevel( "none" ) );
		assertEquals( "Incorrect log level.", Log.NONE, Log.parseLevel( "NonE" ) );
		assertEquals( "Incorrect log level.", Log.NONE, Log.parseLevel( "NONE" ) );

		assertEquals( "Incorrect log level.", Log.ERROR, Log.parseLevel( "error" ) );
		assertEquals( "Incorrect log level.", Log.WARN, Log.parseLevel( "warn" ) );
		assertEquals( "Incorrect log level.", Log.INFO, Log.parseLevel( "info" ) );
		assertEquals( "Incorrect log level.", Log.TRACE, Log.parseLevel( "trace" ) );
		assertEquals( "Incorrect log level.", Log.DEBUG, Log.parseLevel( "debug" ) );
		assertEquals( "Incorrect log level.", Log.ALL, Log.parseLevel( "all" ) );
	}

}
