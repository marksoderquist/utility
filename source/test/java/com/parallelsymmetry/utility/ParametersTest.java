package com.parallelsymmetry.utility;

import java.io.File;
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
	public void testParseWithValueAndFile() throws Exception {
		Parameters parameters = Parameters.parse( new String[] { "-help", "topic", "test.txt" } );
		assertEquals( "topic", parameters.get( "help" ) );
		assertEquals( 1, parameters.getResources().size() );
		assertEquals( UriUtil.resolve( "test.txt" ).toString(), parameters.getResources().get( 0 ).toString() );
	}

	@Test
	public void testParseWithValueAndFiles() throws Exception {
		Parameters parameters = Parameters.parse( new String[] { "-help", "topic", "test1.txt", "test2.txt" } );
		assertEquals( "topic", parameters.get( "help" ) );
		assertEquals( 2, parameters.getResources().size() );
		assertEquals( UriUtil.resolve( "test1.txt" ).toString(), parameters.getResources().get( 0 ).toString() );
		assertEquals( UriUtil.resolve( "test2.txt" ).toString(), parameters.getResources().get( 1 ).toString() );
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
		assertEquals( "topic", parameters.get( "help" ) );
		assertEquals( 1, parameters.getResources().size() );
		assertEquals( UriUtil.resolve( "test.txt" ).toString(), parameters.getResources().get( 0 ).toString() );
	}

	@Test
	public void testParseWithUnknownFlagAndFile() throws Exception {
		try {
			Parameters.parse( new String[] { "-help", "topic", "-test", "test", "test.txt" }, "-help" );
			fail( "Unknown flags should cause an exception" );
		} catch( IllegalArgumentException exception ) {
			assertEquals( "Unknown command: -test", exception.getMessage() );
		}
	}

	@Test
	public void testParseWithFlag() throws Exception {
		String flag = "flag";
		String notaflag = "notaflag";
		String[] args = new String[] { "-" + flag };
		Parameters parameters = Parameters.parse( args );
		assertFalse( parameters.isTrue( notaflag ) );
		assertTrue( "Flag not set.", parameters.isTrue( flag ) );
	}

	@Test
	public void testParseWithFlags2() throws Exception {
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
		assertFalse( parameters.isTrue( notaflag ) );
		for( String flag : flags ) {
			assertTrue( "Flag not set.", parameters.isTrue( flag ) );
		}
	}

	public void testParseWithValue2() throws Exception {
		String key = "key";
		String value = "value";
		String notakey = "notakey";
		String[] args = new String[] { "-" + key, value };
		Parameters parameters = Parameters.parse( args );
		assertFalse( parameters.isTrue( notakey ) );
		assertEquals( "Value not set.", value, parameters.get( key ) );
	}

	@Test
	public void testParseWithValues() throws Exception {
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
		assertFalse( parameters.isTrue( notakey ) );
		for( int index = 0; index < count; index++ ) {
			assertEquals( "Value not set.", values.get( index ), parameters.get( keys.get( index ) ) );
		}
	}

	@Test
	public void testParseWithEscapedValues() throws Exception {
		Parameters parameters = Parameters.parse( new String[] { "--test", "go", "\\-help" } );
		assertTrue( parameters.isSet( "test" ) );
		assertFalse( parameters.isTrue( "test" ) );
		List<String> values = parameters.getValues( "test" );

		assertEquals( "go", values.get( 0 ) );
		assertEquals( "-help", values.get( 1 ) );
	}

	@Test
	public void testParseFlagsWithValue() throws Exception {
		String[] args = new String[] { "-flag1", "-key", "value", "-flag2" };
		Parameters parameters = Parameters.parse( args );
		assertTrue( "Flag 1 not set.", parameters.isTrue( "flag1" ) );
		assertEquals( "Value not set.", "value", parameters.get( "key" ) );
		assertTrue( "Flag 2 not set.", parameters.isTrue( "flag2" ) );
	}

	@Test
	public void testParseValuesWithFlag() throws Exception {
		String[] args = new String[] { "-key1", "value1", "-flag", "-key2", "value2" };
		Parameters parameters = Parameters.parse( args );
		assertEquals( "Value 1 not set.", "value1", parameters.get( "key1" ) );
		assertTrue( "Flag not set.", parameters.isTrue( "flag" ) );
		assertEquals( "Value 2 not set.", "value2", parameters.get( "key2" ) );
	}

	@Test
	public void testParseWithFile() throws Exception {
		String filename = "test.file";
		String[] args = new String[] { filename };
		Parameters parameters = Parameters.parse( args );
		List<String> resources = parameters.getResources();
		assertEquals( "Number of files incorrect.", 1, resources.size() );
		assertEquals( "File name incorrect.", UriUtil.resolve( filename ).toString(), resources.get( 0 ) );
	}

	@Test
	public void testParseWithFiles() throws Exception {
		int count = 5;
		List<String> list = new ArrayList<String>();
		for( int index = 0; index < count; index++ ) {
			list.add( "test" + index + ".file" );
		}

		String[] args = new String[list.size()];
		list.toArray( args );
		Parameters parameters = Parameters.parse( args );

		List<String> resources = parameters.getResources();
		assertEquals( "Number of files incorrect.", count, resources.size() );
		for( int index = 0; index < count; index++ ) {
			assertEquals( "File name incorrect.", UriUtil.resolve( "test" + index + ".file" ).toString(), resources.get( index ) );
		}
	}

	@Test
	public void testParseFlagWithFile() throws Exception {
		String filename = "file";
		String[] args = new String[] { "-flag", "--", filename };
		Parameters parameters = Parameters.parse( args );
		List<String> resources = parameters.getResources();

		assertTrue( "Flag not set.", parameters.isTrue( "flag" ) );
		assertEquals( "Number of files incorrect.", 1, resources.size() );
		assertEquals( "File name incorrect.", UriUtil.resolve( filename ).toString(), resources.get( 0 ) );
	}

	@Test
	public void testParseWithNullEntry() throws Exception {
		String[] args = new String[1];
		try {
			Parameters.parse( args );
			fail( "Null values should cause an exception" );
		} catch( IllegalArgumentException exception ) {
			assertEquals( "Null command at index: 0", exception.getMessage() );
		}
	}

	@Test
	public void testGet() throws Exception {
		String[] args = new String[] { "-flag", "-key", "value", "file" };
		Parameters parameters = Parameters.parse( args );
		assertEquals( null, parameters.get( "none" ) );
		assertEquals( "true", parameters.get( "flag" ) );
		assertEquals( "value", parameters.get( "key" ) );

		assertEquals( null, parameters.get( "-none" ) );
		assertEquals( "true", parameters.get( "-flag" ) );
		assertEquals( "value", parameters.get( "-key" ) );

		assertEquals( null, parameters.get( "--none" ) );
		assertEquals( "true", parameters.get( "--flag" ) );
		assertEquals( "value", parameters.get( "--key" ) );
	}

	@Test
	public void testGetWithDefault() throws Exception {
		String[] args = new String[] { "-flag", "-key", "value", "file" };
		Parameters parameters = Parameters.parse( args );

		assertEquals( "true", parameters.get( "none", "true" ) );
		assertEquals( "true", parameters.get( "flag", "false" ) );
		assertEquals( "value", parameters.get( "key", null ) );
	}

	@Test
	public void testGetValues() throws Exception {
		String[] args = new String[] { "--flag", "value0", "value1", "value2" };
		Parameters parameters = Parameters.parse( args );

		assertEquals( "value0", parameters.getValues( "flag" ).get( 0 ) );
		assertEquals( "value1", parameters.getValues( "flag" ).get( 1 ) );
		assertEquals( "value2", parameters.getValues( "flag" ).get( 2 ) );

		assertEquals( "value0", parameters.getValues( "-flag" ).get( 0 ) );
		assertEquals( "value1", parameters.getValues( "-flag" ).get( 1 ) );
		assertEquals( "value2", parameters.getValues( "-flag" ).get( 2 ) );

		assertEquals( "value0", parameters.getValues( "--flag" ).get( 0 ) );
		assertEquals( "value1", parameters.getValues( "--flag" ).get( 1 ) );
		assertEquals( "value2", parameters.getValues( "--flag" ).get( 2 ) );
	}

	@Test
	public void testGetValuesWithFlags() throws Exception {
		String[] args = new String[] { "--flag", "value0", "value1", "value2", "-other" };
		Parameters parameters = Parameters.parse( args );

		assertEquals( "value0", parameters.getValues( "flag" ).get( 0 ) );
		assertEquals( "value1", parameters.getValues( "flag" ).get( 1 ) );
		assertEquals( "value2", parameters.getValues( "flag" ).get( 2 ) );

		assertTrue( parameters.isSet( "other" ) );
		assertTrue( parameters.isTrue( "other" ) );
	}

	@Test
	public void testGetValuesWithFiles() throws Exception {
		String[] args = new String[] { "--flag", "value0", "value1", "value2", "--", "file1.txt" };
		Parameters parameters = Parameters.parse( args );

		assertEquals( "value0", parameters.getValues( "flag" ).get( 0 ) );
		assertEquals( "value1", parameters.getValues( "flag" ).get( 1 ) );
		assertEquals( "value2", parameters.getValues( "flag" ).get( 2 ) );

		assertEquals( UriUtil.resolve( "file1.txt" ).toString(), parameters.getResources().get( 0 ) );
	}

	@Test
	public void testGetValuesWithFlagsAndFiles() throws Exception {
		String[] args = new String[] { "--flag", "value0", "value1", "value2", "-other", "test", "file1.txt" };
		Parameters parameters = Parameters.parse( args );

		assertEquals( "value0", parameters.getValues( "flag" ).get( 0 ) );
		assertEquals( "value1", parameters.getValues( "flag" ).get( 1 ) );
		assertEquals( "value2", parameters.getValues( "flag" ).get( 2 ) );

		assertTrue( parameters.isSet( "other" ) );
		assertFalse( parameters.isTrue( "other" ) );

		assertEquals( UriUtil.resolve( "file1.txt" ).toString(), parameters.getResources().get( 0 ) );
	}

	@Test
	public void testIsSet() throws Exception {
		String[] args = new String[] { "-flag1", "false", "-flag2", "true", "-flag3" };
		Parameters parameters = Parameters.parse( args );
		assertFalse( parameters.isSet( "flag0" ) );
		assertFalse( parameters.isTrue( "flag0" ) );

		assertTrue( parameters.isSet( "flag1" ) );
		assertFalse( parameters.isTrue( "flag1" ) );

		assertTrue( parameters.isSet( "flag2" ) );
		assertTrue( parameters.isTrue( "flag2" ) );

		assertTrue( parameters.isSet( "flag3" ) );
		assertTrue( parameters.isTrue( "flag3" ) );

		assertFalse( parameters.isSet( "-flag0" ) );
		assertFalse( parameters.isTrue( "-flag0" ) );

		assertTrue( parameters.isSet( "-flag1" ) );
		assertFalse( parameters.isTrue( "-flag1" ) );

		assertTrue( parameters.isSet( "-flag2" ) );
		assertTrue( parameters.isTrue( "-flag2" ) );

		assertTrue( parameters.isSet( "-flag3" ) );
		assertTrue( parameters.isTrue( "-flag3" ) );

		assertFalse( parameters.isSet( "--flag0" ) );
		assertFalse( parameters.isTrue( "--flag0" ) );

		assertTrue( parameters.isSet( "--flag1" ) );
		assertFalse( parameters.isTrue( "--flag1" ) );

		assertTrue( parameters.isSet( "--flag2" ) );
		assertTrue( parameters.isTrue( "--flag2" ) );

		assertTrue( parameters.isSet( "--flag3" ) );
		assertTrue( parameters.isTrue( "--flag3" ) );
	}

	@Test
	public void testGetCommands() throws Exception {
		String[] args = new String[] { "-flag", "-key", "value", "file" };
		Parameters parameters = Parameters.parse( args );
		String[] commands = parameters.getCommands();

		assertNotSame( "Commands are same object.", args, parameters.getCommands() );

		int index = 0;
		assertEquals( "-flag", commands[index++] );
		assertEquals( "-key", commands[index++] );
		assertEquals( "value", commands[index++] );
		assertEquals( new File( "file" ).toURI().toString(), commands[index++] );
	}

	@Test
	public void testIdempotentParse() throws Exception {
		String[] commands = new String[] { "-flag", "-key", "value", "file" };
		Parameters parameters1 = Parameters.parse( commands );
		Parameters parameters2 = Parameters.parse( parameters1.getCommands() );

		assertEquals( parameters1, parameters2 );
	}

	@Test
	public void testHashCode() {
		String[] commands = new String[] { "-flag", "-key", "value", "file" };
		Parameters parameters1 = Parameters.parse( commands );
		Parameters parameters2 = Parameters.parse( commands );

		assertEquals( parameters1.hashCode(), parameters2.hashCode() );
	}

	@Test
	public void testEquals() {
		String[] commands = new String[] { "-flag", "-key", "value", "file" };
		Parameters parameters1 = Parameters.parse( commands );
		Parameters parameters2 = Parameters.parse( commands );
		Parameters parameters3 = Parameters.parse( new String[] { "-flag", "-key", "value", "otherfile" } );

		assertTrue( parameters1.equals( parameters2 ) );
		assertTrue( parameters2.equals( parameters1 ) );
		assertFalse( parameters1.equals( parameters3 ) );
		assertFalse( parameters3.equals( parameters1 ) );
	}

}
