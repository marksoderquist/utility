package com.parallelsymmetry.utility.agent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.parallelsymmetry.utility.Controllable;
import com.parallelsymmetry.utility.log.Log;

public abstract class Agent implements Controllable {

	public enum State {
		STARTING, STARTED, STOPPING, STOPPED, CONNECTING, CONNECTED, DISCONNECTING, DISCONNECTED
	}

	private String name;

	// Values locks and conditions for managing the state.
	private State state = State.STOPPED;

	private ReentrantLock stateLock = new ReentrantLock();

	private Condition stateChanging = stateLock.newCondition();

	// Values locks and conditions for managing transitions.
	private boolean transitionFlag;

	private ReentrantLock transitionLock = new ReentrantLock();

	private Condition transitionRunning = transitionLock.newCondition();

	// The agent listener collection.
	private Set<AgentListener> listeners = new HashSet<AgentListener>();

	protected Agent() {
		this( null );
	}

	protected Agent( String name ) {
		setName( name );
	}

	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name == null ? getClass().getSimpleName() : name;
	}

	/**
	 * Convenience method to check if the agent is currently running. The agent is
	 * running if the state is STARTED.
	 * 
	 * @return True if running, false otherwise.
	 */
	@Override
	public boolean isRunning() {
		return state == State.STARTED;
	}

	/**
	 * Start the agent. This method changes the agent state to STARTING and
	 * returns immediately.
	 */
	@Override
	public final void start() {
		transitionLock.lock();
		try {
			if( state != State.STOPPED ) return;

			// Set the starting flag to synchronize with external callers.
			transitionFlag = true;
			transitionRunning.signalAll();

			// Change the state to starting before running the start task.
			changeState( State.STARTING );

			// Run the start task on a separate thread.
			Thread thread = new Thread( new StartTransition() );
			thread.setPriority( Thread.NORM_PRIORITY + 2 );
			thread.start();
		} finally {
			transitionLock.unlock();
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
	@Override
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
	@Override
	public final void startAndWait( long timeout, TimeUnit unit ) throws InterruptedException {
		transitionLock.lock();
		try {
			start();
			waitForStartup( timeout, unit );
		} finally {
			transitionLock.unlock();
		}
	}

	/**
	 * Stop the agent. This method changes the agent state to STOPPING and returns
	 * immediately.
	 */
	@Override
	public final void stop() {
		transitionLock.lock();
		try {
			if( state != State.STARTED ) return;

			// Set the stopping flag to synchronize with external callers.
			transitionFlag = true;
			transitionRunning.signalAll();

			// Change the state to stopping before running the stop task.
			changeState( State.STOPPING );

			// Run the stop task on a separate thread.
			Thread thread = new Thread( new StopTransition(), "Agent stop thread" );
			thread.setPriority( Thread.MAX_PRIORITY );
			thread.start();
		} finally {
			transitionLock.unlock();
		}
	}

	/**
	 * Stop the agent and wait for the stop operation to complete.This method
	 * changes the agent state to STOPPING and waits for the agent state to be set
	 * to STOPPED before returning.
	 * 
	 * @throws InterruptedException
	 */
	@Override
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
	@Override
	public final void stopAndWait( long timeout, TimeUnit unit ) throws InterruptedException {
		transitionLock.lock();
		try {
			stop();
			waitForShutdown( timeout, unit );
		} finally {
			transitionLock.unlock();
		}
	}

	/**
	 * Restart the agent.
	 * 
	 * @throws InterruptedException
	 */
	@Override
	public final void restart() throws InterruptedException {
		restart( 0, TimeUnit.SECONDS );
	}

	/**
	 * Restart the agent.
	 * 
	 * @param timeout
	 * @throws InterruptedException
	 */
	@Override
	public final void restart( long timeout, TimeUnit unit ) throws InterruptedException {
		transitionLock.lock();
		try {
			transitionFlag = true;
			transitionRunning.signalAll();

			// Don't use start() and stop() because they are asynchronous.
			stopAndWait( timeout / 2, unit );
			startAndWait( timeout / 2, unit );
		} finally {
			transitionLock.unlock();
		}
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
		transitionLock.lock();
		try {
			while( transitionFlag ) {
				transitionRunning.await( timeout, unit );
			}
		} finally {
			transitionLock.unlock();
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
		transitionLock.lock();
		try {
			while( transitionFlag ) {
				transitionRunning.await( timeout, unit );
			}
		} finally {
			transitionLock.unlock();
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
			Log.write( throwable );
			try {
				shutdown();
			} catch( Throwable stopThrowable ) {
				Log.write( stopThrowable );
			} finally {
				changeState( State.STOPPED );
			}
		} finally {
			transitionLock.lock();
			try {
				transitionFlag = false;
				transitionRunning.signalAll();
			} finally {
				transitionLock.unlock();
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
			transitionLock.lock();
			try {
				transitionFlag = false;
				transitionRunning.signalAll();
			} finally {
				transitionLock.unlock();
			}
		}
	}

	private final void changeState( State state ) {
		if( this.state == state ) return;

		stateLock.lock();
		try {
			this.state = state;
			stateChanging.signalAll();
		} finally {
			stateLock.unlock();
		}

		fireEvent( state );
	}

	private final class StartTransition implements Runnable {

		public void run() {
			startup();
		}

	}

	private final class StopTransition implements Runnable {

		public void run() {
			shutdown();
		}

	}

}
