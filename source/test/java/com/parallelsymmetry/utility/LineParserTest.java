package com.parallelsymmetry.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LineParserTest extends BaseTestCase {

	@Test
	public void testParserWithNull() {
		LineParser parser = new LineParser( null );
		assertNull( parser.next() );
	}

	@Test
	public void testParserWithEmpty() {
		LineParser parser = new LineParser( "" );
		assertEquals( "", parser.next() );
		assertNull( parser.next() );
	}

	@Test
	public void testParserWithSingleLine() {
		LineParser parser = new LineParser( "a" );
		assertEquals( "a", parser.next() );
		assertNull( parser.next() );
	}

	@Test
	public void testParserWithTwoEmptyLines() {
		LineParser parser = new LineParser( "\n" );
		assertEquals( "", parser.next() );
		assertEquals( "\n", parser.getTerminator() );
		assertEquals( "", parser.next() );
		assertEquals( "", parser.getTerminator() );
		assertNull( parser.next() );
	}

	//	public void testParserWithTwoEmptyLinesWithTerminators() {
	//		LineParser parser = new LineParser( "\n", true );
	//		assertEquals( "\n", parser.next() );
	//		assertEquals( "", parser.next() );
	//		assertNull( parser.next() );
	//
	//		parser = new LineParser( "\r", true );
	//		assertEquals( "\r", parser.next() );
	//		assertEquals( "", parser.next() );
	//		assertNull( parser.next() );
	//
	//		parser = new LineParser( "\r\n", true );
	//		assertEquals( "\r\n", parser.next() );
	//		assertEquals( "", parser.next() );
	//		assertNull( parser.next() );
	//	}

	@Test
	public void testParserWithFirstLineEmpty() {
		LineParser parser = new LineParser( "\na" );
		assertEquals( "", parser.next() );
		assertEquals( "a", parser.next() );
		assertNull( parser.next() );
	}

	@Test
	public void testParserWithTwoLines() {
		LineParser parser = new LineParser( "a\nb" );
		assertEquals( "a", parser.next() );
		assertEquals( "b", parser.next() );
		assertNull( parser.next() );
	}

	@Test
	public void testParserWithThreeLinesLastLineEmpty() {
		LineParser parser = new LineParser( "a\nb\n" );
		assertEquals( "a", parser.next() );
		assertEquals( "b", parser.next() );
		assertEquals( "", parser.next() );
		assertNull( parser.next() );
	}

	@Test
	public void testParserWithFirstLineAndLastLineEmpty() {
		LineParser parser = new LineParser( "\na\nb\n" );
		assertEquals( "", parser.next() );
		assertEquals( "a", parser.next() );
		assertEquals( "b", parser.next() );
		assertEquals( "", parser.next() );
		assertNull( parser.next() );
	}

	@Test
	public void testGetRemaining() {
		LineParser parser = new LineParser( "a\nb\nc" );
		assertEquals( "a", parser.next() );
		assertEquals( "b\nc", parser.getRemaining() );
		assertEquals( "b", parser.next() );
		assertEquals( "c", parser.getRemaining() );
		assertEquals( "c", parser.next() );
		assertNull( parser.next() );
	}

}
