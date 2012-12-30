package com.parallelsymmetry.utility.data;

import com.parallelsymmetry.utility.data.DataNode;

class MockDataNode extends DataNode implements WatchedMockData {

	private String name;

	private DataEventWatcher handler;

	public MockDataNode() {
		this( null );
	}

	public MockDataNode( String name ) {
		this.name = name;
		setAttribute( "name", name );
		unmodify();

		handler = new DataEventWatcher( name );
		addDataListener( handler );
	}

	@Override
	public String toString() {
		return name == null ? super.toString() : name;
	}

	@Override
	public DataEventWatcher getDataEventWatcher() {
		return handler;
	}

}
