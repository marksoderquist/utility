package com.parallelsymmetry.escape.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

	public boolean isProcessRunning() {
		try {
			process.exitValue();
			return false;
		} catch( IllegalThreadStateException exception ) {
			return true;
		}
	}

	public Process getProcess() {
		return process;
	}

	@Override
	public void run() {
		String line = null;
		String errLine = null;
		PrintStream printer = new PrintStream( output );
		BufferedReader reader = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
		BufferedReader errReader = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
		try {
			while( ( errLine = errReader.readLine() ) != null || ( line = reader.readLine() ) != null ) {
				if( errLine != null ) printer.println( errLine );
				if( line != null ) printer.println( line );
				printer.flush();
			}
		} catch( IOException exception ) {
			exception.printStackTrace( printer );
		}
		printer.flush();
	}
}
