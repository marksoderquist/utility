package com.parallelsymmetry.escape.utility.data;

import java.util.ArrayList;
import java.util.List;

import com.parallelsymmetry.escape.utility.log.Log;

class DataHandler implements DataListener {

	private List<DataEvent> events = new ArrayList<DataEvent>();

	private List<DataEvent> dataChangedEvents = new ArrayList<DataEvent>();

	private List<DataAttributeEvent> dataAttributeEvents = new ArrayList<DataAttributeEvent>();

	private List<MetaAttributeEvent> metaAttributeEvents = new ArrayList<MetaAttributeEvent>();

	@Override
	public void dataChanged( DataEvent event ) {
		Log.write( Log.TRACE, "Data change event received." );
		dataChangedEvents.add( event );
		events.add( event );
	}

	@Override
	public void dataAttributeChanged( DataAttributeEvent event ) {
		Log.write( Log.TRACE, "Data attribute change event received." );
		dataAttributeEvents.add( event );
		events.add( event );
	}

	@Override
	public void metaAttributeChanged( MetaAttributeEvent event ) {
		Log.write( Log.TRACE, "Meta attribute change event received." );
		metaAttributeEvents.add( event );
		events.add( event );
	}

	public List<DataEvent> getEvents() {
		return events;
	}

	public List<DataEvent> getDataEvents() {
		return dataChangedEvents;
	}

	public List<DataAttributeEvent> getDataAttributeEvents() {
		return dataAttributeEvents;
	}

	public List<MetaAttributeEvent> getMetaAttributeEvents() {
		return metaAttributeEvents;
	}

}