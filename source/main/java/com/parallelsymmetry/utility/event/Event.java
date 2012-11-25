package com.parallelsymmetry.utility.event;

import java.awt.EventQueue;

/**
 * <p>
 * The Event class is the superclass of all application events.
 * <p>
 * Event priorities range from 0 to 9 with 5 being the default.
 * 
 * @author Mark Soderquist
 */
public class Event implements Comparable<Event> {

	public static final int HIGHEST_PRIORITY = 9;

	public static final int DEFAULT_PRIORITY = 5;

	public static final int LOWEST_PRIORITY = 1;

	private volatile EventBus bus;

	private Object source;

	private Object scope;

	private Throwable callStackThrowable;

	private int priority = DEFAULT_PRIORITY;

	private transient boolean cancelled;

	private transient boolean processed;

	public Event( Object source ) {
		this( source, null, DEFAULT_PRIORITY );
	}

	public Event( Object source, Object scope ) {
		this( source, scope, DEFAULT_PRIORITY );
	}

	public Event( Object source, int priority ) {
		this( source, null, priority );
	}

	public Event( Object source, Object scope, int priority ) {
		this.source = source;
		this.scope = scope;
		this.priority = priority;
	}

	public Object getSource() {
		return source;
	}

	public Object getScope() {
		return scope;
	}

	public void setScope( Object scope ) {
		this.scope = scope;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority( int priority ) {
		if( priority < LOWEST_PRIORITY ) priority = LOWEST_PRIORITY;
		if( priority > HIGHEST_PRIORITY ) priority = HIGHEST_PRIORITY;
		this.priority = priority;
	}

	public synchronized void processed() {
		processed = true;
		notifyAll();
	}

	/**
	 * Wait for the event to be processed.
	 * 
	 * @return True if the event not interrupted, false otherwise.
	 */
	public synchronized boolean waitFor() {
		if( EventQueue.isDispatchThread() ) throw new RuntimeException( "The dispatch thread should not be blocked." );
		if( bus != null && bus.isEventBusThread() ) throw new RuntimeException( "The event bus thread should not be blocked." );

		if( isCancelled() ) return true;

		while( !processed ) {
			try {
				wait();
			} catch( InterruptedException exception ) {
				return false;
			}
		}
		return true;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void cancel() {
		cancelled = true;
	}

	@Override
	public int compareTo( Event event ) {
		return event.getPriority() - getPriority();
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

	Throwable getCallStackThrowable() {
		return callStackThrowable;
	}

	void setCallStackThrowable( Throwable caller ) {
		this.callStackThrowable = caller;
	}

	void setEventBus( EventBus bus ) {
		this.bus = bus;
	}

}
