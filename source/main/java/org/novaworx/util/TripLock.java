package org.novaworx.util;

public class TripLock {
	
	private volatile boolean tripped;

	public synchronized void hold() {
		while( !tripped ) {
			try {
				wait();
			} catch( InterruptedException exception ) {
				// Allow thread to exit.
			}
		}
	}

	public synchronized void trip() {
		tripped = true;
		notifyAll();
	}
	
	public synchronized void reset() {
		trip();
		tripped = false;
	}

}
