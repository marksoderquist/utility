package com.parallelsymmetry.escape.utility;

import java.util.Date;

import junit.framework.TestCase;

public class ReleaseTest extends TestCase {

	private String versionString = "1.2.3-u-04";

	public void testGetVersion() {
		Release release = new Release( versionString );
		assertEquals( 0, new Version( "1.2.3-u-04" ).compareTo( release.getVersion() ) );
	}

	public void testGetDate() {
		assertNull( new Release( versionString ).getDate() );
		assertEquals( new Date( 0 ), new Release( new Version( versionString ), new Date( 0 ) ).getDate() );
	}

	public void testToString() {
		assertEquals( "1.2.3-u-04", new Release( versionString ).toString() );
		assertEquals( "1.2.3-u-04  1970-01-01 00:00:00", new Release( new Version( versionString ), new Date( 0 ) ).toString() );
	}

	public void testToHumanString() {
		assertEquals( "1.2.3 Update 04", new Release( versionString ).toHumanString() );
		assertEquals( "1.2.3 Update 04  1970-01-01 00:00:00", new Release( new Version( versionString ), new Date( 0 ) ).toHumanString() );
	}

	public void testCompareTo() {
		assertTrue( new Release( "1" ).compareTo( new Release( "1" ) ) == 0 );
		assertTrue( new Release( "1" ).compareTo( new Release( "2" ) ) < 0 );
		assertTrue( new Release( "2" ).compareTo( new Release( "1" ) ) > 0 );

		assertTrue( new Release( new Version( "1" ), new Date( 0 ) ).compareTo( new Release( new Version( "1" ), new Date( 0 ) ) ) == 0 );
		assertTrue( new Release( new Version( "1" ), new Date( -1 ) ).compareTo( new Release( new Version( "1" ), new Date( 0 ) ) ) < 0 );
		assertTrue( new Release( new Version( "1" ), new Date( 1 ) ).compareTo( new Release( new Version( "1" ), new Date( 0 ) ) ) > 0 );
	}

}
