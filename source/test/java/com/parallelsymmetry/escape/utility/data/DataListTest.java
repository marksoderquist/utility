package com.parallelsymmetry.escape.utility.data;

import java.lang.reflect.Modifier;

import org.junit.Test;

import com.parallelsymmetry.escape.utility.log.Log;

public class DataListTest extends DataTestCase {

	@Test
	public void testDataListIsAbstract() {
		assertTrue( "DataList class is not abstract.", ( DataList.class.getModifiers() & Modifier.ABSTRACT ) == Modifier.ABSTRACT );
	}

	@Test
	public void testConstructor() {
		MockDataList list = new MockDataList();
		DataEventHandler handler = list.getDataEventHandler();
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );
	}

	@Test
	public void testAdd() {
		MockDataList list = new MockDataList();
		DataEventHandler handler = list.getDataEventHandler();
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		MockDataNode child = new MockDataNode();
		list.add( child );
		assertEquals( child, list.get( 0 ) );
		assertListState( list, true, 0, 1 );
		assertEventCounts( handler, 1, 0, 1, 1, 0 );
	}

	@Test
	public void testRemove() {
		MockDataList list = new MockDataList();
		DataEventHandler handler = list.getDataEventHandler();
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		MockDataNode child = new MockDataNode();
		list.add( child );
		assertListState( list, true, 0, 1 );
		assertEventCounts( handler, 1, 0, 1, 1, 0 );

		list.remove( child );
		assertEquals( 0, list.size() );
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 2, 0, 2, 1, 1 );
	}

	@Test
	public void testSet() {
		MockDataList list = new MockDataList();
		DataEventHandler handler = list.getDataEventHandler();
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		MockDataNode child0 = new MockDataNode();
		list.add( child0 );
		assertListState( list, true, 0, 1 );
		assertEventCounts( handler, 1, 0, 1, 1, 0 );

		MockDataNode child1 = new MockDataNode();
		list.set( 0, child1 );
		assertEquals( 1, list.size() );
		assertListState( list, true, 0, 1 );
		assertEventCounts( handler, 2, 0, 1, 2, 1 );
	}

	@Test
	public void testClearModifed() {
		MockDataList list = new MockDataList();
		DataEventHandler handler = list.getDataEventHandler();
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		MockDataNode child = new MockDataNode();
		list.add( child );
		assertListState( list, true, 0, 1 );
		assertEventCounts( handler, 1, 0, 1, 1, 0 );

		list.clearModified();
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 2, 0, 2, 1, 0 );
	}

	@Test
	public void testSetAttributeOnChildModifiesParent() {
		MockDataList list = new MockDataList();
		DataEventHandler listHandler = list.getDataEventHandler();
		MockDataNode child = new MockDataNode();
		DataEventHandler childHandler = child.getDataEventHandler();
		assertListState( list, false, 0, 0 );
		assertEventCounts( listHandler, 0, 0, 0, 0, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 0, 0, 0, 0, 0 );

		list.add( child );
		assertEquals( child, list.get( 0 ) );
		assertListState( list, true, 0, 1 );
		assertEventCounts( listHandler, 1, 0, 1, 1, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 0, 0, 0, 0, 0 );

		list.clearModified();
		assertListState( list, false, 0, 0 );
		assertEventCounts( listHandler, 2, 0, 2, 1, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 0, 0, 0, 0, 0 );

		child.setAttribute( "attribute", "value" );
		assertListState( list, true, 0, 1 );
		assertNodeState( child, true, 1 );
		assertEventCounts( listHandler, 3, 0, 3, 1, 0 );
		assertNodeState( child, true, 1 );
		assertEventCounts( childHandler, 1, 1, 1, 0, 0 );
	}

	@Test
	public void testClearModifiedOnParentClearsChild() {
		MockDataList list = new MockDataList();
		DataEventHandler listHandler = list.getDataEventHandler();
		MockDataNode child = new MockDataNode();
		DataEventHandler childHandler = child.getDataEventHandler();
		assertListState( list, false, 0, 0 );
		assertEventCounts( listHandler, 0, 0, 0, 0, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 0, 0, 0, 0, 0 );

		list.add( child );
		assertEquals( child, list.get( 0 ) );
		assertListState( list, true, 0, 1 );
		assertEventCounts( listHandler, 1, 0, 1, 1, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 0, 0, 0, 0, 0 );

		list.clearModified();
		assertListState( list, false, 0, 0 );
		assertEventCounts( listHandler, 2, 0, 2, 1, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 0, 0, 0, 0, 0 );

		child.setAttribute( "attribute", "value" );
		assertListState( list, true, 0, 1 );
		assertEventCounts( listHandler, 3, 0, 3, 1, 0 );
		assertNodeState( child, true, 1 );
		assertEventCounts( childHandler, 1, 1, 1, 0, 0 );

		list.clearModified();
		assertListState( list, false, 0, 0 );
		assertEventCounts( listHandler, 4, 0, 4, 1, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 2, 1, 2, 0, 0 );
	}

}
