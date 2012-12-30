package com.parallelsymmetry.utility.data;

public class DataNodeTreeEventTest extends DataTreeEventTest {

	@Override
	protected DataNode createNode( String name ) {
		return new MockDataNode( name );
	}

	@Override
	protected void addParent( DataNode grandparent, DataNode parent ) {
		grandparent.setAttribute( "parent", parent );
	}

	@Override
	protected void addChild( DataNode parent, DataNode child ) {
		parent.setAttribute( "child", child );
	}

}
