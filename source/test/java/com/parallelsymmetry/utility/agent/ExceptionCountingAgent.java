package com.parallelsymmetry.utility.agent;

public class ExceptionCountingAgent extends CountingAgent {

	private final boolean failStart;

	private final boolean failStop;

	public ExceptionCountingAgent( boolean failStart, boolean failStop ) {
		super( 0, 0 );
		this.failStart = failStart;
		this.failStop = failStop;
	}

	@Override
	protected void startAgent() throws Exception {
		super.startAgent();
		if( failStart ) throw new RuntimeException( "Test exception during startAgent()." );
	}

	@Override
	protected void stopAgent() throws Exception {
		super.stopAgent();
		if( failStop ) throw new RuntimeException( "Test exception during stopAgent()." );
	}

}
