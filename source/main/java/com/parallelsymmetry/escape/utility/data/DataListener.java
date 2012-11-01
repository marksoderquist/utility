package com.parallelsymmetry.escape.utility.data;

public interface DataListener {

	void dataChanged( DataChangedEvent event );

	void dataAttributeChanged( DataAttributeEvent event );

	void metaAttributeChanged( MetaAttributeEvent event );

	void childInserted( DataChildEvent event );

	void childRemoved( DataChildEvent event );

}
