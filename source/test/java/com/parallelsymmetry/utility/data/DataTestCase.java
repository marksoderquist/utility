package com.parallelsymmetry.utility.data;

import junit.framework.TestCase;

import com.parallelsymmetry.utility.data.DataAttributeEvent;
import com.parallelsymmetry.utility.data.DataEvent;
import com.parallelsymmetry.utility.data.DataNode;
import com.parallelsymmetry.utility.data.MetaAttributeEvent;
import com.parallelsymmetry.utility.log.Log;

public abstract class DataTestCase extends TestCase {

	@Override
	public void setUp() {
		Log.setLevel( Log.NONE );
	}

	@Override
	public void tearDown() {
		Log.setLevel( Log.NONE );
	}

	protected void assertNodeState( DataNode node, boolean modified, int modifiedAttributeCount ) {
		assertNodeState( node, modified, modifiedAttributeCount, false );
	}

	protected void assertNodeState( DataNode node, boolean modified, int modifiedAttributeCount, boolean transactionActive ) {
		assertEquals( modified, node.isModified() );
		assertEquals( modifiedAttributeCount, node.getModifiedAttributeCount() );
		assertEquals( transactionActive, node.isTransactionActive() );
	}

	protected void assertListState( DataList<?> node, boolean modified, int modifiedAttributeCount, int modifiedChildCount ) {
		assertNodeState( node, modified, modifiedAttributeCount );

		// FIXME - C Re-enable modified child count check.
		//assertEquals( modifiedChildCount, node.getModifiedChildCount() );
	}

	protected void assertEventCounts( DataEventWatcher handler, int dataEventCount, int dataAttributeEventCount, int metaAttributeEventCount ) {
		assertEventCounts( handler, dataEventCount, dataAttributeEventCount, metaAttributeEventCount, 0, 0 );
	}

	protected void assertEventCounts( DataEventWatcher handler, int dataEventCount, int dataAttributeEventCount, int metaAttributeEventCount, int childInsertEventCount, int childRemoveEventCount ) {
		assertEquals( dataEventCount, handler.getDataChangedEvents().size() );
		assertEquals( dataAttributeEventCount, handler.getDataAttributeEvents().size() );
		assertEquals( metaAttributeEventCount, handler.getMetaAttributeEvents().size() );
		assertEquals( childInsertEventCount, handler.getChildInsertedEvents().size() );
		assertEquals( childRemoveEventCount, handler.getChildRemovedEvents().size() );
	}

	/**
	 * For use with DataChangedEvents.
	 */
	protected void assertEventState( DataEventWatcher handler, int index, DataEvent.Type type, DataEvent.Action action, DataNode data ) {
		assertEventState( handler, index, type, action, data, null, null, null, null, -1, null );
	}

	/**
	 * For use with MetaAttributeEvents.
	 */
	protected void assertEventState( DataEventWatcher handler, int index, DataEvent.Type type, DataEvent.Action action, DataNode data, String name, Object oldValue, Object newValue ) {
		assertEventState( handler, index, type, action, data, null, name, oldValue, newValue, -1, null );
	}
	
	/**
	 * For use with DataAttributeEvents.
	 */
	protected void assertEventState( DataEventWatcher handler, int index, DataEvent.Type type, DataEvent.Action action, DataNode data, DataNode cause, String name, Object oldValue, Object newValue ) {
		assertEventState( handler, index, type, action, data, cause, name, oldValue, newValue, -1, null );
	}
	
	/**
	 * For use with DataChildEvents.
	 */
	protected void assertEventState( DataEventWatcher handler, int index, DataEvent.Type type, DataEvent.Action action, DataNode data, DataNode cause, int listIndex, DataNode child ) {
		assertEventState( handler, index, type, action, data, cause, null, null, null, listIndex, child );
	}

	private void assertEventState( DataEventWatcher handler, int index, DataEvent.Type type, DataEvent.Action action, DataNode data, DataNode cause, String name, Object oldValue, Object newValue, int listIndex, DataNode child ) {
		DataEvent event = handler.getEvents().get( index );
		
		// Check things common to all event types.
		assertEquals( type, event.getType() );
		assertEquals( action, event.getAction() );
		assertEquals( data, event.getData() );

		switch( event.getType() ) {
			case DATA_CHANGED : {
				break;
			}
			case META_ATTRIBUTE: {
				MetaAttributeEvent metaEvent = (MetaAttributeEvent)event;
				assertEquals( name, metaEvent.getAttributeName() );
				assertEquals( oldValue, metaEvent.getOldValue() );
				assertEquals( newValue, metaEvent.getNewValue() );
				break;
			}
			case DATA_ATTRIBUTE: {
				DataAttributeEvent dataEvent = (DataAttributeEvent)event;
				assertEquals( cause, dataEvent.getCause() );
				assertEquals( name, dataEvent.getAttributeName() );
				assertEquals( oldValue, dataEvent.getOldValue() );
				assertEquals( newValue, dataEvent.getNewValue() );
				break;
			}
			case DATA_CHILD: {
				DataChildEvent childEvent = (DataChildEvent)event;
				assertEquals( cause, childEvent.getCause() );
				assertEquals( listIndex, childEvent.getIndex() );
				assertEquals( child, childEvent.getChild() );
				break;
			}
		}
	}

}
