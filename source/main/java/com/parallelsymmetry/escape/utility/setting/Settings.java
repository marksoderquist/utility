package com.parallelsymmetry.escape.utility.setting;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Settings {

	private SettingProvider defaultProvider;

	private List<SettingProvider> providers;

	private String defaultMount;

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
		defaultMount = mount;
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

	public Settings node( String path ) {
		return new Settings( root, this.path + path );
	}

	public String get( String path ) {
		return get( path, null );
	}

	public String get( String path, String defaultValue ) {
		String full = this.path + path;
		validatePath( full );
		String result = null;
		for( SettingProvider provider : root.providers ) {
			String mount = root.mounts.get( provider );

			if( mount == null ) {
				result = provider.get( full );
			} else if( full.startsWith( mount ) ) {
				result = provider.get( full.substring( mount.length() ) );
			}

			if( result != null ) return result;
		}

		if( root.defaultProvider != null ) {
			if( root.defaultMount == null ) {
				result = root.defaultProvider.get( full );
			} else if( full.startsWith( root.defaultMount ) ) {
				result = root.defaultProvider.get( full.substring( root.defaultMount.length() ) );
			}
			return result;
		}

		return defaultValue;
	}

	public void put( String path, String value ) {
		String full = this.path + path;
		validatePath( full );

		boolean written = false;
		for( SettingProvider provider : root.providers ) {
			if( provider.isWritable() ) {
				String mount = root.mounts.get( provider );

				provider.put( mount == null ? full : mount + full, value );

				if( written ) break;
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

	public void putInt( String path, int value ) {
		put( path, String.valueOf( value ) );
	}

	private void validatePath( String path ) {
		if( !path.startsWith( "/" ) ) throw new IllegalArgumentException( "Path should start with '/': " + path );
	}

}
