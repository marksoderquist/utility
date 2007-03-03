package org.novaworx.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.logging.Level;

/**
 * The IOPump class reads data from an InputStream writes it to an OutputStream.
 * Subclasses may change the write behavior by overriding the write() method.
 * 
 * @author mvsoder
 */
public class IOPump implements Runnable {

	public static final int DEFAULT_BUFFER_SIZE = 11;

	private String name;

	private InputStream input;

	private OutputStream output;

	private Reader reader;

	private Writer writer;

	private Thread worker;

	private boolean execute;

	private boolean logEnabled;

	private boolean logContent;

	private Level logAtLevel = Log.DEBUG;

	private int bufferSize;

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
		this.input = input;
		this.output = output;
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
		this.reader = new InputStreamReader( input, charset );
		this.writer = writer;
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
		this.reader = reader;
		this.writer = new OutputStreamWriter( output, charset );
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
		this.reader = reader;
		this.writer = writer;
		this.bufferSize = bufferSize;
	}

	public boolean isLogEnabled() {
		return logEnabled;
	}

	public void setLogEnabled( boolean logEnabled ) {
		this.logEnabled = logEnabled;
	}

	public Level getLogAtLevel() {
		return logAtLevel;
	}

	public void setLogAtLevel( Level logAtLevel ) {
		this.logAtLevel = logAtLevel;
	}

	public final boolean isLogContent() {
		return logContent;
	}

	public final void setLogContent( boolean showContent ) {
		this.logContent = showContent;
	}

	public final void start() {
		if( input == null & reader == null ) throw new IllegalArgumentException( "Must specify either an input stream or reader." );
		if( output == null & writer == null ) throw new IllegalArgumentException( "Must specify either an output stream or writer." );

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

	public final void startAndWait() {
		start();
		waitFor();
	}

	public final void startAndWait( int timeout ) {
		start();
		waitFor( timeout );
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
		waitFor();
	}

	public final void stopAndWait( int timeout ) {
		stop();
		waitFor( timeout );
	}

	public final void waitFor() {
		try {
			worker.join();
		} catch( InterruptedException aoException ) {
			//
		}
	}

	public final void waitFor( int timeout ) {
		try {
			worker.join( timeout );
		} catch( InterruptedException aoException ) {
			//
		}
	}

	public final void run() {
		boolean newline = false;
		StringBuilder builder = new StringBuilder();

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

		if( logEnabled ) Log.write( logAtLevel, "IOPump started." );

		try {
			int read = 0;

			while( execute ) {
				// Read data.
				if( reader == null ) {
					read = readFromInputStream( bytearray );
				} else {
					read = readFromReader( chararray );
				}

				if( read == -1 ) {
					if( builder.length() > 0 ) Log.write( logAtLevel, builder.toString() );
					execute = false;
					continue;
				}

				if( logEnabled && logContent ) {
					int datum = 0;
					for( int index = 0; index < read; index++ ) {
						if( reader == null ) {
							datum = bytearray[ index ];
						} else {
							datum = chararray[ index ];
						}

						if( datum == 10 || datum == 13 ) {
							if( newline ) {
								continue;
							} else {
								Log.write( logAtLevel, builder.toString() );
								builder.delete( 0, builder.length() );
								newline = true;
							}
						} else {
							builder.append( TextUtil.toPrintableString( (char)datum ) );
							newline = false;
						}
					}
				}

				// Write data.
				if( writer == null ) {
					if( reader == null ) {
						writeToOutputStream( bytearray, read );
					}
				} else {
					if( input == null ) {
						writeToWriter( chararray, read );
					}
				}
			}
		} catch( IOException exception ) {
			exception.printStackTrace();
		} finally {
			if( logEnabled ) Log.write( logAtLevel, "IOPump finished." );
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
