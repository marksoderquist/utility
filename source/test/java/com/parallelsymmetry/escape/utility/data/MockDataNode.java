package com.parallelsymmetry.escape.utility.data;

class MockDataNode extends DataNode {

	private String name;

	public MockDataNode() {
		this( null );
	}

	public MockDataNode( String name ) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name == null ? super.toString() : name;
	}

}
