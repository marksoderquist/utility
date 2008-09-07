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
	public void testConstructorWithNull() throws Exception {
		assertEquals( "Unknown", Version.parse( null ).toString() );
	}

	@Test
	public void testConstructorWithEmpty() throws Exception {
		assertEquals( "Unknown", Version.parse( "" ).toString() );
	}

	@Test
	public void testConstructorWithVersionString() throws Exception {
		Version version = Version.parse( "1.2-test-3" );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( "test", version.getState() );
		assertEquals( 3, version.getMicro() );
		assertEquals( false, version.isSnapshot() );
		assertEquals( null, version.getDate() );
	}

	@Test
	public void testConstructorWithSnapshotVersionString() throws Exception {
		Version version = Version.parse( "1-2-test-3-SNAPSHOT" );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( "test", version.getState() );
		assertEquals( 3, version.getMicro() );
		assertEquals( true, version.isSnapshot() );
		assertEquals( null, version.getDate() );
	}

	@Test
	public void testConstructorWithVersionDateString() throws Exception {
		Version version = Version.parse( "1-2-Update-3 2000-01-01 00:00:00 MST" );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( "Update", version.getState() );
		assertEquals( 3, version.getMicro() );
		assertEquals( false, version.isSnapshot() );
		assertEquals( DATE_FORMAT.parse( "2000-01-01 07:00:00" ), version.getDate() );
	}

	@Test
	public void testConstructorWithSnapshotVersionDateString() throws Exception {
		Version version = Version.parse( "1-2-Update-3-SNAPSHOT 2000-01-01 00:00:00 MST" );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( "Update", version.getState() );
		assertEquals( 3, version.getMicro() );
		assertEquals( true, version.isSnapshot() );
		assertEquals( DATE_FORMAT.parse( "2000-01-01 07:00:00" ), version.getDate() );
	}

	@Test
	public void testGetVersion() throws Exception {
		assertEquals( "1.2", Version.parse( "1-2-U-3 2000-01-01 00:00:00 MST" ).getVersion() );
	}

	@Test
	public void testGetFullVersion() throws Exception {
		assertEquals( "1.2 Update 3 SNAPSHOT", Version.parse( "1-2-Update-3-SNAPSHOT 2000-01-01 00:00:00 MST" ).getFullVersion() );
	}

	@Test
	public void testGetCodedVersion() throws Exception {
		String code = "1-2-U-3-SNAPSHOT 2000-01-01 00:00:00 -0700";
		assertEquals( code, Version.parse( code ).getCodedVersion() );
	}

	@Test
	public void testGetDateString() throws Exception {
		assertEquals( "Unknown", Version.parse( "1-2-U-3" ).getDateString() );
		assertEquals( "2000-01-01 00:00:00 -0700", Version.parse( "1-2-U-3 2000-01-01 00:00:00 MST" ).getDateString() );
	}

	@Test
	public void testToString() throws Exception {
		assertEquals( "1.2 Update 3", Version.parse( "1-2-Update-3 2000-01-01 00:00:00 MST" ).toString() );
	}

	@Test
	public void testEquals() throws Exception {
		assertEquals( Version.parse( null ), Version.parse( null ) );
		assertEquals( Version.parse( "" ), Version.parse( "" ) );
		assertEquals( Version.parse( null ), Version.parse( "" ) );
		assertEquals( Version.parse( "" ), Version.parse( null ) );

		assertEquals( Version.parse( "1-2-Update-3" ), Version.parse( "1-2-Update-3" ) );
		assertEquals( Version.parse( "1-2-Update-3-SNAPSHOT" ), Version.parse( "1-2-Update-3-SNAPSHOT" ) );
	}

}
