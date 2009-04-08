package com.parallelsymmetry.util;

import junit.framework.TestCase;

import org.junit.Test;

public class ArrayUtilTest extends TestCase {

	@Test
	public void testContains() {
		Object object1 = new Object();
		Object object2 = new Object();
		Object object3 = new Object();

		Object[] array = new Object[3];
		array[0] = object1;
		array[2] = object3;

		assertTrue( ArrayUtil.contains( array, object1 ) );
		assertFalse( ArrayUtil.contains( array, object2 ) );
		assertTrue( ArrayUtil.contains( array, object3 ) );
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
}
