package com.parallelsymmetry.utility.task;

import com.parallelsymmetry.utility.BaseTestCase;
import com.parallelsymmetry.utility.ThreadUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskTest extends BaseTestCase {

	private final TaskManager manager = new TaskManager();

	/*
	 * Don't make this number too small. The smaller the number, the more likely
	 * the computer can't complete the task quickly enough to pass the test. A
	 * good time is between 10-50 milliseconds.
	 */
	private final int delay = 50;

	@BeforeEach
	@Override
	public void setup() throws Exception {
		super.setup();
		manager.startAndWait();
	}

	@Test
	public void testPriority() {
		Task<?> task = new MockTask( manager );

		// Check default priority.
		assertEquals( Task.Priority.MEDIUM, task.getPriority() );

		// Check changing priority.
		task.setPriority( Task.Priority.LOW );
		assertEquals( Task.Priority.LOW, task.getPriority() );
	}

	@Test
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

	@Test
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
