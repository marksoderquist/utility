package com.parallelsymmetry.utility.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Panel;

import junit.framework.TestCase;

public class SwingUtilTest extends TestCase {

	public void testSwingWait() {
		ExecuteTester tester = new ExecuteTester();

		EventQueue.invokeLater( tester );
		SwingUtil.swingWait();
		assertTrue( tester.isDone() );
	}

	public void testExecuteSafely() {
		ExecuteTester tester = new ExecuteTester();

		tester.reset();
		tester.run();
		assertFalse( tester.isSafe() );

		tester.reset();
		SwingUtil.executeSafely( tester );
		tester.waitFor();
		assertTrue( tester.isSafe() );
	}

	public void testGetNamedComponent() {
		Container container = new Container();

		Component one = new Panel();
		one.setName( "one" );
		Component two = new Panel();
		two.setName( "two" );

		container.add( one );
		container.add( two );

		assertNull( SwingUtil.getNamedComponent( container, "zero" ) );
		assertEquals( one, SwingUtil.getNamedComponent( container, "one" ) );
		assertEquals( two, SwingUtil.getNamedComponent( container, "two" ) );
	}

	private static final class ExecuteTester implements Runnable {

		private boolean done;

		private boolean safe;

		@Override
		public synchronized void run() {
			safe = EventQueue.isDispatchThread();
			done = true;
			notifyAll();
		}

		public boolean isDone() {
			return done;
		}

		public boolean isSafe() {
			return safe;
		}

		public void reset() {
			done = false;
		}

		public synchronized void waitFor() {
			while( !done ) {
				try {
					wait();
				} catch( InterruptedException exception ) {
					return;
				}
			}
		}

	}

}
