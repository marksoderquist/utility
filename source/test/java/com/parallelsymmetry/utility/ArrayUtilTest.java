package com.parallelsymmetry.utility;

import junit.framework.TestCase;

import org.junit.Test;

public class ArrayUtilTest extends TestCase {

	public void testCombine() {
		assertEquals( null, ArrayUtil.combine( null, null ) );

		assertEquals( new String[] { "a" }, ArrayUtil.combine( new String[] { "a" }, null ) );
		assertEquals( new String[] { "b" }, ArrayUtil.combine( null, new String[] { "b" } ) );

		assertEquals( new String[] { "a", "b" }, ArrayUtil.combine( new String[] { "a" }, new String[] { "b" } ) );
		assertEquals( new String[] { "a", "b", "c", "d" }, ArrayUtil.combine( new String[] { "a", "b" }, new String[] { "c", "d" } ) );
	}

	@Test
	public void testContainsEquivalent() {
		Object object1 = new Integer( 1 );
		Object object2 = new Integer( 2 );
		Object object3 = new Integer( 3 );

		Object[] array = new Object[3];
		array[0] = object1;
		array[2] = object3;

		assertTrue( ArrayUtil.containsEquivalent( array, object1 ) );
		assertFalse( ArrayUtil.containsEquivalent( array, object2 ) );
		assertTrue( ArrayUtil.containsEquivalent( array, object3 ) );

		assertTrue( ArrayUtil.containsEquivalent( array, new Integer( 1 ) ) );
		assertFalse( ArrayUtil.containsEquivalent( array, new Integer( 2 ) ) );
		assertTrue( ArrayUtil.containsEquivalent( array, new Integer( 3 ) ) );
	}

	@Test
	public void testEncodeIntArray() {
		assertEquals( "0,1,2,3", ArrayUtil.encodeIntArray( new int[] { 0, 1, 2, 3 } ) );
	}

	@Test
	public void testDecodeIntArray() {
		assertEquals( new int[] { 4, 5, 6, 7 }, ArrayUtil.decodeIntArray( "4,5,6,7" ) );
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

	private void assertEquals( int[] a, int[] b ) {
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
