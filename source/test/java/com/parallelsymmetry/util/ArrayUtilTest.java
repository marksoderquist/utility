package com.parallelsymmetry.util;

import junit.framework.TestCase;

import org.junit.Test;

import com.parallelsymmetry.util.ArrayUtil;

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

}
