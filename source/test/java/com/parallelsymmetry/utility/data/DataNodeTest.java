package com.parallelsymmetry.utility.data;

import com.parallelsymmetry.utility.Accessor;
import com.parallelsymmetry.utility.log.Log;
import com.parallelsymmetry.utility.mock.DataEventWatcher;
import com.parallelsymmetry.utility.mock.MockDataList;
import com.parallelsymmetry.utility.mock.MockDataNode;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataNodeTest extends DataTestCase {

	@Test
	public void testDataNodeIsAbstract() {
		assertTrue( (DataNode.class.getModifiers() & Modifier.ABSTRACT) == Modifier.ABSTRACT );
	}

	@Test
	public void testIsNewNodeModified() {
		MockDataNode node = new MockDataNode();
		assertFalse( node.isModified() );
	}

	@Test
	public void testModifyUnmodify() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertFalse( data.isModified() );
		assertEventCounts( handler, 0, 0, 0 );

		data.setModified( true );
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertTrue( data.isModified() );
		assertEventCounts( handler, 1, 1, 0 );

		data.setModified( false );
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertFalse( data.isModified() );
		assertEventCounts( handler, 2, 2, 0 );
	}

	@Test
	public void testSetAttributeUnmodify() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertFalse( data.isModified() );
		assertEventCounts( handler, 0, 0, 0 );

		data.setAttribute( "attribute", "value" );
		assertEquals( 1, data.getModifiedAttributeCount() );
		assertTrue( data.isModified() );
		assertEventCounts( handler, 1, 1, 1 );

		data.setModified( false );
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertFalse( data.isModified() );
		assertEventCounts( handler, 2, 2, 1 );
	}

	@Test
	public void testAttributes() {
		String key = "key";
		Object value = "value";

		MockDataNode node = new MockDataNode();
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
		MockDataNode node = new MockDataNode();
		node.setAttribute( key, value );
		Object check = node.getAttribute( key );
		assertEquals( value, check, "Object value not equal" );
	}

	@Test
	public void testStringAttribute() {
		String key = "key";
		String value = "value";
		MockDataNode node = new MockDataNode();
		node.setAttribute( key, value );
		String check = node.getAttribute( key );
		assertEquals( value, check, "String value not equal" );
	}

	@Test
	public void testBooleanAttribute() {
		String key = "key";
		boolean value = true;
		MockDataNode node = new MockDataNode();
		node.setAttribute( key, value );
		boolean check = node.getAttribute( key );
		assertEquals( value, check, "Boolean value not equal" );
	}

	@Test
	public void testIntegerAttribute() {
		String key = "key";
		int value = 0;
		MockDataNode node = new MockDataNode();
		node.setAttribute( key, value );
		int check = node.getAttribute( key );
		assertEquals( value, check, "Integer value not equal" );
	}

	@Test
	public void testSetNullAttributeToNull() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();
		data.setAttribute( "attribute", null );
		assertEventCounts( handler, 0, 0, 0 );
	}

	@Test
	public void testSetAttributeWithNullName() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();

		try {
			data.setAttribute( null, "value" );
			fail( "Null attribute names are not allowed." );
		} catch( NullPointerException exception ) {
			assertEquals( "Data value name cannot be null.", exception.getMessage() );
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
			assertEquals( "Data value name cannot be null.", exception.getMessage() );
		}
	}

	@Test
	public void testNullAttributeValues() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();
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
		DataEventWatcher handler = data.getDataEventWatcher();

		assertNull( data.getAttribute( "x" ) );
		assertNull( data.getAttribute( "y" ) );
		assertNull( data.getAttribute( "z" ) );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.setAttribute( "x", 1 );
		assertEquals( 1, (int)data.getAttribute( "x" ) );
		assertNull( data.getAttribute( "y" ) );
		assertNull( data.getAttribute( "z" ) );
		assertNodeState( data, true, 1 );
		assertEventCounts( handler, 1, 1, 1 );

		data.setAttribute( "x", 0 );
		assertEquals( 0, (int)data.getAttribute( "x" ) );
		assertNull( data.getAttribute( "y" ) );
		assertNull( data.getAttribute( "z" ) );
		assertNodeState( data, true, 1 );
		assertEventCounts( handler, 2, 1, 2 );
	}

	@Test
	public void testModifiedBySetAttribute() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();
		assertFalse( data.isModified() );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.setAttribute( "x", 1 );
		data.setAttribute( "y", 2 );
		data.setAttribute( "z", 3 );
		assertNodeState( data, true, 3 );
		assertEventCounts( handler, 3, 1, 3 );
	}

	@Test
	public void testUnmodifiedByUnsetAttribute() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.setAttribute( "x", 0 );
		data.setAttribute( "y", 0 );
		data.setAttribute( "z", 0 );
		data.setModified( false );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 4, 2, 3 );

		data.setAttribute( "x", 1 );
		data.setAttribute( "y", 2 );
		data.setAttribute( "z", 3 );
		assertNodeState( data, true, 3 );
		assertEventCounts( handler, 7, 3, 6 );

		data.setAttribute( "x", 0 );
		data.setAttribute( "y", 0 );
		data.setAttribute( "z", 0 );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 10, 4, 9 );
	}

	@Test
	public void testModifiedAttributeCountResetByCommit() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.setAttribute( "x", 1 );
		data.setAttribute( "y", 2 );
		data.setAttribute( "z", 3 );
		assertNodeState( data, true, 3 );
		assertEventCounts( handler, 3, 1, 3 );

		data.setModified( false );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 4, 2, 3 );
	}

	@Test
	public void testTestMetaValueModified() {
		String key = DataNode.MODIFIED;
		MockDataNode data = new MockDataNode();
		assertEquals( false, data.getMetaValue( key ) );
		data.setMetaValue( key, true );
		assertEquals( true, data.getMetaValue( key ) );
		data.setMetaValue( key, false );
		assertEquals( false, data.getMetaValue( key ) );
	}

	@Test
	public void testTestMetaValueOther() {
		String key = "other";
		MockDataNode data = new MockDataNode();
		assertNull( data.getMetaValue( key ) );
		data.setMetaValue( key, true );
		assertEquals( true, data.getMetaValue( key ) );
		data.setMetaValue( key, false );
		assertEquals( false, data.getMetaValue( key ) );
	}

	@Test
	public void testSetNullMetaValueToNull() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();
		data.setMetaValue( "attribute", null );
		assertEventCounts( handler, 0, 0, 0 );
	}

	@Test
	public void testSetMetaValueWithNullName() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();

		try {
			data.setMetaValue( null, "value" );
			fail( "Null attribute names are not allowed." );
		} catch( NullPointerException exception ) {
			assertEquals( "Meta value name cannot be null.", exception.getMessage() );
		}
		assertEventCounts( handler, 0, 0, 0 );
	}

	@Test
	public void testGetMetaValueWithNullName() {
		MockDataNode data = new MockDataNode();

		try {
			data.getMetaValue( null );
			fail( "Null attribute names are not allowed." );
		} catch( NullPointerException exception ) {
			assertEquals( "Meta value name cannot be null.", exception.getMessage() );
		}
	}

	@Test
	public void testResources() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();
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

	@Test
	public void testDataEventNotification() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();
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
		assertEventCounts( handler, 2, 1, 2 );

		// Remove the attribute.
		data.setAttribute( "attribute", null );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 3, 2, 3 );

		int index = 0;
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, data, data, "attribute", null, "value0" );
		assertEventState( handler, index++, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, data, DataNode.MODIFIED, false, true );
		assertEventState( handler, index++, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, data );
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.MODIFY, data, data, "attribute", "value0", "value1" );
		assertEventState( handler, index++, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, data );
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.REMOVE, data, data, "attribute", "value1", null );
		assertEventState( handler, index++, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, data, DataNode.MODIFIED, true, false );
		assertEventState( handler, index++, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

	@Test
	public void testParentDataEventNotification() {
		MockDataNode data = new MockDataNode();
		MockDataNode child = new MockDataNode();
		data.setAttribute( "child", child );
		DataEventWatcher handler = data.getDataEventWatcher();
		assertNodeState( child, false, 0 );
		assertEventCounts( handler, 1, 1, 1 );

		// Insert an attribute.
		child.setAttribute( "attribute", "value0" );
		assertNodeState( child, true, 1 );
		assertEventCounts( handler, 2, 1, 2 );

		// Modify the attribute to the same value. Should do nothing.
		child.setAttribute( "attribute", "value0" );
		assertNodeState( child, true, 1 );
		assertEventCounts( handler, 2, 1, 2 );

		// Modify the attribute.
		child.setAttribute( "attribute", "value1" );
		assertNodeState( child, true, 1 );
		assertEventCounts( handler, 3, 1, 3 );

		// Remove the attribute.
		child.setAttribute( "attribute", null );
		assertNodeState( child, false, 0 );
		assertEventCounts( handler, 4, 1, 4 );

		int index = 0;
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, data, data, "child", null, child );
		assertEventState( handler, index++, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, data, data, DataNode.MODIFIED, false, true );
		assertEventState( handler, index++, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, data );
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, data, child, "attribute", null, "value0" );
		assertEventState( handler, index++, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, data );
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.MODIFY, data, child, "attribute", "value0", "value1" );
		assertEventState( handler, index++, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, data );
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.REMOVE, data, child, "attribute", "value1", null );
		assertEventState( handler, index++, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

	@Test
	public void testEventsWithCommit() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		// Change an attribute.
		data.setAttribute( "attribute", "value0" );
		assertNodeState( data, true, 1 );
		assertEventCounts( handler, 1, 1, 1 );

		// Commit the changes.
		data.setModified( false );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 2, 2, 1 );

		// Commit again to test that nothing else happens.
		data.setModified( false );
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 2, 2, 1 );

		int index = 0;
		assertEventState( handler, index++, DataEvent.Type.DATA_ATTRIBUTE, DataEvent.Action.INSERT, data, data, "attribute", null, "value0" );
		assertEventState( handler, index++, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, data, DataNode.MODIFIED, false, true );
		assertEventState( handler, index++, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, data );
		assertEventState( handler, index++, DataEvent.Type.META_ATTRIBUTE, DataEvent.Action.MODIFY, data, DataNode.MODIFIED, true, false );
		assertEventState( handler, index++, DataEvent.Type.DATA_CHANGED, DataEvent.Action.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

	@Test
	public void testCollapsingEventsWithTransaction() {
		MockDataNode data = new MockDataNode();
		DataEventWatcher handler = data.getDataEventWatcher();
		assertNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		Transaction.create();
		data.setAttribute( "a", "1" );
		data.setAttribute( "a", "2" );
		data.setAttribute( "a", "3" );
		data.setAttribute( "a", "4" );
		data.setAttribute( "a", "5" );
		Transaction.commit();

		assertEquals( "5", data.getAttribute( "a" ) );
		assertNodeState( data, true, 1 );

		// FIXME The following event counts should be 1,1,1 not 1,5,1.
		assertEventCounts( handler, 1, 1, 5 );
	}

	@Test
	public void testGetParent() {
		MockDataNode parent = new MockDataNode();
		MockDataNode child = new MockDataNode();
		//assertEquals( 0, child.getParents().size() );
		assertNull( child.getParent() );

		String key = "key";

		parent.setAttribute( key, child );
		assertEquals( child.getParent(), parent );

		parent.setAttribute( key, null );
		assertNull( child.getParent() );
	}

	@Test
	public void testGetNodePath() {
		MockDataList list = new MockDataList();
		MockDataNode child = new MockDataNode();

		list.add( child );

		List<DataNode> path = list.getNodePath();
		assertEquals( 1, path.size() );
		assertEquals( list, path.get( 0 ) );

		path = child.getNodePath();
		assertEquals( 2, path.size() );
		assertEquals( list, path.get( 0 ) );
		assertEquals( child, path.get( 1 ) );
	}

	@Test
	public void testParentModifiedByChildNodeAttributeChange() {
		MockDataNode parent = new MockDataNode();
		MockDataNode child = new MockDataNode();
		DataEventWatcher parentHandler = parent.getDataEventWatcher();
		DataEventWatcher childHandler = child.getDataEventWatcher();
		assertNodeState( parent, false, 0 );

		parent.setAttribute( "child", child );
		assertNodeState( parent, true, 1 );
		assertEventCounts( parentHandler, 1, 1, 1 );
		assertEventCounts( childHandler, 0, 0, 0 );

		parent.setModified( false );
		assertNodeState( parent, false, 0 );
		assertEventCounts( parentHandler, 2, 2, 1 );

		// Test setting an attribute on the child node modifies the parent.
		child.setAttribute( "attribute", "value" );
		assertEquals( child.getParent(), parent );

		assertNodeState( child, true, 1 );
		assertNodeState( parent, true, 1 );
		assertEventCounts( parentHandler, 3, 3, 2 );
		assertEventCounts( childHandler, 1, 1, 1 );

		// Test unsetting an attribute on the child node unmodifies the parent.
		child.setAttribute( "attribute", null );
		assertNodeState( child, false, 0 );
		assertNodeState( parent, false, 0 );
		assertEventCounts( parentHandler, 4, 4, 3 );
		assertEventCounts( childHandler, 2, 2, 2 );
	}

	@Test
	public void testParentModifiedByChildNodeAttributeRippleChange() {
		MockDataNode parent = new MockDataNode();
		MockDataNode child = new MockDataNode();
		DataEventWatcher parentHandler = parent.getDataEventWatcher();
		DataEventWatcher childHandler = child.getDataEventWatcher();
		assertNodeState( parent, false, 0 );

		parent.setAttribute( "child", child );
		assertEquals( child.getParent(), parent );
		assertNodeState( parent, true, 1 );
		assertEventCounts( parentHandler, 1, 1, 1 );
		assertEventCounts( childHandler, 0, 0, 0 );
		parentHandler.reset();

		parent.setModified( false );
		assertNodeState( parent, false, 0 );
		parentHandler.reset();
		childHandler.reset();

		// Set the 'a' attribute to '1'.
		child.setAttribute( "a", "1" );
		assertEventCounts( parentHandler, 1, 1, 1 );
		assertEventCounts( childHandler, 1, 1, 1 );
		parentHandler.reset();
		childHandler.reset();

		// Set the 'b' attribute to '1';
		child.setAttribute( "b", "1" );
		assertEventCounts( parentHandler, 1, 0, 1 );
		assertEventCounts( childHandler, 1, 0, 1 );
		parentHandler.reset();
		childHandler.reset();

		// Set this state as the new unmodified state.
		child.setModified( false );
		assertNodeState( parent, false, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( parentHandler, 1, 1, 0 );
		assertEventCounts( childHandler, 1, 1, 0 );

		// The parent is already not modified no event should be sent.
		parent.setModified( false );
		assertNodeState( parent, false, 0 );
		assertNodeState( child, false, 0 );
		assertEventCounts( parentHandler, 1, 1, 0 );
		assertEventCounts( childHandler, 1, 1, 0 );
		parentHandler.reset();
		childHandler.reset();

		// Test setting 'a' attribute on the child node modifies the parent.
		child.setAttribute( "a", "2" );
		assertNodeState( child, true, 1 );
		assertNodeState( parent, true, 1 );
		assertEventCounts( parentHandler, 1, 1, 1 );
		assertEventCounts( childHandler, 1, 1, 1 );
		parentHandler.reset();
		childHandler.reset();

		// Test setting the 'b' attribute on the child leaves the parent modified.
		child.setAttribute( "b", "2" );
		assertNodeState( child, true, 2 );
		assertNodeState( parent, true, 1 );
		assertEventCounts( parentHandler, 1, 0, 1 );
		assertEventCounts( childHandler, 1, 0, 1 );
		parentHandler.reset();
		childHandler.reset();

		// Test unsetting 'a' attribute on the child leaves the parent modified.
		child.setAttribute( "a", "1" );
		assertNodeState( child, true, 1 );
		assertNodeState( parent, true, 1 );
		assertEventCounts( parentHandler, 1, 0, 1 );
		assertEventCounts( childHandler, 1, 0, 1 );
		parentHandler.reset();
		childHandler.reset();

		// Test unsetting the 'b' attribute on the child returns the parent to unmodified.
		child.setAttribute( "b", "1" );
		assertNodeState( child, false, 0 );
		assertNodeState( parent, false, 0 );
		assertEventCounts( parentHandler, 1, 1, 1 );
		assertEventCounts( childHandler, 1, 1, 1 );
	}

	@Test
	public void testParentModifiedByNodeAttributeClearModified() {
		MockDataNode node = new MockDataNode();
		MockDataNode attribute = new MockDataNode();
		DataEventWatcher nodeHandler = node.getDataEventWatcher();
		DataEventWatcher attributeHandler = attribute.getDataEventWatcher();
		assertNodeState( node, false, 0 );
		assertEventCounts( nodeHandler, 0, 0, 0 );
		assertEventCounts( attributeHandler, 0, 0, 0 );

		node.setAttribute( "attribute", attribute );
		assertNodeState( node, true, 1 );
		assertEventCounts( nodeHandler, 1, 1, 1 );
		assertEventCounts( attributeHandler, 0, 0, 0 );

		node.setModified( false );
		assertNodeState( node, false, 0 );
		assertEventCounts( nodeHandler, 2, 2, 1 );

		// Test setting an attribute on the attribute node modifies the parent.
		attribute.setAttribute( "attribute", "value" );
		assertEquals( attribute.getParent(), node );

		assertNodeState( attribute, true, 1 );
		assertNodeState( node, true, 1 );
		assertEventCounts( nodeHandler, 3, 3, 2 );
		assertEventCounts( attributeHandler, 1, 1, 1 );

		// Test unsetting an attribute on the attribute node unmodified the parent.
		attribute.setModified( false );
		assertNodeState( attribute, false, 0 );
		assertNodeState( node, false, 0 );
		assertEventCounts( nodeHandler, 4, 4, 2 );
		assertEventCounts( attributeHandler, 2, 2, 1 );
	}

	@Test
	public void testChildNodeAttributesClearedByParentClearModified() {
		MockDataNode child = new MockDataNode( "child" );
		MockDataNode parent = new MockDataNode( "parent" );
		DataEventWatcher childHandler = child.getDataEventWatcher();
		DataEventWatcher parentHandler = parent.getDataEventWatcher();
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 0, 0, 0 );
		assertNodeState( parent, false, 0 );
		assertEventCounts( parentHandler, 0, 0, 0 );

		parent.setAttribute( "child", child );
		assertNodeState( parent, true, 1 );
		assertEventCounts( parentHandler, 1, 1, 1 );

		Log.setLevel( Log.DEBUG );
		parent.setModified( false );
		Log.setLevel( Log.NONE );
		assertNodeState( parent, false, 0 );
		assertEventCounts( parentHandler, 2, 2, 1 );

		child.setAttribute( "attribute", "value" );
		assertNodeState( child, true, 1 );
		assertEventCounts( childHandler, 1, 1, 1 );
		assertNodeState( parent, true, 1 );
		assertEventCounts( parentHandler, 3, 3, 2 );

		parent.setModified( false );
		assertNodeState( child, false, 0 );
		assertEventCounts( childHandler, 2, 2, 1 );
		assertNodeState( parent, false, 0 );
		assertEventCounts( parentHandler, 4, 4, 2 );
	}

	@Test
	public void testAddNodeAttributeToDifferentParent() {
		MockDataNode child = new MockDataNode();
		MockDataNode parent0 = new MockDataNode();
		MockDataNode parent1 = new MockDataNode();
		DataEventWatcher childHandler = child.getDataEventWatcher();
		DataEventWatcher parent0Handler = parent0.getDataEventWatcher();
		DataEventWatcher parent1Handler = parent1.getDataEventWatcher();

		// Add the child attribute to parent 0.
		parent0.setAttribute( "child", child );
		assertNodeState( parent0, true, 1 );
		assertEventCounts( childHandler, 0, 0, 0 );
		assertEventCounts( parent0Handler, 1, 1, 1 );
		assertEventCounts( parent1Handler, 0, 0, 0 );

		// Clear the modified flag of parent 0.
		parent0.setModified( false );
		assertNodeState( parent0, false, 0 );
		assertEventCounts( childHandler, 0, 0, 0 );
		assertEventCounts( parent0Handler, 2, 2, 1 );
		assertEventCounts( parent1Handler, 0, 0, 0 );

		// Add the child attribute to parent 1.
		parent1.setAttribute( "child", child );
		assertEquals( child, parent0.getAttribute( "child" ) );
		assertEquals( child, parent1.getAttribute( "child" ) );
		assertNodeState( parent0, false, 0 );
		assertNodeState( parent1, true, 1 );
		assertEventCounts( childHandler, 0, 0, 0 );
		assertEventCounts( parent0Handler, 2, 2, 1 );
		assertEventCounts( parent1Handler, 1, 1, 1 );
	}

	@Test
	public void testAddDataListener() throws Exception {
		MockDataNode node = new MockDataNode();
		DataEventWatcher handler = node.getDataEventWatcher();
		Collection<DataListener> listeners = Accessor.getField( node, "listeners" );

		assertNotNull( listeners );
		assertEquals( 1, listeners.size() );
		assertTrue( listeners.contains( handler ) );
	}

	@Test
	public void testRemoveDataListener() throws Exception {
		MockDataNode node = new MockDataNode();
		DataEventWatcher handler = node.getDataEventWatcher();
		Collection<DataListener> listeners = Accessor.getField( node, "listeners" );

		assertNotNull( listeners );
		assertEquals( 1, listeners.size() );
		assertTrue( listeners.contains( handler ) );

		node.removeDataListener( handler );
		assertEquals( 0, listeners.size() );
		assertFalse( listeners.contains( handler ) );

		node.addDataListener( handler );
		assertEquals( 1, listeners.size() );
		assertTrue( listeners.contains( handler ) );

		node.removeDataListener( handler );
		assertEquals( 0, listeners.size() );
		assertFalse( listeners.contains( handler ) );
	}

	@Test
	public void testCircularReferenceCheck() {
		MockDataNode node = new MockDataNode();
		try {
			node.setAttribute( "node", node );
			fail( "CircularReferenceException should be throw." );
		} catch( CircularReferenceException exception ) {
			// Intentionally ignore exception.
		}
	}

}
