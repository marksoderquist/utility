package com.parallelsymmetry.utility.task;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import com.parallelsymmetry.utility.task.TaskEvent.Type;

public class CompositeTask extends Task<Object> implements TaskListener {

	private Set<Task<?>> tasks;

	private long total;

	private long progress;

	private Map<Task<?>, Long> progresses;

	private HashSet<Future<?>> futures;

	public CompositeTask() {
		tasks = new HashSet<Task<?>>();
		progresses = new HashMap<Task<?>, Long>();
	}

	public CompositeTask( Collection<? extends Task<?>> tasks ) {
		this();
		for( Task<?> task : tasks ) {
			addTask( task );
		}
	}

	public void addTask( Task<?> task ) {
		tasks.add( task );
		total += task.getMaximum() - task.getMinimum();
		progresses.put( task, new Long( task.getProgress() ) );
		updateProgress();
	}

	public void removeTask( Task<?> task ) {
		tasks.remove( task );
		progresses.remove( task );
		total -= task.getMaximum() - task.getMinimum();
		updateProgress();
	}

	public Set<Future<?>> getFutures() {
		return new HashSet<Future<?>>( futures );
	}

	@Override
	public Object execute() throws Exception {
		TaskManager manager = getTaskManager();

		futures = new HashSet<Future<?>>( tasks.size() );

		// Submit all the tasks for execution.
		for( Task<?> task : tasks ) {
			futures.add( manager.submit( task ) );
		}

		// Wait for all the tasks to terminate.
		for( Future<?> future : futures ) {
			try {
				future.get();
			} catch( Exception exception ) {
				// Intentionally ignore exception.
			}
		}

		return null;
	}

	@Override
	public long getMinimum() {
		return 0;
	}

	@Override
	public long getMaximum() {
		return total;
	}

	@Override
	public long getProgress() {
		return progress;
	}

	@Override
	public void handleEvent( TaskEvent event ) {
		if( event.getType() == Type.TASK_PROGRESS ) {
			progresses.put( event.getTask(), event.getTask().getProgress() );
			updateProgress();
		}
	}

	private void updateProgress() {
		long count = 0;
		for( Long progress : progresses.values() ) {
			count += progress;
		}
		progress = count;
	}

}
