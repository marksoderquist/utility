package com.parallelsymmetry.data;

public interface DataListener {

	void dataChanged( DataEvent event );

	void metadataChanged( DataMetadataEvent event );

	void attributeChanged( DataAttributeEvent event );

	void childAdded( DataChildEvent event );

	void childRemoved( DataChildEvent event );

}
