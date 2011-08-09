package com.parallelsymmetry.escape.utility.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.parallelsymmetry.escape.utility.log.Log;

public class Transaction {

	private static final String PREVIOUS_MODIFIED_STATE = Transaction.class.getName() + ".previousModifiedState";

	private static final String EVENT_LIST = Transaction.class.getName() + ".eventList";

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
			for( DataNode node : nodes ) {
				node.putResource( PREVIOUS_MODIFIED_STATE, node.isModified() );
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
			for( ActionResult result : results ) {
				DataNode node = result.getAction().getData();

				List<DataEvent> datumEvents = node.getResource( EVENT_LIST );
				if( datumEvents == null ) {
					datumEvents = new ArrayList<DataEvent>();
					node.putResource( EVENT_LIST, datumEvents );
				}

				datumEvents.addAll( result.getEvents() );
			}

			// Send the events for each data object.
			for( DataNode node : nodes ) {
				boolean changed = false;

				List<DataEvent> events = node.getResource( EVENT_LIST );
				if( events != null ) {
					node.putResource( EVENT_LIST, null );
					for( DataEvent event : events ) {
						node.dispatchEvent( event );
						changed = true;
					}
				}

				boolean oldModified = (Boolean)node.getResource( PREVIOUS_MODIFIED_STATE );
				boolean newModified = node.isModified();
				node.putResource( PREVIOUS_MODIFIED_STATE, null );

				if( newModified != oldModified ) {
					// Notify the parent.
					DataNode parent = node.getParent();
					while( parent != null ) {
						boolean parentOldModified = parent.isModified();
						if( parent instanceof DataList ) {
							( (DataList<?>)parent ).childNodeModified( newModified );
						} else {
							parent.attributeNodeModified( newModified );
						}
						boolean parentNewModified = parent.isModified();

						// Dispatch events for parent.
						if( parentNewModified != parentOldModified ) parent.dispatchEvent( new MetaAttributeEvent( DataEvent.Type.MODIFY, node, DataNode.MODIFIED, oldModified, newModified ) );

						parent = parent.getParent();
					}

					// A meta attribute change event needs to be sent.
					node.dispatchEvent( new MetaAttributeEvent( DataEvent.Type.MODIFY, node, DataNode.MODIFIED, oldModified, newModified ) );
					changed = true;
				}

				if( changed ) node.dispatchEvent( new DataChangedEvent( DataEvent.Type.MODIFY, node ) );
			}
		} finally {
			cleanup();
			Log.write( Log.DEBUG, "Transaction[" + System.identityHashCode( this ) + "] committed!" );
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
