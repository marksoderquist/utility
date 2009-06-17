package com.parallelsymmetry.util;

import com.parallelsymmetry.log.Log;

import junit.framework.TestCase;

public class LineParserTest extends TestCase {

	@Override
	public void setUp() {
		Log.write();
	}

	public void testParseNull() {
		LineParser parser = new LineParser( (String)null );
		assertEquals( null, parser.next() );
	}

	public void testParseEmpty() {
		LineParser parser = new LineParser( "" );
		assertEquals( "", parser.next() );
		assertEquals( null, parser.next() );
	}

	public void testParseSpace() {
		LineParser parser = new LineParser( " " );
		assertEquals( " ", parser.next() );
		assertEquals( null, parser.next() );
	}

	public void testParseCharacterOnly() {
		LineParser parser = new LineParser( "a" );
		assertEquals( "a", parser.next() );
		assertEquals( null, parser.next() );
	}

	public void testParseTwoEmptyUnixLines() {
		LineParser parser = new LineParser( "\n" );
		assertEquals( "\n", parser.next() );
		assertEquals( "", parser.next() );
		assertEquals( null, parser.next() );
	}

	public void testParseThreeEmptyUnixLines() {
		LineParser parser = new LineParser( "\n\n" );
		assertEquals( "\n", parser.next() );
		assertEquals( "\n", parser.next() );
		assertEquals( "", parser.next() );
		assertEquals( null, parser.next() );
	}

	public void testParseTwoEmptyMacLines() {
		LineParser parser = new LineParser( "\r" );
		assertEquals( "\r", parser.next() );
		assertEquals( "", parser.next() );
		assertEquals( null, parser.next() );
	}

	public void testParseThreeEmptyMacLines() {
		LineParser parser = new LineParser( "\r\r" );
		assertEquals( "\r", parser.next() );
		assertEquals( "\r", parser.next() );
		assertEquals( "", parser.next() );
		assertEquals( null, parser.next() );
	}

	public void testParseTwoEmptyWindowsLines() {
		LineParser parser = new LineParser( "\r\n" );
		assertEquals( "\r\n", parser.next() );
		assertEquals( "", parser.next() );
		assertEquals( null, parser.next() );
	}

	public void testParseThreeEmptyWindowsLines() {
		LineParser parser = new LineParser( "\r\n\r\n" );
		assertEquals( "\r\n", parser.next() );
		assertEquals( "\r\n", parser.next() );
		assertEquals( "", parser.next() );
		assertEquals( null, parser.next() );
	}

	public void testParseTwoEmptyBackwardsLines() {
		LineParser parser = new LineParser( "\n\r" );
		assertEquals( "\n\r", parser.next() );
		assertEquals( "", parser.next() );
		assertEquals( null, parser.next() );
	}

	public void testParseThreeEmptyBackwardsLines() {
		LineParser parser = new LineParser( "\n\r\n\r" );
		assertEquals( "\n\r", parser.next() );
		assertEquals( "\n\r", parser.next() );
		assertEquals( "", parser.next() );
		assertEquals( null, parser.next() );
	}

	public void testParseMixedLines() {
		String string = "a\nb\rc\r\nd\n\re";
		LineParser parser = new LineParser( string );
		assertEquals( "a\n", parser.next() );
		assertEquals( "b\r", parser.next() );
		assertEquals( "c\r\n", parser.next() );
		assertEquals( "d\n\r", parser.next() );
		assertEquals( "e", parser.next() );
		assertEquals( null, parser.next() );
	}

}
