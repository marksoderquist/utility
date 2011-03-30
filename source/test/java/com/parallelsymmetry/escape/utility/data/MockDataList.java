package com.parallelsymmetry.escape.utility.data;

public class MockDataList extends DataList<DataNode> {

	private String name;

	private DataEventHandler handler;

	public MockDataList() {
		this( null );
	}

	public MockDataList( String name ) {
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
