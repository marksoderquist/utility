package com.parallelsymmetry.escape.utility;

import java.net.URI;

import junit.framework.TestCase;

public class UriUtilTest extends TestCase {

	public void testResolve() {
		URI base = URI.create( "file:///test/folder/" );
		URI absolute = URI.create( "file:///test/folder/absolute" );
		URI relative = URI.create( "relative" );

		assertEquals( null, UriUtil.resolve( null, null ) );
		assertEquals( null, UriUtil.resolve( base, null ) );
		assertEquals( URI.create( "relative" ), UriUtil.resolve( null, relative ) );
		assertEquals( URI.create( "file:///test/folder/absolute" ), UriUtil.resolve( null, absolute ) );
		
		assertEquals( URI.create( "file:///test/folder/absolute" ), UriUtil.resolve( base, absolute ) );
		assertEquals( URI.create( "file:///test/folder/relative" ), UriUtil.resolve( base, relative ) );
		assertEquals( URI.create( "file:///test/folder/relative" ), UriUtil.resolve( absolute, relative ) );
	}

}
