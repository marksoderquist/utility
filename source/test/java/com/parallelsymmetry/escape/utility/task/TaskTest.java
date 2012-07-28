package com.parallelsymmetry.escape.utility.task;

import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.ThreadUtil;

public class TaskTest extends TestCase {

	private TaskManager manager = new TaskManager();

	/*
	 * Don't make this number too small. The smaller the number, the more likely
	 * the computer can't complete the task quickly enough to pass the test. A
	 * good time is between 10-50 milliseconds.
	 */
	private int delay = 50;

	@Override
	public void setUp() throws Exception {
		manager.startAndWait();
	}

	public void testSuccess() throws Exception {
		Task<?> task = new MockTask( manager, 4 * delay );
		ThreadUtil.pause( delay );
		assertEquals( Task.State.WAITING, task.getState() );
		assertEquals( Task.Result.UNKNOWN, task.getResult() );

		manager.submit( task );
		task.waitForState( Task.State.RUNNING, 100, TimeUnit.MILLISECONDS );
		assertEquals( Task.State.RUNNING, task.getState() );
		assertEquals( Task.Result.UNKNOWN, task.getResult() );
		ThreadUtil.pause( delay );
		assertEquals( Task.State.RUNNING, task.getState() );
		assertEquals( Task.Result.UNKNOWN, task.getResult() );
		ThreadUtil.pause( 2 * delay );
		assertEquals( Task.State.RUNNING, task.getState() );
		assertEquals( Task.Result.UNKNOWN, task.getResult() );
		ThreadUtil.pause( 2 * delay );
		assertEquals( Task.State.DONE, task.getState() );
		assertEquals( Task.Result.SUCCESS, task.getResult() );
	}

	public void testFailure() throws Exception {
		Task<?> task = new MockTask( manager, 4 * delay, true );
		ThreadUtil.pause( delay );
		assertEquals( Task.State.WAITING, task.getState() );
		assertEquals( Task.Result.UNKNOWN, task.getResult() );

		manager.submit( task );
		task.waitForState( Task.State.RUNNING );
		assertEquals( Task.State.RUNNING, task.getState() );
		assertEquals( Task.Result.UNKNOWN, task.getResult() );
		ThreadUtil.pause( delay );
		assertEquals( Task.State.RUNNING, task.getState() );
		assertEquals( Task.Result.UNKNOWN, task.getResult() );
		ThreadUtil.pause( 2 * delay );
		assertEquals( Task.State.RUNNING, task.getState() );
		assertEquals( Task.Result.UNKNOWN, task.getResult() );
		ThreadUtil.pause( 2 * delay );
		assertEquals( Task.State.DONE, task.getState() );
		assertEquals( Task.Result.FAILED, task.getResult() );
	}

}
