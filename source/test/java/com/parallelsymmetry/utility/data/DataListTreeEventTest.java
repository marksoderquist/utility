package com.parallelsymmetry.utility.data;

import com.parallelsymmetry.utility.mock.MockDataList;

public class DataListTreeEventTest extends DataTreeEventTest {

	@Override
	protected DataNode createNode( String name ) {
		return new MockDataList( name );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	protected void addParent( DataNode grandparent, DataNode parent ) {
		( (DataList<DataNode>)grandparent ).add( parent );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	protected void addChild( DataNode parent, DataNode child ) {
		( (DataList<DataNode>)parent ).add( child );
	}

}
