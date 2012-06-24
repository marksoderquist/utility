package com.parallelsymmetry.escape.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

public final class ConsoleReader extends Thread {

	private InputStream input;

	private OutputStream output;

	public ConsoleReader( Process process ) {
		this( process, System.out );
	}

	public ConsoleReader( Process process, OutputStream output ) {
		super( "Console Reader" );
		this.input = process.getInputStream();
		this.output = output;
		setDaemon( true );
	}

	@Override
	public void run() {
		String line = null;
		try {
			BufferedReader reader = new BufferedReader( new InputStreamReader( input ) );
			PrintStream printer = new PrintStream( output );
			while( ( line = reader.readLine() ) != null ) {
				printer.println( line );
			}
		} catch( IOException exception ) {
			exception.printStackTrace();
		}
	}

}
