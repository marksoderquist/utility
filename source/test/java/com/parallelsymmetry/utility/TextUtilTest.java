package com.parallelsymmetry.utility;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TextUtilTest extends BaseTestCase {

	@Test
	public void testIsEmpty() {
		assertTrue( TextUtil.isEmpty( null ) );
		assertTrue( TextUtil.isEmpty( "" ) );
		assertTrue( TextUtil.isEmpty( " " ) );
		assertFalse( TextUtil.isEmpty( "." ) );
	}

	@Test
	public void testAreEqual() {
		assertTrue( TextUtil.areEqual( null, null ) );
		assertTrue( TextUtil.areEqual( "", "" ) );
		assertTrue( TextUtil.areEqual( " ", " " ) );
		assertTrue( TextUtil.areEqual( "a", "a" ) );

		assertFalse( TextUtil.areEqual( null, "" ) );
		assertFalse( TextUtil.areEqual( "", null ) );
		assertFalse( TextUtil.areEqual( "a", "b" ) );
		assertFalse( TextUtil.areEqual( "b", "a" ) );
	}

	@Test
	public void testAreEqualIgnoreCase() {
		assertTrue( TextUtil.areEqualIgnoreCase( null, null ) );
		assertTrue( TextUtil.areEqualIgnoreCase( "", "" ) );
		assertTrue( TextUtil.areEqualIgnoreCase( " ", " " ) );
		assertTrue( TextUtil.areEqualIgnoreCase( "A", "a" ) );
		assertTrue( TextUtil.areEqualIgnoreCase( "a", "A" ) );

		assertFalse( TextUtil.areEqualIgnoreCase( null, "" ) );
		assertFalse( TextUtil.areEqualIgnoreCase( "", null ) );
		assertFalse( TextUtil.areEqual( "A", "b" ) );
		assertFalse( TextUtil.areEqual( "B", "a" ) );
	}

	@Test
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

	@Test
	public void testCompare() {
		assertEquals( 0, TextUtil.compare( null, null ) );
		assertEquals( -1, TextUtil.compare( null, "" ) );
		assertEquals( 1, TextUtil.compare( "", null ) );
		assertEquals( 0, TextUtil.compare( "", "" ) );

		assertEquals( 0, TextUtil.compare( "a", "a" ) );
		assertEquals( -1, TextUtil.compare( "a", "b" ) );
		assertEquals( 1, TextUtil.compare( "b", "a" ) );
	}

	@Test
	public void testCompareIgnoreCase() {
		assertEquals( 0, TextUtil.compareIgnoreCase( null, null ) );
		assertEquals( -1, TextUtil.compareIgnoreCase( null, "" ) );
		assertEquals( 1, TextUtil.compareIgnoreCase( "", null ) );
		assertEquals( 0, TextUtil.compareIgnoreCase( "", "" ) );

		assertEquals( 0, TextUtil.compareIgnoreCase( "A", "a" ) );
		assertEquals( 0, TextUtil.compareIgnoreCase( "a", "A" ) );
		assertEquals( -1, TextUtil.compareIgnoreCase( "a", "B" ) );
		assertEquals( -1, TextUtil.compareIgnoreCase( "A", "b" ) );
		assertEquals( 1, TextUtil.compareIgnoreCase( "B", "a" ) );
		assertEquals( 1, TextUtil.compareIgnoreCase( "b", "A" ) );
	}

	@Test
	public void testCleanNull() {
		assertEquals( null, TextUtil.cleanNull( null ) );
		assertEquals( null, TextUtil.cleanNull( "" ) );
		assertEquals( null, TextUtil.cleanNull( " " ) );
		assertEquals( "a", TextUtil.cleanNull( " a " ) );
	}

	@Test
	public void testCleanEmpty() {
		assertEquals( "", TextUtil.cleanEmpty( null ) );
		assertEquals( "", TextUtil.cleanEmpty( "" ) );
		assertEquals( "", TextUtil.cleanEmpty( " " ) );
		assertEquals( "a", TextUtil.cleanEmpty( " a " ) );
	}

	@Test
	public void testConcatenate() {
		assertEquals( "Count: 10", TextUtil.concatenate( "Count: ", 10 ) );
		assertEquals( "Flag: false", TextUtil.concatenate( "Flag: ", false ) );
		assertEquals( "Test String", TextUtil.concatenate( "Test", " ", "String" ) );
	}

	@Test
	public void testGetMD5Sum() {
		assertEquals( null, TextUtil.getMD5Sum( null ) );
		assertEquals( "d41d8cd98f00b204e9800998ecf8427e", TextUtil.toHexEncodedString( TextUtil.getMD5Sum( "" ) ) );
		assertEquals( "7215ee9c7d9dc229d2921a40e899ec5f", TextUtil.toHexEncodedString( TextUtil.getMD5Sum( " " ) ) );
		assertEquals( "ae2b1fca515949e5d54fb22b8ed95575", TextUtil.toHexEncodedString( TextUtil.getMD5Sum( "testing" ) ) );

		char[] data = new char[ 8192 ];
		Arrays.fill( data, 't' );
		assertEquals( "aea6ce04bb28d644a8d4e0bc6a319b54", TextUtil.toHexEncodedString( TextUtil.getMD5Sum( new String( data ) ) ) );
	}

	@Test
	public void testToPrintableStringUsingByte() {
		assertEquals( "[0]", TextUtil.toPrintableString( (byte)0 ) );
		assertEquals( "[27]", TextUtil.toPrintableString( (byte)27 ) );
		assertEquals( "[31]", TextUtil.toPrintableString( (byte)31 ) );
		assertEquals( " ", TextUtil.toPrintableString( (byte)32 ) );
		assertEquals( "A", TextUtil.toPrintableString( (byte)65 ) );
		assertEquals( "~", TextUtil.toPrintableString( (byte)126 ) );
		assertEquals( "[127]", TextUtil.toPrintableString( (byte)127 ) );
		assertEquals( "[128]", TextUtil.toPrintableString( (byte)128 ) );
		assertEquals( "[255]", TextUtil.toPrintableString( (byte)255 ) );

		assertEquals( "[255]", TextUtil.toPrintableString( (byte)-1 ) );
		assertEquals( "[0]", TextUtil.toPrintableString( (byte)256 ) );
	}

	@Test
	public void testToPrintableString() {
		assertEquals( "[0]", TextUtil.toPrintableString( (char)0 ) );
		assertEquals( "[27]", TextUtil.toPrintableString( (char)27 ) );
		assertEquals( "[31]", TextUtil.toPrintableString( (char)31 ) );
		assertEquals( " ", TextUtil.toPrintableString( (char)32 ) );
		assertEquals( "A", TextUtil.toPrintableString( (char)65 ) );
		assertEquals( "~", TextUtil.toPrintableString( (char)126 ) );
		assertEquals( "[127]", TextUtil.toPrintableString( (char)127 ) );
		assertEquals( "[255]", TextUtil.toPrintableString( (char)255 ) );
	}

	@Test
	public void testToHexEncodedStringWithBytes() {
		Charset encoding = StandardCharsets.ISO_8859_1;
		assertEquals( "", TextUtil.toHexEncodedString( "".getBytes( encoding ) ) );
		assertEquals( "00", TextUtil.toHexEncodedString( "\u0000".getBytes( encoding ) ) );
		assertEquals( "0001", TextUtil.toHexEncodedString( "\u0000\u0001".getBytes( encoding ) ) );
		assertEquals( "ff01", TextUtil.toHexEncodedString( "\u00ff\u0001".getBytes( encoding ) ) );
		assertEquals( "00010f", TextUtil.toHexEncodedString( "\u0000\u0001\u000f".getBytes( encoding ) ) );
		assertEquals( "74657374", TextUtil.toHexEncodedString( "test".getBytes( encoding ) ) );
	}

	@Test
	public void testHexEncodeWithString() {
		assertEquals( "", TextUtil.hexEncode( "" ) );
		assertEquals( "0000", TextUtil.hexEncode( "\u0000" ) );
		assertEquals( "00000001", TextUtil.hexEncode( "\u0000\u0001" ) );
		assertEquals( "00000001000f", TextUtil.hexEncode( "\u0000\u0001\u000f" ) );
		assertEquals( "0074006500730074", TextUtil.hexEncode( "test" ) );
	}

	@Test
	public void testHexDecodeWithString() {
		assertEquals( null, TextUtil.hexDecode( null ) );
		assertEquals( "", TextUtil.hexDecode( "" ) );
		assertEquals( "\u0000", TextUtil.hexDecode( "0000" ) );
		assertEquals( "\u0000\u0001", TextUtil.hexDecode( "00000001" ) );
		assertEquals( "\u0000\u0001\u000f", TextUtil.hexDecode( "00000001000f" ) );
		assertEquals( "test", TextUtil.hexDecode( "0074006500730074" ) );

	}

	@Test
	public void testHexByteEncode() {
		Charset encoding = StandardCharsets.ISO_8859_1;
		assertEquals( "", TextUtil.secureHexByteEncode( "".getBytes( encoding ) ) );
		assertEquals( "00", TextUtil.secureHexByteEncode( "\u0000".getBytes( encoding ) ) );
		assertEquals( "0001", TextUtil.secureHexByteEncode( "\u0000\u0001".getBytes( encoding ) ) );
		assertEquals( "ff01", TextUtil.secureHexByteEncode( "\u00ff\u0001".getBytes( encoding ) ) );
		assertEquals( "00010f", TextUtil.secureHexByteEncode( "\u0000\u0001\u000f".getBytes( encoding ) ) );
		assertEquals( "74657374", TextUtil.secureHexByteEncode( "test".getBytes( encoding ) ) );
	}

	@Test
	public void testHexByteDecode() {
		Charset encoding = StandardCharsets.ISO_8859_1;
		assertEquals( null, TextUtil.secureHexByteDecode( null ) );
		assertEquals( "", new String( TextUtil.secureHexByteDecode( "" ), encoding ) );
		assertEquals( "\u0000", new String( TextUtil.secureHexByteDecode( "00" ), encoding ) );
		assertEquals( "\u0000\u0001", new String( TextUtil.secureHexByteDecode( "0001" ), encoding ) );
		assertEquals( "\u00ff\u0001", new String( TextUtil.secureHexByteDecode( "ff01" ), encoding ) );
		assertEquals( "\u0000\u0001\u000f", new String( TextUtil.secureHexByteDecode( "00010f" ), encoding ) );
		assertEquals( "test", new String( TextUtil.secureHexByteDecode( "74657374" ), encoding ) );
	}

	@Test
	public void testHexCharEncode() {
		assertEquals( null, TextUtil.secureHexEncode( null ) );
		assertEquals( "", TextUtil.secureHexEncode( "".toCharArray() ) );
		assertEquals( "0000", TextUtil.secureHexEncode( "\u0000".toCharArray() ) );
		assertEquals( "00000001", TextUtil.secureHexEncode( "\u0000\u0001".toCharArray() ) );
		assertEquals( "00000001000f", TextUtil.secureHexEncode( "\u0000\u0001\u000f".toCharArray() ) );
		assertEquals( "0074006500730074", TextUtil.secureHexEncode( "test".toCharArray() ) );
	}

	@Test
	public void testHexCharDecode() {
		assertEquals( null, TextUtil.secureHexDecode( null ) );
		assertEquals( "", new String( TextUtil.secureHexDecode( "" ) ) );
		assertEquals( "\u0000", new String( TextUtil.secureHexDecode( "0000" ) ) );
		assertEquals( "\u0000\u0001", new String( TextUtil.secureHexDecode( "00000001" ) ) );
		assertEquals( "\u0000\u0001\u000f", new String( TextUtil.secureHexDecode( "00000001000f" ) ) );
		assertEquals( "test", new String( TextUtil.secureHexDecode( "0074006500730074" ) ) );
	}

	@Test
	public void testIsInteger() {
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

	@Test
	public void testIsLong() {
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

	@Test
	public void testIsFloat() {
		assertEquals( false, TextUtil.isFloat( null ) );
		assertEquals( false, TextUtil.isFloat( "" ) );

		assertEquals( true, TextUtil.isFloat( "0" ) );
		assertEquals( true, TextUtil.isFloat( "1.0" ) );
		assertEquals( true, TextUtil.isFloat( "1e10" ) );
		assertEquals( true, TextUtil.isFloat( "1e-10" ) );
		assertEquals( true, TextUtil.isFloat( "-1e10" ) );
		assertEquals( true, TextUtil.isFloat( "-1e-10" ) );
	}

	@Test
	public void testIsDouble() {
		assertEquals( false, TextUtil.isDouble( null ) );
		assertEquals( false, TextUtil.isDouble( "" ) );

		assertEquals( true, TextUtil.isDouble( "0" ) );
		assertEquals( true, TextUtil.isDouble( "1.0" ) );
		assertEquals( true, TextUtil.isDouble( "1e10" ) );
		assertEquals( true, TextUtil.isDouble( "1e-10" ) );
		assertEquals( true, TextUtil.isDouble( "-1e10" ) );
		assertEquals( true, TextUtil.isDouble( "-1e-10" ) );
	}

	@Test
	public void testCapitalize() {
		assertEquals( null, TextUtil.capitalize( null ) );
		assertEquals( "", TextUtil.capitalize( "" ) );
		assertEquals( "Test", TextUtil.capitalize( "test" ) );
		assertEquals( "New brunswick", TextUtil.capitalize( "new brunswick" ) );
	}

	@Test
	public void testJustify() {
		assertEquals( "        ", TextUtil.justify( TextUtil.LEFT, "", 8 ) );
		assertEquals( "X       ", TextUtil.justify( TextUtil.LEFT, "X", 8 ) );
		assertEquals( "        ", TextUtil.justify( TextUtil.CENTER, "", 8 ) );
		assertEquals( "   X    ", TextUtil.justify( TextUtil.CENTER, "X", 8 ) );
		assertEquals( "   XX   ", TextUtil.justify( TextUtil.CENTER, "XX", 8 ) );
		assertEquals( "        ", TextUtil.justify( TextUtil.RIGHT, "", 8 ) );
		assertEquals( "       X", TextUtil.justify( TextUtil.RIGHT, "X", 8 ) );
	}

	@Test
	public void testJustifyWithChar() {
		assertEquals( "........", TextUtil.justify( TextUtil.LEFT, "", 8, '.' ) );
		assertEquals( "X.......", TextUtil.justify( TextUtil.LEFT, "X", 8, '.' ) );
		assertEquals( "........", TextUtil.justify( TextUtil.CENTER, "", 8, '.' ) );
		assertEquals( "...X....", TextUtil.justify( TextUtil.CENTER, "X", 8, '.' ) );
		assertEquals( "...XX...", TextUtil.justify( TextUtil.CENTER, "XX", 8, '.' ) );
		assertEquals( "........", TextUtil.justify( TextUtil.RIGHT, "", 8, '.' ) );
		assertEquals( ".......X", TextUtil.justify( TextUtil.RIGHT, "X", 8, '.' ) );
	}

	@Test
	public void testJustifyWithCharAndPad() {
		assertEquals( "  ......", TextUtil.justify( TextUtil.LEFT, "", 8, '.', 2 ) );
		assertEquals( "X  .....", TextUtil.justify( TextUtil.LEFT, "X", 8, '.', 2 ) );
		assertEquals( "..    ..", TextUtil.justify( TextUtil.CENTER, "", 8, '.', 2 ) );
		assertEquals( ".  X  ..", TextUtil.justify( TextUtil.CENTER, "X", 8, '.', 2 ) );
		assertEquals( ".  XX  .", TextUtil.justify( TextUtil.CENTER, "XX", 8, '.', 2 ) );
		assertEquals( "......  ", TextUtil.justify( TextUtil.RIGHT, "", 8, '.', 2 ) );
		assertEquals( ".....  X", TextUtil.justify( TextUtil.RIGHT, "X", 8, '.', 2 ) );
	}

	@Test
	public void testPad() {
		assertEquals( "", TextUtil.pad( -1 ) );
		assertEquals( "", TextUtil.pad( 0 ) );
		assertEquals( " ", TextUtil.pad( 1 ) );
		assertEquals( "     ", TextUtil.pad( 5 ) );
		assertEquals( "        ", TextUtil.pad( 8 ) );
	}

	@Test
	public void testPadWithChar() {
		assertEquals( "", TextUtil.pad( -1, '.' ) );
		assertEquals( "", TextUtil.pad( 0, '.' ) );
		assertEquals( "x", TextUtil.pad( 1, 'x' ) );
		assertEquals( ",,,,,", TextUtil.pad( 5, ',' ) );
		assertEquals( "--------", TextUtil.pad( 8, '-' ) );
	}

	@Test
	public void testGetLines() {
		String test = "This\nis\na\ntest.";
		List<String> lines = TextUtil.getLines( test );

		assertEquals( "This", lines.get( 0 ) );
		assertEquals( "is", lines.get( 1 ) );
		assertEquals( "a", lines.get( 2 ) );
		assertEquals( "test.", lines.get( 3 ) );
	}

	@Test
	public void testGetLineCount() {
		assertEquals( 0, TextUtil.getLineCount( null ) );
		assertEquals( 1, TextUtil.getLineCount( "" ) );
		assertEquals( 1, TextUtil.getLineCount( " " ) );
		assertEquals( 2, TextUtil.getLineCount( " \n " ) );
		assertEquals( 2, TextUtil.getLineCount( " \r " ) );
		assertEquals( 2, TextUtil.getLineCount( " \r\n " ) );
	}

	@Test
	public void testCountLines() {
		List<String> lines = new ArrayList<>();
		for( int index = 0; index < 5; index++ ) {
			lines.add( "Test line " + index );
		}

		assertEquals( 0, TextUtil.countLines( lines, "Test line" ) );
		assertEquals( 1, TextUtil.countLines( lines, "Test line 1" ) );
		assertEquals( 5, TextUtil.countLines( lines, "Test line ." ) );
		assertEquals( 2, TextUtil.countLines( lines, "Test .* [3,4]" ) );
		assertEquals( 1, TextUtil.countLines( lines, ".*4" ) );
	}

	@Test
	public void testFindLine() {
		List<String> lines = new ArrayList<>();
		for( int index = 0; index < 5; index++ ) {
			lines.add( "Test line " + index );
		}

		assertEquals( null, TextUtil.findLine( lines, "Test line" ) );
		assertEquals( "Test line 1", TextUtil.findLine( lines, "Test line 1" ) );
		assertEquals( "Test line 2", TextUtil.findLine( lines, "Test line .*", 2 ) );
		assertEquals( "Test line 3", TextUtil.findLine( lines, "Test .* 3" ) );
		assertEquals( "Test line 2", TextUtil.findLine( lines, "Test line .*", 2 ) );
		assertEquals( "Test line 4", TextUtil.findLine( lines, ".*4" ) );
	}

	@Test
	public void testFindLines() {
		List<String> lines = new ArrayList<>();
		for( int index = 0; index < 5; index++ ) {
			lines.add( "Test line " + index );
		}

		List<String> result = null;

		result = TextUtil.findLines( lines, "Test line" );
		assertEquals( 0, result.size() );

		result = TextUtil.findLines( lines, "Test line ." );
		assertEquals( 5, result.size() );

		result = TextUtil.findLines( lines, "Test line 1" );
		assertEquals( 1, result.size() );

		result = TextUtil.findLines( lines, "Test line [2,3]" );
		assertEquals( 2, result.size() );

		result = TextUtil.findLines( lines, ".*4" );
		assertEquals( 1, result.size() );
	}

	@Test
	public void testFindLineIndex() {
		List<String> lines = new ArrayList<>();
		for( int index = 0; index < 5; index++ ) {
			lines.add( "Test line " + index );
		}

		assertEquals( -1, TextUtil.findLineIndex( lines, "Test line" ) );
		assertEquals( 1, TextUtil.findLineIndex( lines, "Test line 1" ) );
		assertEquals( 2, TextUtil.findLineIndex( lines, "Test line .*", 2 ) );
		assertEquals( 3, TextUtil.findLineIndex( lines, "Test .* 3" ) );
		assertEquals( 2, TextUtil.findLineIndex( lines, "Test line .*", 2 ) );
		assertEquals( 4, TextUtil.findLineIndex( lines, ".*4" ) );
	}

	@Test
	public void testPrepend() {
		assertEquals( null, TextUtil.prepend( null, "X" ) );
		assertEquals( "A", TextUtil.prepend( "A", null ) );
		assertEquals( "B", TextUtil.prepend( "B", "" ) );

		assertEquals( "XC", TextUtil.prepend( "C", "X" ) );
		assertEquals( "XD\nXE", TextUtil.prepend( "D\nE", "X" ) );
		assertEquals( "XF\nXG\nX", TextUtil.prepend( "F\nG\n", "X" ) );
	}

	@Test
	public void testAppend() {
		assertEquals( null, TextUtil.append( null, "X" ) );
		assertEquals( "A", TextUtil.append( "A", null ) );
		assertEquals( "B", TextUtil.append( "B", "" ) );

		assertEquals( "CX", TextUtil.append( "C", "X" ) );
		assertEquals( "DX\nEX", TextUtil.append( "D\nE", "X" ) );
		assertEquals( "FX\nGX\nX", TextUtil.append( "F\nG\n", "X" ) );
	}

	@Test
	public void testReline() {
		int length = 40;

		assertEquals( null, TextUtil.reline( null, length ) );

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

	@Test
	public void testArrayToString() {
		Integer[] array = new Integer[ 5 ];

		array[ 0 ] = 0;
		array[ 1 ] = 1;
		array[ 2 ] = 2;
		array[ 3 ] = 3;
		array[ 4 ] = 4;

		assertEquals( "[0] [1] [2] [3] [4]", TextUtil.toString( array ) );
	}

	@Test
	public void testArrayToStringWithOffset() {
		Integer[] array = new Integer[ 5 ];

		array[ 0 ] = 0;
		array[ 1 ] = 1;
		array[ 2 ] = 2;
		array[ 3 ] = 3;
		array[ 4 ] = 4;

		assertEquals( "[2] [3] [4]", TextUtil.toString( array, 2 ) );
	}

	@Test
	public void testArrayToStringWithOffsetAndLength() {
		Integer[] array = new Integer[ 5 ];

		array[ 0 ] = 0;
		array[ 1 ] = 1;
		array[ 2 ] = 2;
		array[ 3 ] = 3;
		array[ 4 ] = 4;

		assertEquals( "[1] [2] [3]", TextUtil.toString( array, 1, 3 ) );
	}

	@Test
	public void testArrayToStringWithDelimiter() {
		Integer[] array = new Integer[ 5 ];

		array[ 0 ] = 0;
		array[ 1 ] = 1;
		array[ 2 ] = 2;
		array[ 3 ] = 3;
		array[ 4 ] = 4;

		assertEquals( "0_1_2_3_4", TextUtil.toString( array, "_" ) );
	}

	@Test
	public void testArrayToStringWithDelimiterAndOffset() {
		Integer[] array = new Integer[ 5 ];

		array[ 0 ] = 0;
		array[ 1 ] = 1;
		array[ 2 ] = 2;
		array[ 3 ] = 3;
		array[ 4 ] = 4;

		assertEquals( "2_3_4", TextUtil.toString( array, "_", 2 ) );
	}

	@Test
	public void testArrayToStringWithDelimiterLengthAndOffset() {
		Integer[] array = new Integer[ 5 ];

		array[ 0 ] = 0;
		array[ 1 ] = 1;
		array[ 2 ] = 2;
		array[ 3 ] = 3;
		array[ 4 ] = 4;

		assertEquals( "1_2_3", TextUtil.toString( array, "_", 1, 3 ) );
	}

	@Test
	public void testListToString() {
		List<Integer> list = new ArrayList<Integer>();

		list.add( 0 );
		list.add( 1 );
		list.add( 2 );
		list.add( 3 );
		list.add( 4 );

		assertEquals( "[0] [1] [2] [3] [4]", TextUtil.toString( list ) );
	}

	@Test
	public void testListToStringWithDelimiter() {
		List<Integer> list = new ArrayList<Integer>();

		list.add( 0 );
		list.add( 1 );
		list.add( 2 );
		list.add( 3 );
		list.add( 4 );

		assertEquals( "0 1 2 3 4", TextUtil.toString( list, " " ) );
	}

	@Test
	public void testListToStringWithPrefixAndSuffix() {
		List<Integer> list = new ArrayList<Integer>();

		list.add( 0 );
		list.add( 1 );
		list.add( 2 );
		list.add( 3 );
		list.add( 4 );

		assertEquals( "<0> <1> <2> <3> <4>", TextUtil.toString( list, "<", ">" ) );
	}

}
