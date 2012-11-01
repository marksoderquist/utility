package com.parallelsymmetry.escape.utility.agent;

public class SlowAgentTest extends AgentTestCase {

	@Override
	public void setUp() {
		agent = new CountingAgent( 1, 1 );
		super.setUp();
	}

}
