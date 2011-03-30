package com.parallelsymmetry.escape.utility.data;

import junit.framework.TestCase;

public abstract class DataTestCase extends TestCase {

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
		assertEquals( dataEventCount, handler.getDataEvents().size() );
		assertEquals( dataAttributeEventCount, handler.getDataAttributeEvents().size() );
		assertEquals( metaAttributeEventCount, handler.getMetaAttributeEvents().size() );
	}

	protected void assertEventState( DataEventHandler handler, int index, Class<?> clazz, DataEvent.Type type, DataNode data ) {
		assertEventState( handler, index, clazz, type, data, null, null, null );
	}

	protected void assertEventState( DataEventHandler handler, int index, Class<?> clazz, DataEvent.Type type, DataNode data, String name, Object oldValue, Object newValue ) {
		DataEvent event = handler.getEvents().get( index );
		assertEquals( clazz, event.getClass() );
		assertEquals( type, event.getType() );
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
