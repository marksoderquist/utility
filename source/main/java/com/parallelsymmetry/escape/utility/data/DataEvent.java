package com.parallelsymmetry.escape.utility.data;

public class DataEvent {

	private DataObject data;

	private String name;

	private Object newValue;

	private Object oldValue;

	public DataEvent( DataObject data, String name, Object newValue, Object oldValue ) {
		this.data = data;
		this.name = name;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	public DataObject getData() {
		return data;
	}

	public String getAttributeName() {
		return name;
	}

	public Object getNewValue() {
		return newValue;
	}

	public Object getOldValue() {
		return oldValue;
	}

}
