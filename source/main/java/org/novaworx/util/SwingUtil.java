package org.novaworx.util;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

public class SwingUtil {

	public static final void swingWait() {
		try {
			EventQueue.invokeAndWait( new Token() );
		} catch( InterruptedException event ) {
			return;
		} catch( InvocationTargetException event ) {
			return;
		}
	}

	private static class Token implements Runnable {

		public void run() {}

	}
}
