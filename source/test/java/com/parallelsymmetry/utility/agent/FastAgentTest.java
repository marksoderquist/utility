package com.parallelsymmetry.utility.agent;

import org.junit.jupiter.api.BeforeEach;

public class FastAgentTest extends AgentTestCase {

	@BeforeEach
	@Override
	public void setup() throws Exception {
		agent = new CountingAgent( 0, 0 );
		super.setup();
	}

}
