package com.parallelsymmetry.utility.agent;

public class AgentEvent {

	private Agent agent;

	private Agent.State state;

	public AgentEvent( Agent service, Agent.State type ) {
		this.agent = service;
		this.state = type;
	}

	public Agent getAgent() {
		return agent;
	}

	public Agent.State getState() {
		return state;
	}

}
