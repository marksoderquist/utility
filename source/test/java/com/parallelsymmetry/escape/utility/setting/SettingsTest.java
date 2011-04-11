package com.parallelsymmetry.escape.utility.setting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.parallelsymmetry.escape.utility.log.Log;

public class SettingsTest extends TestCase {

	private Settings settings = new Settings();

	private MockSettingProvider provider1 = new MockSettingProvider( "Provider 1" );

	private MockSettingProvider provider2 = new MockWritableSettingProvider( "Provider 2" );

	private MockSettingProvider provider3 = new MockSettingProvider( "Provider 3" );

	private MockSettingProvider providerD = new MockSettingProvider( "Provider D" );

	public void setUp() {
		Log.setLevel( Log.NONE );
		settings.addProvider( provider1 );
		settings.addProvider( provider2 );
		settings.addProvider( provider3 );
		settings.setDefaultProvider( providerD );
	}

	public void testGet() {
		String path = "/test/get/value";
		assertNull( settings.get( path, null ) );

		// Setting the value in the default provider should give us a default value.
		providerD.set( path, "D" );
		assertEquals( "D", settings.get( path, null ) );

		// Setting the value in provider 2 should override the default provider.
		provider2.set( path, "2" );
		assertEquals( "2", settings.get( path, null ) );

		// Setting the value in provider 3 should not override provider 2.
		provider3.set( path, "3" );
		assertEquals( "2", settings.get( path, null ) );

		// Setting the value in provider 1 should override provider 2.
		provider1.set( path, "1" );
		assertEquals( "1", settings.get( path, null ) );
	}

	public void testPut() {
		String path = "/test/put/value";
		String value = "X";
		settings.put( path, value );

		// The value should be stored in provider 2 since it is the only one writable.
		assertNull( provider1.get( path ) );
		assertEquals( value, provider2.get( path ) );
		assertNull( provider3.get( path ) );
		assertNull( providerD.get( path ) );
	}

	public void testPutGetColor() {
		settings.putColor( "color", new Color( 73, 74, 99, 128 ) );
		assertEquals( "#80494a63", settings.get( "color", null ) );
		assertEquals( new Color( 73, 74, 99, 128 ), settings.getColor( "color", null ) );
	}

	public void testMounts() {
		settings.addProvider( provider1, "/1" );
		settings.addProvider( provider2, "/2" );
		settings.addProvider( provider3, "/3" );
		settings.setDefaultProvider( providerD, "/D" );

		provider1.set( "/value", "1" );
		provider2.set( "/value", "2" );
		provider3.set( "/value", "3" );
		providerD.set( "/value", "D" );

		assertEquals( "1", settings.get( "/1/value", null ) );
		assertEquals( "2", settings.get( "/2/value", null ) );
		assertEquals( "3", settings.get( "/3/value", null ) );
		assertEquals( "D", settings.get( "/D/value", null ) );

		settings.put( "/1/value", "A" );
		settings.put( "/2/value", "B" );
		settings.put( "/3/value", "C" );
		settings.put( "/D/value", "D" );

		// Remember that provider2 is the only one writable.
		assertEquals( "1", settings.get( "/1/value", null ) );
		assertEquals( "B", settings.get( "/2/value", null ) );
		assertEquals( "3", settings.get( "/3/value", null ) );
		assertEquals( "D", settings.get( "/D/value", null ) );
	}

	public void testNoProviders() {
		assertNull( new Settings().get( "/test", null ) );
	}

	public void testProviderOverride() {
		provider1.set( "/test/path/1", "1" );

		provider2.set( "/test/path/1", "2" );
		provider2.set( "/test/path/2", "2" );

		provider3.set( "/test/path/1", "3" );
		provider3.set( "/test/path/2", "3" );
		provider3.set( "/test/path/3", "3" );

		providerD.set( "/test/path/1", "D" );
		providerD.set( "/test/path/2", "D" );
		providerD.set( "/test/path/3", "D" );
		providerD.set( "/test/path/D", "D" );

		assertEquals( "1", settings.get( "/test/path/1", null ) );
		assertEquals( "2", settings.get( "/test/path/2", null ) );
		assertEquals( "3", settings.get( "/test/path/3", null ) );
		assertEquals( "D", settings.get( "/test/path/D", null ) );
	}

	public void testGetNode() {
		provider1.set( "/test/path/1", "1" );

		provider2.set( "/test/path/1", "2" );
		provider2.set( "/test/path/2", "2" );

		provider3.set( "/test/path/1", "3" );
		provider3.set( "/test/path/2", "3" );
		provider3.set( "/test/path/3", "3" );

		providerD.set( "/test/path/1", "D" );
		providerD.set( "/test/path/2", "D" );
		providerD.set( "/test/path/3", "D" );
		providerD.set( "/test/path/D", "D" );

		Settings node = settings.getNode( "test" );

		assertEquals( "/test", node.getPath() );

		assertEquals( "1", node.get( "path/1", null ) );
		assertEquals( "2", node.get( "path/2", null ) );
		assertEquals( "3", node.get( "path/3", null ) );
		assertEquals( "D", node.get( "path/D", null ) );

		node.put( "path/1", "A" );
		node.put( "path/2", "B" );
		node.put( "path/3", "C" );

		// Remember that provider2 is the only one writable.
		assertEquals( "1", node.get( "path/1", null ) );
		assertEquals( "B", node.get( "path/2", null ) );
		assertEquals( "C", node.get( "path/3", null ) );
		assertEquals( "D", node.get( "path/D", null ) );
	}

	public void testRemoveNode() {
		Settings node = settings.getNode( "/test/path/remove" );
		assertEquals( "/test/path/remove", node.getPath() );

		node.put( "key", "value" );
		assertTrue( settings.nodeExists( "/test/path/remove" ) );
		assertTrue( settings.nodeExists( "/test/path" ) );
		assertTrue( settings.nodeExists( "/test" ) );

		node.removeNode();
		assertFalse( settings.nodeExists( "/test/path/remove" ) );
		assertFalse( settings.nodeExists( "/test/path" ) );
		assertFalse( settings.nodeExists( "/test" ) );
	}

	public void testRemoveNodeWithPath() {
		Settings node = settings.getNode( "/test/path/remove" );
		assertEquals( "/test/path/remove", node.getPath() );

		node.put( "key", "value" );
		assertTrue( settings.nodeExists( "/test/path/remove" ) );
		assertTrue( settings.nodeExists( "/test/path" ) );
		assertTrue( settings.nodeExists( "/test" ) );

		settings.removeNode("/test/path/remove");
		assertFalse( settings.nodeExists( "/test/path/remove" ) );
		assertFalse( settings.nodeExists( "/test/path" ) );
		assertFalse( settings.nodeExists( "/test" ) );
	}

	public void testRemoveNodeWithParentPath() {
		Settings node = settings.getNode( "/test/path/remove" );
		assertEquals( "/test/path/remove", node.getPath() );

		node.put( "key", "value" );
		assertTrue( settings.nodeExists( "/test/path/remove" ) );
		assertTrue( settings.nodeExists( "/test/path" ) );
		assertTrue( settings.nodeExists( "/test" ) );

		settings.removeNode("/test/path");
		assertFalse( settings.nodeExists( "/test/path/remove" ) );
		assertFalse( settings.nodeExists( "/test/path" ) );
		assertFalse( settings.nodeExists( "/test" ) );
	}

	public void testGetEmptyList() {
		List<MockPersistent> list = settings.getList( MockPersistent.class, "/test/lists/list0" );
		assertNotNull( list );
		assertEquals( 0, list.size() );
	}

	public void testPutGetList() {
		int count = 5;
		String path = "/test/lists/list1";
		List<MockPersistent> sourceList = new ArrayList<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceList.add( new MockPersistent( index ) );
		}

		settings.putList( path, sourceList );

		List<MockPersistent> targetList = settings.getList( MockPersistent.class, path );

		assertEquals( count, targetList.size() );
		for( int index = 0; index < count; index++ ) {
			assertEquals( index, targetList.get( index ).getValue() );
		}
	}

	public void testRemoveList() {
		int count = 5;
		String path = "/test/lists/list2";
		List<MockPersistent> sourceList = new ArrayList<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceList.add( new MockPersistent( index ) );
		}

		settings.putList( path, sourceList );

		List<MockPersistent> targetList = settings.getList( MockPersistent.class, path );

		assertEquals( count, targetList.size() );
		for( int index = 0; index < count; index++ ) {
			assertEquals( index, targetList.get( index ).getValue() );
		}

		settings.putList( path, null );

		assertTrue( settings.nodeExists( path ) );
		assertEquals( 0, settings.getList( MockPersistent.class, path ).size() );
	}

	protected void showProviderData( Settings settings ) {
		int pCount = settings.getProviderCount();
		for( int pIndex = 0; pIndex < pCount; pIndex++ ) {
			SettingProvider provider = settings.getProvider( pIndex );
			( (MockSettingProvider)provider ).show();
			System.out.println();
		}
	}

	private static final class MockPersistent implements Persistent<MockPersistent> {

		private int value;

		@SuppressWarnings( "unused" )
		public MockPersistent() {
			this( 0 );
		}

		public MockPersistent( int value ) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		@Override
		public MockPersistent loadSettings( Settings settings ) {
			value = settings.getInt( "value", 0 );
			return this;
		}

		@Override
		public MockPersistent saveSettings( Settings settings ) {
			settings.putInt( "value", value );
			return this;
		}

	}

}
