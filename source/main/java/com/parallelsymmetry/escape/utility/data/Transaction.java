package com.parallelsymmetry.escape.utility.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Transaction {

	private List<Action> actions;

	private List<DataNode> nodes;

	public Transaction() {
		actions = new CopyOnWriteArrayList<Action>();
		nodes = new CopyOnWriteArrayList<DataNode>();
	}

	public void add( Action action ) {
		DataNode data = action.getData();

		actions.add( action );

		if( !this.nodes.contains( data ) ) this.nodes.add( data );
	}

	public void commit() {
		try {
			// Store the current modified state of each data object.
			Map<DataNode, Boolean> modified = new HashMap<DataNode, Boolean>();
			for( DataNode datum : nodes ) {
				modified.put( datum, datum.isModified() );
			}

			// Process the actions.
			List<ActionResult> results = new ArrayList<ActionResult>();
			for( Action action : actions ) {
				results.add( action.process() );
			}

			// Collect events from the action results.
			Map<DataNode, List<DataEvent>> events = new HashMap<DataNode, List<DataEvent>>();
			for( ActionResult result : results ) {
				DataNode datum = result.getAction().getData();

				List<DataEvent> datumEvents = events.get( datum );
				if( datumEvents == null ) {
					datumEvents = new ArrayList<DataEvent>();
					events.put( datum, datumEvents );
				}

				datumEvents.addAll( result.getEvents() );
			}

			// Send the events for each data object.
			for( DataNode datum : nodes ) {
				boolean changed = false;

				for( DataEvent event : events.get( datum ) ) {
					datum.dispatchEvent( event );
					changed = true;
				}

				boolean oldModified = modified.get( datum );
				boolean newModified = datum.isModified();
				if( newModified != oldModified ) {
					// Notify the parent.
					DataNode parent = datum.getParent();
					while( parent != null ) {
						boolean parentOldModified = parent.isModified();
						parent.attributeModified( newModified );
						boolean parentNewModified = parent.isModified();

						// Dispatch events for parent.
						if( parentNewModified != parentOldModified ) {
							parent.dispatchEvent( new MetaAttributeEvent( DataEvent.Type.MODIFY, datum, DataNode.MODIFIED, oldModified, newModified ) );
							parent.dispatchEvent( new DataEvent( DataEvent.Type.MODIFY, datum ) );
						}

						parent = parent.getParent();
					}

					// A meta attribute change event needs to be sent.
					datum.dispatchEvent( new MetaAttributeEvent( DataEvent.Type.MODIFY, datum, DataNode.MODIFIED, oldModified, newModified ) );
					changed = true;
				}

				if( changed ) datum.dispatchEvent( new DataEvent( DataEvent.Type.MODIFY, datum ) );
			}
		} finally {
			cleanup();
		}
	}

	public void cancel() {
		cleanup();
	}

	private void cleanup() {
		for( DataNode node : nodes ) {
			node.setTransaction( null );
		}
	}

}
