package com.parallelsymmetry.escape.utility.data;

import org.junit.Test;

public class DataNodeListenerTest extends DataTestCase {

	@Test
	public void testMetadataModifedTrue() {
		MockDataList data = new MockDataList();
		DataEventHandler handler = data.getDataEventHandler();

		assertEventCounts( handler, 0, 0, 0, 0, 0 );
		handler.reset();

		data.setAttribute( "key", "value" );
		assertEventCounts( handler, 1, 1, 1, 0, 0 );
		handler.reset();
	}

	@Test
	public void testMetadataModifiedFalse() {
		MockDataList data = new MockDataList();
		DataEventHandler handler = data.getDataEventHandler();

		assertEventCounts( handler, 0, 0, 0, 0, 0 );
		handler.reset();

		data.setAttribute( "key", "value" );
		assertEventCounts( handler, 1, 1, 1, 0, 0 );
		handler.reset();

		data.setAttribute( "key", null );
		assertEventCounts( handler, 1, 1, 1, 0, 0 );
		handler.reset();
	}

}
