package com.parallelsymmetry.escape.utility.task;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An executable task.
 * <p>
 * The correct way to wait for the result is to obtain the Future object from
 * the call to submit( Task ) and then call future.get().
 * 
 * @author Mark Soderquist
 * @param <V> The return type of the task.
 */

// WARNING! Do not create a waitFor() method. See JavaDoc above. 

public abstract class Task<V> implements Callable<V>, Future<V> {

	public enum State {
		WAITING, RUNNING, COMPLETE;
	}

	public enum Result {
		UNKNOWN, CANCELLED, SUCCESS, FAILED;
	}

	private State state = State.WAITING;

	private Result result = Result.UNKNOWN;

	private FutureTask<V> future;

	private Set<TaskListener> listeners;

	public Task() {
		future = new TaskFuture<V>( new TaskExecute<V>( this ) );
		listeners = new CopyOnWriteArraySet<TaskListener>();
	}

	/**
	 * 
	 */
	@Override
	public V call() throws Exception {
		return invoke();
	}

	public abstract V execute() throws Exception;

	@Override
	public boolean isDone() {
		return future.isDone();
	}

	@Override
	public final boolean isCancelled() {
		return future.isCancelled();
	}

	public final State getState() {
		return state;
	}

	public final Result getResult() {
		return result;
	}

	@Override
	public final boolean cancel( boolean mayInterruptIfRunning ) {
		return future.cancel( mayInterruptIfRunning );
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		return future.get();
	}

	@Override
	public V get( long timeout, TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException {
		return future.get( timeout, unit );
	}

	public void addTaskListener( TaskListener listener ) {
		listeners.add( listener );
	}

	public void removeTaskListener( TaskListener listener ) {
		listeners.remove( listener );
	}

	protected void fireTaskEvent( TaskEvent.Type type ) {
		TaskEvent event = new TaskEvent( this, this, type );
		for( TaskListener listener : listeners ) {
			listener.handleEvent( event );
		}
	}

	V invoke() throws InterruptedException, ExecutionException {
		state = State.RUNNING;
		fireTaskEvent( TaskEvent.Type.TASK_START );

		try {
			future.run();
		} finally {
			state = State.COMPLETE;
		}

		try {
			V value = future.get();
			result = Result.SUCCESS;
			return value;
		} catch( CancellationException exception ) {
			result = Result.CANCELLED;
			throw exception;
		} catch( ExecutionException exception ) {
			result = Result.FAILED;
			throw exception;
		} finally {
			fireTaskEvent( TaskEvent.Type.TASK_FINISH );
		}
	}

	V invoke( long timeout, TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException {
		state = State.RUNNING;
		fireTaskEvent( TaskEvent.Type.TASK_START );

		try {
			future.run();
		} finally {
			state = State.COMPLETE;
		}

		try {
			V value = future.get( timeout, unit );
			result = Result.SUCCESS;
			return value;
		} catch( CancellationException exception ) {
			result = Result.CANCELLED;
			throw exception;
		} catch( ExecutionException exception ) {
			result = Result.FAILED;
			throw exception;
		} finally {
			fireTaskEvent( TaskEvent.Type.TASK_FINISH );
		}
	}

	private static class TaskFuture<W> extends FutureTask<W> {

		public TaskFuture( Callable<W> callable ) {
			super( callable );
		}

		@Override
		protected void done() {
			super.done();
		}

		@Override
		protected void set( W value ) {
			super.set( value );
			try {
				super.get();
			} catch( Exception exception ) {}
		}

		@Override
		protected void setException( Throwable throwable ) {
			super.setException( throwable );
		}

	}

	private static class TaskExecute<W> implements Callable<W> {

		private Task<W> task;

		public TaskExecute( Task<W> task ) {
			this.task = task;
		}

		@Override
		public W call() throws Exception {
			return task.execute();
		}

	}

}
