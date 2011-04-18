package com.parallelsymmetry.escape.utility.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
		if( Thread.currentThread() instanceof TaskThread ) {
			synchronousExecute( task );
		} else {
			List<Task<T>> tasks = new ArrayList<Task<T>>();
			tasks.add( task );
			invokeAll( tasks );
		}

		return task;
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
		if( Thread.currentThread() instanceof TaskThread ) {
			synchronousExecute( task, timeout, unit );
		} else {
			List<Task<T>> tasks = new ArrayList<Task<T>>();
			tasks.add( task );
			invokeAll( tasks, timeout, unit );
		}
		return task;
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
		if( Thread.currentThread() instanceof TaskThread ) {
			synchronousExecute( tasks );
		} else {
			service.invokeAll( tasks );
		}
		return new ArrayList<Future<T>>( tasks );
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
		if( Thread.currentThread() instanceof TaskThread ) {
			synchronousExecute( tasks, timeout, unit );
		} else {
			service.invokeAll( tasks, timeout, unit );
		}
		return new ArrayList<Future<T>>( tasks );
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

	private <T> void synchronousExecute( Task<T> task ) {
		try {
			task.invoke();
		} catch( Exception exception ) {
			// Exceptions should be retrieved by calling get().
		}
	}

	private <T> void synchronousExecute( Task<T> task, long timeout, TimeUnit unit ) {
		try {
			task.invoke( timeout, unit );
		} catch( Exception exception ) {
			// Exceptions should be retrieved by calling get().
		}
	}

	private <T> void synchronousExecute( Collection<? extends Task<T>> tasks ) {
		for( Task<T> task : tasks ) {
			synchronousExecute( task );
		}
	}

	private <T> void synchronousExecute( Collection<? extends Task<T>> tasks, long timeout, TimeUnit unit ) {
		for( Task<T> task : tasks ) {
			synchronousExecute( task, timeout, unit );
		}
	}

	private void checkNullService() {
		if( service == null ) throw new RuntimeException( "TaskManager has not been started." );
	}

	private static final class TaskThread extends Thread {

		public TaskThread( ThreadGroup group, Runnable target, String name, long stackSize ) {
			super( group, target, name, stackSize );
		}

	}

	private static class TaskThreadFactory implements ThreadFactory {

		private static final AtomicInteger poolNumber = new AtomicInteger( 1 );

		private final AtomicInteger threadNumber = new AtomicInteger( 1 );

		private final ThreadGroup group;

		private final String prefix;

		public TaskThreadFactory() {
			SecurityManager securityManager = System.getSecurityManager();
			group = ( securityManager != null ) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
			prefix = "TaskPool-" + poolNumber.getAndIncrement() + "-Thread-";
		}

		public Thread newThread( Runnable r ) {
			Thread thread = new TaskThread( group, r, prefix + threadNumber.getAndIncrement(), 0 );
			if( thread.getPriority() != Thread.NORM_PRIORITY ) thread.setPriority( Thread.NORM_PRIORITY );
			if( !thread.isDaemon() ) thread.setDaemon( true );
			return thread;
		}

	}

}
