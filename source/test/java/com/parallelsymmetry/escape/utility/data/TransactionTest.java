package com.parallelsymmetry.escape.utility.data;

import org.junit.Test;

public class TransactionTest extends DataTestCase {

	@Test
	public void testTransaction() {
		MockDataNode data = new MockDataNode();
		DataEventHandler handler = new DataEventHandler();
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
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.INSERT, data, "attribute0", null, "value0" );
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.INSERT, data, "attribute1", null, "value1" );
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.INSERT, data, "attribute2", null, "value2" );
		assertEventState( handler, index++, MetaAttributeEvent.class, DataEvent.Type.MODIFY, data, DataNode.MODIFIED, false, true );
		assertEventState( handler, index++, DataChangedEvent.class, DataEvent.Type.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

	@Test
	public void testTransactionAction() {
		MockDataNode data = new MockDataNode();
		DataEventHandler handler = new DataEventHandler();
		data.addDataListener( handler );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		Transaction transaction = new Transaction();
		transaction.add( new MockAction( data ) );

		transaction.commit();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 1, 1, 0 );

		int index = 0;
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.MODIFY, data, "name", "value0", "value1" );
		assertEventState( handler, index++, DataChangedEvent.class, DataEvent.Type.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

	@Test
	public void testTransactionNested() throws Exception {
		MockDataNode node = new MockDataNode();
		DataEventHandler handler = node.getDataEventHandler();

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
		MockDataList parent = new MockDataList();
		MockDataNode child = new MockDataNode();
		DataEventHandler watcher = parent.getDataEventHandler();

		parent.add( child );
		parent.unmodify();
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		watcher.reset();

		Transaction transaction = parent.startTransaction();
		child.setAttribute( "key1", "value1" );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		assertEventCounts( watcher, 0, 0, 0, 0, 0 );
		watcher.reset();

		transaction.commit();
		assertTrue( parent.isModified() );
		assertTrue( child.isModified() );
		assertEventCounts( watcher, 1, 1, 1, 0, 0 );
		watcher.reset();

		transaction = parent.startTransaction();
		child.setAttribute( "key1", null );
		assertEventCounts( watcher, 0, 0, 0, 0, 0 );
		watcher.reset();

		transaction.commit();
		assertEventCounts( watcher, 1, 1, 1, 0, 0 );
		watcher.reset();
	}

	@Test
	public void testTransactionByModifyingGrandChild() {
		MockDataList parent = new MockDataList( "parent" );
		MockDataList child = new MockDataList( "child" );
		MockDataNode grandchild = new MockDataNode( "grandchild" );
		DataEventHandler watcher = parent.getDataEventHandler();

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

	private class MockAction extends Action {

		public MockAction( DataNode data ) {
			super( data );
		}

		@Override
		public ActionResult process() {
			ActionResult result = new ActionResult( this );

			result.addEvent( new DataAttributeEvent( DataEvent.Type.MODIFY, getData(), "name", "value0", "value1" ) );

			return result;
		}

	}

	private class ModifyingDataHandler extends DataAdapter {

		@Override
		public void dataAttributeChanged( DataAttributeEvent event ) {
			event.getData().setAttribute( "time", System.nanoTime() );
		}

	}

}
