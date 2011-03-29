package com.parallelsymmetry.escape.utility.data;

import java.lang.reflect.Modifier;

import org.junit.Test;

import com.parallelsymmetry.escape.utility.log.Log;

public class DataNodeTest extends DataTestCase {

	@Override
	public void setUp() {
		Log.setLevel( Log.NONE );
	}

	@Test
	public void testDataNodeIsAbstract() {
		assertTrue( "DataNode class is not abstract.", ( DataNode.class.getModifiers() & Modifier.ABSTRACT ) == Modifier.ABSTRACT );
	}

	@Test
	public void testIsModified() {
		MockDataNode data = new MockDataNode();
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertFalse( data.isModified() );

		data.setAttribute( "attribute", "value" );
		assertEquals( 1, data.getModifiedAttributeCount() );
		assertTrue( data.isModified() );

		data.clearModified();
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertFalse( data.isModified() );
	}

	@Test
	public void testSetAttributeWithNullName() {
		MockDataNode data = new MockDataNode();

		try {
			data.setAttribute( null, "value" );
			fail( "Null attribute names are not allowed." );
		} catch( NullPointerException exception ) {
			assertEquals( "Attribute name cannot be null.", exception.getMessage() );
		}
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
		assertNull( data.getAttribute( "attribute" ) );

		data.setAttribute( "attribute", null );
		assertNull( data.getAttribute( "attribute" ) );

		data.setAttribute( "attribute", "value" );
		assertEquals( "value", data.getAttribute( "attribute" ) );

		data.setAttribute( "attribute", "value" );
		assertEquals( "value", data.getAttribute( "attribute" ) );

		data.setAttribute( "attribute", null );
		assertNull( data.getAttribute( "attribute" ) );

		data.setAttribute( "attribute", null );
		assertNull( data.getAttribute( "attribute" ) );
	}

	@Test
	public void testGetAndSetAttribute() {
		MockDataNode data = new MockDataNode();
		assertEquals( null, data.getAttribute( "x" ) );
		assertEquals( null, data.getAttribute( "y" ) );
		assertEquals( null, data.getAttribute( "z" ) );

		data.setAttribute( "x", 1 );
		assertEquals( 1, data.getAttribute( "x" ) );
		assertEquals( null, data.getAttribute( "y" ) );
		assertEquals( null, data.getAttribute( "z" ) );

		data.setAttribute( "x", 0 );
		assertEquals( 0, data.getAttribute( "x" ) );
		assertEquals( null, data.getAttribute( "y" ) );
		assertEquals( null, data.getAttribute( "z" ) );
	}

	@Test
	public void testSetAttributeSetsModifiedFlag() {
		MockDataNode data = new MockDataNode();
		assertFalse( data.isModified() );

		data.setAttribute( "x", 1 );
		data.setAttribute( "y", 2 );
		data.setAttribute( "z", 3 );
		assertTrue( data.isModified() );
	}

	@Test
	public void testUnmodifyAttributeClearsModifiedFlag() {
		MockDataNode data = new MockDataNode();
		assertFalse( data.isModified() );

		data.setAttribute( "x", 0 );
		data.setAttribute( "y", 0 );
		data.setAttribute( "z", 0 );
		data.clearModified();
		assertFalse( data.isModified() );

		data.setAttribute( "x", 1 );
		data.setAttribute( "y", 2 );
		data.setAttribute( "z", 3 );
		assertTrue( data.isModified() );

		data.setAttribute( "x", 0 );
		data.setAttribute( "y", 0 );
		data.setAttribute( "z", 0 );
		assertFalse( data.isModified() );
	}

	@Test
	public void testCommitClearsModifiedAttributeCount() {
		MockDataNode data = new MockDataNode();
		assertFalse( data.isModified() );

		data.setAttribute( "x", 1 );
		data.setAttribute( "y", 2 );
		data.setAttribute( "z", 3 );
		assertEquals( 3, data.getModifiedAttributeCount() );
		assertTrue( data.isModified() );

		data.clearModified();
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertFalse( data.isModified() );
	}

	public void testResources() {
		DataHandler handler = new DataHandler();
		MockDataNode data = new MockDataNode();
		data.addDataListener( handler );
		assertDataNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.putResource( "name", "value" );
		assertEquals( "value", data.getResource( "name" ) );
		assertDataNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.putResource( "name", null );
		assertNull( data.getResource( "name" ) );
		assertDataNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );
	}

	public void testDataEventNotification() {
		DataHandler handler = new DataHandler();
		MockDataNode data = new MockDataNode();
		data.addDataListener( handler );
		assertDataNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		// Insert an attribute.
		data.setAttribute( "attribute", "value0" );
		assertDataNodeState( data, true, 1 );
		assertEventCounts( handler, 1, 1, 1 );

		// Modify the attribute to the same value. Should do nothing.
		data.setAttribute( "attribute", "value0" );
		assertDataNodeState( data, true, 1 );
		assertEventCounts( handler, 1, 1, 1 );

		// Modify the attribute.
		data.setAttribute( "attribute", "value1" );
		assertDataNodeState( data, true, 1 );
		assertEventCounts( handler, 2, 2, 1 );

		// Remove the attribute.
		data.setAttribute( "attribute", null );
		assertDataNodeState( data, false, 0 );
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
		Log.setLevel( Log.DEBUG );
		DataHandler handler = new DataHandler();
		MockDataNode data = new MockDataNode();
		data.addDataListener( handler );
		assertDataNodeState( data, false, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		// Change an attribute.
		data.setAttribute( "attribute", "value0" );
		assertDataNodeState( data, true, 1 );
		assertEventCounts( handler, 1, 1, 1 );

		// Commit the changes.
		data.clearModified();
		assertDataNodeState( data, false, 0 );
		assertEventCounts( handler, 2, 1, 2 );

		// Commit again. Should do nothing.
		data.clearModified();
		assertDataNodeState( data, false, 0 );
		assertEventCounts( handler, 2, 1, 2 );

		int index = 0;
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.INSERT, data, "attribute", null, "value0" );
		assertEventState( handler, index++, MetaAttributeEvent.class, DataEvent.Type.MODIFY, data, DataNode.MODIFIED, false, true );
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEventState( handler, index++, MetaAttributeEvent.class, DataEvent.Type.MODIFY, data, DataNode.MODIFIED, true, false );
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

}
