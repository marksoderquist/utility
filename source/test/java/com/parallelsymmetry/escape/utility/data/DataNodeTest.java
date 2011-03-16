package com.parallelsymmetry.escape.utility.data;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.parallelsymmetry.escape.utility.log.Log;

public class DataNodeTest extends TestCase {

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

	public void testDataEventNotification() {
		Log.setLevel( Log.DEBUG );
		DataHandler handler = new DataHandler();
		MockData data = new MockData();
		data.addDataListener( handler );
		assertFalse( data.isModified() );
		assertEquals( 0, handler.getEvents().size() );

		data.setAttribute( "attribute", "value" );
		assertTrue( data.isModified() );
		assertEquals( 1, data.getModifiedAttributeCount() );
		assertEquals( 1, handler.getEvents().size() );

		DataEvent event0 = handler.getEvents().get( 0 );
		assertEquals( data, event0.getData() );
		assertEquals( null, event0.getOldValue() );
		assertEquals( "attribute", event0.getAttributeName() );
		assertEquals( "value", event0.getNewValue() );
		
		data.setAttribute( "attribute", null );
		assertFalse( data.isModified() );
		assertEquals( 0, data.getModifiedAttributeCount() );
		assertEquals( 2, handler.getEvents().size() );

		DataEvent event1 = handler.getEvents().get( 1 );
		assertEquals( data, event1.getData() );
		assertEquals( "value", event1.getOldValue() );
		assertEquals( "attribute", event1.getAttributeName() );
		assertEquals( null, event1.getNewValue() );
	}

	private static class MockData extends DataObject {

		public int getX() {
			Integer x = getAttribute( "x" );
			return x == null ? 0 : x;
		}

		public void setX( int x ) {
			setAttribute( "x", x == 0 ? null : x );
		}

		public int getY() {
			Integer y = getAttribute( "y" );
			return y == null ? 0 : y;
		}

		public void setY( int y ) {
			setAttribute( "y", y == 0 ? null : y );
		}

		public int getZ() {
			Integer z = getAttribute( "z" );
			return z == null ? 0 : z;
		}

		public void setZ( int z ) {
			setAttribute( "z", z == 0 ? null : z );
		}

	}

	private static class DataHandler implements DataListener {

		private List<DataEvent> events = new ArrayList<DataEvent>();

		@Override
		public void dataAttributeChanged( DataEvent event ) {
			events.add( event );
		}

		public List<DataEvent> getEvents() {
			return events;
		}

	}

}
