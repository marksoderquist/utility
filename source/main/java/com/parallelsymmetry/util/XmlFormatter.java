/**
 * 
 */
package com.parallelsymmetry.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class XmlFormatter extends Thread {

	private boolean finished;

	private InputStream input;

	private OutputStream output;

	private int indent;

	private IOException exception;

	public XmlFormatter( InputStream input, OutputStream output ) {
		this( input, output, 2 );
	}

	public XmlFormatter( InputStream input, OutputStream output, int indent ) {
		super( "XML Formatter" );

		this.input = input;
		this.output = output;
		this.indent = indent;

		setDaemon( true );
	}

	public void run() {
		try {
			XmlUtil.format( input, output, indent );
		} catch( IOException exception ) {
			this.exception = exception;
		} finally {
			synchronized( this ) {
				finished = true;
				notifyAll();
			}
		}
	}

	public synchronized void waitFor() throws IOException, InterruptedException {
		while( !finished ) {
			wait();
		}
		if( exception != null ) throw exception;
	}

}
