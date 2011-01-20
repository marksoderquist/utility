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

	public Settings() {
		this( null );
	}

	public Settings( SettingProvider provider ) {
		this.defaultProvider = provider;
		this.providers = new CopyOnWriteArrayList<SettingProvider>();
		this.mounts = new ConcurrentHashMap<SettingProvider, String>();
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

	public String get( String path ) {
		validatePath( path );
		String result = null;
		for( SettingProvider provider : providers ) {
			String mount = mounts.get( provider );

			if( mount == null ) {
				result = provider.get( path );
			} else if( path.startsWith( mount ) ) {
				result = provider.get( path.substring( mount.length() ) );
			}

			if( result != null ) return result;
		}

		if( defaultProvider != null ) {
			if( defaultMount == null ) {
				result = defaultProvider.get( path );
			} else if( path.startsWith( defaultMount ) ) {
				result = defaultProvider.get( path.substring( defaultMount.length() ) );
			}
			return result;
		}

		return null;
	}

	public void put( String path, String value ) {
		validatePath( path );

		boolean written = false;
		for( SettingProvider provider : providers ) {
			if( provider.isWritable() ) {
				String mount = mounts.get( provider );

				provider.put( mount == null ? path : mount + path, value );

				if( written ) break;
			}
		}
	}

	private void validatePath( String path ) {
		if( !path.startsWith( "/" ) ) throw new IllegalArgumentException( "Path should start with '/': " + path );
	}

}
