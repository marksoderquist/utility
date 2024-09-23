package com.parallelsymmetry.utility;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HashUtilTest extends BaseTestCase {

	@Test
	public void testHash() {
		assertNull( HashUtil.hash( (String)null ) );
		assertEquals( "da39a3ee5e6b4b0d3255bfef95601890afd80709", HashUtil.hash( "" ) );
		assertEquals( "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", HashUtil.hash( "test" ) );
	}

	@Test
	public void testHashWithFile() throws Exception {
		assertNull( HashUtil.hash( (File)null ) );
		File empty = File.createTempFile( "HashUtil", "test" );
		assertEquals( "da39a3ee5e6b4b0d3255bfef95601890afd80709", HashUtil.hash( empty ) );

		File test = File.createTempFile( "HashUtil", "test" );
		FileUtil.save( "test", test );
		assertEquals( "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", HashUtil.hash( test ) );
	}

}
