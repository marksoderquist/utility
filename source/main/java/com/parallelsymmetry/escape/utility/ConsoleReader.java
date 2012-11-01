package com.parallelsymmetry.escape.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public final class ConsoleReader extends Thread {

	private Process process;

	private OutputStream output;

	public ConsoleReader( Process process ) {
		this( process, System.out );
	}

	public ConsoleReader( Process process, OutputStream output ) {
		super( "Console Reader" );
		this.process = process;
		this.output = output;
		setDaemon( true );
	}

	public Process getProcess() {
		return process;
	}

	@Override
	public void run() {
		InputStream input = process.getInputStream();
		PrintStream printer = new PrintStream( output );

		int inputData = 0;

		try {
			while( ( inputData = input.read() ) > -1 ) {
				printer.print( (char)inputData );
			}
		} catch( IOException exception ) {
			exception.printStackTrace( printer );
		}
	}

}
