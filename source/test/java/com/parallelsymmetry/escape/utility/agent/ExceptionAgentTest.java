package com.parallelsymmetry.escape.utility.agent;

public class ExceptionAgentTest extends AgentTestCase {

	@Override
	public void setUp() {
		agent = new ExceptionCountingAgent( 0, 0 );
		super.setUp();
	}

}
