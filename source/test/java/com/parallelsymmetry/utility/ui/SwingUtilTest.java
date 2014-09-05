package com.parallelsymmetry.utility.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Panel;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JPanel;

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
		SwingUtil.invokeNowOrLater( tester );
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

	public void testGetChildOfType() {
		JPanel parent = new JPanel();
		JPanel container1 = new JPanel();
		JPanel container2 = new JPanel();

		JButton button = new JButton();

		parent.add( container1 );
		parent.add( container2 );
		container2.add( button );

		parent.setBounds( 0, 0, 100, 100 );
		container1.setBounds( 20, 20, 60, 60 );
		container2.setBounds( 20, 20, 60, 60 );
		button.setBounds( 20, 20, 20, 20 );

		Component component = SwingUtil.getChildOfType( parent, JButton.class, new Point( 50, 50 ) );
		assertNotNull( component );
		assertSame( button, component );
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
