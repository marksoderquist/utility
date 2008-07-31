package com.parallelsymmetry.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import junit.framework.TestCase;

/**
 * This test case requires the existence of a specific preferences.xml file
 * accessible as a system resource in the default package.
 * 
 * @author mvsoder
 */
public class PreferencesTest extends TestCase {

	private static final String NAMESPACE = "com.parallelsymmetry.utility";

	private static final String IDENTIFIER = "testing";

	private static final int EVENT_WAIT_TIME = 1000;

	private Preferences preferences;

	public void setUp() {
		try {
			Preferences.loadDefaults( PreferencesTest.class.getResourceAsStream( "/test.preferences.xml" ) );
		} catch( IOException exception ) {
			throw new RuntimeException( exception );
		}
		preferences = Preferences.getApplicationRoot( NAMESPACE, IDENTIFIER );
	}

	public void testRoot() throws Exception {
		assertNotNull( "Root node is null.", preferences.name() );
		assertEquals( "Root node name is incorrect.", IDENTIFIER, preferences.name() );

		assertEquals( "/", preferences.absolutePath() );
		assertEquals( "/" + NAMESPACE.replace( '.', '/' ) + "/" + IDENTIFIER, preferences.realPath() );
	}

	public void testName() throws Exception {
		assertNotNull( "Root node is null.", preferences.name() );
		assertEquals( "Root node name is incorrect.", IDENTIFIER, preferences.name() );
	}

	public void testNode() throws Exception {
		String nodeName = "test";
		Preferences preferences = this.preferences.node( nodeName );
		assertNotNull( "Test node", preferences );
		assertEquals( "Test node name", nodeName, preferences.name() );
		assertEquals( "/" + NAMESPACE.replace( '.', '/' ) + "/" + IDENTIFIER + "/" + nodeName, preferences.realPath() );

		nodeName = "test";
		preferences = this.preferences.node( "/" + nodeName );
		assertNotNull( "Test node", preferences );
		assertEquals( "Test node name", nodeName, preferences.name() );
		assertEquals( "/" + NAMESPACE.replace( '.', '/' ) + "/" + IDENTIFIER + "/" + nodeName, preferences.realPath() );
	}

	public void testAbsolutePath() throws Exception {
		assertEquals( "Node absolute path is incorrect.", "/test", preferences.node( "test" ).absolutePath() );
		assertEquals( "Node absolute path is incorrect.", "/test", preferences.node( "/test" ).absolutePath() );
	}

	public void testGet() throws Exception {
		resetDefaults();
		preferences.reset();
		Preferences preferences = this.preferences.node( "test" );
		String key = "string";

		assertEquals( "String parameter default value is incorrect.", "parameter-default", preferences.get( key, "parameter-default" ) );
		putDefaultValue( "/test", key, "defined-default" );
		assertEquals( "String defined default value is incorrect.", "defined-default", preferences.get( key, "parameter-default" ) );
		preferences.put( key, "specified-value" );
		assertEquals( "String specified value is incorrect.", "specified-value", preferences.get( key, "parameter-default" ) );
	}

	public void testGetInt() throws Exception {
		resetDefaults();
		preferences.reset();
		Preferences preferences = this.preferences.node( "test" );
		String key = "int";

		assertEquals( "Int parameter default value is incorrect", 123, preferences.getInt( key, 123 ) );
		putDefaultValue( "/test", key, "456" );
		assertEquals( "Int defined default value is incorrect", 456, preferences.getInt( key, 123 ) );
		preferences.putInt( key, 789 );
		assertEquals( "Int specified value is incorrect.", 789, preferences.getInt( key, 123 ) );
		putDefaultValue( "/test", key, "text" );
		assertEquals( "Int specified value is incorrect.", 789, preferences.getInt( key, 123 ) );
	}

	public void testGetLong() throws Exception {
		resetDefaults();
		preferences.reset();
		Preferences preferences = this.preferences.node( "test" );
		String key = "long";

		assertEquals( "Long parameter default value is incorrect.", 123, preferences.getLong( key, 123 ) );
		putDefaultValue( "/test", key, "456" );
		assertEquals( "Long defined default value is incorrect.", 456, preferences.getLong( key, 123 ) );
		preferences.putLong( key, 789 );
		assertEquals( "Long specified value is incorrect.", 789, preferences.getLong( key, 123 ) );
		putDefaultValue( "/test", key, "text" );
		assertEquals( "Long specified value is incorrect.", 789, preferences.getLong( key, 123 ) );
	}

	public void testGetFloat() throws Exception {
		resetDefaults();
		preferences.reset();
		Preferences preferences = this.preferences.node( "test" );
		String key = "float";

		assertEquals( "Float parameter default value is incorrect.", 123.0f, preferences.getFloat( key, 123 ) );
		putDefaultValue( "/test", key, "456" );
		assertEquals( "Float defined default value is incorrect.", 456.0f, preferences.getFloat( key, 123 ) );
		preferences.putFloat( key, 789 );
		assertEquals( "Float specified value is incorrect.", 789.0f, preferences.getFloat( key, 123 ) );
		putDefaultValue( "/test", key, "text" );
		assertEquals( "Float specified value is incorrect.", 789.0f, preferences.getFloat( key, 123 ) );
	}

	public void testGetDouble() throws Exception {
		resetDefaults();
		preferences.reset();
		Preferences preferences = this.preferences.node( "test" );
		String key = "double";

		assertEquals( "Double parameter default value is incorrect.", 123.0, preferences.getDouble( key, 123 ) );
		putDefaultValue( "/test", key, "456" );
		assertEquals( "Double defined default value is incorrect.", 456.0, preferences.getDouble( key, 123 ) );
		preferences.putDouble( key, 789 );
		assertEquals( "Double specified value is incorrect.", 789.0, preferences.getDouble( key, 123 ) );
		putDefaultValue( "/test", key, "text" );
		assertEquals( "Double specified value is incorrect.", 789.0, preferences.getDouble( key, 123 ) );
	}

	public void testGetBoolean() throws Exception {
		resetDefaults();
		preferences.reset();
		Preferences preferences = this.preferences.node( "test" );
		String key = "boolean";

		assertEquals( "Boolean parameter default value is incorrect.", false, preferences.getBoolean( key, false ) );
		putDefaultValue( "/test", key, "true" );
		assertEquals( "Boolean defined default value is incorrect.", true, preferences.getBoolean( key, false ) );
		preferences.putBoolean( key, false );
		assertEquals( "Boolean specified value is incorrect.", false, preferences.getBoolean( key, false ) );
		putDefaultValue( "/test", key, "text" );
		assertEquals( "Boolean specified value is incorrect.", false, preferences.getBoolean( key, false ) );
	}

	public void testGetByteArray() throws Exception {
		resetDefaults();
		preferences.reset();
		Preferences preferences = this.preferences.node( "test" );
		String key = "byte-array";

		assertEquals( "Byte array parameter default value is incorrect.", "parameter-default", new String( preferences.getByteArray( key, "parameter-default".getBytes() ) ) );
		putDefaultValue( "/test", key, "defined-default" );
		assertEquals( "Byte array defined default value is incorrect.", "defined-default", new String( preferences.getByteArray( key, "parameter-default".getBytes() ) ) );
		preferences.putByteArray( key, "specified-value".getBytes() );
		assertEquals( "Byte array specified value is incorrect.", "specified-value", new String( preferences.getByteArray( key, "parameter-default".getBytes() ) ) );
	}

	public void testChildrenNames() throws Exception {
		resetDefaults();
		preferences.reset();
		Preferences preferences = this.preferences.node( "test" );
		assertEquals( "Wrong number of child names.", 0, preferences.childrenNames().length );
		putDefaultValue( "/test/child1", "key", "value" );
		assertEquals( "Wrong number of child names.", 1, preferences.childrenNames().length );
		preferences.node( "child2" );
		assertEquals( "Wrong number of child names.", 2, preferences.childrenNames().length );
	}

	public void testKeys() throws Exception {
		resetDefaults();
		preferences.reset();
		Preferences preferences = this.preferences.node( "test" );
		assertEquals( "Wrong number of keys.", 0, preferences.keys().length );
		putDefaultValue( "/test", "key1", "value1" );
		assertEquals( "Wrong number of keys.", 1, preferences.keys().length );
		preferences.put( "key2", "value2" );
		assertEquals( "Wrong number of keys.", 2, preferences.keys().length );
	}

	public void testNodeExists() throws Exception {
		resetDefaults();
		preferences.reset();
		Preferences preferences = this.preferences.node( "test" );

		assertFalse( preferences.nodeExists( "default" ) );
		putDefaultValue( "/test/default", "key", "value" );
		assertTrue( preferences.nodeExists( "default" ) );

		assertFalse( preferences.nodeExists( "specified" ) );
		preferences.node( "specified" );
		assertTrue( preferences.nodeExists( "specified" ) );
	}

	public void testGetNodePath() throws Exception {
		Preferences preferences = this.preferences.node( "test" );

		assertEquals( "Node path is incorrect.", "/", Accessor.callMethod( preferences, "getNodePath", "/" ) );
		assertEquals( "Node path is incorrect.", "/child", Accessor.callMethod( preferences, "getNodePath", "/child" ) );
		assertEquals( "Node path is incorrect.", "/test", Accessor.callMethod( preferences, "getNodePath", "" ) );
		assertEquals( "Node path is incorrect.", "/test/child", Accessor.callMethod( preferences, "getNodePath", "child" ) );
	}

	public void testGetRealPath() throws Exception {
		Preferences preferences = this.preferences.node( "test" );

		String prefix = "/" + NAMESPACE.replace( '.', '/' ) + "/" + IDENTIFIER;
		assertEquals( "Real path is incorrect.", prefix, Accessor.callMethod( preferences, "getRealPath", "/" ) );
		assertEquals( "Real path is incorrect.", prefix + "/child", Accessor.callMethod( preferences, "getRealPath", "/child" ) );
		assertEquals( "Real path is incorrect.", prefix + "/test", Accessor.callMethod( preferences, "getRealPath", "" ) );
		assertEquals( "Real path is incorrect.", prefix + "/test/child", Accessor.callMethod( preferences, "getRealPath", "child" ) );
	}

	public void testPreferenceChangeListener() throws Exception {
		String nodeKey = "listener";
		Preferences preferences = this.preferences.node( nodeKey );

		String key = "attribute";
		preferences.remove( key );

		MockListener listener = new MockListener();
		preferences.addPreferenceChangeListener( listener );

		String value = "some arbitrary value";
		preferences.put( key, value );
		PreferenceChangeEvent event = listener.getPreferenceChangedEvent();
		assertNotNull( event );
		assertEquals( value, event.getNewValue() );
	}

	public void testPathForPreferenceChangeListener() throws Exception {
		String nodeKey = "listener";
		Preferences preferences = this.preferences.node( nodeKey );

		String key = "listener";
		preferences.remove( key );

		MockListener listener = new MockListener();
		preferences.addPreferenceChangeListener( listener );

		preferences.put( key, "some arbitrary value" );
		PreferenceChangeEvent event = listener.getPreferenceChangedEvent();
		assertNotNull( event );
		assertEquals( "/" + nodeKey, event.getNode().absolutePath() );
	}

	private void resetDefaults() throws Exception {
		Map<String, Map<String, String>> defaults = Accessor.getField( Preferences.class, "defaults" );
		defaults.clear();
	}

	private void putDefaultValue( String path, String name, String value ) throws Exception {
		if( !path.startsWith( "/" ) ) throw new RuntimeException( "Path must begin with a slash." );
		Map<String, Map<String, String>> defaults = Accessor.getField( Preferences.class, "defaults" );
		Map<String, String> values = defaults.get( path );
		if( values == null ) {
			values = new ConcurrentHashMap<String, String>();
			defaults.put( path, values );
		}
		values.put( name, value );
	}

	private static class MockListener implements NodeChangeListener, PreferenceChangeListener {

		private NodeChangeEvent childAddedEvent;

		private NodeChangeEvent childRemovedEvent;

		private PreferenceChangeEvent preferenceChangedEvent;

		public synchronized NodeChangeEvent getChildAddedEvent() {
			try {
				while( childAddedEvent == null ) {
					wait( EVENT_WAIT_TIME );
				}
			} catch( InterruptedException exception ) {
				return null;
			}
			return childAddedEvent;
		}

		public synchronized NodeChangeEvent getChildRemovedEvent() {
			try {
				while( childRemovedEvent == null ) {
					wait( EVENT_WAIT_TIME );
				}
			} catch( InterruptedException exception ) {
				return null;
			}
			return childRemovedEvent;
		}

		public synchronized PreferenceChangeEvent getPreferenceChangedEvent() {
			try {
				while( preferenceChangedEvent == null ) {
					wait( EVENT_WAIT_TIME );
				}
			} catch( InterruptedException exception ) {
				return null;
			}
			return preferenceChangedEvent;
		}

		@Override
		public synchronized void childAdded( NodeChangeEvent event ) {
			childAddedEvent = event;
			notifyAll();
		}

		@Override
		public synchronized void childRemoved( NodeChangeEvent event ) {
			childRemovedEvent = event;
			notifyAll();
		}

		@Override
		public synchronized void preferenceChange( PreferenceChangeEvent event ) {
			preferenceChangedEvent = event;
			notifyAll();
		}

	}

}
