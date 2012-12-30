package com.parallelsymmetry.utility.data;

import org.junit.Test;

import com.parallelsymmetry.utility.ObjectUtil;

public class TransactionTest extends DataTestCase {

	@Test
	public void testTransaction() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = new DataEventWatcher();
		data.addDataListener( handler );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		Transaction transaction = data.startTransaction();
		assertTrue( data.isTransactionActive() );

		data.setAttribute( "attribute0", "value0" );
		data.setAttribute( "attribute1", "value1" );
		data.setAttribute( "attribute2", "value2" );
		assertNodeState( data, false, 0, true );
		assertEventCounts( handler, 0, 0, 0 );

		transaction.commit();
		assertNodeState( data, true, 3, false );
		assertEventCounts( handler, 1, 3, 1 );

		int index = 0;
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, data, data, "attribute0", null, "value0" );
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, data, data, "attribute1", null, "value1" );
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, data, data, "attribute2", null, "value2" );
		assertEventState( handler, index++, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, data, DataNode.MODIFIED, false, true );
		assertEventState( handler, index++, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

	@Test
	public void testTransactionOperation() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = new DataEventWatcher();
		data.addDataListener( handler );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		Transaction transaction = new Transaction();
		transaction.add( new MockOperation( data ) );

		transaction.commit();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 1, 1, 0 );

		int index = 0;
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.MODIFY, data, data, "name", "value0", "value1" );
		assertEventState( handler, index++, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

	@Test
	public void testTransactionNested() throws Exception {
		MockDataNode node = new MockDataNode();
		DataEventWatcher handler = node.getDataEventWatcher();

		// Initial transaction.
		Transaction transaction0 = node.startTransaction();
		node.setAttribute( "key1", "value1" );
		node.setAttribute( "key2", "value2" );
		assertEventCounts( handler, 0, 0, 0, 0, 0 );
		assertNull( node.getAttribute( "key1" ) );
		assertNull( node.getAttribute( "key2" ) );
		assertNull( node.getAttribute( "key3" ) );

		// Nested transaction.
		Transaction transaction1 = node.startTransaction();
		node.setAttribute( "key3", "value3" );
		assertEventCounts( handler, 0, 0, 0, 0, 0 );
		assertNull( node.getAttribute( "key1" ) );
		assertNull( node.getAttribute( "key2" ) );
		assertNull( node.getAttribute( "key3" ) );

		// Nested commit.
		transaction1.commit();
		assertEventCounts( handler, 0, 0, 0, 0, 0 );
		assertNull( node.getAttribute( "key1" ) );
		assertNull( node.getAttribute( "key2" ) );
		assertNull( node.getAttribute( "key3" ) );

		// Final commit.
		transaction0.commit();
		assertEventCounts( handler, 1, 3, 1, 0, 0 );
		handler.reset();
	}

	@Test
	public void testTransactionWithModifingEvent() {
		MockDataNode node = new MockDataNode();
		node.addDataListener( new ModifyingDataHandler() );
		try {
			node.setAttribute( "fire", "event" );
			fail( "RuntimeException should be thrown due to modifying data listener." );
		} catch( RuntimeException exception ) {

		}
	}

	@Test
	public void testTransactionByModifyingChild() {
		MockDataList parent = new MockDataList( "parent" );
		MockDataNode child = new MockDataNode( "child" );
		DataEventWatcher parentWatcher = parent.getDataEventWatcher();
		DataEventWatcher childWatcher = child.getDataEventWatcher();

		// Set up the data model.
		parent.add( child );

		// Set the parent unmodified.
		parent.setModified( false );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		parentWatcher.reset();
		childWatcher.reset();

		// Start a transaction
		Transaction transaction = parent.startTransaction();

		// Set the child attribute but nothing should happen
		// because the transaction has not been committed yet.
		child.setAttribute( "key1", "value1" );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		assertEventCounts( parentWatcher, 0, 0, 0, 0, 0 );
		assertEventCounts( childWatcher, 0, 0, 0, 0, 0 );
		parentWatcher.reset();
		childWatcher.reset();

		// Commit the transaction.
		transaction.commit();
		assertTrue( parent.isModified() );
		assertTrue( child.isModified() );
		assertEquals( "value1", child.getAttribute( "key1" ) );
		assertEventCounts( parentWatcher, 1, 1, 1, 0, 0 );
		assertEventState( parentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, parent, child, "key1", null, "value1" );
		assertEventState( parentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, parent, DataNode.MODIFIED, false, true );
		assertEventState( parentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, parent );
		assertEventCounts( childWatcher, 1, 1, 1, 0, 0 );
		assertEventState( childWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, child, child, "key1", null, "value1" );
		assertEventState( childWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, child, DataNode.MODIFIED, false, true );
		assertEventState( childWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, child );
		parentWatcher.reset();
		childWatcher.reset();

		// Unset the the child attribute but nothing should happen 
		// because the transaction has not been committed yet.
		transaction = parent.startTransaction();
		child.setAttribute( "key1", null );
		assertEventCounts( parentWatcher, 0, 0, 0, 0, 0 );
		assertEventCounts( childWatcher, 0, 0, 0, 0, 0 );
		parentWatcher.reset();
		childWatcher.reset();

		// Commit the transaction.
		transaction.commit();
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		assertEquals( null, child.getAttribute( "key1" ) );
		assertEventCounts( parentWatcher, 1, 1, 1, 0, 0 );
		assertEventState( parentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.REMOVE, parent, child, "key1", "value1", null );
		assertEventState( parentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, parent, DataNode.MODIFIED, true, false );
		assertEventState( parentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, parent );
		assertEventCounts( childWatcher, 1, 1, 1, 0, 0 );
		assertEventState( childWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.REMOVE, child, child, "key1", "value1", null );
		assertEventState( childWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, child, DataNode.MODIFIED, true, false );
		assertEventState( childWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, child );
	}

	@Test
	public void testTransactionByModifyingGrandChild() {
		MockDataList parent = new MockDataList( "parent" );
		MockDataList child = new MockDataList( "child" );
		MockDataNode grandchild = new MockDataNode( "grandchild" );
		DataEventWatcher watcher = parent.getDataEventWatcher();

		parent.add( child );
		child.add( grandchild );
		parent.unmodify();
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		assertFalse( grandchild.isModified() );
		watcher.reset();

		Transaction transaction = parent.startTransaction();
		grandchild.setAttribute( "key1", "value1" );
		assertEventCounts( watcher, 0, 0, 0, 0, 0 );
		watcher.reset();

		transaction.commit();
		assertEventCounts( watcher, 1, 1, 1, 0, 0 );
		watcher.reset();

		transaction = parent.startTransaction();
		grandchild.setAttribute( "key1", null );
		assertEventCounts( watcher, 0, 0, 0, 0, 0 );
		watcher.reset();

		transaction.commit();
		assertEventCounts( watcher, 1, 1, 1, 0, 0 );
		watcher.reset();
	}

	/**
	 * This is a particular case where data nodes have redefined the hashCode and
	 * equals methods to be dependent on an attribute. Transaction code cannot
	 * depend on the hashCode() method returning a valid value, therefore it
	 * cannot put the node into any collections that use the hashCode() method.
	 */
	@Test
	public void testNodeWithAttributeDependentHashcode() {
		AttributeDependentHashCodeType type = new AttributeDependentHashCodeType();
		assertNull( type.getKey() );

		type.setKey( "test" );
		assertEquals( "test", type.getKey() );

		type.setKey( null );
		assertNull( type.getKey() );
	}

	private class AttributeDependentHashCodeType extends DataNode {

		private static final String KEY = "key";

		public String getKey() {
			return (String)getAttribute( KEY );
		}

		public void setKey( String key ) {
			setAttribute( KEY, key );
		}

		@Override
		public int hashCode() {
			String key = getKey();
			return key == null ? 0 : key.hashCode();
		}

		@Override
		public boolean equals( Object object ) {
			if( !( object instanceof AttributeDependentHashCodeType ) ) return false;
			String key = getKey();
			return ObjectUtil.areEqual( key, ( (AttributeDependentHashCodeType)object ).getKey() );
		}

	}

	private class MockOperation extends Operation {

		public MockOperation( DataNode data ) {
			super( data );
		}

		@Override
		public OperationResult process() {
			OperationResult result = new OperationResult( this );

			result.addEvent( new DataAttributeEvent( DataEvent.Action.MODIFY, data, data, "name", "value0", "value1" ) );

			return result;
		}

	}

	private class ModifyingDataHandler extends DataAdapter {

		@Override
		public void dataAttributeChanged( DataAttributeEvent event ) {
			event.getCause().setAttribute( "time", System.nanoTime() );
		}

	}

}
