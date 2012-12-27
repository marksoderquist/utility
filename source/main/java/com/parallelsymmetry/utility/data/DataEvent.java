package com.parallelsymmetry.utility.data;

public abstract class DataEvent {

	public enum Type {
		MODIFY, INSERT, REMOVE
	}

	private Type type;

	private DataNode data;

	public DataEvent( Type type, DataNode data ) {
		this.type = type;
		this.data = data;
	}

	public Type getType() {
		return type;
	}

	public DataNode getData() {
		return data;
	}

}
