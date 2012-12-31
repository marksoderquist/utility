package com.parallelsymmetry.utility.task;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.parallelsymmetry.utility.Controllable;
import com.parallelsymmetry.utility.log.Log;
import com.parallelsymmetry.utility.setting.Persistent;
import com.parallelsymmetry.utility.setting.Settings;

public class TaskManager implements Persistent, Controllable {

	private static final int MIN_THREAD_COUNT = 4;

	private static final int MAX_THREAD_COUNT = 32;

	private static final int PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();

	private static final int DEFAULT_MIN_THREAD_COUNT = Math.max( 4, PROCESSOR_COUNT );

	private static final int DEFAULT_MAX_THREAD_COUNT = Math.max( DEFAULT_MIN_THREAD_COUNT, PROCESSOR_COUNT * 2 );

	private ThreadPoolExecutor executor;

	private int maxThreadCount = DEFAULT_MAX_THREAD_COUNT;

	private int minThreadCount = DEFAULT_MIN_THREAD_COUNT;

	private Settings settings;

	private BlockingQueue<Runnable> queue;

	private Set<TaskListener> listeners;

	private List<Task<?>> tasks;

	public TaskManager() {
		tasks = new CopyOnWriteArrayList<Task<?>>();
		queue = new LinkedBlockingQueue<Runnable>();
		listeners = new CopyOnWriteArraySet<TaskListener>();
	}

	public List<Task<?>> getTasks() {
		return new ArrayList<Task<?>>( this.tasks );
	}

	public int getThreadCount() {
		return executor.getPoolSize();
	}

	public void setThreadCount( int count ) {
		if( count > MAX_THREAD_COUNT ) count = MAX_THREAD_COUNT;
		minThreadCount = Math.max( MIN_THREAD_COUNT, count / 2 );
		maxThreadCount = Math.min( MAX_THREAD_COUNT, Math.max( minThreadCount, count ) );

		saveSettings( settings );
		if( isRunning() ) try {
			restart();
		} catch( InterruptedException exception ) {
			Log.write( exception );
		}
	}

	@Override
	public synchronized void start() {
		if( isRunning() ) return;
		Log.write( Log.TRACE, "Task manager thread counts: " + minThreadCount + " min " + maxThreadCount + " max" );
		executor = new ThreadPoolExecutor( minThreadCount, maxThreadCount, 1, TimeUnit.SECONDS, queue, new TaskThreadFactory() );
	}

	@Override
	public synchronized void startAndWait() throws InterruptedException {
		start();
	}

	@Override
	public synchronized void startAndWait( long timeout, TimeUnit unit ) throws InterruptedException {
		start();
	}

	@Override
	public void restart() throws InterruptedException {
		stopAndWait();
		startAndWait();
	}

	@Override
	public void restart( long timeout, TimeUnit unit ) throws InterruptedException {
		// Don't use start() and stop() because they are asynchronous.
		stopAndWait( timeout / 2, unit );
		startAndWait( timeout / 2, unit );
	}

	@Override
	public synchronized void stop() {
		if( executor == null || executor.isShutdown() ) return;
		executor.shutdown();
		executor = null;
	}

	@Override
	public synchronized void stopAndWait() throws InterruptedException {
		stop();
		if( executor != null ) executor.awaitTermination( Long.MAX_VALUE, TimeUnit.DAYS );
	}

	@Override
	public synchronized void stopAndWait( long timeout, TimeUnit unit ) throws InterruptedException {
		stop();
		if( executor != null ) executor.awaitTermination( timeout, unit );
	}

	@Override
	public boolean isRunning() {
		return executor != null && !executor.isTerminated();
	}

	/**
	 * Asynchronously submit a task.
	 * 
	 * @param <T>
	 * @param task
	 * @return
	 */
	public <T> Future<T> submit( Task<T> task ) {
		checkRunning();
		submitted( task );
		return executor.submit( task );
	}

	/**
	 * Asynchronously submit a collection of tasks.
	 * 
	 * @param <T>
	 * @param tasks
	 * @return
	 */
	public <T> List<Future<T>> submitAll( Collection<? extends Task<T>> tasks ) {
		checkRunning();

		for( Task<T> task : tasks ) {
			submitted( task );
		}

		List<Future<T>> futures = new ArrayList<Future<T>>();
		for( Task<T> task : tasks ) {
			futures.add( executor.submit( task ) );
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
		if( EventQueue.isDispatchThread() ) throw new RuntimeException( "The event dispatch thread should not be blocked." );

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
		if( EventQueue.isDispatchThread() ) throw new RuntimeException( "The event dispatch thread should not be blocked." );

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
		if( EventQueue.isDispatchThread() ) throw new RuntimeException( "The event dispatch thread should not be blocked." );

		checkRunning();
		if( Thread.currentThread() instanceof TaskThread ) {
			synchronousExecute( tasks );
		} else {
			for( Task<T> task : tasks ) {
				submitted( task );
			}
			executor.invokeAll( tasks );
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
		if( EventQueue.isDispatchThread() ) throw new RuntimeException( "The event dispatch thread should not be blocked." );

		checkRunning();
		if( Thread.currentThread() instanceof TaskThread ) {
			synchronousExecute( tasks, timeout, unit );
		} else {
			for( Task<T> task : tasks ) {
				submitted( task );
			}
			executor.invokeAll( tasks, timeout, unit );
		}
		return new ArrayList<Future<T>>( tasks );
	}

	public void addTaskListener( TaskListener listener ) {
		listeners.add( listener );
	}

	public void removeTaskListener( TaskListener listener ) {
		listeners.remove( listener );
	}

	@Override
	public void loadSettings( Settings settings ) {
		this.settings = settings;

		this.maxThreadCount = settings.getInt( "thread-count", maxThreadCount );
	}

	@Override
	public void saveSettings( Settings settings ) {
		if( settings == null ) return;

		settings.putInt( "thread-count", maxThreadCount );
	}

	protected void fireTaskEvent( TaskEvent event ) {
		for( TaskListener listener : listeners ) {
			listener.handleEvent( event );
		}
	}

	protected BlockingQueue<Runnable> getQueue() {
		return executor.getQueue();
	}

	void submitted( Task<?> task ) {
		if( task == null ) throw new NullPointerException();
		tasks.add( task );
		task.setTaskManager( this );
		fireTaskEvent( new TaskEvent( this, task, TaskEvent.Type.TASK_SUBMITTED ) );
	}

	void completed( Task<?> task ) {
		if( task == null ) throw new NullPointerException();
		fireTaskEvent( new TaskEvent( this, task, TaskEvent.Type.TASK_COMPLETED ) );
		task.setTaskManager( null );
		tasks.remove( task );
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

	private void checkRunning() {
		if( executor == null ) throw new RuntimeException( "TaskManager is not running." );
	}

	//	private static final class TaskExecutor extends ThreadPoolExecutor {
	//
	//		public TaskExecutor( int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory ) {
	//			super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new ThreadPoolExecutor.CallerRunsPolicy() );
	//		}
	//
	//	}

	private static final class TaskThreadFactory implements ThreadFactory {

		private static final AtomicInteger poolNumber = new AtomicInteger( 1 );

		private final AtomicInteger threadNumber = new AtomicInteger( 1 );

		private final ThreadGroup group;

		private final String prefix;

		public TaskThreadFactory() {
			SecurityManager securityManager = System.getSecurityManager();
			group = ( securityManager != null ) ? securityManager.getThreadGroup() : Thread.currentThread().getThreadGroup();
			prefix = "TaskPool-" + poolNumber.getAndIncrement() + "-Thread-";
		}

		@Override
		public Thread newThread( Runnable r ) {
			Thread thread = new TaskThread( group, r, prefix + threadNumber.getAndIncrement(), 0 );
			if( thread.getPriority() != Thread.NORM_PRIORITY ) thread.setPriority( Thread.NORM_PRIORITY );
			if( !thread.isDaemon() ) thread.setDaemon( true );
			return thread;
		}

	}

	private static final class TaskThread extends Thread {

		public TaskThread( ThreadGroup group, Runnable target, String name, long stackSize ) {
			super( group, target, name, stackSize );
		}

	}

	//	private static final class TaskExecutor extends ThreadPoolExecutor {
	//
	//		public TaskExecutor( int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory ) {
	//			super( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, new ThreadPoolExecutor.CallerRunsPolicy() );
	//		}
	//
	//	}

}
