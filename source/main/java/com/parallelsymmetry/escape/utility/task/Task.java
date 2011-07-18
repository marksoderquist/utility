package com.parallelsymmetry.escape.utility.task;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.parallelsymmetry.escape.utility.ThreadUtil;

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
		WAITING, RUNNING, DONE;
	}

	public enum Result {
		UNKNOWN, CANCELLED, SUCCESS, FAILED;
	}

	private Object stateLock = new Object();

	private State state = State.WAITING;

	private Result result = Result.UNKNOWN;

	private FutureTask<V> future;

	private Set<TaskListener> listeners;

	public Task() {
		future = new TaskFuture<V>( this, new TaskExecute<V>( this ) );
		listeners = new CopyOnWriteArraySet<TaskListener>();
	}

	@Override
	public V call() throws Exception {
		setState( State.RUNNING );
		fireTaskEvent( TaskEvent.Type.TASK_START );

		future.run();
		return future.get();
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

	public void waitForState( State state ) throws InterruptedException {
		synchronized( stateLock ) {
			while( this.state != state ) {
				stateLock.wait();
			}
		}
	}

	private void setState( State state ) {
		synchronized( stateLock ) {
			this.state = state;
			stateLock.notifyAll();
		}
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
		setState( State.RUNNING );
		fireTaskEvent( TaskEvent.Type.TASK_START );

		future.run();
		return future.get();
	}

	V invoke( long timeout, TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException {
		setState( State.RUNNING );
		fireTaskEvent( TaskEvent.Type.TASK_START );

		future.run();
		return future.get( timeout, unit );
	}

	private static class TaskFuture<W> extends FutureTask<W> {

		private Task<?> task;

		public TaskFuture( Task<?> task, Callable<W> callable ) {
			super( callable );
			this.task = task;
		}

		@Override
		protected void done() {
			task.setState( State.DONE );
			ThreadUtil.pause( 5 );
			task.fireTaskEvent( TaskEvent.Type.TASK_FINISH );
			super.done();
		}

		@Override
		protected void set( W value ) {
			task.result = Result.SUCCESS;
			super.set( value );
		}

		@Override
		protected void setException( Throwable throwable ) {
			task.result = Result.FAILED;
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
