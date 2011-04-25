package com.parallelsymmetry.escape.utility.agent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.parallelsymmetry.escape.utility.Controllable;
import com.parallelsymmetry.escape.utility.log.Log;

public abstract class Agent implements Controllable {

	public enum State {
		STARTING, STARTED, STOPPING, STOPPED, CONNECTING, CONNECTED, DISCONNECTING, DISCONNECTED
	}

	private String name;

	private Thread thread;

	private State state = State.STOPPED;

	private Set<AgentListener> listeners = new HashSet<AgentListener>();

	private Object statelock = new Object();

	private Object operationLock = new Object();

	private boolean startingFlag;

	private boolean stoppingFlag;

	protected Agent() {
		this( null );
	}

	protected Agent( String name ) {
		thread = new Thread( new AgentRunner() );
		thread.setPriority( Thread.NORM_PRIORITY );
		thread.setDaemon( true );
		thread.start();
		setName( name );
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name == null ? getClass().getSimpleName() : name;
		thread.setName( "Agent - " + this.name );
	}

	/**
	 * Convenience method to check if the agent is currently running. The agent is
	 * running if the state is STARTED.
	 * 
	 * @return True if running, false otherwise.
	 */
	public boolean isRunning() {
		return state == State.STARTED;
	}

	/**
	 * Start the agent. This method changes the agent state to STARTING and
	 * returns immediately.
	 */
	public final void start() {
		synchronized( operationLock ) {
			if( state != State.STOPPED ) return;

			// Set the starting flag to synchronize with external callers.
			startingFlag = true;
			operationLock.notifyAll();

			// Change the state so the agent thread can start the agent.
			changeState( State.STARTING );
		}
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
		startAndWait( 0, TimeUnit.SECONDS );
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
	public final void startAndWait( long timeout, TimeUnit unit ) throws InterruptedException {
		synchronized( operationLock ) {
			start();
			waitForStartup( timeout, unit );
		}
	}

	/**
	 * Stop the agent. This method changes the agent state to STOPPING and returns
	 * immediately.
	 */
	public final void stop() {
		synchronized( operationLock ) {
			if( state != State.STARTED ) return;

			// Set the stopping flag to synchronize with external callers.
			stoppingFlag = true;
			operationLock.notifyAll();

			// Change the state so the agent thread can stop the agent.
			changeState( State.STOPPING );
		}
	}

	/**
	 * Stop the agent and wait for the stop operation to complete.This method
	 * changes the agent state to STOPPING and waits for the agent state to be set
	 * to STOPPED before returning.
	 * 
	 * @throws InterruptedException
	 */
	public final void stopAndWait() throws InterruptedException {
		stopAndWait( 0, TimeUnit.SECONDS );
	}

	/**
	 * Stop the agent and wait for the stop operation to complete.This method
	 * changes the agent state to STOPPING and waits for the agent state to be set
	 * to STOPPED, or the timeout has elapsed, before returning. A timeout of zero
	 * will wait indefinitely.
	 * 
	 * @param timeout
	 * @throws InterruptedException
	 */
	public final void stopAndWait( long timeout, TimeUnit unit ) throws InterruptedException {
		synchronized( operationLock ) {
			stop();
			waitForShutdown( timeout, unit );
		}
	}

	/**
	 * Restart the agent.
	 * 
	 * @throws InterruptedException
	 */
	public final void restart() throws InterruptedException {
		restart( 0, TimeUnit.SECONDS );
	}

	/**
	 * Restart the agent.
	 * 
	 * @param timeout
	 * @throws InterruptedException
	 */
	public final void restart( long timeout, TimeUnit unit ) throws InterruptedException {
		synchronized( operationLock ) {
			startingFlag = true;
			stoppingFlag = true;
			operationLock.notifyAll();

			// Don't use start() and stop() because they are asynchronous.
			stopAndWait( timeout / 2, unit );
			startAndWait( timeout / 2, unit );
		}
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
		waitForStartup( 0, TimeUnit.SECONDS );
	}

	/**
	 * Wait a specific amount of time for the start operation to complete. Returns
	 * immediately if the agent is already started.
	 * 
	 * @param timeout
	 * @throws InterruptedException
	 */
	public final void waitForStartup( long timeout, TimeUnit unit ) throws InterruptedException {
		synchronized( operationLock ) {
			while( startingFlag ) {
				operationLock.wait( timeout );
			}
		}
	}

	/**
	 * Wait indefinitely for the stop operation to complete. Returns immediately
	 * if the agent is already stopped.
	 * 
	 * @throws InterruptedException
	 */
	public final void waitForShutdown() throws InterruptedException {
		waitForShutdown( 0, TimeUnit.SECONDS );
	}

	/**
	 * Wait a specific amount of time for the stop operation to complete. Returns
	 * immediately if the agent is already stopped.
	 * 
	 * @param timeout
	 * @throws InterruptedException
	 */
	public final void waitForShutdown( long timeout, TimeUnit unit ) throws InterruptedException {
		synchronized( operationLock ) {
			while( stoppingFlag ) {
				operationLock.wait( timeout );
			}
		}
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
			try {
				listener.agentEventOccurred( event );
			} catch( Throwable throwable ) {
				Log.write( throwable );
			}
		}
	}

	/**
	 * Start the agent. If the start is successful then the state is set to
	 * STARTED. If an exception is thrown the state is set to STOPPED.
	 */
	private final void startup() {
		try {
			startAgent();
			changeState( State.STARTED );
		} catch( Throwable throwable ) {
			changeState( State.STOPPED );
			Log.write( throwable );
		} finally {
			synchronized( operationLock ) {
				startingFlag = false;
				operationLock.notifyAll();
			}
		}
	}

	/**
	 * Stop the agent. If the stop is successful then the state is set to STOPPED.
	 * If an exception is thrown the state is set to STARTED.
	 */
	private final void shutdown() {
		try {
			stopAgent();
			changeState( State.STOPPED );
		} catch( Throwable throwable ) {
			changeState( State.STARTED );
			Log.write( throwable );
		} finally {
			synchronized( operationLock ) {
				stoppingFlag = false;
				operationLock.notifyAll();
			}
		}
	}

	private final void changeState( State state ) {
		if( this.state == state ) return;

		synchronized( statelock ) {
			this.state = state;
			statelock.notifyAll();
		}

		fireEvent( state );
	}

	private final void waitForState( State state ) throws InterruptedException {
		waitForState( state, 0 );
	}

	private final void waitForState( State state, int timeout ) throws InterruptedException {
		synchronized( statelock ) {
			//Log.write( Log.TRACE, "Waiting for " + state + "..." );
			long mark = System.currentTimeMillis();
			while( this.state != state ) {
				statelock.wait( timeout );
				if( timeout > 0 && System.currentTimeMillis() - mark > timeout ) return;
			}
		}
	}

	private final void waitForStateChange( State state ) throws InterruptedException {
		waitForStateChange( state, 0 );
	}

	private final void waitForStateChange( State state, int timeout ) throws InterruptedException {
		synchronized( statelock ) {
			long mark = System.currentTimeMillis();
			while( this.state == state ) {
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
					switch( getState() ) {
						case STOPPED:
						case STARTING: {
							waitForState( State.STARTING );
							startup();
							waitForStateChange( State.STARTING );
							break;
						}
						case STARTED:
						case STOPPING: {
							waitForState( State.STOPPING );
							shutdown();
							waitForStateChange( State.STOPPING );
							break;
						}
					}

				}
			} catch( InterruptedException exception ) {
				Log.write( Log.ERROR, exception, "AgentRunner terminated." );
			}
		}

	}

}
