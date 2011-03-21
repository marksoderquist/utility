package com.parallelsymmetry.escape.utility.data;

public class MetaAttributeEvent extends DataEvent {

	private String name;

	private Object newValue;

	private Object oldValue;

	public MetaAttributeEvent( DataObject data, String name, Object newValue, Object oldValue ) {
		super( data );
		this.name = name;
		this.newValue = newValue;
		this.oldValue = oldValue;
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
