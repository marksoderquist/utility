package com.parallelsymmetry.escape.utility.agent;

public class FastAgentTest extends AgentTestCase {

	@Override
	public void setUp() {
		agent = new CountingAgent( 0, 0 );
		super.setUp();
	}

}
