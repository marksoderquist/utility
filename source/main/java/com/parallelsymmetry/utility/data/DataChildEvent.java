package com.parallelsymmetry.utility.data;

public class DataChildEvent extends DataEvent {

	private int index;

	private DataNode child;

	public DataChildEvent( Type type, DataNode node, int index, DataNode child ) {
		super( type, node );
		this.index = index;
		this.child = child;
	}

	public int getIndex() {
		return index;
	}

	public DataNode getChild() {
		return child;
	}

	@Override
	public String toString() {
		return getType().toString() + ": " + getData() + "(" + index + "): " + child;
	}

}
