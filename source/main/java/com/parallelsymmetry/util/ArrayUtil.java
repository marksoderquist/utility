package com.parallelsymmetry.util;

public class ArrayUtil {

	public static final <T> boolean contains( T[] array, T element ) {
		for( T test : array ) {
			if( test == element ) return true;
		}
		return false;
	}

	public static final <T> boolean containsEquivalent( T[] array, T element ) {
		for( T test : array ) {
			if( element == null && test == null ) return true;
			if( test != null && test.equals( element ) ) return true;
		}
		return false;
	}

}
