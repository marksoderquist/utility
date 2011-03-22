package com.parallelsymmetry.escape.utility.data;

public class DataEvent {

	public enum Type {
		INSERT, REMOVE, MODIFY
	}

	private Type type;

	private DataObject data;

	public DataEvent( Type type, DataObject data ) {
		this.type = type;
		this.data = data;
	}

	public Type getType() {
		return type;
	}

	public DataObject getData() {
		return data;
	}

}
