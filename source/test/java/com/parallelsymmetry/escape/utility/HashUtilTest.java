package com.parallelsymmetry.escape.utility;

import junit.framework.TestCase;

public class HashUtilTest extends TestCase {

	public void testHash() {
		assertNull( HashUtil.hash( null ) );
		assertEquals( "da39a3ee5e6b4b0d3255bfef95601890afd80709", HashUtil.hash( "" ) );
		assertEquals( "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", HashUtil.hash( "test" ) );
	}
}
