package org.novaworx.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The IOPump class reads data from an InputStream writes it to an OutputStream.
 * Subclasses may change the write behavior by overriding the write() method.
 * 
 * @author mvsoder
 */
public class IOPump implements Runnable {

	public static final int DEFAULT_BUFFER_SIZE = 1024;

	private String name;

	private InputStream input;

	private OutputStream output;

	private Thread worker;

	private boolean execute;

	private boolean showContent;

	private boolean newline = true;

	private int bufferSize = DEFAULT_BUFFER_SIZE;

	public IOPump( String name, InputStream input, OutputStream output ) {
		this( name, input, output, DEFAULT_BUFFER_SIZE );
	}

	public IOPump( String name, InputStream input, OutputStream output, int bufferSize ) {
		this.name = name;
		this.input = input;
		this.output = output;
		this.bufferSize = bufferSize;
	}

	public final boolean isShowContent() {
		return showContent;
	}

	public final void setShowContent( boolean showContent ) {
		this.showContent = showContent;
	}

	public final void start() {
		if( input == null ) throw new IllegalArgumentException( "Input stream cannot be null." );
		if( output == null ) throw new IllegalArgumentException( "Output stream cannot be null." );

		execute = true;
		worker = new Thread( this, name );
		worker.setPriority( Thread.NORM_PRIORITY );
		worker.setDaemon( true );
		worker.start();
	}

	public final void startAndWait() {
		start();
		try {
			worker.join();
		} catch( InterruptedException aoException ) {
			//
		}
	}

	public final void startAndWait( int timeout ) {
		start();
		try {
			worker.join( timeout );
		} catch( InterruptedException aoException ) {
			//
		}
	}

	public final boolean isExecuting() {
		return worker.isAlive();
	}

	public final void stop() {
		execute = false;
		worker.interrupt();
	}

	public final void stopAndWait() {
		stop();
		try {
			worker.join();
		} catch( InterruptedException aoException ) {
			//
		}
	}

	public final void stopAndWait( int timeout ) {
		stop();
		try {
			worker.join( timeout );
		} catch( InterruptedException aoException ) {
			//
		}
	}

	public final void run() {
		// Check for bad parameters.
		if( input == null || output == null ) return;

		// Setup the data buffer.
		byte[] byteBuffer = new byte[ bufferSize ];

		Log.write( Log.DEBUG, "IOPump running." );

		try {
			int dataRead = 0;
			while( execute ) {
				// Read data.
				dataRead = read( byteBuffer );

				if( dataRead == -1 ) {
					execute = false;
					continue;
				}

				if( showContent ) {
					for( int index = 0; index < dataRead; index++ ) {

						if( newline ) {
							boolean print = false;
							if( byteBuffer[ index ] != 10 && byteBuffer[ index ] != 13 ) {
								print = true;
							}

							if( print ) {
								Log.write();
								System.out.print( name == null ? "" : name + ": " );
								newline = false;
							}
						}

						if( byteBuffer[ index ] == 10 || byteBuffer[ index ] == 13 ) {
							newline = true;
						}

						System.out.print( TextUtil.toPrintableString( (char)byteBuffer[ index ] ) );
					}
				}

				// Write data.
				write( byteBuffer, dataRead );
			}
		} catch( IOException exception ) {
			exception.printStackTrace();
		} finally {
			Log.write( Log.DEBUG, "IOPump terminating." );
		}
	}

	protected int read( byte[] buffer ) throws IOException {
		return input.read( buffer );
	}

	protected void write( byte[] buffer, int count ) throws IOException {
		output.write( buffer, 0, count );
		output.flush();
	}

}
