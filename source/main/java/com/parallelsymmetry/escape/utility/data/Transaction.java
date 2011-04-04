package com.parallelsymmetry.escape.utility.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.parallelsymmetry.escape.utility.log.Log;

public class Transaction {

	private Queue<Action> actions;

	private List<DataNode> nodes;

	private boolean commitInProgress;

	private AtomicInteger depth = new AtomicInteger();

	public Transaction() {
		actions = new ConcurrentLinkedQueue<Action>();
		nodes = new CopyOnWriteArrayList<DataNode>();
	}

	public void add( Action action ) {
		if( commitInProgress ) throw new RuntimeException( "Data should not be modified from data listeners." );

		Log.write( Log.DEBUG, "Transaction: " + toString() + " adding action: " + action );

		DataNode data = action.getData();

		actions.offer( action );

		if( !this.nodes.contains( data ) ) this.nodes.add( data );
	}

	public void commit() {
		int depth = decrementDepth();
		if( depth > 0 ) return;

		commitInProgress = true;
		Log.write( Log.DEBUG, "Committing transaction[" + System.identityHashCode( this ) + "]..." );
		try {
			// Store the current modified state of each data object.
			Map<DataNode, Boolean> modified = new HashMap<DataNode, Boolean>();
			for( DataNode datum : nodes ) {
				modified.put( datum, datum.isModified() );
			}

			// Process the actions.
			Action action = null;
			List<ActionResult> results = new ArrayList<ActionResult>();
			while( !actions.isEmpty() ) {
				action = actions.poll();
				Log.write( Log.DEBUG, "Transaction: " + toString() + " processing action: " + action );
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
						if( parent instanceof DataList ) {
							( (DataList<?>)parent ).childNodeModified( newModified );
						} else {
							parent.attributeNodeModified( newModified );
						}
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
			Log.write( Log.TRACE, "Transaction[" + System.identityHashCode( this ) + "] committed!" );
			commitInProgress = false;
		}
	}

	public void cancel() {
		cleanup();
	}

	public int getDepth() {
		return depth.get();
	}

	public int incrementDepth() {
		return depth.incrementAndGet();
	}

	public int decrementDepth() {
		return depth.decrementAndGet();
	}

	public String toString() {
		return String.valueOf( "transaction[" + System.identityHashCode( this ) + "]" );
	}

	private void cleanup() {
		for( DataNode node : nodes ) {
			node.setTransaction( null );
		}
	}

}
