package org.novaworx.util;

public class ArrayUtil {

	public static final <T> boolean contains( T[] array, T element ) {
		for( T test : array ) {
			if( test == element ) return true;
		}
		return false;
	}

}
