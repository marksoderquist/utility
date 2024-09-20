package com.parallelsymmetry.utility;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class PersistentQueueTest extends BaseTestCase {

	@Test
	public void testConstructorWithBadFile() {
		try {
			new PersistentQueue<String>( new File( "/path/should/not/exist/test.queue" ) );
			fail( "Queue constructor with a bad file string should throw an exception." );
		} catch( IOException exception ) {
			// Intentionally ignore exception.
		}
	}

	@Test
	public void testConstructorWithNewFile() throws Exception {
		PersistentQueue<String> queue1 = createTemporaryQueue();
		assertTrue( queue1.getStore().exists() );
	}

	@Test
	public void testConstructorWithExistingFile() throws Exception {
		PersistentQueue<String> queue1 = createTemporaryQueue();
		assertTrue( queue1.getStore().exists() );

		queue1.add( "element 1" );
		queue1.add( "element 2" );

		Queue<String> queue2 = new PersistentQueue<>( queue1.getStore() );
		assertEquals( 2, queue2.size() );
	}

	@Test
	public void testAdd() throws Exception {
		PersistentQueue<String> queue = createTemporaryQueue();
		queue.add( "element 1" );
		assertEquals( 1, queue.size() );
		queue.add( "element 2" );
		assertEquals( 2, queue.size() );
	}

	@Test
	public void testRemove() throws Exception {
		PersistentQueue<String> queue = createTemporaryQueue();
		queue.add( "element 1" );
		assertEquals( 1, queue.size() );
		queue.add( "element 2" );
		assertEquals( 2, queue.size() );
		assertEquals( "element 1", queue.remove() );
		assertEquals( 1, queue.size() );
		assertEquals( "element 2", queue.remove() );
		assertEquals( 0, queue.size() );
		try {
			queue.remove();
			fail();
		} catch( NoSuchElementException exception ) {
			// Intentionally ignore exception.
		}
	}

	@Test
	public void testOffer() throws Exception {
		PersistentQueue<String> queue = createTemporaryQueue();
		queue.offer( "element 1" );
		assertEquals( 1, queue.size() );
		queue.offer( "element 2" );
		assertEquals( 2, queue.size() );
	}

	@Test
	public void testPoll() throws Exception {
		PersistentQueue<String> queue = createTemporaryQueue();
		queue.offer( "element 1" );
		assertEquals( 1, queue.size() );
		queue.offer( "element 2" );
		assertEquals( 2, queue.size() );
		assertEquals( "element 1", queue.poll() );
		assertEquals( 1, queue.size() );
		assertEquals( "element 2", queue.poll() );
		assertEquals( 0, queue.size() );
		assertNull( queue.poll() );
	}

	@Test
	public void testDefraggingWithRemove() throws Exception {
		PersistentQueue<String> queue = createTemporaryQueue( 0 );
		queue.add( "element 1" );
		assertEquals( 1, queue.size() );
		queue.add( "element 2" );
		assertEquals( 2, queue.size() );
		assertEquals( "element 1", queue.remove() );
		assertEquals( 1, queue.size() );
		assertEquals( "element 2", queue.remove() );
		assertEquals( 0, queue.size() );
		try {
			queue.remove();
			fail();
		} catch( NoSuchElementException exception ) {
			// Intentionally ignore exception.
		}
	}

	@Test
	public void testDefraggingWithPoll() throws Exception {
		PersistentQueue<String> queue = createTemporaryQueue( 0 );
		queue.offer( "element 1" );
		assertEquals( 1, queue.size() );
		queue.offer( "element 2" );
		assertEquals( 2, queue.size() );
		assertEquals( "element 1", queue.poll() );
		assertEquals( 1, queue.size() );
		assertEquals( "element 2", queue.poll() );
		assertEquals( 0, queue.size() );
		assertNull( queue.poll() );
	}

	private PersistentQueue<String> createTemporaryQueue() throws IOException {
		return createTemporaryQueue( PersistentQueue.DEFAULT_DEFRAG_INTERVAL );
	}

	private PersistentQueue<String> createTemporaryQueue( int defragInterval ) throws IOException {
		String filename = "store" + System.nanoTime() + ".queue";
		File store = new File( System.getProperty( "java.io.tmpdir" ), filename );
		store.deleteOnExit();
		return new PersistentQueue<>( store, defragInterval );
	}

}
