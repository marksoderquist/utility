package com.parallelsymmetry.escape.utility;

import java.util.Date;

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
			DateUtil.parse( "", null, null );
			fail( "Null format should cause a NullPointerException." );
		} catch( NullPointerException exception ) {
			// Intentionally ignore exception.
		}

		try {
			DateUtil.parse( "", "", null );
			fail( "Null time zone should cause a NullPointerException." );
		} catch( NullPointerException exception ) {
			// Intentionally ignore exception.
		}

		assertNull( DateUtil.parse( null, null, null ) );
		assertNull( DateUtil.parse( null, "", "" ) );
		assertNull( DateUtil.parse( "", "", "" ) );

		assertEquals( new Date( 0 ), DateUtil.parse( "1970-01-01 05:00:00", "yyyy-MM-dd HH:mm:ss", "GMT+05" ) );
	}

}
