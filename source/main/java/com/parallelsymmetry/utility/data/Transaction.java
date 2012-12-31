package com.parallelsymmetry.utility.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

import com.parallelsymmetry.utility.log.Log;

public class Transaction {

	private static final String PREVIOUS_MODIFIED_STATE = Transaction.class.getName() + ".previousModifiedState";

	private static final String EVENT_LIST = Transaction.class.getName() + ".eventList";

	private static final Object COMMIT_LOCK = new Object();

	private static boolean commitInProgress;

	private Set<DataNode> nodes;

	private Queue<Operation> operations;

	private Map<DataNode, FinalEvents> finalEvents;

	private boolean committed;

	public Transaction() {
		nodes = new CopyOnWriteArraySet<DataNode>();
		operations = new ConcurrentLinkedQueue<Operation>();
		finalEvents = new ConcurrentHashMap<DataNode, FinalEvents>();
	}

	public void setAttribute( DataNode node, String name, Object newValue ) {
		add( new SetAttributeOperation( node, name, node.getAttribute( name ), newValue ) );
	}

	public <T extends DataNode> boolean add( DataList<T> list, T child ) {
		return add( list, Integer.MAX_VALUE, child );
	}

	public <T extends DataNode> boolean add( DataList<T> list, int index, T child ) {
		if( child == null ) return false;

		add( new InsertChildOperation<T>( list, index, child ) );

		return true;
	}

	public <T extends DataNode> boolean addAll( DataList<T> list, Collection<? extends T> collection ) {
		return addAll( list, Integer.MAX_VALUE, collection );
	}

	public <T extends DataNode> boolean addAll( DataList<T> list, int index, Collection<? extends T> collection ) {
		if( collection == null ) return false;

		// Figure out if nodes need to be added.
		List<T> children = new ArrayList<T>();
		for( T node : collection ) {
			if( !list.contains( node ) ) children.add( node );
		}
		if( children.size() == 0 ) return false;

		// Add the nodes.
		for( T node : children ) {
			add( list, index, node );
			if( index < Integer.MAX_VALUE ) index++;
		}

		return true;
	}

	public <T extends DataNode> boolean remove( DataList<T> list, T child ) {
		if( child == null || !( child instanceof DataNode ) || !list.contains( child ) ) return false;
		return remove( list, list.indexOf( child ) );
	}

	public <T extends DataNode> boolean remove( DataList<T> list, int index ) {
		if( index < 0 || index >= list.size() ) throw new ArrayIndexOutOfBoundsException( index );
		add( new RemoveChildOperation<T>( list, list.get( index ) ) );
		return true;
	}

	@SuppressWarnings( "unchecked" )
	public <T extends DataNode> boolean removeAll( DataList<T> list, Collection<?> collection ) {
		if( collection == null ) return false;

		int count = 0;
		for( Object node : collection ) {
			if( !( node instanceof DataNode ) ) continue;
			if( remove( list, (T)node ) ) count++;
		}

		return count > 0;
	}

	public void commit() {
		synchronized( COMMIT_LOCK ) {
			if( committed ) throw new RuntimeException( "Transaction can only be committed once." );
			committed = true;

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
				Log.write( Log.DETAIL, "Transaction[" + System.identityHashCode( this ) + "] committed!" );
				commitInProgress = false;
			}
		}
	}

	@Override
	public String toString() {
		return String.valueOf( "transaction[" + System.identityHashCode( this ) + "]" );
	}

	void modify( DataNode node ) {
		add( new ModifyAction( node ) );
	}

	void unmodify( DataNode node ) {
		add( new UnmodifyAction( node ) );
	}

	private void add( Operation operation ) {
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
		}

		// Fire the data changed events last.
		for( DataNode node : finalEvents.keySet() ) {
			FinalEvents events = finalEvents.get( node );
			if( events.changed != null ) node.dispatchEvent( events.changed );
		}
	}

	public static class ModifyAction extends Operation {

		public ModifyAction( DataNode data ) {
			super( data );
		}

		@Override
		protected OperationResult process() {
			OperationResult result = new OperationResult( this );

			getData().doModify();

			return result;
		}

	}

	private static class UnmodifyAction extends Operation {

		public UnmodifyAction( DataNode data ) {
			super( data );
		}

		@Override
		protected OperationResult process() {
			OperationResult result = new OperationResult( this );

			getData().doUnmodify();

			return result;
		}
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

		private T child;

		public RemoveChildOperation( DataList<T> list, T child ) {
			super( list );
			this.list = list;
			this.child = child;
		}

		@Override
		protected OperationResult process() {
			OperationResult result = new OperationResult( this );

			int index = list.indexOf( child );
			list.doRemoveChild( child );
			result.addEvent( new DataChildEvent( DataEvent.Action.REMOVE, list, list, index, child ) );

			return result;
		}

	}

	private class FinalEvents {

		public DataChangedEvent changed;

		public MetaAttributeEvent modified;

	}

}
