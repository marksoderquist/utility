package com.parallelsymmetry.data;

import junit.framework.TestCase;

import org.junit.Test;

public class DataNodeListenerTest extends TestCase {

	@Test
	public void testMetadataModifedTrue() {
		MockDataList data = new MockDataList();
		DataWatcher watcher = new DataWatcher();

		data.addDataListener( watcher );
		watcher.assertEventCounts( 0, 0, 0, 0, 0 );
		watcher.reset();

		data.setAttribute( "key", "value" );
		watcher.assertEventCounts( 1, 1, 1, 0, 0 );
		watcher.reset();
	}

	@Test
	public void testMetadataModifiedFalse() {
		MockDataList data = new MockDataList();
		DataWatcher watcher = new DataWatcher();

		data.addDataListener( watcher );
		watcher.assertEventCounts( 0, 0, 0, 0, 0 );
		watcher.reset();

		data.setAttribute( "key", "value" );
		watcher.assertEventCounts( 1, 1, 1, 0, 0 );
		watcher.reset();

		data.setAttribute( "key", null );
		watcher.assertEventCounts( 1, 1, 1, 0, 0 );
		watcher.reset();
	}

}
