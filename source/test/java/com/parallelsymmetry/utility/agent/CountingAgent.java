package com.parallelsymmetry.utility.agent;

import com.parallelsymmetry.utility.ThreadUtil;
import com.parallelsymmetry.utility.agent.Agent;

public class CountingAgent extends Agent {

	private int startupPause;

	private int shutdownPause;

	private int startAgentCount;

	private int stopAgentCount;

	public CountingAgent( int startupPause, int shutdownPause ) {
		this.startupPause = startupPause;
		this.shutdownPause = shutdownPause;
	}

	@Override
	protected void startAgent() throws Exception {
		startAgentCount++;
		ThreadUtil.pause( startupPause );
	}

	@Override
	protected void stopAgent() throws Exception {
		stopAgentCount++;
		ThreadUtil.pause( shutdownPause );
	}

	public void resetCounts() {
		startAgentCount = 0;
		stopAgentCount = 0;
	}

	public int getStartupPause() {
		return startupPause;
	}

	public int getShutdownPause() {
		return shutdownPause;
	}

	public int getStartAgentCount() {
		return startAgentCount;
	}

	public int getStopAgentCount() {
		return stopAgentCount;
	}

}
