package org.novaworx.util;

import junit.framework.TestCase;

public class ConvenienceTest extends TestCase {

	public void testPause() {
		long length = 100;
		long start = System.currentTimeMillis();
		Convenience.pause( length );
		long stop = System.currentTimeMillis();
		assertTrue( stop - start >= length );
	}

	public void testGetClassNameOnly() {
		assertEquals( "Object", Convenience.getClassNameOnly( Object.class ) );
	}

}
