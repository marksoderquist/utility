package com.parallelsymmetry.utility.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.parallelsymmetry.utility.TextUtil;
import com.parallelsymmetry.utility.log.Log;

public class SwingUtil {

	/**
	 * Cause the calling thread to wait until all events on the AWT event queue at
	 * the time this method was called have been processed. This is done by
	 * submitting a token event on the queue and waiting until the token event is
	 * processed.
	 */
	public static final void swingWait() {
		try {
			EventQueue.invokeAndWait( new WaitToken() );
		} catch( InterruptedException event ) {
			return;
		} catch( InvocationTargetException event ) {
			return;
		}
	}

	public static final void invokeLater( Runnable runnable ) {
		EventQueue.invokeLater( runnable );
	}

	public static final void invokeAndWait( Runnable runnable ) throws InterruptedException, InvocationTargetException {
		EventQueue.invokeAndWait( runnable );
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
	public static final boolean invokeNowOrLater( Runnable runnable ) {
		if( EventQueue.isDispatchThread() ) {
			runnable.run();
			return true;
		} else {
			EventQueue.invokeLater( runnable );
			return false;
		}
	}

	public static final boolean safeInvokeAndWait( Runnable runnable ) throws InterruptedException, InvocationTargetException {
		if( EventQueue.isDispatchThread() ) {
			runnable.run();
			return true;
		} else {
			EventQueue.invokeAndWait( runnable );
			return false;
		}
	}

	/**
	 * Convenience method to get a child component by name.
	 * 
	 * @param container
	 * @param name
	 * @return
	 */
	public static final Component getNamedComponent( Container container, String name ) {
		if( container == null ) throw new NullPointerException( "Container cannot be null." );
		if( name == null ) throw new NullPointerException( "Name cannot be null." );
		for( Component component : container.getComponents() ) {
			if( name.equals( component.getName() ) ) return component;
		}
		return null;
	}

	public static final void printComponentHierarchy( Container container ) {
		printComponentHierarchy( container, 0 );
	}

	public static final void printComponentHierarchy( Container container, int indent ) {
		String indentString = TextUtil.pad( indent );
		for( Component component : container.getComponents() ) {
			Log.write( Log.DEBUG, indentString, component );
			if( component instanceof Container ) printComponentHierarchy( (Container)component, indent + 1 );
		}
	}

	public static final void printComponetAncestry( Component component ) {
		List<Component> tree = new ArrayList<Component>();
		while( component != null ) {
			tree.add( component );
			component = component.getParent();
		}

		Collections.reverse( tree );

		int index = 0;
		for( Component parent : tree ) {
			Log.write( Log.DEBUG, TextUtil.pad( index++ ), parent );
		}
	}

	private static class WaitToken implements Runnable {

		@Override
		public void run() {}

	}

}
