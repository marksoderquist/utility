package com.parallelsymmetry.data;

import java.util.Collection;

import org.junit.Test;

import com.parallelsymmetry.util.Accessor;

public class DataNodeTest extends BaseTestCase {

	@Test
	public void testAttributes() {
		String key = "key";
		Object value = "value";

		MockDataNode node = new MockDataNode();
		assertNull( "Missing attribute should be null.", node.getAttribute( key ) );

		node.setAttribute( key, value );
		assertEquals( "Attribute value incorrect", value, node.getAttribute( key ) );

		node.setAttribute( key, null );
		assertNull( "Removed attribute should be null.", node.getAttribute( key ) );
	}

	@Test
	public void testSelfKeyedAttribute() {
		MockDataNode node = new MockDataNode();
		try {
			node.setAttribute( "key", "key" );
			fail( "DataNode.setAttribute() should not allow the key and value to be the same." );
		} catch( RuntimeException exception ) {}
	}

	@Test
	public void testObjectAttribute() {
		String key = "key";
		Object value = new Object();
		MockDataNode node = new MockDataNode();
		node.setAttribute( key, value );
		Object check = node.getAttribute( key );
		assertEquals( "Object value not equal", value, check );
	}

	@Test
	public void testStringAttribute() {
		String key = "key";
		String value = "value";
		MockDataNode node = new MockDataNode();
		node.setAttribute( key, value );
		String check = (String)node.getAttribute( key );
		assertEquals( "String value not equal", value, check );
	}

	@Test
	public void testBooleanAttribute() {
		String key = "key";
		boolean value = true;
		MockDataNode node = new MockDataNode();
		node.setAttribute( key, value );
		boolean check = (Boolean)node.getAttribute( key );
		assertEquals( "Integer value not equal", value, check );
	}

	@Test
	public void testIntegerAttribute() {
		String key = "key";
		int value = 0;
		MockDataNode node = new MockDataNode();
		node.setAttribute( key, value );
		int check = (Integer)node.getAttribute( key );
		assertEquals( "Integer value not equal", value, check );
	}

	@Test
	public void testSetAttributeWithUsedChildNode() {
		String key = "key";
		MockDataList node0 = new MockDataList();
		MockDataList node1 = new MockDataList();
		MockDataList child = new MockDataList();

		node0.add( child );
		node0.setModified( false );
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
		node0.setModified( false );
		assertFalse( node0.isModified() );
		assertEquals( child, node0.getAttribute( key ) );

		node1.setAttribute( key, child );
		assertTrue( node0.isModified() );
		assertEquals( 0, node0.size() );
		assertEquals( child, node1.getAttribute( key ) );
	}

	@Test
	public void testIsNewNodeModified() {
		MockDataNode node = new MockDataNode();
		assertFalse( "New node should not be modified.", node.isModified() );
	}

	@Test
	public void testIsModifiedBySetModified() {
		MockDataNode node = new MockDataNode();

		node.setModified( true );
		assertTrue( "Node should be modified after setting modified to true.", node.isModified() );
		node.setModified( false );
		assertFalse( "Node should not be modified after setting modified to false.", node.isModified() );
	}

	@Test
	public void testIsModifiedByAttributes() {
		MockDataNode node = new MockDataNode();
		String key = "key";
		Object value = new Object();

		node.setAttribute( key, value );
		assertTrue( "Node not modified after setting attribute.", node.isModified() );

		node.setAttribute( key, null );
		assertFalse( "Node still modified after removing attribute.", node.isModified() );
	}

	@Test
	public void testIsSelfModified() {
		MockDataNode node = new MockDataNode();
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
		MockDataNode node = new MockDataNode();

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
	public void testSetModifiedWithChildrenInAttributes() {
		DataNode node = new MockDataNode();
		DataList<DataNode> list = new MockDataList();
		DataNode child = new MockDataNode();

		node.setAttribute( "list", list );
		node.setModified( false );
		assertFalse( "The node should not be modified.", node.isModified() );

		list.add( child );
		assertTrue( "Addition of the child from the list should modify the node.", node.isModified() );

		node.setModified( false );
		assertFalse( "Setting the node modified flag to false should unmodify all children and attriutes.", node.isModified() );
	}

	@Test
	public void testGetParent() {
		MockDataNode node = new MockDataNode();
		MockDataNode child = new MockDataNode();
		assertNull( child.getParent() );

		String key = "key";

		node.setAttribute( key, child );
		assertEquals( node, child.getParent() );

		node.setAttribute( key, null );
		assertNull( child.getParent() );
	}

	@Test
	public void testGetTreePath() {
		MockDataList list = new MockDataList();
		MockDataNode child = new MockDataNode();

		list.add( child );

		DataNode[] path = list.getTreePath();
		assertEquals( 1, path.length );
		assertEquals( list, path[0] );

		DataNode[] childPath = child.getTreePath();
		assertEquals( 2, childPath.length );
		assertEquals( list, childPath[0] );
		assertEquals( child, childPath[1] );
	}

	@Test
	public void testGetTreePathWithNode() {
		MockDataList list = new MockDataList();
		MockDataList child = new MockDataList();
		MockDataList grandchild = new MockDataList();

		list.add( child );
		child.add( grandchild );

		DataNode[] path = list.getTreePath( list );
		assertEquals( 1, path.length );
		assertEquals( list, path[0] );

		path = child.getTreePath( child );
		assertEquals( 1, path.length );
		assertEquals( child, path[0] );

		path = grandchild.getTreePath( child );
		assertEquals( 2, path.length );
		assertEquals( child, path[0] );
		assertEquals( grandchild, path[1] );
	}

	@Test
	public void testNodeModifiedByModifedNodeInAttributeMap() {
		DataNode node = new MockDataNode();
		DataNode attributeNode = new MockDataNode();
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
		DataNode node = new MockDataNode();
		DataNode attributeNode = new MockDataNode();
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
	public void testAddDataListener() throws Exception {
		DataNode node = new MockDataNode();
		DataWatcher watcher = new DataWatcher();
		assertNull( Accessor.getField( node, "listeners" ) );

		node.addDataListener( watcher );
		Collection<DataListener> listeners = Accessor.getField( node, "listeners" );
		assertNotNull( listeners );
		assertEquals( 1, listeners.size() );
	}

	@Test
	public void testRemoveDataListener() throws Exception {
		DataNode node = new MockDataNode();
		DataWatcher watcher = new DataWatcher();
		node.removeDataListener( watcher );
		node.addDataListener( watcher );
		Collection<DataListener> listeners = Accessor.getField( node, "listeners" );
		assertNotNull( listeners );
		assertEquals( 1, listeners.size() );

		node.removeDataListener( watcher );
		assertNull( Accessor.getField( node, "listeners" ) );
	}

	@Test
	public void testTransaction() throws Exception {
		MockDataNode node = new MockDataNode();
		DataWatcher watcher = new DataWatcher();
		node.addDataListener( watcher );

		node.startTransaction();
		node.setAttribute( "key1", "value1" );
		node.setAttribute( "key2", "value2" );
		node.setAttribute( "key3", "value3" );
		watcher.assertEventCounts( 0, 0, 0, 0, 0 );
		watcher.reset();
		assertNull( node.getAttribute( "key1" ) );
		assertNull( node.getAttribute( "key2" ) );
		assertNull( node.getAttribute( "key3" ) );

		node.commitTransaction();
		watcher.assertEventCounts( 1, 1, 3, 0, 0 );
		watcher.reset();
		assertEquals( "value1", node.getAttribute( "key1" ) );
		assertEquals( "value2", node.getAttribute( "key2" ) );
		assertEquals( "value3", node.getAttribute( "key3" ) );

		node.startTransaction();
		node.setAttribute( "key1", null );
		node.setAttribute( "key2", null );
		node.setAttribute( "key3", null );
		watcher.assertEventCounts( 0, 0, 0, 0, 0 );
		watcher.reset();
		assertEquals( "value1", node.getAttribute( "key1" ) );
		assertEquals( "value2", node.getAttribute( "key2" ) );
		assertEquals( "value3", node.getAttribute( "key3" ) );

		node.commitTransaction();
		watcher.assertEventCounts( 1, 1, 3, 0, 0 );
		watcher.reset();
		assertNull( node.getAttribute( "key1" ) );
		assertNull( node.getAttribute( "key2" ) );
		assertNull( node.getAttribute( "key3" ) );
	}

	@Test
	public void testEquals() {
		MockDataNode node1 = null;
		MockDataNode node2 = null;

		node1 = new MockDataNode();
		node2 = new MockDataNode();
		assertTrue( node1.equals( node2 ) );
		assertTrue( node2.equals( node1 ) );

		node1 = new MockDataNode();
		node2 = new MockDataNode();
		node1.setAttribute( "key", "value" );
		node2.setAttribute( "key", "value" );
		assertTrue( node1.equals( node2 ) );
		assertTrue( node2.equals( node1 ) );

		node1.setAttribute( "key", "a" );
		node2.setAttribute( "key", "b" );
		assertFalse( node1.equals( node2 ) );
		assertFalse( node2.equals( node1 ) );
	}

	@Test
	public void testEqualsUsingAttributesAndChildren() {
		MockDataNode node1 = null;
		MockDataNode node2 = null;

		node1 = new MockDataNode();
		node2 = new MockDataNode();
		assertTrue( node1.equalsUsingAttributes( node2 ) );
		assertTrue( node2.equalsUsingAttributes( node1 ) );

		node1 = new MockDataNode();
		node2 = new MockDataNode();
		node1.setAttribute( "key", "value" );
		node2.setAttribute( "key", "value" );
		assertTrue( node1.equalsUsingAttributes( node2 ) );
		assertTrue( node2.equalsUsingAttributes( node1 ) );

		node1.setAttribute( "key", "a" );
		node2.setAttribute( "key", "b" );
		assertFalse( node1.equalsUsingAttributes( node2 ) );
		assertFalse( node2.equalsUsingAttributes( node1 ) );
	}

}
