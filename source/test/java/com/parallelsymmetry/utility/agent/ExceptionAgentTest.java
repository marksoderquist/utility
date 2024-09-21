package com.parallelsymmetry.utility.agent;

import com.parallelsymmetry.utility.BaseTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionAgentTest extends BaseTestCase {

	@Test
	public void testStartFailure() throws Exception {
		//Log.write( "testStartFailure()..." );
		ExceptionCountingAgent agent = new ExceptionCountingAgent( true, false );

		assertFalse( agent.isRunning() );

		agent.start();
		agent.waitForStartup();
		assertFalse( agent.isRunning() );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 1, agent.getStopAgentCount(), "Wrong stop call count." );
	}

	@Test
	public void testStopFailure() throws Exception {
		//Log.write( "testStopFailure()..." );
		ExceptionCountingAgent agent = new ExceptionCountingAgent( false, true );

		assertFalse( agent.isRunning() );

		agent.start();
		agent.waitForStartup();
		assertTrue( agent.isRunning() );
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 0, agent.getStopAgentCount(), "Wrong stop call count." );

		agent.stop();
		agent.waitForShutdown();
		assertTrue( agent.isRunning() );
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 1, agent.getStopAgentCount(), "Wrong stop call count." );
	}

}
