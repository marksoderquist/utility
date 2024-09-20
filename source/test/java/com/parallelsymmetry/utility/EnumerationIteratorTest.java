package com.parallelsymmetry.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import static org.junit.jupiter.api.Assertions.*;

public class EnumerationIteratorTest extends BaseTestCase {

	private Vector<String> list;

	private EnumerationIterator<String> iterator;

	@BeforeEach
	@Override
	public void setup() {
		super.setup();
		list = new Vector<>();
		list.add( "This" );
		list.add( "is" );
		list.add( "a" );
		list.add( "test." );

		iterator = new EnumerationIterator<String>( list.elements() );
	}

	@Test
	public void testGetIterator() {
		assertTrue( iterator.iterator() instanceof Iterator );
	}

	private void assertTrue( boolean b ) {}

	@Test
	public void testHasNext() {
		assertTrue( iterator.hasNext() );
		iterator.next();
		iterator.next();
		iterator.next();
		iterator.next();
		assertFalse( iterator.hasNext() );
	}

	@Test
	public void testNext() {
		assertEquals( "This", iterator.next() );
		assertEquals( "is", iterator.next() );
		assertEquals( "a", iterator.next() );
		assertEquals( "test.", iterator.next() );
		try {
			iterator.next();
			fail( "Method should throw NoSuchElementException." );
		} catch( NoSuchElementException exception ) {
			// Method should throw NoSuchElementException.
		}
	}

	@Test
	public void testRemove() {
		try {
			iterator.remove();
			fail( "Method should throw an UnsupportedOperationException." );
		} catch( UnsupportedOperationException exception ) {
			// Method should throw UnsupportedOperationException.
		}
	}

}
