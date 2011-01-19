package com.parallelsymmetry.escape.utility.setting;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Settings {

	private SettingProvider defaultProvider;

	private List<SettingProvider> providers;

	public Settings() {
		this( null );
	}

	public Settings( SettingProvider provider ) {
		this.defaultProvider = provider;
		this.providers = new CopyOnWriteArrayList<SettingProvider>();
	}

	public int getProviderCount() {
		return providers.size();
	}

	public SettingProvider getDefaultProvider() {
		return defaultProvider;
	}

	public void setDefaultProvider( SettingProvider provider ) {
		this.defaultProvider = provider;
	}

	public SettingProvider getProvider( int index ) {
		return providers.get( index );
	}

	public void addProvider( SettingProvider provider ) {
		providers.add( provider );
	}

	public void addProvider( int index, SettingProvider provider ) {
		providers.add( index, provider );
	}

	public void removeProvider( SettingProvider provider ) {
		providers.remove( provider );
	}

	public void removeProvider( int index ) {
		providers.remove( index );
	}

	public String get( String path ) {
		validatePath( path );
		String result = null;
		for( SettingProvider provider : providers ) {
			result = provider.get( path );
			if( result != null ) return result;
		}

		if( defaultProvider != null ) return defaultProvider.get( path );

		return null;
	}

	public void put( String path, String value ) {
		validatePath( path );
		for( SettingProvider provider : providers ) {
			if( provider.isWritable() ) {
				provider.put( path, value );
				break;
			}
		}
	}

	private void validatePath( String path ) {
		if( !path.startsWith( "/" ) ) throw new IllegalArgumentException( "Path should start with '/': " + path );
	}
	
}
