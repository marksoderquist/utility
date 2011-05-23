package com.parallelsymmetry.escape.utility.task;

import java.util.concurrent.Callable;
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

	private boolean running;

	private FutureTask<V> future;

	public Task() {
		future = new TaskFuture<V>( new TaskExecute<V>( this ) );
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

	public final boolean isRunning() {
		return running && !future.isDone();
	}

	@Override
	public final boolean isCancelled() {
		return future.isCancelled();
	}

	public final boolean isSuccess() {
		if( !isRunning() && !isDone() ) return false;

		try {
			future.get();
			return true;
		} catch( Throwable throwable ) {
			return false;
		}
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

	V invoke() throws InterruptedException, ExecutionException {
		running = true;
		fireTaskEvent();

		try {
			future.run();
			return future.get();
		} finally {
			running = false;
			fireTaskEvent();
		}
	}

	V invoke( long timeout, TimeUnit unit ) throws InterruptedException, ExecutionException, TimeoutException {
		running = true;
		fireTaskEvent();

		try {
			future.run();
			return future.get( timeout, unit );
		} finally {
			running = false;
			fireTaskEvent();
		}
	}

	protected void fireTaskEvent() {
		// TODO Implement task events.
	}

	private class TaskFuture<W> extends FutureTask<W> {

		public TaskFuture( Callable<W> callable ) {
			super( callable );
		}

		@Override
		protected void done() {
			super.done();
			fireTaskEvent();
		}

		@Override
		protected void set( W value ) {
			super.set( value );
			try {
				super.get();
			} catch( Exception exception ) {}
			fireTaskEvent();
		}

		@Override
		protected void setException( Throwable throwable ) {
			super.setException( throwable );
			fireTaskEvent();
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
