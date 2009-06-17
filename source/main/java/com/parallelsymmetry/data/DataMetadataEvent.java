package com.parallelsymmetry.data;

public class DataMetadataEvent extends DataEvent {

	private DataNode.Metadata key;

	private Object oldValue;

	private Object newValue;

	public DataMetadataEvent( DataNode node, DataNode.Metadata key, Object oldValue, Object newValue ) {
		super( Type.METADATA, node );
		this.key = key;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public DataNode.Metadata getKey() {
		return key;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

}
