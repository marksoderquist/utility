package com.parallelsymmetry.escape.utility;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class PersistentBlockingQueueTest extends PersistentQueueTest {

	@Test
	public void testPut() throws Exception {
		PersistentBlockingQueue<String> queue = createTemporaryQueue();
		queue.put( "element 1" );
		assertEquals( 1, queue.size() );
		queue.put( "element 2" );
		assertEquals( 2, queue.size() );
	}

	@Test
	public void testTake() throws Exception {
		PersistentBlockingQueue<String> queue = createTemporaryQueue();
		queue.put( "element 1" );
		assertEquals( 1, queue.size() );
		queue.put( "element 2" );
		assertEquals( 2, queue.size() );
		assertEquals( "element 1", queue.take() );
		assertEquals( 1, queue.size() );
		assertEquals( "element 2", queue.take() );
		assertEquals( 0, queue.size() );
	}

	@Override
	@Test
	public void testOffer() throws Exception {
		PersistentBlockingQueue<String> queue = createTemporaryQueue();
		queue.offer( "element 1", 1, TimeUnit.SECONDS );
		assertEquals( 1, queue.size() );
		queue.offer( "element 2", 1, TimeUnit.SECONDS );
		assertEquals( 2, queue.size() );
	}

	@Override
	@Test
	public void testPoll() throws Exception {
		PersistentBlockingQueue<String> queue = createTemporaryQueue();
		queue.offer( "element 1" );
		assertEquals( 1, queue.size() );
		queue.offer( "element 2" );
		assertEquals( 2, queue.size() );
		assertEquals( "element 1", queue.poll( 1, TimeUnit.SECONDS ) );
		assertEquals( 1, queue.size() );
		assertEquals( "element 2", queue.poll( 1, TimeUnit.SECONDS ) );
		assertEquals( 0, queue.size() );
		assertNull( queue.poll() );
	}

	private PersistentBlockingQueue<String> createTemporaryQueue() throws IOException {
		return createTemporaryQueue( PersistentBlockingQueue.DEFAULT_DEFRAG_INTERVAL );
	}

	private PersistentBlockingQueue<String> createTemporaryQueue( int defragInterval ) throws IOException {
		File store = File.createTempFile( "store", ".queue" );
		store.deleteOnExit();
		return new PersistentBlockingQueue<String>( store, defragInterval );
	}
}
