package com.parallelsymmetry.escape.utility.task;

import java.util.concurrent.Future;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.log.Log;

public class TaskManagerTest extends TestCase {

	private TaskManager manager;

	@Override
	public void setUp() {
		manager = new TaskManager();
	}

	public void testStartAndStop() throws Exception {
		assertFalse( manager.isRunning() );

		manager.startAndWait();
		assertTrue( manager.isRunning() );

		manager.stopAndWait();
		assertFalse( manager.isRunning() );
	}

	public void testRestart() throws Exception {
		assertFalse( manager.isRunning() );

		manager.startAndWait();
		assertTrue( manager.isRunning() );

		manager.stopAndWait();
		assertFalse( manager.isRunning() );

		manager.startAndWait();
		assertTrue( manager.isRunning() );

		manager.stopAndWait();
		assertFalse( manager.isRunning() );
	}

	public void testStopBeforeStart() throws Exception {
		assertFalse( manager.isRunning() );

		manager.stopAndWait();
		assertFalse( manager.isRunning() );
	}

	public void testSubmitNullResult() throws Exception {
		Log.setLevel( Log.DEBUG );
		assertFalse( manager.isRunning() );

		manager.startAndWait();
		assertTrue( manager.isRunning() );

		MockTask task = new MockTask();
		Future<Object> future = manager.submit( task );
		assertNull( future.get() );
		assertTrue( task.isDone() );
		assertFalse( task.isRunning() );
		assertTrue( task.isSuccess() );
		assertFalse( task.isCancelled() );
	}

	public void testSubmitWithResult() throws Exception {
		assertFalse( manager.isRunning() );

		manager.startAndWait();
		assertTrue( manager.isRunning() );

		Object result = new Object();
		MockTask task = new MockTask( result );
		Future<Object> future = manager.submit( task );
		assertEquals( result, future.get() );
		assertTrue( task.isDone() );
		assertFalse( task.isRunning() );
		assertTrue( task.isSuccess() );
		assertFalse( task.isCancelled() );
	}

	public void testFailedTask() throws Exception {
		assertFalse( manager.isRunning() );

		manager.startAndWait();
		assertTrue( manager.isRunning() );

		MockTask task = new MockTask( null, true );
		Future<Object> future = manager.submit( task );
		try {
			assertNull( future.get() );
			fail();
		} catch( Exception exception ) {
			assertNotNull( exception );
		}
		assertTrue( task.isDone() );
		assertFalse( task.isRunning() );
		assertFalse( task.isSuccess() );
		assertFalse( task.isCancelled() );
	}

	public void testSubmitBeforeStart() throws Exception {
		assertFalse( manager.isRunning() );

		MockTask task = new MockTask();

		try {
			manager.submit( task );
			fail( "TaskManager.submit() should throw and exception if the manager is not running." );
		} catch( Exception exception ) {
			// Intentionally ignore exception.
		}

		assertFalse( manager.isRunning() );
		assertFalse( task.isDone() );
		assertFalse( task.isRunning() );
		assertFalse( task.isSuccess() );
		assertFalse( task.isCancelled() );
	}

	public void testUsingTaskAsFuture() throws Exception {
		assertFalse( manager.isRunning() );

		manager.startAndWait();
		assertTrue( manager.isRunning() );

		Object result = new Object();
		MockTask task = new MockTask( result );
		manager.submit( task );
		assertEquals( result, task.get() );
		assertTrue( task.isDone() );
		assertFalse( task.isRunning() );

		// Intermittently fails.
		assertTrue( task.isSuccess() );
		assertFalse( task.isCancelled() );
	}

	public void testNestedTasks() throws Exception {
		manager.startAndWait();
		assertTrue( manager.isRunning() );

	}

	private static final class MockTask extends Task<Object> {

		private Object object;

		private boolean fail;

		public MockTask() {
			this( null, false );
		}

		public MockTask( Object object ) {
			this( object, false );
		}

		public MockTask( Object object, boolean fail ) {
			this.object = object;
			this.fail = fail;
		}

		@Override
		public Object execute() throws Exception {
			if( fail ) throw new Exception( "Intentionally fail task." );
			return object;
		}

	}

}
