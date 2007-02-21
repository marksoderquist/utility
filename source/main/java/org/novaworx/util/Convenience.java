package org.novaworx.util;

public class Convenience {
	
	public static final String getClassNameOnly( Class<?> clazz ) {
		String className = clazz.getName();
		int index = className.lastIndexOf( '.' );
		return className.substring( index + 1 );
	}

}
