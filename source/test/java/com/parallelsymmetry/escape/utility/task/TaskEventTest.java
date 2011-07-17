package com.parallelsymmetry.escape.utility.task;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TaskEventTest extends TestCase {

	private TaskManager manager;

	public void setUp() throws Exception {
		manager = new TaskManager();
		manager.startAndWait();
	}

	public void testSucess() throws Exception {
		Task<Object> task = new MockTask( manager );

		TaskEventWatcher watcher = new TaskEventWatcher();
		task.addTaskListener( watcher );

		manager.submit( task );
		task.get();

		assertTrue( task.isDone() );
		assertEquals( Task.State.COMPLETE, task.getState() );
		assertEquals( Task.Result.SUCCESS, task.getResult() );

		assertEvent( watcher.events.get( 0 ), task, TaskEvent.Type.TASK_START );
		assertEvent( watcher.events.get( 1 ), task, TaskEvent.Type.TASK_FINISH );
		assertEquals( 2, watcher.events.size() );
	}

	public void testFailure() throws Exception {
		Task<Object> task = new MockTask( manager, null, true );

		TaskEventWatcher watcher = new TaskEventWatcher();
		task.addTaskListener( watcher );

		manager.submit( task );
		try {
			task.get();
			fail( "Exception should be thrown." );
		} catch( Exception exception ) {
			assertNotNull( exception );
		}

		assertTrue( task.isDone() );
		assertEquals( Task.State.COMPLETE, task.getState() );
		assertEquals( Task.Result.FAILED, task.getResult() );

		assertEvent( watcher.events.get( 0 ), task, TaskEvent.Type.TASK_START );
		assertEvent( watcher.events.get( 1 ), task, TaskEvent.Type.TASK_FINISH );
		assertEquals( 2, watcher.events.size() );
	}

	private void assertEvent( TaskEvent event, Task<?> task, TaskEvent.Type type ) {
		assertEquals( task, event.getTask() );
		assertEquals( type, event.getType() );
	}

	private static class TaskEventWatcher implements TaskListener {

		List<TaskEvent> events = new ArrayList<TaskEvent>();

		@Override
		public void handleEvent( TaskEvent event ) {
			events.add( event );
		}

	}

}
