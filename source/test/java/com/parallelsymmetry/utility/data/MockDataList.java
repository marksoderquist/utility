package com.parallelsymmetry.utility.data;

import com.parallelsymmetry.utility.data.DataList;
import com.parallelsymmetry.utility.data.DataNode;

public class MockDataList extends DataList<DataNode> {

	private String name;

	private DataEventHandler handler;

	public MockDataList() {
		super();
		init( null );
	}

	public MockDataList( String name ) {
		super();
		init( name );
	}

	public MockDataList( DataNode[] children ) {
		super( children );
		init( null );
	}

	public MockDataList( String name, DataNode[] children ) {
		super( children );
		init( name );
	}

	private void init( String name ) {
		this.name = name;
		handler = new DataEventHandler();
		addDataListener( handler );
	}

	@Override
	public String toString() {
		return name == null ? super.toString() : name;
	}

	public DataEventHandler getDataEventHandler() {
		return handler;
	}

}
