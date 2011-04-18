package com.parallelsymmetry.escape.utility.task;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

		MockTask task = new MockTask( manager, null, MockTask.Mode.FAIL );
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

	public void testNestedTasks() throws Exception {
		// FIXME If there is only one thread then this fails.
		manager.setThreadCount( 2 );
		manager.startAndWait();
		assertTrue( manager.isRunning() );

		Object result = new Object();
		MockTask task = new MockTask( manager, result, MockTask.Mode.NEST );
		manager.submit( task );
		assertEquals( result, task.get( 100, TimeUnit.MILLISECONDS ) );
		assertTrue( task.isDone() );
		assertFalse( task.isRunning() );
		assertTrue( task.isSuccess() );
		assertFalse( task.isCancelled() );
	}

	private static final class MockTask extends Task<Object> {

		public enum Mode {
			NONE, FAIL, NEST
		};

		private TaskManager manager;

		private Object object;

		private Mode mode;

		public MockTask( TaskManager manager ) {
			this( manager, null, Mode.NONE );
		}

		public MockTask( TaskManager manager, Object object ) {
			this( manager, object, Mode.NONE );
		}

		public MockTask( TaskManager manager, Object object, Mode mode ) {
			this.manager = manager;
			this.object = object;
			this.mode = mode;
		}

		@Override
		public Object execute() throws Exception {
			switch( mode ) {
				case FAIL: {
					throw new Exception( "Intentionally fail task." );
				}
				case NEST: {
					manager.invoke( new MockTask( manager ) );
					break;
				}
			}
			return object;
		}

	}

}
