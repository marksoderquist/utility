package com.parallelsymmetry.utility.setting;

import com.parallelsymmetry.utility.BaseTestCase;
import com.parallelsymmetry.utility.Version;
import com.parallelsymmetry.utility.mock.MockSettingsProvider;
import com.parallelsymmetry.utility.mock.MockWritableSettingsProvider;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsTest extends BaseTestCase {

	private final Settings settings = new Settings();

	private final MockSettingsProvider provider1 = new MockSettingsProvider( "Provider 1" );

	private final MockSettingsProvider provider2 = new MockWritableSettingsProvider( "Provider 2" );

	private final MockSettingsProvider provider3 = new MockSettingsProvider( "Provider 3" );

	private final MockSettingsProvider providerD = new MockSettingsProvider( "Provider D" );

	@BeforeEach
	@Override
	public void setup() throws Exception {
		super.setup();
		settings.addProvider( provider1 );
		settings.addProvider( provider2 );
		settings.addProvider( provider3 );
		settings.setDefaultProvider( providerD );
	}

	@Test
	public void testGetName() {
		assertEquals( "", settings.getName() );
		assertEquals( "test", settings.getNode( "/test" ).getName() );
		assertEquals( "name", settings.getNode( "/test/name" ).getName() );
	}

	@Test
	public void testGetPath() {
		assertEquals( "/", settings.getPath() );
		assertEquals( "/test", settings.getNode( "/test" ).getPath() );
		assertEquals( "/test/name", settings.getNode( "/test/name" ).getPath() );
		assertEquals( "/test/name", settings.getNode( "test" ).getNode( "name" ).getPath() );
	}

	@Test
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

	@Test
	public void testGetWithSelf() {
		String path = "/test/get/value";
		settings.put( path, "5" );
		assertEquals( "5", settings.get( path, null ) );

		Settings self = settings.getNode( path );
		assertEquals( "5", self.get( ".", null ) );
	}

	@Test
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

	@Test
	public void testPutGetInt() {
		settings.putInt( "int", 73 );
		assertEquals( "73", settings.get( "int", null ) );
		assertEquals( 73, settings.getInt( "int", -1 ) );
	}

	@Test
	public void testPutGetLong() {
		settings.putLong( "long", 23234993237L );
		assertEquals( "23234993237", settings.get( "long", null ) );
		assertEquals( 23234993237L, settings.getLong( "long", -1 ) );
	}

	@Test
	public void testPutGetFloat() {
		settings.putFloat( "float", 2.718283f );
		assertEquals( "2.718283", settings.get( "float", null ) );
		assertEquals( 2.718283f, settings.getFloat( "float", Float.NaN ) );

		settings.putFloat( "float-nan", Float.NaN );
		assertEquals( Float.NaN, settings.getFloat( "float-nan", 0 ) );
	}

	@Test
	public void testPutGetDouble() {
		settings.putDouble( "double", 3.141593 );
		assertEquals( "3.141593", settings.get( "double", null ) );
		assertEquals( 3.141593, settings.getDouble( "double", Double.NaN ) );

		settings.putDouble( "double-nan", Double.NaN );
		assertEquals( Double.NaN, settings.getDouble( "double-nan", 0 ) );
	}

	@Test
	public void testPutGetColor() {
		settings.putColor( "color", new Color( 73, 74, 99, 128 ) );
		assertEquals( "#80494a63", settings.get( "color", null ) );
		assertEquals( new Color( 73, 74, 99, 128 ), settings.getColor( "color", null ) );
	}

	@Test
	public void testCopy() {
		settings.put( "/a", "A" );
		settings.put( "/b", "B" );
		settings.put( "/p/a", "A" );
		settings.put( "/p/b", "B" );

		Settings newSettings = new Settings();
		newSettings.addProvider( new MapSettingsProvider() );

		settings.copyTo( newSettings );

		assertEquals( 2, newSettings.getKeys().size() );
		assertEquals( 0, newSettings.getChildCount() );
	}

	@Test
	public void testDeepCopy() {
		settings.put( "/a", "A" );
		settings.put( "/b", "B" );
		settings.put( "/p/a", "A" );
		settings.put( "/p/b", "B" );

		Settings newSettings = new Settings();
		newSettings.addProvider( new MapSettingsProvider() );

		settings.copyDeepTo( newSettings );

		assertEquals( 2, newSettings.getKeys().size() );
		assertEquals( 1, newSettings.getChildCount() );
	}

	@Test
	public void testMounts() {
		settings.setDefaultProvider( providerD );

		// Do not set a provider for path /1, just use the defaults.
		settings.addProvider( provider2, "/2" );
		settings.addProvider( provider3, "/3" );

		providerD.set( "/1/value", "D" );
		providerD.set( "/2/value", "D" );
		providerD.set( "/3/value", "D" );

		provider1.set( "/value", "1" );
		provider2.set( "/value", "2" );
		provider3.set( "/value", "3" );

		assertEquals( "D", settings.get( "/1/value", null ) );
		assertEquals( "2", settings.get( "/2/value", null ) );
		assertEquals( "3", settings.get( "/3/value", null ) );

		// Remember that provider2 is the only one writable.
		settings.put( "/1/value", "A" );
		settings.put( "/2/value", "B" );
		settings.put( "/3/value", "C" );

		// Remember that provider2 is the only one writable.
		assertEquals( "D", settings.get( "/1/value", null ) );
		assertEquals( "B", settings.get( "/2/value", null ) );
		assertEquals( "3", settings.get( "/3/value", null ) );
	}

	@Test
	public void testNoProviders() {
		assertNull( new Settings().get( "/test", null ) );
	}

	@Test
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

	@Test
	public void testGetKeys() {
		Set<String> keys = settings.getKeys();
		assertEquals( 0, keys.size() );

		provider1.set( "/value1", "1" );
		provider2.set( "/value2", "2" );
		provider3.set( "/value3", "3" );
		providerD.set( "/valueD", "D" );

		assertEquals( 4, settings.getKeys().size() );
	}

	@Test
	public void testGetChildCount() {
		assertEquals( 0, settings.getChildCount() );

		provider1.set( "/test/path1/value", "1" );
		// Intentionally skip provider 2.
		provider3.set( "/test/path3/value", "3" );
		providerD.set( "/test/pathD/value", "D" );

		assertEquals( 1, settings.getChildCount() );
	}

	@Test
	public void testGetChildCountWithString() {
		assertEquals( 0, settings.getChildCount( "/test" ) );

		provider1.set( "/test/path1/value", "1" );
		// Intentionally skip provider 2.
		provider3.set( "/test/path3/value", "3" );
		providerD.set( "/test/pathD/value", "D" );

		assertEquals( 3, settings.getChildCount( "/test" ) );
	}

	@Test
	public void testGetChildNames() {
		assertEquals( 0, settings.getChildNames().size() );

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

	@Test
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

	@Test
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

	@Test
	public void testGetIndexedNodes() {
		int count = 5;
		String path = "/test/lists/list1";
		List<MockPersistent> sourceList = new ArrayList<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceList.add( new MockPersistent( index ) );
		}

		settings.putNodeList( path, sourceList );

		int index = 0;
		for( Settings itemSettings : settings.getIndexedNodes( "/test/lists/list1" ) ) {
			assertEquals( "item-" + index, itemSettings.getName() );
			assertEquals( "/test/lists/list1/item-" + index, itemSettings.getPath() );
			index++;
		}
	}

	@Test
	public void testGetIndexedNodesAtRoot() {
		int count = 5;
		String path = "/";
		List<MockPersistent> sourceList = new ArrayList<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceList.add( new MockPersistent( index ) );
		}

		settings.putNodeList( path, sourceList );

		int index = 0;
		for( Settings itemSettings : settings.getIndexedNodes() ) {
			assertEquals( "item-" + index, itemSettings.getName() );
			assertEquals( "/item-" + index, itemSettings.getPath() );
			index++;
		}
		assertEquals( count, index );
	}

	@Test
	public void testGetIndexedNodesAtRootWithPath() {
		int count = 5;
		String path = "/";
		List<MockPersistent> sourceList = new ArrayList<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceList.add( new MockPersistent( index ) );
		}

		settings.putNodeList( path, sourceList );

		int index = 0;
		for( Settings itemSettings : settings.getIndexedNodes( "/" ) ) {
			assertEquals( "item-" + index, itemSettings.getName() );
			assertEquals( "/item-" + index, itemSettings.getPath() );
			index++;
		}
		assertEquals( count, index );
	}

	@Test
	public void testGetIndexedNodesAtRootWithEmptyPath() {
		int count = 5;
		String path = "/";
		List<MockPersistent> sourceList = new ArrayList<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceList.add( new MockPersistent( index ) );
		}

		settings.putNodeList( path, sourceList );

		int index = 0;
		for( Settings itemSettings : settings.getIndexedNodes( "" ) ) {
			assertEquals( "item-" + index, itemSettings.getName() );
			assertEquals( "/item-" + index, itemSettings.getPath() );
			index++;
		}
		assertEquals( count, index );
	}

	@Test
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

	@Test
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

	@Test
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

	@Test
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

	@Test
	public void testGetList() {
		int count = 5;
		String path = "/test/lists/list1";
		List<MockPersistent> sourceList = new ArrayList<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceList.add( new MockPersistent( index ) );
		}

		// Create a check list of the settings objects.
		List<Settings> checkList = new ArrayList<Settings>();
		for( MockPersistent object : sourceList ) {
			Settings settings = new Settings();
			settings.addProvider( new MapSettingsProvider() );
			object.saveSettings( settings );
			checkList.add( settings );
		}

		settings.putNodeList( path, sourceList );

		List<Settings> targetList = settings.getNodeList( path, null );
		assertEquals( count, targetList.size() );
		for( int index = 0; index < count; index++ ) {
			assertSettingsValues( checkList.get( index ), targetList.get( index ) );
		}
	}

	@Test
	public void testGetInvalidList() {
		assertNull( settings.getNodeList( "/invalid", null ) );
	}

	@Test
	public void testGetListWithDefault() {
		String path = "/test/lists/list4";
		List<Settings> list = settings.getNodeList( path, null );
		assertNull( list );

		List<MockPersistent> defaultList = new ArrayList<MockPersistent>();
		defaultList.add( new MockPersistent() );
		list = settings.getNodeList( path, defaultList );

		assertEquals( 1, list.size() );
		assertNotNull( defaultList.get( 0 ).getSettings() );
		assertEquals( list.get( 0 ), defaultList.get( 0 ).getSettings() );
	}

	@Test
	public void testPutEmptyList() {
		String path = "/test/lists/list0";
		List<MockPersistent> list = new ArrayList<MockPersistent>();
		settings.putNodeList( path, list );
		assertEquals( 0, settings.getNodeList( path, null ).size() );
	}

	@Test
	public void testRemoveList() {
		int count = 5;
		String path = "/test/lists/list2";
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
			settings.addProvider( new MapSettingsProvider() );
			object.saveSettings( settings );
			checkList.add( settings );
		}

		assertEquals( count, targetList.size() );
		for( int index = 0; index < count; index++ ) {
			assertSettingsValues( checkList.get( index ), targetList.get( index ) );
		}

		settings.putNodeList( path, null );

		targetList = settings.getNodeList( path, null );
		assertFalse( settings.nodeExists( path ) );
		assertNull( targetList );
	}

	@Test
	public void testPutSetEvents() {
		int count = 5;
		String path = "/test/sets/set5";
		Set<MockPersistent> sourceSet = new HashSet<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceSet.add( new MockPersistent( index ) );
		}

		settings.removeNode( path );
		assertFalse( settings.nodeExists( path ) );

		MockSettingListener listener = new MockSettingListener();
		settings.addSettingListener( path, listener );

		settings.putNodeSet( path, sourceSet );
		assertTrue( settings.nodeExists( path ) );

		// The event count is 6 but should be 1.
		assertEquals( 6, listener.getEvents().size() );
	}

	@Test
	public void testGetSet() {
		int count = 5;
		String path = "/test/sets/set1";
		Set<MockPersistent> sourceSet = new HashSet<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceSet.add( new MockPersistent( index ) );
		}

		settings.putNodeSet( path, sourceSet );

		Set<Settings> targetSet = settings.getNodeSet( path, null );

		// Create a check list of the settings objects.
		List<Settings> checkList = new ArrayList<Settings>();
		for( MockPersistent object : sourceSet ) {
			Settings settings = new Settings();
			settings.addProvider( new MapSettingsProvider() );
			object.saveSettings( settings );
			checkList.add( settings );
		}

		assertEquals( count, targetSet.size() );
		for( int index = 0; index < count; index++ ) {
			boolean found = false;
			for( Settings settings : targetSet ) {
				found = areEqualsByValue( checkList.get( index ), settings );
				if( found ) break;
			}
			assertTrue( found );
		}
	}

	@Test
	public void testGetInvalidSet() {
		assertNull( settings.getNodeSet( "/invalid", null ) );
	}

	@Test
	public void testGetSetWithDefault() {
		String path = "/test/sets/set4";
		Set<Settings> set = settings.getNodeSet( path, null );
		assertNull( set );

		Set<MockPersistent> defaultSet = new HashSet<MockPersistent>();
		defaultSet.add( new MockPersistent() );
		set = settings.getNodeSet( path, defaultSet );

		assertEquals( 1, set.size() );
		assertNotNull( defaultSet.iterator().next().getSettings() );
		assertEquals( set.iterator().next(), defaultSet.iterator().next().getSettings() );
	}

	@Test
	public void testPutEmptySet() {
		String path = "/test/sets/set0";
		Set<MockPersistent> set = new HashSet<MockPersistent>();
		settings.putNodeSet( path, set );
		assertEquals( 0, settings.getNodeSet( path, null ).size() );
	}

	@Test
	public void testRemoveSet() {
		int count = 5;
		String path = "/test/sets/set2";
		Set<MockPersistent> sourceSet = new HashSet<MockPersistent>();

		for( int index = 0; index < count; index++ ) {
			sourceSet.add( new MockPersistent( index ) );
		}

		settings.putNodeSet( path, sourceSet );

		Set<Settings> targetSet = settings.getNodeSet( path, null );

		// Create a check list of the settings objects.
		List<Settings> checkList = new ArrayList<Settings>();
		for( MockPersistent object : sourceSet ) {
			Settings settings = new Settings();
			settings.addProvider( new MapSettingsProvider() );
			object.saveSettings( settings );
			checkList.add( settings );
		}

		assertEquals( count, targetSet.size() );
		for( int index = 0; index < count; index++ ) {
			boolean found = false;
			for( Settings settings : targetSet ) {
				found = areEqualsByValue( checkList.get( index ), settings );
				if( found ) break;
			}
			assertTrue( found );
		}

		settings.putNodeSet( path, null );

		targetSet = settings.getNodeSet( path, null );
		assertFalse( settings.nodeExists( path ) );
		assertNull( targetSet );
	}

	@Test
	public void testGetMap() {
		int count = 5;
		String path = "/test/maps/map1";
		Map<String, MockPersistent> sourceMap = new HashMap<>();

		for( int index = 0; index < count; index++ ) {
			String name = String.valueOf( (char)(65 + index) );
			sourceMap.put( name, new MockPersistent( name ) );
		}

		// Create a check list of the settings objects.
		Map<String, Settings> checkMap = new HashMap<>();
		for( String name : sourceMap.keySet() ) {
			Settings settings = new Settings();
			settings.addProvider( new MapSettingsProvider() );
			sourceMap.get( name ).saveSettings( settings );
			checkMap.put( name, settings );
		}

		settings.putNodeMap( path, sourceMap );

		Map<String, Settings> targetMap = settings.getNodeMap( path, null );
		assertEquals( count, targetMap.size() );
		for( int index = 0; index < count; index++ ) {
			String name = String.valueOf( (char)(65 + index) );
			assertSettingsValues( checkMap.get( name ), targetMap.get( name ) );
		}
	}

	@Test
	public void testGetInvalidMap() {
		assertNull( settings.getNodeMap( "/invalid", null ) );
	}

	@Test
	public void testGetMapWithDefault() {
		String path = "/test/maps/map4";
		Map<String, Settings> map = settings.getNodeMap( path, null );
		assertNull( map );

		Map<String, MockPersistent> defaultMap = new HashMap<>();
		defaultMap.put( "0", new MockPersistent() );
		map = settings.getNodeMap( path, defaultMap );

		assertEquals( 1, map.size() );
		assertNotNull( defaultMap.get( "0" ).getSettings() );
		assertEquals( map.get( "0" ), defaultMap.get( "0" ).getSettings() );
	}

	@Test
	public void testPutEmptyMap() {
		String path = "/test/maps/map0";
		Map<String, MockPersistent> map = new HashMap<>();
		settings.putNodeMap( path, map );
		assertEquals( 0, settings.getNodeSet( path, null ).size() );
	}

	@Test
	public void testRemoveMap() {
		int count = 5;
		String path = "/test/maps/map2";
		Map<String, MockPersistent> sourceMap = new HashMap<>();

		for( int index = 0; index < count; index++ ) {
			String name = String.valueOf( (char)(65 + index) );
			sourceMap.put( name, new MockPersistent( name ) );
		}

		// Create a checklist of the settings objects.
		Map<String, Settings> checkMap = new HashMap<>();
		for( String name : sourceMap.keySet() ) {
			Settings settings = new Settings();
			settings.addProvider( new MapSettingsProvider() );
			sourceMap.get( name ).saveSettings( settings );
			checkMap.put( name, settings );
		}

		settings.putNodeMap( path, sourceMap );

		Map<String, Settings> targetMap = settings.getNodeMap( path, null );
		assertEquals( count, targetMap.size() );
		for( int index = 0; index < count; index++ ) {
			String name = String.valueOf( (char)(65 + index) );
			assertSettingsValues( checkMap.get( name ), targetMap.get( name ) );
		}

		settings.putNodeMap( path, null );

		targetMap = settings.getNodeMap( path, null );
		assertFalse( settings.nodeExists( path ) );
		assertNull( targetMap );
	}

	@Test
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

	@Test
	public void testGetParentPath() {
		assertEquals( null, Settings.getParentPath( "/" ) );
		assertEquals( "/", Settings.getParentPath( "/path/" ) );
		assertEquals( "/", Settings.getParentPath( "/element" ) );
		assertEquals( "/path/to", Settings.getParentPath( "/path/to/path/" ) );
		assertEquals( "/path/to", Settings.getParentPath( "/path/to/setting" ) );
	}

	@Test
	public void testGetSettingKey() {
		assertEquals( null, Settings.getSettingKey( "/" ) );
		assertEquals( "setting", Settings.getSettingKey( "/path/to/setting" ) );
	}

	@Test
	public void testGetPaths() {
		providerD.set( "/a", "A" );
		providerD.set( "/b", "B" );
		providerD.set( "/z/a", "A" );
		providerD.set( "/z/b", "B" );
		providerD.set( "/y/b", "B" );
		providerD.set( "/y/a", "A" );

		List<String> paths = Settings.getPaths( settings );

		int index = 0;
		assertEquals( "/a=A", paths.get( index++ ) );
		assertEquals( "/b=B", paths.get( index++ ) );
		assertEquals( "/y/a=A", paths.get( index++ ) );
		assertEquals( "/y/b=B", paths.get( index++ ) );
		assertEquals( "/z/a=A", paths.get( index++ ) );
		assertEquals( "/z/b=B", paths.get( index++ ) );
	}

	@Test
	public void testToStringPaths() {
		providerD.set( "/a", "A" );
		providerD.set( "/b", "B" );
		providerD.set( "/z/a", "A" );
		providerD.set( "/z/b", "B" );
		providerD.set( "/y/a", "A" );
		providerD.set( "/y/b", "B" );
		String expected = "/a=A\n/b=B\n/y/a=A\n/y/b=B\n/z/a=A\n/z/b=B\n";

		assertEquals( expected, settings.toStringPaths() );
	}

	private static boolean isJava8OrLater() {
		Version version = new Version( System.getProperty( "java.version" ) );
		return  version.compareTo( new Version( "1.8" ) ) >= 0;
	}

	@Test
	public void testToStringXml() {
		providerD.set( "/a", "A" );
		providerD.set( "/b", "B" );
		providerD.set( "/z/a", "A" );
		providerD.set( "/z/b", "B" );
		providerD.set( "/y/a", "A" );
		providerD.set( "/y/b", "B" );
		String expected = "<settings>\n" + "  <b>B</b>\n" + "  <a>A</a>\n" + "  <y>\n" + "    <b>B</b>\n" + "    <a>A</a>\n" + "  </y>\n" + "  <z>\n" + "    <b>B</b>\n" + "    <a>A</a>\n" + "  </z>\n" + "</settings>\n";

		if( isJava8OrLater() ) {
			expected = "<settings>\n" + "  <a>A</a>\n" + "  <b>B</b>\n" + "  <y>\n" + "    <a>A</a>\n" + "    <b>B</b>\n" + "  </y>\n" + "  <z>\n" + "    <a>A</a>\n" + "    <b>B</b>\n" + "  </z>\n" + "</settings>\n";
		}

//		if( isJava8OrLater() ) {
//			expected = "<settings>\n" + "  <a>A</a>\n" + "  <b>B</b>\n" + "  <y>\n" + "    <a>A</a>\n" + "    <b>B</b>\n" + "  </y>\n" + "  <z>\n" + "    <a>A</a>\n" + "    <b>B</b>\n" + "  </z>\n" + "</settings>\n";
//		}
		assertEquals( expected, settings.toStringXml() );
	}

	@Test
	public void testPrintAsPaths() {
		providerD.set( "/a", "A" );
		providerD.set( "/b", "B" );
		providerD.set( "/z/a", "A" );
		providerD.set( "/z/b", "B" );
		providerD.set( "/y/a", "A" );
		providerD.set( "/y/b", "B" );
		String expected = "/a=A\n/b=B\n/y/a=A\n/y/b=B\n/z/a=A\n/z/b=B\n";

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream( buffer );
		settings.printAsPaths( stream );

		assertEquals( expected, buffer.toString() );
	}

	@Test
	public void testPrintAsPathsStatic() {
		providerD.set( "/a", "A" );
		providerD.set( "/b", "B" );
		providerD.set( "/z/a", "A" );
		providerD.set( "/z/b", "B" );
		providerD.set( "/y/a", "A" );
		providerD.set( "/y/b", "B" );
		String expected = "/a=A\n/b=B\n/y/a=A\n/y/b=B\n/z/a=A\n/z/b=B\n";

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream( buffer );
		Settings.printAsPaths( settings, stream );

		assertEquals( expected, buffer.toString() );
	}

	@Test
	public void testPrintAsXml() {
		providerD.set( "/a", "A" );
		providerD.set( "/b", "B" );
		providerD.set( "/z/a", "A" );
		providerD.set( "/z/b", "B" );
		String expected = "<settings>\n" + "  <b>B</b>\n" + "  <a>A</a>\n" + "  <z>\n" + "    <b>B</b>\n" + "    <a>A</a>\n" + "  </z>\n" + "</settings>\n";

		if( isJava8OrLater() ) {
			expected = "<settings>\n" + "  <a>A</a>\n" + "  <b>B</b>\n" + "  <z>\n" + "    <a>A</a>\n" + "    <b>B</b>\n" + "  </z>\n" + "</settings>\n";
		}

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream( buffer );
		settings.printAsXml( settings, stream );

		assertEquals( expected, buffer.toString() );
	}

	@Test
	public void testPrintAsXmlStatic() {
		providerD.set( "/a", "A" );
		providerD.set( "/b", "B" );
		providerD.set( "/z/a", "A" );
		providerD.set( "/z/b", "B" );
		String expected = "<settings>\n" + "  <b>B</b>\n" + "  <a>A</a>\n" + "  <z>\n" + "    <b>B</b>\n" + "    <a>A</a>\n" + "  </z>\n" + "</settings>\n";

		if( isJava8OrLater() ) {
			expected = "<settings>\n" + "  <a>A</a>\n" + "  <b>B</b>\n" + "  <z>\n" + "    <a>A</a>\n" + "    <b>B</b>\n" + "  </z>\n" + "</settings>\n";
		}

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream( buffer );
		Settings.printAsXml( settings, stream );

		assertEquals( expected, buffer.toString() );
	}

	@Test
	public void testGetSafeUuid() {
		String id = Settings.getSafeUuid();
		assertNotNull( id );
		assertTrue( id.startsWith( "uuid-" ) );
	}

	@Test
	public void testAddListener() {
		MockSettingListener listener1 = new MockSettingListener();
		MockSettingListener listener2 = new MockSettingListener();
		MockSettingListener listener3 = new MockSettingListener();
		MockSettingListener listener4 = new MockSettingListener();

		settings.getNode( "/watched/node/" ).addSettingListener( listener1 );
		settings.getNode( "/watched/node" ).addSettingListener( listener2 );
		settings.addSettingListener( "/watched/node", listener3 );
		settings.addSettingListener( "/watched", listener4 );

		settings.put( "/watched/node/a", "a" );

		assertEquals( 1, listener1.getEvents().size() );
		assertEquals( 1, listener2.getEvents().size() );
		assertEquals( 1, listener3.getEvents().size() );
		assertEquals( 1, listener4.getEvents().size() );
	}

	protected void showProviderData( Settings settings ) {
		int pCount = settings.getProviderCount();
		for( int pIndex = 0; pIndex < pCount; pIndex++ ) {
			SettingsProvider provider = settings.getProvider( pIndex );
			((MockSettingsProvider)provider).show();
			System.out.println();
		}
	}

	private void assertSettingsValues( Settings expected, Settings actual ) {
		assertEquals( expected.getKeys().size(), actual.getKeys().size() );

		Set<String> expectedKeys = expected.getKeys();
		for( String name : expectedKeys ) {
			assertEquals( expected.get( name, null ), actual.get( name, null ) );
		}
	}

	private boolean areEqualsByValue( Settings expected, Settings actual ) {
		if( expected.getKeys().size() != actual.getKeys().size() ) return false;

		boolean result = true;
		Set<String> expectedKeys = expected.getKeys();
		for( String name : expectedKeys ) {
			result &= expected.get( name, null ).equals( actual.get( name, null ) );
		}
		return result;
	}

	private static final class MockPersistent implements Persistent {

		private int index;

		private String value;

		@Getter
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
			if( !(object instanceof MockPersistent that) ) return false;
			return value == null ? this.index == that.index : this.value.equals( that.value );
		}

	}

}
