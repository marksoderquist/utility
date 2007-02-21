package org.novaworx.util;

public class Convenience {
	
	public void pause( long duration ) {
		try {
			Thread.sleep( duration );
		} catch( InterruptedException exception ) {
			// Intentionally ignore exception.
		}
	}

	public static final String getClassNameOnly( Class<?> clazz ) {
		String className = clazz.getName();
		int index = className.lastIndexOf( '.' );
		return className.substring( index + 1 );
	}

}
