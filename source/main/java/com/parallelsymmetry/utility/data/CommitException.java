package com.parallelsymmetry.utility.data;

public class CommitException extends Exception {

	private static final long serialVersionUID = 6380622228081816027L;

	public CommitException() {}

	public CommitException( String message ) {
		super( message );
	}

	public CommitException( Throwable cause ) {
		super( cause );
	}

	public CommitException( String message, Throwable cause ) {
		super( message, cause );
	}

}
