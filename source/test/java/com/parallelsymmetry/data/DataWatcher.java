package com.parallelsymmetry.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import junit.framework.Assert;

import com.parallelsymmetry.data.DataAttributeEvent;
import com.parallelsymmetry.data.DataChildEvent;
import com.parallelsymmetry.data.DataEvent;
import com.parallelsymmetry.data.DataListener;
import com.parallelsymmetry.data.DataMetadataEvent;

public class DataWatcher extends Assert implements DataListener {

	private List<DataEvent> dataChangedEvents;

	private List<DataMetadataEvent> metadataChangedEvents;

	private List<DataAttributeEvent> attributeChangedEvents;

	private List<DataChildEvent> dataAddedEvents;

	private List<DataChildEvent> dataRemovedEvents;

	public DataWatcher() {
		dataChangedEvents = new CopyOnWriteArrayList<DataEvent>();
		metadataChangedEvents = new CopyOnWriteArrayList<DataMetadataEvent>();
		attributeChangedEvents = new CopyOnWriteArrayList<DataAttributeEvent>();
		dataAddedEvents = new CopyOnWriteArrayList<DataChildEvent>();
		dataRemovedEvents = new CopyOnWriteArrayList<DataChildEvent>();
	}

	public void reset() {
		dataChangedEvents.clear();
		metadataChangedEvents.clear();
		attributeChangedEvents.clear();
		dataAddedEvents.clear();
		dataRemovedEvents.clear();
	}

	@Override
	public void dataChanged( DataEvent event ) {
		dataChangedEvents.add( event );
	}

	@Override
	public void metadataChanged( DataMetadataEvent event ) {
		metadataChangedEvents.add( event );
	}

	@Override
	public void attributeChanged( DataAttributeEvent event ) {
		attributeChangedEvents.add( event );
	}

	@Override
	public void childAdded( DataChildEvent event ) {
		dataAddedEvents.add( event );
	}

	@Override
	public void childRemoved( DataChildEvent event ) {
		dataRemovedEvents.add( event );
	}

	public DataEvent[] getDataChangedEvents() {
		return dataChangedEvents.toArray( new DataEvent[dataChangedEvents.size()] );
	}

	public DataMetadataEvent[] getMetadataChangedEvents() {
		return metadataChangedEvents.toArray( new DataMetadataEvent[metadataChangedEvents.size()] );
	}

	public DataAttributeEvent[] getAttributeChangedEvents() {
		return attributeChangedEvents.toArray( new DataAttributeEvent[attributeChangedEvents.size()] );
	}

	public DataChildEvent[] getChildAddedEvents() {
		return dataAddedEvents.toArray( new DataChildEvent[dataAddedEvents.size()] );
	}

	public DataChildEvent[] getChildRemovedEvents() {
		return dataRemovedEvents.toArray( new DataChildEvent[dataRemovedEvents.size()] );
	}

	public void assertEventCounts( int dataChanged, int metadataChanged, int attributeChanged, int childAdded, int childRemoved ) {
		if( dataChanged > -1 ) assertEquals( "Data changed event count", dataChanged, getDataChangedEvents().length );
		if( metadataChanged > -1 ) assertEquals( "Metadata changed event count", metadataChanged, getMetadataChangedEvents().length );
		if( attributeChanged > -1 ) assertEquals( "Attribute changed event count", attributeChanged, getAttributeChangedEvents().length );
		if( childAdded > -1 ) assertEquals( "Child added event count", childAdded, getChildAddedEvents().length );
		if( childRemoved > -1 ) assertEquals( "Child removed event count", childRemoved, getChildRemovedEvents().length );
	}

}
