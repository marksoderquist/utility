package com.parallelsymmetry.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

public class CountingReader extends Reader {

	private AtomicLong count = new AtomicLong();

	private Reader reader;

	public CountingReader( Reader reader ) {
		this.reader = reader;
		listeners = new CopyOnWriteArraySet<CountingListener>();
	}

	public long getCount() {
		return count.get();
	}

	private Set<CountingListener> listeners;

	public void addCountingListener( CountingListener listener ) {
		listeners.add( listener );
	}

	public void removeCountingListener( CountingListener listener ) {
		listeners.remove( listener );
	}

	public void close() throws IOException {
		reader.close();
	}

	public boolean equals( Object obj ) {
		return reader.equals( obj );
	}

	public int hashCode() {
		return reader.hashCode();
	}

	public void mark( int readAheadLimit ) throws IOException {
		reader.mark( readAheadLimit );
	}

	public boolean markSupported() {
		return reader.markSupported();
	}

	public int read() throws IOException {
		int read = reader.read();
		if( read > 0 ) triggerCountingEvent( count.incrementAndGet() );
		return read;
	}

	public int read( char[] cbuf, int off, int len ) throws IOException {
		int read = reader.read( cbuf, off, len );
		if( read > 0 ) triggerCountingEvent( this.count.addAndGet( read ) );
		return read;
	}

	public int read( char[] cbuf ) throws IOException {
		int read = reader.read( cbuf );
		if( read > 0 ) triggerCountingEvent( this.count.addAndGet( read ) );
		return read;
	}

	public int read( CharBuffer target ) throws IOException {
		int read = reader.read( target );
		if( read > 0 ) triggerCountingEvent( this.count.addAndGet( read ) );
		return read;
	}

	public boolean ready() throws IOException {
		return reader.ready();
	}

	public void reset() throws IOException {
		reader.reset();
	}

	public long skip( long n ) throws IOException {
		return reader.skip( n );
	}

	public String toString() {
		return reader.toString();
	}

	protected void triggerCountingEvent( long count ) {
		fireCountingEvent( new CountingEvent( count ) );
	}

	protected void fireCountingEvent( CountingEvent event ) {
		for( CountingListener listener : listeners ) {
			listener.countUpdated( event );
		}
	}

}
