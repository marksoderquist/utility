package com.parallelsymmetry.utility.data;

public abstract class DataEvent {
	
	public enum Type {
		DATA_CHANGED, DATA_ATTRIBUTE, META_ATTRIBUTE, DATA_CHILD
	}

	public enum Action {
		MODIFY, INSERT, REMOVE
	}

	private Type type;
	
	private Action action;

	private DataNode data;

	public DataEvent( Type type, Action action, DataNode data ) {
		this.type = type;
		this.action = action;
		this.data = data;
	}
	
	public Type getType() {
		return type;
	}

	public Action getAction() {
		return action;
	}

	public DataNode getData() {
		return data;
	}

}
