package com.parallelsymmetry.escape.utility;

import java.util.Date;

import junit.framework.TestCase;

public class ReleaseTest extends TestCase {

	private String versionString = "1.2.3-u-04";

	private String timestampString = "1970-01-01 00:00:00";

	public void testConstructorWithStringString() throws Exception {
		Release release = new Release( versionString, timestampString );
		assertEquals( versionString, release.getVersion().toString() );
		assertEquals( new Date( 0 ), release.getDate() );
	}

	public void testConstructorWithStringStringAndBadTimestamp() throws Exception {
		Release release = new Release( versionString, "bad timestamp string" );
		assertEquals( versionString, release.getVersion().toString() );
		assertEquals( null, release.getDate() );
	}

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

	public void testEquals() {
		assertTrue( new Release( versionString ).equals( new Release( versionString ) ) );
		assertFalse( new Release( "1.2.3 Update 04" ).equals( new Release( versionString ) ) );

		assertTrue( new Release( new Version( "1" ) ).equals( new Release( "1" ) ) );
		assertFalse( new Release( new Version( "1" ) ).equals( new Release( "2" ) ) );
		assertTrue( new Release( new Version( "1" ), new Date( 0 ) ).equals( new Release( "1", new Date( 0 ) ) ) );
		assertFalse( new Release( new Version( "1" ), new Date( 0 ) ).equals( new Release( "1", new Date( 1 ) ) ) );
	}

	public void testCompareTo() {
		assertTrue( new Release( "1" ).compareTo( new Release( "1" ) ) == 0 );
		assertTrue( new Release( "1" ).compareTo( new Release( "2" ) ) < 0 );
		assertTrue( new Release( "2" ).compareTo( new Release( "1" ) ) > 0 );

		assertTrue( new Release( new Version( "1" ), new Date( 0 ) ).compareTo( new Release( new Version( "1" ), new Date( 0 ) ) ) == 0 );
		assertTrue( new Release( new Version( "1" ), new Date( -1 ) ).compareTo( new Release( new Version( "1" ), new Date( 0 ) ) ) < 0 );
		assertTrue( new Release( new Version( "1" ), new Date( 1 ) ).compareTo( new Release( new Version( "1" ), new Date( 0 ) ) ) > 0 );

		assertTrue( new Release( new Version( "1" ), null ).compareTo( new Release( new Version( "1" ), null ) ) == 0 );
		assertTrue( new Release( new Version( "1" ), null ).compareTo( new Release( new Version( "1" ), new Date( 0 ) ) ) == 0 );
		assertTrue( new Release( new Version( "1" ), new Date( 0 ) ).compareTo( new Release( new Version( "1" ), null ) ) == 0 );
	}

}
