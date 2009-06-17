package com.parallelsymmetry.data;

public class DataAdapter implements DataListener {

	@Override
	public void dataChanged( DataEvent event ) {}

	@Override
	public void metadataChanged( DataMetadataEvent event ) {}

	@Override
	public void attributeChanged( DataAttributeEvent event ) {}

	@Override
	public void childAdded( DataChildEvent event ) {}

	@Override
	public void childRemoved( DataChildEvent event ) {}

}
