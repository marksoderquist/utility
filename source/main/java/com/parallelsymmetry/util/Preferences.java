package com.parallelsymmetry.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Preferences extends java.util.prefs.Preferences {

	private static final String INDEX_SEPARATOR = "-";

	private static final Map<String, Preferences> applicationRoots = new ConcurrentHashMap<String, Preferences>();

	private static final Map<String, Map<String, String>> defaults = new ConcurrentHashMap<String, Map<String, String>>();

	private String rootPath;

	private java.util.prefs.Preferences preferences;

	private Map<NodeChangeListener, NodeChangeWrapper> nodeChangeWrappers = new ConcurrentHashMap<NodeChangeListener, NodeChangeWrapper>();

	private Map<PreferenceChangeListener, PreferenceChangeWrapper> preferenceChangeWrappers = new ConcurrentHashMap<PreferenceChangeListener, PreferenceChangeWrapper>();

	private Preferences( String rootPath, java.util.prefs.Preferences preferences ) {
		this.rootPath = rootPath;
		this.preferences = preferences;
	}

	public static Preferences getApplicationRoot( String namespace, String identifier ) {
		String path = "/" + namespace.replace( '.', '/' ) + "/" + identifier;

		Preferences preferences = applicationRoots.get( path );
		if( preferences == null ) {
			preferences = new Preferences( path, java.util.prefs.Preferences.userRoot().node( path ) );
			applicationRoots.put( path, preferences );
		}

		return preferences;
	}

	public void loadDefaults( InputStream input ) throws IOException {
		if( input == null ) throw new NullPointerException();

		Descriptor descriptor = null;
		try {
			String prefix = "/preferences";
			int prefixSize = prefix.length();
			descriptor = new Descriptor( input );
			for( String path : descriptor.getPaths() ) {
				if( !path.startsWith( prefix ) ) continue;
				int slash = path.lastIndexOf( '/' );
				String preferencePath = path.substring( prefixSize, slash );
				String key = path.substring( slash + 1 );
				String value = descriptor.getValue( path );
				preferencePath = rootPath + preferencePath + "/";
				putDefaultValue( preferencePath, key, value );
			}
		} catch( SAXException e ) {
			throw new IOException( e );
		} catch( ParserConfigurationException e ) {
			throw new IOException( e );
		}
	}

	public String realPath() {
		return preferences.absolutePath();
	}

	@Override
	public String absolutePath() {
		String path = preferences.absolutePath();
		if( path.startsWith( rootPath ) ) path = path.substring( rootPath.length() );
		if( "".equals( path ) ) path = "/";
		return path;
	}

	@Override
	public String[] childrenNames() throws BackingStoreException {
		List<String> names = new ArrayList<String>( Arrays.asList( preferences.childrenNames() ) );

		String prefix = getDefaultPath( absolutePath() );
		int prefixLength = prefix.length();
		for( String key : defaults.keySet() ) {
			if( key.length() > prefixLength && key.startsWith( prefix ) ) {
				names.add( key );
			}
		}

		return names.toArray( new String[names.size()] );
	}

	@Override
	public void clear() throws BackingStoreException {
		preferences.clear();
	}

	@Override
	public void exportNode( OutputStream stream ) throws IOException, BackingStoreException {
		preferences.exportNode( stream );
	}

	@Override
	public void exportSubtree( OutputStream stream ) throws IOException, BackingStoreException {
		preferences.exportSubtree( stream );
	}

	@Override
	public void flush() throws BackingStoreException {
		preferences.flush();
	}

	@Override
	public String get( String key, String def ) {
		if( exists( key ) ) return preferences.get( key, def );
		if( defaultExists( key ) ) return getDefaultValue( key );
		return def;
	}

	@Override
	public boolean getBoolean( String key, boolean def ) {
		if( exists( key ) ) return preferences.getBoolean( key, def );
		if( defaultExists( key ) ) return Boolean.parseBoolean( getDefaultValue( key ) );
		return def;
	}

	@Override
	public byte[] getByteArray( String key, byte[] def ) {
		if( exists( key ) ) return preferences.getByteArray( key, def );
		if( defaultExists( key ) ) return getDefaultValue( key ).getBytes();
		return def;
	}

	@Override
	public double getDouble( String key, double def ) {
		if( exists( key ) ) return preferences.getDouble( key, def );
		if( defaultExists( key ) ) return Double.parseDouble( getDefaultValue( key ) );
		return def;
	}

	@Override
	public float getFloat( String key, float def ) {
		if( exists( key ) ) return preferences.getFloat( key, def );
		if( defaultExists( key ) ) return Float.parseFloat( getDefaultValue( key ) );
		return def;
	}

	@Override
	public int getInt( String key, int def ) {
		if( exists( key ) ) return preferences.getInt( key, def );
		if( defaultExists( key ) ) return Integer.parseInt( getDefaultValue( key ) );
		return def;
	}

	@Override
	public long getLong( String key, long def ) {
		if( exists( key ) ) return preferences.getLong( key, def );
		if( defaultExists( key ) ) return Long.parseLong( getDefaultValue( key ) );
		return def;
	}

	@Override
	public boolean isUserNode() {
		return preferences.isUserNode();
	}

	@Override
	public String[] keys() throws BackingStoreException {
		List<String> keys = new ArrayList<String>( Arrays.asList( preferences.keys() ) );

		String path = getDefaultPath( absolutePath() );
		if( defaults.containsKey( path ) ) {
			for( String key : defaults.get( path ).keySet() ) {
				keys.add( key );
			}
		}

		return keys.toArray( new String[keys.size()] );
	}

	@Override
	public String name() {
		return preferences.name();
	}

	@Override
	public Preferences node( String path ) {
		if( path.startsWith( "/" ) ) path = path.substring( 1 );
		return new Preferences( rootPath, preferences.node( path ) );
	}

	public Preferences node( String path, int index ) {
		return node( path + INDEX_SEPARATOR + index );
	}

	@Override
	public boolean nodeExists( String path ) throws BackingStoreException {
		return preferences.nodeExists( path ) || defaults.containsKey( getDefaultPath( getNodePath( path ) ) );
	}

	@Override
	public Preferences parent() {
		return new Preferences( rootPath, preferences.parent() );
	}

	@Override
	public void put( String key, String value ) {
		preferences.put( key, value );
	}

	@Override
	public void putBoolean( String key, boolean value ) {
		preferences.putBoolean( key, value );
	}

	@Override
	public void putByteArray( String key, byte[] value ) {
		preferences.putByteArray( key, value );
	}

	@Override
	public void putDouble( String key, double value ) {
		preferences.putDouble( key, value );
	}

	@Override
	public void putFloat( String key, float value ) {
		preferences.putFloat( key, value );
	}

	@Override
	public void putInt( String key, int value ) {
		preferences.putInt( key, value );
	}

	@Override
	public void putLong( String key, long value ) {
		preferences.putLong( key, value );
	}

	@Override
	public void remove( String key ) {
		preferences.remove( key );
	}

	@Override
	public void removeNode() throws BackingStoreException {
		preferences.removeNode();
	}

	@Override
	public void addNodeChangeListener( NodeChangeListener listener ) {
		if( nodeChangeWrappers.containsKey( listener ) ) return;

		NodeChangeWrapper wrapper = new NodeChangeWrapper( this, listener );
		nodeChangeWrappers.put( listener, wrapper );
		preferences.addNodeChangeListener( wrapper );
	}

	@Override
	public void addPreferenceChangeListener( PreferenceChangeListener listener ) {
		if( preferenceChangeWrappers.containsKey( listener ) ) return;

		PreferenceChangeWrapper wrapper = new PreferenceChangeWrapper( this, listener );
		preferenceChangeWrappers.put( listener, wrapper );
		preferences.addPreferenceChangeListener( wrapper );
	}

	@Override
	public void removeNodeChangeListener( NodeChangeListener listener ) {
		NodeChangeWrapper wrapper = nodeChangeWrappers.get( listener );
		if( wrapper == null ) return;

		nodeChangeWrappers.remove( listener );
		preferences.removeNodeChangeListener( wrapper );
	}

	@Override
	public void removePreferenceChangeListener( PreferenceChangeListener listener ) {
		PreferenceChangeWrapper wrapper = preferenceChangeWrappers.get( listener );
		if( wrapper == null ) return;

		preferenceChangeWrappers.remove( listener );
		preferences.removePreferenceChangeListener( wrapper );
	}

	public void reset() {
		String path = preferences.absolutePath();
		try {
			preferences.removeNode();
		} catch( BackingStoreException exception ) {
			// Intentionally ignore exception.
		}
		preferences = Preferences.userRoot().node( path );
	}

	@Override
	public void sync() throws BackingStoreException {
		preferences.sync();
	}

	@Override
	public String toString() {
		return preferences.toString();
	}

	private boolean exists( String key ) {
		return preferences.get( key, null ) != null;
	}

	private boolean defaultExists( String key ) {
		String path = getDefaultPath( absolutePath() );
		Map<String, String> values = defaults.get( path );
		boolean result = values != null && values.get( key ) != null;
		//Log.write( "Exists:      " + path + key + " = " + result );
		return result;
	}

	/**
	 * Convert an application preferences path into a regular preferences path.
	 * 
	 * @param path
	 * @return
	 */
	private String getNodePath( String path ) {
		if( path.startsWith( "/" ) ) return path;
		if( "".equals( path ) ) return absolutePath();
		return absolutePath() + "/" + path;
	}

	/**
	 * Convert an application preferences path into a regular preferences path.
	 * 
	 * @param path
	 * @return
	 */
	@SuppressWarnings( "unused" )
	private String getRealPath( String path ) {
		if( "/".equals( path ) ) return rootPath;
		return rootPath + getNodePath( path );
	}

	private String getDefaultPath( String path ) {
		if( !path.endsWith( "/" ) ) path += "/";
		return rootPath + path;
	}

	private String getDefaultValue( String key ) {
		String value = null;
		String path = getDefaultPath( absolutePath() );
		//Log.write( "Get default: " + path + key );
		Map<String, String> values = defaults.get( path );
		if( values != null ) value = values.get( key );
		return value;
	}

	private static void putDefaultValue( String path, String name, String value ) {
		if( !path.startsWith( "/" ) ) throw new RuntimeException( "Path must begin with a slash: " + path );
		if( !path.endsWith( "/" ) ) throw new RuntimeException( "Path must end with a slash: " + path );

		//Log.write( "Put default: " + path + name + " = " + value );

		Map<String, String> values = defaults.get( path );
		if( values == null ) {
			values = new ConcurrentHashMap<String, String>();
			defaults.put( path, values );
		}

		values.put( name, value );
	}

	private static class NodeChangeWrapper implements NodeChangeListener {

		Preferences preferences;

		private NodeChangeListener listener;

		public NodeChangeWrapper( Preferences preferences, NodeChangeListener listener ) {
			if( listener == null ) throw new IllegalArgumentException( "Listener cannot be null." );
			this.preferences = preferences;
			this.listener = listener;
		}

		private NodeChangeEvent convertEvent( NodeChangeEvent event ) {
			return new NodeChangeEvent( preferences.parent(), preferences );
		}

		@Override
		public void childAdded( NodeChangeEvent event ) {
			listener.childAdded( convertEvent( event ) );
		}

		@Override
		public void childRemoved( NodeChangeEvent event ) {
			listener.childRemoved( convertEvent( event ) );
		}

		@Override
		public int hashCode() {
			return listener.hashCode();
		}

		@Override
		public boolean equals( Object object ) {
			return listener.equals( object );
		}

	}

	private static class PreferenceChangeWrapper implements PreferenceChangeListener {

		private Preferences preferences;

		private PreferenceChangeListener listener;

		public PreferenceChangeWrapper( Preferences preferences, PreferenceChangeListener listener ) {
			if( listener == null ) throw new IllegalArgumentException( "Listener cannot be null." );
			this.preferences = preferences;
			this.listener = listener;
		}

		private PreferenceChangeEvent convertEvent( PreferenceChangeEvent event ) {
			return new PreferenceChangeEvent( preferences, event.getKey(), event.getNewValue() );
		}

		@Override
		public void preferenceChange( PreferenceChangeEvent event ) {
			listener.preferenceChange( convertEvent( event ) );
		}

		@Override
		public int hashCode() {
			return listener.hashCode();
		}

		@Override
		public boolean equals( Object object ) {
			return listener.equals( object );
		}

	}

}
