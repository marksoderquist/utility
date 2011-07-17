package com.parallelsymmetry.escape.utility.task;

final class MockTask extends Task<Object> {

	static final String EXCEPTION_MESSAGE = "Intentionally fail task.";

	private boolean fail;

	private Task<?> nest;

	private TaskManager manager;

	private Object object;

	public MockTask( TaskManager manager ) {
		this( manager, null );
	}

	public MockTask( TaskManager manager, Object object ) {
		this.manager = manager;
		this.object = object;
	}

	public MockTask( TaskManager manager, Object object, boolean fail ) {
		this.manager = manager;
		this.object = object;
		this.fail = fail;
	}

	public MockTask( TaskManager manager, Object object, Task<?> nest ) {
		this.manager = manager;
		this.object = object;
		this.nest = nest;
	}

	@Override
	public Object execute() throws Exception {
		if( fail ) throw new Exception( EXCEPTION_MESSAGE );
		if( nest != null ) manager.invoke( nest );
		return object;
	}

}
