package com.parallelsymmetry.escape.utility;

import java.util.Date;

import junit.framework.TestCase;

public class ReleaseTest extends TestCase {
	
	private String versionString = "1.2.3-u-04";

	public void testGetVersion() {
		Release release = new Release( Version.parse( versionString ) );
		assertEquals( 0, Version.parse( "1.2.3-u-04" ).compareTo( release.getVersion() ) );
	}

	public void testGetDate() {
		assertNull( new Release( Version.parse( versionString ), null ).getDate() );
		assertEquals( new Date( 0 ), new Release( Version.parse( versionString ), new Date( 0 ) ).getDate() );
	}

	public void testToHumanString() {
		assertEquals( "1.2.3 Update 4", new Release( Version.parse( versionString ) ).toHumanString() );
		assertEquals( "1.2.3 Update 4  1970-01-01 00:00:00", new Release( Version.parse( versionString ), new Date( 0 ) ).toHumanString() );
	}

}
