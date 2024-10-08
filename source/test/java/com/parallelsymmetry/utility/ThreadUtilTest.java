package com.parallelsymmetry.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ThreadUtilTest extends BaseTestCase {

	@Test
	public void testPause() {
		long length = 100;
		long start = System.nanoTime();
		ThreadUtil.pause( length );
		long stop = System.nanoTime();
		long delta = stop - start;
		assertTrue( delta >= length * 1000, "Delta: " + delta );
	}

	@Test
	public void testCalledFrom() {
		assertFalse( ThreadUtil.calledFrom( "ThreadUtilTest", "notHere" ) );

		assertTrue( ThreadUtil.calledFrom( "ThreadUtilTest", "testCalledFrom" ) );
		assertTrue( ThreadUtil.calledFrom( "com.parallelsymmetry.utility.ThreadUtilTest", "testCalledFrom" ) );
	}

	@Test
	public void testAppendStackTraceWithNullSource() {
		Throwable target = new Throwable();
		StackTraceElement[] trace = target.getStackTrace();
		assertArrayEquals( trace, ThreadUtil.appendStackTrace( (Throwable)null, target ).getStackTrace() );
	}

	@Test
	public void testAppendStackTraceWithNullTarget() {
		Throwable source = new Throwable();
		StackTraceElement[] trace = source.getStackTrace();
		assertArrayEquals( trace, ThreadUtil.appendStackTrace( source, null ).getStackTrace() );
	}

	@Test
	public void testAppendStackTrace() {
		Throwable source = new Throwable();
		Throwable target = new Throwable();

		StackTraceElement[] sourceTrace = source.getStackTrace();
		StackTraceElement[] targetTrace = target.getStackTrace();

		StackTraceElement[] elements = new StackTraceElement[ targetTrace.length + sourceTrace.length ];
		System.arraycopy( targetTrace, 0, elements, 0, targetTrace.length );
		System.arraycopy( sourceTrace, 0, elements, targetTrace.length, sourceTrace.length );

		assertArrayEquals( elements, ThreadUtil.appendStackTrace( source, target ).getStackTrace() );
	}

	@Test
	public void testGetStackClasses() {
		Class<?>[] frame = ThreadUtil.getStackClasses();
		assertEquals( ThreadUtilTest.class, frame[ 0 ] );
	}

	// @Deprecated
	// For removal
	//	private void assertEquals( Object[] array1, Object[] array2 ) {
	//		for( int index = 0; index < array1.length; index++ ) {
	//			assertEquals( array1[ index ], array2[ index ] );
	//		}
	//	}

}
