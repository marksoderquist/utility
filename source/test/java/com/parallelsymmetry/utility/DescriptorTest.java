package com.parallelsymmetry.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.parallelsymmetry.utility.Descriptor;

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
		Descriptor descriptor = null;
		try {
			descriptor = new Descriptor( (InputStream)null );
			fail( "Descriptor constructor should not allow null streams." );
		} catch( NullPointerException exception ) {
			// Intentionally ignore exception.
		}
		assertNull( descriptor );
	}

	public void testConstructorWithNode() throws Exception {
		Descriptor descriptor = loadTestDescriptor();
		Descriptor descriptor2 = new Descriptor( descriptor.getNode( "/test" ) );
		assertNotNull( descriptor2 );
		assertEquals( "test.name", descriptor2.getValue( "name" ) );
	}

	public void testConstructorWithStream() throws Exception {
		Descriptor descriptor = loadTestDescriptor();
		assertNotNull( descriptor );
	}

	public void testGetDocument() throws Exception {
		Descriptor descriptor = loadTestDescriptor();
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
		Descriptor descriptor = loadTestDescriptor();

		List<String> names = descriptor.getNames( "/test" );
		assertEquals( 8, names.size() );
		assertTrue( names.contains( "name" ) );
		assertTrue( names.contains( "alias" ) );
		assertTrue( names.contains( "path" ) );
		assertTrue( names.contains( "bounds" ) );
		assertTrue( names.contains( "integer" ) );
		assertTrue( names.contains( "list" ) );
		assertTrue( names.contains( "nodes" ) );

		names = descriptor.getNames( "/test/list" );
		assertTrue( names.contains( "one" ) );
		assertTrue( names.contains( "two" ) );
		assertTrue( names.contains( "three" ) );
	}

	public void testGetPaths() throws Exception {
		Descriptor descriptor = loadTestDescriptor();

		List<String> paths = descriptor.getPaths();
		assertNotNull( paths );
		assertEquals( 10, paths.size() );
		assertEquals( "/test/name", paths.get( 0 ) );
		assertEquals( "/test/alias", paths.get( 1 ) );
		assertEquals( "/test/path/value", paths.get( 2 ) );
		assertEquals( "/test/integer", paths.get( 3 ) );
		assertEquals( "/test/summary", paths.get( 9 ) );
	}

	public void testGetNode() throws Exception {
		Descriptor descriptor = loadTestDescriptor();

		Document document = descriptor.getDocument();

		assertEquals( null, descriptor.getNode( null ) );
		assertEquals( null, descriptor.getNode( "" ) );
		assertEquals( document, descriptor.getNode( "/" ) );
		assertEquals( document.getDocumentElement(), descriptor.getNode( "/test" ) );
	}

	public void testGetNodes() throws Exception {
		Descriptor descriptor = loadTestDescriptor();

		assertEquals( null, descriptor.getNodes( null ) );
		assertEquals( null, descriptor.getNodes( "" ) );

		Node[] values = descriptor.getNodes( "/test/nodes/node" );
		assertEquals( "one", values[0].getTextContent() );
		assertEquals( "two", values[1].getTextContent() );
		assertEquals( "three", values[2].getTextContent() );
	}

	public void testGetValue() throws Exception {
		Descriptor descriptor = loadTestDescriptor();

		assertEquals( null, descriptor.getValue( null ) );
		assertEquals( null, descriptor.getValue( "" ) );
		assertEquals( "test.name", descriptor.getValue( "test/name" ) );
		assertEquals( "test.alias", descriptor.getValue( "test/alias" ) );
		assertEquals( "test.path.value", descriptor.getValue( "test/path/value" ) );
		assertEquals( null, descriptor.getValue( "notfound" ) );
	}

	public void testGetValueWithDefault() throws Exception {
		Descriptor descriptor = loadTestDescriptor();

		assertEquals( null, descriptor.getValue( (String)null, null ) );
		assertEquals( "default", descriptor.getValue( (String)null, "default" ) );
		assertEquals( "test.name", descriptor.getValue( "test/name", null ) );
		assertEquals( null, descriptor.getValue( "", null ) );
		assertEquals( null, descriptor.getValue( "notfound", null ) );
		assertEquals( "default", descriptor.getValue( "notfound", "default" ) );
	}

	public void testGetMultilineValue() throws Exception {
		Descriptor descriptor = loadTestDescriptor();

		assertEquals( "This summary needs to span multiple line in order for the test to work correctly. Please ensure that this summary is wrapped roughly at characters per line so that there are three lines.", descriptor.getValue( "/test/summary" ) );
	}

	public void testGetValues() throws Exception {
		Descriptor descriptor = loadTestDescriptor();

		assertEquals( null, descriptor.getValues( null ) );
		assertEquals( null, descriptor.getValues( "" ) );

		String[] values = descriptor.getValues( "/test/nodes/node" );
		assertEquals( 3, values.length );
		assertEquals( "one", values[0] );
		assertEquals( "two", values[1] );
		assertEquals( "three", values[2] );
	}

	public void testGetMultilineValues() throws Exception {
		Descriptor descriptor = loadTestDescriptor();

		assertEquals( null, descriptor.getValues( null ) );
		assertEquals( null, descriptor.getValues( "" ) );

		String[] values = descriptor.getValues( "/test/summary" );
		assertEquals( 1, values.length );
		assertEquals( "This summary needs to span multiple line in order for the test to work correctly. Please ensure that this summary is wrapped roughly at characters per line so that there are three lines.", values[0] );
	}

	public void testGetAttributeValueFromSubDescriptor() throws Exception {
		Descriptor descriptor = loadTestDescriptor();
		Descriptor subDescriptor = new Descriptor( descriptor.getNode( "/test/bounds" ) );

		assertEquals( "15", descriptor.getValue( "/test/bounds/@h" ) );
		assertEquals( "15", subDescriptor.getValue( "@h" ) );
	}

	private Descriptor loadTestDescriptor() throws IOException {
		InputStream input = DescriptorTest.class.getResourceAsStream( "/test.descriptor.xml" );
		assertNotNull( input );
		Descriptor descriptor = new Descriptor( input );
		assertNotNull( descriptor );
		return descriptor;
	}

}
