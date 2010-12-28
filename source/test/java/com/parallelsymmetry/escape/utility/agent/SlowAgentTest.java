package com.parallelsymmetry.escape.utility.agent;

public class SlowAgentTest extends AgentTestCase {

	@Override
	public void setUp() {
		agent = new CountingAgent( 10, 10 );
		super.setUp();
	}

}
