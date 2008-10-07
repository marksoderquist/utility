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
		assertEquals( "Unknown", Version.parse( null, null ).toString() );
	}

	@Test
	public void testParseWithEmpty() throws Exception {
		assertEquals( "Unknown", Version.parse( "" ).toString() );
		assertEquals( "Unknown", Version.parse( "", "" ).toString() );
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
		assertEquals( "1.0", version.toString() );
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
		assertEquals( "test", version.getState() );
		assertEquals( 3, version.getMicro() );
		assertEquals( false, version.isSnapshot() );
		assertEquals( null, version.getDate() );
	}

	@Test
	public void testParseWithSnapshotVersionString() throws Exception {
		Version version = Version.parse( "1-2-test-3-SNAPSHOT", null );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( "test", version.getState() );
		assertEquals( 3, version.getMicro() );
		assertEquals( true, version.isSnapshot() );
		assertEquals( null, version.getDate() );
	}

	@Test
	public void testParseWithVersionDateString() throws Exception {
		Version version = Version.parse( "1-2-Update-3", "2000-01-01 00:00:00 MST" );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( "Update", version.getState() );
		assertEquals( 3, version.getMicro() );
		assertEquals( false, version.isSnapshot() );
		assertEquals( DATE_FORMAT.parse( "2000-01-01 07:00:00" ), version.getDate() );
	}

	@Test
	public void testParseWithSnapshotVersionDateString() throws Exception {
		Version version = Version.parse( "1-2-Update-3-SNAPSHOT", "2000-01-01 00:00:00 MST" );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( "Update", version.getState() );
		assertEquals( 3, version.getMicro() );
		assertEquals( true, version.isSnapshot() );
		assertEquals( DATE_FORMAT.parse( "2000-01-01 07:00:00" ), version.getDate() );
	}

	@Test
	public void testGetVersion() throws Exception {
		assertEquals( "1.2", Version.parse( "1-2-U-3", "2000-01-01 00:00:00 MST" ).getVersion() );
	}

	@Test
	public void testGetFullVersion() throws Exception {
		assertEquals( "1.2 Update 3 SNAPSHOT", Version.parse( "1-2-Update-3-SNAPSHOT", "2000-01-01 00:00:00 MST" ).getFullVersion() );
	}

	@Test
	public void testGetCodedVersion() throws Exception {
		String code = "1-2-U-3-SNAPSHOT";
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
		assertEquals( "1.2 Update 3", Version.parse( "1-2-Update-3", "2000-01-01 00:00:00 MST" ).toString() );
	}

	@Test
	public void testEquals() throws Exception {
		assertEquals( Version.parse( null, null ), Version.parse( null, null ) );
		assertEquals( Version.parse( "", null ), Version.parse( "", null ) );
		assertEquals( Version.parse( null, null ), Version.parse( "", null ) );
		assertEquals( Version.parse( "", null ), Version.parse( null, null ) );

		assertEquals( Version.parse( "1-2-Update-3", null ), Version.parse( "1-2-Update-3", null ) );
		assertEquals( Version.parse( "1-2-Update-3-SNAPSHOT", null ), Version.parse( "1-2-Update-3-SNAPSHOT", null ) );
	}

	@Test
	public void testCompare() throws Exception {
		assertTrue( Version.parse( null ).compareTo( Version.parse( null ) ) == 0 );
		assertTrue( Version.parse( null ).compareTo( Version.parse( "" ) ) == 0 );
		assertTrue( Version.parse( "" ).compareTo( Version.parse( null ) ) == 0 );
		assertTrue( Version.parse( "" ).compareTo( Version.parse( "" ) ) == 0 );

		assertTrue( Version.parse( "1" ).compareTo( Version.parse( "1" ) ) == 0 );
		assertTrue( Version.parse( "1" ).compareTo( Version.parse( "2" ) ) < 0 );
		assertTrue( Version.parse( "2" ).compareTo( Version.parse( "1" ) ) > 0 );

		assertTrue( Version.parse( "1-0-Alpha-10-SNAPSHOT" ).compareTo( Version.parse( "1-0-Alpha-9-SNAPSHOT" ) ) > 0 );
	}
}
