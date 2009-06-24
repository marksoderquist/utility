package com.parallelsymmetry.data;

public class DataMetadataEvent extends DataEvent {

	private String key;

	private Object oldValue;

	private Object newValue;

	public DataMetadataEvent( DataNode node, String key, Object oldValue, Object newValue ) {
		super( Type.METADATA, node );
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
