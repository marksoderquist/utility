package com.parallelsymmetry.escape.utility.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class TaskManagerTest extends TestCase {

	private static final String EXCEPTION_MESSAGE = "Intentionally fail task.";

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
		assertFalse( manager.isRunning() );

		manager.startAndWait();
		assertTrue( manager.isRunning() );

		MockTask task = new MockTask( manager );
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
		MockTask task = new MockTask( manager, result );
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

		MockTask task = new MockTask( manager, null, true );
		manager.submit( task );
		try {
			assertNull( task.get() );
			fail();
		} catch( ExecutionException exception ) {
			assertEquals( EXCEPTION_MESSAGE, exception.getCause().getMessage() );
		}
		assertTrue( task.isDone() );
		assertFalse( task.isRunning() );
		assertFalse( task.isSuccess() );
		assertFalse( task.isCancelled() );
	}

	public void testSubmitBeforeStart() throws Exception {
		assertFalse( manager.isRunning() );

		MockTask task = new MockTask( manager );

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
		MockTask task = new MockTask( manager, result );
		manager.submit( task );
		assertEquals( result, task.get() );
		assertTrue( task.isDone() );
		assertFalse( task.isRunning() );
		assertTrue( task.isSuccess() );
		assertFalse( task.isCancelled() );
	}

	public void testNestedTask() throws Exception {
		manager.setThreadCount( 1 );
		manager.startAndWait();
		assertTrue( manager.isRunning() );

		Object nestedResult = new Object();
		MockTask nestedTask = new MockTask( manager, nestedResult );
		Object result = new Object();
		MockTask task = new MockTask( manager, result, nestedTask );
		manager.submit( task );
		assertEquals( result, task.get( 100, TimeUnit.MILLISECONDS ) );
		assertTrue( task.isDone() );
		assertFalse( task.isRunning() );
		assertTrue( task.isSuccess() );
		assertFalse( task.isCancelled() );
	}

	public void testNestedTaskWithException() throws Exception {
		manager.setThreadCount( 1 );
		manager.startAndWait();
		assertTrue( manager.isRunning() );

		Object nestedResult = new Object();
		MockTask nestedTask = new MockTask( manager, nestedResult, true );
		Object result = new Object();
		MockTask task = new MockTask( manager, result, nestedTask );

		manager.submit( task );

		// Check the parent task.
		task.get();
		assertTrue( task.isDone() );
		assertFalse( task.isRunning() );
		assertTrue( task.isSuccess() );
		assertFalse( task.isCancelled() );

		// Check the nested task.
		try {
			assertNull( nestedTask.get() );
			fail();
		} catch( ExecutionException exception ) {
			assertEquals( EXCEPTION_MESSAGE, exception.getCause().getMessage() );
		}
		assertTrue( nestedTask.isDone() );
		assertFalse( nestedTask.isRunning() );
		assertFalse( nestedTask.isSuccess() );
		assertFalse( nestedTask.isCancelled() );
	}

	private static final class MockTask extends Task<Object> {

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

}
