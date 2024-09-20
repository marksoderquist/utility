package com.parallelsymmetry.utility.task;

import java.util.ArrayList;
import java.util.List;

import com.parallelsymmetry.utility.task.Task;
import com.parallelsymmetry.utility.task.TaskEvent;
import com.parallelsymmetry.utility.task.TaskListener;
import com.parallelsymmetry.utility.task.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TaskEventTest {

	private TaskManager manager;

	@BeforeEach
	public void setup() throws Exception {
		manager = new TaskManager();
		manager.startAndWait();
	}

	@Test
	public void testSucess() throws Exception {
		Task<Object> task = new MockTask( manager );

		TaskEventWatcher watcher = new TaskEventWatcher();
		task.addTaskListener( watcher );

		manager.submit( task );
		task.get();

		assertTrue( task.isDone() );
		assertEquals( Task.State.DONE, task.getState() );
		assertEquals( Task.Result.SUCCESS, task.getResult() );

		/*
		 * Because there are two threads involved in this test, the test thread
		 * needs to wait for the events to arrive. Task is required to ensure the
		 * done state is set correctly before events are sent but this allows the
		 * test thread to continue before the events arrive.
		 */
		watcher.waitForEventCount( 3, 100 );
		assertEquals( 3, watcher.events.size() );
		assertEvent( watcher.events.get( 0 ), task, TaskEvent.Type.TASK_START );
		assertEvent( watcher.events.get( 1 ), task, TaskEvent.Type.TASK_PROGRESS );
		assertEvent( watcher.events.get( 2 ), task, TaskEvent.Type.TASK_FINISH );
	}

	@Test
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
		assertEquals( Task.State.DONE, task.getState() );
		assertEquals( Task.Result.FAILED, task.getResult() );

		/*
		 * Because there are two threads involved in this test, the test thread
		 * needs to wait for the events to arrive. Task is required to ensure the
		 * done state is set correctly before events are sent but this allows the
		 * test thread to continue before the events arrive.
		 */
		watcher.waitForEventCount( 3, 100 );
		assertEquals( 3, watcher.events.size() );
		assertEvent( watcher.events.get( 0 ), task, TaskEvent.Type.TASK_START );
		assertEvent( watcher.events.get( 1 ), task, TaskEvent.Type.TASK_PROGRESS );
		assertEvent( watcher.events.get( 2 ), task, TaskEvent.Type.TASK_FINISH );
	}

	private void assertEvent( TaskEvent event, Task<?> task, TaskEvent.Type type ) {
		assertEquals( task, event.getTask() );
		assertEquals( type, event.getType() );
	}

	private static class TaskEventWatcher implements TaskListener {

		List<TaskEvent> events = new ArrayList<TaskEvent>();

		@Override
		public synchronized void handleEvent( TaskEvent event ) {
			events.add( event );
			notifyAll();
		}

		public synchronized void waitForEventCount( int count, int timout ) throws InterruptedException {
			while( events.size() < count ) {
				wait( timout );
			}
		}

	}

}
