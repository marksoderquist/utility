package com.parallelsymmetry.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.concurrent.atomic.AtomicLong;

public class CountingReader extends Reader {

	private AtomicLong count = new AtomicLong();

	private Reader reader;

	public CountingReader( Reader reader ) {
		this.reader = reader;
	}

	public long getCount() {
		return count.get();
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
		// FIXME Add event handling.
		int read = reader.read();
		if( read > 0 ) count.incrementAndGet();
		return read;
	}

	public int read( char[] cbuf, int off, int len ) throws IOException {
		// FIXME Add event handling.
		int read = reader.read( cbuf, off, len );
		if( read > 0 ) this.count.addAndGet( read );
		return read;
	}

	public int read( char[] cbuf ) throws IOException {
		// FIXME Add event handling.
		int read = reader.read( cbuf );
		if( read > 0 ) this.count.addAndGet( read );
		return read;
	}

	public int read( CharBuffer target ) throws IOException {
		// FIXME Add event handling.
		int read = reader.read( target );
		if( read > 0 ) this.count.addAndGet( read );
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

}
