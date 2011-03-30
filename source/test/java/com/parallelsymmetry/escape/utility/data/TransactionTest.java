package com.parallelsymmetry.escape.utility.data;

public class TransactionTest extends DataTestCase {

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
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

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
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
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

}
