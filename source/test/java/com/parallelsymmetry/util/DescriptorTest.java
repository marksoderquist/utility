package com.parallelsymmetry.util;

import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

public class DescriptorTest extends TestCase {

	public void testConstructor() throws Exception {
		InputStream input = getClass().getResourceAsStream( "descriptor.test.xml" );
		assertNotNull( input );
		Descriptor descriptor = new Descriptor( input );
		assertNotNull( descriptor );
	}

	public void testGetPaths() throws Exception {
		InputStream input = getClass().getResourceAsStream( "descriptor.test.xml" );
		assertNotNull( input );
		Descriptor descriptor = new Descriptor( input );

		List<String> paths = descriptor.getPaths();
		assertNotNull( paths );
		assertEquals( 4, paths.size() );
		assertEquals( "/test/name", paths.get( 0 ) );
		assertEquals( "/test/alias", paths.get( 1 ) );
		assertEquals( "/test/path/value", paths.get( 2 ) );
		assertEquals( "/test/integer", paths.get( 3 ) );
	}

	public void testGetValue() throws Exception {
		InputStream input = getClass().getResourceAsStream( "descriptor.test.xml" );
		assertNotNull( input );
		Descriptor descriptor = new Descriptor( input );
		assertEquals( null, descriptor.getValue( null ) );
		assertEquals( null, descriptor.getValue( "" ) );
		assertEquals( "test.name", descriptor.getValue( "test/name" ) );
		assertEquals( "test.alias", descriptor.getValue( "test/alias" ) );
		assertEquals( "test.path.value", descriptor.getValue( "test/path/value" ) );
		assertEquals( null, descriptor.getValue( "notfound" ) );
	}

	public void testGetValueWithDefault() throws Exception {
		InputStream input = getClass().getResourceAsStream( "descriptor.test.xml" );
		assertNotNull( input );
		Descriptor descriptor = new Descriptor( input );
		assertEquals( null, descriptor.getValue( null, null ) );
		assertEquals( "default", descriptor.getValue( null, "default" ) );
		assertEquals( "test.name", descriptor.getValue( "test/name", null ) );
		assertEquals( null, descriptor.getValue( "", null ) );
		assertEquals( null, descriptor.getValue( "notfound", null ) );
		assertEquals( "default", descriptor.getValue( "notfound", "default" ) );
	}

}
