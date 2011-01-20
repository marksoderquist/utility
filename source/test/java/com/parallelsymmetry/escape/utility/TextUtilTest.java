package com.parallelsymmetry.escape.utility;

import java.nio.charset.Charset;
import java.util.Arrays;

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

	public void testGetMD5Sum() {
		assertEquals( null, TextUtil.getMD5Sum( null ) );
		assertEquals( "d41d8cd98f00b204e9800998ecf8427e", TextUtil.toHexEncodedString( TextUtil.getMD5Sum( "" ) ) );
		assertEquals( "7215ee9c7d9dc229d2921a40e899ec5f", TextUtil.toHexEncodedString( TextUtil.getMD5Sum( " " ) ) );
		assertEquals( "ae2b1fca515949e5d54fb22b8ed95575", TextUtil.toHexEncodedString( TextUtil.getMD5Sum( "testing" ) ) );

		char[] data = new char[8192];
		Arrays.fill( data, 't' );
		assertEquals( "aea6ce04bb28d644a8d4e0bc6a319b54", TextUtil.toHexEncodedString( TextUtil.getMD5Sum( new String( data ) ) ) );
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

	public void testIsInteger() throws Exception {
		assertEquals( false, TextUtil.isInteger( null ) );
		assertEquals( false, TextUtil.isInteger( "" ) );

		assertEquals( false, TextUtil.isInteger( "1e-10" ) );
		assertEquals( false, TextUtil.isInteger( "1.0" ) );
		assertEquals( false, TextUtil.isInteger( "2147483648" ) );
		assertEquals( false, TextUtil.isInteger( "-2147483649" ) );

		assertEquals( true, TextUtil.isInteger( "0" ) );
		assertEquals( true, TextUtil.isInteger( "2147483647" ) );
		assertEquals( true, TextUtil.isInteger( "-2147483648" ) );
	}

	public void testIsLong() throws Exception {
		assertEquals( false, TextUtil.isLong( null ) );
		assertEquals( false, TextUtil.isLong( "" ) );

		assertEquals( false, TextUtil.isLong( "1e-10" ) );
		assertEquals( false, TextUtil.isLong( "1.0" ) );
		assertEquals( false, TextUtil.isLong( "9223372036854775808" ) );
		assertEquals( false, TextUtil.isLong( "-9223372036854775809" ) );

		assertEquals( true, TextUtil.isLong( "0" ) );
		assertEquals( true, TextUtil.isLong( "9223372036854775807" ) );
		assertEquals( true, TextUtil.isLong( "-9223372036854775808" ) );
	}

	public void testIsFloat() throws Exception {
		assertEquals( false, TextUtil.isFloat( null ) );
		assertEquals( false, TextUtil.isFloat( "" ) );

		assertEquals( true, TextUtil.isFloat( "0" ) );
		assertEquals( true, TextUtil.isFloat( "1.0" ) );
		assertEquals( true, TextUtil.isFloat( "1e10" ) );
		assertEquals( true, TextUtil.isFloat( "1e-10" ) );
		assertEquals( true, TextUtil.isFloat( "-1e10" ) );
		assertEquals( true, TextUtil.isFloat( "-1e-10" ) );
	}

	public void testIsDouble() throws Exception {
		assertEquals( false, TextUtil.isDouble( null ) );
		assertEquals( false, TextUtil.isDouble( "" ) );

		assertEquals( true, TextUtil.isDouble( "0" ) );
		assertEquals( true, TextUtil.isDouble( "1.0" ) );
		assertEquals( true, TextUtil.isDouble( "1e10" ) );
		assertEquals( true, TextUtil.isDouble( "1e-10" ) );
		assertEquals( true, TextUtil.isDouble( "-1e10" ) );
		assertEquals( true, TextUtil.isDouble( "-1e-10" ) );
	}

	public void testCapitalize() {
		assertEquals( null, TextUtil.capitalize( null ) );
		assertEquals( "", TextUtil.capitalize( "" ) );
		assertEquals( "Test", TextUtil.capitalize( "test" ) );
		assertEquals( "New brunswick", TextUtil.capitalize( "new brunswick" ) );
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
	
	public void testPrepend() {
		assertEquals( null, TextUtil.prepend( null, "X" ) );
		assertEquals( "A", TextUtil.prepend( "A", null ) );
		assertEquals( "B", TextUtil.prepend( "B", "" ) );

		assertEquals( "XC", TextUtil.prepend( "C", "X" ) );
		assertEquals( "XD\nXE", TextUtil.prepend( "D\nE", "X" ) );
	}

	public void testAppend() {
		assertEquals( null, TextUtil.append( null, "X" ) );
		assertEquals( "A", TextUtil.append( "A", null ) );
		assertEquals( "B", TextUtil.append( "B", "" ) );

		assertEquals( "CX", TextUtil.append( "C", "X" ) );
		assertEquals( "DX\nEX", TextUtil.append( "D\nE", "X" ) );
	}

	public void testReline() {
		int length = 40;
		String sample = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

		String result = TextUtil.reline( sample, length );

		LineParser parser = new LineParser( result );
		assertEquals( "Lorem ipsum dolor sit amet, consectetur", parser.next() );
		assertEquals( "adipisicing elit, sed do eiusmod tempor", parser.next() );
		assertEquals( "incididunt ut labore et dolore magna", parser.next() );
		assertEquals( "aliqua. Ut enim ad minim veniam, quis", parser.next() );
		assertEquals( "nostrud exercitation ullamco laboris", parser.next() );
		assertEquals( "nisi ut aliquip ex ea commodo consequat.", parser.next() );
		assertEquals( "Duis aute irure dolor in reprehenderit", parser.next() );
		assertEquals( "in voluptate velit esse cillum dolore eu", parser.next() );
		assertEquals( "fugiat nulla pariatur. Excepteur sint", parser.next() );
		assertEquals( "occaecat cupidatat non proident, sunt in", parser.next() );
		assertEquals( "culpa qui officia deserunt mollit anim", parser.next() );
		assertEquals( "id est laborum.", parser.next() );
	}

}
