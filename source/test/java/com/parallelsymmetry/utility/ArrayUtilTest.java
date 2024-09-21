package com.parallelsymmetry.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayUtilTest extends BaseTestCase {

	@Test
	public void testPush() {
		assertArrayEquals( null, ArrayUtil.push( null, "a" ) );
		assertArrayEquals( new String[]{ "a" }, ArrayUtil.push( new String[]{ "a" }, null ) );

		assertArrayEquals( new String[]{ "a" }, ArrayUtil.push( new String[]{}, "a" ) );
		assertArrayEquals( new String[]{ "a", "b" }, ArrayUtil.push( new String[]{ "a" }, "b" ) );
		assertArrayEquals( new String[]{ "a", "b", "c" }, ArrayUtil.push( new String[]{ "a", "b" }, "c" ) );
	}

	@Test
	public void testPop() {
		assertArrayEquals( null, ArrayUtil.pop( null ) );
		assertArrayEquals( new String[]{}, ArrayUtil.pop( new String[]{} ) );

		assertArrayEquals( new String[]{ "a", "b" }, ArrayUtil.pop( new String[]{ "a", "b", "c" } ) );
		assertArrayEquals( new String[]{ "a" }, ArrayUtil.pop( new String[]{ "a", "b" } ) );
		assertArrayEquals( new String[]{}, ArrayUtil.pop( new String[]{ "a" } ) );
	}

	@Test
	public void testCombine() {
		assertArrayEquals( null, ArrayUtil.combine( null, null ) );

		assertArrayEquals( new String[]{ "a" }, ArrayUtil.combine( new String[]{ "a" }, null ) );
		assertArrayEquals( new String[]{ "b" }, ArrayUtil.combine( null, new String[]{ "b" } ) );

		assertArrayEquals( new String[]{ "a", "b" }, ArrayUtil.combine( new String[]{ "a" }, new String[]{ "b" } ) );
		assertArrayEquals( new String[]{ "a", "b", "c", "d" }, ArrayUtil.combine( new String[]{ "a", "b" }, new String[]{ "c", "d" } ) );
	}

	@Test
	public void testContainsEquivalent() {
		Object object1 = 1;
		Object object2 = 2;
		Object object3 = 3;

		Object[] array = new Object[ 3 ];
		array[ 0 ] = object1;
		array[ 2 ] = object3;

		assertTrue( ArrayUtil.containsEquivalent( array, object1 ) );
		assertFalse( ArrayUtil.containsEquivalent( array, object2 ) );
		assertTrue( ArrayUtil.containsEquivalent( array, object3 ) );

		assertTrue( ArrayUtil.containsEquivalent( array, 1 ) );
		assertFalse( ArrayUtil.containsEquivalent( array, 2 ) );
		assertTrue( ArrayUtil.containsEquivalent( array, 3 ) );
	}

	@Test
	public void testEncodeIntArray() {
		assertEquals( "0,1,2,3", ArrayUtil.encodeIntArray( new int[]{ 0, 1, 2, 3 } ) );
	}

	@Test
	public void testDecodeIntArray() {
		assertArrayEquals( new int[]{ 4, 5, 6, 7 }, ArrayUtil.decodeIntArray( "4,5,6,7" ) );
	}

	//	private <T> void assertEquals( T[] a, T[] b ) {
	//		if( a == null ) assertNull( b );
	//		if( b == null ) assertNull( a );
	//		if( a == null && b == null ) return;
	//
	//		assertEquals( a.length, b.length );
	//
	//		int count = a.length;
	//		for( int index = 0; index < count; index++ ) {
	//			assertEquals( a[index], b[index] );
	//		}
	//	}

	//	private void assertIntArrayEquals( int[] a, int[] b ) {
	//		if( a == null ) assertNull( b );
	//		if( b == null ) assertNull( a );
	//		if( a == null && b == null ) return;
	//
	//		assertEquals( a.length, b.length );
	//
	//		int count = a.length;
	//		for( int index = 0; index < count; index++ ) {
	//			assertEquals( a[index], b[index] );
	//		}
	//	}

}
