package org.novaworx.util;

import junit.framework.TestCase;

public class TripLockTest extends TestCase {

	public void testHoldAndTrip() throws Exception {
		TripLock lock = new TripLock();
		Holder holder = new Holder( lock );
		holder.start();
		lock.trip();
		assertTrue( holder.released() );
	}

	private static class Holder implements Runnable {

		private TripLock lock;

		private Thread thread;

		private boolean released;

		private TripLock startLock = new TripLock();

		private TripLock releaseLock = new TripLock();

		public Holder( TripLock lock ) {
			this.lock = lock;
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
