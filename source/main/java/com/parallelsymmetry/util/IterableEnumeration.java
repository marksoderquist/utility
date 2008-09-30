package com.parallelsymmetry.util;

import java.util.Enumeration;
import java.util.Iterator;

public class IterableEnumeration<T> implements Iterable<T> {

	private final Enumeration<T> enumeration;

	public IterableEnumeration( Enumeration<T> enumeration ) {
		this.enumeration = enumeration;
	}

	public Iterator<T> iterator() {
		return new EnumerationIterator();
	}

	private final class EnumerationIterator implements Iterator<T> {

		public boolean hasNext() {
			return enumeration.hasMoreElements();
		}

		public T next() {
			return enumeration.nextElement();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
