package org.novaworx.util;

import junit.framework.TestCase;

public class ThreadUtilTest extends TestCase {

	public void testPause() {
		long length = 100;
		long start = System.currentTimeMillis();
		ThreadUtil.pause( length );
		long stop = System.currentTimeMillis();
		assertTrue( stop - start >= length );
	}

}