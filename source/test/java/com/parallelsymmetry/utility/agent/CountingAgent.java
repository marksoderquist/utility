package com.parallelsymmetry.utility.agent;

import com.parallelsymmetry.utility.ThreadUtil;
import lombok.Getter;

@Getter
public class CountingAgent extends Agent {

	private final int startupPause;

	private final int shutdownPause;

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

}
