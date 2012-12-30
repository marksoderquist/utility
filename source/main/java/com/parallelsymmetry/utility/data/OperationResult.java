package com.parallelsymmetry.utility.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OperationResult {

	private Operation action;

	private List<DataEvent> events;

	public OperationResult( Operation action ) {
		this.action = action;
		this.events = new CopyOnWriteArrayList<DataEvent>();
	}

	public Operation getOperation() {
		return action;
	}

	public List<DataEvent> getEvents() {
		return events;
	}

	public void addEvent( DataEvent event ) {
		events.add( event );
	}

}
