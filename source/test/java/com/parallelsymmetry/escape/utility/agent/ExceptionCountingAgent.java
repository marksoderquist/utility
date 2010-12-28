package com.parallelsymmetry.escape.utility.agent;

public class ExceptionCountingAgent extends CountingAgent {

	public ExceptionCountingAgent( int startupPause, int shutdownPause ) {
		super( startupPause, shutdownPause );
	}

	@Override
	protected void startAgent() throws Exception {
		super.startAgent();
		throw new Exception( "Test exception during startAgent()." );
	}

	@Override
	protected void stopAgent() throws Exception {
		super.stopAgent();
		throw new Exception( "Test exception during stopAgent()." );
	}

}
