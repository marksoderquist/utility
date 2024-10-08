package com.parallelsymmetry.utility.setting;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import com.parallelsymmetry.utility.TextUtil;
import com.parallelsymmetry.utility.log.Log;
import com.parallelsymmetry.utility.ui.Colors;

/**
 * A node in a hierarchical collection of setting data. This class allows
 * applications to store and retrieve user settings using different setting
 * providers. The setting providers are configured in order of precedence and
 * some providers even provide the ability to store settings. Settings are
 * stored in the highest precedence writable provider.
 * <p>
 * Nodes in a settings tree are named in a similar fashion to directories in a
 * hierarchical file system. Every node in a settings tree has a <i>name</i>
 * (which is not necessarily unique) and a unique <i>absolute path</i>.
 * <p>
 * The root node has a name of the empty string (""). Every other node has an
 * arbitrary node name, specified at the time it is created. The only
 * restrictions on this name are that it cannot be the empty string, and it
 * cannot contain the slash character ('/').
 * <p>
 * The root node has an absolute path of <tt>"/"</tt>. Children of the root node
 * have absolute path of <tt>"/" + </tt><i>&lt;node name&gt;</i>. All other
 * nodes have absolute paths of <i>&lt;parent's absolute path&gt;</i>
 * <tt> + "/" + </tt><i>&lt;node name&gt;</i>. Note that all absolute paths
 * begin with the slash character.
 *
 * @author Mark Soderquist
 */
public class Settings {

	/**
	 * Use this path to refer to the node itself.
	 */
	public static final String SELF = ".";

	/**
	 * The settings path separator character.
	 */
	/*
	 * DEVELOPERS: Do not change the value of this field, it will break backward
	 * compatibility with previously created settings.
	 */
	public static final String SEPARATOR = "/";

	/**
	 * The node name prefix for list items.
	 */
	/*
	 * DEVELOPERS: Do not change the value of this field, it will break backward
	 * compatibility with previously created settings.
	 */
	public static final String ITEM_PREFIX = "item-";

	/**
	 * The key name for list item counts.
	 */
	/*
	 * DEVELOPERS: Do not change the value of this field, it will break backward
	 * compatibility with previously created settings.
	 */
	public static final String ITEM_COUNT = ITEM_PREFIX + "count";

	/**
	 * The key name for list item classes.
	 */
	/*
	 * DEVELOPERS: Do not change the value of this field, it will break backward
	 * compatibility with previously created settings.
	 */
	public static final String ITEM_CLASS = ITEM_PREFIX + "class";

	private SettingsProvider defaultProvider;

	private List<SettingsProvider> providers;

	private Map<SettingsProvider, String> mounts;

	private Map<String, Set<SettingListener>> listeners;

	/**
	 * The root settings node.
	 */

	private Settings root;

	/**
	 * The absolute path to this node.
	 */
	private String path;

	public Settings() {
		this( null );
	}

	public Settings( SettingsProvider defaultProvider ) {
		root = this;
		path = "/";

		providers = new CopyOnWriteArrayList<SettingsProvider>();
		mounts = new ConcurrentHashMap<SettingsProvider, String>();
		listeners = new ConcurrentHashMap<String, Set<SettingListener>>();

		if( defaultProvider != null ) setDefaultProvider( defaultProvider );
	}

	private Settings( Settings root, String path ) {
		this.root = root;
		this.path = path;
	}

	public Settings getRoot() {
		return root;
	}

	public String getName() {
		return path.substring( path.lastIndexOf( "/" ) + 1 );
	}

	public String getPath() {
		return path;
	}

	public int getProviderCount() {
		return root.providers.size();
	}

	public SettingsProvider getDefaultProvider() {
		return defaultProvider;
	}

	/**
	 * Set the default provider for the settings root. There is only on default
	 * provider per settings root.
	 *
	 * @param provider
	 */
	public void setDefaultProvider( SettingsProvider provider ) {
		this.defaultProvider = provider;
	}

	public SettingsProvider getProvider( int index ) {
		return providers.get( index );
	}

	public String getMount( SettingsProvider provider ) {
		return mounts.get( provider );
	}

	public void addProvider( SettingsProvider provider ) {
		addProvider( provider, null );
	}

	public void addProvider( SettingsProvider provider, String mount ) {
		providers.add( provider );
		if( defaultProvider == provider ) defaultProvider = null;
		if( mount != null ) mounts.put( provider, mount );
	}

	public void addProvider( int index, SettingsProvider provider ) {
		addProvider( index, provider, null );
	}

	public void addProvider( int index, SettingsProvider provider, String mount ) {
		providers.add( index, provider );
		if( mount != null ) mounts.put( provider, mount );
	}

	public void removeProvider( SettingsProvider provider ) {
		providers.remove( provider );
		mounts.remove( provider );
	}

	public void removeProvider( int index ) {
		mounts.remove( providers.remove( index ) );
	}

	public boolean nodeExists( String path ) {
		boolean result = false;
		for( SettingsProvider provider : root.providers ) {
			String full = getProviderPath( provider, path );
			if( full != null ) result = provider.nodeExists( full );
			if( result == true ) return true;
		}

		if( root.defaultProvider != null ) {
			String full = getProviderPath( root.defaultProvider, path );
			if( full != null ) result = root.defaultProvider.nodeExists( full );
			return result;
		}

		return false;
	}

	public Settings getNode( String path ) {
		return new Settings( root, getAbsolutePath( path ) );
	}

	public Settings getIndexedNode( String path, int index ) {
		return getNode( getItemPath( path, index ) );
	}

	public Set<String> getKeys() {
		Set<String> keys = new HashSet<String>();

		for( SettingsProvider provider : root.providers ) {
			String full = getProviderPath( provider, path );
			if( full != null ) {
				Set<String> providerKeys = provider.getKeys( full );
				if( providerKeys != null ) keys.addAll( providerKeys );
			}
		}

		if( root.defaultProvider != null ) {
			String full = getProviderPath( root.defaultProvider, path );
			if( full != null ) {
				Set<String> providerKeys = root.defaultProvider.getKeys( full );
				if( providerKeys != null ) keys.addAll( providerKeys );
			}
		}

		return keys;
	}

	public int getChildCount() {
		return getChildCount( "." );
	}

	public int getChildCount( String path ) {
		return getChildNames( path ).size();
	}

	public Set<String> getChildNames() {
		return getChildNames( SELF );
	}

	public Set<String> getChildNames( String path ) {
		Set<String> names = new HashSet<String>();

		for( SettingsProvider provider : root.providers ) {
			String full = getProviderPath( provider, path );
			if( full != null ) {
				Set<String> providerNames = provider.getChildNames( full );
				if( providerNames != null ) names.addAll( providerNames );
			}
		}

		if( root.defaultProvider != null ) {
			String full = getProviderPath( root.defaultProvider, path );
			if( full != null ) {
				Set<String> providerNames = root.defaultProvider.getChildNames( full );
				if( providerNames != null ) names.addAll( providerNames );
			}
		}

		return names;
	}

	public Set<Settings> getChildNodes() {
		return getChildNodes( SELF );
	}

	public Set<Settings> getChildNodes( String path ) {
		Settings node = getNode( path );
		Set<String> names = getChildNames( path );
		Set<Settings> nodes = new HashSet<Settings>();
		for( String name : names ) {
			nodes.add( node.getNode( name ) );
		}
		return nodes;
	}

	public List<Settings> getIndexedNodes() {
		// Get the indexed nodes from this node.
		return getIndexedNodes( "." );
	}

	public List<Settings> getIndexedNodes( String path ) {
		int count = 0;
		for( String childName : getChildNames() ) {
			if( childName.startsWith( ITEM_PREFIX ) ) count++;
		}

		List<Settings> settings = new ArrayList<Settings>();
		for( int index = 0; index < count; index++ ) {
			Settings node = getIndexedNode( path, index );
			settings.add( node );
		}
		return settings;
	}

	public void reset() {
		removeNode();
	}

	public void reset( String path ) {
		removeNode( path );
	}

	public void removeNode() {
		removeNode( getPath() );
	}

	public void removeNode( String path ) {
		try {
			for( SettingsProvider provider : root.providers ) {
				if( provider instanceof WritableSettingsProvider ) {
					String full = getProviderPath( provider, path );
					if( full != null ) ( (WritableSettingsProvider)provider ).removeNode( full );
				}
			}
		} catch( SettingsStoreException exception ) {
			Log.write( exception );
		}
	}

	public void flush() {
		flush( getPath() );
	}

	public void flush( String path ) {
		try {
			for( SettingsProvider provider : root.providers ) {
				if( provider instanceof WritableSettingsProvider ) {
					String full = getProviderPath( provider, path );
					if( full != null ) ( (WritableSettingsProvider)provider ).flush( full );
				}
			}
		} catch( SettingsStoreException exception ) {
			Log.write( exception );
		}
	}

	public void sync() {
		sync( getPath() );
	}

	public void sync( String path ) {
		try {
			for( SettingsProvider provider : root.providers ) {
				if( provider instanceof WritableSettingsProvider ) {
					String full = getProviderPath( provider, path );
					if( full != null ) ( (WritableSettingsProvider)provider ).sync( full );
				}
			}
		} catch( SettingsStoreException exception ) {
			Log.write( exception );
		}

	}

	/**
	 * Copy the values from this settings object to the specified settings object.
	 *
	 * @param settings
	 */
	public void copyTo( Settings settings ) {
		for( String key : getKeys() ) {
			settings.put( key, get( key, null ) );
		}
	}

	/**
	 * Deep copy the values from this settings object to the specified settings
	 * object.
	 *
	 * @param settings
	 */
	public void copyDeepTo( Settings settings ) {
		copyTo( settings );

		for( Settings child : getChildNodes() ) {
			child.copyDeepTo( settings.getNode( child.getName() ) );
		}
	}

	/**
	 * Copy the values from this settings object to the specified settings object.
	 *
	 * @param settings
	 */
	public void copyFrom( Settings settings ) {
		for( String key : settings.getKeys() ) {
			put( key, settings.get( key, null ) );
		}
	}

	/**
	 * Deep copy the values from this settings object to the specified settings
	 * object.
	 *
	 * @param settings
	 */
	public void copyDeepFrom( Settings settings ) {
		copyFrom( settings );

		for( Settings child : settings.getChildNodes() ) {
			getNode( child.getName() ).copyDeepFrom( child );
		}
	}

	/**
	 * Get a value. If the value is not defined in the settings return the
	 * specified default value.
	 *
	 * @param path
	 * @param defaultValue
	 * @return
	 */
	public String get( String path, String defaultValue ) {
		String result = null;
		for( SettingsProvider provider : root.providers ) {
			String full = getProviderPath( provider, path );
			if( full != null ) result = provider.get( full );
			if( result != null ) return result;
		}

		if( root.defaultProvider != null ) {
			String full = getProviderPath( root.defaultProvider, path );
			if( full != null ) result = root.defaultProvider.get( full );
			if( result != null ) return result;
		}

		return defaultValue;
	}

	public String getDefault( String path ) {
		String result = null;

		if( root.defaultProvider != null ) {
			String full = getProviderPath( root.defaultProvider, path );
			if( full != null ) result = root.defaultProvider.get( full );
		}

		return result;
	}

	/**
	 * Set a value. To remove a value set it to null.
	 *
	 * @param path
	 * @param value
	 */
	public void put( String path, String value ) {
		String oldValue = get( path, null );
		if( TextUtil.areEqual( oldValue, value ) ) return;

		String absolute = getAbsolutePath( path );
		boolean changed = false;

		for( SettingsProvider provider : root.providers ) {
			if( provider instanceof WritableSettingsProvider ) {
				String full = getProviderPath( provider, path );
				if( full != null ) {
					( (WritableSettingsProvider)provider ).put( full, value );
					changed = true;
				}
			}
		}

		if( changed ) {
			fireSettingChangedEvent( new SettingEvent( absolute, oldValue, value ) );
			Log.write( Log.DETAIL, "Write setting: ", absolute, " = ", value );
		}
	}

	public boolean getBoolean( String path, boolean defaultValue ) {
		String value = get( path, null );
		if( value == null ) return defaultValue;
		return Boolean.parseBoolean( value );
	}

	public boolean getDefaultBoolean( String path ) {
		return Boolean.parseBoolean( getDefault( path ) );
	}

	public void putBoolean( String path, boolean value ) {
		put( path, value ? "true" : "false" );
	}

	public int getInt( String path, int defaultValue ) {
		try {
			return Integer.parseInt( get( path, null ) );
		} catch( Throwable throwable ) {
			return defaultValue;
		}
	}

	public int getDefaultInt( String path ) {
		return Integer.parseInt( getDefault( path ) );
	}

	public void putInt( String path, int value ) {
		put( path, String.valueOf( value ) );
	}

	public long getLong( String path, int defaultValue ) {
		try {
			return Long.parseLong( get( path, null ) );
		} catch( Throwable throwable ) {
			return defaultValue;
		}
	}

	public long getDefaultLong( String path ) {
		return Long.parseLong( getDefault( path ) );
	}

	public void putLong( String path, long value ) {
		put( path, String.valueOf( value ) );
	}

	public float getFloat( String path, float defaultValue ) {
		try {
			return Float.parseFloat( get( path, null ) );
		} catch( Throwable throwable ) {
			return defaultValue;
		}
	}

	public float getDefaultFloat( String path ) {
		return Float.parseFloat( getDefault( path ) );
	}

	public void putFloat( String path, float value ) {
		put( path, String.valueOf( value ) );
	}

	public double getDouble( String path, double defaultValue ) {
		try {
			return Double.parseDouble( get( path, null ) );
		} catch( Throwable throwable ) {
			return defaultValue;
		}
	}

	public double getDefaultDouble( String path ) {
		return Double.parseDouble( getDefault( path ) );
	}

	public void putDouble( String path, double value ) {
		put( path, String.valueOf( value ) );
	}

	public Color getColor( String path, Color value ) {
		String code = get( path, null );
		return code != null ? Colors.decode( code ) : value;
	}

	public Color getDefaultColor( String path ) {
		return Colors.decode( getDefault( path ) );
	}

	public void putColor( String path, Color color ) {
		put( path, Colors.encode( color ) );
	}

	public <T extends Persistent> List<Settings> getNodeList( String path, List<T> defaultList ) {
		String absolute = getAbsolutePath( path );

		int count = getInt( absolute + SEPARATOR + ITEM_COUNT, -1 );
		if( count < 0 && defaultList == null ) return null;

		List<Settings> list = new ArrayList<Settings>();
		if( count < 0 ) {
			count = defaultList.size();
			for( int index = 0; index < count; index++ ) {
				Settings node = getNode( getItemPath( path, index ) );
				defaultList.get( index ).loadSettings( node );
				list.add( node );
			}
		} else {
			for( int index = 0; index < count; index++ ) {
				Settings node = getNode( getItemPath( path, index ) );
				list.add( node );
			}
		}

		return list;
	}

	public <T extends Persistent> void putNodeList( String path, List<T> list ) {
		String absolute = getAbsolutePath( path );

		// Remove the old list.
		int oldCount = getInt( absolute + SEPARATOR + ITEM_COUNT, 0 );
		for( int index = 0; index < oldCount; index++ ) {
			removeNode( getItemPath( path, index ) );
		}

		// Store the new list.
		if( list == null ) {
			reset( path );
		} else {
			int count = list.size();
			for( int index = 0; index < count; index++ ) {
				list.get( index ).saveSettings( getNode( getItemPath( path, index ) ) );
			}
			putInt( absolute + SEPARATOR + ITEM_COUNT, count );
		}
	}

	public <T extends Persistent> Set<Settings> getNodeSet( String path, Set<T> defaultSet ) {
		ArrayList<T> defaultList = defaultSet == null ? null : new ArrayList<T>( defaultSet );
		List<Settings> list = getNodeList( path, defaultList );
		return list == null ? null : new HashSet<Settings>( list );
	}

	public <T extends Persistent> void putNodeSet( String path, Set<T> set ) {
		putNodeList( path, set == null ? null : new ArrayList<T>( set ) );
	}

	public <T extends Persistent> Map<String, Settings> getNodeMap( String path, Map<String, T> defaultMap ) {
		Map<String, Settings> map = new HashMap<String, Settings>();
		Set<String> names = getChildNames( path );

		int count = names.size();
		if( names.size() == 0 && defaultMap == null ) return null;

		if( count == 0 ) {
			for( String name : defaultMap.keySet() ) {
				Settings node = getNode( path + "/" + name );
				defaultMap.get( name ).loadSettings( node );
				map.put( name, node );
			}
		} else {
			for( String name : names ) {
				Settings node = getNode( path + "/" + name );
				map.put( name, node );
			}
		}

		return map;
	}

	public <T extends Persistent> void putNodeMap( String path, Map<String, T> map ) {
		// Remove the old map.
		Set<String> names = getChildNames( path );
		for( String name : names ) {
			removeNode( path + "/" + name );
		}

		// Store the new map.
		if( map == null ) {
			reset( path );
		} else {
			for( String name : map.keySet() ) {
				map.get( name ).saveSettings( getNode( path + "/" + name ) );
			}
			putInt( getAbsolutePath( path ) + SEPARATOR + ITEM_COUNT, map.size() );
		}
	}

	// TODO Clean up deprecated methods.

	@Deprecated
	@SuppressWarnings( "unchecked" )
	public <T extends Persistent> List<T> getList( String path, List<T> defaultList ) {
		int count = getInt( path + SEPARATOR + ITEM_COUNT, -1 );
		String typeName = get( path + SEPARATOR + ITEM_CLASS, null );

		Class<T> type = null;
		try {
			type = typeName == null ? null : (Class<T>)Class.forName( typeName );
		} catch( ClassNotFoundException exception ) {
			Log.write( exception );
		}

		List<T> list = null;
		if( type == null || count < 0 ) {
			list = defaultList;
			if( list != null ) {
				count = list.size();
				for( int index = 0; index < count; index++ ) {
					Settings node = getNode( getItemPath( path, index ) );
					list.get( index ).loadSettings( node );
				}
			}
		} else {
			list = new ArrayList<T>( count );
			for( int index = 0; index < count; index++ ) {
				try {
					Constructor<T> constructor = type.getDeclaredConstructor();
					constructor.setAccessible( true );
					T object = constructor.newInstance();
					Settings node = getNode( getItemPath( path, index ) );
					( (Persistent)object ).loadSettings( node );
					list.add( type.cast( object ) );
				} catch( InstantiationException exception ) {
					Log.write( exception, getAbsolutePath( path ) );
				} catch( IllegalAccessException exception ) {
					Log.write( exception, getAbsolutePath( path ) );
				} catch( SecurityException exception ) {
					Log.write( exception, getAbsolutePath( path ) );
				} catch( NoSuchMethodException exception ) {
					Log.write( exception, getAbsolutePath( path ) );
				} catch( IllegalArgumentException exception ) {
					Log.write( exception, getAbsolutePath( path ) );
				} catch( InvocationTargetException exception ) {
					Log.write( exception, getAbsolutePath( path ) );
				}
			}
		}

		return list;
	}

	@Deprecated
	public <T extends Persistent> void putList( String path, List<T> list ) {
		// Remove the old list.
		int oldCount = getInt( path + SEPARATOR + ITEM_COUNT, 0 );
		for( int index = 0; index < oldCount; index++ ) {
			removeNode( getItemPath( path, index ) );
		}

		// Store the new list.
		if( list == null ) {
			reset( path );
		} else {
			int newCount = list.size();
			if( newCount > 0 ) {
				for( int index = 0; index < newCount; index++ ) {
					list.get( index ).saveSettings( getNode( getItemPath( path, index ) ) );
				}
				put( path + SEPARATOR + ITEM_CLASS, list.iterator().next().getClass().getName() );
			}
			putInt( path + SEPARATOR + ITEM_COUNT, newCount );
		}
	}

	@Deprecated
	public <T extends Persistent> Set<T> getSet( String path, Set<T> defaultSet ) {
		ArrayList<T> defaultList = defaultSet == null ? null : new ArrayList<T>( defaultSet );
		List<T> list = getList( path, defaultList );
		return list == defaultList ? defaultSet : new HashSet<T>( list );
	}

	@Deprecated
	public <T extends Persistent> void putSet( String path, Set<T> set ) {
		putList( path, set == null ? null : new ArrayList<T>( set ) );
	}

	@Deprecated
	@SuppressWarnings( "unchecked" )
	public <T extends Persistent> Map<String, T> getMap( String path, Map<String, T> defaultMap ) {
		String typeName = get( path + SEPARATOR + ITEM_CLASS, null );

		Class<T> type = null;
		try {
			type = typeName == null ? null : (Class<T>)Class.forName( typeName );
		} catch( ClassNotFoundException exception ) {
			Log.write( exception );
		}

		Map<String, T> map = new HashMap<String, T>();
		if( type == null ) {
			map = defaultMap;
			if( map != null ) {
				for( String name : map.keySet() ) {
					Settings node = getNode( path + "/" + name );
					map.get( name ).loadSettings( node );
				}
			}
		} else {
			map = new HashMap<String, T>();
			Set<String> names = getChildNames( path );
			for( String name : names ) {
				try {
					Constructor<T> constructor = type.getDeclaredConstructor();
					constructor.setAccessible( true );
					T object = constructor.newInstance();
					Settings node = getNode( path + "/" + name );
					( (Persistent)object ).loadSettings( node );
					map.put( name, type.cast( object ) );
				} catch( NoSuchMethodException exception ) {
					Log.write( Log.ERROR, "Unable to restore state: ", getAbsolutePath( path ) );
					Log.write( Log.ERROR, exception );
				} catch( Throwable throwable ) {
					Log.write( Log.WARN, "Unable to restore state: ", getAbsolutePath( path ) );
					Log.write( Log.WARN, throwable );
				}
			}
		}

		return map;
	}

	@Deprecated
	public <T extends Persistent> void putMap( String path, Map<String, T> map ) {
		// Remove the old map.
		Set<String> names = getChildNames( path );
		for( String name : names ) {
			removeNode( path + "/" + name );
		}

		// Store the new map.
		if( map == null ) {
			reset( path );
		} else {
			if( map.size() > 0 ) {
				for( String name : map.keySet() ) {
					map.get( name ).saveSettings( getNode( path + "/" + name ) );
				}
				put( path + SEPARATOR + ITEM_CLASS, map.values().iterator().next().getClass().getName() );
			}
		}
	}

	/**
	 * Add a SettingListener for changes to this node. This is the same as calling
	 * settings.addSettingListener( settings.getPath() + "/", listener ).
	 */
	public void addSettingListener( SettingListener listener ) {
		addSettingListener( path, listener );
	}

	/**
	 * Remove a SettingListener for changes to this node. This is the same as
	 * calling settings.removeSettingListener( settings.getPath() + "/", listener
	 * ).
	 */
	public void removeSettingListener( SettingListener listener ) {
		removeSettingListener( path, listener );
	}

	/**
	 * Add a SettingListener for changes to a specific path.
	 *
	 * @param path
	 * @param listener
	 */
	public void addSettingListener( String path, SettingListener listener ) {
		String full = getAbsolutePath( path );
		synchronized( root.listeners ) {
			Set<SettingListener> listeners = root.listeners.get( full );
			if( listeners == null ) {
				listeners = new CopyOnWriteArraySet<SettingListener>();
				root.listeners.put( full, listeners );
			}
			listeners.add( listener );
		}
	}

	/**
	 * Remove a SettingListener for changes to a specific path.
	 *
	 * @param path
	 * @param listener
	 */
	public void removeSettingListener( String path, SettingListener listener ) {
		String full = getAbsolutePath( path );
		synchronized( root.listeners ) {
			Set<SettingListener> listeners = root.listeners.get( full );
			if( listeners != null ) {
				listeners.remove( listener );
				if( listeners.size() == 0 ) root.listeners.remove( full );
			}
		}
	}

	public String toStringPaths() {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream( buffer );
		printAsPaths( this, stream );
		return buffer.toString();
	}

	@Deprecated
	public String toStringXml() {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PrintStream stream = new PrintStream( buffer );
		printAsXml( this, stream );
		return buffer.toString();
	}

	public void printAsPaths( PrintStream stream ) {
		printAsPaths( this, stream );
	}

//	@Deprecated
//	public void printAsXml( PrintStream stream ) {
//		printAsXml( this, stream );
//	}

	/**
	 * Get a settings safe UUID. A settings safe UUID does not start with a number
	 * and therefore is prefixed with "uuid-".
	 */
	public static String getSafeUuid() {
		UUID id = UUID.randomUUID();

		StringBuilder builder = new StringBuilder( "uuid-" );
		builder.append( id.toString() );

		return builder.toString();
	}

	public static List<String> getPaths( Settings settings ) {
		List<String> paths = new LinkedList<String>();

		String path = settings.getPath();
		List<String> keys = new ArrayList<String>( settings.getKeys() );

		for( String key : keys ) {
			StringBuilder builder = new StringBuilder();
			if( !"/".equals( path ) ) builder.append( path );
			builder.append( "/" );
			builder.append( key );
			builder.append( "=" );
			builder.append( settings.get( key, null ) );
			paths.add( builder.toString() );
		}

		Set<Settings> children = settings.getChildNodes();
		for( Settings child : children ) {
			paths.addAll( getPaths( child ) );
		}

		Collections.sort( paths );

		return paths;
	}

	public static void printAsPaths( Settings settings, PrintStream stream ) {
		for( String path : getPaths( settings ) ) {
			stream.print( path );
			stream.print( "\n" );
		}
	}

	@Deprecated
	public static void printAsXml( Settings settings, PrintStream stream ) {
		printAsXml( settings, stream, 0 );
	}

	@Deprecated
	private static void printAsXml( Settings settings, PrintStream stream, int level ) {
		String name = settings.getName();
		List<String> keys = new ArrayList<>( settings.getKeys() );
		Set<Settings> children = settings.getChildNodes();
		String indent = TextUtil.pad( 2 * level );
		String valueIndent = TextUtil.pad( 2 * ( level + 1 ) );
		int valueCount = keys.size() + children.size();

		Collections.sort( keys );

		stream.append( indent );
		stream.append( "<" );
		stream.append( "".equals( name ) ? "settings" : name );
		if( valueCount == 0 ) {
			stream.append( "/>\n" );
		} else {
			stream.append( ">\n" );

			for( String key : keys ) {
				stream.append( valueIndent );
				stream.append( "<" );
				stream.append( key );
				stream.append( ">" );
				stream.append( settings.get( key, null ) );
				stream.append( "</" );
				stream.append( key );
				stream.append( ">\n" );
			}

			for( Settings child : children ) {
				printAsXml( child, stream, level + 1 );
			}

			stream.append( indent );
			stream.append( "</" );
			stream.append( "".equals( name ) ? "settings" : name );
			stream.append( ">\n" );
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append( getClass().getName() );
		builder.append( ": " );
		builder.append( getPath() );

		return builder.toString();
	}

	@Override
	public int hashCode() {
		return path.hashCode();
	}

	@Override
	public boolean equals( Object object ) {
		if( !( object instanceof Settings ) ) return false;
		Settings that = (Settings)object;
		return this.path.equals( that.path );
	}

	/**
	 * Get the parent path from a path. Path may, but is not required to, include
	 * the trailing slash. Examples:
	 * <p>
	 * <table>
	 * <tr>
	 * <th>Path</th>
	 * <th>Parent Path</th>
	 * </tr>
	 * <tr>
	 * <td>/path/to/setting</td>
	 * <td>/path/to</td>
	 * </tr>
	 * <tr>
	 * <td>/path/to/path/</td>
	 * <td>/path/to</td>
	 * </tr>
	 * <tr>
	 * <td>/element</td>
	 * <td>/</td>
	 * </tr>
	 * <tr>
	 * <td>/path/</td>
	 * <td>/</td>
	 * </tr>
	 * <tr>
	 * <td>/</td>
	 * <td>null</td>
	 * </tr>
	 * </table>
	 *
	 * @param path
	 * @return
	 */
	static String getParentPath( String path ) {
		if( path == null ) return null;
		path = path.trim();
		if( "/".equals( path ) ) return null;
		if( path.endsWith( "/" ) ) path = path.substring( 0, path.length() - 1 );
		path = path.substring( 0, path.lastIndexOf( '/' ) );
		return "".equals( path ) ? "/" : path;
	}

	/**
	 * Get the setting key from a path. If the path ends with a slash, the key is
	 * null.
	 */
	static String getSettingKey( String path ) {
		if( path == null ) return null;
		path = path.trim();
		if( path.endsWith( "/" ) ) return null;
		return path.substring( path.lastIndexOf( '/' ) + 1 );
	}

	private void fireSettingChangedEvent( SettingEvent event ) {
		String eventPath = event.getFullPath();

		while( eventPath != null ) {
			Set<SettingListener> listeners = root.listeners.get( eventPath );
			if( listeners != null ) {
				for( SettingListener listener : listeners ) {
					listener.settingChanged( event );
				}
			}

			// Dispatch the event to any listeners matching a parent node path.
			eventPath = getParentPath( eventPath );
		}
	}

	private String getProviderPath( SettingsProvider provider, String path ) {
		String full = getAbsolutePath( path );

		String mount = root.mounts.get( provider );

		if( mount != null && !full.startsWith( mount ) ) return null;

		return mount == null ? full : full.substring( mount.length() );
	}

	private String getAbsolutePath( String path ) {
		// If the path is empty, return this node's path.
		if( TextUtil.isEmpty( path ) ) return this.path;

		// Trim trailing separator.
		if( path.endsWith( SEPARATOR ) ) path = path.substring( 0, path.length() - 1 );

		// Handle references to self.
		if( path.startsWith( "." ) ) return this.path + path.substring( 1 );

		// If the path is already absolute, return the specified path.
		if( path.startsWith( SEPARATOR ) ) return path;

		// If this node's path is the root.
		if( SEPARATOR.equals( this.path ) ) return this.path + path;

		return this.path + SEPARATOR + path;
	}

	private String getItemPath( String path, int index ) {
		path = getAbsolutePath( path );
		if( path.endsWith( SEPARATOR ) ) path = path.substring( 0, path.length() - 1 );
		return path + SEPARATOR + ITEM_PREFIX + index;
	}

}
