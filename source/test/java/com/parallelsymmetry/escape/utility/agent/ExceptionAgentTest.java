package com.parallelsymmetry.escape.utility.agent;

import junit.framework.TestCase;

public class ExceptionAgentTest extends TestCase {

	public void testStartFailure() throws Exception {
		//Log.write( "testStartFailure()..." );
		ExceptionCountingAgent agent = new ExceptionCountingAgent( true, false );

		assertFalse( agent.isRunning() );

		agent.start();
		agent.waitForStartup();
		assertFalse( agent.isRunning() );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
	}

	public void testStopFailure() throws Exception {
		//Log.write( "testStopFailure()..." );
		ExceptionCountingAgent agent = new ExceptionCountingAgent( false, true );

		assertFalse( agent.isRunning() );

		agent.start();
		agent.waitForStartup();
		assertTrue( agent.isRunning() );
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );

		agent.stop();
		agent.waitForShutdown();
		assertTrue( agent.isRunning() );
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
	}

}
