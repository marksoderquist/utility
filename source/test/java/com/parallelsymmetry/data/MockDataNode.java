package com.parallelsymmetry.data;

import com.parallelsymmetry.data.DataNode;

public class MockDataNode extends DataNode {

	private final String name;

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

	@Override
	public int hashCode() {
		return name == null ? super.hashCode() : name.hashCode();
	}

	@Override
	public boolean equals( Object object ) {
		if( !( object instanceof MockDataNode ) ) return false;
		MockDataNode that = (MockDataNode)object;
		return name == null ? super.equals( object ) : name.equals( that.name );
	}

}
