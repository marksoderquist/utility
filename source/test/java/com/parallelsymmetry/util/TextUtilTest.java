package com.parallelsymmetry.util;

import java.nio.charset.Charset;

import junit.framework.TestCase;

public class TextUtilTest extends TestCase {

	public void testIsEmpty() {
		assertTrue( TextUtil.isEmpty( null ) );
		assertTrue( TextUtil.isEmpty( "" ) );
		assertTrue( TextUtil.isEmpty( " " ) );
		assertFalse( TextUtil.isEmpty( "." ) );
	}

	public void testAreEqual() {
		assertTrue( TextUtil.areEqual( null, null ) );
		assertTrue( TextUtil.areEqual( "", "" ) );
		assertTrue( TextUtil.areEqual( " ", " " ) );
		assertTrue( TextUtil.areEqual( "a", "a" ) );

		assertFalse( TextUtil.areEqual( null, "" ) );
		assertFalse( TextUtil.areEqual( "", null ) );
	}

	public void testAreSame() {
		assertTrue( TextUtil.areSame( null, null ) );
		assertTrue( TextUtil.areSame( "", "" ) );
		assertTrue( TextUtil.areSame( " ", " " ) );
		assertTrue( TextUtil.areSame( "a", "a" ) );

		assertTrue( TextUtil.areSame( null, "" ) );
		assertTrue( TextUtil.areSame( "", null ) );
		assertTrue( TextUtil.areSame( null, " " ) );

		assertFalse( TextUtil.areSame( null, "a" ) );
		assertFalse( TextUtil.areSame( "", "a" ) );
		assertFalse( TextUtil.areSame( " ", "a" ) );
	}

	public void testCompare() {
		assertEquals( 0, TextUtil.compare( null, null ) );
		assertEquals( -1, TextUtil.compare( null, "" ) );
		assertEquals( 1, TextUtil.compare( "", null ) );
		assertEquals( 0, TextUtil.compare( "", "" ) );

		assertEquals( 0, TextUtil.compare( "a", "a" ) );
		assertEquals( -1, TextUtil.compare( "a", "b" ) );
		assertEquals( 1, TextUtil.compare( "b", "a" ) );
	}

	public void testConcatenate() {
		assertEquals( "Count: 10", TextUtil.concatenate( "Count: ", 10 ) );
		assertEquals( "Flag: false", TextUtil.concatenate( "Flag: ", false ) );
		assertEquals( "Test String", TextUtil.concatenate( "Test", " ", "String" ) );
	}

	public void testToPrintableString() {
		assertEquals( "Bad conversion.", "[0]", TextUtil.toPrintableString( (char)0 ) );
		assertEquals( "Bad conversion.", "[27]", TextUtil.toPrintableString( (char)27 ) );
		assertEquals( "Bad conversion.", "[31]", TextUtil.toPrintableString( (char)31 ) );
		assertEquals( "Bad conversion.", " ", TextUtil.toPrintableString( (char)32 ) );
		assertEquals( "Bad conversion.", "A", TextUtil.toPrintableString( (char)65 ) );
		assertEquals( "Bad conversion.", "~", TextUtil.toPrintableString( (char)126 ) );
		assertEquals( "Bad conversion.", "[127]", TextUtil.toPrintableString( (char)127 ) );
		assertEquals( "Bad conversion.", "[255]", TextUtil.toPrintableString( (char)255 ) );
	}

	public void testToHexEncodedStringWithBytes() {
		Charset ascii = Charset.forName( "ASCII" );
		assertEquals( "Bad conversion.", "", TextUtil.toHexEncodedString( "".getBytes( ascii ) ) );
		assertEquals( "Bad conversion.", "00", TextUtil.toHexEncodedString( "\u0000".getBytes( ascii ) ) );
		assertEquals( "Bad conversion.", "0001", TextUtil.toHexEncodedString( "\u0000\u0001".getBytes( ascii ) ) );
		assertEquals( "Bad conversion.", "00010f", TextUtil.toHexEncodedString( "\u0000\u0001\u000f".getBytes( ascii ) ) );
		assertEquals( "Bad conversion.", "74657374", TextUtil.toHexEncodedString( "test".getBytes( ascii ) ) );
	}

	public void testToHexEncodedStringWithString() {
		assertEquals( "Bad conversion.", "", TextUtil.toHexEncodedString( "" ) );
		assertEquals( "Bad conversion.", "0", TextUtil.toHexEncodedString( "\u0000" ) );
		assertEquals( "Bad conversion.", "01", TextUtil.toHexEncodedString( "\u0000\u0001" ) );
		assertEquals( "Bad conversion.", "01f", TextUtil.toHexEncodedString( "\u0000\u0001\u000f" ) );
		assertEquals( "Bad conversion.", "74657374", TextUtil.toHexEncodedString( "test" ) );
	}

	public void testJustify() {
		assertEquals( "Incorrect format.", "        ", TextUtil.justify( TextUtil.LEFT, "", 8 ) );
		assertEquals( "Incorrect format.", "X       ", TextUtil.justify( TextUtil.LEFT, "X", 8 ) );
		assertEquals( "Incorrect format.", "        ", TextUtil.justify( TextUtil.CENTER, "", 8 ) );
		assertEquals( "Incorrect format.", "   X    ", TextUtil.justify( TextUtil.CENTER, "X", 8 ) );
		assertEquals( "Incorrect format.", "   XX   ", TextUtil.justify( TextUtil.CENTER, "XX", 8 ) );
		assertEquals( "Incorrect format.", "        ", TextUtil.justify( TextUtil.RIGHT, "", 8 ) );
		assertEquals( "Incorrect format.", "       X", TextUtil.justify( TextUtil.RIGHT, "X", 8 ) );
	}

	public void testJustifyWithChar() {
		assertEquals( "Incorrect format.", "........", TextUtil.justify( TextUtil.LEFT, "", 8, '.' ) );
		assertEquals( "Incorrect format.", "X.......", TextUtil.justify( TextUtil.LEFT, "X", 8, '.' ) );
		assertEquals( "Incorrect format.", "........", TextUtil.justify( TextUtil.CENTER, "", 8, '.' ) );
		assertEquals( "Incorrect format.", "...X....", TextUtil.justify( TextUtil.CENTER, "X", 8, '.' ) );
		assertEquals( "Incorrect format.", "...XX...", TextUtil.justify( TextUtil.CENTER, "XX", 8, '.' ) );
		assertEquals( "Incorrect format.", "........", TextUtil.justify( TextUtil.RIGHT, "", 8, '.' ) );
		assertEquals( "Incorrect format.", ".......X", TextUtil.justify( TextUtil.RIGHT, "X", 8, '.' ) );
	}

	public void testJustifyWithCharAndPad() {
		assertEquals( "Incorrect format.", "  ......", TextUtil.justify( TextUtil.LEFT, "", 8, '.', 2 ) );
		assertEquals( "Incorrect format.", "X  .....", TextUtil.justify( TextUtil.LEFT, "X", 8, '.', 2 ) );
		assertEquals( "Incorrect format.", "..    ..", TextUtil.justify( TextUtil.CENTER, "", 8, '.', 2 ) );
		assertEquals( "Incorrect format.", ".  X  ..", TextUtil.justify( TextUtil.CENTER, "X", 8, '.', 2 ) );
		assertEquals( "Incorrect format.", ".  XX  .", TextUtil.justify( TextUtil.CENTER, "XX", 8, '.', 2 ) );
		assertEquals( "Incorrect format.", "......  ", TextUtil.justify( TextUtil.RIGHT, "", 8, '.', 2 ) );
		assertEquals( "Incorrect format.", ".....  X", TextUtil.justify( TextUtil.RIGHT, "X", 8, '.', 2 ) );
	}

	public void testPad() {
		assertEquals( "Incorrect pad.", "", TextUtil.pad( -1 ) );
		assertEquals( "Incorrect pad.", "", TextUtil.pad( 0 ) );
		assertEquals( "Incorrect pad.", " ", TextUtil.pad( 1 ) );
		assertEquals( "Incorrect pad.", "     ", TextUtil.pad( 5 ) );
		assertEquals( "Incorrect pad.", "        ", TextUtil.pad( 8 ) );
	}

	public void testPadWithChar() {
		assertEquals( "Incorrect pad.", "", TextUtil.pad( -1, '.' ) );
		assertEquals( "Incorrect pad.", "", TextUtil.pad( 0, '.' ) );
		assertEquals( "Incorrect pad.", "x", TextUtil.pad( 1, 'x' ) );
		assertEquals( "Incorrect pad.", ",,,,,", TextUtil.pad( 5, ',' ) );
		assertEquals( "Incorrect pad.", "--------", TextUtil.pad( 8, '-' ) );
	}

	public void testGetLineCount() {
		assertEquals( 0, TextUtil.getLineCount( null ) );
		assertEquals( 1, TextUtil.getLineCount( "" ) );
		assertEquals( 1, TextUtil.getLineCount( " " ) );
		assertEquals( 2, TextUtil.getLineCount( " \n " ) );
		assertEquals( 2, TextUtil.getLineCount( " \r " ) );
		assertEquals( 2, TextUtil.getLineCount( " \r\n " ) );
	}

	public void testIndent() {
		assertEquals( null, TextUtil.indent( null ) );
		assertEquals( "  ", TextUtil.indent( "" ) );
		assertEquals( "  a", TextUtil.indent( "a" ) );
		assertEquals( "  \n  \n  ", TextUtil.indent( "\n\n" ) );
		assertEquals( "  \r  \r  ", TextUtil.indent( "\r\r" ) );
		assertEquals( "  \r\n  \r\n  ", TextUtil.indent( "\r\n\r\n" ) );
		assertEquals( "  \n  a\n  ", TextUtil.indent( "\na\n" ) );
	}

}
