package com.parallelsymmetry.escape.utility;

public class ControllableException extends Exception {

	private static final long serialVersionUID = -1515649245482039582L;

	public ControllableException() {}

	public ControllableException( String message ) {
		super( message );
	}

	public ControllableException( Throwable cause ) {
		super( cause );
	}

	public ControllableException( String message, Throwable cause ) {
		super( message, cause );
	}

	public ControllableException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace ) {
		super( message, cause, enableSuppression, writableStackTrace );
	}

}
