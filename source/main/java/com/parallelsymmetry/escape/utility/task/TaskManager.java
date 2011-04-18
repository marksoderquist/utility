package com.parallelsymmetry.escape.utility.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.parallelsymmetry.escape.utility.Controllable;
import com.parallelsymmetry.escape.utility.log.Log;
import com.parallelsymmetry.escape.utility.setting.Persistent;
import com.parallelsymmetry.escape.utility.setting.Settings;

public class TaskManager implements Persistent<TaskManager>, Controllable {

	private static final int DEFAULT_THREAD_COUNT = 5;

	private ExecutorService service;

	private int threadCount = DEFAULT_THREAD_COUNT;

	private Settings settings;

	private BlockingQueue<Runnable> queue;

	public TaskManager() {
		queue = new LinkedBlockingQueue<Runnable>();
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount( int count ) {
		if( count < 1 ) count = 1;
		this.threadCount = count;
		saveSettings( settings );
		if( isRunning() ) restart();
	}

	@Override
	public synchronized void start() {
		if( isRunning() ) return;
		int min = Math.min( Runtime.getRuntime().availableProcessors(), threadCount );
		int max = Math.max( Runtime.getRuntime().availableProcessors(), threadCount );
		service = new ThreadPoolExecutor( min, max, 5, TimeUnit.SECONDS, queue, new TaskThreadFactory() );
	}

	@Override
	public synchronized void startAndWait() throws InterruptedException {
		start();
	}

	@Override
	public synchronized void startAndWait( long timeout, TimeUnit unit ) throws InterruptedException {
		start();
	}

	public void restart() {
		try {
			stopAndWait();
		} catch( InterruptedException exception ) {
			Log.write( exception );
		}
		start();
	}

	@Override
	public synchronized void stop() {
		if( service == null || service.isShutdown() ) return;
		service.shutdown();
	}

	@Override
	public synchronized void stopAndWait() throws InterruptedException {
		stopAndWait( 0, TimeUnit.SECONDS );
	}

	@Override
	public synchronized void stopAndWait( long timeout, TimeUnit unit ) throws InterruptedException {
		stop();
		if( service != null ) service.awaitTermination( timeout, unit );
	}

	@Override
	public boolean isRunning() {
		return service != null && !service.isTerminated();
	}

	/**
	 * Asynchronously submit a task.
	 * 
	 * @param <T>
	 * @param task
	 * @return
	 */
	public <T> Future<T> submit( Task<T> task ) {
		checkNullService();

		// If submitted on a task thread then just execute the task.

		return service.submit( task );
	}

	/**
	 * Asynchronously submit a collection of tasks.
	 * 
	 * @param <T>
	 * @param tasks
	 * @return
	 */
	public <T> List<Future<T>> submitAll( Collection<? extends Task<T>> tasks ) {
		checkNullService();
		List<Future<T>> futures = new ArrayList<Future<T>>();

		for( Task<T> task : tasks ) {
			futures.add( service.submit( task ) );
		}

		return futures;
	}

	/**
	 * Synchronously submit a task.
	 * 
	 * @param <T>
	 * @param task
	 * @return
	 * @throws InterruptedException
	 */
	public <T> Future<T> invoke( Task<T> task ) throws InterruptedException {
		List<Task<T>> tasks = new ArrayList<Task<T>>();
		tasks.add( task );
		return invokeAll( tasks ).get( 0 );
	}

	/**
	 * Synchronously submit a task.
	 * 
	 * @param <T>
	 * @param task
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	public <T> Future<T> invoke( Task<T> task, long timeout, TimeUnit unit ) throws InterruptedException {
		List<Task<T>> tasks = new ArrayList<Task<T>>();
		tasks.add( task );
		return invokeAll( tasks, timeout, unit ).get( 0 );
	}

	/**
	 * Synchronously submit a collection of tasks.
	 * 
	 * @param <T>
	 * @param tasks
	 * @return
	 * @throws InterruptedException
	 */
	public <T> List<Future<T>> invokeAll( Collection<? extends Task<T>> tasks ) throws InterruptedException {
		checkNullService();
		return service.invokeAll( tasks );
	}

	/**
	 * Synchronously submit a collection of tasks.
	 * 
	 * @param <T>
	 * @param tasks
	 * @param timeout
	 * @param unit
	 * @return
	 * @throws InterruptedException
	 */
	public <T> List<Future<T>> invokeAll( Collection<? extends Task<T>> tasks, long timeout, TimeUnit unit ) throws InterruptedException {
		checkNullService();
		return service.invokeAll( tasks, timeout, unit );
	}

	@Override
	public TaskManager loadSettings( Settings settings ) {
		this.settings = settings;

		this.threadCount = settings.getInt( "thread-count", DEFAULT_THREAD_COUNT );

		return this;
	}

	@Override
	public TaskManager saveSettings( Settings settings ) {
		if( settings == null ) return this;

		settings.putInt( "thread-count", threadCount );

		return this;
	}

	private void checkNullService() {
		if( service == null ) throw new RuntimeException( "TaskManager has not been started." );
	}

	private static final class TaskThreadFactory implements ThreadFactory {

		private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

		public Thread newThread( final Runnable runnable ) {
			Thread thread = defaultFactory.newThread( runnable );
			thread.setName( "TaskQueue-" + thread.getName() );
			thread.setDaemon( true );
			return thread;
		}
	}

}
