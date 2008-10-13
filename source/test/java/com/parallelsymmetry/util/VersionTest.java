package com.parallelsymmetry.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.junit.Test;

public class VersionTest extends TestCase {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

	static {
		DATE_FORMAT.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
	}

	@Test
	public void testParseWithNull() throws Exception {
		assertEquals( true, Version.parse( null ).isUnknown() );
		assertEquals( true, Version.parse( null, null ).isUnknown() );
	}

	@Test
	public void testParseWithEmpty() throws Exception {
		assertEquals( true, Version.parse( "" ).isUnknown() );
		assertEquals( true, Version.parse( "", null ).isUnknown() );
		assertEquals( true, Version.parse( null, "" ).isUnknown() );
		assertEquals( true, Version.parse( "", "" ).isUnknown() );
	}

	@Test
	public void testParseWithMajorOnly() throws Exception {
		Version version = Version.parse( "1" );
		assertEquals( 1, version.getMajor() );
		assertEquals( 0, version.getMinor() );
		assertEquals( null, version.getState() );
		assertEquals( 0, version.getMicro() );
		assertEquals( false, version.isSnapshot() );
		assertEquals( null, version.getDate() );
		assertEquals( "1", version.toString() );
	}

	@Test
	public void testParseWithMajorAndMinor() throws Exception {
		Version version = Version.parse( "1.2" );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( null, version.getState() );
		assertEquals( 0, version.getMicro() );
		assertEquals( false, version.isSnapshot() );
		assertEquals( null, version.getDate() );
		assertEquals( "1.2", version.toString() );
	}

	@Test
	public void testParseWithVersionString() throws Exception {
		Version version = Version.parse( "1.2-test-3", null );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( 0, version.getMicro() );
		assertEquals( "test", version.getState() );
		assertEquals( 3, version.getBuild() );
		assertEquals( false, version.isSnapshot() );
		assertEquals( null, version.getDate() );
	}

	@Test
	public void testParseWithSnapshotVersionString() throws Exception {
		Version version = Version.parse( "1-2-3-SNAPSHOT-4", null );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( 3, version.getMicro() );
		assertEquals( "SNAPSHOT", version.getState() );
		assertEquals( 4, version.getBuild() );
		assertEquals( true, version.isSnapshot() );
		assertEquals( null, version.getDate() );
	}

	@Test
	public void testParseWithVersionDateString() throws Exception {
		Version version = Version.parse( "1-2-Update-3", "2000-01-01 00:00:00 MST" );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( 0, version.getMicro() );
		assertEquals( "Update", version.getState() );
		assertEquals( 3, version.getBuild() );
		assertEquals( false, version.isSnapshot() );
		assertEquals( DATE_FORMAT.parse( "2000-01-01 07:00:00" ), version.getDate() );
	}

	@Test
	public void testParseWithSnapshotVersionDateString() throws Exception {
		Version version = Version.parse( "1-2-SNAPSHOT-3", "2000-01-01 00:00:00 MST" );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( "SNAPSHOT", version.getState() );
		assertEquals( 3, version.getBuild() );
		assertEquals( true, version.isSnapshot() );
		assertEquals( DATE_FORMAT.parse( "2000-01-01 07:00:00" ), version.getDate() );
	}

	@Test
	public void testParseExample1() throws Exception {
		String text = "0.0.1-SNAPSHOT";
		Version version = Version.parse( text );
		assertEquals( 0, version.getMajor() );
		assertEquals( 0, version.getMinor() );
		assertEquals( 1, version.getMicro() );
		assertEquals( "SNAPSHOT", version.getState() );
		assertEquals( 0, version.getBuild() );
		assertEquals( true, version.isSnapshot() );
		assertEquals( "0.0.1", version.getVersion() );
		assertEquals( text, version.getFullVersion() );
	}

	@Test
	public void testParseWithStateDeclaredTwice() throws Exception {
		try {
			Version.parse( "1.0-Alpha-Beta-7" );
			fail( "Runtime exception should be thrown." );
		} catch( RuntimeException exception ) {
			// Test passes.
		}
	}

	@Test
	public void testParseWithSnapshotDeclaredTwice() throws Exception {
		try {
			Version.parse( "SNAPSHOT-SNAPSHOT" );
			fail( "Runtime exception should be thrown." );
		} catch( RuntimeException exception ) {
			// Test passes.
		}
	}

	@Test
	public void testGetVersion() throws Exception {
		assertEquals( Version.UNKNOWN, Version.parse( null ).getVersion() );
		assertEquals( "1.2", Version.parse( "1-2-U-3", "2000-01-01 00:00:00 MST" ).getVersion() );
	}

	@Test
	public void testGetFullVersion() throws Exception {
		assertEquals( Version.UNKNOWN, Version.parse( null ).getFullVersion() );

		String text = "1-2-SNAPSHOT-3";
		assertEquals( text, Version.parse( text, "2000-01-01 00:00:00 MST" ).getFullVersion() );
	}

	@Test
	public void testGetCodedVersion() throws Exception {
		assertEquals( Version.UNKNOWN, Version.parse( null ).getCodedVersion() );

		String code = "1-2-SNAPSHOT-3";
		String time = "2000-01-01 00:00:00 -0700";
		assertEquals( code, Version.parse( code, time ).getCodedVersion() );
	}

	@Test
	public void testGetDateString() throws Exception {
		assertEquals( "Unknown", Version.parse( "1-2-U-3", null ).getDateString() );
		assertEquals( "2000-01-01 00:00:00 -0700", Version.parse( "1-2-U-3", "2000-01-01 00:00:00 MST" ).getDateString() );
	}

	@Test
	public void testToString() throws Exception {
		assertEquals( "1.2", Version.parse( "1-2-Update-3", "2000-01-01 00:00:00 MST" ).toString() );
	}

	@Test
	public void testEquals() throws Exception {
		assertEquals( Version.parse( null, null ), Version.parse( null, null ) );
		assertEquals( Version.parse( "", null ), Version.parse( "", null ) );
		assertEquals( Version.parse( null, null ), Version.parse( "", null ) );
		assertEquals( Version.parse( "", null ), Version.parse( null, null ) );

		assertEquals( Version.parse( "1-2-Update-3", null ), Version.parse( "1-2-Update-3", null ) );
		assertEquals( Version.parse( "1-2-SNAPSHOT-3", null ), Version.parse( "1-2-SNAPSHOT-3", null ) );
	}

	@Test
	public void testCompareEmptyVersions() throws Exception {
		assertTrue( Version.parse( null ).compareTo( Version.parse( null ) ) == 0 );
		assertTrue( Version.parse( null ).compareTo( Version.parse( "" ) ) == 0 );
		assertTrue( Version.parse( "" ).compareTo( Version.parse( null ) ) == 0 );
		assertTrue( Version.parse( "" ).compareTo( Version.parse( "" ) ) == 0 );
	}

	@Test
	public void testCompareSimpleVersions() throws Exception {
		assertTrue( Version.parse( "1" ).compareTo( Version.parse( "1" ) ) == 0 );
		assertTrue( Version.parse( "1" ).compareTo( Version.parse( "2" ) ) < 0 );
		assertTrue( Version.parse( "2" ).compareTo( Version.parse( "1" ) ) > 0 );
	}

	@Test
	public void testCompareComplexVersions() throws Exception {
		assertTrue( Version.parse( "1-0-SNAPSHOT-10" ).compareTo( Version.parse( "1-0-SNAPSHOT-9" ) ) > 0 );
	}

	@Test
	public void testCompareMixedVersions() throws Exception {
		// Unknown tests.
		assertTrue( Version.parse( null ).compareTo( Version.parse( null ) ) == 0 );
		assertTrue( Version.parse( null ).compareTo( Version.parse( "1" ) ) < 0 );
		assertTrue( Version.parse( "1" ).compareTo( Version.parse( null ) ) > 0 );

		// Equals tests.
		assertTrue( Version.parse( "1" ).compareTo( Version.parse( "1-0" ) ) == 0 );
		assertTrue( Version.parse( "1" ).compareTo( Version.parse( "1-0-0" ) ) == 0 );

		// Greater than tests.
		assertTrue( Version.parse( "1-0-0" ).compareTo( Version.parse( "1-0-0-SNAPSHOT" ) ) > 0 );
		assertTrue( Version.parse( "1-0-0" ).compareTo( Version.parse( "1-0-Alpha-0" ) ) > 0 );
		assertTrue( Version.parse( "1-0-0" ).compareTo( Version.parse( "1-0-Beta-0" ) ) > 0 );

		// Less than tests.
		assertTrue( Version.parse( "1-0-Alpha-0" ).compareTo( Version.parse( "1-0-Beta-0" ) ) < 0 );
		assertTrue( Version.parse( "1-0-SNAPSHOT-0" ).compareTo( Version.parse( "1-0-SNAPSHOT-1" ) ) < 0 );
		assertTrue( Version.parse( "1-beta" ).compareTo( Version.parse( "1-beta-0" ) ) < 0 );
	}

}
