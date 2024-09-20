package com.parallelsymmetry.utility.data;

import com.parallelsymmetry.utility.mock.DataEventWatcher;
import com.parallelsymmetry.utility.mock.MockDataList;
import com.parallelsymmetry.utility.mock.MockDataNode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataListTest extends DataTestCase {

	@Test
	public void testConstructor() {
		MockDataList list = new MockDataList();
		DataEventWatcher handler = list.getDataEventWatcher();
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );
	}

	@Test
	public void testConstructorWithChildren() {
		MockDataList child1 = new MockDataList();
		MockDataList child2 = new MockDataList();

		MockDataList[] children = new MockDataList[ 2 ];
		children[ 0 ] = child1;
		children[ 1 ] = child2;

		MockDataList parent = new MockDataList( children );

		assertFalse( parent.isModified() );
		assertSame( child1, parent.get( 0 ) );
		assertSame( child2, parent.get( 1 ) );
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
		node.setModified( false );
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
		node.setModified( false );
		assertFalse( node.isSelfModified() );
		assertFalse( node.isTreeModified() );
	}

	@Test
	public void testClearModifed() {
		MockDataList parent = new MockDataList( "parent" );
		DataEventWatcher watcher = parent.getDataEventWatcher();
		assertListState( parent, false, 0, 0 );
		assertEventCounts( watcher, 0, 0, 0 );

		MockDataNode child = new MockDataNode( "child" );
		parent.add( child );
		assertListState( parent, true, 0, 0 );
		assertEventCounts( watcher, 1, 1, 0, 1, 0 );
		watcher.reset();

		parent.setModified( false );
		assertListState( parent, false, 0, 0 );
		assertEventCounts( watcher, 1, 1, 0, 0, 0 );
	}

	@Test
	public void testAttributes() {
		String key = "key";
		Object value = "value";

		MockDataList node = new MockDataList();
		assertNull( node.getAttribute( key ), "Missing attribute should be null." );

		node.setAttribute( key, value );
		assertEquals( value, node.getAttribute( key ), "Attribute value incorrect" );

		node.setAttribute( key, null );
		assertNull( node.getAttribute( key ), "Removed attribute should be null." );
	}

	@Test
	public void testObjectAttribute() {
		String key = "key";
		Object value = new Object();
		MockDataList node = new MockDataList();
		node.setAttribute( key, value );
		Object check = node.getAttribute( key );
		assertEquals( value, check, "Object value not equal" );
	}

	@Test
	public void testStringAttribute() {
		String key = "key";
		String value = "value";
		MockDataList node = new MockDataList();
		node.setAttribute( key, value );
		String check = node.getAttribute( key );
		assertEquals( value, check, "String value not equal" );
	}

	@Test
	public void testBooleanAttribute() {
		String key = "key";
		boolean value = true;
		MockDataList node = new MockDataList();
		node.setAttribute( key, value );
		boolean check = node.getAttribute( key );
		assertEquals( value, check, "Boolean value not equal" );
	}

	@Test
	public void testIntegerAttribute() {
		String key = "key";
		int value = 0;
		MockDataList node = new MockDataList();
		node.setAttribute( key, value );
		int check = node.getAttribute( key );
		assertEquals( value, check, "Integer value not equal" );
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
		DataEventWatcher handler = list.getDataEventWatcher();
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		MockDataNode child = new MockDataNode();
		list.add( child );
		assertEquals( child, list.get( 0 ) );
		assertListState( list, true, 0, 0 );
		assertEventCounts( handler, 1, 1, 0, 1, 0 );
	}

	@Test
	public void testAddMultipleChildren() {
		MockDataList node = new MockDataList();
		DataEventWatcher handler = node.getDataEventWatcher();
		MockDataList child0 = new MockDataList();
		MockDataList child1 = new MockDataList();
		MockDataList child2 = new MockDataList();

		node.add( child0 );
		assertEquals( child0.getParent(), node );
		assertSame( child0, node.get( 0 ) );
		assertListState( node, true, 0, 0 );
		assertEventCounts( handler, 1, 1, 0, 1, 0 );

		node.add( child1 );
		assertEquals( child1.getParent(), node );
		assertSame( child1, node.get( 1 ) );
		assertListState( node, true, 0, 0 );
		assertEventCounts( handler, 2, 1, 0, 2, 0 );

		node.add( child2 );
		assertEquals( child2.getParent(), node );
		assertSame( child2, node.get( 2 ) );
		assertListState( node, true, 0, 0 );
		assertEventCounts( handler, 3, 1, 0, 3, 0 );
	}

	@Test
	public void testAddUsingTransaction() {
		MockDataList node = new MockDataList();
		MockDataNode child0 = new MockDataNode();
		MockDataNode child1 = new MockDataNode();
		MockDataNode child2 = new MockDataNode();

		Transaction.create();
		node.add( child0 );
		node.add( child1 );
		node.add( child2 );
		Transaction.commit();

		assertEquals( child0.getParent(), node );
		assertSame( child0, node.get( 0 ) );

		assertEquals( child1.getParent(), node );
		assertSame( child1, node.get( 1 ) );

		assertEquals( child2.getParent(), node );
		assertSame( child2, node.get( 2 ) );
	}

	@Test
	public void testAddWithIndex() {
		MockDataList node = new MockDataList();
		MockDataList child0 = new MockDataList();
		MockDataList child1 = new MockDataList();
		MockDataList child2 = new MockDataList();

		node.add( child0 );
		assertEquals( child0.getParent(), node );
		assertSame( child0, node.get( 0 ) );

		node.add( child2 );
		assertEquals( child2.getParent(), node );
		assertSame( child2, node.get( 1 ) );

		node.add( 1, child1 );
		assertEquals( child1.getParent(), node );
		assertSame( child1, node.get( 1 ) );
	}

	@Test
	public void testAddWithUsedChildNode() {
		MockDataList node0 = new MockDataList();
		MockDataList node1 = new MockDataList();
		MockDataList child = new MockDataList();

		node0.add( child );
		node0.setModified( false );
		assertFalse( node0.isModified() );
		assertEquals( 1, node0.size() );

		node1.add( child );
		assertFalse( node0.isModified() );
		assertEquals( 1, node0.size() );
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

		Transaction.create();
		assertTrue( list.addAll( nodes0 ) );
		assertTrue( list.addAll( nodes1 ) );
		Transaction.commit();

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
		DataEventWatcher handler = list.getDataEventWatcher();
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		MockDataNode child0 = new MockDataNode();
		list.add( child0 );
		assertListState( list, true, 0, 0 );
		assertEventCounts( handler, 1, 1, 0, 1, 0 );

		MockDataNode child1 = new MockDataNode();
		list.set( 0, child1 );
		assertEquals( 1, list.size() );
		assertListState( list, true, 0, 0 );
		assertEventCounts( handler, 2, 1, 0, 2, 1 );
	}

	@Test
	public void testRemoveWithNode() {
		MockDataList list = new MockDataList();
		DataEventWatcher handler = list.getDataEventWatcher();
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		list.remove( null );
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		MockDataNode child = new MockDataNode();
		list.add( child );
		assertListState( list, true, 0, 0 );
		assertEventCounts( handler, 1, 1, 0, 1, 0 );

		list.remove( child );
		assertEquals( 0, list.size() );
		assertListState( list, false, 0, 0 );
		assertEventCounts( handler, 2, 2, 0, 1, 1 );
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
		assertEquals( child.getParent(), node );
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
		List<DataNode> nodes = new ArrayList<>();
		MockDataNode node0 = new MockDataNode( "0" );
		MockDataNode node1 = new MockDataNode( "1" );
		MockDataNode node2 = new MockDataNode( "2" );

		nodes.add( node0 );
		nodes.add( node1 );
		nodes.add( node2 );
		list.addAll( nodes );

		List<DataNode> remove = new ArrayList<>();
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
		MockDataList[] array = new MockDataList[ 0 ];
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
		node0.setModified( false );
		assertEquals( child.getParent(), node0 );
		assertFalse( node0.isModified() );
		assertEquals( 1, node0.size() );
		assertTrue( node0.contains( child ) );

		node1.setAttribute( key, child );
		assertEquals( child.getParent(), node1 );
		assertFalse( node0.isModified() );
		assertEquals( 1, node0.size() );
		assertTrue( node0.contains( child ) );
		assertEquals( child, node1.getAttribute( key ) );
	}

	@Test
	public void testSetAttributeOnChildModifiesParent() {
		MockDataList list = new MockDataList();
		DataEventWatcher listHandler = list.getDataEventWatcher();
		MockDataNode child = new MockDataNode();
		DataEventWatcher childHandler = child.getDataEventWatcher();
		assertListState( list, false, 0, 0 );
		assertEventCounts( listHandler, 0, 0, 0, 0, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 0, 0, 0, 0, 0 );

		list.add( child );
		assertEquals( child, list.get( 0 ) );
		assertListState( list, true, 0, 0 );
		assertEventCounts( listHandler, 1, 1, 0, 1, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 0, 0, 0, 0, 0 );

		list.setModified( false );
		assertListState( list, false, 0, 0 );
		assertEventCounts( listHandler, 2, 2, 0, 1, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 0, 0, 0, 0, 0 );

		child.setAttribute( "attribute", "value" );
		assertListState( list, true, 0, 1 );
		assertNodeState( child, true, 1 );
		assertEventCounts( listHandler, 3, 3, 1, 1, 0 );
		assertNodeState( child, true, 1 );
		assertEventCounts( childHandler, 1, 1, 1, 0, 0 );
	}

	@Test
	public void testClearModifiedOnParentClearsChild() {
		// Set up data model
		MockDataList parent = new MockDataList();
		MockDataNode child = new MockDataNode();
		DataEventWatcher parentWatcher = parent.getDataEventWatcher();
		DataEventWatcher childWatcher = child.getDataEventWatcher();

		// Initial assertions.
		assertListState( parent, false, 0, 0 );
		assertEventCounts( parentWatcher, 0, 0, 0, 0, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childWatcher, 0, 0, 0, 0, 0 );

		// Add the child to the parent.
		parent.add( child );
		assertEquals( child, parent.get( 0 ) );
		assertListState( parent, true, 0, 0 );
		assertEventCounts( parentWatcher, 1, 1, 0, 1, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childWatcher, 0, 0, 0, 0, 0 );
		parentWatcher.reset();
		childWatcher.reset();

		// Set the parent unmodified.
		parent.setModified( false );
		assertListState( parent, false, 0, 0 );
		assertEventCounts( parentWatcher, 1, 1, 0, 0, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childWatcher, 0, 0, 0, 0, 0 );
		parentWatcher.reset();
		childWatcher.reset();

		// Set the child attribute.
		child.setAttribute( "attribute", "value" );
		assertListState( parent, true, 0, 1 );
		assertEventCounts( parentWatcher, 1, 1, 1, 0, 0 );
		assertNodeState( child, true, 1 );
		assertEventCounts( childWatcher, 1, 1, 1, 0, 0 );
		parentWatcher.reset();
		childWatcher.reset();

		// Set the parent unmodified.
		parent.setModified( false );
		assertListState( parent, false, 0, 0 );
		assertEventCounts( parentWatcher, 1, 1, 0, 0, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( childWatcher, 1, 1, 0, 0, 0 );
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

		node.setModified( false );
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

		node.setModified( false );
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

		node.setModified( false );
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

		node.setModified( false );
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
		MockDataList node = new MockDataList();
		MockDataList attributeNode = new MockDataList();
		assertFalse( node.isModified() );
		assertFalse( attributeNode.isModified() );

		node.setAttribute( "node", attributeNode );
		assertTrue( node.isModified() );
		assertFalse( attributeNode.isModified() );

		node.setModified( false );
		assertFalse( node.isModified() );
		assertFalse( attributeNode.isModified() );
	}

	@Test
	public void testNodeUnmodifiedByUnmodifedNodeInAttributeMap() {
		MockDataList node = new MockDataList();
		MockDataList attributeNode = new MockDataList();
		assertFalse( node.isModified() );
		assertFalse( attributeNode.isModified() );

		node.setAttribute( "node", attributeNode );
		assertTrue( node.isModified() );
		assertFalse( attributeNode.isModified() );

		node.setModified( false );
		assertFalse( node.isModified() );
		assertFalse( attributeNode.isModified() );
	}

	@Test
	public void testNodeModifiedByAdditionOfChildInDataNodeAttribute() {
		MockDataList node = new MockDataList();
		MockDataList list = new MockDataList();
		MockDataList child = new MockDataList();

		node.setAttribute( "list", list );
		node.setModified( false );
		assertFalse( node.isModified(), "The node should not be modified." );

		list.add( child );
		assertTrue( node.isModified(), "Addition of the child from the list should modify the node." );
	}

	@Test
	public void testNodeUnmodifiedByRemovalOfChildInDataNodeAttribute() {
		MockDataList node = new MockDataList();
		MockDataList list = new MockDataList();
		MockDataList child = new MockDataList();

		node.setAttribute( "list", list );
		node.setModified( false );
		assertFalse( node.isModified(), "The node should not be modified." );

		list.add( child );
		assertTrue( node.isModified(), "Addition of the child from the list should modify the node." );

		list.remove( child );
		assertFalse( node.isModified(), "Removal of the child from the list should unmodify the node." );
	}

	@Test
	public void testNodeModifiedByAttributeModifyOfChildInDataNodeAttribute() {
		MockDataList node = new MockDataList();
		MockDataList list = new MockDataList();
		MockDataList child = new MockDataList();

		node.setAttribute( "list", list );
		list.add( child );
		node.setModified( false );
		assertFalse( node.isModified(), "The node should not be modified." );

		child.setAttribute( "key", "value" );
		assertTrue( child.isModified() );
		assertTrue( list.isModified() );
		assertTrue( node.isModified() );
	}

	@Test
	public void testDataChangedEventTriggering() {
		MockDataList parent = new MockDataList();
		MockDataNode child = new MockDataNode();
		DataEventWatcher handler = parent.getDataEventWatcher();
		parent.add( child );
		parent.setModified( false );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		assertEventCounts( handler, 2, 2, 0, 1, 0 );
		handler.reset();

		child.setAttribute( "key1", "value1" );
		assertEventCounts( handler, 1, 1, 1, 0, 0 );
		handler.reset();

		child.setAttribute( "key1", "value1" );
		assertEventCounts( handler, 0, 0, 0, 0, 0 );
		handler.reset();

		child.setAttribute( "key1", "value2" );
		assertEventCounts( handler, 1, 0, 1, 0, 0 );
		handler.reset();
	}

	@Test
	public void testDataChangedEventFiredFromChildAttributeNode() {
		MockDataList node = new MockDataList();
		MockDataList attribute = new MockDataList();
		DataEventWatcher watcher = node.getDataEventWatcher();
		assertFalse( node.isModified() );

		// Set the attribute.
		node.setAttribute( "attribute", attribute );
		assertTrue( node.isModified() );
		assertEventCounts( watcher, 1, 1, 1, 0, 0 );

		// Setting an attribute on the attribute node should cause a data change event.
		attribute.setAttribute( "key", "value1" );
		assertTrue( node.isModified() );
		assertEventCounts( watcher, 2, 1, 2, 0, 0 );

		// Setting another attribute on the attribute node should cause a data change event.
		attribute.setAttribute( "key", "value2" );
		assertTrue( node.isModified() );
		assertEventCounts( watcher, 3, 1, 3, 0, 0 );
	}

	@Test
	public void testDataChangedEventFiredByModifyFlagOfChildInDataNodeAttribute() {
		MockDataList node = new MockDataList( "a" );
		MockDataList list = new MockDataList( "b" );
		MockDataList child = new MockDataList( "c" );
		MockDataList attribute = new MockDataList( "d" );
		DataEventWatcher watcher = node.getDataEventWatcher();

		node.setAttribute( "list", list );
		list.add( child );
		child.setAttribute( "attribute", attribute );
		node.setModified( false );
		watcher.reset();

		assertFalse( node.isModified(), "The node should not be modified." );
		assertFalse( list.isModified(), "The list should not be modified." );
		assertFalse( child.isModified(), "The child should not be modified." );
		assertFalse( attribute.isModified(), "The attribute should not be modified." );

		attribute.setAttribute( "key", "value1" );
		assertTrue( node.isModified(), "The node should be modified." );
		assertTrue( list.isModified(), "The list should be modified." );
		assertTrue( child.isModified(), "The child should be modified." );
		assertTrue( attribute.isModified(), "The attribute should be modified." );
		assertEventCounts( watcher, 1, 1, 1, 0, 0 );
		watcher.reset();

		attribute.setAttribute( "key", "value1" );
		assertEventCounts( watcher, 0, 0, 0, 0, 0 );
		watcher.reset();

		attribute.setAttribute( "key", "value2" );
		assertEventCounts( watcher, 1, 0, 1, 0, 0 );
		watcher.reset();

		attribute.setModified( false );
		assertFalse( attribute.isModified(), "The attribute should not be modified." );
		assertFalse( child.isModified(), "The child should not be modified." );
		assertFalse( list.isModified(), "The list should not be modified." );
		assertFalse( node.isModified(), "The node should not be modified." );
		assertEventCounts( watcher, 1, 1, 0, 0, 0 );
		watcher.reset();
	}

	@Test
	public void testDataChangedEventFiredByAttributeModifyInTransaction() {
		MockDataList node = new MockDataList();
		DataEventWatcher watcher = node.getDataEventWatcher();
		node.addDataListener( watcher );

		Transaction.create();
		node.setAttribute( "key1", "value1" );
		node.setAttribute( "key2", "value2" );
		Transaction.commit();

		assertEventCounts( watcher, 1, 1, 2, 0, 0 );
		assertEquals( "key1", watcher.getDataAttributeEvents().get( 0 ).getAttributeName() );
		assertEquals( "key2", watcher.getDataAttributeEvents().get( 1 ).getAttributeName() );
		watcher.reset();

		Transaction.create();
		node.setAttribute( "key1", null );
		node.setAttribute( "key2", null );
		Transaction.commit();

		assertEventCounts( watcher, 1, 1, 2, 0, 0 );
		assertEquals( "key1", watcher.getDataAttributeEvents().get( 0 ).getAttributeName() );
		assertEquals( "key2", watcher.getDataAttributeEvents().get( 1 ).getAttributeName() );
		watcher.reset();
	}

	@Test
	public void testNodeModifiedEventFiredByAttribute() {
		MockDataList node = new MockDataList();
		MockDataList list = new MockDataList();
		DataEventWatcher watcher = node.getDataEventWatcher();

		node.setModified( false );
		list.setModified( false );
		assertFalse( node.isModified(), "The node should not be modified." );
		assertFalse( list.isModified(), "The list should not be modified." );

		node.setAttribute( "list", list );
		assertEventCounts( watcher, 1, 1, 1, 0, 0 );
		watcher.reset();

		node.setAttribute( "list", null );
		assertEventCounts( watcher, 1, 1, 1, 0, 0 );
		watcher.reset();
	}

	@Test
	public void testNodeModifiedEventFiredByAttributeChild() {
		MockDataList node = new MockDataList();
		MockDataList list = new MockDataList();
		MockDataList child = new MockDataList();
		DataEventWatcher watcher = node.getDataEventWatcher();

		node.setAttribute( "list", list );
		node.setModified( false );
		assertFalse( node.isModified(), "The node should not be modified." );
		assertFalse( list.isModified(), "The list should not be modified." );
		watcher.reset();

		list.add( child );
		assertEventCounts( watcher, 1, 1, 0, 1, 0 );
		watcher.reset();

		list.remove( child );
		assertEventCounts( watcher, 1, 1, 0, 0, 1 );
		watcher.reset();
	}

	@Test
	public void testNodeModifiedEventFiredByChildAttributeChild() {
		MockDataList node = new MockDataList();
		MockDataList child1 = new MockDataList();
		MockDataList list = new MockDataList();
		MockDataList child2 = new MockDataList();
		DataEventWatcher watcher = node.getDataEventWatcher();

		node.add( child1 );
		child1.setAttribute( "list", list );
		node.setModified( false );
		assertFalse( node.isModified(), "The node should not be modified." );
		assertFalse( child1.isModified(), "The child1 should not be modified." );
		assertFalse( list.isModified(), "The list should not be modified." );
		watcher.reset();

		list.add( child2 );
		assertEventCounts( watcher, 1, 1, 0, 1, 0 );
		watcher.reset();

		list.remove( child2 );
		assertEventCounts( watcher, 1, 1, 0, 0, 1 );
		watcher.reset();
	}

	@Test
	public void testChildAddedEventFiredByChild() {
		MockDataList node = new MockDataList();
		MockDataList child = new MockDataList();
		DataEventWatcher watcher = node.getDataEventWatcher();

		node.setModified( false );
		assertFalse( node.isModified(), "The node should not be modified." );
		watcher.reset();

		node.add( child );
		assertEventCounts( watcher, 1, 1, 0, 1, 0 );
		watcher.reset();

		node.remove( child );
		assertEventCounts( watcher, 1, 1, 0, 0, 1 );
		watcher.reset();
	}

	@Test
	public void testEquals() {
		MockDataList node1;
		MockDataList node2;
		MockDataNode child1;
		MockDataNode child2;

		node1 = new MockDataList();
		node2 = new MockDataList();
		assertFalse( node1.equals( node2 ) );
		assertFalse( node2.equals( node1 ) );

		node1 = new MockDataList();
		node2 = new MockDataList();
		node1.setAttribute( "key", "value" );
		node2.setAttribute( "key", "value" );
		assertFalse( node1.equals( node2 ) );
		assertFalse( node2.equals( node1 ) );

		node1 = new MockDataList();
		node2 = new MockDataList();
		child1 = new MockDataNode();
		child2 = new MockDataNode();
		child1.setAttribute( "key", "a" );
		child2.setAttribute( "key", "a" );
		node1.add( child1 );
		node2.add( child2 );
		assertFalse( node1.equals( node2 ) );
		assertFalse( node2.equals( node1 ) );

		child1.setAttribute( "key", "a" );
		child2.setAttribute( "key", "b" );
		assertFalse( node1.equals( node2 ) );
		assertFalse( node2.equals( node1 ) );
	}

	@Test
	public void testEqualsUsingChildren() {
		MockDataList node1;
		MockDataList node2;
		MockDataNode child1;
		MockDataNode child2;

		node1 = new MockDataList();
		node2 = new MockDataList();
		assertTrue( node1.equalsUsingChildren( node2 ) );
		assertTrue( node2.equalsUsingChildren( node1 ) );

		node1 = new MockDataList();
		node2 = new MockDataList();
		node1.setAttribute( "key", "value" );
		node2.setAttribute( "key", "value" );
		assertTrue( node1.equalsUsingChildren( node2 ) );
		assertTrue( node2.equalsUsingChildren( node1 ) );

		node1 = new MockDataList();
		node2 = new MockDataList();
		child1 = new MockDataNode();
		child2 = new MockDataNode();
		child1.setAttribute( "key", "a" );
		child2.setAttribute( "key", "a" );
		node1.add( child1 );
		node2.add( child2 );
		assertTrue( node1.equalsUsingChildren( node2 ) );
		assertTrue( node2.equalsUsingChildren( node1 ) );

		child1.setAttribute( "key", "a" );
		child2.setAttribute( "key", "b" );
		assertFalse( node1.equalsUsingChildren( node2 ) );
		assertFalse( node2.equalsUsingChildren( node1 ) );
	}

	@Test
	public void testEqualsUsingAttributesAndChildren() {
		MockDataList node1;
		MockDataList node2;
		MockDataNode child1;
		MockDataNode child2;

		node1 = new MockDataList();
		node2 = new MockDataList();
		assertTrue( node1.equalsUsingAttributesAndChildren( node2 ) );
		assertTrue( node2.equalsUsingAttributesAndChildren( node1 ) );

		node1 = new MockDataList();
		node2 = new MockDataList();
		node1.setAttribute( "key", "value" );
		node2.setAttribute( "key", "value" );
		assertTrue( node1.equalsUsingAttributesAndChildren( node2 ) );
		assertTrue( node2.equalsUsingAttributesAndChildren( node1 ) );

		node1 = new MockDataList();
		node2 = new MockDataList();
		child1 = new MockDataNode();
		child2 = new MockDataNode();
		child1.setAttribute( "key", "a" );
		child2.setAttribute( "key", "a" );
		node1.add( child1 );
		node2.add( child2 );
		assertTrue( node1.equalsUsingAttributesAndChildren( node2 ) );
		assertTrue( node2.equalsUsingAttributesAndChildren( node1 ) );

		child1.setAttribute( "key", "a" );
		child2.setAttribute( "key", "b" );
		assertFalse( node1.equalsUsingAttributesAndChildren( node2 ) );
		assertFalse( node2.equalsUsingAttributesAndChildren( node1 ) );
	}

}
