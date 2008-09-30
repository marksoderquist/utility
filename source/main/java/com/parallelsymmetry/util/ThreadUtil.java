package com.parallelsymmetry.util;

import java.util.Arrays;

public class ThreadUtil {

	private static final StackClassResolver STACK_CLASS_RESOLVER = new StackClassResolver();

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

	/**
	 * <p>
	 * Returns the current execution stack as an array of classes. This is useful
	 * to determine the calling class.
	 * <p>
	 * The length of the array is the number of methods on the execution stack
	 * before this method is called. The element at index 0 is the calling class
	 * of this method, the element at index 1 is the calling class of the method
	 * in the previous class, and so on.
	 * 
	 * @return A class array of the execution stack before this method was called.
	 */
	public static final Class<?>[] getStackClasses() {
		Class<?>[] frame = STACK_CLASS_RESOLVER.getClassContext();
		return Arrays.copyOfRange( frame, 2, frame.length );
	}

	private static final class StackClassResolver extends SecurityManager {

		@Override
		public Class<?>[] getClassContext() {
			return super.getClassContext();
		}

	}

}
