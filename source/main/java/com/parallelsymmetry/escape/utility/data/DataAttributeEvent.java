package com.parallelsymmetry.escape.utility.data;

public class DataAttributeEvent extends DataEvent {

	private String name;

	private Object newValue;

	private Object oldValue;

	public DataAttributeEvent( DataEvent.Type type, DataNode data, String name, Object oldValue, Object newValue ) {
		super( type, data );
		this.name = name;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public String getAttributeName() {
		return name;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

}
