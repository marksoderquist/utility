package com.parallelsymmetry.data;

public class DataChildEvent extends DataEvent {

	private DataNode child;

	private int index;

	public DataChildEvent( Type type, DataNode node, DataNode child, int index ) {
		super( type, node );
		this.child = child;
		this.index = index;
	}

	public DataNode getChild() {
		return child;
	}

	public int getIndex() {
		return index;
	}

}
