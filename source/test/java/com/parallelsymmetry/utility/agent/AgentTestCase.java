package com.parallelsymmetry.utility.agent;

import junit.framework.TestCase;

import com.parallelsymmetry.utility.agent.Agent;
import com.parallelsymmetry.utility.log.Log;

public abstract class AgentTestCase extends TestCase {

	protected CountingAgent agent;

	@Override
	public void setUp() {
		Log.setLevel( Log.NONE );
	}

	@Override
	public void tearDown() {
		Log.setLevel( Log.NONE );
	}

	public void testStartAndStop() throws Exception {
		//Log.write( "testStartAndStop()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );

		agent.start();
		agent.waitForStartup();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );

		agent.stop();
		agent.waitForShutdown();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
	}

	public void testDoubleStart() throws Exception {
		//Log.write( "testDoubleStart()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.start();
		agent.start();
		agent.waitForStartup();
		agent.start();
		agent.waitForStartup();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		agent.stop();
		agent.waitForShutdown();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
	}

	public void testStartAndWait() throws Exception {
		//Log.write( "testStartAndWait()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.startAndWait();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		agent.stopAndWait();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
	}

	public void testStop() throws Exception {
		//Log.write( "testStop()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.start();
		agent.waitForStartup();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		agent.stop();
		agent.waitForShutdown();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
	}

	public void testDoubleStop() throws Exception {
		//Log.write( "testDoubleStop()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.start();
		agent.waitForStartup();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		agent.stop();
		agent.stop();
		agent.waitForShutdown();
		agent.stop();
		agent.waitForShutdown();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
	}

	public void testStopAndWait() throws Exception {
		//Log.write( "testStopAndWait()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.startAndWait();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		agent.stopAndWait();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
	}

	public void testRestart() throws Exception {
		//Log.write( "testRestart()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.startAndWait();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		agent.restart();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( "Wrong start call count.", 2, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
		agent.stopAndWait();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( "Wrong start call count.", 2, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 2, agent.getStopAgentCount() );
	}

	public void testFastRestarts() throws Exception {
		//Log.write( "testFastRestarts()..." );
		assertEquals( Agent.State.STOPPED, agent.getState() );
		agent.startAndWait();
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		int count = 10;
		for( int index = 0; index < count; index++ ) {
			agent.restart();
		}
		assertEquals( Agent.State.STARTED, agent.getState() );
		assertEquals( "Wrong start call count.", count + 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", count, agent.getStopAgentCount() );
		agent.stopAndWait();
		assertEquals( Agent.State.STOPPED, agent.getState() );
		assertEquals( "Wrong start call count.", count + 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", count + 1, agent.getStopAgentCount() );
	}

}
