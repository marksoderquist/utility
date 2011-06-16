package com.parallelsymmetry.escape.utility.log;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import junit.framework.TestCase;

public class DefaultFormatterTest extends TestCase {

	private DefaultFormatter formatter = new DefaultFormatter();

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
		assertEquals( "[E] Test message.\n", formatter.format( new LogRecord( Log.ERROR, "Test message." ) ) );
		assertEquals( "[W] Test message.\n", formatter.format( new LogRecord( Log.WARN, "Test message." ) ) );
		assertEquals( "[I] Test message.\n", formatter.format( new LogRecord( Log.INFO, "Test message." ) ) );
		assertEquals( "[T] Test message.\n", formatter.format( new LogRecord( Log.TRACE, "Test message." ) ) );
		assertEquals( "[D] Test message.\n", formatter.format( new LogRecord( Log.DEBUG, "Test message." ) ) );
	}
	
	public void testFormatWithMultiLineRecord() {
		assertEquals( "[E] Test message one.\n[E] Test message two.\n", formatter.format( new LogRecord( Log.ERROR, "Test message one.\nTest message two.\n" ) ) );
		assertEquals( "[W] Test message one.\n[W] Test message two.\n", formatter.format( new LogRecord( Log.WARN, "Test message one.\nTest message two.\n" ) ) );
		assertEquals( "[I] Test message one.\n[I] Test message two.\n", formatter.format( new LogRecord( Log.INFO, "Test message one.\nTest message two.\n" ) ) );
		assertEquals( "[T] Test message one.\n[T] Test message two.\n", formatter.format( new LogRecord( Log.TRACE, "Test message one.\nTest message two.\n" ) ) );
		assertEquals( "[D] Test message one.\n[D] Test message two.\n", formatter.format( new LogRecord( Log.DEBUG, "Test message one.\nTest message two.\n" ) ) );
	}

	public void testColorFormatWithNonEmptyRecord() {
		Log.setShowColor( true );
		assertEquals( "\u001b[1m\u001b[31m[E] Test message.\u001b[0m\n", formatter.format( new LogRecord( Log.ERROR, "Test message." ) ) );
		assertEquals( "\u001b[1m\u001b[33m[W] Test message.\u001b[0m\n", formatter.format( new LogRecord( Log.WARN, "Test message." ) ) );
		assertEquals( "\u001b[0m[I] Test message.\u001b[0m\n", formatter.format( new LogRecord( Log.INFO, "Test message." ) ) );
		assertEquals( "\u001b[36m[T] Test message.\u001b[0m\n", formatter.format( new LogRecord( Log.TRACE, "Test message." ) ) );
		assertEquals( "\u001b[1m\u001b[30m[D] Test message.\u001b[0m\n", formatter.format( new LogRecord( Log.DEBUG, "Test message." ) ) );
	}

	public void testGetPrefix() throws Exception {
		assertEquals( "", DefaultFormatter.getPrefix( Log.NONE ) );

		assertEquals( "*", DefaultFormatter.getPrefix( new TestLevel( "", Log.ERROR.intValue() + 1 ) ) );

		assertEquals( "*", DefaultFormatter.getPrefix( Log.ERROR ) );

		assertEquals( "-", DefaultFormatter.getPrefix( new TestLevel( "", Log.ERROR.intValue() - 1 ) ) );
		assertEquals( "-", DefaultFormatter.getPrefix( new TestLevel( "", Log.WARN.intValue() + 1 ) ) );

		assertEquals( "-", DefaultFormatter.getPrefix( Log.WARN ) );

		assertEquals( " ", DefaultFormatter.getPrefix( new TestLevel( "", Log.WARN.intValue() - 1 ) ) );
		assertEquals( " ", DefaultFormatter.getPrefix( new TestLevel( "", Log.INFO.intValue() + 1 ) ) );

		assertEquals( " ", DefaultFormatter.getPrefix( Log.INFO ) );

		assertEquals( "  ", DefaultFormatter.getPrefix( new TestLevel( "", Log.INFO.intValue() - 1 ) ) );
		assertEquals( "  ", DefaultFormatter.getPrefix( new TestLevel( "", Log.TRACE.intValue() + 1 ) ) );

		assertEquals( "  ", DefaultFormatter.getPrefix( Log.TRACE ) );

		assertEquals( "   ", DefaultFormatter.getPrefix( new TestLevel( "", Log.TRACE.intValue() - 1 ) ) );
		assertEquals( "   ", DefaultFormatter.getPrefix( new TestLevel( "", Log.DEBUG.intValue() + 1 ) ) );

		assertEquals( "   ", DefaultFormatter.getPrefix( Log.DEBUG ) );

		assertEquals( "    ", DefaultFormatter.getPrefix( new TestLevel( "", Log.DEBUG.intValue() - 1 ) ) );
	}

	public void testGetColorPrefix() throws Exception {
		assertEquals( "", DefaultFormatter.getColorPrefix( Log.NONE ) );

		assertEquals( "\u001b[1m\u001b[31m", DefaultFormatter.getColorPrefix( new TestLevel( "", Log.ERROR.intValue() + 1 ) ) );

		assertEquals( "\u001b[1m\u001b[31m", DefaultFormatter.getColorPrefix( Log.ERROR ) );

		assertEquals( "\u001b[1m\u001b[33m", DefaultFormatter.getColorPrefix( new TestLevel( "", Log.ERROR.intValue() - 1 ) ) );
		assertEquals( "\u001b[1m\u001b[33m", DefaultFormatter.getColorPrefix( new TestLevel( "", Log.WARN.intValue() + 1 ) ) );

		assertEquals( "\u001b[1m\u001b[33m", DefaultFormatter.getColorPrefix( Log.WARN ) );

		assertEquals( "\u001b[0m", DefaultFormatter.getColorPrefix( new TestLevel( "", Log.WARN.intValue() - 1 ) ) );
		assertEquals( "\u001b[0m", DefaultFormatter.getColorPrefix( new TestLevel( "", Log.INFO.intValue() + 1 ) ) );

		assertEquals( "\u001b[0m", DefaultFormatter.getColorPrefix( Log.INFO ) );

		assertEquals( "\u001b[36m", DefaultFormatter.getColorPrefix( new TestLevel( "", Log.INFO.intValue() - 1 ) ) );
		assertEquals( "\u001b[36m", DefaultFormatter.getColorPrefix( new TestLevel( "", Log.TRACE.intValue() + 1 ) ) );

		assertEquals( "\u001b[36m", DefaultFormatter.getColorPrefix( Log.TRACE ) );

		assertEquals( "\u001b[1m\u001b[30m", DefaultFormatter.getColorPrefix( new TestLevel( "", Log.TRACE.intValue() - 1 ) ) );
		assertEquals( "\u001b[1m\u001b[30m", DefaultFormatter.getColorPrefix( new TestLevel( "", Log.DEBUG.intValue() + 1 ) ) );

		assertEquals( "\u001b[1m\u001b[30m", DefaultFormatter.getColorPrefix( Log.DEBUG ) );

		assertEquals( "", DefaultFormatter.getColorPrefix( new TestLevel( "", Log.DEBUG.intValue() - 1 ) ) );
	}

	public void testGetColorSuffix() throws Exception {
		assertEquals( "", DefaultFormatter.getColorSuffix( Log.NONE ) );

		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( new TestLevel( "", Log.ERROR.intValue() + 1 ) ) );

		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( Log.ERROR ) );

		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( new TestLevel( "", Log.ERROR.intValue() - 1 ) ) );
		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( new TestLevel( "", Log.WARN.intValue() + 1 ) ) );

		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( Log.WARN ) );

		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( new TestLevel( "", Log.WARN.intValue() - 1 ) ) );
		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( new TestLevel( "", Log.INFO.intValue() + 1 ) ) );

		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( Log.INFO ) );

		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( new TestLevel( "", Log.INFO.intValue() - 1 ) ) );
		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( new TestLevel( "", Log.TRACE.intValue() + 1 ) ) );

		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( Log.TRACE ) );

		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( new TestLevel( "", Log.TRACE.intValue() - 1 ) ) );
		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( new TestLevel( "", Log.DEBUG.intValue() + 1 ) ) );

		assertEquals( "\u001b[0m", DefaultFormatter.getColorSuffix( Log.DEBUG ) );

		assertEquals( "", DefaultFormatter.getColorSuffix( new TestLevel( "", Log.DEBUG.intValue() - 1 ) ) );
	}

	private static class TestLevel extends Level {

		private static final long serialVersionUID = 7758425141513202129L;

		protected TestLevel( String name, int value ) {
			super( name, value );
		}

	}

}
