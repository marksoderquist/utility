package com.parallelsymmetry.escape.utility.task;

import java.util.EventObject;

public class TaskEvent extends EventObject {

	public enum Type {
		TASK_START, TASK_SUCCESS, TASK_FAILURE, TASK_FINISH;
	}

	private static final long serialVersionUID = 6199687149599225794L;

	private Task<?> task;

	private Type type;

	public TaskEvent( Object source, Task<?> task, Type type ) {
		super( source );
		this.task = task;
		this.type = type;
	}

	public Task<?> getTask() {
		return task;
	}

	public Type getType() {
		return type;
	}

}
