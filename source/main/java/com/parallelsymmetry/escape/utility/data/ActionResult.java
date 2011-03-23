package com.parallelsymmetry.escape.utility.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ActionResult {

	private Action action;

	private List<DataEvent> events;

	public ActionResult( Action action ) {
		this.action = action;
		this.events = new CopyOnWriteArrayList<DataEvent>();
	}

	public Action getAction() {
		return action;
	}

	public List<DataEvent> getEvents() {
		return events;
	}

	public void addEvent( DataEvent event ) {
		events.add( event );
	}

}
