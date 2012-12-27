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

	protected void assertNodeState( DataNode node, boolean modified, int modifiedAttributeCount ) {
		assertNodeState( node, modified, modifiedAttributeCount, false );
	}

	protected void assertNodeState( DataNode node, boolean modified, int modifiedAttributeCount, boolean transactionActive ) {
		assertEquals( modified, node.isModified() );
		assertEquals( modifiedAttributeCount, node.getModifiedAttributeCount() );
		assertEquals( transactionActive, node.isTransactionActive() );
	}

	protected void assertListState( DataNode node, boolean modified, int modifiedAttributeCount, int modifiedChildCount ) {
		assertNodeState( node, modified, modifiedAttributeCount );
		//assertEquals( modifiedChildCount, node.getModifiedChildCount() );
	}

	protected void assertEventCounts( DataEventHandler handler, int dataEventCount, int dataAttributeEventCount, int metaAttributeEventCount ) {
		assertEventCounts( handler, dataEventCount, dataAttributeEventCount, metaAttributeEventCount, 0, 0 );
	}

	protected void assertEventCounts( DataEventHandler handler, int dataEventCount, int dataAttributeEventCount, int metaAttributeEventCount, int childInsertEventCount, int childRemoveEventCount ) {
		assertEquals( dataEventCount, handler.getDataChangedEvents().size() );
		assertEquals( dataAttributeEventCount, handler.getDataAttributeEvents().size() );
		assertEquals( metaAttributeEventCount, handler.getMetaAttributeEvents().size() );
		assertEquals( childInsertEventCount, handler.getChildInsertedEvents().size() );
		assertEquals( childRemoveEventCount, handler.getChildRemovedEvents().size() );
	}

	protected void assertEventState( DataEventHandler handler, int index, Class<?> clazz, DataEvent.Action type, DataNode data ) {
		assertEventState( handler, index, clazz, type, data, null, null, null );
	}

	protected void assertEventState( DataEventHandler handler, int index, Class<?> clazz, DataEvent.Action type, DataNode data, String name, Object oldValue, Object newValue ) {
		DataEvent event = handler.getEvents().get( index );
		assertEquals( clazz, event.getClass() );
		assertEquals( type, event.getAction() );
		assertEquals( data, event.getData() );

		if( name == null ) return;

		if( event instanceof DataAttributeEvent ) {
			DataAttributeEvent dataEvent = (DataAttributeEvent)event;
			assertEquals( name, dataEvent.getAttributeName() );
			assertEquals( oldValue, dataEvent.getOldValue() );
			assertEquals( newValue, dataEvent.getNewValue() );
		} else if( event instanceof MetaAttributeEvent ) {
			MetaAttributeEvent metaEvent = (MetaAttributeEvent)event;
			assertEquals( name, metaEvent.getAttributeName() );
			assertEquals( oldValue, metaEvent.getOldValue() );
			assertEquals( newValue, metaEvent.getNewValue() );
		}
	}

}
