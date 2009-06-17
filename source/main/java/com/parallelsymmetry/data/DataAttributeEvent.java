package com.parallelsymmetry.data;

public class DataAttributeEvent extends DataEvent {

	private String key;

	private Object oldValue;

	private Object newValue;

	public DataAttributeEvent( DataNode node, String key, Object oldValue, Object newValue ) {
		super( Type.ATTRIBUTE, node );
		this.key = key;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public String getKey() {
		return key;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

}
