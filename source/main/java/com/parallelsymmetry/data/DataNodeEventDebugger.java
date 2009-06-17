package com.parallelsymmetry.data;

import com.parallelsymmetry.log.Log;

public class DataNodeEventDebugger implements DataListener {

	@Override
	public void dataChanged( DataEvent event ) {
		Log.write( Log.WARN, "Event type: " + event.getType() );
	}

	@Override
	public void metadataChanged( DataMetadataEvent event ) {
		Log.write( Log.WARN, "Event type: " + event.getType() + " " + event.getKey() + " == " + event.getNewValue() );
	}

	@Override
	public void attributeChanged( DataAttributeEvent event ) {
		Log.write( Log.WARN, "Event type: " + event.getType() + " " + event.getKey() + " == " + event.getNewValue() );
	}

	@Override
	public void childAdded( DataChildEvent event ) {
		Log.write( Log.WARN, "Event type: " + event.getType() );
	}

	@Override
	public void childRemoved( DataChildEvent event ) {
		Log.write( Log.WARN, "Event type: " + event.getType() );
	}

}
