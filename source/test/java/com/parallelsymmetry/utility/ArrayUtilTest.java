package com.parallelsymmetry.utility;

import com.parallelsymmetry.utility.ArrayUtil;

import junit.framework.TestCase;

public class ArrayUtilTest extends TestCase {

	public void testCombine() {
		assertEquals( null, ArrayUtil.combine( null, null ) );

		assertEquals( new String[] { "a" }, ArrayUtil.combine( new String[] { "a" }, null ) );
		assertEquals( new String[] { "b" }, ArrayUtil.combine( null, new String[] { "b" } ) );

		assertEquals( new String[] { "a", "b" }, ArrayUtil.combine( new String[] { "a" }, new String[] { "b" } ) );
		assertEquals( new String[] { "a", "b", "c", "d" }, ArrayUtil.combine( new String[] { "a", "b" }, new String[] { "c", "d" } ) );
	}

	private <T> void assertEquals( T[] a, T[] b ) {
		if( a == null ) assertNull( b );
		if( b == null ) assertNull( a );
		if( a == null && b == null ) return;

		assertEquals( a.length, b.length );

		int count = a.length;
		for( int index = 0; index < count; index++ ) {
			assertEquals( a[index], b[index] );
		}
	}
}
