package com.parallelsymmetry.utility;

import java.util.Date;
import java.util.TimeZone;

import com.parallelsymmetry.utility.DateUtil;

import junit.framework.TestCase;

public class DateUtilTest extends TestCase {

	public void testParse() {
		try {
			DateUtil.parse( "", null );
			fail( "Null format should cause a NullPointerException." );
		} catch( NullPointerException exception ) {
			// Intentionally ignore exception.
		}

		assertNull( DateUtil.parse( null, null ) );
		assertNull( DateUtil.parse( null, "" ) );
		assertNull( DateUtil.parse( "", "" ) );

		assertEquals( new Date( 0 ), DateUtil.parse( "1970-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss" ) );
	}

	public void testParseWithTimeZone() {
		try {
			DateUtil.parse( "", null, (TimeZone)null );
			fail( "Null format should cause a NullPointerException." );
		} catch( NullPointerException exception ) {
			// Intentionally ignore exception.
		}

		try {
			DateUtil.parse( "", "", (TimeZone)null );
			fail( "Null time zone should cause a NullPointerException." );
		} catch( NullPointerException exception ) {
			// Intentionally ignore exception.
		}

		try {
			DateUtil.parse( "", null, (String)null );
			fail( "Null format should cause a NullPointerException." );
		} catch( NullPointerException exception ) {
			// Intentionally ignore exception.
		}

		try {
			DateUtil.parse( "", "", (String)null );
			fail( "Null time zone should cause a NullPointerException." );
		} catch( NullPointerException exception ) {
			// Intentionally ignore exception.
		}

		assertNull( DateUtil.parse( null, null, (TimeZone)null ) );
		assertNull( DateUtil.parse( null, null, (String)null ) );
		assertNull( DateUtil.parse( null, "", "" ) );
		assertNull( DateUtil.parse( "", "", "" ) );

		assertEquals( new Date( 0 ), DateUtil.parse( "1970-01-01 05:00:00", "yyyy-MM-dd HH:mm:ss", "GMT+05" ) );
	}

}
