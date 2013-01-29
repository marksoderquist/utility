package com.parallelsymmetry.utility.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.parallelsymmetry.utility.log.DefaultHandler;
import com.parallelsymmetry.utility.log.Log;

import junit.framework.TestCase;

public class LogTest extends TestCase {

	private MockLogHandler handler;

	@Override
	public void setUp() {
		// Must set log level because it is in an unknown state from previous tests.
		Log.setLevel( Log.INFO );
		Log.removeHandler( Log.getDefaultHandler() );
		handler = new MockLogHandler();
		Log.addHandler( handler );
	}

	@Override
	public void tearDown() {
		Log.removeHandler( handler );
		Log.setLevel( null );
	}

	public void testIsActive() {
		Log.setLevel( Log.NONE );
		assertTrue( Log.isActive( Log.NONE ) );
		assertFalse( Log.isActive( Log.ERROR ) );
		assertFalse( Log.isActive( Log.DETAIL ) );
		assertFalse( Log.isActive( Log.ALL ) );

		Log.setLevel( Log.ERROR );
		assertTrue( Log.isActive( Log.NONE ) );
		assertTrue( Log.isActive( Log.ERROR ) );
		assertFalse( Log.isActive( Log.DETAIL ) );
		assertFalse( Log.isActive( Log.ALL ) );

		Log.setLevel( Log.DETAIL );
		assertTrue( Log.isActive( Log.NONE ) );
		assertTrue( Log.isActive( Log.ERROR ) );
		assertTrue( Log.isActive( Log.DETAIL ) );
		assertFalse( Log.isActive( Log.ALL ) );

		Log.setLevel( Log.ALL );
		assertTrue( Log.isActive( Log.NONE ) );
		assertTrue( Log.isActive( Log.ERROR ) );
		assertTrue( Log.isActive( Log.DETAIL ) );
		assertTrue( Log.isActive( Log.ALL ) );
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
			assertEquals( "Incorrect log message.", "\u001b[0m[I] " + message + "\u001b[0m\n", output.toString() );
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

	public void testWriteWithObjectListAndNullValue() {
		LogRecord record = null;

		Log.write( "Test: ", null );
		record = handler.getLogRecord();
		assertNotNull( "Log record is null.", record );
		assertEquals( "Incorrect log level.", Log.INFO, record.getLevel() );
		assertEquals( "Incorrect log message.", "Test: null", record.getMessage() );
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

	public void testGetLevels() {
		SortedSet<? extends Level> levels = Log.getLevels();
		assertEquals( 9, levels.size() );

		Iterator<? extends Level> iterator = levels.iterator();
		assertEquals( Log.ALL, iterator.next() );
		assertEquals( Log.DETAIL, iterator.next() );
		assertEquals( Log.DEBUG, iterator.next() );
		assertEquals( Log.TRACE, iterator.next() );
		assertEquals( Log.INFO, iterator.next() );
		assertEquals( Log.WARN, iterator.next() );
		assertEquals( Log.ERROR, iterator.next() );
		assertEquals( Log.HELP, iterator.next() );
		assertEquals( Log.NONE, iterator.next() );
	}

	public void testParseLevelWithInt() {
		assertEquals( Log.ALL, Log.parseLevel( 0 ) );

		SortedSet<? extends Level> levels = Log.getLevels();
		for( Level level : levels ) {
			assertEquals( level, Log.parseLevel( level.intValue() ) );
		}
	}

	public void testParseLevelWithString() {
		assertEquals( "Incorrect log level.", null, Log.parseLevel( null ) );
		assertEquals( "Incorrect log level.", null, Log.parseLevel( "" ) );
		assertEquals( "Incorrect log level.", null, Log.parseLevel( "junk" ) );

		SortedSet<? extends Level> levels = Log.getLevels();
		for( Level level : levels ) {
			assertEquals( level, Log.parseLevel( level.getName().toLowerCase() ) );
			assertEquals( level, Log.parseLevel( level.getName().toUpperCase() ) );
		}
	}

	public void testSetDefaultHandlerCheckLogLevel() {
		Handler handlerD = Log.getDefaultHandler();
		Handler handler1 = new MockLogHandler();
		Handler handler2 = new MockLogHandler();
		assertEquals( Log.INFO, handlerD.getLevel() );
		assertEquals( Log.ALL, handler1.getLevel() );
		assertEquals( Log.ALL, handler2.getLevel() );

		Log.setDefaultHandler( handler1 );
		assertEquals( Log.ALL, handlerD.getLevel() );
		assertEquals( Log.INFO, handler1.getLevel() );
		assertEquals( Log.ALL, handler2.getLevel() );

		Log.setDefaultHandler( handler2 );
		assertEquals( Log.ALL, handlerD.getLevel() );
		assertEquals( Log.ALL, handler1.getLevel() );
		assertEquals( Log.INFO, handler2.getLevel() );
	}

}
