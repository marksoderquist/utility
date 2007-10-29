package com.parallelsymmetry.util;

import com.parallelsymmetry.util.ThreadUtil;
import com.parallelsymmetry.util.TripLock;

import junit.framework.TestCase;

public class TripLockTest extends TestCase {

	public void testHoldAndTripWaitingCaller() throws Exception {
		TripLock lock = new TripLock();
		Holder holder = new Holder( lock );
		holder.start();
		ThreadUtil.pause( 50 );
		lock.trip();
		assertTrue( holder.released() );
	}

	public void testHoldAndTripWaitingHolder() throws Exception {
		TripLock lock = new TripLock();
		Holder holder = new Holder( lock, 50 );
		holder.start();
		lock.trip();
		assertTrue( holder.released() );
	}

	private static class Holder implements Runnable {

		private TripLock lock;

		private int pause;

		private Thread thread;

		private boolean released;

		private TripLock startLock = new TripLock();

		private TripLock releaseLock = new TripLock();

		public Holder( TripLock lock ) {
			this( lock, 0 );
		}

		public Holder( TripLock lock, int pause ) {
			this.lock = lock;
			this.pause = pause;
		}

		public void start() {
			thread = new Thread( this );
			thread.setPriority( Thread.NORM_PRIORITY );
			thread.setDaemon( true );
			thread.start();
			startLock.hold();
		}

		public synchronized void run() {
			startLock.trip();
			if( pause > 0 ) ThreadUtil.pause( pause );
			lock.hold();
			released = true;
			releaseLock.trip();
		}

		public synchronized boolean released() throws InterruptedException {
			releaseLock.hold();
			return released;
		}

	}

}
