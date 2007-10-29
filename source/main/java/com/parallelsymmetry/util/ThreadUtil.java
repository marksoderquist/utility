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

}