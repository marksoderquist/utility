package com.parallelsymmetry.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class ParametersTest extends TestCase {

	@Test
	public void testNull() throws Exception {
		try {
			Parameters.parse( null );
			fail( "Parameters.parse(null) should throw a NullPointerException." );
		} catch( NullPointerException exception ) {
			// 
		}
	}

	@Test
	public void testEmpty() throws Exception {
		Parameters parameters = Parameters.parse( new String[] {} );
		assertEquals( 0, parameters.size() );
	}

	@Test
	public void testParse() throws Exception {
		Parameters parameters = Parameters.parse( new String[] { "-help" } );
		assertEquals( "true", parameters.get( "help" ) );
	}

	@Test
	public void testParseWithValue() throws Exception {
		Parameters parameters = Parameters.parse( new String[] { "-help", "topic" } );
		assertEquals( "topic", parameters.get( "help" ) );
	}

	@Test
	public void testParseWithValueUsingEquals() throws Exception {
		Parameters parameters = Parameters.parse( new String[] { "-locale=spa" } );
		assertEquals( "spa", parameters.get( "locale" ) );
	}

	@Test
	public void testParseWithValueAndFile() throws Exception {
		Parameters parameters = Parameters.parse( new String[] { "-help", "topic", "test.txt" } );
		assertEquals( "topic", parameters.get( "help" ) );
		assertEquals( 1, parameters.getFiles().size() );
		assertEquals( "test.txt", parameters.getFiles().get( 0 ).toString() );
	}

	@Test
	public void testParseWithValueAndFiles() throws Exception {
		Parameters parameters = Parameters.parse( new String[] { "-help", "topic", "test1.txt", "test2.txt" } );
		assertEquals( "topic", parameters.get( "help" ) );
		assertEquals( 2, parameters.getFiles().size() );
		assertEquals( "test1.txt", parameters.getFiles().get( 0 ).toString() );
		assertEquals( "test2.txt", parameters.getFiles().get( 1 ).toString() );
	}

	@Test
	public void testParseWithFlags() throws Exception {
		Parameters parameters = Parameters.parse( new String[] { "-one", "-two", "-three" } );
		assertEquals( "true", parameters.get( "one" ) );
		assertEquals( "true", parameters.get( "two" ) );
		assertEquals( "true", parameters.get( "three" ) );
	}

	@Test
	public void testParseWithFlagAndFile() throws Exception {
		Parameters parameters = Parameters.parse( new String[] { "-help", "topic", "test.txt" }, "-help" );
		assertEquals( "true", parameters.get( "help" ) );
		assertEquals( 2, parameters.getFiles().size() );
		assertEquals( "topic", parameters.getFiles().get( 0 ).toString() );
		assertEquals( "test.txt", parameters.getFiles().get( 1 ).toString() );
	}

	@Test
	public void testParseWithFlagAndParameterAndFile() throws Exception {
		Parameters parameters = Parameters.parse( new String[] { "-help", "topic", "-test", "test", "test.txt" }, "-help" );
		assertEquals( "true", parameters.get( "help" ) );
		assertEquals( "test", parameters.get( "test" ) );
		assertEquals( 2, parameters.getFiles().size() );
		assertEquals( "topic", parameters.getFiles().get( 0 ).toString() );
		assertEquals( "test.txt", parameters.getFiles().get( 1 ).toString() );
	}

	@Test
	public void testGetCommands() {
		String[] args = new String[] { "-flag", "-key", "value", "file" };
		Parameters parameters = Parameters.parse( args );
		assertNotSame( "Commands are same object.", args, parameters.getCommands() );
		assertEquals( "Commands not identical values.", sumHashCode( args ), sumHashCode( parameters.getCommands() ) );
	}

	@Test
	public void testParseWithFlag() {
		String flag = "flag";
		String notaflag = "notaflag";
		String[] args = new String[] { "-" + flag };
		Parameters parameters = Parameters.parse( args );
		assertFalse( parameters.isSet( notaflag ) );
		assertTrue( "Flag not set.", parameters.isSet( flag ) );
	}

	@Test
	public void testParseWithFlags2() {
		String notaflag = "notaflag";
		List<String> flags = new ArrayList<String>();
		flags.add( "flag1" );
		flags.add( "flag2" );
		flags.add( "flag3" );
		flags.add( "flag4" );
		flags.add( "flag5" );

		String[] args = new String[flags.size()];
		int count = args.length;
		for( int index = 0; index < count; index++ ) {
			args[index] = "-" + flags.get( index );
		}

		Parameters parameters = Parameters.parse( args );
		assertFalse( parameters.isSet( notaflag ) );
		for( String flag : flags ) {
			assertTrue( "Flag not set.", parameters.isSet( flag ) );
		}
	}

	public void testParseWithValue2() {
		String key = "key";
		String value = "value";
		String notakey = "notakey";
		String[] args = new String[] { "-" + key, value };
		Parameters parameters = Parameters.parse( args );
		assertFalse( parameters.isSet( notakey ) );
		assertEquals( "Value not set.", value, parameters.get( key ) );
	}

	@Test
	public void testParseWithValues() {
		int count = 5;
		String notakey = "notakey";
		List<String> keys = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		for( int index = 0; index < count; index++ ) {
			keys.add( "key" + index );
			values.add( "value" + index );
		}

		String[] args = new String[keys.size() + values.size()];
		for( int index = 0; index < count; index++ ) {
			args[index * 2] = "-" + keys.get( index );
			args[index * 2 + 1] = values.get( index );
		}

		Parameters parameters = Parameters.parse( args );
		assertFalse( parameters.isSet( notakey ) );
		for( int index = 0; index < count; index++ ) {
			assertEquals( "Value not set.", values.get( index ), parameters.get( keys.get( index ) ) );
		}
	}

	@Test
	public void testParseFlagsWithValue() {
		String[] args = new String[] { "-flag1", "-key", "value", "-flag2" };
		Parameters parameters = Parameters.parse( args );
		assertTrue( "Flag 1 not set.", parameters.isSet( "flag1" ) );
		assertEquals( "Value not set.", "value", parameters.get( "key" ) );
		assertTrue( "Flag 2 not set.", parameters.isSet( "flag2" ) );
	}

	@Test
	public void testParseValuesWithFlag() {
		String[] args = new String[] { "-key1", "value1", "-flag", "-key2", "value2" };
		Parameters parameters = Parameters.parse( args );
		assertEquals( "Value 1 not set.", "value1", parameters.get( "key1" ) );
		assertTrue( "Flag not set.", parameters.isSet( "flag" ) );
		assertEquals( "Value 2 not set.", "value2", parameters.get( "key2" ) );
	}

	@Test
	public void testParseWithFile() {
		String filename = "test.file";
		String[] args = new String[] { filename };
		Parameters parameters = Parameters.parse( args );
		List<String> files = parameters.getFiles();
		assertEquals( "Number of files incorrect.", 1, files.size() );
		assertEquals( "File name incorrect.", filename, files.get( 0 ) );
	}

	@Test
	public void testParseWithFiles() {
		int count = 5;
		List<String> list = new ArrayList<String>();
		for( int index = 0; index < count; index++ ) {
			list.add( "test" + index + ".file" );
		}

		String[] args = new String[list.size()];
		list.toArray( args );
		Parameters parameters = Parameters.parse( args );

		List<String> files = parameters.getFiles();
		assertEquals( "Number of files incorrect.", count, files.size() );
		for( int index = 0; index < count; index++ ) {
			assertEquals( "File name incorrect.", "test" + index + ".file", files.get( index ) );
		}
	}

	@Test
	public void testParseFlagWithFile() {
		String[] args = new String[] { "-flag", "--", "file" };
		Parameters parameters = Parameters.parse( args );
		List<String> files = parameters.getFiles();

		assertTrue( "Flag not set.", parameters.isSet( "flag" ) );
		assertEquals( "Number of files incorrect.", 1, files.size() );
		assertEquals( "File name incorrect.", "file", files.get( 0 ) );
	}

	@Test
	public void testParseWithNullEntry() {
		String[] args = new String[1];
		Parameters parameters = Parameters.parse( args );
		assertEquals( 0, parameters.size() );
	}

	private long sumHashCode( Object[] objects ) {
		long value = 0;

		for( Object object : objects ) {
			value += object.hashCode();
		}

		return value;
	}

}
