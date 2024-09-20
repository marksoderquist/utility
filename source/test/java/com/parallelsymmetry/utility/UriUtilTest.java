package com.parallelsymmetry.utility;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UriUtilTest extends BaseTestCase {

	@Test
	public void testResolveWithString() throws Exception {
		assertEquals( new File( "" ).getCanonicalFile().toURI(), UriUtil.resolve( "" ) );
		assertEquals( new File( "." ).getCanonicalFile().toURI(), UriUtil.resolve( "." ) );
		assertEquals( new File( "test" ).getCanonicalFile().toURI(), UriUtil.resolve( "test" ) );
		assertEquals( new File( "/test" ).getCanonicalFile().toURI(), UriUtil.resolve( "/test" ) );

		assertEquals( URI.create( "ssh://localhost" ), UriUtil.resolve( "ssh://localhost" ) );
	}

	@Test
	public void testResolveWithRelativeUri() {
		URI base = URI.create( "file:/test/folder/" );
		URI absolute = URI.create( "file:/test/folder/absolute" );
		URI relative = URI.create( "relative" );
		URI jar = URI.create( "jar:file:/test/folder%20with%20spaces/file.jar!/path/to/resource" );

		assertNull( UriUtil.resolve( null, null ) );
		assertNull( UriUtil.resolve( base, null ) );
		assertEquals( URI.create( "relative" ), UriUtil.resolve( null, relative ) );
		assertEquals( URI.create( "file:/test/folder/absolute" ), UriUtil.resolve( null, absolute ) );

		assertEquals( URI.create( "file:/test/folder/absolute" ), UriUtil.resolve( base, absolute ) );
		assertEquals( URI.create( "file:/test/folder/relative" ), UriUtil.resolve( base, relative ) );
		assertEquals( URI.create( "file:/test/folder/relative" ), UriUtil.resolve( absolute, relative ) );

		assertEquals( URI.create( "jar:file:/test/folder%20with%20spaces/file.jar!/path/to/relative" ), UriUtil.resolve( jar, relative ) );

		URI icon = URI.create( "http://www.parallelsymmetry.com/images/icons/escape.png" );
		assertEquals( URI.create( "http://www.parallelsymmetry.com/images/icons/escape.png" ), UriUtil.resolve( jar, icon ) );
	}

	@Test
	public void testGetParent() {
		URI absolute = URI.create( "file:/test/folder/absolute" );
		URI opaque = URI.create( "jar:" + absolute.toString() );
		URI doubleOpaque = URI.create( "double:jar:" + absolute.toString() );

		assertEquals( "file:/test/folder/", UriUtil.getParent( absolute ).toString() );
		assertEquals( "jar:file:/test/folder/", UriUtil.getParent( opaque ).toString() );
		assertEquals( "double:jar:file:/test/folder/", UriUtil.getParent( doubleOpaque ).toString() );
	}

	@Test
	public void testParseQueryWithUri() {
		assertNull( UriUtil.parseQuery( (URI)null ) );

		URI uri = URI.create( "test:///path?attr1&attr2" );
		Map<String, String> parameters = UriUtil.parseQuery( uri );
		assertEquals( "true", parameters.get( "attr1" ) );
		assertEquals( "true", parameters.get( "attr2" ) );

		uri = URI.create( "test:///path?attr1=value1&attr2=value2" );
		parameters = UriUtil.parseQuery( uri );
		assertEquals( "value1", parameters.get( "attr1" ) );
		assertEquals( "value2", parameters.get( "attr2" ) );
	}

	@Test
	public void testParseQueryWithString() {
		assertNull( UriUtil.parseQuery( (String)null ) );

		Map<String, String> parameters = UriUtil.parseQuery( "attr1&attr2" );
		assertEquals( "true", parameters.get( "attr1" ) );
		assertEquals( "true", parameters.get( "attr2" ) );

		parameters = UriUtil.parseQuery( "attr1=value1&attr2=value2" );
		assertEquals( "value1", parameters.get( "attr1" ) );
		assertEquals( "value2", parameters.get( "attr2" ) );
	}

}
