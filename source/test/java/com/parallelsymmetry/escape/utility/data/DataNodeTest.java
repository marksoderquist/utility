package com.parallelsymmetry.escape.utility.data;

import java.lang.reflect.Modifier;

import org.junit.Test;

public class DataNodeTest extends DataTestCase {

	@Test
	public void testDataNodeIsAbstract() {
		assertTrue( "DataNode class is not abstract.", ( DataNode.class.getModifiers() & Modifier.ABSTRACT ) == Modifier.ABSTRACT );
	}

	@Test
	public void testIsModified() {
		MockDataNode data = new MockDataNode();
		DataEventHandler handler = data.getDataEventHandler();
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertFalse( data.isModified() );
		assertEventCounts( handler, 0, 0, 0 );

		data.setAttribute( "attribute", "value" );
		assertEquals( 1, data.getModifiedAttributeCount() );
		assertTrue( data.isModified() );
		assertEventCounts( handler, 1, 1, 1 );

		data.clearModified();
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertFalse( data.isModified() );
		assertEventCounts( handler, 2, 1, 2 );
	}

	@Test
	public void testSetNullAttributeToNull() {
		MockDataNode data = new MockDataNode();
		DataEventHandler handler = data.getDataEventHandler();
		data.setAttribute( "attribute", null );
		assertEventCounts( handler, 0, 0, 0 );
	}

	@Test
	public void testSetAttributeWithNullName() {
		MockDataNode data = new MockDataNode();
		DataEventHandler handler = data.getDataEventHandler();

		try {
			data.setAttribute( null, "value" );
			fail( "Null attribute names are not allowed." );
		} catch( NullPointerException exception ) {
			assertEquals( "Attribute name cannot be null.", exception.getMessage() );
		}
		assertEventCounts( handler, 0, 0, 0 );
	}

	@Test
	public void testGetAttributeWithNullName() {
		MockDataNode data = new MockDataNode();

		try {
			data.getAttribute( null );
			fail( "Null attribute names are not allowed." );
		} catch( NullPointerException exception ) {
			assertEquals( "Attribute name cannot be null.", exception.getMessage() );
		}
	}

	@Test
	public void testNullAttributeValues() {
		MockDataNode data = new MockDataNode();
		DataEventHandler handler = data.getDataEventHandler();
		assertNull( data.getAttribute( "attribute" ) );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.setAttribute( "attribute", null );
		assertNull( data.getAttribute( "attribute" ) );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		// Set the attribute value.
		data.setAttribute( "attribute", "value" );
		assertEquals( "value", data.getAttribute( "attribute" ) );
		assertNodeState( data, true, 1 );
		assertEventCounts( handler, 1, 1, 1 );

		// Set the attribute to the same value to test nothing else happens.
		data.setAttribute( "attribute", "value" );
		assertEquals( "value", data.getAttribute( "attribute" ) );
		assertNodeState( data, true, 1 );
		assertEventCounts( handler, 1, 1, 1 );

		data.setAttribute( "attribute", null );
		assertNull( data.getAttribute( "attribute" ) );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 2, 2, 2 );

		data.setAttribute( "attribute", null );
		assertNull( data.getAttribute( "attribute" ) );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 2, 2, 2 );
	}

	@Test
	public void testGetAndSetAttribute() {
		MockDataNode data = new MockDataNode();
		DataEventHandler handler = data.getDataEventHandler();

		assertEquals( null, data.getAttribute( "x" ) );
		assertEquals( null, data.getAttribute( "y" ) );
		assertEquals( null, data.getAttribute( "z" ) );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.setAttribute( "x", 1 );
		assertEquals( 1, data.getAttribute( "x" ) );
		assertEquals( null, data.getAttribute( "y" ) );
		assertEquals( null, data.getAttribute( "z" ) );
		assertNodeState( data, true, 1 );
		assertEventCounts( handler, 1, 1, 1 );

		data.setAttribute( "x", 0 );
		assertEquals( 0, data.getAttribute( "x" ) );
		assertEquals( null, data.getAttribute( "y" ) );
		assertEquals( null, data.getAttribute( "z" ) );
		assertNodeState( data, true, 1 );
		assertEventCounts( handler, 2, 2, 1 );
	}

	@Test
	public void testSetAttributeSetsModifiedFlag() {
		MockDataNode data = new MockDataNode();
		DataEventHandler handler = data.getDataEventHandler();
		assertFalse( data.isModified() );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.setAttribute( "x", 1 );
		data.setAttribute( "y", 2 );
		data.setAttribute( "z", 3 );
		assertNodeState( data, true, 3 );
		assertEventCounts( handler, 3, 3, 1 );
	}

	@Test
	public void testUnmodifyAttributeClearsModifiedFlag() {
		MockDataNode data = new MockDataNode();
		DataEventHandler handler = data.getDataEventHandler();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.setAttribute( "x", 0 );
		data.setAttribute( "y", 0 );
		data.setAttribute( "z", 0 );
		data.clearModified();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 4, 3, 2 );

		data.setAttribute( "x", 1 );
		data.setAttribute( "y", 2 );
		data.setAttribute( "z", 3 );
		assertNodeState( data, true, 3 );
		assertEventCounts( handler, 7, 6, 3 );

		data.setAttribute( "x", 0 );
		data.setAttribute( "y", 0 );
		data.setAttribute( "z", 0 );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 10, 9, 4 );
	}

	@Test
	public void testCommitClearsModifiedAttributeCount() {
		MockDataNode data = new MockDataNode();
		DataEventHandler handler = data.getDataEventHandler();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.setAttribute( "x", 1 );
		data.setAttribute( "y", 2 );
		data.setAttribute( "z", 3 );
		assertNodeState( data, true, 3 );
		assertEventCounts( handler, 3, 3, 1 );

		data.clearModified();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 4, 3, 2 );
	}

	public void testResources() {
		MockDataNode data = new MockDataNode();
		DataEventHandler handler = data.getDataEventHandler();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.putResource( "name", "value" );
		assertEquals( "value", data.getResource( "name" ) );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.putResource( "name", null );
		assertNull( data.getResource( "name" ) );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );
	}

	public void testDataEventNotification() {
		MockDataNode data = new MockDataNode();
		DataEventHandler handler = data.getDataEventHandler();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		// Insert an attribute.
		data.setAttribute( "attribute", "value0" );
		assertNodeState( data, true, 1 );
		assertEventCounts( handler, 1, 1, 1 );

		// Modify the attribute to the same value. Should do nothing.
		data.setAttribute( "attribute", "value0" );
		assertNodeState( data, true, 1 );
		assertEventCounts( handler, 1, 1, 1 );

		// Modify the attribute.
		data.setAttribute( "attribute", "value1" );
		assertNodeState( data, true, 1 );
		assertEventCounts( handler, 2, 2, 1 );

		// Remove the attribute.
		data.setAttribute( "attribute", null );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 3, 3, 2 );

		int index = 0;
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.INSERT, data, "attribute", null, "value0" );
		assertEventState( handler, index++, MetaAttributeEvent.class, DataEvent.Type.MODIFY, data, DataNode.MODIFIED, false, true );
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.MODIFY, data, "attribute", "value0", "value1" );
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.REMOVE, data, "attribute", "value1", null );
		assertEventState( handler, index++, MetaAttributeEvent.class, DataEvent.Type.MODIFY, data, DataNode.MODIFIED, true, false );
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

	public void testEventsWithCommit() {
		MockDataNode data = new MockDataNode();
		DataEventHandler handler = data.getDataEventHandler();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		// Change an attribute.
		data.setAttribute( "attribute", "value0" );
		assertNodeState( data, true, 1 );
		assertEventCounts( handler, 1, 1, 1 );

		// Commit the changes.
		data.clearModified();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 2, 1, 2 );

		// Commit again to test that nothing else happens.
		data.clearModified();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 2, 1, 2 );

		int index = 0;
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.INSERT, data, "attribute", null, "value0" );
		assertEventState( handler, index++, MetaAttributeEvent.class, DataEvent.Type.MODIFY, data, DataNode.MODIFIED, false, true );
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEventState( handler, index++, MetaAttributeEvent.class, DataEvent.Type.MODIFY, data, DataNode.MODIFIED, true, false );
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

	public void testAttributeNodeUnmodifiesParent() {
		MockDataNode node = new MockDataNode();
		MockDataNode attribute = new MockDataNode();
		DataEventHandler nodeHandler = node.getDataEventHandler();
		DataEventHandler attributeHandler = attribute.getDataEventHandler();
		assertNodeState( node, false, 0 );

		node.setAttribute( "attribute", attribute );
		assertNodeState( node, true, 1 );
		assertEventCounts( nodeHandler, 1, 1, 1 );
		assertEventCounts( attributeHandler, 0, 0, 0 );

		node.clearModified();
		assertNodeState( node, false, 0 );
		assertEventCounts( nodeHandler, 2, 1, 2 );

		// Test setting an attribute on the attribute node modifies the parent.
		attribute.setAttribute( "attribute", "value" );
		assertEquals( node, attribute.getParent() );
		assertNodeState( attribute, true, 1 );
		assertNodeState( node, true, 1 );
		assertEventCounts( nodeHandler, 3, 1, 3 );
		assertEventCounts( attributeHandler, 1, 1, 1 );

		// Test unsetting an attribute on the attribute node unmodified the parent.
		attribute.setAttribute( "attribute", null );
		assertNodeState( attribute, false, 0 );
		assertNodeState( node, false, 0 );
		assertEventCounts( nodeHandler, 4, 1, 4 );
		assertEventCounts( attributeHandler, 2, 2, 2 );
	}

	public void testClearModifiedOnAttributeNodeClearsParent() {
		MockDataNode node = new MockDataNode();
		MockDataNode attribute = new MockDataNode();
		DataEventHandler nodeHandler = node.getDataEventHandler();
		DataEventHandler attributeHandler = attribute.getDataEventHandler();
		assertNodeState( node, false, 0 );
		assertEventCounts( nodeHandler, 0, 0, 0 );
		assertEventCounts( attributeHandler, 0, 0, 0 );

		node.setAttribute( "attribute", attribute );
		assertNodeState( node, true, 1 );
		assertEventCounts( nodeHandler, 1, 1, 1 );
		assertEventCounts( attributeHandler, 0, 0, 0 );

		node.clearModified();
		assertNodeState( node, false, 0 );
		assertEventCounts( nodeHandler, 2, 1, 2 );

		// Test setting an attribute on the attribute node modifies the parent.
		attribute.setAttribute( "attribute", "value" );
		assertEquals( node, attribute.getParent() );
		assertNodeState( attribute, true, 1 );
		assertNodeState( node, true, 1 );
		assertEventCounts( nodeHandler, 3, 1, 3 );
		assertEventCounts( attributeHandler, 1, 1, 1 );

		// Test unsetting an attribute on the attribute node unmodified the parent.
		attribute.clearModified();
		assertNodeState( attribute, false, 0 );
		assertNodeState( node, false, 0 );
		assertEventCounts( nodeHandler, 4, 1, 4 );
		assertEventCounts( attributeHandler, 2, 1, 2 );
	}

	public void testMoveAttributeNodeToDifferentParent() {
		MockDataNode child = new MockDataNode();
		MockDataNode parent0 = new MockDataNode();
		MockDataNode parent1 = new MockDataNode();
		DataEventHandler childHandler = child.getDataEventHandler();
		DataEventHandler parent0Handler = parent0.getDataEventHandler();
		DataEventHandler parent1Handler = parent1.getDataEventHandler();

		// Add the child attribute to parent 0.
		parent0.setAttribute( "child", child );
		assertNodeState( parent0, true, 1 );
		assertEventCounts( childHandler, 0, 0, 0 );
		assertEventCounts( parent0Handler, 1, 1, 1 );
		assertEventCounts( parent1Handler, 0, 0, 0 );

		// Clear the modified flag of parent 0.
		parent0.clearModified();
		assertNodeState( parent0, false, 0 );
		assertEventCounts( childHandler, 0, 0, 0 );
		assertEventCounts( parent0Handler, 2, 1, 2 );
		assertEventCounts( parent1Handler, 0, 0, 0 );

		// Add the child attribute to parent 1.
		parent1.setAttribute( "child", child );
		assertNull( parent0.getAttribute( "child" ) );
		assertEquals( child, parent1.getAttribute( "child" ) );
		assertNodeState( parent0, true, 1 );
		assertNodeState( parent1, true, 1 );
		assertEventCounts( childHandler, 0, 0, 0 );
		assertEventCounts( parent0Handler, 3, 2, 3 );
		assertEventCounts( parent1Handler, 1, 1, 1 );
	}

	public void testClearModifiedClearsChildAttributes() {
		MockDataNode child = new MockDataNode( "child" );
		MockDataNode parent = new MockDataNode( "parent" );
		DataEventHandler childHandler = child.getDataEventHandler();
		DataEventHandler parentHandler = parent.getDataEventHandler();
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 0, 0, 0 );
		assertNodeState( parent, false, 0 );
		assertEventCounts( parentHandler, 0, 0, 0 );

		parent.setAttribute( "child", child );
		assertNodeState( parent, true, 1 );
		assertEventCounts( parentHandler, 1, 1, 1 );

		parent.clearModified();
		assertNodeState( parent, false, 0 );
		assertEventCounts( parentHandler, 2, 1, 2 );

		child.setAttribute( "attribute", "value" );
		assertNodeState( child, true, 1 );
		assertEventCounts( childHandler, 1, 1, 1 );
		assertNodeState( parent, true, 1 );
		assertEventCounts( parentHandler, 3, 1, 3 );

		parent.clearModified();
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 2, 1, 2 );
		assertNodeState( parent, false, 0 );
		assertEventCounts( parentHandler, 4, 1, 4 );
	}

}
