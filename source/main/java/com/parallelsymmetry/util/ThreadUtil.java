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
		if( parent == null ) return original;
		if( original == null ) return parent;
		return appendStackTrace( original, parent.getStackTrace() );
	}

	public static final Throwable appendStackTrace( Throwable original, StackTraceElement[] parentStack ) {
		if( original == null ) return null;
		if( parentStack != null ) {
			StackTraceElement[] originalStack = original.getStackTrace();
			StackTraceElement[] elements = new StackTraceElement[originalStack.length + parentStack.length];
			System.arraycopy( originalStack, 0, elements, 0, originalStack.length );
			System.arraycopy( parentStack, 0, elements, originalStack.length, parentStack.length );
			original.setStackTrace( elements );
		}
		return original;
	}

}
