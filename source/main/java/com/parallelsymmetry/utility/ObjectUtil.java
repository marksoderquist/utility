package com.parallelsymmetry.utility;

public class ObjectUtil {

	public static final boolean areEqual( Object object1, Object object2 ) {
		if( object1 == null && object2 == null ) return true;
		if( object1 != null && object1.equals( object2 ) ) return true;
		return false;
	}

	public static final <T extends Comparable<T>> int compare( T object1, T object2 ) {
		if( object1 == null && object2 == null ) return 0;
		if( object1 == null && object2 != null ) return -1;
		if( object1 != null && object2 == null ) return 1;
		return object1.compareTo( object2 );
	}

}
