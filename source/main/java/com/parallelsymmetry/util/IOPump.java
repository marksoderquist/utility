package com.parallelsymmetry.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import com.parallelsymmetry.log.Log;

/**
 * The IOPump class reads data from an InputStream writes it to an OutputStream.
 * Subclasses may change the write behavior by overriding the write() method.
 * 
 * @author mvsoder
 */
public class IOPump implements Runnable {

	public static final int DEFAULT_BUFFER_SIZE = 1024;

	public static final int DEFAULT_LINE_LENGTH = 160;

	private String name;

	private InputStream input;

	private OutputStream output;

	private Reader reader;

	private Writer writer;

	private Thread worker;

	private boolean execute;

	private boolean interruptOnStop;

	private boolean logEnabled;

	private boolean logContent;

	private int bufferSize;

	private int lineLength = DEFAULT_LINE_LENGTH;

	private boolean started;

	private Object startLock = new Object();

	public IOPump( InputStream input, OutputStream output ) {
		this( null, input, output, DEFAULT_BUFFER_SIZE );
	}

	public IOPump( String name, InputStream input, OutputStream output ) {
		this( name, input, output, DEFAULT_BUFFER_SIZE );
	}

	public IOPump( InputStream input, OutputStream output, int bufferSize ) {
		this( null, input, output, bufferSize );
	}

	public IOPump( String name, InputStream input, OutputStream output, int bufferSize ) {
		this.name = name;
		this.input = new BufferedInputStream( input );
		this.output = new BufferedOutputStream( output );
		this.bufferSize = bufferSize;
	}

	public IOPump( InputStream input, Writer writer ) {
		this( null, input, writer, Charset.defaultCharset(), DEFAULT_BUFFER_SIZE );
	}

	public IOPump( String name, InputStream input, Writer writer ) {
		this( name, input, writer, Charset.defaultCharset(), DEFAULT_BUFFER_SIZE );
	}

	public IOPump( InputStream input, Writer writer, String charset ) {
		this( null, input, writer, charset, DEFAULT_BUFFER_SIZE );
	}

	public IOPump( InputStream input, Writer writer, Charset charset ) {
		this( null, input, writer, charset, DEFAULT_BUFFER_SIZE );
	}

	public IOPump( String name, InputStream input, Writer writer, String charset ) {
		this( name, input, writer, charset, DEFAULT_BUFFER_SIZE );
	}

	public IOPump( String name, InputStream input, Writer writer, Charset charset ) {
		this( name, input, writer, charset, DEFAULT_BUFFER_SIZE );
	}

	public IOPump( String name, InputStream input, Writer writer, String charset, int bufferSize ) {
		this( name, input, writer, charset == null ? Charset.defaultCharset() : Charset.forName( charset ), bufferSize );
	}

	public IOPump( String name, InputStream input, Writer writer, Charset charset, int bufferSize ) {
		this.name = name;
		this.reader = new BufferedReader( new InputStreamReader( input, charset ) );
		this.writer = new BufferedWriter( writer );
		this.bufferSize = bufferSize;
	}

	public IOPump( Reader reader, OutputStream output ) {
		this( null, reader, output, Charset.defaultCharset(), DEFAULT_BUFFER_SIZE );
	}

	public IOPump( String name, Reader reader, OutputStream output ) {
		this( name, reader, output, Charset.defaultCharset(), DEFAULT_BUFFER_SIZE );
	}

	public IOPump( Reader reader, OutputStream output, String charset ) {
		this( null, reader, output, charset, DEFAULT_BUFFER_SIZE );
	}

	public IOPump( Reader reader, OutputStream output, Charset charset ) {
		this( null, reader, output, charset, DEFAULT_BUFFER_SIZE );
	}

	public IOPump( String name, Reader reader, OutputStream output, String charset ) {
		this( name, reader, output, charset, DEFAULT_BUFFER_SIZE );
	}

	public IOPump( String name, Reader reader, OutputStream output, Charset charset ) {
		this( name, reader, output, charset, DEFAULT_BUFFER_SIZE );
	}

	public IOPump( String name, Reader reader, OutputStream output, String charset, int bufferSize ) {
		this( name, reader, output, charset == null ? Charset.defaultCharset() : Charset.forName( charset ), bufferSize );
	}

	public IOPump( String name, Reader reader, OutputStream output, Charset charset, int bufferSize ) {
		this.name = name;
		this.reader = new BufferedReader( reader );
		this.writer = new BufferedWriter( new OutputStreamWriter( output, charset ) );
		this.bufferSize = bufferSize;
	}

	public IOPump( Reader reader, Writer writer ) {
		this( null, reader, writer, DEFAULT_BUFFER_SIZE );
	}

	public IOPump( String name, Reader reader, Writer writer ) {
		this( name, reader, writer, DEFAULT_BUFFER_SIZE );
	}

	public IOPump( Reader reader, Writer writer, int bufferSize ) {
		this( null, reader, writer, bufferSize );
	}

	public IOPump( String name, Reader reader, Writer writer, int bufferSize ) {
		this.name = name;
		this.reader = new BufferedReader( reader );
		this.writer = new BufferedWriter( writer );
		this.bufferSize = bufferSize;
	}

	public boolean isInterruptOnStop() {
		return interruptOnStop;
	}

	public void setInterruptOnStop( boolean interruptOnStop ) {
		this.interruptOnStop = interruptOnStop;
	}

	public int getLineLength() {
		return lineLength;
	}

	public void setLineLength( int length ) {
		this.lineLength = length;
	}

	public boolean isLogEnabled() {
		return logEnabled;
	}

	public void setLogEnabled( boolean logEnabled ) {
		this.logEnabled = logEnabled;
	}

	public final boolean isLogContent() {
		return logContent;
	}

	public final void setLogContent( boolean showContent ) {
		this.logContent = showContent;
	}

	/**
	 * Start the pump thread and return immediately.
	 */
	public final void start() {
		if( input == null & reader == null ) throw new IllegalArgumentException( "Must specify either an input stream or reader." );
		if( output == null & writer == null ) throw new IllegalArgumentException( "Must specify either an output stream or writer." );

		if( logEnabled ) Log.write( Log.DEBUG, name + " IO Pump starting..." );

		execute = true;
		if( name == null ) {
			worker = new Thread( this );
		} else {
			worker = new Thread( this, name );
		}
		worker.setPriority( Thread.NORM_PRIORITY );
		worker.setDaemon( true );
		worker.start();
	}

	/**
	 * Start the pump thread and wait for the pump thread to begin.
	 * 
	 * @throws InterruptedException
	 */
	public final void startAndWait() throws InterruptedException {
		start();
		waitForStart( 0 );
	}

	/**
	 * Start the pump thread and wait for the pump thread to begin.
	 * 
	 * @param timeout
	 */
	public final void startAndWait( long timeout ) throws InterruptedException {
		start();
		waitForStart( timeout );
	}

	private final void waitForStart( long timeout ) {
		while( !started ) {
			synchronized( startLock ) {
				try {
					startLock.wait( timeout );
				} catch( InterruptedException exception ) {
					// Intentionally ignore exception.
				}
			}
		}
	}

	private final void startNotify() {
		synchronized( startLock ) {
			started = true;
			startLock.notifyAll();
		}
	}

	public final boolean isExecuting() {
		return worker.isAlive();
	}

	public final void stop() {
		execute = false;
		if( interruptOnStop ) worker.interrupt();
	}

	public final void stopAndWait() throws InterruptedException {
		stop();
		waitFor();
	}

	public final void stopAndWait( long timeout ) {
		stop();
		waitFor( timeout );
	}

	public final void waitFor() throws InterruptedException {
		worker.join();
	}

	public final void waitFor( long timeout ) {
		try {
			worker.join( timeout );
		} catch( InterruptedException aoException ) {
			//
		}
	}

	public final void run() {
		try {
			// Check for bad parameters.
			if( input == null & reader == null || output == null & writer == null ) return;

			// Setup the data buffer.
			byte[] bytearray = null;
			char[] chararray = null;
			if( reader == null ) {
				bytearray = new byte[ bufferSize ];
			} else {
				chararray = new char[ bufferSize ];
			}

			if( logEnabled ) Log.write( Log.TRACE, name, " IOPump started." );
			startNotify();

			int read = 0;
			boolean binary = false;
			boolean lineTerminator = false;
			StringBuilder builder = new StringBuilder();

			while( execute ) {
				// Read data.
				if( reader == null ) {
					read = readFromInputStream( bytearray );
				} else {
					read = readFromReader( chararray );
				}

				if( read == -1 ) {
					if( logEnabled && logContent && builder.length() > 0 ) Log.write( Log.TRACE, name, ": ", builder.toString() );
					execute = false;
					continue;
				}

				if( logEnabled && logContent ) {
					int datum = 0;
					for( int index = 0; index < read; index++ ) {
						if( reader == null ) {
							datum = bytearray[ index ];
							if( datum < 0 ) datum += 256;
						} else {
							datum = chararray[ index ];
							if( datum < 0 ) datum += 65536;
						}

						if( datum < 32 || datum > 127 ) binary = true;

						if( datum == 10 || datum == 13 || builder.length() > lineLength || ( binary && builder.length() > 80 ) ) {
							if( !lineTerminator ) {
								Log.write( Log.TRACE, name, ": ", builder.toString() );
								builder.delete( 0, builder.length() );
								binary = false;
							}
							lineTerminator = true;
						} else {
							builder.append( TextUtil.toPrintableString( (char)datum ) );
							lineTerminator = false;
						}
					}
				}

				// Write data.
				if( writer == null ) {
					writeToOutputStream( bytearray, read );
				} else {
					writeToWriter( chararray, read );
				}
			}
		} catch( InterruptedIOException exception ) {
			// Intentionally ignore exception.
		} catch( IOException exception ) {
			if( logEnabled ) Log.write( exception, name, " ", exception.getMessage() );
		} finally {
			startNotify();
			if( logEnabled ) Log.write( Log.TRACE, name, " IOPump finished." );
		}
	}

	protected int readFromInputStream( byte[] buffer ) throws IOException {
		return input.read( buffer );
	}

	protected int readFromReader( char[] buffer ) throws IOException {
		return reader.read( buffer );
	}

	protected void writeToOutputStream( byte[] buffer, int count ) throws IOException {
		output.write( buffer, 0, count );
		output.flush();
	}

	protected void writeToWriter( char[] buffer, int count ) throws IOException {
		writer.write( buffer, 0, count );
		writer.flush();
	}

}
