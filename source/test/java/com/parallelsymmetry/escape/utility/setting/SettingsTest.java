package com.parallelsymmetry.escape.utility.setting;

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
		assertNull( settings.get( path ) );

		// Setting the value in the default provider should give us a default value.
		providerD.set( path, "D" );
		assertEquals( "D", settings.get( path ) );

		// Setting the value in provider 2 should override the default provider.
		provider2.set( path, "2" );
		assertEquals( "2", settings.get( path ) );

		// Setting the value in provider 3 should not override provider 2.
		provider3.set( path, "3" );
		assertEquals( "2", settings.get( path ) );

		// Setting the value in provider 1 should override provider 2.
		provider1.set( path, "1" );
		assertEquals( "1", settings.get( path ) );
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

	public void testMounts() {
		settings = new Settings();
		settings.addProvider( provider1, "/1" );
		settings.addProvider( provider2, "/2" );
		settings.addProvider( provider3, "/3" );
		settings.setDefaultProvider( providerD, "/D" );

		provider1.set( "/value", "1" );
		provider2.set( "/value", "2" );
		provider3.set( "/value", "3" );
		providerD.set( "/value", "D" );

		assertEquals( "1", settings.get( "/1/value" ) );
		assertEquals( "2", settings.get( "/2/value" ) );
		assertEquals( "3", settings.get( "/3/value" ) );
		assertEquals( "D", settings.get( "/D/value" ) );

		settings.put( "/1/value", "A" );
		settings.put( "/2/value", "B" );
		settings.put( "/3/value", "C" );
		settings.put( "/D/value", "D" );

		// Remember that provider2 is the only one writable.
		assertEquals( "1", settings.get( "/1/value" ) );
		assertEquals( "B", settings.get( "/2/value" ) );
		assertEquals( "3", settings.get( "/3/value" ) );
		assertEquals( "D", settings.get( "/D/value" ) );
	}

	public void testNoProviders() {
		assertNull( new Settings().get( "/test" ) );
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

		assertEquals( "1", settings.get( "/test/path/1" ) );
		assertEquals( "2", settings.get( "/test/path/2" ) );
		assertEquals( "3", settings.get( "/test/path/3" ) );
		assertEquals( "D", settings.get( "/test/path/D" ) );
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

		Settings node = settings.getNode( "/test" );

		assertEquals( "1", node.get( "/path/1" ) );
		assertEquals( "2", node.get( "/path/2" ) );
		assertEquals( "3", node.get( "/path/3" ) );
		assertEquals( "D", node.get( "/path/D" ) );

		node.put( "/path/1", "A" );
		node.put( "/path/2", "B" );
		node.put( "/path/3", "C" );

		// Remember that provider2 is the only one writable.
		assertEquals( "1", node.get( "/path/1" ) );
		assertEquals( "B", node.get( "/path/2" ) );
		assertEquals( "C", node.get( "/path/3" ) );
		assertEquals( "D", node.get( "/path/D" ) );
	}

	public void testRemoveNode() {
		int count = 3;

		String listPath = "/test/path/list";
		for( int index = 0; index < count; index++ ) {
			settings.addListNode( listPath );
		}

		List<Settings> list = settings.getList( listPath );
		assertEquals( count, list.size() );

		int index = 0;
		for( Settings item : list ) {
			assertEquals( listPath + Settings.ITEM_PREFIX + ( index++ ), item.getPath() );
		}

		settings.removeNode( listPath );
		assertFalse( settings.nodeExists( listPath ) );
	}

	public void testAddListNode() {
		String listPath = "/test/path/list";
		Settings next = settings.addListNode( listPath );
		assertEquals( listPath + Settings.ITEM_PREFIX + "0", next.getPath() );
		assertEquals( 1, settings.getInt( listPath + Settings.ITEM_COUNT ) );

		next.put( "/test", "value1" );
		assertEquals( "value1", settings.get( listPath + Settings.ITEM_PREFIX + "0/test" ) );
	}

	public void testGetListNode() {
		String listPath = "/test/path/list";
		Settings next = settings.addListNode( listPath );
		next.put( "/test", "value1" );

		Settings node = settings.getListNode( listPath, 0 );
		assertEquals( listPath + Settings.ITEM_PREFIX + "0", node.getPath() );
		assertEquals( "value1", node.get( "/test" ) );
	}

	public void testGetList() {
		int count = 3;

		String listPath = "/test/path/list";
		for( int index = 0; index < count; index++ ) {
			settings.addListNode( listPath );
		}

		List<Settings> list = settings.getList( listPath );
		assertEquals( count, list.size() );

		int index = 0;
		for( Settings item : list ) {
			assertEquals( listPath + Settings.ITEM_PREFIX + ( index++ ), item.getPath() );
		}
	}

	protected void showProviderData( Settings settings ) {
		int pCount = settings.getProviderCount();
		for( int pIndex = 0; pIndex < pCount; pIndex++ ) {
			SettingProvider provider = settings.getProvider( pIndex );
			( (MockSettingProvider)provider ).show();
			System.out.println();
		}

	}

}
