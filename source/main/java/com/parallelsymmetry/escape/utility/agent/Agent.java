package com.parallelsymmetry.escape.utility.agent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.parallelsymmetry.escape.utility.log.Log;

public abstract class Agent {

	public enum State {
		STARTING, STARTED, STOPPING, STOPPED, CONNECTING, CONNECTED, DISCONNECTING, DISCONNECTED
	}

	private String name;

	private Thread thread;

	private State state = State.STOPPED;

	private Set<AgentListener> listeners = new HashSet<AgentListener>();

	protected Agent() {
		this( null );
	}

	protected Agent( String name ) {
		setName( name );

		thread = new Thread( new AgentRunner(), getName() );
		thread.setPriority( Thread.NORM_PRIORITY );
		thread.setDaemon( true );
		thread.start();
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name == null ? getClass().getSimpleName() : name;
	}

	/**
	 * Start the agent. This method changes the agent state to STARTING and
	 * returns immediately.
	 */
	public final void start() {
		if( state == State.STOPPED ) changeState( State.STARTING );
	}

	/**
	 * Start the agent and wait for the start operation to complete. This method
	 * changes the agent state to STARTING and waits for the agent state to be set
	 * to STARTED before returning.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public final void startAndWait() throws InterruptedException {
		startAndWait( 0 );
	}

	/**
	 * Start the agent and wait for the start operation to complete. This method
	 * changes the agent state to STARTING and waits for the agent state to be set
	 * to STARTED, or the timeout has elapsed, before returning. A timeout of zero
	 * will wait indefinitely.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public final void startAndWait( int timeout ) throws InterruptedException {
		start();
		waitForStartup( timeout );
	}

	/**
	 * Stop the agent. This method changes the agent state to STOPPING and returns
	 * immediately.
	 */
	public final void stop() {
		if( state == State.STARTED ) changeState( State.STOPPING );
	}

	/**
	 * Stop the agent and wait for the stop operation to complete.This method
	 * changes the agent state to STOPPING and waits for the agent state to be set
	 * to STOPPED before returning.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public final void stopAndWait() throws Exception {
		stopAndWait( 0 );
	}

	/**
	 * Stop the agent and wait for the stop operation to complete.This method
	 * changes the agent state to STOPPING and waits for the agent state to be set
	 * to STOPPED, or the timeout has elapsed, before returning. A timeout of zero
	 * will wait indefinitely.
	 * 
	 * @param timeout
	 * @throws Exception
	 */
	public final void stopAndWait( int timeout ) throws Exception {
		stop();
		waitForShutdown( timeout );
	}

	/**
	 * Restart the agent. This method waits indefinitely for all operations to
	 * complete before returning.
	 * 
	 * @throws IOException
	 */
	public final void restart() throws Exception {
		// Don't use start() and stop() because they are asynchronous.
		stopAndWait();
		startAndWait();
	}

	/**
	 * Convenience method to check if the agent is currently running. The agent is
	 * running if the state is STARTED.
	 * 
	 * @return True if running, false otherwise.
	 */
	public final boolean isRunning() {
		return state == State.STARTED;
	}

	public final boolean isAgentThread() {
		return Thread.currentThread() == thread;
	}

	public final boolean shouldExecute() {
		State state = this.state;
		return state == State.STARTED || state == State.STARTING;
	}

	public final State getState() {
		return state;
	}

	public final String getStatus() {
		return state.toString();
	}

	/**
	 * Wait indefinitely for the start operation to complete. Returns immediately
	 * if the agent is already started.
	 * 
	 * @throws InterruptedException
	 */
	public final void waitForStartup() throws InterruptedException {
		waitForStartup( 0 );
	}

	/**
	 * Wait a specific amount of time for the start operation to complete. Returns
	 * immediately if the agent is already started.
	 * 
	 * @param timeout
	 * @throws InterruptedException
	 */
	public final void waitForStartup( int timeout ) throws InterruptedException {
		waitForState( State.STARTED, timeout );
	}

	/**
	 * Wait indefinitely for the stop operation to complete. Returns immediately
	 * if the agent is already stopped.
	 * 
	 * @throws InterruptedException
	 */
	public final void waitForShutdown() throws InterruptedException {
		waitForShutdown( 0 );
	}

	/**
	 * Wait a specific amount of time for the stop operation to complete. Returns
	 * immediately if the agent is already stopped.
	 * 
	 * @param timeout
	 * @throws InterruptedException
	 */
	public final void waitForShutdown( int timeout ) throws InterruptedException {
		waitForState( State.STOPPED, timeout );
	}

	public final void addListener( AgentListener listener ) {
		listeners.add( listener );
	}

	public final void removeListener( AgentListener listener ) {
		listeners.remove( listener );
	}

	/**
	 * Subclasses implement this method to start the agent. Implementations of
	 * this method should return when the agent is started. This usually means
	 * starting a separate thread, waiting for the new thread to notify the
	 * calling thread that it has started successfully, and then returning.
	 * 
	 * @throws IOException
	 */
	protected abstract void startAgent() throws Exception;

	/**
	 * Subclasses implement this method to stop the agent. Implementations of this
	 * method should return when the agent has stopped. This usually means
	 * stopping a separate thread that was started previously and waiting for the
	 * thread to terminate before returning.
	 * 
	 * @throws IOException
	 */
	protected abstract void stopAgent() throws Exception;

	protected final void fireEvent( State state ) {
		fireEvent( new AgentEvent( this, state ) );
	}

	protected final void fireEvent( AgentEvent event ) {
		Log.write( Log.DEBUG, getName(), " Event: ", event.getState() );
		for( AgentListener listener : listeners ) {
			listener.agentEventOccurred( event );
		}
	}

	private final void startup() throws Exception {
		try {
			startAgent();
		} finally {
			changeState( State.STARTED );
		}
	}

	private final void shutdown() throws Exception {
		try {
			stopAgent();
		} finally {
			changeState( State.STOPPED );
		}
	}

	private Object statelock = new Object();

	private void changeState( State state ) {
		if( this.state == state ) return;

		synchronized( statelock ) {
			this.state = state;
			statelock.notifyAll();
		}

		fireEvent( state );
	}

	private void waitForState( State state ) throws InterruptedException {
		waitForState( state, 0 );
	}

	private void waitForState( State state, int timeout ) throws InterruptedException {
		synchronized( statelock ) {
			long mark = System.currentTimeMillis();
			while( this.state != state ) {
				statelock.wait( timeout );
				if( timeout > 0 && System.currentTimeMillis() - mark > timeout ) return;
			}
		}
	}

	private final class AgentRunner implements Runnable {

		/**
		 * The implementation of the Runnable interface.
		 */
		@Override
		public void run() {
			try {
				while( true ) {
					waitForState( State.STARTING );
					try {
						startup();
					} catch( Exception exception ) {
						Log.write( exception );
					}
					waitForState( State.STOPPING );
					try {
						shutdown();
					} catch( Exception exception ) {
						Log.write( exception );
					}
				}
			} catch( InterruptedException exception ) {
				Log.write( Log.ERROR, exception, "AgentRunner terminated." );
			}
		}

	}

}
