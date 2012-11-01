package com.parallelsymmetry.escape.utility;

import java.net.URI;

import junit.framework.TestCase;

public class UriUtilTest extends TestCase {

	public void testResolve() {
		URI base = URI.create( "file:/test/folder/" );
		URI absolute = URI.create( "file:/test/folder/absolute" );
		URI relative = URI.create( "relative" );
		URI jar = URI.create( "jar:file:/test/folder%20with%20spaces/file.jar!/path/to/resource" );

		assertEquals( null, UriUtil.resolve( null, null ) );
		assertEquals( null, UriUtil.resolve( base, null ) );
		assertEquals( URI.create( "relative" ), UriUtil.resolve( null, relative ) );
		assertEquals( URI.create( "file:/test/folder/absolute" ), UriUtil.resolve( null, absolute ) );

		assertEquals( URI.create( "file:/test/folder/absolute" ), UriUtil.resolve( base, absolute ) );
		assertEquals( URI.create( "file:/test/folder/relative" ), UriUtil.resolve( base, relative ) );
		assertEquals( URI.create( "file:/test/folder/relative" ), UriUtil.resolve( absolute, relative ) );

		assertEquals( URI.create( "jar:file:/test/folder%20with%20spaces/file.jar!/path/to/relative" ), UriUtil.resolve( jar, relative ) );
	}

	public void testGetParent() {
		URI absolute = URI.create( "file:/test/folder/absolute" );
		URI opaque = URI.create( "jar:" + absolute.toString() );
		URI doubleOpaque = URI.create( "double:jar:" + absolute.toString() );
		
		assertEquals( "file:/test/folder/", UriUtil.getParent( absolute ).toString() );
		assertEquals( "jar:file:/test/folder/", UriUtil.getParent( opaque ).toString() );
		assertEquals( "double:jar:file:/test/folder/", UriUtil.getParent( doubleOpaque ).toString() );
	}

}
