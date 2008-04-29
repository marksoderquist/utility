package com.parallelsymmetry.util;

public class ObjectUtil {

	public static final boolean areEqual( Object object1, Object object2 ) {
		if( object1 == null && object2 == null ) return true;
		if( object1 != null && object1.equals( object2 ) ) return true;
		return false;
	}

}
