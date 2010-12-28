package com.parallelsymmetry.escape.utility;

import java.util.Arrays;

public class ThreadUtil {

	/**
	 * Pause a thread for a specific amount of time. If an InterruptedException
	 * occurs the method returns immediately.
	 * 
	 * @param duration
	 */
	public static final void pause( long duration ) {
		try {
			Thread.sleep( duration );
		} catch( InterruptedException exception ) {
			// Intentionally ignore exception.
		}
	}

	/**
	 * Append the stack trace of the source throwable to the target throwable.
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public static final Throwable appendStackTrace( Throwable source, Throwable target ) {
		if( source == null ) return target;
		if( target == null ) return source;
		return appendStackTrace( source.getStackTrace(), target );
	}

	/**
	 * Append stack trace to the target throwable.
	 * 
	 * @param target
	 * @param trace
	 * @return
	 */
	public static final Throwable appendStackTrace( StackTraceElement[] trace, Throwable target ) {
		if( target == null ) return null;
		if( trace != null ) {
			StackTraceElement[] originalStack = target.getStackTrace();
			StackTraceElement[] elements = new StackTraceElement[originalStack.length + trace.length];
			System.arraycopy( originalStack, 0, elements, 0, originalStack.length );
			System.arraycopy( trace, 0, elements, originalStack.length, trace.length );
			target.setStackTrace( elements );
		}
		return target;
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
		Class<?>[] frame = new StackClassResolver().getClassContext();
		return Arrays.copyOfRange( frame, 2, frame.length );
	}

	private static final class StackClassResolver extends SecurityManager {

		@Override
		public Class<?>[] getClassContext() {
			return super.getClassContext();
		}

	}

}
