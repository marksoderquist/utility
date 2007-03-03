package org.novaworx.util;

import java.nio.charset.Charset;

import junit.framework.TestCase;

public class TextFormatTest extends TestCase {

	public void testToPrintableString() {
		assertEquals( "Bad conversion.", "[0]", TextFormat.toPrintableString( (char)0 ) );
		assertEquals( "Bad conversion.", "[27]", TextFormat.toPrintableString( (char)27 ) );
		assertEquals( "Bad conversion.", "[31]", TextFormat.toPrintableString( (char)31 ) );
		assertEquals( "Bad conversion.", " ", TextFormat.toPrintableString( (char)32 ) );
		assertEquals( "Bad conversion.", "A", TextFormat.toPrintableString( (char)65 ) );
		assertEquals( "Bad conversion.", "~", TextFormat.toPrintableString( (char)126 ) );
		assertEquals( "Bad conversion.", "[127]", TextFormat.toPrintableString( (char)127 ) );
		assertEquals( "Bad conversion.", "[255]", TextFormat.toPrintableString( (char)255 ) );
	}

	public void testToHexEncodedString() {
		Charset ascii = Charset.forName( "ASCII" );
		assertEquals( "Bad conversion.", "", TextFormat.toHexEncodedString( "".getBytes( ascii ) ) );
		assertEquals( "Bad conversion.", "00", TextFormat.toHexEncodedString( "\u0000".getBytes( ascii ) ) );
		assertEquals( "Bad conversion.", "0001", TextFormat.toHexEncodedString( "\u0000\u0001".getBytes( ascii ) ) );
		assertEquals( "Bad conversion.", "00010f", TextFormat.toHexEncodedString( "\u0000\u0001\u000f".getBytes( ascii ) ) );
		assertEquals( "Bad conversion.", "74657374", TextFormat.toHexEncodedString( "test".getBytes( ascii ) ) );
	}

	public void testToHexEncodedStringWithString() {
		assertEquals( "Bad conversion.", "", TextFormat.toHexEncodedString( "" ) );
		assertEquals( "Bad conversion.", "0", TextFormat.toHexEncodedString( "\u0000" ) );
		assertEquals( "Bad conversion.", "01", TextFormat.toHexEncodedString( "\u0000\u0001" ) );
		assertEquals( "Bad conversion.", "01f", TextFormat.toHexEncodedString( "\u0000\u0001\u000f" ) );
		assertEquals( "Bad conversion.", "74657374", TextFormat.toHexEncodedString( "test" ) );
	}

	public void testJustify() {
		assertEquals( "Incorrect format.", "        ", TextFormat.justify( TextFormat.LEFT, "", 8 ) );
		assertEquals( "Incorrect format.", "X       ", TextFormat.justify( TextFormat.LEFT, "X", 8 ) );
		assertEquals( "Incorrect format.", "        ", TextFormat.justify( TextFormat.CENTER, "", 8 ) );
		assertEquals( "Incorrect format.", "   X    ", TextFormat.justify( TextFormat.CENTER, "X", 8 ) );
		assertEquals( "Incorrect format.", "   XX   ", TextFormat.justify( TextFormat.CENTER, "XX", 8 ) );
		assertEquals( "Incorrect format.", "        ", TextFormat.justify( TextFormat.RIGHT, "", 8 ) );
		assertEquals( "Incorrect format.", "       X", TextFormat.justify( TextFormat.RIGHT, "X", 8 ) );
	}

	public void testJustifyWithChar() {
		assertEquals( "Incorrect format.", "........", TextFormat.justify( TextFormat.LEFT, "", 8, '.' ) );
		assertEquals( "Incorrect format.", "X.......", TextFormat.justify( TextFormat.LEFT, "X", 8, '.' ) );
		assertEquals( "Incorrect format.", "........", TextFormat.justify( TextFormat.CENTER, "", 8, '.' ) );
		assertEquals( "Incorrect format.", "...X....", TextFormat.justify( TextFormat.CENTER, "X", 8, '.' ) );
		assertEquals( "Incorrect format.", "...XX...", TextFormat.justify( TextFormat.CENTER, "XX", 8, '.' ) );
		assertEquals( "Incorrect format.", "........", TextFormat.justify( TextFormat.RIGHT, "", 8, '.' ) );
		assertEquals( "Incorrect format.", ".......X", TextFormat.justify( TextFormat.RIGHT, "X", 8, '.' ) );
	}

	public void testJustifyWithCharAndPad() {
		assertEquals( "Incorrect format.", "  ......", TextFormat.justify( TextFormat.LEFT, "", 8, '.', 2 ) );
		assertEquals( "Incorrect format.", "X  .....", TextFormat.justify( TextFormat.LEFT, "X", 8, '.', 2 ) );
		assertEquals( "Incorrect format.", "..    ..", TextFormat.justify( TextFormat.CENTER, "", 8, '.', 2 ) );
		assertEquals( "Incorrect format.", ".  X  ..", TextFormat.justify( TextFormat.CENTER, "X", 8, '.', 2 ) );
		assertEquals( "Incorrect format.", ".  XX  .", TextFormat.justify( TextFormat.CENTER, "XX", 8, '.', 2 ) );
		assertEquals( "Incorrect format.", "......  ", TextFormat.justify( TextFormat.RIGHT, "", 8, '.', 2 ) );
		assertEquals( "Incorrect format.", ".....  X", TextFormat.justify( TextFormat.RIGHT, "X", 8, '.', 2 ) );
	}

	public void testPad() {
		assertEquals( "Incorrect pad.", "", TextFormat.pad( -1 ) );
		assertEquals( "Incorrect pad.", "", TextFormat.pad( 0 ) );
		assertEquals( "Incorrect pad.", " ", TextFormat.pad( 1 ) );
		assertEquals( "Incorrect pad.", "     ", TextFormat.pad( 5 ) );
		assertEquals( "Incorrect pad.", "        ", TextFormat.pad( 8 ) );
	}

	public void testPadWithChar() {
		assertEquals( "Incorrect pad.", "", TextFormat.pad( -1, '.' ) );
		assertEquals( "Incorrect pad.", "", TextFormat.pad( 0, '.' ) );
		assertEquals( "Incorrect pad.", "x", TextFormat.pad( 1, 'x' ) );
		assertEquals( "Incorrect pad.", ",,,,,", TextFormat.pad( 5, ',' ) );
		assertEquals( "Incorrect pad.", "--------", TextFormat.pad( 8, '-' ) );
	}

}
