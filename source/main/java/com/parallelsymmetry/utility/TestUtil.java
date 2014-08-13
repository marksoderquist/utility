package com.parallelsymmetry.utility;

import java.util.Map;

public class TestUtil {

	private static Boolean test;

	/**
	 * Check if the main thread is running in the JUnit test framework. This is
	 * done by searching the main thread stack for the class
	 * junit.framework.TestSuite and caching the result. If the TestSuite class is
	 * found the method will return true.
	 * 
	 * @return
	 */
	public static final boolean isTest() {
		if( test != null ) return test;

		boolean result = false;
		Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
		for( Thread thread : stacks.keySet() ) {
			if( thread.getId() == 1 ) {
				StackTraceElement[] elements = stacks.get( thread );
				for( StackTraceElement element : elements ) {
					if( "junit.framework.TestSuite".equals( element.getClassName() ) ) {
						result = true;
						break;
					};
				}
			}
		}

		return test = result;
	}

}
