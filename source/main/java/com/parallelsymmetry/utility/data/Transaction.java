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
import java.util.concurrent.locks.ReentrantLock;

import com.parallelsymmetry.utility.log.Log;

public class Transaction {

	private static final String PREVIOUS_MODIFIED_STATE = Transaction.class.getName() + ".previousModifiedState";

	private static final ReentrantLock COMMIT_LOCK = new ReentrantLock();

	private static Transaction activeTransaction;

	private Map<DataNode, TransactionEvents> finalEvents;

	private Queue<Operation> operations;

	private Set<DataNode> nodes;

	public Transaction() {
		nodes = new CopyOnWriteArraySet<DataNode>();
		operations = new ConcurrentLinkedQueue<Operation>();
		finalEvents = new ConcurrentHashMap<DataNode, TransactionEvents>();
	}

	public void setAttribute( DataNode node, String name, Object newValue ) {
		addOperation( new SetAttributeOperation( node, name, node.getAttribute( name ), newValue ) );
	}

	public <T extends DataNode> boolean add( DataList<T> list, T child ) {
		return add( list, Integer.MAX_VALUE, child );
	}

	public <T extends DataNode> boolean add( DataList<T> list, int index, T child ) {
		if( child == null ) return false;

		addOperation( new InsertChildOperation<T>( list, index, child ) );

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
		addOperation( new RemoveChildOperation<T>( list, list.get( index ) ) );
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
		try {
			Log.write( Log.DETAIL, "Committing transaction[" + System.identityHashCode( this ) + "]..." );
			COMMIT_LOCK.lock();

			activeTransaction = this;

			// Store the current modified state of each data object.
			for( DataNode node : nodes ) {
				node.putResource( PREVIOUS_MODIFIED_STATE, node.isModified() );
			}

			// Process the operations.
			List<OperationResult> results = new ArrayList<OperationResult>();
			for( Operation operation : operations ) {
				Log.write( Log.DETAIL, "Transaction[" + System.identityHashCode( this ) + "] processing operation: " + operation );
				results.add( operation.process() );
			}

			// Go through each operation result and collect the events for each node.
			for( OperationResult result : results ) {
				DataNode node = result.getOperation().getData();
				getTransactionEvents( node ).events.addAll( result.getEvents() );
			}

			// Send the events for each data node.
			for( DataNode node : nodes ) {
				boolean oldModified = (Boolean)node.getResource( PREVIOUS_MODIFIED_STATE );
				boolean newModified = node.isModified();
				node.putResource( PREVIOUS_MODIFIED_STATE, null );

				collectFinalEvents( node, node, oldModified, newModified );
			}

			dispatchTransactionEvents();
		} finally {
			reset();
			activeTransaction = null;
			COMMIT_LOCK.unlock();
			Log.write( Log.DETAIL, "Transaction[" + System.identityHashCode( this ) + "] committed!" );
		}
	}

	public void reset() {
		finalEvents.clear();
		operations.clear();
		nodes.clear();
	}

	@Override
	public String toString() {
		return String.valueOf( "transaction[" + System.identityHashCode( this ) + "]" );
	}

	void modify( DataNode node ) {
		addOperation( new ModifyOperation( node ) );
	}

	void unmodify( DataNode node ) {
		addOperation( new UnmodifyOperation( node ) );
	}

	private void addOperation( Operation operation ) {
		if( COMMIT_LOCK.isLocked() && inActiveTransaction( operation.getData() ) ) throw new RuntimeException( "Data should not be modified from data listeners." );

		Log.write( Log.DETAIL, "Transaction[" + System.identityHashCode( this ) + "] adding operation: " + operation );

		nodes.add( operation.getData() );
		operations.offer( operation );
	}

	private boolean inActiveTransaction( DataNode node ) {
		return activeTransaction != null && activeTransaction.nodes.contains( node );
	}

	private TransactionEvents getTransactionEvents( DataNode node ) {
		TransactionEvents events = finalEvents.get( node );
		if( events == null ) {
			events = new TransactionEvents();
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
		getTransactionEvents( event.getData() ).modified = event;
	}

	private void postChangedEvent( DataChangedEvent event ) {
		getTransactionEvents( event.getData() ).changed = event;
	}

	private void dispatchTransactionEvents() {
		for( DataNode node : finalEvents.keySet() ) {
			TransactionEvents events = finalEvents.get( node );
			for( DataValueEvent event : events.events ) {
				dispatchValueEvent( event );
			}
		}

		// Fire the modified events first.
		for( DataNode node : finalEvents.keySet() ) {
			TransactionEvents events = finalEvents.get( node );
			if( events.modified != null ) node.dispatchEvent( events.modified );
		}

		// Fire the data changed events last.
		for( DataNode node : finalEvents.keySet() ) {
			TransactionEvents events = finalEvents.get( node );
			if( events.changed != null ) node.dispatchEvent( events.changed );
		}
	}

	private void dispatchValueEvent( DataEvent event ) {
		DataNode sender = event.getData();

		sender.dispatchEvent( event );

		for( DataNode parent : sender.getParents() ) {
			dispatchValueEvent( event.cloneWithNewSender( parent ) );
		}
	}

	private class TransactionEvents {
	
		public List<DataValueEvent> events = new ArrayList<DataValueEvent>();
	
		public DataChangedEvent changed;
	
		public MetaAttributeEvent modified;
	
	}

	private static class ModifyOperation extends Operation {

		public ModifyOperation( DataNode data ) {
			super( data );
		}

		@Override
		protected OperationResult process() {
			OperationResult result = new OperationResult( this );

			getData().doModify();

			return result;
		}

	}

	private static class UnmodifyOperation extends Operation {

		public UnmodifyOperation( DataNode data ) {
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

			if( index == Integer.MAX_VALUE ) index = list.size();
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

}
