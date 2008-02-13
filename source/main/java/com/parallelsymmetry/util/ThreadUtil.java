package com.parallelsymmetry.util;

public class ThreadUtil {

	public static final void pause( long duration ) {
		try {
			Thread.sleep( duration );
		} catch( InterruptedException exception ) {
			// Intentionally ignore exception.
		}
		Thread.yield();
	}

	public static final Throwable appendStackTrace( Throwable original, Throwable parent ) {
		if( original == null ) return parent;
		if( parent != null ) {
			StackTraceElement[] originalStack = original.getStackTrace();
			StackTraceElement[] parentStack = parent.getStackTrace();
			StackTraceElement[] elements = new StackTraceElement[originalStack.length + parentStack.length];
			System.arraycopy( originalStack, 0, elements, 0, originalStack.length );
			System.arraycopy( parentStack, 0, elements, originalStack.length, parentStack.length );
			original.setStackTrace( elements );
		}
		return original;
	}

}
