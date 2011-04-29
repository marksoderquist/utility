package com.parallelsymmetry.escape.utility;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterator<E> implements Iterator<E>, Iterable<E> {
	
	private Enumeration<E> enumeration;
	
	public EnumerationIterator( Enumeration<E> enumeration ) {
		this.enumeration = enumeration;
	}

	@Override
	public Iterator<E> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return enumeration.hasMoreElements();
	}

	@Override
	public E next() {
		return enumeration.nextElement();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
