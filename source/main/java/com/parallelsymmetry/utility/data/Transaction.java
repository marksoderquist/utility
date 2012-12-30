package com.parallelsymmetry.utility.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import com.parallelsymmetry.utility.log.Log;

public class Transaction {

	private static final String PREVIOUS_MODIFIED_STATE = Transaction.class.getName() + ".previousModifiedState";

	private static final String EVENT_LIST = Transaction.class.getName() + ".eventList";

	private static final Object COMMIT_LOCK = new Object();

	private Set<DataNode> nodes;

	private Queue<Operation> operations;

	private boolean commitInProgress;

	/**
	 * @deprecated Once the bulk of the transaction updates have been completed, this can be removed.
	 */
	@Deprecated
	private AtomicInteger depth = new AtomicInteger();

	private Map<DataNode, FinalEvents> finalEvents = new ConcurrentHashMap<DataNode, FinalEvents>();

	public Transaction() {
		nodes = new CopyOnWriteArraySet<DataNode>();
		operations = new ConcurrentLinkedQueue<Operation>();
	}

	public void setAttribute( DataNode node, String name, Object newValue ) {
		add( new SetAttributeOperation( node, name, node.getAttribute( name ), newValue ) );
	}

	public <T extends DataNode> void insertChild( DataList<T> list, int index, T child ) {
		add( new InsertChildOperation<T>( list, index, child ) );
	}

	public <T extends DataNode> void removeChild( DataList<T> list, T child ) {
		removeChild( list, list.indexOf( child ) );
	}

	public <T extends DataNode> void removeChild( DataList<T> list, int index ) {
		add( new RemoveChildOperation<T>( list, index ) );
	}

	public void commit() {
		synchronized( COMMIT_LOCK ) {
			int depth = decrementDepth();
			if( depth > 0 ) return;

			commitInProgress = true;
			Log.write( Log.DETAIL, "Committing transaction[" + System.identityHashCode( this ) + "]..." );
			try {
				// Store the current modified state of each data object.
				for( DataNode node : nodes ) {
					node.putResource( PREVIOUS_MODIFIED_STATE, node.isModified() );
				}

				// Process the operations.
				List<OperationResult> results = new ArrayList<OperationResult>();
				for( Operation operation : operations ) {
					Log.write( Log.DETAIL, "Transaction: " + toString() + " processing operation: " + operation );
					results.add( operation.process() );
				}

				// Go through each operation result and collect the events for each node.
				for( OperationResult result : results ) {
					DataNode node = result.getOperation().getData();

					List<DataEvent> resultEvents = result.getEvents();
					List<DataEvent> nodeEvents = node.getResource( EVENT_LIST );
					if( nodeEvents == null ) {
						nodeEvents = new ArrayList<DataEvent>( resultEvents.size() );
						node.putResource( EVENT_LIST, nodeEvents );
					}

					nodeEvents.addAll( resultEvents );
				}

				// Send the events for each data node.
				for( DataNode node : nodes ) {
					List<DataEvent> events = node.getResource( EVENT_LIST );
					if( events != null ) {
						node.putResource( EVENT_LIST, null );
						for( DataEvent event : events ) {
							dispatchEvent( event );
						}
					}

					boolean oldModified = (Boolean)node.getResource( PREVIOUS_MODIFIED_STATE );
					boolean newModified = node.isModified();
					node.putResource( PREVIOUS_MODIFIED_STATE, null );

					collectFinalEvents( node, node, oldModified, newModified );
				}

				fireFinalEvents();
			} finally {
				cleanup();
				Log.write( Log.DETAIL, "Transaction[" + System.identityHashCode( this ) + "] committed!" );
				commitInProgress = false;
			}
		}
	}

	public void cancel() {
		cleanup();
	}

	@Deprecated
	public int getDepth() {
		return depth.get();
	}

	@Deprecated
	public int incrementDepth() {
		return depth.incrementAndGet();
	}

	@Deprecated
	public int decrementDepth() {
		return depth.decrementAndGet();
	}

	@Override
	public String toString() {
		return String.valueOf( "transaction[" + System.identityHashCode( this ) + "]" );
	}

	// NEXT Make this method private and fix all references.
	void add( Operation operation ) {
		if( commitInProgress ) throw new RuntimeException( "Data should not be modified from data listeners." );

		Log.write( Log.DETAIL, "Transaction: " + toString() + " adding operation: " + operation );

		operations.offer( operation );
		nodes.add( operation.getData() );
	}

	private void dispatchEvent( DataEvent event ) {
		DataNode sender = event.getData();

		sender.dispatchEvent( event );

		for( DataNode parent : sender.getParents() ) {
			dispatchEvent( event.cloneWithNewSender( parent ) );
		}
	}

	private FinalEvents getFinalEvents( DataNode node ) {
		FinalEvents events = finalEvents.get( node );
		if( events == null ) {
			events = new FinalEvents();
			finalEvents.put( node, events );
		}
		return events;
	}

	private void collectFinalEvents( DataNode sender, DataNode cause, boolean oldModified, boolean newModified ) {
		boolean modifiedChanged = oldModified != newModified;

		// Post the modified event.
		if( modifiedChanged ) postModifiedEvent( new MetaAttributeEvent( DataEvent.Action.MODIFY, sender, DataNode.MODIFIED, oldModified, newModified ) );

		// Post the changed event.
		postChangedEvent( new DataChangedEvent( DataEvent.Action.MODIFY, sender ) );

		for( DataNode parent : sender.getParents() ) {
			boolean parentOldModified = parent.isModified();
			if( modifiedChanged ) {
				if( parent instanceof DataList ) {
					( (DataList<?>)parent ).childNodeModified( newModified );
				} else {
					parent.attributeNodeModified( newModified );
				}
			}
			boolean parentNewModified = parent.isModified();

			collectFinalEvents( parent, cause, parentOldModified, parentNewModified );
		}
	}

	private void postModifiedEvent( MetaAttributeEvent event ) {
		getFinalEvents( event.getData() ).modified = event;
	}

	private void postChangedEvent( DataChangedEvent event ) {
		getFinalEvents( event.getData() ).changed = event;
	}

	private void fireFinalEvents() {
		// Fire the modified events first.
		for( DataNode node : finalEvents.keySet() ) {
			FinalEvents events = finalEvents.get( node );
			if( events.modified != null ) node.dispatchEvent( events.modified );

			//			MetaAttributeEvent event = events.modified;
			//			if( event == null ) {
			//				Log.write( "Event: ", node, "  null" );
			//			} else {
			//				Log.write( "Event: ", event.getData(), "  cause: " + event.getCause(), "  old: ", event.getOldValue(), "  new: ", event.getNewValue() );
			//			}
		}

		// Fire the data changed events last.
		for( DataNode node : finalEvents.keySet() ) {
			FinalEvents events = finalEvents.get( node );
			if( events.changed != null ) node.dispatchEvent( events.changed );
		}
	}

	private void cleanup() {
		for( DataNode node : nodes ) {
			node.setTransaction( null );
		}
		operations.clear();
		nodes.clear();
	}

	private static class SetAttributeOperation extends Operation {

		private String name;

		private Object oldValue;

		private Object newValue;

		public SetAttributeOperation( DataNode data, String name, Object oldValue, Object newValue ) {
			super( data );
			this.name = name;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		@Override
		protected OperationResult process() {
			OperationResult result = new OperationResult( this );

			getData().doSetAttribute( name, oldValue, newValue );

			DataEvent.Action type = DataEvent.Action.MODIFY;
			type = oldValue == null ? DataEvent.Action.INSERT : type;
			type = newValue == null ? DataEvent.Action.REMOVE : type;
			result.addEvent( new DataAttributeEvent( type, data, data, name, oldValue, newValue ) );

			return result;
		}

	}

	private static class InsertChildOperation<T extends DataNode> extends Operation {

		private DataList<T> list;

		private int index;

		private T child;

		public InsertChildOperation( DataList<T> list, int index, T child ) {
			super( list );
			this.list = list;
			this.index = index;
			this.child = child;
		}

		@Override
		protected OperationResult process() {
			OperationResult result = new OperationResult( this );

			list.doAddChild( index, child );
			result.addEvent( new DataChildEvent( DataEvent.Action.INSERT, list, list, index, child ) );

			return result;
		}

	}

	private static class RemoveChildOperation<T extends DataNode> extends Operation {

		private DataList<T> list;

		private int index;

		public RemoveChildOperation( DataList<T> list, int index ) {
			super( list );
			this.list = list;
			this.index = index;
		}

		@Override
		protected OperationResult process() {
			OperationResult result = new OperationResult( this );

			T child = list.doRemoveChild( index );
			result.addEvent( new DataChildEvent( DataEvent.Action.REMOVE, list, list, index, child ) );

			return result;
		}

	}

	private class FinalEvents {

		public DataChangedEvent changed;

		public MetaAttributeEvent modified;

	}

}
