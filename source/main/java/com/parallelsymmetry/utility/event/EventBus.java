package com.parallelsymmetry.utility.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import com.parallelsymmetry.utility.agent.Worker;
import com.parallelsymmetry.utility.log.Log;

/**
 * <p>
 * The EventBus class allows a program to implement a publish/subscribe model to
 * allow for loosely coupled communication.
 * <p>
 * Event listeners are added by registering to listen for certain types of
 * events or their subclasses. It is even possible to listen to all events by
 * adding a listener for the Event class, which is the parent of all program
 * events.
 * <p>
 * The EventBus class uses queues to determine event delivery order. By default
 * events are processed in the order they received by the default queue. A
 * different queue may be specified to increase efficiency since each queue may
 * be processed on a separate thread. Events in different queues have no
 * guaranteed delivery order.
 * <p>
 * The delivery order of an event to the registered listeners is not guaranteed.
 * 
 * @author Mark Soderquist
 */
public final class EventBus extends Worker {

	public static final String DEFAULT_QUEUE_NAME = "default";

	private volatile int eventCount;

	private Map<String, Queue<Event>> queues;

	private Map<String, Map<Class<? extends Event>, Set<EventListener<?>>>> listenersByQueue;

	private final Object eventlock = new Object();

	public EventBus() {
		listenersByQueue = new ConcurrentHashMap<String, Map<Class<? extends Event>, Set<EventListener<?>>>>();
		queues = new ConcurrentHashMap<String, Queue<Event>>();
	}

	public boolean isEventBusThread() {
		return isWorkerThread();
	}

	public void eventWait() {
		eventWait( DEFAULT_QUEUE_NAME );
	}

	public void eventWait( String queueName ) {
		if( isWorkerThread() ) return;
		Event event = new Event( this );
		submit( event, queueName );
		event.waitFor();
	}

	public void submit( Event event ) {
		submit( event, DEFAULT_QUEUE_NAME );
	}

	public void submit( Event event, String queueName ) {
		event.setEventBus( this );
		event.setCallStackThrowable( new Throwable() );

		synchronized( eventlock ) {
			Queue<Event> queue = queues.get( queueName );
			if( queue == null ) {
				queue = new PriorityQueue<Event>();
				queues.put( queueName, queue );
			}

			queue.add( event );
			eventCount++;
			eventlock.notifyAll();
		}
	}

	public void addEventListener( EventListener<?> listener ) {
		addEventListener( listener, null, null );
	}

	public void addEventListener( EventListener<?> listener, String queueName ) {
		addEventListener( listener, queueName, null );
	}

	public void addEventListener( EventListener<?> listener, Class<? extends Event> clazz ) {
		addEventListener( listener, null, clazz );
	}

	public void addEventListener( EventListener<?> listener, String queueName, Class<? extends Event> clazz ) {
		if( queueName == null ) queueName = DEFAULT_QUEUE_NAME;
		if( clazz == null ) clazz = Event.class;

		Map<Class<? extends Event>, Set<EventListener<?>>> queueListeners = listenersByQueue.get( queueName );
		if( queueListeners == null ) {
			queueListeners = new ConcurrentHashMap<Class<? extends Event>, Set<EventListener<?>>>();
			listenersByQueue.put( queueName, queueListeners );
		}

		Set<EventListener<?>> classListeners = queueListeners.get( clazz );
		if( classListeners == null ) {
			classListeners = new CopyOnWriteArraySet<EventListener<?>>();
			queueListeners.put( clazz, classListeners );
		}
		classListeners.add( listener );
	}

	public void removeEventListener( EventListener<?> listener ) {
		removeEventListener( listener, null, null );
	}

	public void removeEventListener( EventListener<?> listener, String queueName ) {
		removeEventListener( listener, queueName, null );
	}

	public void removeEventListener( EventListener<?> listener, Class<? extends Event> clazz ) {
		removeEventListener( listener, null, clazz );
	}

	public void removeEventListener( EventListener<?> listener, String queueName, Class<? extends Event> clazz ) {
		if( queueName == null ) queueName = DEFAULT_QUEUE_NAME;
		if( clazz == null ) clazz = Event.class;

		Map<Class<? extends Event>, Set<EventListener<?>>> queueListeners = listenersByQueue.get( queueName );
		if( queueListeners == null ) return;

		Set<EventListener<?>> classListeners = queueListeners.get( clazz );
		if( classListeners == null ) return;

		classListeners.remove( listener );
	}

	@Override
	public void startWorker() {

	}

	@Override
	public void stopWorker() {
		synchronized( eventlock ) {
			eventlock.notifyAll();
		}
	}

	@Override
	public void run() {
		while( isExecutable() ) {

			// Wait for events to be submitted.
			synchronized( eventlock ) {
				while( isExecutable() && eventCount == 0 ) {
					try {
						eventlock.wait();
					} catch( InterruptedException exception ) {
						return;
					}
				}
			}

			// Process the events outside the lock.
			processEvents( selectEvents() );
		}

	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	private void processEvents( Map<String, Event> events ) {
		// Go through each queue and process an event if one is waiting.
		for( String queue : events.keySet() ) {
			Event event = events.get( queue );
			if( event == null ) continue;

			// An event was found for the queue, send it to the listeners.
			Log.write( Log.DEBUG, "Queue: " + queue + " Event: " + event );
			for( EventListener listener : selectListeners( event, queue ) ) {
				try {
					listener.eventOccurred( event );
				} catch( Throwable throwable ) {
					event.getCallStackThrowable().initCause( throwable );
					Log.write( event.getCallStackThrowable() );
				}
			}

			event.processed();
		}
	}

	private Map<String, Event> selectEvents() {
		Map<String, Event> events = new HashMap<String, Event>();

		for( String queueName : queues.keySet() ) {
			synchronized( eventlock ) {
				Queue<Event> queue = queues.get( queueName );
				Event event = queue.poll();
				if( event != null ) eventCount--;
				while( event != null && event.isCancelled() ) {
					event = queue.poll();
					if( event != null ) eventCount--;
				}
				if( eventCount < 0 ) eventCount = 0;
				if( event != null ) events.put( queueName, event );
			}
		}

		return events;
	}

	private Set<EventListener<?>> selectListeners( Event event, String queueName ) {
		Set<EventListener<?>> listeners = new HashSet<EventListener<?>>();

		Map<Class<? extends Event>, Set<EventListener<?>>> queueListeners = listenersByQueue.get( queueName );
		if( queueListeners == null ) return listeners;

		Class<?> clazz = event.getClass();
		while( clazz != null ) {
			Set<EventListener<?>> classListeners = queueListeners.get( clazz );
			if( classListeners != null ) listeners.addAll( classListeners );
			clazz = clazz.getSuperclass();
		}

		return listeners;
	}

}
