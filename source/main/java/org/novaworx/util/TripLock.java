/*
 * Copyright (C) 2007 Mark Soderquist
 */
package org.novaworx.util;

/**
 * The TripLock class is thread synchronization tool intended to block threads
 * if not tripped, release the threads when tripped, and not to block threads if
 * already tripped.
 * 
 * @author Mark Soderquist
 */
public class TripLock {

	private volatile boolean tripped;

	/**
	 * Block the calling thread if the lock has not been tripped or return
	 * immediately if the lock has already been tripped.
	 */
	public synchronized void hold() {
		while( !tripped ) {
			try {
				wait();
			} catch( InterruptedException exception ) {
				// Allow thread to exit.
			}
		}
	}

	/**
	 * Trip the lock. This relases all threads blocked by the hold() method and
	 * sets the tripped flag so that no other threads will be blocked by a call to
	 * hold().
	 */
	public synchronized void trip() {
		tripped = true;
		notifyAll();
	}

}
