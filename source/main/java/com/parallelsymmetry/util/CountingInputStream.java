package com.parallelsymmetry.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class CountingInputStream extends InputStream {

	private AtomicLong count = new AtomicLong();

	private InputStream input;

	public CountingInputStream( InputStream input ) {
		this.input = input;
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

	public int available() throws IOException {
		return input.available();
	}

	public void close() throws IOException {
		input.close();
	}

	public boolean equals( Object obj ) {
		return input.equals( obj );
	}

	public int hashCode() {
		return input.hashCode();
	}

	public void mark( int readlimit ) {
		input.mark( readlimit );
	}

	public boolean markSupported() {
		return input.markSupported();
	}

	public int read() throws IOException {
		int read = input.read();
		if( read > 0 ) triggerCountingEvent( count.incrementAndGet() );
		return read;
	}

	public int read( byte[] b, int off, int len ) throws IOException {
		int read = input.read( b, off, len );
		if( read > 0 ) triggerCountingEvent( count.addAndGet( read ) );
		return read;
	}

	public int read( byte[] b ) throws IOException {
		int read = input.read( b );
		if( read > 0 ) triggerCountingEvent( count.addAndGet( read ) );
		return read;
	}

	public void reset() throws IOException {
		input.reset();
	}

	public long skip( long n ) throws IOException {
		return input.skip( n );
	}

	public String toString() {
		return input.toString();
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
