package com.parallelsymmetry.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class IterableEnumerationTest extends TestCase {

	@Test
	public void testIterator() {
		List<String> list = new ArrayList<String>();
		list.add( "A" );
		list.add( "B" );
		list.add( "C" );

		Enumeration<String> enumeration = Collections.enumeration( list );

		IterableEnumeration<String> iterable = new IterableEnumeration<String>( enumeration );
		Iterator<String> iterator = iterable.iterator();

		assertTrue( iterator.hasNext() );
		assertEquals( "A", iterator.next() );
		assertTrue( iterator.hasNext() );
		assertEquals( "B", iterator.next() );
		assertTrue( iterator.hasNext() );
		assertEquals( "C", iterator.next() );
		assertFalse( iterator.hasNext() );
	}

}
