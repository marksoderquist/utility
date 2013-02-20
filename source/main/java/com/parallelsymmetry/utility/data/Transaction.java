package com.parallelsymmetry.utility.data;

import java.util.ArrayList;
import java.util.Iterator;
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

	private static final ThreadLocal<Transaction> threadLocalTransaction = new ThreadLocal<Transaction>();

	private static Transaction committingTransaction;

	private Queue<Operation> operations;

	private Set<Integer> nodeKeys;

	private Map<Integer, DataNode> nodes;

	private Map<Integer, ResultCollector> collectors;

	private int depth;

	private Transaction() {
		nodeKeys = new CopyOnWriteArraySet<Integer>();
		operations = new ConcurrentLinkedQueue<Operation>();
		nodes = new ConcurrentHashMap<Integer, DataNode>();
		collectors = new ConcurrentHashMap<Integer, ResultCollector>();
	}

	public static final Transaction create() {
		Transaction transaction = threadLocalTransaction.get();
		if( transaction == null ) threadLocalTransaction.set( transaction = new Transaction() );

		++transaction.depth;

		return transaction;
	}

	public static final void submit( Operation operation ) {
		Transaction transaction = threadLocalTransaction.get();
		if( transaction == null ) throw new NullPointerException( "Transaction must be created first." );

		transaction.doSubmit( operation );
	}

	public static final boolean commit() {
		Transaction transaction = threadLocalTransaction.get();
		if( transaction == null ) throw new NullPointerException( "Transaction must be created first." );

		--transaction.depth;

		if( transaction.depth == 0 ) {
			threadLocalTransaction.set( null );
			transaction.doCommit();
		}

		return true;

		//		try {
		//			transaction.commit();
		//			return true;
		//		} catch( CommitException exception ) {
		//			transaction.rollback();
		//			return false;
		//		} finally {
		//			threadLocalTransaction.set( null );
		//		}
	}

	public static final void rollback() {
		Transaction transaction = threadLocalTransaction.get();
		if( transaction == null ) throw new NullPointerException( "Transaction must be created first." );

		threadLocalTransaction.set( null );
		transaction.doRollback();
	}

	public static final void reset() {
		Transaction transaction = threadLocalTransaction.get();

		threadLocalTransaction.set( null );

		if( transaction != null ) transaction.doReset();
	}

	public static final int depth() {
		Transaction transaction = threadLocalTransaction.get();
		return transaction == null ? 0 : transaction.depth;
	}

	@Override
	public String toString() {
		return String.valueOf( "transaction[" + System.identityHashCode( this ) + "]" );
	}

	private void doSubmit( Operation operation ) {
		if( COMMIT_LOCK.isLocked() && inCommittingTransaction( operation.getData() ) ) throw new TransactionException( "Data should not be modified from data listeners." );

		Log.write( Log.DETAIL, "Transaction[" + System.identityHashCode( this ) + "] add operation: " + operation );

		addOperationNode( operation.getData() );
		operations.offer( operation );
	}

	private void doCommit() {
		//public void commit() throws CommitException {
		try {
			Log.write( Log.DETAIL, "Committing transaction[" + System.identityHashCode( this ) + "]..." );
			COMMIT_LOCK.lock();

			committingTransaction = this;

			// Store the current modified state of each data object.
			for( DataNode node : nodes.values() ) {
				node.putResource( PREVIOUS_MODIFIED_STATE, node.isModified() );
			}

			// Process the operations.
			List<OperationResult> operationResults = new ArrayList<OperationResult>();
			for( Operation operation : operations ) {
				Log.write( Log.DETAIL, "Transaction[" + System.identityHashCode( this ) + "] processing operation: " + operation );
				operationResults.add( operation.process() );
			}

			// Go through each operation result and collect the events for each node.
			for( OperationResult operationResult : operationResults ) {
				DataNode node = operationResult.getOperation().getData();
				getResultCollector( node ).events.addAll( operationResult.getEvents() );
				getResultCollector( node ).modified.addAll( operationResult.getMetaValueEvents() );
			}

			// Send the events for each data node.
			for( DataNode node : nodes.values() ) {
				boolean oldModified = (Boolean)node.getResource( PREVIOUS_MODIFIED_STATE );
				boolean newModified = node.isModified();
				node.putResource( PREVIOUS_MODIFIED_STATE, null );
				collectFinalEvents( node, node, oldModified, newModified );
			}

			dispatchTransactionEvents();
		} finally {
			doReset();
			committingTransaction = null;
			COMMIT_LOCK.unlock();
			Log.write( Log.DETAIL, "Transaction[" + System.identityHashCode( this ) + "] committed!" );
		}
	}

	private void doRollback() {
		//throw new UnsupportedOperationException( "Transaction.rollback() not implemented yet." );

		doReset();
	}

	private void doReset() {
		collectors.clear();
		operations.clear();
		nodeKeys.clear();
		nodes.clear();
	}

	private void addOperationNode( DataNode node ) {
		Integer key = System.identityHashCode( node );
		synchronized( nodeKeys ) {
			if( nodeKeys.contains( key ) ) return;
			nodeKeys.add( key );
			nodes.put( key, node );
		}
	}

	private boolean inCommittingTransaction( DataNode node ) {
		return committingTransaction != null && committingTransaction.nodeKeys.contains( System.identityHashCode( node ) );
	}

	private ResultCollector getResultCollector( DataNode node ) {
		Integer key = System.identityHashCode( node );
		ResultCollector collector = collectors.get( key );
		if( collector == null ) {
			collector = new ResultCollector();
			collectors.put( key, collector );
		}
		return collector;
	}

	private void collectFinalEvents( DataNode sender, DataNode cause, boolean oldModified, boolean newModified ) {
		// Post the modified event.
		boolean modifiedChanged = oldModified != newModified;
		if( modifiedChanged ) storeModifiedEvent( new MetaAttributeEvent( DataEvent.Action.MODIFY, sender, DataNode.MODIFIED, oldModified, newModified ) );

		// Update the parent nodes.
		for( DataNode parent : sender.getParents() ) {
			boolean parentOldModified = parent.isModified();
			if( modifiedChanged ) {
				if( parent instanceof DataList ) {
					( (DataList<?>)parent ).listNodeChildModified( newModified );
				} else {
					parent.dataNodeModified( newModified );
				}
			}
			boolean parentNewModified = parent.isModified();

			collectFinalEvents( parent, cause, parentOldModified, parentNewModified );
		}

		// Post the changed event.
		storeChangedEvent( new DataChangedEvent( DataEvent.Action.MODIFY, sender ) );
	}

	private void storeModifiedEvent( MetaAttributeEvent event ) {
		// Remove any previously added modified events.
		List<MetaAttributeEvent> events = getResultCollector( event.getSender() ).modified;
		Iterator<MetaAttributeEvent> iterator = events.iterator();
		while( iterator.hasNext() ) {
			MetaAttributeEvent metaValueEvent = iterator.next();
			if( DataNode.MODIFIED.equals( metaValueEvent.getAttributeName() ) ) iterator.remove();
		}

		// Add the new modified event.
		getResultCollector( event.getSender() ).modified.add( event );
	}

	private void storeChangedEvent( DataChangedEvent event ) {
		getResultCollector( event.getSender() ).changed = event;
	}

	private void dispatchTransactionEvents() {
		// Fire the data value events first
		for( Integer key : collectors.keySet() ) {
			ResultCollector collector = collectors.get( key );
			for( DataValueEvent event : collector.events ) {
				dispatchValueEvent( event );
			}
		}

		// Fire the meta value events next.
		for( Integer key : collectors.keySet() ) {
			ResultCollector collector = collectors.get( key );
			for( MetaAttributeEvent event : collector.modified ) {
				dispatchEvent( event );
			}
		}

		// Fire the data changed events last.
		for( Integer key : collectors.keySet() ) {
			ResultCollector collector = collectors.get( key );
			if( collector.changed != null ) dispatchEvent( collector.changed );
		}
	}

	private void dispatchValueEvent( DataEvent event ) {
		DataNode sender = event.getSender();

		sender.dispatchEvent( event );

		for( DataNode parent : sender.getParents() ) {
			dispatchValueEvent( event.cloneWithNewSender( parent ) );
		}
	}

	private void dispatchEvent( DataEvent event ) {
		event.getSender().dispatchEvent( event );
	}

	private class ResultCollector {

		public List<DataValueEvent> events = new ArrayList<DataValueEvent>();

		public List<MetaAttributeEvent> modified = new ArrayList<MetaAttributeEvent>();

		public DataChangedEvent changed;

	}

}
