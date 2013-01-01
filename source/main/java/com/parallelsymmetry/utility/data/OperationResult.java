package com.parallelsymmetry.utility.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OperationResult {

	private Operation action;

	private List<DataValueEvent> events;

	public OperationResult( Operation action ) {
		this.action = action;
		this.events = new CopyOnWriteArrayList<DataValueEvent>();
	}

	public Operation getOperation() {
		return action;
	}

	public List<DataValueEvent> getEvents() {
		return events;
	}

	public void addEvent( DataValueEvent event ) {
		events.add( event );
	}

}
