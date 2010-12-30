package com.parallelsymmetry.escape.utility.agent;


public class SlowAgentTest extends AgentTestCase {

	@Override
	public void setUp() {
		agent = new CountingAgent( 50, 50 );
		super.setUp();
	}

}
