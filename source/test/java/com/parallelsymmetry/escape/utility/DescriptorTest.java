package com.parallelsymmetry.escape.utility;

import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class DescriptorTest extends TestCase {

	public void testConstructor() throws Exception {
		Descriptor descriptor = new Descriptor();
		assertNotNull( descriptor );

		assertNotNull( descriptor.getPaths() );
		assertEquals( 0, descriptor.getPaths().size() );
	}

	public void testConstructorWithNullNode() throws Exception {
		Descriptor descriptor = new Descriptor( (Node)null );
		assertNotNull( descriptor );

		assertNotNull( descriptor.getPaths() );
		assertEquals( 0, descriptor.getPaths().size() );
	}

	public void testConstructorWithNullStream() throws Exception {
		Descriptor descriptor = new Descriptor( (InputStream)null );
		assertNotNull( descriptor );

		assertNotNull( descriptor.getPaths() );
		assertEquals( 0, descriptor.getPaths().size() );
	}

	public void testConstructorWithNode() throws Exception {
		InputStream input = DescriptorTest.class.getResourceAsStream( "/test.descriptor.xml" );
		assertNotNull( input );
		Descriptor descriptor = new Descriptor( input );
		assertNotNull( descriptor );

		Descriptor descriptor2 = new Descriptor( descriptor.getNode( "/test" ) );
		assertNotNull( descriptor2 );
		assertEquals( "test.name", descriptor2.getValue( "name" ) );
	}

	public void testConstructorWithStream() throws Exception {
		InputStream input = DescriptorTest.class.getResourceAsStream( "/test.descriptor.xml" );
		assertNotNull( input );
		Descriptor descriptor = new Descriptor( input );
		assertNotNull( descriptor );
	}

	public void testGetDocument() throws Exception {
		InputStream input = DescriptorTest.class.getResourceAsStream( "/test.descriptor.xml" );
		Descriptor descriptor = new Descriptor( input );
		assertNotNull( descriptor.getDocument() );
	}

	public void testGetPathsWithEmptyDescriptor() throws Exception {
		Descriptor descriptor = new Descriptor();

		List<String> paths = descriptor.getPaths();
		assertNotNull( paths );
		assertEquals( 0, paths.size() );
	}

	public void testGetValueWithEmptyDescriptor() throws Exception {
		Descriptor descriptor = new Descriptor();
		assertEquals( null, descriptor.getValue( null ) );
		assertEquals( null, descriptor.getValue( "" ) );
		assertEquals( null, descriptor.getValue( "test/name" ) );
	}

	public void testGetValueWithDefaultWithEmptyDescriptor() throws Exception {
		Descriptor descriptor = new Descriptor();
		assertEquals( null, descriptor.getValue( (String)null, null ) );
		assertEquals( "default", descriptor.getValue( (String)null, "default" ) );
		assertEquals( null, descriptor.getValue( "test/name", null ) );
		assertEquals( null, descriptor.getValue( "", null ) );
		assertEquals( null, descriptor.getValue( "notfound", null ) );
		assertEquals( "default", descriptor.getValue( "notfound", "default" ) );
	}

	public void testGetNames() throws Exception {
		InputStream input = DescriptorTest.class.getResourceAsStream( "/test.descriptor.xml" );
		assertNotNull( input );
		Descriptor descriptor = new Descriptor( input );

		List<String> names = descriptor.getNames( "/test" );
		assertEquals( 6, names.size() );
		assertTrue( names.contains( "name" ) );
		assertTrue( names.contains( "alias" ) );
		assertTrue( names.contains( "path" ) );
		assertTrue( names.contains( "integer" ) );
		assertTrue( names.contains( "list" ) );
		assertTrue( names.contains( "nodes" ) );
	}

	public void testGetPaths() throws Exception {
		InputStream input = DescriptorTest.class.getResourceAsStream( "/test.descriptor.xml" );
		assertNotNull( input );
		Descriptor descriptor = new Descriptor( input );

		List<String> paths = descriptor.getPaths();
		assertNotNull( paths );
		assertEquals( 9, paths.size() );
		assertEquals( "/test/name", paths.get( 0 ) );
		assertEquals( "/test/alias", paths.get( 1 ) );
		assertEquals( "/test/path/value", paths.get( 2 ) );
		assertEquals( "/test/integer", paths.get( 3 ) );
	}

	public void testGetNode() throws Exception {
		InputStream input = DescriptorTest.class.getResourceAsStream( "/test.descriptor.xml" );
		assertNotNull( input );
		Descriptor descriptor = new Descriptor( input );

		Document document = descriptor.getDocument();

		assertEquals( null, descriptor.getNode( null ) );
		assertEquals( null, descriptor.getNode( "" ) );
		assertEquals( document, descriptor.getNode( "/" ) );
		assertEquals( document.getDocumentElement(), descriptor.getNode( "/test" ) );
	}

	public void testGetNodes() throws Exception {
		InputStream input = DescriptorTest.class.getResourceAsStream( "/test.descriptor.xml" );
		assertNotNull( input );
		Descriptor descriptor = new Descriptor( input );

		assertEquals( null, descriptor.getNodes( null ) );
		assertEquals( null, descriptor.getNodes( "" ) );

		Node[] values = descriptor.getNodes( "/test/nodes/node" );
		assertEquals( "one", values[0].getTextContent() );
		assertEquals( "two", values[1].getTextContent() );
		assertEquals( "three", values[2].getTextContent() );
	}

	public void testGetValue() throws Exception {
		InputStream input = DescriptorTest.class.getResourceAsStream( "/test.descriptor.xml" );
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
		InputStream input = DescriptorTest.class.getResourceAsStream( "/test.descriptor.xml" );
		assertNotNull( input );
		Descriptor descriptor = new Descriptor( input );
		assertEquals( null, descriptor.getValue( (String)null, null ) );
		assertEquals( "default", descriptor.getValue( (String)null, "default" ) );
		assertEquals( "test.name", descriptor.getValue( "test/name", null ) );
		assertEquals( null, descriptor.getValue( "", null ) );
		assertEquals( null, descriptor.getValue( "notfound", null ) );
		assertEquals( "default", descriptor.getValue( "notfound", "default" ) );
	}

	public void testGetValues() throws Exception {
		InputStream input = DescriptorTest.class.getResourceAsStream( "/test.descriptor.xml" );
		assertNotNull( input );
		Descriptor descriptor = new Descriptor( input );

		assertEquals( null, descriptor.getValues( null ) );
		assertEquals( null, descriptor.getValues( "" ) );

		String[] values = descriptor.getValues( "/test/nodes/node" );
		assertEquals( 3, values.length );
		assertEquals( "one", values[0] );
		assertEquals( "two", values[1] );
		assertEquals( "three", values[2] );
	}

}
