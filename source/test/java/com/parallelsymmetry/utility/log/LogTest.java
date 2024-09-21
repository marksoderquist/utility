package com.parallelsymmetry.utility.log;

import com.parallelsymmetry.utility.BaseTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.*;

public class LogTest extends BaseTestCase {

	private MockLogHandler handler;

	@BeforeEach
	@Override
	public void setup() throws Exception {
		super.setup();
		// Must set log level because it is in an unknown state from previous tests.
		Log.setLevel( Log.INFO );
		Log.removeHandler( Log.getDefaultHandler() );
		handler = new MockLogHandler();
		Log.addHandler( handler );
	}

	@AfterEach
	@Override
	public void teardown() throws Exception {
		Log.removeHandler( handler );
		super.teardown();
	}

	@Test
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

	@Test
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

	@Test
	public void testWrite() {
		Log.write();
		LogRecord record = handler.getLogRecord();
		assertNotNull( record );
		assertEquals( Log.INFO, record.getLevel() );
		assertEquals( "", record.getMessage() );
	}

	@Test
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
			assertNotNull( record );
			assertEquals( Log.INFO, record.getLevel() );
			assertEquals( "\u001b[37m[I] " + message + "\u001b[0m\n", output.toString() );
		} finally {
			Log.setShowColor( false );
			Log.setDefaultHandler( oldHandler );
		}
	}

	@Test
	public void testWriteWithString() {
		LogRecord record;

		Log.write( "Test" );
		record = handler.getLogRecord();
		assertNotNull( record );
		assertEquals( Log.INFO, record.getLevel() );
		assertEquals( "Test", record.getMessage() );
	}

	@Test
	public void testWriteWithObjectListAndNullValue() {
		LogRecord record;

		Log.write( "Test: ", null );
		record = handler.getLogRecord();
		assertNotNull( record );
		assertEquals( Log.INFO, record.getLevel() );
		assertEquals( "Test: null", record.getMessage() );
	}

	@Test
	public void testWriteWithLevelAndString() {
		LogRecord record;

		Log.write( Log.ALL, "Test 1" );
		record = handler.getLogRecord();
		assertNotNull( record );

		Log.write( Log.NONE, "Test 2" );
		record = handler.getLogRecord();
		assertNotNull( record );
		assertEquals( Log.NONE, record.getLevel() );
		assertEquals( "Test 2", record.getMessage() );
	}

	@Test
	public void testWriteWithThrowable() {
		LogRecord record;

		Throwable throwable = new Exception( "Test" );
		Log.write( throwable );
		record = handler.getLogRecord();
		assertNotNull( record );
		assertEquals( Log.ERROR, record.getLevel() );
		assertEquals( throwable, record.getThrown() );
	}

	@Test
	public void testWriteWithStringAndThrowable() {
		LogRecord record;

		Throwable throwable = new Exception( "Test" );
		Log.write( throwable, "Test" );
		record = handler.getLogRecord();
		assertNotNull( record );
		assertEquals( Log.ERROR, record.getLevel() );
		assertEquals( "Test", record.getMessage() );
		assertEquals( throwable, record.getThrown() );
	}

	@Test
	public void testWriteWithChangingLevel() {
		Log.setDefaultHandler( Log.DEFAULT_LOGGER_NAME, handler );

		Log.write( Log.DEBUG, "debug" );
		assertNull( handler.getLogRecord() );

		Log.write( "info" );
		LogRecord record = handler.getLogRecord();
		assertEquals( Log.INFO, record.getLevel() );
		assertEquals( "info", record.getMessage() );
	}

	@Test
	public void testWriteToLogger() {
		String name = "testWriteToLogger";

		Log.setDefaultHandler( name, handler );
		Log.writeTo( name );

		LogRecord record = handler.getLogRecord();
		assertNotNull( record );
		assertEquals( Log.INFO, record.getLevel() );
		assertEquals( "", record.getMessage() );
	}

	@Test
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
		assertNotNull( record );
		assertEquals( "2", record.getMessage() );
		assertEquals( Log.TRACE, record.getLevel() );
	}

	@Test
	public void testGetLevels() {
		SortedSet<? extends Level> levels = Log.getLevels();
		assertEquals( 10, levels.size() );

		Iterator<? extends Level> iterator = levels.iterator();
		assertEquals( Log.ALL, iterator.next() );
		assertEquals( Log.DETAIL, iterator.next() );
		assertEquals( Log.DEBUG, iterator.next() );
		assertEquals( Log.TRACE, iterator.next() );
		assertEquals( Log.INFO, iterator.next() );
		assertEquals( Log.WARN, iterator.next() );
		assertEquals( Log.ERROR, iterator.next() );
		assertEquals( Log.DEVEL, iterator.next() );
		assertEquals( Log.HELP, iterator.next() );
		assertEquals( Log.NONE, iterator.next() );
	}

	@Test
	public void testParseLevelWithInt() {
		assertEquals( Log.ALL, Log.parseLevel( 0 ) );

		SortedSet<? extends Level> levels = Log.getLevels();
		for( Level level : levels ) {
			assertEquals( level, Log.parseLevel( level.intValue() ) );
		}
	}

	@Test
	public void testParseLevelWithString() {
		assertNull( Log.parseLevel( null ) );
		assertNull( Log.parseLevel( "" ) );
		assertNull( Log.parseLevel( "junk" ) );

		SortedSet<? extends Level> levels = Log.getLevels();
		for( Level level : levels ) {
			assertEquals( level, Log.parseLevel( level.getName().toLowerCase() ) );
			assertEquals( level, Log.parseLevel( level.getName().toUpperCase() ) );
		}
	}

	@Test
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
