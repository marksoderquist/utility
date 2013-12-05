package com.parallelsymmetry.utility.log;

import java.util.logging.LogRecord;

import junit.framework.TestCase;

public class DefaultFormatterTest extends TestCase {

	private DefaultFormatter formatter = new DefaultFormatter();

	@Override
	public void tearDown() {
		Log.setShowDate( false );
		Log.setShowColor( false );
		Log.setShowPrefix( false );
	}

	public void testFormatWithNull() {
		assertNull( null, formatter.format( null ) );
	}

	public void testFormatWithEmptyInfoRecord() {
		assertEquals( "[I] \n", formatter.format( new LogRecord( Log.INFO, "" ) ) );
	}

	public void testFormatWithNonEmptyRecord() {
		assertEquals( "[V] Test message.\n", formatter.format( new LogRecord( Log.DEVEL, "Test message." ) ) );
		assertEquals( "[E] Test message.\n", formatter.format( new LogRecord( Log.ERROR, "Test message." ) ) );
		assertEquals( "[W] Test message.\n", formatter.format( new LogRecord( Log.WARN, "Test message." ) ) );
		assertEquals( "[I] Test message.\n", formatter.format( new LogRecord( Log.INFO, "Test message." ) ) );
		assertEquals( "[T] Test message.\n", formatter.format( new LogRecord( Log.TRACE, "Test message." ) ) );
		assertEquals( "[D] Test message.\n", formatter.format( new LogRecord( Log.DEBUG, "Test message." ) ) );
		assertEquals( "[L] Test message.\n", formatter.format( new LogRecord( Log.DETAIL, "Test message." ) ) );
	}

	public void testFormatWithWhitespacedRecord() {
		assertEquals( "[V]   Test message.\n", formatter.format( new LogRecord( Log.DEVEL, "  Test message." ) ) );
		assertEquals( "[E]   Test message.\n", formatter.format( new LogRecord( Log.ERROR, "  Test message." ) ) );
		assertEquals( "[W]   Test message.\n", formatter.format( new LogRecord( Log.WARN, "  Test message." ) ) );
		assertEquals( "[I]   Test message.\n", formatter.format( new LogRecord( Log.INFO, "  Test message." ) ) );
		assertEquals( "[T]   Test message.\n", formatter.format( new LogRecord( Log.TRACE, "  Test message." ) ) );
		assertEquals( "[D]   Test message.\n", formatter.format( new LogRecord( Log.DEBUG, "  Test message." ) ) );
		assertEquals( "[L]   Test message.\n", formatter.format( new LogRecord( Log.DETAIL, "  Test message." ) ) );
	}

	public void testFormatWithMultiLineRecord() {
		assertEquals( "[V] Test message one.\n[V] Test message two.\n", formatter.format( new LogRecord( Log.DEVEL, "Test message one.\nTest message two." ) ) );
		assertEquals( "[E] Test message one.\n[E] Test message two.\n", formatter.format( new LogRecord( Log.ERROR, "Test message one.\nTest message two." ) ) );
		assertEquals( "[W] Test message one.\n[W] Test message two.\n", formatter.format( new LogRecord( Log.WARN, "Test message one.\nTest message two." ) ) );
		assertEquals( "[I] Test message one.\n[I] Test message two.\n", formatter.format( new LogRecord( Log.INFO, "Test message one.\nTest message two." ) ) );
		assertEquals( "[T] Test message one.\n[T] Test message two.\n", formatter.format( new LogRecord( Log.TRACE, "Test message one.\nTest message two." ) ) );
		assertEquals( "[D] Test message one.\n[D] Test message two.\n", formatter.format( new LogRecord( Log.DEBUG, "Test message one.\nTest message two." ) ) );
		assertEquals( "[L] Test message one.\n[L] Test message two.\n", formatter.format( new LogRecord( Log.DETAIL, "Test message one.\nTest message two." ) ) );
	}

	public void testColorFormatWithNonEmptyRecord() {
		Log.setShowColor( true );
		assertEquals( "\u001b[1m\u001b[35m[V] Test message.\u001b[0m\n", formatter.format( new LogRecord( Log.DEVEL, "Test message." ) ) );
		assertEquals( "\u001b[1m\u001b[31m[E] Test message.\u001b[0m\n", formatter.format( new LogRecord( Log.ERROR, "Test message." ) ) );
		assertEquals( "\u001b[1m\u001b[33m[W] Test message.\u001b[0m\n", formatter.format( new LogRecord( Log.WARN, "Test message." ) ) );
		assertEquals( "\u001b[37m[I] Test message.\u001b[0m\n", formatter.format( new LogRecord( Log.INFO, "Test message." ) ) );
		assertEquals( "\u001b[36m[T] Test message.\u001b[0m\n", formatter.format( new LogRecord( Log.TRACE, "Test message." ) ) );
		assertEquals( "\u001b[32m[D] Test message.\u001b[0m\n", formatter.format( new LogRecord( Log.DEBUG, "Test message." ) ) );
		assertEquals( "\u001b[1m\u001b[30m[L] Test message.\u001b[0m\n", formatter.format( new LogRecord( Log.DETAIL, "Test message." ) ) );
	}

}
