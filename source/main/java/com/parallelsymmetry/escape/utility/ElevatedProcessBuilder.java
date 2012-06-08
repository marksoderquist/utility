package com.parallelsymmetry.escape.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for executing commands with elevated privileges.
 * The way of achieving this greatly varies across operating systems. The Java
 * API does not help because there is no way provided to start a program with
 * elevated privileges.
 * 
 * @author Mark Soderquist
 */
public class ElevatedProcessBuilder {

	static final String ELEVATED_PRIVILEGE_KEY = "escape.process.privilege";

	static final String ELEVATED_PRIVILEGE_VALUE = "elevated";

	private ProcessBuilder builder;

	public ElevatedProcessBuilder() throws IOException {
		this( new ProcessBuilder(), false );
	}

	public ElevatedProcessBuilder( boolean veto ) throws IOException {
		this( new ProcessBuilder(), veto );
	}

	public ElevatedProcessBuilder( ProcessBuilder builder ) throws IOException {
		this( builder, false );
	}

	public ElevatedProcessBuilder( ProcessBuilder builder, boolean veto ) throws IOException {
		if( !veto && !OperatingSystem.isProcessElevated() ) {
			this.builder = new ProcessBuilder( getElevateCommands() );
			this.builder.command().addAll( builder.command() );
			this.builder.environment().putAll( builder.environment() );
			this.builder.environment().put( ELEVATED_PRIVILEGE_KEY, ELEVATED_PRIVILEGE_VALUE );
		} else {
			this.builder = builder;
		}
	}

	/**
	 * Check if the current operating system is supported.
	 * 
	 * @return true if the operating system is supported, false otherwise.
	 */
	public static final boolean isPlatformSupported() {
		return OperatingSystem.isMac() || OperatingSystem.isUnix() || OperatingSystem.isWindows();
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

	public static final boolean isElevatedFlagSet() {
		return ELEVATED_PRIVILEGE_VALUE.equals( System.getenv( ELEVATED_PRIVILEGE_KEY ) ) || ELEVATED_PRIVILEGE_VALUE.equals( System.getProperty( ELEVATED_PRIVILEGE_KEY ) );
	}

	private List<String> getElevateCommands() throws IOException {
		List<String> commands = new ArrayList<String>();

		if( OperatingSystem.isMac() ) {
			commands.add( extractMacElevate().getPath() );
		} else if( OperatingSystem.isUnix() ) {
			File gksudo = new File( "/usr/bin/gksudo" );
			File kdesudo = new File( "/usr/bin/kdesudo" );
			if( gksudo.exists() ) {
				commands.add( "/usr/bin/gksudo" );
			} else if( kdesudo.exists() ) {
				commands.add( "/usr/bin/kdesudo" );
			} else {
				commands.add( "xterm" );
				commands.add( "-title" );
				commands.add( "elevate" );
				commands.add( "-e" );
				commands.add( "sudo" );
			}
		} else if( OperatingSystem.isWindows() ) {
			commands.add( "wscript" );
			commands.add( extractWinElevate().getPath() );
		}

		return commands;
	}

	private File extractWinElevate() throws IOException {
		File elevator = new File( System.getProperty( "java.io.tmpdir" ), "elevate.js" ).getCanonicalFile();
		InputStream source = getClass().getResourceAsStream( "/elevate/win-elevate.js" );
		FileOutputStream target = new FileOutputStream( elevator );
		try {
			IoUtil.copy( source, target );
		} finally {
			source.close();
			target.close();
		}

		elevator.setExecutable( true );

		return elevator;
	}

	private File extractMacElevate() throws IOException {
		File elevator = new File( System.getProperty( "java.io.tmpdir" ), "elevate" ).getCanonicalFile();
		InputStream source = getClass().getResourceAsStream( "/elevate/mac-elevate" );
		FileOutputStream target = new FileOutputStream( elevator );
		try {
			IoUtil.copy( source, target );
		} finally {
			source.close();
			target.close();
		}

		elevator.setExecutable( true );

		return elevator;
	}

}
