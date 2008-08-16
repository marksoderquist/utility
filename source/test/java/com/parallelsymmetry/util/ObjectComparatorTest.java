package com.parallelsymmetry.util;

import junit.framework.TestCase;

import org.junit.Test;

public class ObjectComparatorTest extends TestCase {

	ObjectComparator comparator = new ObjectComparator();

	@Test
	public void testCompare() {
		assertEquals( 0, comparator.compare( null, null ) );
		assertEquals( -1, comparator.compare( null, new Object() ) );
		assertEquals( 1, comparator.compare( new Object(), null ) );
		assertEquals( 0, comparator.compare( "a", "a" ) );
		assertEquals( -1, comparator.compare( "a", "b" ) );
		assertEquals( 1, comparator.compare( "b", "a" ) );
	}
}
