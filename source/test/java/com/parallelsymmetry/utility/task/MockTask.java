package com.parallelsymmetry.utility.task;

import com.parallelsymmetry.utility.ThreadUtil;
import com.parallelsymmetry.utility.task.Task;
import com.parallelsymmetry.utility.task.TaskManager;

final class MockTask extends Task<Object> {

	static final String EXCEPTION_MESSAGE = "Intentionally fail task.";

	private int delay;

	private boolean fail;

	private Task<?> nest;

	private final TaskManager manager;

	private final Object object;

	public MockTask( TaskManager manager ) {
		this( manager, null );
	}

	public MockTask( TaskManager manager, int delay ) {
		this( manager, null, delay );
	}

	public MockTask( TaskManager manager, int delay, boolean fail ) {
		this( manager, null, delay, fail );
	}

	public MockTask( TaskManager manager, Object object ) {
		this( manager, object, 0 );
	}

	public MockTask( TaskManager manager, Object object, int delay ) {
		this( manager, object, delay, false );
	}

	public MockTask( TaskManager manager, Object object, boolean fail ) {
		this( manager, object, 0, fail );
	}

	public MockTask( TaskManager manager, Object object, int delay, boolean fail ) {
		this.manager = manager;
		this.object = object;
		this.delay = delay;
		this.fail = fail;
	}

	public MockTask( TaskManager manager, Object object, Task<?> nest ) {
		this.manager = manager;
		this.object = object;
		this.nest = nest;
	}

	@Override
	public Object execute() throws Exception {
		if( delay > 0 ) ThreadUtil.pause( delay );
		if( fail ) throw new Exception( EXCEPTION_MESSAGE );
		if( nest != null ) manager.invoke( nest );
		return object;
	}

}
