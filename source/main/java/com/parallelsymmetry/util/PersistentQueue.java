package com.parallelsymmetry.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Implements a persistent(stored on disk) FIFO Queue.
 * 
 * @author mvsoder
 * @param <E> Any serializable object.
 */
public class PersistentQueue<E extends Serializable> implements Queue<E> {

	public static final int DEFAULT_DEFRAG_INTERVAL = 20;

	public static final String TEMPFILE_SUFFIX = "tmp";

	private final File store;

	private final int defragInterval;

	private int removesSinceLastDefrag;

	private Queue<E> queue;

	public PersistentQueue( String file ) throws IOException {
		this( file, DEFAULT_DEFRAG_INTERVAL );
	}

	public PersistentQueue( String file, int defragInterval ) throws IOException {
		this( new File( file ), defragInterval );
	}

	public PersistentQueue( File file ) throws IOException {
		this( file, DEFAULT_DEFRAG_INTERVAL );
	}

	public PersistentQueue( File file, int defragInterval ) throws IOException {
		this.store = file;
		this.defragInterval = defragInterval;

		queue = new ConcurrentLinkedQueue<E>();

		if( file.exists() ) {
			readFromStore( file );
		} else {
			createNewStore( file );
		}
	}

	public File getStore() {
		return store;
	}

	@Override
	public boolean add( E element ) {
		boolean result = queue.add( element );

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
	public boolean addAll( Collection< ? extends E> collection ) {
		boolean result = queue.addAll( collection );

		if( result == true ) {
			try {
				for( E element : collection ) {
					appendToStore( store, element );
				}
			} catch( IOException exception ) {
				throw new RuntimeException( exception );
			}
		}

		return result;
	}

	@Override
	public void clear() {
		queue.clear();

		try {
			defragStore( store );
		} catch( IOException exception ) {
			throw new RuntimeException( exception );
		}
	}

	@Override
	public boolean contains( Object object ) {
		return queue.contains( object );
	}

	@Override
	public boolean containsAll( Collection< ? > collection ) {
		return queue.containsAll( collection );
	}

	@Override
	public E element() {
		return queue.element();
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return queue.iterator();
	}

	@Override
	public boolean offer( E element ) {
		boolean result = queue.offer( element );

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
	public E peek() {
		return queue.peek();
	}

	@Override
	public E poll() {
		E element = queue.poll();

		// Defragment the store if necessary.
		if( element != null ) {
			removesSinceLastDefrag++;
			try {
				if( removesSinceLastDefrag >= defragInterval ) {
					defragStore( store );
					removesSinceLastDefrag = 0;
				} else {
					appendToStore( store, new DeleteMarker() );
				}
			} catch( IOException exception ) {
				throw new RuntimeException( exception );
			}
		}

		return element;
	}

	@Override
	public E remove() {
		E element = queue.remove();

		// Defragment the store if necessary.
		if( element != null ) {
			removesSinceLastDefrag++;
			try {
				if( removesSinceLastDefrag >= defragInterval ) {
					defragStore( store );
					removesSinceLastDefrag = 0;
				} else {
					appendToStore( store, new DeleteMarker() );
				}
			} catch( IOException exception ) {
				throw new RuntimeException( exception );
			}
		}

		return element;
	}

	@Override
	public boolean remove( Object object ) {
		boolean result = queue.remove( object );

		if( result == true ) {
			try {
				defragStore( store );
			} catch( IOException exception ) {
				throw new RuntimeException( exception );
			}
		}

		return true;
	}

	@Override
	public boolean removeAll( Collection< ? > collection ) {
		boolean result = queue.removeAll( collection );

		if( result == true ) {
			try {
				defragStore( store );
			} catch( IOException exception ) {
				throw new RuntimeException( exception );
			}
		}

		return true;
	}

	@Override
	public boolean retainAll( Collection< ? > collection ) {
		boolean result = queue.retainAll( collection );

		if( result == true ) {
			try {
				defragStore( store );
			} catch( IOException exception ) {
				throw new RuntimeException( exception );
			}
		}

		return true;
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public Object[] toArray() {
		return queue.toArray();
	}

	@Override
	public <T> T[] toArray( T[] array ) {
		return queue.toArray( array );
	}

	@Override
	public boolean equals( Object object ) {
		return queue.equals( object );
	}

	@Override
	public int hashCode() {
		return queue.hashCode();
	}

	private void createNewStore( File store ) throws IOException {
		if( !store.createNewFile() ) {
			throw new IOException( "Could not create new store: " + store );
		}
	}

	@SuppressWarnings( "unchecked" )
	private void readFromStore( File store ) throws IOException {
		// Create the input stream from the store.
		FileInputStream fileInput = new FileInputStream( store );

		// Empty the queue for new data.
		queue.clear();

		// Read all the data in the store.
		while( fileInput.available() > 0 ) {
			Object element;

			// Create the object input stream using the file input stream.
			ObjectInputStream objectInput = new ObjectInputStream( fileInput );

			// Read the next store element.
			try {
				element = objectInput.readObject();
			} catch( ClassNotFoundException e ) {
				throw new IOException( e.getMessage(), e );
			}

			// Store the element.
			if( element instanceof DeleteMarker ) {
				queue.remove( 0 );
			} else {
				try {
					queue.add( (E)element );
				} catch( ClassCastException exception ) {
					throw new IOException( exception.toString() );
				}
			}
		}
	}

	private synchronized void appendToStore( File store, Serializable element ) throws IOException {
		// Create the output streams.
		FileOutputStream fileOutput = new FileOutputStream( store, true );
		ObjectOutputStream objectOutput = new ObjectOutputStream( fileOutput );

		objectOutput.writeObject( element );

		// Flush the output stream.
		objectOutput.flush();
		fileOutput.flush();

		// Close the output streams.
		objectOutput.close();
		fileOutput.close();
	}

	private synchronized void writeToStore( File store ) throws IOException {
		FileOutputStream fileOutput = new FileOutputStream( store );

		ObjectOutputStream objectOutput = null;
		for( E element : queue ) {
			objectOutput = new ObjectOutputStream( fileOutput );
			objectOutput.writeObject( element );
			objectOutput.flush();
			objectOutput.close();
		}

		fileOutput.flush();
		fileOutput.close();
	}

	private synchronized void defragStore( File store ) throws IOException {
		File defraggedStore = new File( store.getParent(), store.getName() + "." + TEMPFILE_SUFFIX );

		// Write out a new file.
		writeToStore( defraggedStore );

		// Rename the defragmented file to the original file name.
		store.delete();
		if( !defraggedStore.renameTo( store ) ) {
			throw new IOException( "Unable to rename " + defraggedStore + " to " + store + "." );
		}

		removesSinceLastDefrag = 0;
	}

	private static final class DeleteMarker implements Serializable {
		private static final long serialVersionUID = 6656403687908487381L;
	}

}