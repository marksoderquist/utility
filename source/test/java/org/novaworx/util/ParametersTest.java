package org.novaworx.util;

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

}
