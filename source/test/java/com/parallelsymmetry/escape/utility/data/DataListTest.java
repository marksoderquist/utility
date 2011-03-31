package com.parallelsymmetry.escape.utility.data;

import java.lang.reflect.Modifier;

import org.junit.Test;

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
	public void testConstructorWithChildren() {
		MockDataList child1 = new MockDataList();
		MockDataList child2 = new MockDataList();

		MockDataList[] children = new MockDataList[2];
		children[0] = child1;
		children[1] = child2;

		MockDataList parent = new MockDataList( children );

		assertFalse( parent.isModified() );
		assertEquals( child1, parent.get( 0 ) );
		assertEquals( child2, parent.get( 1 ) );
	}

	@Test
	public void testIsSelfModified() {
		MockDataList node = new MockDataList();
		assertFalse( node.isModified() );
		node.setAttribute( "key", "value" );
		assertTrue( node.isSelfModified() );
		assertFalse( node.isTreeModified() );
		node.setAttribute( "key", null );
		assertFalse( node.isSelfModified() );
		assertFalse( node.isTreeModified() );
		node.setAttribute( "key", "value" );
		assertTrue( node.isSelfModified() );
		assertFalse( node.isTreeModified() );
		node.clearModified();
		assertFalse( node.isSelfModified() );
		assertFalse( node.isTreeModified() );
	}

	@Test
	public void testIsTreeModified() {
		MockDataList node = new MockDataList();
		MockDataNode child = new MockDataNode();
		assertFalse( node.isModified() );
		node.add( child );
		assertFalse( node.isSelfModified() );
		assertTrue( node.isTreeModified() );
		node.remove( child );
		assertFalse( node.isSelfModified() );
		assertFalse( node.isTreeModified() );
		node.add( child );
		assertFalse( node.isSelfModified() );
		assertTrue( node.isTreeModified() );
		node.clearModified();
		assertFalse( node.isSelfModified() );
		assertFalse( node.isTreeModified() );
	}

	@Test
	public void testAttributes() {
		String key = "key";
		Object value = "value";

		MockDataList node = new MockDataList();
		assertNull( "Missing attribute should be null.", node.getAttribute( key ) );

		node.setAttribute( key, value );
		assertEquals( "Attribute value incorrect", value, node.getAttribute( key ) );

		node.setAttribute( key, null );
		assertNull( "Removed attribute should be null.", node.getAttribute( key ) );
	}

	@Test
	public void testObjectAttribute() {
		String key = "key";
		Object value = new Object();
		MockDataList node = new MockDataList();
		node.setAttribute( key, value );
		Object check = node.getAttribute( key );
		assertEquals( "Object value not equal", value, check );
	}

	@Test
	public void testStringAttribute() {
		String key = "key";
		String value = "value";
		MockDataList node = new MockDataList();
		node.setAttribute( key, value );
		String check = (String)node.getAttribute( key );
		assertEquals( "String value not equal", value, check );
	}

	@Test
	public void testBooleanAttribute() {
		String key = "key";
		boolean value = true;
		MockDataList node = new MockDataList();
		node.setAttribute( key, value );
		boolean check = (Boolean)node.getAttribute( key );
		assertEquals( "Integer value not equal", value, check );
	}

	@Test
	public void testIntegerAttribute() {
		String key = "key";
		int value = 0;
		MockDataList node = new MockDataList();
		node.setAttribute( key, value );
		int check = (Integer)node.getAttribute( key );
		assertEquals( "Integer value not equal", value, check );
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
	public void testSetAttributeWithUsedChildNode() {
		String key = "key";
		MockDataList node0 = new MockDataList( "list0" );
		MockDataList node1 = new MockDataList( "list1" );
		MockDataList child = new MockDataList( "child" );

		node0.add( child );
		node0.clearModified();
		assertFalse( node0.isModified() );
		assertEquals( 1, node0.size() );

		node1.setAttribute( key, child );
		assertTrue( node0.isModified() );
		assertEquals( 0, node0.size() );
		assertEquals( child, node1.getAttribute( key ) );
	}

	@Test
	public void testSetAttributeWithUsedAttributeNode() {
		String key = "key";
		MockDataList node0 = new MockDataList();
		MockDataList node1 = new MockDataList();
		MockDataList child = new MockDataList();

		node0.setAttribute( key, child );
		node0.clearModified();
		assertFalse( node0.isModified() );
		assertEquals( child, node0.getAttribute( key ) );

		node1.setAttribute( key, child );
		assertTrue( node0.isModified() );
		assertEquals( 0, node0.size() );
		assertEquals( child, node1.getAttribute( key ) );
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
