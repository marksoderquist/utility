package com.parallelsymmetry.escape.utility.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class Transaction {

	private List<Action> actions;

	public Transaction() {
		actions = new CopyOnWriteArrayList<Action>();
	}

	public void add( Action action ) {
		actions.add( action );
	}
	
	public void commit() {
		// Create a set of the data objects involved.
		Set<DataObject> data = new HashSet<DataObject>();
		for( Action action : actions ) {
			data.add( action.getData() );
		}

		// Store the current modified state of each data object.
		Map<DataObject, Boolean> modified = new HashMap<DataObject, Boolean>();
		for( DataObject datum : data ) {
			modified.put( datum, datum.isModified() );
		}

		// Process the actions.
		List<ActionResult> results = new ArrayList<ActionResult>();
		for( Action action : actions ) {
			results.add( action.process() );
		}

		// Collect events from the action results.
		Map<DataObject, List<DataEvent>> events = new HashMap<DataObject, List<DataEvent>>();
		for( ActionResult result : results ) {
			DataObject datum = result.getAction().getData();

			List<DataEvent> datumEvents = events.get( datum );
			if( datumEvents == null ) {
				datumEvents = new ArrayList<DataEvent>();
				events.put( datum, datumEvents );
			}

			datumEvents.addAll( result.getEvents() );
		}

		// Send the events for each data object.
		for( DataObject datum : data ) {
			boolean changed = false;

			for( DataEvent event : events.get( datum ) ) {
				datum.dispatchDataEvent( event );
				changed = true;
			}

			boolean oldModified = modified.get( datum );
			boolean newModified = datum.isModified();
			if( newModified != oldModified ) {
				// A meta attribute change event needs to be sent.
				datum.dispatchDataEvent( new MetaAttributeEvent( DataEvent.Type.MODIFY, datum, DataObject.MODIFIED, newModified, oldModified ) );
				changed = true;
			}

			if( changed ) datum.dispatchDataEvent( new DataEvent( DataEvent.Type.MODIFY, datum ) );
		}
	}

}
