package com.parallelsymmetry.data;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class DataListTest extends BaseTestCase {

	@Test
	public void testConstructorWithChildren() {
		MockDataList child1 = new MockDataList();
		MockDataList child2 = new MockDataList();

		MockDataList[] children = new MockDataList[2];
		children[0] = child1;
		children[1] = child2;

		DataList<MockDataList> parent = new DataList<MockDataList>( children );

		assertFalse( parent.isModified() );
		assertEquals( child1, parent.get( 0 ) );
		assertEquals( child2, parent.get( 1 ) );
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
	public void testIsNewNodeModified() {
		MockDataList node = new MockDataList();
		assertFalse( "New node should not be modified.", node.isModified() );
	}

	@Test
	public void testIsModifiedBySetModified() {
		MockDataList node = new MockDataList();

		node.setModified( true );
		assertTrue( "Node should be modified after setting modified to true.", node.isModified() );
		node.setModified( false );
		assertFalse( "Node should not be modified after setting modified to false.", node.isModified() );
	}

	@Test
	public void testIsModifiedByAttributes() {
		MockDataList node = new MockDataList();
		String key = "key";
		Object value = new Object();

		node.setAttribute( key, value );
		assertTrue( "Node not modified after setting attribute.", node.isModified() );

		node.setAttribute( key, null );
		assertFalse( "Node still modified after removing attribute.", node.isModified() );
	}

	@Test
	public void testIsModifiedByChildren() {
		MockDataList node = new MockDataList();
		MockDataList child = new MockDataList();

		node.add( child );
		assertTrue( "Node not modified after child addition.", node.isModified() );

		node.remove( child );
		assertFalse( "Node still modified after child removal.", node.isModified() );
	}

	@Test
	public void testIsModifiedByNullChild() {
		MockDataList node = new MockDataList();
		assertFalse( "New node should not be modified.", node.isModified() );
		node.add( null );
		assertFalse( "Addition of null child should not modify node.", node.isModified() );
		node.add( 0, null );
		assertFalse( "Addition of null child should not modify node.", node.isModified() );
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
	}

	@Test
	public void testSetModifiedWithAttributes() {
		MockDataList node = new MockDataList();

		node.setAttribute( "1", "one" );
		assertTrue( "Node should be self modified by setting an attribute.", node.isSelfModified() );

		node.setModified( false );
		assertFalse( "Node should not be self modified after setting modified to false.", node.isSelfModified() );

		node.setAttribute( "2", "two" );
		assertTrue( "Node should be self modified by setting an attribute.", node.isSelfModified() );

		node.setAttribute( "2", null );
		assertFalse( "Node should not be self modified after removing attribute that caused modification.", node.isSelfModified() );
	}

	@Test
	public void testSetModifiedWithChildren() {
		MockDataList node = new MockDataList();
		assertFalse( node.isModified() );

		node.add( new MockDataList() );
		assertTrue( node.isModified() );
		assertFalse( node.isSelfModified() );
		assertTrue( node.isTreeModified() );

		node.add( new MockDataList() );
		assertTrue( node.isModified() );
		assertFalse( node.isSelfModified() );
		assertTrue( node.isTreeModified() );

		node.setModified( false );
		assertFalse( node.isModified() );
	}

	@Test
	public void testSetModifiedWithChildrenInAttributes() {
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> list = new MockDataList();
		DataList<DataNode> child = new MockDataList();

		node.setAttribute( "list", list );
		node.setModified( false );
		assertFalse( "The node should not be modified.", node.isModified() );

		list.add( child );
		assertTrue( "Addition of the child from the list should modify the node.", node.isModified() );

		node.setModified( false );
		assertFalse( "Setting the node modified flag to false should unmodify all children and attriutes.", node.isModified() );
	}

	@Test
	public void testIsTreeModified() throws Exception {
		MockDataList node = new MockDataList();
		MockDataList child = new MockDataList();
		MockDataList grandchild = new MockDataList();

		assertFalse( node.isModified() );
		node.add( child );
		assertFalse( node.isSelfModified() );
		assertTrue( node.isTreeModified() );
		node.remove( child );
		assertFalse( node.isSelfModified() );
		assertFalse( node.isTreeModified() );

		node.add( child );
		child.add( grandchild );
		grandchild.setAttribute( "key", "value" );
		assertTrue( "Grandchild node should be modified.", grandchild.isModified() );
		assertTrue( "Child node should be modified.", child.isModified() );
		assertTrue( "Node should be modified.", node.isModified() );

		//System.out.println( "Node child modified count: " + Accessor.getField( node, "modifiedChildren" ) );
		//System.out.println( "Child child modified count: " + Accessor.getField( child, "modifiedChildren" ) );
		//System.out.println( "Grandchild child modified count: " + Accessor.getField( grandchild, "modifiedChildren" ) );

		node.setModified( false );

		//System.out.println( "Node child modified count: " + Accessor.getField( node, "modifiedChildren" ) );
		//System.out.println( "Child child modified count: " + Accessor.getField( child, "modifiedChildren" ) );
		//System.out.println( "Grandchild child modified count: " + Accessor.getField( grandchild, "modifiedChildren" ) );

		assertFalse( "Grandchild node should not be modified.", grandchild.isModified() );
		assertFalse( "Child node should not be modified.", child.isModified() );
		assertFalse( "Node should not be modified.", node.isModified() );
	}

	@Test
	public void testGetChildCount() {
		MockDataList node = new MockDataList();
		assertEquals( 0, node.size() );
		node.add( new MockDataList() );
		assertEquals( 1, node.size() );
		node.add( new MockDataList() );
		assertEquals( 2, node.size() );
		node.remove( 0 );
		assertEquals( 1, node.size() );
		node.remove( 0 );
		assertEquals( 0, node.size() );
	}

	@Test
	public void testGetParent() {
		MockDataList node = new MockDataList();
		MockDataList child = new MockDataList();
		assertNull( child.getParent() );

		node.add( child );
		assertEquals( node, child.getParent() );

		node.remove( child );
		assertNull( child.getParent() );
	}

	@Test
	public void testGet() {
		MockDataList parent = new MockDataList();
		MockDataList child = new MockDataList();

		try {
			parent.get( 0 );
			fail();
		} catch( ArrayIndexOutOfBoundsException exception ) {
			// This exception should occur.
		}

		parent.add( child );
		assertEquals( child, parent.get( 0 ) );
	}

	@Test
	public void testGetIndex() {
		MockDataList parent = new MockDataList();
		MockDataList child = new MockDataList();
		assertEquals( -1, parent.getIndex( child ) );

		parent.add( child );
		assertEquals( 0, parent.getIndex( child ) );
	}

	@Test
	public void testAdd() {
		MockDataList node = new MockDataList();
		MockDataList child0 = new MockDataList();
		MockDataList child1 = new MockDataList();
		MockDataList child2 = new MockDataList();

		node.add( child0 );
		assertEquals( node, child0.getParent() );
		assertSame( child0, node.get( 0 ) );

		node.add( child1 );
		assertEquals( node, child1.getParent() );
		assertSame( child1, node.get( 1 ) );

		node.add( child2 );
		assertEquals( node, child2.getParent() );
		assertSame( child2, node.get( 2 ) );
	}

	@Test
	public void testAddUsingTransaction() {
		MockDataList node = new MockDataList();
		MockDataNode child0 = new MockDataNode();
		MockDataNode child1 = new MockDataNode();
		MockDataNode child2 = new MockDataNode();

		node.startTransaction();
		node.add( child0 );
		node.add( child1 );
		node.add( child2 );
		node.commitTransaction();

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
		node0.setModified( false );
		assertFalse( node0.isModified() );
		assertEquals( 1, node0.size() );

		node1.add( child );
		assertTrue( node0.isModified() );
		assertEquals( 0, node0.size() );
		assertEquals( 1, node1.size() );
	}

	@Test
	public void testAddWithUsedAttributeNode() {
		String key = "key";
		MockDataList node0 = new MockDataList();
		MockDataList node1 = new MockDataList();
		MockDataList child = new MockDataList();

		node0.setAttribute( key, child );
		node0.setModified( false );
		assertFalse( node0.isModified() );
		assertEquals( child, node0.getAttribute( key ) );

		node1.add( child );
		assertTrue( node0.isModified() );
		assertNull( node0.getAttribute( key ) );
		assertEquals( 0, node0.size() );

		assertTrue( node1.isModified() );
		assertEquals( child, node1.get( 0 ) );
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
	public void testRemoveWithNode() {
		MockDataList node = new MockDataList();
		MockDataList child = new MockDataList();

		node.remove( null );
		assertEquals( 0, node.size() );

		node.add( child );
		assertEquals( node, child.getParent() );
		assertEquals( child, node.get( 0 ) );

		node.remove( child );
		assertNull( child.getParent() );
		assertEquals( 0, node.size() );
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
	public void testClear() {
		MockDataList list = new MockDataList();
		list.add( new MockDataNode( "0" ) );
		list.add( new MockDataNode( "1" ) );
		list.add( new MockDataNode( "2" ) );

		assertEquals( 3, list.size() );
		list.clear();
		assertEquals( 0, list.size() );
	}

	@Test
	public void testToArray() {
		DataList<MockDataList> node = new DataList<MockDataList>();
		MockDataList[] array = new MockDataList[0];
		assertEquals( array, node.toArray( array ) );

		node.add( new MockDataList() );
		assertEquals( 1, node.toArray( array ).length );
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
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> attributeNode = new MockDataList();
		assertFalse( node.isModified() );
		assertFalse( attributeNode.isModified() );

		node.setAttribute( "node", attributeNode );
		assertTrue( node.isModified() );
		assertFalse( attributeNode.isModified() );

		node.setModified( false );
		assertFalse( node.isModified() );
		assertFalse( attributeNode.isModified() );

		attributeNode.setModified( true );
		assertTrue( node.isModified() );
		assertTrue( attributeNode.isModified() );
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

		node.setModified( false );
		assertFalse( node.isModified() );
		assertFalse( attributeNode.isModified() );

		attributeNode.setModified( true );
		assertTrue( node.isModified() );
		assertTrue( attributeNode.isModified() );

		attributeNode.setModified( false );
		assertFalse( node.isModified() );
		assertFalse( attributeNode.isModified() );
	}

	@Test
	public void testNodeModifiedByAdditionOfChildInDataNodeAttribute() {
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> list = new MockDataList();
		DataList<DataNode> child = new MockDataList();

		node.setAttribute( "list", list );
		node.setModified( false );
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
		node.setModified( false );
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
		node.setModified( false );
		assertFalse( "The node should not be modified.", node.isModified() );

		child.setAttribute( "key", "value" );
		assertTrue( child.isModified() );
		assertTrue( list.isModified() );
		assertTrue( node.isModified() );
	}

	public void testDataChangedEventTriggering() throws Exception {
		MockDataList parent = new MockDataList();
		MockDataNode child = new MockDataNode();
		parent.add( child );
		parent.setModified( false );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );

		DataWatcher watcher = new DataWatcher();
		parent.addDataListener( watcher );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		assertEquals( 0, watcher.getDataChangedEvents().length );
		watcher.reset();

		child.setAttribute( "key1", "value1" );
		watcher.assertEventCounts( 1, 1, 1, 0, 0 );
		watcher.reset();

		child.setAttribute( "key1", "value1" );
		watcher.assertEventCounts( 0, 0, 0, 0, 0 );
		watcher.reset();

		child.setAttribute( "key1", "value2" );
		watcher.assertEventCounts( 1, 0, 1, 0, 0 );
		watcher.reset();
	}

	@Test
	public void testDataChangedEventFiredFromChildAttributeNode() throws Exception {
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> attribute = new MockDataList();

		DataWatcher watcher = new DataWatcher();
		node.addDataListener( watcher );

		// Set the attribute.
		assertFalse( node.isModified() );
		node.setAttribute( "attribute", attribute );
		assertTrue( node.isModified() );
		watcher.assertEventCounts( 1, 1, 1, 0, 0 );
		watcher.reset();

		// Setting an attribute on the attribute node should cause a data change event.
		attribute.setAttribute( "key", "value1" );
		watcher.assertEventCounts( 1, 1, 1, 0, 0 );
		watcher.reset();

		// Setting another attribute on the attribute node should cause a data change event.
		attribute.setAttribute( "key", "value2" );
		watcher.assertEventCounts( 1, 0, 1, 0, 0 );
		watcher.reset();
	}

	@Test
	public void testDataChangedEventFiredByModifyFlagOfChildInDataNodeAttribute() {
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> list = new MockDataList();
		DataList<DataNode> child = new MockDataList();
		DataList<DataNode> attribute = new MockDataList();

		node.setAttribute( "list", list );
		list.add( child );
		child.setAttribute( "attribute", attribute );
		node.setModified( false );
		assertFalse( "The node should not be modified.", node.isModified() );
		assertFalse( "The list should not be modified.", list.isModified() );
		assertFalse( "The child should not be modified.", child.isModified() );
		assertFalse( "The attribute should not be modified.", attribute.isModified() );

		DataWatcher watcher = new DataWatcher();
		node.addDataListener( watcher );

		attribute.setModified( true );
		watcher.assertEventCounts( 1, 1, 0, 0, 0 );
		watcher.reset();

		attribute.setModified( true );
		watcher.assertEventCounts( 0, 0, 0, 0, 0 );
		watcher.reset();

		attribute.setModified( false );
		watcher.assertEventCounts( 1, 1, 0, 0, 0 );
		watcher.reset();
	}

	@Test
	public void testDataChangedEventFiredByAttributeModifyOfChildInDataNodeAttribute() {
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> list = new MockDataList();
		DataList<DataNode> child = new MockDataList();
		DataList<DataNode> attribute = new MockDataList();

		node.setAttribute( "list", list );
		list.add( child );
		child.setAttribute( "attribute", attribute );
		node.setModified( false );
		assertFalse( "The node should not be modified.", node.isModified() );
		assertFalse( "The list should not be modified.", list.isModified() );
		assertFalse( "The child should not be modified.", child.isModified() );
		assertFalse( "The attribute should not be modified.", attribute.isModified() );

		DataWatcher watcher = new DataWatcher();
		node.addDataListener( watcher );

		attribute.setAttribute( "key", "value1" );
		watcher.assertEventCounts( 1, 1, 1, 0, 0 );
		watcher.reset();

		attribute.setAttribute( "key", "value1" );
		watcher.assertEventCounts( 0, 0, 0, 0, 0 );
		watcher.reset();

		attribute.setAttribute( "key", "value2" );
		watcher.assertEventCounts( 1, 0, 1, 0, 0 );
		watcher.reset();
	}

	@Test
	public void testDataChangedEventFiredByAttributeModifyInTransaction() {
		MockDataList node = new MockDataList();
		DataWatcher watcher = new DataWatcher();
		node.addDataListener( watcher );

		node.startTransaction();
		node.setAttribute( "key1", "value1" );
		node.setAttribute( "key2", "value2" );
		node.commitTransaction();

		watcher.assertEventCounts( 1, 1, 2, 0, 0 );
		assertEquals( "key1", watcher.getAttributeChangedEvents()[0].getKey() );
		assertEquals( "key2", watcher.getAttributeChangedEvents()[1].getKey() );
		watcher.reset();

		node.startTransaction();
		node.setAttribute( "key1", null );
		node.setAttribute( "key2", null );
		node.commitTransaction();

		watcher.assertEventCounts( 1, 1, 2, 0, 0 );
		assertEquals( "key1", watcher.getAttributeChangedEvents()[0].getKey() );
		assertEquals( "key2", watcher.getAttributeChangedEvents()[1].getKey() );
		watcher.reset();
	}

	@Test
	public void testNodeModifiedEventFiredByAttribute() {
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> list = new MockDataList();

		node.setModified( false );
		list.setModified( false );
		assertFalse( "The node should not be modified.", node.isModified() );
		assertFalse( "The list should not be modified.", list.isModified() );

		DataWatcher watcher = new DataWatcher();
		node.addDataListener( watcher );

		node.setAttribute( "list", list );
		watcher.assertEventCounts( 1, 1, 1, 0, 0 );
		watcher.reset();

		node.setAttribute( "list", null );
		watcher.assertEventCounts( 1, 1, 1, 0, 0 );
		watcher.reset();
	}

	@Test
	public void testNodeModifiedEventFiredByAttributeChild() {
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> list = new MockDataList();
		DataList<DataNode> child = new MockDataList();

		node.setAttribute( "list", list );
		node.setModified( false );
		assertFalse( "The node should not be modified.", node.isModified() );
		assertFalse( "The list should not be modified.", list.isModified() );

		DataWatcher watcher = new DataWatcher();
		node.addDataListener( watcher );

		list.add( child );
		watcher.assertEventCounts( 1, 1, 0, 1, 0 );
		watcher.reset();

		list.remove( child );
		watcher.assertEventCounts( 1, 1, 0, 0, 1 );
		watcher.reset();
	}

	@Test
	public void testNodeModifiedEventFiredByChildAttributeChild() {
		MockDataList node = new MockDataList();
		MockDataList child1 = new MockDataList();
		MockDataList list = new MockDataList();
		MockDataList child2 = new MockDataList();

		node.add( child1 );
		child1.setAttribute( "list", list );
		node.setModified( false );
		assertFalse( "The node should not be modified.", node.isModified() );
		assertFalse( "The child1 should not be modified.", child1.isModified() );
		assertFalse( "The list should not be modified.", list.isModified() );

		DataWatcher watcher = new DataWatcher();
		node.addDataListener( watcher );

		list.add( child2 );
		watcher.assertEventCounts( 1, 1, 0, 1, 0 );
		watcher.reset();

		list.remove( child2 );
		watcher.assertEventCounts( 1, 1, 0, 0, 1 );
		watcher.reset();
	}

	@Test
	public void testChildAddedEventFiredByChild() {
		DataList<DataNode> node = new MockDataList();
		DataList<DataNode> child = new MockDataList();

		node.setModified( false );
		assertFalse( "The node should not be modified.", node.isModified() );

		DataWatcher watcher = new DataWatcher();
		node.addDataListener( watcher );

		node.add( child );
		watcher.assertEventCounts( 1, 1, 0, 1, 0 );
		watcher.reset();

		node.remove( child );
		watcher.assertEventCounts( 1, 1, 0, 0, 1 );
		watcher.reset();
	}

	@Test
	public void testTransactionByModifyingChild() {
		MockDataList parent = new MockDataList();
		MockDataNode child = new MockDataNode();
		DataWatcher watcher = new DataWatcher();

		parent.add( child );
		parent.setModified( false );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );

		parent.addDataListener( watcher );

		parent.startTransaction();
		child.setAttribute( "key1", "value1" );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		watcher.assertEventCounts( 0, 0, 0, 0, 0 );
		watcher.reset();

		parent.commitTransaction();
		assertTrue( parent.isModified() );
		assertTrue( child.isModified() );
		watcher.assertEventCounts( 1, 1, 1, 0, 0 );
		watcher.reset();

		parent.startTransaction();
		child.setAttribute( "key1", null );
		watcher.assertEventCounts( 0, 0, 0, 0, 0 );
		watcher.reset();

		parent.commitTransaction();
		watcher.assertEventCounts( 1, 1, 1, 0, 0 );
		watcher.reset();
	}

	@Test
	public void testTransactionByModifyingGrandChild() {
		MockDataList parent = new MockDataList( "parent" );
		MockDataList child = new MockDataList( "child" );
		MockDataNode grandchild = new MockDataNode( "grandchild" );

		parent.add( child );
		child.add( grandchild );
		parent.setModified( false );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		assertFalse( grandchild.isModified() );

		DataWatcher watcher = new DataWatcher();
		parent.addDataListener( watcher );

		parent.startTransaction();
		grandchild.setAttribute( "key1", "value1" );
		watcher.assertEventCounts( 0, 0, 0, 0, 0 );
		watcher.reset();

		parent.commitTransaction();
		watcher.assertEventCounts( 1, 1, 1, 0, 0 );
		watcher.reset();

		parent.startTransaction();
		grandchild.setAttribute( "key1", null );
		watcher.assertEventCounts( 0, 0, 0, 0, 0 );
		watcher.reset();

		parent.commitTransaction();
		watcher.assertEventCounts( 1, 1, 1, 0, 0 );
		watcher.reset();
	}

	@Test
	public void testEquals() {
		MockDataList node1 = null;
		MockDataList node2 = null;
		MockDataNode child1 = null;
		MockDataNode child2 = null;

		node1 = new MockDataList();
		node2 = new MockDataList();
		assertTrue( node1.equals( node2 ) );
		assertTrue( node2.equals( node1 ) );

		node1 = new MockDataList();
		node2 = new MockDataList();
		node1.setAttribute( "key", "value" );
		node2.setAttribute( "key", "value" );
		assertTrue( node1.equals( node2 ) );
		assertTrue( node2.equals( node1 ) );

		node1 = new MockDataList();
		node2 = new MockDataList();
		child1 = new MockDataNode();
		child2 = new MockDataNode();
		child1.setAttribute( "key", "a" );
		child2.setAttribute( "key", "a" );
		node1.add( child1 );
		node2.add( child2 );
		assertTrue( node1.equals( node2 ) );
		assertTrue( node2.equals( node1 ) );

		child1.setAttribute( "key", "a" );
		child2.setAttribute( "key", "b" );
		assertFalse( node1.equals( node2 ) );
		assertFalse( node2.equals( node1 ) );
	}

	@Test
	public void testEqualsUsingChildren() {
		MockDataList node1 = null;
		MockDataList node2 = null;
		MockDataNode child1 = null;
		MockDataNode child2 = null;

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
		MockDataList node1 = null;
		MockDataList node2 = null;
		MockDataNode child1 = null;
		MockDataNode child2 = null;

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
