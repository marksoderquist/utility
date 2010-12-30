package com.parallelsymmetry.escape.utility;

import java.util.Date;

import junit.framework.TestCase;

public class ReleaseTest extends TestCase {

	private Release release = new Release( Version.parse( "1.2.3-u-04" ), new Date( 0 ) );

	public void testGetVersion() {
		assertEquals( 0, Version.parse( "1.2.3-u-04" ).compareTo( release.getVersion() ) );
	}

	public void testGetDate() {
		assertEquals( new Date( 0 ), release.getDate() );
	}

	public void testToHumanString() {
		assertEquals( "1.2.3 Update 4  1970-01-01 00:00:00", release.toHumanString() );
	}

}
