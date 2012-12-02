package com.parallelsymmetry.utility.setting;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.parallelsymmetry.utility.log.Log;
import com.parallelsymmetry.utility.mock.MockSettingProvider;
import com.parallelsymmetry.utility.mock.MockWritableSettingProvider;

public class SettingsTest extends TestCase {

	private Settings settings = new Settings();

	private MockSettingProvider provider1 = new MockSettingProvider( "Provider 1" );

	private MockSettingProvider provider2 = new MockWritableSettingProvider( "Provider 2" );

	private MockSettingProvider provider3 = new MockSettingProvider( "Provider 3" );

	private MockSettingProvider providerD = new MockSettingProvider( "Provider D" );

	@Override
	public void setUp() {
		Log.setLevel( Log.NONE );
		settings.addProvider( provider1 );
		settings.addProvider( provider2 );
		settings.addProvider( provider3 );
		settings.setDefaultProvider( providerD );
	}

	public void testGetName() {
		assertEquals( "", settings.getName() );
		assertEquals( "test", settings.getNode( "/test" ).getName() );
		assertEquals( "name", settings.getNode( "/test/name" ).getName() );
	}

	public void testGetPath() {
		assertEquals( "/", settings.getPath() );
		assertEquals( "/test", settings.getNode( "/test" ).getPath() );
		assertEquals( "/test/name", settings.getNode( "/test/name" ).getPath() );
		assertEquals( "/test/name", settings.getNode( "test" ).getNode( "name" ).getPath() );
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

	public void testGetWithSelf() {
		String path = "/test/get/value";
		settings.put( path, "5" );
		assertEquals( "5", settings.get( path, null ) );

		Settings self = settings.getNode( path );
		assertEquals( "5", self.get( ".", null ) );
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

	public void testPutGetInt() {
		settings.putInt( "int", 73 );
		assertEquals( "73", settings.get( "int", null ) );
		assertEquals( 73, settings.getInt( "int", -1 ) );
	}

	public void testPutGetFloat() {
		settings.putFloat( "float", 2.718283f );
		assertEquals( "2.718283", settings.get( "float", null ) );
		assertEquals( 2.718283f, settings.getFloat( "float", Float.NaN ) );

		settings.putFloat( "float-nan", Float.NaN );
		assertEquals( Float.NaN, settings.getFloat( "float-nan", 0 ) );
	}

	public void testPutGetDouble() {
		settings.putDouble( "double", 3.141593 );
		assertEquals( "3.141593", settings.get( "double", null ) );
		assertEquals( 3.141593, settings.getDouble( "double", Double.NaN ) );

		settings.putDouble( "double-nan", Double.NaN );
		assertEquals( Double.NaN, settings.getDouble( "double-nan", 0 ) );
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

	public void testGetChildNames() {
		provider1.set( "/test/path1/value", "1" );
		// Intentionally skip provider 2.
		provider3.set( "/test/path3/value", "3" );
		providerD.set( "/test/pathD/value", "D" );

		Set<String> names = settings.getChildNames( "/test" );
		assertEquals( 3, names.size() );
		assertTrue( names.contains( "path1" ) );
		assertTrue( names.contains( "path3" ) );
		assertTrue( names.contains( "pathD" ) );

		names = settings.getChildNames( "test" );
		assertEquals( 3, names.size() );
		assertTrue( names.contains( "path1" ) );
		assertTrue( names.contains( "path3" ) );
		assertTrue( names.contains( "pathD" ) );

		names = settings.getNode( "/test" ).getChildNames( "." );
		assertEquals( 3, names.size() );
		assertTrue( names.contains( "path1" ) );
		assertTrue( names.contains( "path3" ) );
		assertTrue( names.contains( "pathD" ) );

		names = settings.getNode( "test" ).getChildNames( "." );
		assertEquals( 3, names.size() );
		assertTrue( names.contains( "path1" ) );
		assertTrue( names.contains( "path3" ) );
		assertTrue( names.contains( "pathD" ) );
	}

	public void testGetChildNodes() {
		provider1.set( "/test/path1/value", "1" );
		// Intentionally skip provider 2.
		provider3.set( "/test/path3/value", "3" );
		providerD.set( "/test/pathD/value", "D" );

		Set<Settings> nodes = settings.getNode( "/test" ).getChildNodes();
		assertEquals( 3, nodes.size() );
		assertTrue( nodes.contains( settings.getNode( "/test/path1" ) ) );
		assertTrue( nodes.contains( settings.getNode( "/test/path3" ) ) );
		assertTrue( nodes.contains( settings.getNode( "/test/pathD" ) ) );

		nodes = settings.getNode( "test" ).getChildNodes();
		assertEquals( 3, nodes.size() );
		assertTrue( nodes.contains( settings.getNode( "/test/path1" ) ) );
		assertTrue( nodes.contains( settings.getNode( "/test/path3" ) ) );
		assertTrue( nodes.contains( settings.getNode( "/test/pathD" ) ) );
	}

	public void testGetChildNodesWithPath() {
		provider1.set( "/test/path1/value", "1" );
		// Intentionally skip provider 2.
		provider3.set( "/test/path3/value", "3" );
		providerD.set( "/test/pathD/value", "D" );

		Set<Settings> nodes = settings.getChildNodes( "/test" );
		assertEquals( 3, nodes.size() );
		assertTrue( nodes.contains( settings.getNode( "/test/path1" ) ) );
		assertTrue( nodes.contains( settings.getNode( "/test/path3" ) ) );
		assertTrue( nodes.contains( settings.getNode( "/test/pathD" ) ) );

		nodes = settings.getNode( "/test" ).getChildNodes( "." );
		assertEquals( 3, nodes.size() );
		assertTrue( nodes.contains( settings.getNode( "/test/path1" ) ) );
		assertTrue( nodes.contains( settings.getNode( "/test/path3" ) ) );
		assertTrue( nodes.contains( settings.getNode( "/test/pathD" ) ) );

		nodes = settings.getChildNodes( "test" );
		assertEquals( 3, nodes.size() );
		assertTrue( nodes.contains( settings.getNode( "/test/path1" ) ) );
		assertTrue( nodes.contains( settings.getNode( "/test/path3" ) ) );
		assertTrue( nodes.contains( settings.getNode( "/test/pathD" ) ) );
	}

	public void testGetIndexedNodes() {
		int count = 5;
		String path = "/test/lists/list1";
		List<MockPersistent> sourceList = new ArrayList<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceList.add( new MockPersistent( index ) );
		}

		settings.putList( path, sourceList );

		int index = 0;
		for( Settings itemSettings : settings.getIndexedNodes( "/test/lists/list1" ) ) {
			assertEquals( "item-" + index++, itemSettings.getName() );
		}
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

		node = settings.getNode( "test" ).getNode( "path" );
		assertEquals( "/test/path", node.getPath() );
		assertEquals( "1", node.get( "1", null ) );
		assertEquals( "B", node.get( "2", null ) );
		assertEquals( "C", node.get( "3", null ) );
		assertEquals( "D", node.get( "D", null ) );
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

		settings.removeNode( "/test/path/remove" );
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

		settings.removeNode( "/test/path" );
		assertFalse( settings.nodeExists( "/test/path/remove" ) );
		assertFalse( settings.nodeExists( "/test/path" ) );
		assertFalse( settings.nodeExists( "/test" ) );
	}

	public void testGetNodeList() {
		int count = 5;
		String path = "/test/lists/list1";
		List<MockPersistent> sourceList = new ArrayList<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceList.add( new MockPersistent( index ) );
		}

		settings.putNodeList( path, sourceList );

		List<Settings> targetList = settings.getNodeList( path, null );

		// Create a check list of the settings objects.
		List<Settings> checkList = new ArrayList<Settings>();
		for( MockPersistent object : sourceList ) {
			Settings settings = new Settings();
			settings.addProvider( new MapSettingProvider() );
			object.saveSettings( settings );
			checkList.add( settings );
		}

		assertEquals( count, targetList.size() );
		for( int index = 0; index < count; index++ ) {
			assertSettingsValues( checkList.get( index ), targetList.get( index ) );
		}
	}

	public void testGetList() {
		int count = 5;
		String path = "/test/lists/list1";
		List<MockPersistent> sourceList = new ArrayList<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceList.add( new MockPersistent( index ) );
		}

		settings.putList( path, sourceList );

		List<MockPersistent> targetList = settings.getList( path, null );

		assertEquals( count, targetList.size() );
		for( int index = 0; index < count; index++ ) {
			assertEquals( index, targetList.get( index ).getIndex() );
		}
	}

	public void testGetListWithDefault() {
		List<MockPersistent> list = settings.getList( "/test/lists/list0", null );
		assertNull( list );

		List<MockPersistent> defaultList = new ArrayList<MockPersistent>();
		defaultList.add( new MockPersistent() );
		list = settings.getList( "/test/lists/list0", defaultList );
		assertNotNull( defaultList.get( 0 ).getSettings() );
	}

	public void testPutEmptyList() {
		String path = "/test/lists/list0";
		List<MockPersistent> list = new ArrayList<MockPersistent>();
		settings.putList( path, list );
	}

	public void testRemoveList() {
		int count = 5;
		String path = "/test/lists/list2";
		List<MockPersistent> sourceList = new ArrayList<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceList.add( new MockPersistent( index ) );
		}

		settings.putList( path, sourceList );
		List<MockPersistent> targetList = settings.getList( path, null );

		assertEquals( count, targetList.size() );
		for( int index = 0; index < count; index++ ) {
			assertEquals( index, targetList.get( index ).getIndex() );
		}

		settings.putList( path, null );

		targetList = settings.getList( path, null );
		assertFalse( settings.nodeExists( path ) );
		assertNull( targetList );
	}

	public void testGetSet() {
		int count = 5;
		String path = "/test/sets/set1";
		Set<MockPersistent> sourceSet = new HashSet<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceSet.add( new MockPersistent( index ) );
		}

		settings.putSet( path, sourceSet );

		Set<MockPersistent> targetSet = settings.getSet( path, null );

		assertEquals( count, targetSet.size() );

		for( MockPersistent item : sourceSet ) {
			assertTrue( targetSet.contains( item ) );
		}
	}

	public void testGetSetWithDefault() {
		Set<MockPersistent> set = settings.getSet( "/test/sets/set0", null );
		assertNull( set );

		Set<MockPersistent> defaultSet = new HashSet<MockPersistent>();
		defaultSet.add( new MockPersistent() );
		set = settings.getSet( "/test/sets/set0", defaultSet );
		assertEquals( defaultSet, set );
		assertNotNull( defaultSet.iterator().next().getSettings() );
	}

	public void testPutEmptySet() {
		String path = "/test/sets/set0";
		Set<MockPersistent> set = new HashSet<MockPersistent>();
		settings.putSet( path, set );
	}

	public void testRemoveSet() {
		int count = 5;
		String path = "/test/sets/set2";
		Set<MockPersistent> sourceSet = new HashSet<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceSet.add( new MockPersistent( index ) );
		}

		settings.putSet( path, sourceSet );
		Set<MockPersistent> targetSet = settings.getSet( path, null );

		assertEquals( count, targetSet.size() );
		for( MockPersistent item : sourceSet ) {
			assertTrue( targetSet.contains( item ) );
		}

		settings.putSet( path, null );

		targetSet = settings.getSet( path, null );
		assertFalse( settings.nodeExists( path ) );
		assertNull( targetSet );
	}

	public void testGetMap() {
		int count = 5;
		String path = "/test/maps/map1";
		Map<String, MockPersistent> sourceMap = new HashMap<String, MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			String name = String.valueOf( (char)( 65 + index ) );
			sourceMap.put( name, new MockPersistent( name ) );
		}

		settings.putMap( path, sourceMap );
		Map<String, MockPersistent> targetMap = settings.getMap( path, null );

		assertEquals( count, targetMap.size() );
		for( int index = 0; index < count; index++ ) {
			String name = String.valueOf( (char)( 65 + index ) );
			assertEquals( name, targetMap.get( name ).getValue() );
		}
	}

	public void testGetMapWithDefault() {
		Map<String, MockPersistent> map = settings.getMap( "/test/maps/map0", null );
		assertNull( map );

		Map<String, MockPersistent> defaultMap = new HashMap<String, MockPersistent>();
		defaultMap.put( "0", new MockPersistent() );
		map = settings.getMap( "/test/maps/map0", defaultMap );
		assertNotNull( defaultMap.get( "0" ).getSettings() );
	}

	public void testPutEmptyMap() {
		String path = "/test/maps/map0";
		Map<String, MockPersistent> map = new HashMap<String, MockPersistent>();
		settings.putMap( path, map );
	}

	public void testRemoveMap() {
		int count = 5;
		String path = "/test/maps/map1";
		Map<String, MockPersistent> sourceMap = new HashMap<String, MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			String name = String.valueOf( (char)( 65 + index ) );
			sourceMap.put( name, new MockPersistent( name ) );
		}

		settings.putMap( path, sourceMap );
		settings.putMap( path, null );

		Map<String, MockPersistent> targetMap = settings.getMap( path, null );
		assertFalse( settings.nodeExists( path ) );
		assertNull( targetMap );
	}

	public void testReset() {
		assertFalse( settings.nodeExists( "/test/path/remove" ) );
		assertFalse( settings.nodeExists( "/test/path" ) );
		assertFalse( settings.nodeExists( "/test" ) );

		Settings node = settings.getNode( "/test/path/remove" );
		assertEquals( "/test/path/remove", node.getPath() );

		node.put( "key", "value" );
		assertTrue( settings.nodeExists( "/test/path/remove" ) );
		assertTrue( settings.nodeExists( "/test/path" ) );
		assertTrue( settings.nodeExists( "/test" ) );

		settings.reset();
		assertFalse( settings.nodeExists( "/test/path/remove" ) );
		assertFalse( settings.nodeExists( "/test/path" ) );
		assertFalse( settings.nodeExists( "/test" ) );

	}

	public void testGetParentPath() {
		assertEquals( null, Settings.getParentPath( "/" ) );
		assertEquals( "/", Settings.getParentPath( "/path/" ) );
		assertEquals( "/", Settings.getParentPath( "/element" ) );
		assertEquals( "/path/to", Settings.getParentPath( "/path/to/path/" ) );
		assertEquals( "/path/to", Settings.getParentPath( "/path/to/setting" ) );
	}

	public void testGetSettingKey() {
		assertEquals( null, Settings.getSettingKey( "/" ) );
		assertEquals( "setting", Settings.getSettingKey( "/path/to/setting" ) );
	}

	public void testSettingResources() {
		String key = "key";
		String object = new String( "value" );
		settings.putResource( key, object );
		assertEquals( object, settings.getResource( key ) );
		assertEquals( object, settings.getNode( "/test/path" ).getResource( key ) );
	}

	protected void showProviderData( Settings settings ) {
		int pCount = settings.getProviderCount();
		for( int pIndex = 0; pIndex < pCount; pIndex++ ) {
			SettingProvider provider = settings.getProvider( pIndex );
			( (MockSettingProvider)provider ).print();
			System.out.println();
		}
	}

	private void assertSettingsValues( Settings expected, Settings actual ) {
		assertEquals( expected.getKeys().size(), actual.getKeys().size() );

		Set<String> expectedKeys = expected.getKeys();
		for( String name : expectedKeys ) {
			assertEquals( name, expected.get( name, null ), actual.get( name, null ) );
		}
	}

	private static final class MockPersistent implements Persistent {

		private int index;

		private String value;

		private Settings settings;

		public MockPersistent() {
			this( 0 );
		}

		public MockPersistent( int index ) {
			this.index = index;
		}

		public MockPersistent( String value ) {
			this.value = value;
		}

		public int getIndex() {
			return index;
		}

		public String getValue() {
			return value;
		}

		public Settings getSettings() {
			return settings;
		}

		@Override
		public void loadSettings( Settings settings ) {
			this.settings = settings;
			value = settings.get( "value", null );
			index = settings.getInt( "index", 0 );
		}

		@Override
		public void saveSettings( Settings settings ) {
			settings.put( "value", value );
			settings.putInt( "index", index );
		}

		@Override
		public int hashCode() {
			return value == null ? index : value.hashCode();
		}

		@Override
		public boolean equals( Object object ) {
			if( !( object instanceof MockPersistent ) ) return false;
			MockPersistent that = (MockPersistent)object;
			return value == null ? this.index == that.index : this.value.equals( that.value );
		}

	}

}