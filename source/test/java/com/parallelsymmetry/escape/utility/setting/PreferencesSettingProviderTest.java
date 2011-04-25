package com.parallelsymmetry.escape.utility.setting;

import java.util.prefs.Preferences;

import junit.framework.TestCase;

public class PreferencesSettingProviderTest extends TestCase {

	private Preferences preferences;

	private PreferencesSettingProvider provider;

	public void setUp() {
		preferences = Preferences.userNodeForPackage( getClass() );
		provider = new PreferencesSettingProvider( preferences );

		preferences.node( "test" ).put( "path1", "value1" );
	}

	public void testGet() {
		assertEquals( "value1", provider.get( "/test/path1" ) );
	}

	public void testPut() {
		// Because preferences are persistent the value needs to be removed.
		preferences.node( "test" ).remove( "path2" );
		assertNull( provider.get( "/test/path2" ) );

		// Put the value.
		provider.put( "/test/path2", "value2" );
		assertEquals( "value2", provider.get( "/test/path2" ) );

		// Remove the value.
		provider.put( "/test/path2", null );
		assertNull( provider.get( "/test/path2" ) );
	}

	public void testNodeExists() throws Exception {
		// Clear the node if it exists.
		provider.removeNode( "/test/node" );
		assertFalse( provider.nodeExists( "/test/node" ) );

		// Get a value the same name as the node.
		provider.get( "/test/node" );
		assertFalse( provider.nodeExists( "/test/node" ) );

		// Get a value in the node.
		provider.get( "/test/node/exists" );
		assertTrue( provider.nodeExists( "/test/node" ) );
	}

	public void testRemoveNode() throws Exception {
		String path = "/test/remove/node";
		provider.put( path + "/value", "true" );
		assertTrue( provider.nodeExists( path ) );
		provider.removeNode( path );
		assertFalse( provider.nodeExists( path ) );
	}

	public void testResetNode() throws Exception {
		String path = "/test/reset/node";
		provider.put( path + "/value", "true" );
		assertTrue( provider.nodeExists( path ) );

		provider.removeNode( path );
		assertFalse( provider.nodeExists( path ) );

		provider.put( path + "/value", "true" );
		assertTrue( provider.nodeExists( path ) );
	}

	public void testFlushAfterRemoveDoesNotRecreateNode() throws Exception {
		String path = "/test/remove/node";
		provider.put( path + "/value", "true" );
		assertTrue( provider.nodeExists( path ) );
		provider.removeNode( path );
		assertFalse( provider.nodeExists( path ) );
		provider.flush( path );
		assertFalse( provider.nodeExists( path ) );
	}

	public void testPreferencesRemove() throws Exception {
		Preferences a = preferences.node( "a" );
		a.put( "key", "a" );
		assertEquals( "a", a.get( "key", null ) );

		a.removeNode();
		try {
			a.put( "key", "a" );
			fail();
		} catch( IllegalStateException exception ) {
			// 
		}
		
		a = preferences.node( "a" );
		a.put( "key", "a" );
		assertEquals( "a", a.get( "key", null ) );
	}

}
