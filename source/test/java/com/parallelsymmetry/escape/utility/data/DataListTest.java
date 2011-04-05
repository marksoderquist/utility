package com.parallelsymmetry.escape.utility.data;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

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
	public void testIndexOf() {
		MockDataList parent = new MockDataList();
		MockDataList child = new MockDataList();
		assertEquals( -1, parent.indexOf( child ) );

		parent.add( child );
		assertEquals( 0, parent.indexOf( child ) );
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
	public void testAddMultipleChildren() {
		MockDataList node = new MockDataList();
		DataEventHandler handler = node.getDataEventHandler();
		MockDataList child0 = new MockDataList();
		MockDataList child1 = new MockDataList();
		MockDataList child2 = new MockDataList();

		node.add( child0 );
		assertEquals( node, child0.getParent() );
		assertSame( child0, node.get( 0 ) );
		assertListState( node, true, 0, 1 );
		assertEventCounts( handler, 1, 0, 1, 1, 0 );

		node.add( child1 );
		assertEquals( node, child1.getParent() );
		assertSame( child1, node.get( 1 ) );
		assertListState( node, true, 0, 2 );
		assertEventCounts( handler, 2, 0, 1, 2, 0 );

		node.add( child2 );
		assertEquals( node, child2.getParent() );
		assertSame( child2, node.get( 2 ) );
		assertListState( node, true, 0, 3 );
		assertEventCounts( handler, 3, 0, 1, 3, 0 );
	}

	@Test
	public void testAddUsingTransaction() {
		MockDataList node = new MockDataList();
		MockDataNode child0 = new MockDataNode();
		MockDataNode child1 = new MockDataNode();
		MockDataNode child2 = new MockDataNode();

		Transaction transaction = node.startTransaction();
		node.add( child0 );
		node.add( child1 );
		node.add( child2 );
		transaction.commit();

		assertEquals( node, child0.getParent() );
		assertSame( child0, node.get( 0 ) );

		assertEquals( node, child1.getParent() );
		assertSame( child1, node.get( 1 ) );

		assertEquals( node, child2.getParent() );
		assertSame( child2, node.get( 2 ) );
	}

	@Test
	public void testAddWithIndex() {
		MockDataList node = new MockDataList();
		MockDataList child0 = new MockDataList();
		MockDataList child1 = new MockDataList();
		MockDataList child2 = new MockDataList();

		node.add( child0 );
		assertEquals( node, child0.getParent() );
		assertSame( child0, node.get( 0 ) );

		node.add( child2 );
		assertEquals( node, child2.getParent() );
		assertSame( child2, node.get( 1 ) );

		node.add( 1, child1 );
		assertEquals( node, child1.getParent() );
		assertSame( child1, node.get( 1 ) );
	}

	@Test
	public void testAddWithUsedChildNode() {
		MockDataList node0 = new MockDataList();
		MockDataList node1 = new MockDataList();
		MockDataList child = new MockDataList();

		node0.add( child );
		node0.clearModified();
		assertFalse( node0.isModified() );
		assertEquals( 1, node0.size() );

		node1.add( child );
		assertTrue( node0.isModified() );
		assertEquals( 0, node0.size() );
		assertEquals( 1, node1.size() );
	}

	@Test
	public void testAddAll() {
		MockDataList list = new MockDataList();
		List<DataNode> nodes = new ArrayList<DataNode>();
		nodes.add( new MockDataNode( "0" ) );
		nodes.add( new MockDataNode( "1" ) );
		nodes.add( new MockDataNode( "2" ) );

		assertTrue( list.addAll( nodes ) );
		assertFalse( list.addAll( nodes ) );
		assertEquals( nodes.size(), list.size() );

		nodes.add( new MockDataNode( "3" ) );
		nodes.add( new MockDataNode( "4" ) );
		nodes.add( new MockDataNode( "5" ) );

		assertTrue( list.addAll( nodes ) );
		assertFalse( list.addAll( nodes ) );
		assertEquals( nodes.size(), list.size() );
	}

	@Test
	public void testAddAllUsingTransaction() {
		MockDataList list = new MockDataList();
		List<DataNode> nodes0 = new ArrayList<DataNode>();
		nodes0.add( new MockDataNode( "node0" ) );
		nodes0.add( new MockDataNode( "node1" ) );
		nodes0.add( new MockDataNode( "node2" ) );

		List<DataNode> nodes1 = new ArrayList<DataNode>();
		nodes1.add( new MockDataNode( "node3" ) );
		nodes1.add( new MockDataNode( "node4" ) );
		nodes1.add( new MockDataNode( "node5" ) );

		Transaction transaction = list.startTransaction();
		assertTrue( list.addAll( nodes0 ) );
		assertTrue( list.addAll( nodes1 ) );
		transaction.commit();

		assertEquals( nodes0.size() + nodes1.size(), list.size() );
		assertSame( nodes0.get( 0 ), list.get( 0 ) );
		assertSame( nodes0.get( 1 ), list.get( 1 ) );
		assertSame( nodes0.get( 2 ), list.get( 2 ) );
		assertSame( nodes1.get( 0 ), list.get( 3 ) );
		assertSame( nodes1.get( 1 ), list.get( 4 ) );
		assertSame( nodes1.get( 2 ), list.get( 5 ) );
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
	public void testRemoveWithNode() {
		MockDataList list = new MockDataList();
		DataEventHandler handler = list.getDataEventHandler();
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		list.remove( null );
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
	public void testRemoveWithIndex() {
		MockDataList node = new MockDataList();
		MockDataList child = new MockDataList();

		try {
			node.remove( -1 );
			fail();
		} catch( IndexOutOfBoundsException exception ) {}

		try {
			node.remove( 0 );
			fail();
		} catch( IndexOutOfBoundsException exception ) {}

		try {
			node.remove( 1 );
			fail();
		} catch( IndexOutOfBoundsException exception ) {}

		node.add( child );
		assertEquals( node, child.getParent() );
		assertEquals( child, node.get( 0 ) );

		try {
			node.remove( -1 );
			fail();
		} catch( IndexOutOfBoundsException exception ) {}

		try {
			node.remove( 1 );
			fail();
		} catch( IndexOutOfBoundsException exception ) {}

		node.remove( 0 );
		assertNull( child.getParent() );
		assertEquals( 0, node.size() );
	}

	@Test
	public void testRemoveAll() {
		MockDataList list = new MockDataList();
		List<DataNode> nodes = new ArrayList<DataNode>();
		MockDataNode node0 = new MockDataNode( "0" );
		MockDataNode node1 = new MockDataNode( "1" );
		MockDataNode node2 = new MockDataNode( "2" );

		nodes.add( node0 );
		nodes.add( node1 );
		nodes.add( node2 );
		list.addAll( nodes );

		List<DataNode> remove = new ArrayList<DataNode>();
		remove.add( node0 );
		remove.add( node2 );

		assertEquals( nodes.size(), list.size() );
		list.removeAll( remove );
		assertEquals( 1, list.size() );
		assertEquals( node1, list.get( 0 ) );
	}

	@Test
	public void testToArray() {
		MockDataList node = new MockDataList();
		MockDataList[] array = new MockDataList[0];
		assertEquals( array, node.toArray( array ) );

		node.add( new MockDataList() );
		assertEquals( 1, node.toArray( array ).length );
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

	@Test
	public void testNodeModifiedByTreeAttribute() {
		MockDataList node = new MockDataList();
		MockDataList child1 = new MockDataList();
		MockDataList child2 = new MockDataList();

		node.add( child1 );
		node.add( child2 );

		assertTrue( node.isModified() );
		assertFalse( node.isSelfModified() );
		assertTrue( node.isTreeModified() );
		assertFalse( child1.isModified() );
		assertFalse( child2.isModified() );

		node.clearModified();
		assertFalse( node.isModified() );
		assertFalse( child1.isModified() );
		assertFalse( child2.isModified() );

		child1.setAttribute( "key", "value" );
		assertTrue( node.isModified() );
		assertFalse( node.isSelfModified() );
		assertTrue( node.isTreeModified() );
		assertTrue( child1.isModified() );
		assertTrue( child1.isSelfModified() );
		assertFalse( child1.isTreeModified() );
		assertFalse( child2.isModified() );
	}

	@Test
	public void testNodeUnmodifiedByTreeAttribute() {
		MockDataList node = new MockDataList();
		MockDataList child1 = new MockDataList();
		MockDataList child2 = new MockDataList();

		node.add( child1 );
		node.add( child2 );

		assertTrue( node.isModified() );
		assertFalse( node.isSelfModified() );
		assertTrue( node.isTreeModified() );
		assertFalse( child1.isModified() );
		assertFalse( child2.isModified() );

		node.clearModified();
		assertFalse( node.isModified() );
		assertFalse( child1.isModified() );
		assertFalse( child2.isModified() );

		child1.setAttribute( "key", "value" );
		assertTrue( node.isModified() );
		assertFalse( node.isSelfModified() );
		assertTrue( node.isTreeModified() );
		assertTrue( child1.isModified() );
		assertTrue( child1.isSelfModified() );
		assertFalse( child1.isTreeModified() );
		assertFalse( child2.isModified() );

		child1.setAttribute( "key", null );
		assertFalse( node.isModified() );
		assertFalse( child1.isModified() );
		assertFalse( child2.isModified() );
	}

	@Test
	public void testNodeModifiedByTreeChildren() {
		MockDataList node = new MockDataList();
		MockDataList child1 = new MockDataList();
		MockDataList child2 = new MockDataList();
		MockDataList grandchild = new MockDataList();

		node.add( child1 );
		node.add( child2 );

		assertTrue( node.isModified() );
		assertFalse( node.isSelfModified() );
		assertTrue( node.isTreeModified() );
		assertFalse( child1.isModified() );
		assertFalse( child2.isModified() );

		node.clearModified();
		assertFalse( node.isModified() );
		assertFalse( child1.isModified() );
		assertFalse( child2.isModified() );

		child1.add( grandchild );
		assertTrue( node.isModified() );
		assertFalse( node.isSelfModified() );
		assertTrue( node.isTreeModified() );
		assertTrue( child1.isModified() );
		assertFalse( child1.isSelfModified() );
		assertTrue( child1.isTreeModified() );
		assertFalse( child2.isModified() );
	}

	@Test
	public void testNodeUnmodifiedByTreeChildren() {
		MockDataList node = new MockDataList();
		MockDataList child1 = new MockDataList();
		MockDataList child2 = new MockDataList();
		MockDataList grandchild = new MockDataList();

		node.add( child1 );
		node.add( child2 );

		assertTrue( node.isModified() );
		assertFalse( node.isSelfModified() );
		assertTrue( node.isTreeModified() );
		assertFalse( child1.isModified() );
		assertFalse( child2.isModified() );

		node.clearModified();
		assertFalse( node.isModified() );
		assertFalse( child1.isModified() );
		assertFalse( child2.isModified() );

		child1.add( grandchild );
		assertTrue( node.isModified() );
		assertFalse( node.isSelfModified() );
		assertTrue( node.isTreeModified() );
		assertTrue( child1.isModified() );
		assertFalse( child1.isSelfModified() );
		assertTrue( child1.isTreeModified() );
		assertFalse( child2.isModified() );

		child1.remove( grandchild );
		assertFalse( node.isModified() );
		assertFalse( child1.isModified() );
		assertFalse( child2.isModified() );
	}

	@Test
	public void testNodeModifiedByModifedNodeInAttributeMap() {
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> attributeNode = new MockDataList();
		assertFalse( node.isModified() );
		assertFalse( attributeNode.isModified() );

		node.setAttribute( "node", attributeNode );
		assertTrue( node.isModified() );
		assertFalse( attributeNode.isModified() );

		node.clearModified();
		assertFalse( node.isModified() );
		assertFalse( attributeNode.isModified() );
	}

	@Test
	public void testNodeUnmodifiedByUnmodifedNodeInAttributeMap() {
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> attributeNode = new MockDataList();
		assertFalse( node.isModified() );
		assertFalse( attributeNode.isModified() );

		node.setAttribute( "node", attributeNode );
		assertTrue( node.isModified() );
		assertFalse( attributeNode.isModified() );

		node.clearModified();
		assertFalse( node.isModified() );
		assertFalse( attributeNode.isModified() );
	}

	@Test
	public void testNodeModifiedByAdditionOfChildInDataNodeAttribute() {
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> list = new MockDataList();
		DataList<DataNode> child = new MockDataList();

		node.setAttribute( "list", list );
		node.clearModified();
		assertFalse( "The node should not be modified.", node.isModified() );

		list.add( child );
		assertTrue( "Addition of the child from the list should modify the node.", node.isModified() );
	}

	@Test
	public void testNodeUnmodifiedByRemovalOfChildInDataNodeAttribute() {
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> list = new MockDataList();
		DataList<DataNode> child = new MockDataList();

		node.setAttribute( "list", list );
		node.clearModified();
		assertFalse( "The node should not be modified.", node.isModified() );

		list.add( child );
		assertTrue( "Addition of the child from the list should modify the node.", node.isModified() );

		list.remove( child );
		assertFalse( "Removal of the child from the list should unmodify the node.", node.isModified() );
	}

	@Test
	public void testNodeModifiedByAttributeModifyOfChildInDataNodeAttribute() {
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> list = new MockDataList();
		DataList<DataNode> child = new MockDataList();

		node.setAttribute( "list", list );
		list.add( child );
		node.clearModified();
		assertFalse( "The node should not be modified.", node.isModified() );

		child.setAttribute( "key", "value" );
		assertTrue( child.isModified() );
		assertTrue( list.isModified() );
		assertTrue( node.isModified() );
	}

	//	public void testDataChangedEventTriggering() throws Exception {
	//		MockDataList parent = new MockDataList();
	//		MockDataNode child = new MockDataNode();
	//		DataEventHandler handler = parent.getDataEventHandler();
	//		parent.add( child );
	//		parent.clearModified();
	//		assertFalse( parent.isModified() );
	//		assertFalse( child.isModified() );
	//		assertEventCounts( handler, 2, 0, 2, 1, 0 );
	//		handler.reset();
	//
	//		child.setAttribute( "key1", "value1" );
	//		assertEventCounts( handler, 1, 1, 1, 0, 0 );
	//		handler.reset();
	//
	//		child.setAttribute( "key1", "value1" );
	//		assertEventCounts( handler, 0, 0, 0, 0, 0 );
	//		handler.reset();
	//
	//		child.setAttribute( "key1", "value2" );
	//		assertEventCounts( handler, 1, 1, 0, 0, 0 );
	//		handler.reset();
	//	}

}
