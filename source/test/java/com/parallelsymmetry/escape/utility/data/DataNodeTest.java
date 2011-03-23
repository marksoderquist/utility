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
		assertTrue( "DataNode class is not abstract.", ( DataObject.class.getModifiers() & Modifier.ABSTRACT ) == Modifier.ABSTRACT );
	}

	@Test
	public void testIsModified() {
		MockData data = new MockData();
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertFalse( data.isModified() );

		data.setAttribute( "attribute", "value" );
		assertEquals( 1, data.getModifiedAttributeCount() );
		assertTrue( data.isModified() );

		data.commit();
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertFalse( data.isModified() );
	}

	@Test
	public void testSetAttributeWithNullName() {
		MockData data = new MockData();

		try {
			data.setAttribute( null, "value" );
			fail( "Null attribute names are not allowed." );
		} catch( NullPointerException exception ) {
			assertEquals( "Attribute name cannot be null.", exception.getMessage() );
		}
	}

	@Test
	public void testGetAttributeWithNullName() {
		MockData data = new MockData();

		try {
			data.getAttribute( null );
			fail( "Null attribute names are not allowed." );
		} catch( NullPointerException exception ) {
			assertEquals( "Attribute name cannot be null.", exception.getMessage() );
		}
	}

	@Test
	public void testNullAttributeValues() {
		MockData data = new MockData();
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
		MockData data = new MockData();
		assertEquals( 0, data.getX() );
		assertEquals( 0, data.getY() );
		assertEquals( 0, data.getZ() );

		data.setX( 1 );
		assertEquals( 1, data.getX() );
		assertEquals( 0, data.getY() );
		assertEquals( 0, data.getZ() );

		data.setX( 0 );
		assertEquals( 0, data.getX() );
		assertEquals( 0, data.getY() );
		assertEquals( 0, data.getZ() );
	}

	@Test
	public void testSetAttributeSetsModifiedFlag() {
		MockData data = new MockData();
		assertFalse( data.isModified() );

		data.setX( 1 );
		data.setY( 2 );
		data.setZ( 3 );
		assertTrue( data.isModified() );
	}

	@Test
	public void testUnmodifyAttributeClearsModifiedFlag() {
		MockData data = new MockData();
		assertFalse( data.isModified() );

		data.setX( 1 );
		data.setY( 2 );
		data.setZ( 3 );
		assertTrue( data.isModified() );

		data.setX( 0 );
		data.setY( 0 );
		data.setZ( 0 );
		assertFalse( data.isModified() );
	}

	@Test
	public void testCommitClearsModifiedAttributeCount() {
		MockData data = new MockData();
		assertFalse( data.isModified() );

		data.setX( 1 );
		data.setY( 2 );
		data.setZ( 3 );
		assertEquals( 3, data.getModifiedAttributeCount() );
		assertTrue( data.isModified() );

		data.commit();
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertFalse( data.isModified() );
	}

	public void testResources() {
		DataHandler handler = new DataHandler();
		MockData data = new MockData();
		data.addDataListener( handler );
		assertDataState( data, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.putResource( "name", "value" );
		assertEquals( "value", data.getResource( "name" ) );
		assertDataState( data, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		data.putResource( "name", null );
		assertNull( data.getResource( "name" ) );
		assertDataState( data, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );
	}

	public void testDataEventNotification() {
		DataHandler handler = new DataHandler();
		MockData data = new MockData();
		data.addDataListener( handler );
		assertDataState( data, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		// Insert an attribute.
		data.setAttribute( "attribute", "value0" );
		assertDataState( data, true, 1, 0 );
		assertEventCounts( handler, 1, 1, 1 );

		// Modify the attribute to the same value. Should do nothing.
		data.setAttribute( "attribute", "value0" );
		assertDataState( data, true, 1, 0 );
		assertEventCounts( handler, 1, 1, 1 );

		// Modify the attribute.
		data.setAttribute( "attribute", "value1" );
		assertDataState( data, true, 1, 0 );
		assertEventCounts( handler, 2, 2, 1 );

		// Remove the attribute.
		data.setAttribute( "attribute", null );
		assertDataState( data, false, 0, 0 );
		assertEventCounts( handler, 3, 3, 2 );

		int index = 0;
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.INSERT, data, "attribute", null, "value0" );
		assertEventState( handler, index++, MetaAttributeEvent.class, DataEvent.Type.MODIFY, data, DataObject.MODIFIED, false, true );
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.MODIFY, data, "attribute", "value0", "value1" );
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.REMOVE, data, "attribute", "value1", null );
		assertEventState( handler, index++, MetaAttributeEvent.class, DataEvent.Type.MODIFY, data, DataObject.MODIFIED, true, false );
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

	public void testEventsWithCommit() {
		Log.setLevel( Log.DEBUG );
		DataHandler handler = new DataHandler();
		MockData data = new MockData();
		data.addDataListener( handler );
		assertDataState( data, false, 0, 0 );
		assertEventCounts( handler, 0, 0, 0 );

		// Change an attribute.
		data.setAttribute( "attribute", "value0" );
		assertDataState( data, true, 1, 0 );
		assertEventCounts( handler, 1, 1, 1 );

		// Commit the changes.
		data.commit();
		assertDataState( data, false, 0, 0 );
		assertEventCounts( handler, 2, 1, 2 );

		// Commit again. Should do nothing.
		data.commit();
		assertDataState( data, false, 0, 0 );
		assertEventCounts( handler, 2, 1, 2 );

		int index = 0;
		assertEventState( handler, index++, DataAttributeEvent.class, DataEvent.Type.INSERT, data, "attribute", null, "value0" );
		assertEventState( handler, index++, MetaAttributeEvent.class, DataEvent.Type.MODIFY, data, DataObject.MODIFIED, false, true );
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEventState( handler, index++, MetaAttributeEvent.class, DataEvent.Type.MODIFY, data, DataObject.MODIFIED, true, false );
		assertEventState( handler, index++, DataEvent.class, DataEvent.Type.MODIFY, data );
		assertEquals( index++, handler.getEvents().size() );
	}

}
