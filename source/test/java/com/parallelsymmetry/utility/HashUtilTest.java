package com.parallelsymmetry.utility;

import java.io.File;

import com.parallelsymmetry.utility.FileUtil;
import com.parallelsymmetry.utility.HashUtil;

import junit.framework.TestCase;

public class HashUtilTest extends TestCase {

	public void testHash() {
		assertNull( HashUtil.hash( (String)null ) );
		assertEquals( "da39a3ee5e6b4b0d3255bfef95601890afd80709", HashUtil.hash( "" ) );
		assertEquals( "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", HashUtil.hash( "test" ) );
	}

	public void testHashWithFile() throws Exception {
		assertNull( HashUtil.hash( (File)null ) );
		File empty = File.createTempFile( "HashUtil", "test" );
		assertEquals( "da39a3ee5e6b4b0d3255bfef95601890afd80709", HashUtil.hash( empty ) );

		File test = File.createTempFile( "HashUtil", "test" );
		FileUtil.save( "test", test );
		assertEquals( "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3", HashUtil.hash( test ) );
	}
	
	public void testHashUsingKeccak() {
		assertNull( HashUtil.hash( (String)null ) );
		assertEquals( "c5d2460186f7233c927e7db2dcc703c0e500b653ca82273b7bfad8045d85a470", HashUtil.hash( "", HashUtil.KECCAK ) );
		assertEquals( "9c22ff5f21f0b81b113e63f7db6da94fedef11b2119b4088b89664fb9a3cb658", HashUtil.hash( "test", HashUtil.KECCAK ) );
	}

}
