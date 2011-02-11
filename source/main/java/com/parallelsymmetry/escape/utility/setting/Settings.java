package com.parallelsymmetry.escape.utility.setting;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.parallelsymmetry.escape.utility.log.Log;

public class Settings {

	/**
	 * The path name prefix to list items. Do not change the value of this field,
	 * it will break backward compatibility with previously created setting lists.
	 */
	public static final String ITEM_PREFIX = "/item-";

	/**
	 * The path name to list item counts. Do not change the value of this field,
	 * it will break backward compatibility with previously created setting lists.
	 */
	public static final String ITEM_COUNT = ITEM_PREFIX + "count";

	private SettingProvider defaultProvider;

	private List<SettingProvider> providers;

	private Map<SettingProvider, String> mounts;

	private Settings root;

	private String path;

	public Settings() {
		this.providers = new CopyOnWriteArrayList<SettingProvider>();
		this.mounts = new ConcurrentHashMap<SettingProvider, String>();
		this.root = this;
		this.path = "";
	}

	private Settings( Settings root, String path ) {
		this.root = root;
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public int getProviderCount() {
		return providers.size();
	}

	public SettingProvider getDefaultProvider() {
		return defaultProvider;
	}

	public void setDefaultProvider( SettingProvider provider ) {
		setDefaultProvider( provider, null );
	}

	public void setDefaultProvider( SettingProvider provider, String mount ) {
		this.defaultProvider = provider;
		providers.remove( provider );
		if( mount == null ) {
			mounts.remove( provider );
		} else {
			mounts.put( provider, mount );
		}
	}

	public SettingProvider getProvider( int index ) {
		return providers.get( index );
	}

	public String getMount( SettingProvider provider ) {
		return mounts.get( provider );
	}

	public void addProvider( SettingProvider provider ) {
		addProvider( provider, null );
	}

	public void addProvider( SettingProvider provider, String mount ) {
		providers.add( provider );
		if( defaultProvider == provider ) defaultProvider = null;
		if( mount != null ) mounts.put( provider, mount );
	}

	public void addProvider( int index, SettingProvider provider ) {
		addProvider( index, provider, null );
	}

	public void addProvider( int index, SettingProvider provider, String mount ) {
		providers.add( index, provider );
		if( mount != null ) mounts.put( provider, mount );
	}

	public void removeProvider( SettingProvider provider ) {
		providers.remove( provider );
		mounts.remove( provider );
	}

	public void removeProvider( int index ) {
		mounts.remove( providers.remove( index ) );
	}

	public boolean nodeExists( String path ) {
		boolean result = false;
		for( SettingProvider provider : root.providers ) {
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

	public void removeNode( String path ) {
		try {
			for( SettingProvider provider : root.providers ) {
				if( provider instanceof WritableSettingProvider ) {
					String full = getProviderPath( provider, path );
					if( full != null ) ( (WritableSettingProvider)provider ).removeNode( full );
				}
			}
		} catch( SettingsStoreException exception ) {
			Log.write( exception );
		}
	}

	public void flush() {
		flush( "/" );
	}

	public void flush( String path ) {
		try {
			for( SettingProvider provider : root.providers ) {
				if( provider instanceof WritableSettingProvider ) {
					String full = getProviderPath( provider, path );
					if( full != null ) ( (WritableSettingProvider)provider ).flush( full );
				}
			}
		} catch( SettingsStoreException exception ) {
			Log.write( exception );
		}
	}

	public void sync() {
		sync( "/" );
	}

	public void sync( String path ) {
		try {
			for( SettingProvider provider : root.providers ) {
				if( provider instanceof WritableSettingProvider ) {
					String full = getProviderPath( provider, path );
					if( full != null ) ( (WritableSettingProvider)provider ).sync( full );
				}
			}
		} catch( SettingsStoreException exception ) {
			Log.write( exception );
		}

	}

	public String get( String path ) {
		return get( path, null );
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
		for( SettingProvider provider : root.providers ) {
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
		for( SettingProvider provider : root.providers ) {
			if( provider instanceof WritableSettingProvider ) {
				String full = getProviderPath( provider, path );
				if( full != null ) ( (WritableSettingProvider)provider ).put( full, value );
			}
		}
	}

	public boolean getBoolean( String path ) {
		return Boolean.parseBoolean( get( path ) );
	}

	public boolean getBoolean( String path, boolean defaultValue ) {
		String value = get( path );
		if( value == null ) return defaultValue;
		return Boolean.parseBoolean( value );
	}

	public boolean getDefaultBoolean( String path ) {
		return Boolean.parseBoolean( getDefault( path ) );
	}

	public void putBoolean( String path, boolean value ) {
		put( path, value ? "true" : "false" );
	}

	public int getInt( String path ) {
		return Integer.parseInt( get( path ) );
	}

	public int getInt( String path, int defaultValue ) {
		try {
			return Integer.parseInt( get( path ) );
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

	public <T extends Persistent<T>> List<T> getList( Class<T> type, String path ) {
		int count = getInt( path + ITEM_COUNT, 0 );

		List<T> list = new ArrayList<T>( count );
		for( int index = 0; index < count; index++ ) {
			try {
				Constructor<T> constructor = type.getConstructor();
				constructor.setAccessible( true );
				T object = constructor.newInstance();
				Settings node = getNode( getItemPath( path, index ) );
				list.add( type.cast( ( (Persistent<T>)object ).loadSettings( node ) ) );
			} catch( InstantiationException exception ) {
				Log.write( exception );
			} catch( IllegalAccessException exception ) {
				Log.write( exception );
			} catch( SecurityException exception ) {
				Log.write( exception );
			} catch( NoSuchMethodException exception ) {
				Log.write( exception );
			} catch( IllegalArgumentException exception ) {
				Log.write( exception );
			} catch( InvocationTargetException exception ) {
				Log.write( exception );
			}
		}

		return list;
	}

	public <T extends Persistent<?>> void putList( String path, List<T> list ) {

		int oldCount = getInt( path + ITEM_COUNT, 0 );
		for( int index = 0; index < oldCount; index++ ) {
			removeNode( getItemPath( path, index ) );
		}

		int newCount = list == null ? 0 : list.size();
		for( int index = 0; index < newCount; index++ ) {
			list.get( index ).saveSettings( getNode( getItemPath( path, index ) ) );
		}

		putInt( path + ITEM_COUNT, newCount );
	}

	private String getProviderPath( SettingProvider provider, String path ) {
		String full = getAbsolutePath( path );
		String mount = root.mounts.get( provider );

		if( mount != null && !full.startsWith( mount ) ) return null;

		return mount == null ? full : full.substring( mount.length() );
	}

	private String getAbsolutePath( String path ) {
		validatePath( path );
		return this.path + path;
	}

	private void validatePath( String path ) {
		if( !path.startsWith( "/" ) ) throw new IllegalArgumentException( "Path should start with '/': " + path );
		if( path.endsWith( "/" ) ) throw new IllegalArgumentException( "Path should not end with '/': " + path );
	}

	private String getItemPath( String path, int index ) {
		return path + "/item-" + index;
	}

}
