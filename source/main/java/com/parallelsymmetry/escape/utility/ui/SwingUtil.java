package com.parallelsymmetry.escape.utility.ui;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

public class SwingUtil {

	/**
	 * Cause the calling thread to wait until all events on the AWT event queue at
	 * the time this method was called have been processed. This is done by
	 * submitting a token event on the queue and waiting until the token event is
	 * processed.
	 */
	public static final void swingWait() {
		try {
			EventQueue.invokeAndWait( new Token() );
		} catch( InterruptedException event ) {
			return;
		} catch( InvocationTargetException event ) {
			return;
		}
	}

	/**
	 * Choose how to execute a runnable by checking if the current thread is the
	 * event dispatch thread. If so it is safe to execute the runnable directly on
	 * the calling thread. Otherwise, queue it to execute on the event dispatch
	 * thread.
	 * 
	 * @param runnable
	 * @return True if executed on the event dispatch thread. False if queued to
	 *         execute on the event dispatch thread.
	 */
	public static final boolean executeSafely( Runnable runnable ) {
		if( EventQueue.isDispatchThread() ) {
			runnable.run();
			return true;
		} else {
			EventQueue.invokeLater( runnable );
			return false;
		}
	}

	private static class Token implements Runnable {

		@Override
		public void run() {}

	}

}
