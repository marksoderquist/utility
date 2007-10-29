package com.parallelsymmetry.util;

public class ClassUtil {

	public static final String getClassNameOnly( Class<?> clazz ) {
		String className = clazz.getName();
		int index = className.lastIndexOf( '.' );
		return className.substring( index + 1 );
	}

}
