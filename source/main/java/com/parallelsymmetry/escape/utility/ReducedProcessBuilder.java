package com.parallelsymmetry.escape.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReducedProcessBuilder {

	private ProcessBuilder builder;

	public ReducedProcessBuilder() throws IOException {
		this( new ProcessBuilder() );
	}

	public ReducedProcessBuilder( ProcessBuilder builder ) throws IOException {
		if( OperatingSystem.isProcessElevated() ) {
			this.builder = new ProcessBuilder( getReduceCommands() );
			builder.command().addAll( this.builder.command() );
			builder.environment().putAll( this.builder.environment() );
			builder.environment().remove( ElevatedProcessBuilder.ELEVATED_PRIVILEGE_KEY );
		} else {
			this.builder = builder;
		}
	}

	public ProcessBuilder getBuilder() {
		return builder;
	}

	public List<String> command() {
		return builder.command();
	}

	public void command( String... command ) {
		builder.command( command );
	}

	public void command( List<String> command ) {
		builder.command( command );
	}

	public File directory() {
		return builder.directory();
	}

	public void directory( File directory ) {
		builder.directory( directory );
	}

	public Map<String, String> environment() {
		return builder.environment();
	}

	public boolean redirectErrorStream() {
		return builder.redirectErrorStream();
	}

	public void redirectErrorStream( boolean redirectErrorStream ) {
		builder.redirectErrorStream( redirectErrorStream );
	}

	public Process start() throws IOException {
		return builder.start();
	}

	private List<String> getReduceCommands() throws IOException {
		List<String> commands = new ArrayList<String>();

		if( OperatingSystem.isMac() ) {
			throw new UnsupportedOperationException();
		} else if( OperatingSystem.isUnix() ) {
			commands.add( "su" );
			commands.add( System.getenv( "SUDO_USER" ) );
		} else if( OperatingSystem.isWindows() ) {
			commands.add( "runas" );
			commands.add( "/trustlevel:0x20000" );
		}

		return commands;
	}

}
