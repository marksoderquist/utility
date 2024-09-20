package com.parallelsymmetry.utility.data;

import com.parallelsymmetry.utility.mock.DataEventWatcher;
import com.parallelsymmetry.utility.mock.MockDataList;
import org.junit.jupiter.api.Test;

public class DataNodeListenerTest extends DataTestCase {

	@Test
	public void testMetadataModifiedTrue() {
		MockDataList data = new MockDataList();
		DataEventWatcher handler = data.getDataEventWatcher();

		assertEventCounts( handler, 0, 0, 0, 0, 0 );
		handler.reset();

		data.setAttribute( "key", "value" );
		assertEventCounts( handler, 1, 1, 1, 0, 0 );
		handler.reset();
	}

	@Test
	public void testMetadataModifiedFalse() {
		MockDataList data = new MockDataList();
		DataEventWatcher handler = data.getDataEventWatcher();

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
