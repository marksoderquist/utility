package com.parallelsymmetry.utility.agent;

import org.junit.jupiter.api.BeforeEach;

public class SlowAgentTest extends AgentTestCase {

	@BeforeEach
	@Override
	public void setup() throws Exception {
		agent = new CountingAgent( 1, 1 );
		super.setup();
	}

}
