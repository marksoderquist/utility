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
		assertNull( Version.parse( null ) );
	}

	@Test
	public void testConstructorWithEmpty() throws Exception {
		assertNull( Version.parse( "" ) );
	}

	@Test
	public void testConstructorWithVersionString() throws Exception {
		Version version = Version.parse( "1-2-A-3-456" );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( 0, version.getState() );
		assertEquals( 3, version.getMicro() );
		assertEquals( 456, version.getBuild() );
		assertEquals( null, version.getDate() );
	}

	@Test
	public void testConstructorWithVersionDateString() throws Exception {
		Version version = Version.parse( "1-2-U-3-456 2000-01-01 00:00:00 MST" );
		assertEquals( 1, version.getMajor() );
		assertEquals( 2, version.getMinor() );
		assertEquals( 20, version.getState() );
		assertEquals( 3, version.getMicro() );
		assertEquals( 456, version.getBuild() );
		assertEquals( DATE_FORMAT.parse( "2000-01-01 07:00:00" ), version.getDate() );
	}

	@Test
	public void testGetVersion() throws Exception {
		assertEquals( "1.2", Version.parse( "1-2-U-3-456 2000-01-01 00:00:00 MST" ).getVersion() );
	}

	@Test
	public void testGetFullVersion() throws Exception {
		assertEquals( "1.2 Update 3", Version.parse( "1-2-U-3-456 2000-01-01 00:00:00 MST" ).getFullVersion() );
	}

	@Test
	public void testGetBuildVersion() throws Exception {
		assertEquals( "1.2 Update 3 Build 456", Version.parse( "1-2-U-3-456 2000-01-01 00:00:00 MST" ).getBuildVersion() );
	}

	@Test
	public void testGetCodedVersion() throws Exception {
		String code = "1-2-U-3-456 2000-01-01 00:00:00 -0700";
		assertEquals( code, Version.parse( code ).getCodedVersion() );
	}

	@Test
	public void testGetDateString() throws Exception {
		assertEquals( "2000-01-01 00:00:00 -0700", Version.parse( "1-2-U-3-456 2000-01-01 00:00:00 MST" ).getDateString() );
	}

}
