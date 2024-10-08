package com.parallelsymmetry.utility.event;

import com.parallelsymmetry.utility.Accessor;
import com.parallelsymmetry.utility.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class EventBusTest extends BaseTestCase {

	private static final String TEST_QUEUE_NAME = "test";

	@Test
	public void testStartAndStop() throws Exception {
		EventBus bus = new EventBus();
		assertFalse( bus.isRunning() );
		bus.startAndWait();
		assertTrue( bus.isRunning() );
		bus.stopAndWait();
		assertFalse( bus.isRunning() );
	}

	@Test
	public void testAddListener() throws Exception {
		EventBus bus = new EventBus();
		EventListener<Event> listener = new MockEventListener();
		Map<String, Map<Class<? extends Event>, Set<EventListener<Event>>>> listenersByQueue = Accessor.getField( bus, "listenersByQueue" );
		assertEquals( 0, listenersByQueue.size() );

		bus.addEventListener( listener );
		assertEquals( 1, listenersByQueue.size() );

		Map<Class<? extends Event>, Set<EventListener<Event>>> listenersByClass = listenersByQueue.get( EventBus.DEFAULT_QUEUE_NAME );
		Set<EventListener<Event>> classListeners = listenersByClass.get( Event.class );
		assertEquals( 1, classListeners.size() );
		assertEquals( listener, classListeners.iterator().next() );
	}

	@Test
	public void testAddListenerByQueue() throws Exception {
		EventBus bus = new EventBus();
		EventListener<Event> listener = new MockEventListener();
		Map<String, Map<Class<? extends Event>, Set<EventListener<Event>>>> listenersByQueue = Accessor.getField( bus, "listenersByQueue" );
		assertEquals( 0, listenersByQueue.size() );

		bus.addEventListener( listener, TEST_QUEUE_NAME );
		assertEquals( 1, listenersByQueue.size() );

		Map<Class<? extends Event>, Set<EventListener<Event>>> listenersByClass = listenersByQueue.get( TEST_QUEUE_NAME );
		Set<EventListener<Event>> classListeners = listenersByClass.get( Event.class );
		assertEquals( 1, classListeners.size() );
		assertEquals( listener, classListeners.iterator().next() );
	}

	@Test
	public void testAddListenerByClass() throws Exception {
		EventBus bus = new EventBus();
		EventListener<Event> listener = new MockEventListener();
		Map<String, Map<Class<? extends Event>, Set<EventListener<Event>>>> listenersByQueue = Accessor.getField( bus, "listenersByQueue" );
		assertEquals( 0, listenersByQueue.size() );

		bus.addEventListener( listener, TestEvent.class );
		assertEquals( 1, listenersByQueue.size() );

		Map<Class<? extends Event>, Set<EventListener<Event>>> listenersByClass = listenersByQueue.get( EventBus.DEFAULT_QUEUE_NAME );
		Set<EventListener<Event>> classListeners = listenersByClass.get( TestEvent.class );
		assertEquals( 1, classListeners.size() );
		assertEquals( listener, classListeners.iterator().next() );
	}

	@Test
	public void testRemoveListener() throws Exception {
		EventBus bus = new EventBus();
		EventListener<Event> listener = new MockEventListener();
		Map<String, Map<Class<? extends Event>, Set<EventListener<Event>>>> listenersByQueue = Accessor.getField( bus, "listenersByQueue" );
		assertEquals( 0, listenersByQueue.size() );

		bus.addEventListener( listener );
		assertEquals( 1, listenersByQueue.size() );

		Map<Class<? extends Event>, Set<EventListener<Event>>> listenersByClass = listenersByQueue.get( EventBus.DEFAULT_QUEUE_NAME );
		Set<EventListener<Event>> classListeners = listenersByClass.get( Event.class );
		assertEquals( 1, classListeners.size() );
		assertEquals( listener, classListeners.iterator().next() );

		bus.removeEventListener( listener );
		assertEquals( 0, classListeners.size() );
	}

	@Test
	public void testRemoveListenerByQueue() throws Exception {
		EventBus bus = new EventBus();
		EventListener<Event> listener = new MockEventListener();
		Map<String, Map<Class<? extends Event>, Set<EventListener<Event>>>> listenersByQueue = Accessor.getField( bus, "listenersByQueue" );
		assertEquals( 0, listenersByQueue.size() );

		bus.addEventListener( listener, TEST_QUEUE_NAME );
		assertEquals( 1, listenersByQueue.size() );

		Map<Class<? extends Event>, Set<EventListener<Event>>> listenersByClass = listenersByQueue.get( TEST_QUEUE_NAME );
		Set<EventListener<Event>> classListeners = listenersByClass.get( Event.class );
		assertEquals( 1, classListeners.size() );
		assertEquals( listener, classListeners.iterator().next() );

		bus.removeEventListener( listener, TEST_QUEUE_NAME );
		assertEquals( 0, classListeners.size() );
	}

	@Test
	public void testRemoveListenerByClass() throws Exception {
		EventBus bus = new EventBus();
		EventListener<Event> listener = new MockEventListener();
		Map<String, Map<Class<? extends Event>, Set<EventListener<Event>>>> listenersByQueue = Accessor.getField( bus, "listenersByQueue" );
		assertEquals( 0, listenersByQueue.size() );

		bus.addEventListener( listener, TestEvent.class );
		assertEquals( 1, listenersByQueue.size() );

		Map<Class<? extends Event>, Set<EventListener<Event>>> listenersByClass = listenersByQueue.get( EventBus.DEFAULT_QUEUE_NAME );
		Set<EventListener<Event>> classListeners = listenersByClass.get( TestEvent.class );
		assertEquals( 1, classListeners.size() );
		assertEquals( listener, classListeners.iterator().next() );

		bus.removeEventListener( listener, TestEvent.class );
		assertEquals( 0, classListeners.size() );
	}

	@Test
	public void testSubmit() throws Exception {
		EventBus bus = new EventBus();
		Event event = new TestEvent( getClass() );
		MockEventListener listener = new MockEventListener();
		MockEventListener defaultQueueListener = new MockEventListener();
		MockEventListener defaultClassListener = new MockEventListener();

		bus.startAndWait();
		bus.addEventListener( listener );
		bus.addEventListener( defaultQueueListener, EventBus.DEFAULT_QUEUE_NAME );
		bus.addEventListener( defaultClassListener, Event.class );
		bus.submit( event );

		event.waitFor();

		assertEquals( 1, listener.getEvents().size() );
		assertEquals( 1, defaultQueueListener.getEvents().size() );
		assertEquals( 1, defaultQueueListener.getEvents().size() );

		assertEquals( event, listener.getEvents().get( 0 ) );
		assertEquals( event, defaultQueueListener.getEvents().get( 0 ) );
		assertEquals( event, defaultClassListener.getEvents().get( 0 ) );
		bus.stopAndWait();
	}

	@Test
	public void testSubmitCancelledEvent() throws Exception {
		EventBus bus = new EventBus();
		Event cancelledEvent = new TestEvent( "cancelled" );
		Event event = new TestEvent( getClass() );
		MockEventListener listener = new MockEventListener();

		cancelledEvent.cancel();

		bus.startAndWait();
		bus.addEventListener( listener );

		bus.submit( cancelledEvent );
		cancelledEvent.waitFor();
		assertEquals( 0, listener.getEvents().size() );

		bus.submit( event );
		event.waitFor();
		assertEquals( event, listener.getEvents().get( 0 ) );

		bus.stopAndWait();
	}

	@Test
	public void testSubmitToQueue() throws Exception {
		EventBus bus = new EventBus();
		Event event = new TestEvent( getClass() );
		MockEventListener listener = new MockEventListener();
		MockEventListener defaultQueueListener = new MockEventListener();
		MockEventListener defaultClassListener = new MockEventListener();

		bus.startAndWait();
		bus.addEventListener( listener, TEST_QUEUE_NAME );
		bus.addEventListener( defaultQueueListener, EventBus.DEFAULT_QUEUE_NAME );
		bus.addEventListener( defaultClassListener, TEST_QUEUE_NAME, Event.class );
		bus.submit( event, TEST_QUEUE_NAME );
		event.waitFor();
		assertEquals( event, listener.getEvents().get( 0 ) );
		assertEquals( 0, defaultQueueListener.getEvents().size() );
		assertEquals( event, defaultClassListener.getEvents().get( 0 ) );
		bus.stopAndWait();
	}

	@Test
	public void testSubmitToClass() throws Exception {
		EventBus bus = new EventBus();
		Event event = new TestEvent( getClass() );
		MockEventListener listener = new MockEventListener();
		MockEventListener defaultQueueListener = new MockEventListener();
		MockEventListener defaultClassListener = new MockEventListener();

		bus.startAndWait();
		bus.addEventListener( listener );
		bus.addEventListener( defaultQueueListener, TEST_QUEUE_NAME );
		bus.addEventListener( defaultClassListener, TestEvent.class );
		bus.submit( event );
		event.waitFor();
		assertEquals( event, listener.getEvents().get( 0 ) );
		assertEquals( 0, defaultQueueListener.getEvents().size() );
		assertEquals( event, defaultClassListener.getEvents().get( 0 ) );
		bus.stopAndWait();
	}

	@Test
	public void testSubmitMultipleEvents() throws Exception {
		EventBus bus = new EventBus();
		MockEventListener listener = new MockEventListener();
		MockEventListener defaultQueueListener = new MockEventListener();
		MockEventListener defaultClassListener = new MockEventListener();

		bus.startAndWait();
		bus.addEventListener( listener );
		bus.addEventListener( defaultQueueListener, EventBus.DEFAULT_QUEUE_NAME );
		bus.addEventListener( defaultClassListener, Event.class );
		for( int index = 0; index < 20; index++ ) {
			Event event = new TestEvent( getClass() );
			bus.submit( event );
			event.waitFor();
			assertEquals( event, listener.getEvents().get( index ) );
			assertEquals( event, defaultQueueListener.getEvents().get( index ) );
			assertEquals( event, defaultClassListener.getEvents().get( index ) );
		}
		bus.stopAndWait();
	}

	private static class TestEvent extends Event {

		public TestEvent( Object source ) {
			super( source );
		}

	}

	private static class MockEventListener implements EventListener<Event> {

		private final List<Event> events = new CopyOnWriteArrayList<Event>();

		@Override
		public synchronized void eventOccurred( Event event ) {
			events.add( event );
		}

		public synchronized List<Event> getEvents() {
			return events;
		}

	}

}
