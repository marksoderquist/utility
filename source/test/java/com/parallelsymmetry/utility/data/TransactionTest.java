package com.parallelsymmetry.utility.data;

import org.junit.Test;

import com.parallelsymmetry.utility.ObjectUtil;
import com.parallelsymmetry.utility.mock.DataEventWatcher;
import com.parallelsymmetry.utility.mock.MockDataList;
import com.parallelsymmetry.utility.mock.MockDataNode;

public class TransactionTest extends DataTestCase {

	@Test
	public void testTransaction() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = new DataEventWatcher();
		data.addDataListener( handler );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		Transaction transaction = new Transaction();

		transaction.setAttribute( data, "attribute0", "value0" );
		transaction.setAttribute( data, "attribute1", "value1" );
		transaction.setAttribute( data, "attribute2", "value2" );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		transaction.commit();
		assertNodeState( data, true, 3 );
		assertEventCounts( handler, 1, 1, 3 );

		int index = 0;
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, data, data, "attribute0", null, "value0" );
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, data, data, "attribute1", null, "value1" );
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, data, data, "attribute2", null, "value2" );
		assertEventState( handler, index++, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, data, DataNode.MODIFIED, false, true );
		assertEventState( handler, index++, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

	@Test
	public void testReuseTransaction() throws Exception {
		MockDataNode node = new MockDataNode();
		DataEventWatcher watcher = node.getDataEventWatcher();

		Transaction transaction = new Transaction();

		transaction.setAttribute( node, "key1", "value1" );
		assertEventCounts( watcher, 0, 0, 0 );
		transaction.commit();
		assertEventCounts( watcher, 1, 1, 1 );
		assertEquals( "value1", node.getAttribute( "key1" ) );
		watcher.reset();

		transaction.setAttribute( node, "key1", "value2" );
		assertEventCounts( watcher, 0, 0, 0 );
		transaction.commit();
		assertEquals( "value2", node.getAttribute( "key1" ) );
		assertEventCounts( watcher, 1, 0, 1 );
	}

	@Test
	public void testOverlappingTransactions() throws Exception {
		MockDataNode node = new MockDataNode();
		DataEventWatcher watcher = node.getDataEventWatcher();

		// Initial transaction.
		Transaction transaction0 = new Transaction();
		transaction0.setAttribute( node, "key1", "value1" );
		transaction0.setAttribute( node, "key2", "value2" );
		assertEventCounts( watcher, 0, 0, 0, 0, 0 );
		assertNull( node.getAttribute( "key1" ) );
		assertNull( node.getAttribute( "key2" ) );
		assertNull( node.getAttribute( "key3" ) );

		// Overlapping transaction.
		Transaction transaction1 = new Transaction();
		transaction1.setAttribute( node, "key3", "value3" );
		assertEventCounts( watcher, 0, 0, 0, 0, 0 );
		assertNull( node.getAttribute( "key1" ) );
		assertNull( node.getAttribute( "key2" ) );
		assertNull( node.getAttribute( "key3" ) );

		// Overlapping commit.
		transaction1.commit();
		assertEventCounts( watcher, 1, 1, 1, 0, 0 );
		assertNull( node.getAttribute( "key1" ) );
		assertNull( node.getAttribute( "key2" ) );
		assertEquals( "value3", node.getAttribute( "key3" ) );
		watcher.reset();

		// Final commit.
		transaction0.commit();
		assertEventCounts( watcher, 1, 0, 2, 0, 0 );
		assertEquals( "value1", node.getAttribute( "key1" ) );
		assertEquals( "value2", node.getAttribute( "key2" ) );
		assertEquals( "value3", node.getAttribute( "key3" ) );
		watcher.reset();
	}

	@Test
	public void testSetAttributeToSameValue() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = new DataEventWatcher();
		data.addDataListener( handler );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		Transaction transaction = new Transaction();

		transaction.setAttribute( data, "attribute0", "value0" );
		transaction.setAttribute( data, "attribute1", "value1" );
		transaction.setAttribute( data, "attribute2", "value2" );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		transaction.commit();
		assertNodeState( data, true, 3 );
		assertEventCounts( handler, 1, 1, 3 );

		int index = 0;
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, data, data, "attribute0", null, "value0" );
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, data, data, "attribute1", null, "value1" );
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, data, data, "attribute2", null, "value2" );
		assertEventState( handler, index++, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, data, DataNode.MODIFIED, false, true );
		assertEventState( handler, index++, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );

		handler.reset();
		transaction.setAttribute( data, "attribute0", "value0" );
		transaction.setAttribute( data, "attribute1", "value1" );
		transaction.setAttribute( data, "attribute2", "value2" );
		assertNodeState( data, true, 3 );
		assertEventCounts( handler, 0, 0, 0 );

		transaction.commit();
		assertNodeState( data, true, 3 );
		assertEventCounts( handler, 0, 0, 0 );
	}

	@Test
	public void testTransactionWithEventModifingNode() {
		MockDataNode node = new MockDataNode();
		node.addDataListener( new ModifyingDataHandler( node, "time", System.nanoTime() ) );
		Transaction transaction = new Transaction();
		try {
			transaction.setAttribute( node, "fire", "event" );
			transaction.commit();
			fail( "RuntimeException should be thrown due to modifying data listener." );
		} catch( RuntimeException exception ) {

		}
	}

	@Test
	public void testTransactionWithEventModifyingSeparateNode() {
		MockDataNode node0 = new MockDataNode();
		MockDataNode node1 = new MockDataNode();
		node0.addDataListener( new ModifyingDataHandler( node1, "name", "node1" ) );
		Transaction transaction = new Transaction();
		transaction.setAttribute( node0, "fire", "event" );
		transaction.commit();
		assertEquals( "node1", node1.getAttribute( "name" ) );
	}

	@Test
	public void testTransactionByModifyingChild() {
		MockDataList parent = new MockDataList( "parent" );
		MockDataNode child = new MockDataNode( "child" );
		DataEventWatcher parentWatcher = parent.getDataEventWatcher();
		DataEventWatcher childWatcher = child.getDataEventWatcher();

		// Set up the data model.
		parent.add( child );

		// Set the parent unmodified.
		parent.setModified( false );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		parentWatcher.reset();
		childWatcher.reset();

		// Start a transaction
		Transaction transaction = new Transaction();

		// Set the child attribute but nothing should happen
		// because the transaction has not been committed yet.
		transaction.setAttribute( child, "key1", "value1" );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		assertEventCounts( parentWatcher, 0, 0, 0, 0, 0 );
		assertEventCounts( childWatcher, 0, 0, 0, 0, 0 );
		parentWatcher.reset();
		childWatcher.reset();

		// Commit the transaction.
		transaction.commit();
		assertTrue( parent.isModified() );
		assertTrue( child.isModified() );
		assertEquals( "value1", child.getAttribute( "key1" ) );
		assertEventCounts( parentWatcher, 1, 1, 1, 0, 0 );
		assertEventState( parentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, parent, child, "key1", null, "value1" );
		assertEventState( parentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, parent, DataNode.MODIFIED, false, true );
		assertEventState( parentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, parent );
		assertEventCounts( childWatcher, 1, 1, 1, 0, 0 );
		assertEventState( childWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, child, child, "key1", null, "value1" );
		assertEventState( childWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, child, DataNode.MODIFIED, false, true );
		assertEventState( childWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, child );
		parentWatcher.reset();
		childWatcher.reset();

		// Unset the the child attribute but nothing should happen 
		// because the transaction has not been committed yet.
		transaction = new Transaction();
		transaction.setAttribute( child, "key1", null );
		assertEventCounts( parentWatcher, 0, 0, 0, 0, 0 );
		assertEventCounts( childWatcher, 0, 0, 0, 0, 0 );
		parentWatcher.reset();
		childWatcher.reset();

		// Commit the transaction.
		transaction.commit();
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		assertEquals( null, child.getAttribute( "key1" ) );
		assertEventCounts( parentWatcher, 1, 1, 1, 0, 0 );
		assertEventState( parentWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.REMOVE, parent, child, "key1", "value1", null );
		assertEventState( parentWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, parent, DataNode.MODIFIED, true, false );
		assertEventState( parentWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, parent );
		assertEventCounts( childWatcher, 1, 1, 1, 0, 0 );
		assertEventState( childWatcher, 0, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.REMOVE, child, child, "key1", "value1", null );
		assertEventState( childWatcher, 1, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, child, DataNode.MODIFIED, true, false );
		assertEventState( childWatcher, 2, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, child );
	}

	@Test
	public void testTransactionByModifyingGrandChild() {
		MockDataList parent = new MockDataList( "parent" );
		MockDataList child = new MockDataList( "child" );
		MockDataNode grandchild = new MockDataNode( "grandchild" );
		DataEventWatcher watcher = parent.getDataEventWatcher();

		parent.add( child );
		child.add( grandchild );
		parent.setModified( false );
		assertFalse( parent.isModified() );
		assertFalse( child.isModified() );
		assertFalse( grandchild.isModified() );
		watcher.reset();

		Transaction transaction = new Transaction();
		transaction.setAttribute( grandchild, "key1", "value1" );
		assertEventCounts( watcher, 0, 0, 0, 0, 0 );
		watcher.reset();

		transaction.commit();
		assertEventCounts( watcher, 1, 1, 1, 0, 0 );
		watcher.reset();

		transaction = new Transaction();
		transaction.setAttribute( grandchild, "key1", null );
		assertEventCounts( watcher, 0, 0, 0, 0, 0 );
		watcher.reset();

		transaction.commit();
		assertEventCounts( watcher, 1, 1, 1, 0, 0 );
		watcher.reset();
	}

	/**
	 * This is a fairly complex test to ensure that the transaction handling can
	 * handle two nodes that have overridden the equals() and hashCode() methods
	 * to cause the two nodes to have the same hash code and are equal according
	 * to the equals() method.
	 */
	@Test
	public void testNodesThatOverrideHashcodeAndEqualsWithZeroHashcode() {
		EqualHashOverrideNode node1 = new EqualHashOverrideNode();
		EqualHashOverrideNode node2 = new EqualHashOverrideNode();
		DataEventWatcher watcher1 = new DataEventWatcher();
		DataEventWatcher watcher2 = new DataEventWatcher();
		node1.addDataListener( watcher1 );
		node2.addDataListener( watcher2 );

		// Ensure that the nodes are in the correct state.
		assertEquals( node1, node2 );
		assertEquals( node1.hashCode(), node2.hashCode() );
		assertFalse( node1 == node2 );
		assertEventCounts( watcher1, 0, 0, 0 );
		assertEventCounts( watcher2, 0, 0, 0 );

		// Use a transaction to change both nodes at the same time.
		Transaction transaction = new Transaction();
		transaction.setAttribute( node1, "name", "node1" );
		transaction.setAttribute( node2, "name", "node2" );
		transaction.commit();

		// Check the attributes.
		assertEquals( "node1", node1.getAttribute( "name" ) );
		assertEquals( "node2", node2.getAttribute( "name" ) );
		// Check the equals() and hashCode() methods.
		assertEquals( node1, node2 );
		assertEquals( node1.hashCode(), node2.hashCode() );
		assertFalse( node1 == node2 );
		// Check the event counts.
		assertEventCounts( watcher1, 1, 1, 1 );
		assertEventCounts( watcher2, 1, 1, 1 );
	}

	/**
	 * This is a fairly complex test to ensure that the transaction handling can
	 * handle two nodes that have overridden the equals() and hashCode() methods
	 * to cause the two nodes to have the same hash code and are equal according
	 * to the equals() method.
	 */
	@Test
	public void testNodesThatOverrideHashcodeAndEqualsWithNonZeroHashcode() {
		EqualHashOverrideNode node1 = new EqualHashOverrideNode();
		EqualHashOverrideNode node2 = new EqualHashOverrideNode();
		DataEventWatcher watcher1 = new DataEventWatcher();
		DataEventWatcher watcher2 = new DataEventWatcher();
		node1.addDataListener( watcher1 );
		node2.addDataListener( watcher2 );

		// Ensure that the nodes are in the correct state.
		assertEquals( node1, node2 );
		assertEquals( node1.hashCode(), node2.hashCode() );
		assertFalse( node1 == node2 );
		assertEventCounts( watcher1, 0, 0, 0 );
		assertEventCounts( watcher2, 0, 0, 0 );

		node1.setKey( "value1" );
		node2.setKey( "value1" );
		node1.setModified( false );
		node2.setModified( false );
		watcher1.reset();
		watcher2.reset();

		// Use a transaction to change both nodes at the same time.
		Transaction transaction = new Transaction();
		transaction.setAttribute( node1, "name", "node1" );
		transaction.setAttribute( node2, "name", "node2" );
		transaction.commit();

		// Check the attributes.
		assertEquals( "node1", node1.getAttribute( "name" ) );
		assertEquals( "node2", node2.getAttribute( "name" ) );
		// Check the equals() and hashCode() methods.
		assertEquals( node1, node2 );
		assertEquals( node1.hashCode(), node2.hashCode() );
		assertFalse( node1 == node2 );
		// Check the event counts.
		assertEventCounts( watcher1, 1, 1, 1 );
		assertEventCounts( watcher2, 1, 1, 1 );
	}

	private class ModifyingDataHandler extends DataAdapter {

		private DataNode node;

		private String name;

		private Object value;

		public ModifyingDataHandler( DataNode node, String name, Object value ) {
			this.node = node;
			this.name = name;
			this.value = value;
		}

		@Override
		public void dataAttributeChanged( DataAttributeEvent event ) {
			node.setAttribute( name, value );
		}

	}

	private class EqualHashOverrideNode extends DataNode {

		private static final String KEY = "key";

		public String getKey() {
			return (String)getAttribute( KEY );
		}

		public void setKey( String key ) {
			setAttribute( KEY, key );
		}

		@Override
		public int hashCode() {
			String key = getKey();
			return key == null ? 0 : key.hashCode();
		}

		@Override
		public boolean equals( Object object ) {
			if( !( object instanceof EqualHashOverrideNode ) ) return false;
			String key = getKey();
			return ObjectUtil.areEqual( key, ( (EqualHashOverrideNode)object ).getKey() );
		}

	}

}
