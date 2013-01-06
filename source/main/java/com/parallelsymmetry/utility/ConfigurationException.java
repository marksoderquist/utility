package com.parallelsymmetry.utility;

public class ConfigurationException extends Exception {

	private static final long serialVersionUID = -3961658266673888581L;

	public ConfigurationException() {
		super();
	}

	public ConfigurationException( String message ) {
		super( message );
	}

	public ConfigurationException( Throwable cause ) {
		super( cause );
	}

	public ConfigurationException( String message, Throwable cause ) {
		super( message, cause );
	}

}
