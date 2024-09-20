package com.parallelsymmetry.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TripLockTest extends BaseTestCase {

	@Test
	public void testHoldAndTripWaitingCaller() throws Exception {
		TripLock lock = new TripLock();
		Holder holder = new Holder( lock );
		holder.start();
		ThreadUtil.pause( 50 );
		lock.trip();
		assertTrue( holder.released() );
	}

	@Test
	public void testHoldAndTripWaitingHolder() throws Exception {
		TripLock lock = new TripLock();
		Holder holder = new Holder( lock, 50 );
		holder.start();
		lock.trip();
		assertTrue( holder.released() );
	}

	private static class Holder implements Runnable {

		private final TripLock lock;

		private final int pause;

		private boolean released;

		private final TripLock startLock = new TripLock();

		private final TripLock releaseLock = new TripLock();

		public Holder( TripLock lock ) {
			this( lock, 0 );
		}

		public Holder( TripLock lock, int pause ) {
			this.lock = lock;
			this.pause = pause;
		}

		public void start() {
			Thread thread = new Thread( this );
			thread.setPriority( Thread.NORM_PRIORITY );
			thread.setDaemon( true );
			thread.start();
			startLock.hold();
		}

		@Override
		public synchronized void run() {
			startLock.trip();
			if( pause > 0 ) ThreadUtil.pause( pause );
			lock.hold();
			released = true;
			releaseLock.trip();
		}

		public synchronized boolean released() {
			releaseLock.hold();
			return released;
		}

	}

}
