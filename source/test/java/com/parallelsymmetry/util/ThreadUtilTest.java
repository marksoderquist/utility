package com.parallelsymmetry.util;

import junit.framework.TestCase;

public class ThreadUtilTest extends TestCase {

	public void testPause() {
		long length = 100;
		long start = System.currentTimeMillis();
		ThreadUtil.pause( length );
		long stop = System.currentTimeMillis();
		assertTrue( stop - start >= length );
	}

	public void testAppendStackTraceWithNullOriginal() throws Exception {
		Throwable throwable2 = new Throwable();
		StackTraceElement[] stack2 = throwable2.getStackTrace();
		assertEquals( stack2, ThreadUtil.appendStackTrace( null, throwable2 ).getStackTrace() );
	}

	public void testAppendStackTraceWithNullParent() throws Exception {
		Throwable throwable1 = new Throwable();
		StackTraceElement[] stack1 = throwable1.getStackTrace();
		assertEquals( stack1, ThreadUtil.appendStackTrace( throwable1, null ).getStackTrace() );
	}

	public void testAppendStackTrace() throws Exception {
		Throwable throwable1 = new Throwable();
		Throwable throwable2 = new Throwable();

		StackTraceElement[] stack1 = throwable1.getStackTrace();
		StackTraceElement[] stack2 = throwable2.getStackTrace();

		StackTraceElement[] elements = new StackTraceElement[stack1.length + stack2.length];
		System.arraycopy( stack1, 0, elements, 0, stack1.length );
		System.arraycopy( stack2, 0, elements, stack1.length, stack2.length );

		assertEquals( elements, ThreadUtil.appendStackTrace( throwable1, throwable2 ).getStackTrace() );
	}

	private void assertEquals( Object[] array1, Object[] array2 ) {
		for( int index = 0; index < array1.length; index++ ) {
			assertEquals( array1[index], array2[index] );
		}
	}

}
