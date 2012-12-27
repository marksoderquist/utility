package com.parallelsymmetry.utility.data;

public class DataChangedEvent extends DataEvent {

	public DataChangedEvent( Action action, DataNode data ) {
		super( DataEvent.Type.DATA_CHANGED, action, data );
	}

}
