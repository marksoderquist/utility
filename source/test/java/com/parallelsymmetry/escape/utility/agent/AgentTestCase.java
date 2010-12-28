package com.parallelsymmetry.escape.utility.agent;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.log.Log;

public abstract class AgentTestCase extends TestCase {

	protected CountingAgent agent;

	@Override
	public void setUp() {
		Log.setLevel( Log.NONE );
	}

	@Override
	public void tearDown() {
		Log.setLevel( null );
	}

	public void testStartAndStop() throws Exception {
		//Log.write( "testStartAndStop()..." );
		assertFalse( agent.isRunning() );

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
		assertFalse( agent.isRunning() );
		agent.start();
		agent.start();
		agent.waitForStartup();
		agent.start();
		agent.waitForStartup();
		assertTrue( agent.isRunning() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		agent.stop();
		agent.waitForShutdown();
		assertFalse( agent.isRunning() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
	}

	public void testStartAndWait() throws Exception {
		//Log.write( "testStartAndWait()..." );
		assertFalse( agent.isRunning() );
		agent.startAndWait();
		assertTrue( agent.isRunning() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		agent.stopAndWait();
		assertFalse( agent.isRunning() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
	}

	public void testStop() throws Exception {
		//Log.write( "testStop()..." );
		assertFalse( agent.isRunning() );
		agent.start();
		agent.waitForStartup();
		assertTrue( agent.isRunning() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		agent.stop();
		agent.waitForShutdown();
		assertFalse( agent.isRunning() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
	}

	public void testDoubleStop() throws Exception {
		//Log.write( "testDoubleStop()..." );
		assertFalse( agent.isRunning() );
		agent.start();
		agent.waitForStartup();
		assertTrue( agent.isRunning() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		agent.stop();
		agent.stop();
		agent.waitForShutdown();
		agent.stop();
		agent.waitForShutdown();
		assertFalse( agent.isRunning() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
	}

	public void testStopAndWait() throws Exception {
		//Log.write( "testStopAndWait()..." );
		assertFalse( agent.isRunning() );
		agent.startAndWait();
		assertTrue( agent.isRunning() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		agent.stopAndWait();
		assertFalse( agent.isRunning() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
	}

	public void testRestart() throws Exception {
		//Log.write( "testRestart()..." );
		assertFalse( agent.isRunning() );
		agent.startAndWait();
		assertTrue( agent.isRunning() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		agent.restart();
		assertTrue( agent.isRunning() );
		assertEquals( "Wrong start call count.", 2, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 1, agent.getStopAgentCount() );
		agent.stopAndWait();
		assertFalse( agent.isRunning() );
		assertEquals( "Wrong start call count.", 2, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 2, agent.getStopAgentCount() );
	}

	public void testFastRestarts() throws Exception {
		//Log.write( "testFastRestarts()..." );
		assertFalse( agent.isRunning() );
		agent.startAndWait();
		assertTrue( agent.isRunning() );
		assertEquals( "Wrong start call count.", 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", 0, agent.getStopAgentCount() );
		int count = 10;
		for( int index = 0; index < count; index++ ) {
			agent.restart();
		}
		assertTrue( agent.isRunning() );
		assertEquals( "Wrong start call count.", count + 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", count, agent.getStopAgentCount() );
		agent.stopAndWait();
		assertFalse( agent.isRunning() );
		assertEquals( "Wrong start call count.", count + 1, agent.getStartAgentCount() );
		assertEquals( "Wrong stop call count.", count + 1, agent.getStopAgentCount() );
	}

}
