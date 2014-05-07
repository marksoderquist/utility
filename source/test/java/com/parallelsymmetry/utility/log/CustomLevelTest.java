package com.parallelsymmetry.utility.log;

import junit.framework.TestCase;

public class CustomLevelTest extends TestCase {

	public void testGetTag() {
		assertEquals( "", ( (CustomLevel)Log.NONE ).getTag() );
		assertEquals( "", ( (CustomLevel)Log.HELP ).getTag() );
		assertEquals( "[V]", ( (CustomLevel)Log.DEVEL ).getTag() );
		assertEquals( "[E]", ( (CustomLevel)Log.ERROR ).getTag() );
		assertEquals( "[W]", ( (CustomLevel)Log.WARN ).getTag() );
		assertEquals( "[I]", ( (CustomLevel)Log.INFO ).getTag() );
		assertEquals( "[T]", ( (CustomLevel)Log.TRACE ).getTag() );
		assertEquals( "[D]", ( (CustomLevel)Log.DEBUG ).getTag() );
		assertEquals( "[L]", ( (CustomLevel)Log.DETAIL ).getTag() );
	}

	public void testGetAnsiColor() {
		assertEquals( "", ( (CustomLevel)Log.NONE ).getAnsiColor() );
		assertEquals( "", ( (CustomLevel)Log.HELP ).getAnsiColor() );
		assertEquals( "\u001b[1m\u001b[35m", ( (CustomLevel)Log.DEVEL ).getAnsiColor() );
		assertEquals( "\u001b[1m\u001b[31m", ( (CustomLevel)Log.ERROR ).getAnsiColor() );
		assertEquals( "\u001b[1m\u001b[33m", ( (CustomLevel)Log.WARN ).getAnsiColor() );
		assertEquals( "\u001b[37m", ( (CustomLevel)Log.INFO ).getAnsiColor() );
		assertEquals( "\u001b[36m", ( (CustomLevel)Log.TRACE ).getAnsiColor() );
		assertEquals( "\u001b[32m", ( (CustomLevel)Log.DEBUG ).getAnsiColor() );
		assertEquals( "\u001b[1m\u001b[30m", ( (CustomLevel)Log.DETAIL ).getAnsiColor() );
	}

	public void testGetPrefix() {
		assertEquals( "", ( (CustomLevel)Log.NONE ).getPrefix() );
		assertEquals( "", ( (CustomLevel)Log.HELP ).getPrefix() );
		assertEquals( "=", ( (CustomLevel)Log.DEVEL ).getPrefix() );
		assertEquals( "*", ( (CustomLevel)Log.ERROR ).getPrefix() );
		assertEquals( "-", ( (CustomLevel)Log.WARN ).getPrefix() );
		assertEquals( " ", ( (CustomLevel)Log.INFO ).getPrefix() );
		assertEquals( "  ", ( (CustomLevel)Log.TRACE ).getPrefix() );
		assertEquals( "   ", ( (CustomLevel)Log.DEBUG ).getPrefix() );
		assertEquals( "    ", ( (CustomLevel)Log.DETAIL ).getPrefix() );
	}

}
