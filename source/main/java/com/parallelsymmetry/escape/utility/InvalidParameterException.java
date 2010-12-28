package com.parallelsymmetry.escape.utility;

public class InvalidParameterException extends Exception {

	private static final long serialVersionUID = -6217795827785947498L;

	public InvalidParameterException() {}

	public InvalidParameterException( String message ) {
		super( message );
	}

	public InvalidParameterException( Throwable cause ) {
		super( cause );
	}

	public InvalidParameterException( String message, Throwable cause ) {
		super( message, cause );
	}

}
