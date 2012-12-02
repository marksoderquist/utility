package com.parallelsymmetry.utility.setting;

import java.util.Set;
import java.util.prefs.Preferences;

import com.parallelsymmetry.utility.setting.PreferencesSettingProvider;

import junit.framework.TestCase;

public class PreferencesSettingProvider2Test extends TestCase {

	private Preferences preferences;

	private PreferencesSettingProvider provider;

	@Override
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

	public void testGetChildNames() throws Exception {
		String parent = "/test/children";
		provider.removeNode( parent );
		assertFalse( provider.nodeExists( parent + "/child1" ) );
		assertFalse( provider.nodeExists( parent + "/child2" ) );

		provider.put( parent + "/child1/value1", "value1" );
		provider.put( parent + "/child2/value2", "value2" );
		assertTrue( provider.nodeExists( parent + "/child1" ) );
		assertTrue( provider.nodeExists( parent + "/child2" ) );

		Set<String> names = provider.getChildNames( parent );

		assertEquals( 2, names.size() );
		assertTrue( names.contains( "child1" ) );
		assertTrue( names.contains( "child2" ) );
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
		Preferences a = preferences.node( "/test/a" );
		a.put( "key", "a" );
		assertEquals( "a", a.get( "key", null ) );

		a.removeNode();
		try {
			a.put( "key", "a" );
			fail();
		} catch( IllegalStateException exception ) {
			// 
		}

		a = preferences.node( "/test/a" );
		a.put( "key", "a" );
		assertEquals( "a", a.get( "key", null ) );
	}

}