package com.parallelsymmetry.escape.utility.data;

class MockDataNode extends DataNode {

	private String name;

	private DataEventHandler handler;

	public MockDataNode() {
		this( null );
	}

	public MockDataNode( String name ) {
		this.name = name;
		setAttribute( "name", name );
		unmodify();

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
