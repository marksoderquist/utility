package com.parallelsymmetry.escape.utility.data;

public class MockDataList extends DataList<DataNode> {

	private String name;

	public MockDataList() {
		this( null );
	}

	public MockDataList( String name ) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name == null ? super.toString() : name;
	}

}
