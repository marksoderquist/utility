package com.parallelsymmetry.utility;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class LineOutputStream extends OutputStream {

	private StringBuilder builder;

	private boolean terminator;

	private Set<LineListener> listeners;
	
	private boolean closed;

	public LineOutputStream() {
		builder = new StringBuilder();
		listeners = new CopyOnWriteArraySet<LineListener>();
	}

	@Override
	public void write( int data ) throws IOException {
		checkClosed();
		switch( data ) {
			case 10:
			case 13: {
				if( terminator ) break;
				line( builder.toString() );
				builder.delete( 0, builder.length() );
				terminator = true;
				break;
			}
			default: {
				builder.append( (char)data );
				terminator = false;
				break;
			}
		}
	}
	
	@Override
	public void close() {
		if( closed ) return;
		if( builder.length() == 0 ) return;
		line( builder.toString() );
		builder.delete( 0, builder.length() );
		closed = true;
	}

	public void addLineListener( LineListener listener ) {
		listeners.add( listener );
	}

	public void removeLineListener( LineListener listener ) {
		listeners.remove( listener );
	}

	private void line( String line ) {
		for( LineListener listener : listeners ) {
			listener.line( line );
		}
	}

	private void checkClosed() throws IOException {
		if( closed ) throw new IOException( "Stream is closed" );
	}
	
}
