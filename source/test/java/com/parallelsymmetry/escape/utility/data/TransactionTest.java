package com.parallelsymmetry.escape.utility.data;

public class TransactionTest extends DataTestCase {

	public void testTransactionAction() {
		MockData data = new MockData();
		DataHandler handler = new DataHandler();
		data.addDataListener( handler );
		assertDataState( data, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		Transaction transaction = new Transaction();
		transaction.add( new MockAction( data ) );

		transaction.commit();
		assertDataState( data, false, 0, 0 );
		assertEventCounts( handler, 1, 1, 0 );
		
		int index  = 0;
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.MODIFY, data, "name", "value0", "value1" );
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

	private class MockAction extends Action {

		public MockAction( DataObject data ) {
			super( data );
		}

		@Override
		ActionResult process() {
			ActionResult result = new ActionResult( this );

			result.addEvent( new DataAttributeEvent( DataEvent.Type.MODIFY, getData(), "name", "value0", "value1" ) );

			return result;
		}

	}

}
