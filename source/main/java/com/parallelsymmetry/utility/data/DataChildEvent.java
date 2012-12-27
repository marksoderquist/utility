package com.parallelsymmetry.utility.data;

public class DataChildEvent extends DataEvent {

	private int index;

	private DataNode child;

	public DataChildEvent( Action action, DataNode node, int index, DataNode child ) {
		super( DataEvent.Type.DATA_CHILD, action, node );
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
		return getAction().toString() + ": " + getCause() + "(" + index + "): " + child;
	}

}
