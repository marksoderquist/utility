package com.parallelsymmetry.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Implements a persistent(stored on disk) FIFO BlockingQueue.
 * 
 * @author mvsoder
 * @param <E> Any serializable object.
 */
public class PersistentBlockingQueue<E extends Serializable> extends PersistentQueue<E> implements BlockingQueue<E> {

	public PersistentBlockingQueue( File file, int defragInterval ) throws IOException {
		super( file, defragInterval );
	}

	public PersistentBlockingQueue( File file ) throws IOException {
		super( file );
	}

	public PersistentBlockingQueue( String file, int defragInterval ) throws IOException {
		super( file, defragInterval );
	}

	public PersistentBlockingQueue( String file ) throws IOException {
		super( file );
	}

	@Override
	public void put( E element ) throws InterruptedException {
		queue.put( element );

		try {
			appendToStore( store, element );
		} catch( IOException exception ) {
			throw new RuntimeException( exception );
		}
	}

	@Override
	public boolean offer( E element, long timeout, TimeUnit unit ) throws InterruptedException {
		boolean result = queue.offer( element, timeout, unit );

		if( result == true ) {
			try {
				appendToStore( store, element );
			} catch( IOException exception ) {
				throw new RuntimeException( exception );
			}
		}

		return result;
	}

	@Override
	public E poll( long timeout, TimeUnit unit ) throws InterruptedException {
		E element = queue.poll( timeout, unit );
		if( element != null ) removeDefrag( 1 );
		return element;
	}

	@Override
	public E take() throws InterruptedException {
		E element = queue.take();
		if( element != null ) removeDefrag( 1 );
		return element;
	}

	@Override
	public int drainTo( Collection<? super E> collection ) {
		int result = queue.drainTo( collection );
		removeDefrag( result );
		return result;
	}

	@Override
	public int drainTo( Collection<? super E> collection, int maxElements ) {
		int result = queue.drainTo( collection );
		removeDefrag( result );
		return result;
	}

	@Override
	public int remainingCapacity() {
		return queue.remainingCapacity();
	}

}
