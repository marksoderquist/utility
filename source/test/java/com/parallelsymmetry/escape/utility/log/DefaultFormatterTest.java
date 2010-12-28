package com.parallelsymmetry.escape.utility.log;

import java.util.logging.LogRecord;

import junit.framework.TestCase;

public class DefaultFormatterTest extends TestCase {

	private DefaultFormatter formatter = new DefaultFormatter();

	public void testFormatWithNull() {
		assertNull( null, formatter.format( null ) );
	}

	public void testFormatWithEmptyInfoRecord() {
		assertEquals( "\n", formatter.format( new LogRecord( Log.INFO, "" ) ) );
	}

	public void testFormatWithNonEmptyInfoRecord() {
		assertEquals( "Test message.\n", formatter.format( new LogRecord( Log.INFO, "Test message." ) ) );
	}

}
