package com.parallelsymmetry.utility.agent;

import com.parallelsymmetry.utility.BaseTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AgentTestCase extends BaseTestCase {

	protected CountingAgent agent;

	@Test
	public void testStartAndStop() throws Exception {
		//Log.write( "testStartAndStop()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );

		agent.start();
		agent.waitForStartup();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 0, agent.getStopAgentCount(), "Wrong stop call count." );

		agent.stop();
		agent.waitForShutdown();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 1, agent.getStopAgentCount(), "Wrong stop call count." );
	}

	@Test
	public void testDoubleStart() throws Exception {
		//Log.write( "testDoubleStart()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.start();
		agent.start();
		agent.waitForStartup();
		agent.start();
		agent.waitForStartup();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 0, agent.getStopAgentCount(), "Wrong stop call count." );
		agent.stop();
		agent.waitForShutdown();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 1, agent.getStopAgentCount(), "Wrong stop call count." );
	}

	@Test
	public void testStartAndWait() throws Exception {
		//Log.write( "testStartAndWait()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.startAndWait();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 0, agent.getStopAgentCount(), "Wrong stop call count." );
		agent.stopAndWait();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 1, agent.getStopAgentCount(), "Wrong stop call count." );
	}

	@Test
	public void testStop() throws Exception {
		//Log.write( "testStop()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.start();
		agent.waitForStartup();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 0, agent.getStopAgentCount(), "Wrong stop call count." );
		agent.stop();
		agent.waitForShutdown();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 1, agent.getStopAgentCount(), "Wrong stop call count." );
	}

	@Test
	public void testDoubleStop() throws Exception {
		//Log.write( "testDoubleStop()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.start();
		agent.waitForStartup();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 0, agent.getStopAgentCount(), "Wrong stop call count." );
		agent.stop();
		agent.stop();
		agent.waitForShutdown();
		agent.stop();
		agent.waitForShutdown();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 1, agent.getStopAgentCount(), "Wrong stop call count." );
	}

	@Test
	public void testStopAndWait() throws Exception {
		//Log.write( "testStopAndWait()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.startAndWait();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 0, agent.getStopAgentCount(), "Wrong stop call count." );
		agent.stopAndWait();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 1, agent.getStopAgentCount(), "Wrong stop call count." );
	}

	@Test
	public void testRestart() throws Exception {
		//Log.write( "testRestart()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.startAndWait();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 0, agent.getStopAgentCount(), "Wrong stop call count." );
		agent.restart();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( 2, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 1, agent.getStopAgentCount(), "Wrong stop call count." );
		agent.stopAndWait();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( 2, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 2, agent.getStopAgentCount(), "Wrong stop call count." );
	}

	@Test
	public void testFastRestarts() throws Exception {
		//Log.write( "testFastRestarts()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.startAndWait();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( 0, agent.getStopAgentCount(), "Wrong stop call count." );
		int count = 10;
		for( int index = 0; index < count; index++ ) {
			agent.restart();
		}
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( count + 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( count, agent.getStopAgentCount(), "Wrong stop call count." );
		agent.stopAndWait();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( count + 1, agent.getStartAgentCount(), "Wrong start call count." );
		assertEquals( count + 1, agent.getStopAgentCount(), "Wrong stop call count." );
	}

}
